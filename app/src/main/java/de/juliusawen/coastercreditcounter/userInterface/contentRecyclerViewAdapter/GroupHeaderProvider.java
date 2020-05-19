package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.IGroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.SpecialGroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategory;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasModel;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatus;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

class GroupHeaderProvider
{
    private final Map<UUID, IGroupHeader> groupHeadersByGroupElementUuid = new HashMap<>();
    private final List<SpecialGroupHeader> specialGroupHeaders = new ArrayList<>();

    LinkedList<IElement> groupElements(ArrayList<IElement> elements, GroupType groupType)
    {
        if(elements.isEmpty())
        {
            Log.v("no elements to group - returning ungrouped elements");
            return new LinkedList<>(elements);
        }
        else if(groupType == GroupType.NONE)
        {
            Log.v("GroupType is <NONE> - returning ungrouped elements");
            return new LinkedList<>(elements);
        }
        else if(groupType == GroupType.YEAR)
        {
            return this.groupByYear(elements);
        }

        List<IGroupHeader> groupedElements = this.groupHeadersByGroupElementUuid.isEmpty()
                ? this.createGroupHeaders(elements, groupType)
                : this.updateGroupHeaders(elements, groupType);

        LinkedList<IElement> emptyHeaders = new LinkedList<>();
        for(IElement header : groupedElements)
        {
            if(!header.hasChildren())
            {
                emptyHeaders.add(header);
                Log.e(String.format("%s is empty - marked for removal", header));
            }
        }
        groupedElements.removeAll(emptyHeaders);

        groupedElements = this.sortGroupHeadersBasedOnGroupElementsOrder(groupedElements, groupType);

        return ConvertTool.convertElementsToType(groupedElements, IElement.class);
    }

    private List<IGroupHeader> createGroupHeaders(List<IElement> elements, GroupType groupType)
    {
        Log.d(String.format(Locale.getDefault(), "initalizing GroupHeaders for [%d] Elements...", elements.size()));

        List<IGroupHeader> groupedElements = new ArrayList<>();
        for(IElement element : elements)
        {
            IElement groupElement = this.getGroupElement(element, groupType);
            UUID groupElementUuid = groupElement.getUuid();

            IGroupHeader groupHeader = this.groupHeadersByGroupElementUuid.get(groupElementUuid);
            if(groupHeader != null)
            {
                groupHeader.addChild(element);

                Log.v(String.format("added %s to %s", element, groupHeader));

                if(!groupedElements.contains(groupHeader))
                {
                    groupedElements.add(groupHeader);
                    Log.v(String.format("added %s to GroupedElements", groupHeader));
                }
            }
            else
            {
                groupHeader = GroupHeader.create(groupElement);
                this.groupHeadersByGroupElementUuid.put(groupElementUuid, groupHeader);
                groupedElements.add(groupHeader);
                groupHeader.addChild(element);

                Log.v(String.format("created new %s and added %s", groupHeader, element));
            }
        }
        Log.d(String.format(Locale.getDefault(), "created [%d] GroupHeaders", groupedElements.size()));

        return groupedElements;
    }

    private List<IGroupHeader> updateGroupHeaders(List<IElement> elements, GroupType groupType)
    {
        List<IGroupHeader> groupedElements = new ArrayList<>();

        for(IGroupHeader groupHeader : this.groupHeadersByGroupElementUuid.values())
        {
            groupHeader.getChildren().clear();
        }

        for(IElement element : elements)
        {
            IElement groupElement = this.getGroupElement(element, groupType);
            UUID groupElementUuid = groupElement.getUuid();

            IGroupHeader groupHeader = this.groupHeadersByGroupElementUuid.get(groupElementUuid);
            if(groupHeader != null)
            {
                if(!groupHeader.getName().equals(groupElement.getName()))
                {
                    Log.v(String.format("changing name for %s to [%s]...", groupHeader, groupElement.getName()));

                    groupHeader.setName(groupElement.getName());
                }

                if(!groupedElements.contains(groupHeader))
                {
                    groupedElements.add(groupHeader);
                    Log.v(String.format("added %s to GroupedAttractions", groupHeader));
                }

                groupHeader.addChild(element);
            }
            else
            {
                groupHeader = GroupHeader.create(groupElement);
                this.groupHeadersByGroupElementUuid.put(groupElementUuid, groupHeader);
                groupedElements.add(groupHeader);

                groupHeader.addChild(element);
                Log.v(String.format("created new %s and added %s", groupHeader, element));
            }
        }

        return groupedElements;
    }

