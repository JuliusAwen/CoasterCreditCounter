package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.util.Log;

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

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Blueprint;
import de.juliusawen.coastercreditcounter.dataModel.elements.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Category;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Status;
import de.juliusawen.coastercreditcounter.dataModel.temporaryElements.GroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.temporaryElements.IGroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.temporaryElements.SpecialGroupHeader;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;

public class GroupHeaderProvider
{
    private final Map<UUID, IGroupHeader> groupHeadersByGroupElementUuid = new HashMap<>();
    private GroupType formerGroupType = GroupType.NONE;
    private List<IElement> formerElements;
    private List<IGroupHeader> formerGroupedAttractions;


    private final List<SpecialGroupHeader> specialGroupHeaders = new ArrayList<>();

    public List<IElement> groupElements(List<IElement> elements, GroupType groupType)
    {
        List<IElement> blueprints = new LinkedList<>();

        if(groupType == GroupType.NONE)
        {
            return elements;
        }
        else if(groupType == GroupType.YEAR)
        {
            return this.groupByYear(elements);
        }
        else if(groupType == GroupType.LOCATION || groupType == GroupType.STATUS)
        {
            // bluprints have neither locations nor status - they will be filtered out and added again at the end under a SpecialGroupHeader
            for(IElement element : elements)
            {
                if(Blueprint.class.isAssignableFrom(element.getClass()))
                {
                    blueprints.add(element);
                }
            }
            elements.removeAll(blueprints);
            Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.filterOutBlueprints:: filtered out [%d] blueprint(s)", blueprints.size()));
        }

        List<IGroupHeader> groupedAttractions = new ArrayList<>();
        if(this.groupHeadersByGroupElementUuid.isEmpty() || this.formerGroupType != groupType)
        {
            groupHeadersByGroupElementUuid.clear();

            if(!elements.isEmpty())
            {
                Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: initalizing GroupHeaders for [%d] attractions...", elements.size()));

                List<IAttraction> attractions = ConvertTool.convertElementsToType(elements, IAttraction.class);

                for(IAttraction attraction : attractions)
                {
                    IElement groupElement = null;
                    switch(groupType)
                    {
                        case LOCATION:
                            groupElement = attraction.getParent();
                            break;

                        case CREDIT_TYPE:
                            groupElement = attraction.getCreditType();
                            break;

                        case CATEGORY:
                            groupElement = attraction.getCategory();
                            break;

                        case MANUFACTURER:
                            groupElement = attraction.getManufacturer();
                            break;

                        case STATUS:
                            groupElement = attraction.getStatus();
                            break;
                    }
                    UUID groupElementUuid = groupElement.getUuid();

                    IGroupHeader groupHeader = this.groupHeadersByGroupElementUuid.get(groupElementUuid);
                    if(groupHeader != null)
                    {
                        groupHeader.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: added %s to %s", attraction, groupHeader));

                        if(!groupedAttractions.contains(groupHeader))
                        {
                            groupedAttractions.add(groupHeader);
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: added %s to GroupedAttractions", groupHeader));
                        }
                    }
                    else
                    {
                        groupHeader = GroupHeader.create(groupElement);
                        this.groupHeadersByGroupElementUuid.put(groupElementUuid, groupHeader);
                        groupedAttractions.add(groupHeader);
                        groupHeader.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: created new %s and added %s", groupHeader, attraction));
                    }
                }
                Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: created [%d] GroupHeaders", groupedAttractions.size()));
            }
            else
            {
                Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupElements:: no attractions to group - returning");
            }
        }
        else
        {
            if(this.elementsHaveChanged(elements))
            {
                for(IGroupHeader groupHeader : this.groupHeadersByGroupElementUuid.values())
                {
                    groupHeader.getChildren().clear();
                }

                List<IAttraction> attractions = ConvertTool.convertElementsToType(elements, IAttraction.class);
                for(IAttraction attraction : attractions)
                {
                    IElement groupElement = null;
                    switch(groupType)
                    {
                        case LOCATION:
                            groupElement = attraction.getParent();
                            break;

                        case CREDIT_TYPE:
                            groupElement = attraction.getCreditType();
                            break;

                        case CATEGORY:
                            groupElement = attraction.getCategory();
                            break;

                        case MANUFACTURER:
                            groupElement = attraction.getManufacturer();
                            break;

                        case STATUS:
                            groupElement = attraction.getStatus();
                            break;
                    }
                    UUID groupElementUuid = groupElement.getUuid();

                    IGroupHeader groupHeader = this.groupHeadersByGroupElementUuid.get(groupElementUuid);
                    if(groupHeader != null)
                    {
                        if(!groupHeader.getName().equals(groupElement.getName()))
                        {
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: changing name for %s to [%s]...", groupHeader, groupElement.getName()));

                            groupHeader.setName(groupElement.getName());
                        }

                        if(!groupedAttractions.contains(groupHeader))
                        {
                            groupedAttractions.add(groupHeader);
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: added %s to GroupedAttractions", groupHeader));
                        }

                        groupHeader.addChild(attraction);
                    }
                    else
                    {
                        groupHeader = GroupHeader.create(groupElement);
                        this.groupHeadersByGroupElementUuid.put(groupElementUuid, groupHeader);
                        groupedAttractions.add(groupHeader);

                        groupHeader.addChild(attraction);
                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: created new %s and added %s", groupHeader, attraction));
                    }
                }
            }
            else
            {
                Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupElements:: attractions have not changed - returning former GroupedAttractions");
                return ConvertTool.convertElementsToType(this.formerGroupedAttractions, IElement.class);
            }
        }

        List<IElement> emptyHeaders = new ArrayList<>();
        for(IElement header : groupedAttractions)
        {
            if(!header.hasChildren())
            {
                emptyHeaders.add(header);
                Log.e(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElements:: %s is empty - marked for removal", header));
            }
        }
        groupedAttractions.removeAll(emptyHeaders);

        groupedAttractions = this.sortGroupHeadersBasedOnGroupElementsOrder(groupedAttractions, groupType);

        if(!blueprints.isEmpty())
        {
            Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.addBlueprintsToGroupedAttractions:: adding [%d] blueprint(s) with special header", blueprints.size()));

            SpecialGroupHeader blueprintGroupHeader = SpecialGroupHeader.create(App.getContext().getString(R.string.text_blueprint_special_group_header_name));
            blueprintGroupHeader.addChildren(new ArrayList<>(blueprints));
            groupedAttractions.add(blueprintGroupHeader);
        }

        this.formerElements = elements;
        this.formerGroupType = groupType;
        this.formerGroupedAttractions = groupedAttractions;
        return ConvertTool.convertElementsToType(groupedAttractions, IElement.class);
    }

    private boolean elementsHaveChanged(List<IElement> attractions)
    {
        if(attractions.size() != this.formerElements.size())
        {
            return true;
        }
        else
        {
            for(IElement element : attractions)
            {
                if(!this.formerElements.contains(element) || !element.equals(this.formerElements.get(attractions.indexOf(element))))
                {
                    return true;
                }
            }
        }

        return false;
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

                case LOCATION:
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

                case STATUS:
                    groupElements = App.content.getContentOfType(Status.class);
                    break;
            }

            Log.v(Constants.LOG_TAG,  String.format("GroupHeaderProvider.sortGroupHeadersBasedOnGroupElementsOrder:: sorting [%d] GroupHeaders based on [%d] GroupElements", groupHeaders.size(), groupElements.size()));

            for(IElement groupElement : groupElements)
            {
                for(IGroupHeader groupHeader : groupHeaders)
                {
                    if(groupHeader.getGroupElement().equals(groupElement) && !sortedGroupHeaders.contains(groupHeader))
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
            Log.v(Constants.LOG_TAG,"GroupHeaderProvider.sortGroupHeadersBasedOnGroupElementsOrder:: not sorted - list contains less than two elements");
            return groupHeaders;
        }
    }

    private List<IElement> groupByYear(List<IElement> elements)
    {
        List<Visit> visits = ConvertTool.convertElementsToType(elements, Visit.class);

        if(visits.isEmpty())
        {
            Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupByYear:: no elements found");
            return new ArrayList<IElement>(visits);
        }

        Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByYear:: adding special YearHeaders to [%d] elements...", visits.size()));

        for(SpecialGroupHeader specialGroupHeader : this.specialGroupHeaders)
        {
            specialGroupHeader.getChildren().clear();
        }

        visits = this.sortVisitsByDateAccordingToSortOrder(visits);

        List<IElement> groupedVisits = new ArrayList<>();

        for(Visit visit : visits)
        {
            String year = String.valueOf(StringTool.fetchSimpleYear(visit.getCalendar()));

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
                    Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByYear:: created new %s", specialGroupHeader));
                }

                specialGroupHeader.addChild(visit);
                groupedVisits.add(specialGroupHeader);
            }
        }

        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByYear:: [%d] YearHeaders added", groupedVisits.size()));
        return groupedVisits;
    }

    private List<Visit> sortVisitsByDateAccordingToSortOrder(List<Visit> visits)
    {
        if(Visit.getSortOrder() == SortOrder.ASCENDING)
        {
            visits = this.sortAscendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.sortVisitsByDateAccordingToSortOrder:: sorted [%d] visits <ascending>", visits.size()));
        }
        else
        {
            visits = this.sortDescendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.sortVisitsByDateAccordingToSortOrder:: sorted [%d] visits <descending>", visits.size()));
        }

        return visits;
    }

    private List<Visit> sortDescendingByDate(List<Visit> visits)
    {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        HashMap<String, Visit> visitsByDateString = new HashMap<>();
        for(Visit visit : visits)
        {
            String dateString = simpleDateFormat.format(visit.getCalendar().getTime());
            visitsByDateString.put(dateString, visit);
            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.sortDescendingByDate:: parsed date [%s] from name %s", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Visit> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(0, visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("GroupHeaderProvider.sortDescendingByDate:: [%d] visits sorted", visits.size()));
        return sortedVisits;
    }

    private List<Visit> sortAscendingByDate(List<Visit> visits)
    {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        HashMap<String, Visit> visitsByDateString = new HashMap<>();

        for(Visit visit : visits)
        {
            String dateString = simpleDateFormat.format(visit.getCalendar().getTime());
            visitsByDateString.put(dateString, visit);
            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.sortDescendingByDate:: parsed date [%s] from name %s", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Visit> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("GroupHeaderProvider.sortAscendingByDate:: [%d] visits sorted", visits.size()));

        return sortedVisits;
    }

    public SpecialGroupHeader getLatestYearHeader(List<? extends IElement> yearHeaders)
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
                else if((Integer.valueOf(yearHeader.getName()) > (Integer.valueOf(latestSpecialGroupHeader.getName()))))
                {
                    latestSpecialGroupHeader = (SpecialGroupHeader) yearHeader;
                }
            }

            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.getLatestYearHeader:: %s found as latest SpecialGroupHeader in a list of [%d]", latestSpecialGroupHeader, yearHeaders.size()));
        }

        return latestSpecialGroupHeader;
    }
}