    private IElement getGroupElement(IElement element, GroupType groupType)
    {
        IElement groupElement = null;
        switch(groupType)
        {
            case PARK:
                if(element.isAttraction())
                {
                    groupElement = element.getParent();
                }
                else
                {
                    Log.e(String.format("%s is no Attraction", element));
                }

                break;

            case CREDIT_TYPE:
                if(element.hasCreditType())
                {
                    groupElement = ((IHasCreditType) element).getCreditType();

                    if(groupElement == null)
                    {
                        groupElement = CreditType.getDefault();
                    }
                }
                else
                {
                    Log.e(String.format("%s has no CreditType", element));
                }
                break;

            case CATEGORY:
                if(element.hasCategory())
                {
                    groupElement = ((IHasCategory) element).getCategory();

                    if(groupElement == null)
                    {
                        groupElement = Category.getDefault();
                    }
                }
                else
                {
                    Log.e(String.format("%s has no Category", element));
                }
                break;

            case MANUFACTURER:
                if(element.hasManufacturer())
                {
                    groupElement = ((IHasManufacturer) element).getManufacturer();

                    if(groupElement == null)
                    {
                        groupElement = Manufacturer.getDefault();
                    }
                }
                else
                {
                    Log.e(String.format("%s has no Manufacturer", element));
                }

                break;

            case MODEL:
                if(element.hasModel())
                {
                    groupElement = ((IHasModel) element).getModel();

                    if(groupElement == null)
                    {
                        groupElement = Model.getDefault();
                    }
                }
                else
                {
                    Log.e(String.format("%s has no Model", element));
                }
                break;

            case STATUS:
                if(element.hasStatus())
                {
                    groupElement = ((IHasStatus) element).getStatus();

                    if(groupElement == null)
                    {
                        groupElement = Status.getDefault();
                    }
                }
                else
                {
                    Log.e(String.format("%s has no Status", element));
                }

                break;
        }

        return groupElement;
    }

    private List<IGroupHeader> sortGroupHeadersBasedOnGroupElementsOrder(List<IGroupHeader> groupHeaders, GroupType groupType)
    {
        if(groupHeaders.size() > 1)
        {
            List<IGroupHeader> sortedGroupHeaders = new ArrayList<>();
            List<IElement> groupElements = new ArrayList<>();

            switch(groupType)
            {
                case YEAR:
                    return groupHeaders;

                case PARK:
                    groupElements = App.content.getContentOfType(Park.class);
                    break;

                case CREDIT_TYPE:
                    groupElements = App.content.getContentOfType(CreditType.class);
                    break;

                case CATEGORY:
                    groupElements = App.content.getContentOfType(Category.class);
                    break;

                case MANUFACTURER:
                    groupElements = App.content.getContentOfType(Manufacturer.class);
                    break;

                case MODEL:
                    groupElements = App.content.getContentOfType(Model.class);
                    break;

                case STATUS:
                    groupElements = App.content.getContentOfType(Status.class);
                    break;
            }

            Log.v( String.format(Locale.getDefault(), "sorting [%d] GroupHeaders based on [%d] GroupElements", groupHeaders.size(), groupElements.size()));

            for(IElement groupElement : groupElements)
            {
                for(IGroupHeader groupHeader : groupHeaders)
                {
                    if(((GroupHeader)groupHeader).getGroupElement().equals(groupElement) && !sortedGroupHeaders.contains(groupHeader))
                    {
                        sortedGroupHeaders.add(groupHeader);
                        break;
                    }
                }
            }

            return sortedGroupHeaders;
        }
        else
        {
            Log.v("not sorted - list contains less than two Elements");
            return groupHeaders;
        }
    }

    private LinkedList<IElement> groupByYear(List<IElement> elements)
    {
        LinkedList<Visit> visits = ConvertTool.convertElementsToType(elements, Visit.class);

        if(visits.isEmpty())
        {
            Log.v("no Elements to group");
            return new LinkedList<IElement>(visits);
        }

        Log.d(String.format(Locale.getDefault(), "adding SpecialGroupHeaders to [%d] Visits...", visits.size()));

        for(SpecialGroupHeader specialGroupHeader : this.specialGroupHeaders)
        {
            specialGroupHeader.getChildren().clear();
        }

        visits = this.sortVisitsByDateAccordingToSortOrder(visits);

        LinkedList<IElement> groupedVisits = new LinkedList<>();

        for(Visit visit : visits)
        {
            String year = StringTool.fetchSimpleYear(visit.getCalendar());

            IElement existingYearHeader = null;
            for(IElement yearHeader : groupedVisits)
            {
                if(yearHeader.getName().equals(year))
                {
                    existingYearHeader = yearHeader;
                }
            }

            if(existingYearHeader != null)
            {
                existingYearHeader.addChild(visit);
            }
            else
            {
                SpecialGroupHeader specialGroupHeader = null;

                for(SpecialGroupHeader header : this.specialGroupHeaders)
                {
                    if(header.getName().equals(year))
                    {
                        specialGroupHeader = header;
                        break;
                    }
                }

                if(specialGroupHeader == null)
                {
                    specialGroupHeader = SpecialGroupHeader.create(year);
                    this.specialGroupHeaders.add(specialGroupHeader);
                    Log.v(String.format("created new %s", specialGroupHeader));
                }

                specialGroupHeader.addChild(visit);
                groupedVisits.add(specialGroupHeader);
            }
        }

        Log.v(String.format(Locale.getDefault(), "[%d] SpecialGroupHeaders added", groupedVisits.size()));
        return groupedVisits;
    }

    private LinkedList<Visit> sortVisitsByDateAccordingToSortOrder(List<Visit> visits)
    {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        HashMap<String, Visit> visitsByDateString = new HashMap<>();

        for(Visit visit : visits)
        {
            String dateString = simpleDateFormat.format(visit.getCalendar().getTime());
            visitsByDateString.put(dateString, visit);
            Log.v(String.format("parsed date [%s] from name %s", dateString, visit));
        }

        ArrayList<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        LinkedList<Visit> sortedVisits = new LinkedList<>();

        if(Visit.getSortOrder() == SortOrder.ASCENDING)
        {
            for(String dateString : dateStrings)
            {
                sortedVisits.add(visitsByDateString.get(dateString));
            }
        }
        else
        {
            for(String dateString : dateStrings)
            {
                sortedVisits.add(0, visitsByDateString.get(dateString));
            }
        }

        Log.i(String.format(Locale.getDefault(), "sorted [%d] visits [%s]", visits.size(), Visit.getSortOrder()));
        return sortedVisits;
    }

    SpecialGroupHeader getSpecialGroupHeaderForLatestYear(List<? extends IElement> yearHeaders)
    {
        SpecialGroupHeader latestSpecialGroupHeader = null;

        if(yearHeaders.size() > 0)
        {
            for(IElement yearHeader : yearHeaders)
            {
                if(latestSpecialGroupHeader == null)
                {
                    latestSpecialGroupHeader = (SpecialGroupHeader) yearHeader;
                }
                else if((Integer.parseInt(yearHeader.getName()) > (Integer.parseInt(latestSpecialGroupHeader.getName()))))
                {
                    latestSpecialGroupHeader = (SpecialGroupHeader) yearHeader;
                }
            }

            Log.v(String.format(Locale.getDefault(), "%s found as latest SpecialGroupHeader in a list of [%d]", latestSpecialGroupHeader, yearHeaders.size()));
        }

        return latestSpecialGroupHeader;
    }
}