package de.juliusawen.coastercreditcounter.backend.temporaryElements.GroupHeader;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.IBlueprint;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class GroupHeaderProvider
{
    public enum GroupType
    {
        NONE,
        YEAR,
        LOCATION,
        ATTRACTION_CATEGORY,
        MANUFACTURER,
        STATUS
    }

    private final Map<UUID, GroupHeader> groupHeadersByGroupElementUuid = new HashMap<>();
    private GroupType formerGroupType = GroupType.NONE;
    private List<IAttraction> formerAttractions;
    private List<IElement> formerGroupedAttractions;

    private final List<SpecialGroupHeader> specialGroupHeaders = new ArrayList<>();

    public List<IElement> groupElementsByGroupType(List<IElement> elements, GroupType groupType)
    {
        if(groupType == GroupType.NONE)
        {
            return elements;
        }
        else if(groupType == GroupType.YEAR)
        {
            return this.groupByYear(elements);
        }

        List<IAttraction> attractions = ConvertTool.convertElementsToType(elements, IAttraction.class);

        List<IElement> groupedAttractions = new ArrayList<>();

        Set<IElement> blueprints = new LinkedHashSet<>(); // bluprints have neither locations nor status - they will be filtered out and added again at the end
        if(groupType == GroupType.LOCATION || groupType == GroupType.STATUS)
        {
            for(IElement element : attractions)
            {
                if(IBlueprint.class.isAssignableFrom(element.getClass()))
                {
                    blueprints.add(element);
                }
            }
            attractions.removeAll(blueprints);
        }


        if(this.groupHeadersByGroupElementUuid.isEmpty() || this.formerGroupType != groupType)
        {
            groupHeadersByGroupElementUuid.clear();

            if(!attractions.isEmpty())
            {
                Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: initalizing GroupHeaders for [%d] attractions...", attractions.size()));

                for(IAttraction attraction : attractions)
                {
                    IElement groupElement = null;
                    switch(groupType)
                    {
                        case LOCATION:
                            groupElement = attraction.getParent();
                            break;

                        case ATTRACTION_CATEGORY:
                            groupElement = attraction.getAttractionCategory();
                            break;

                        case MANUFACTURER:
                            groupElement = attraction.getManufacturer();
                            break;

                        case STATUS:
                            groupElement = attraction.getStatus();
                            break;
                    }
                    UUID groupElementUuid = groupElement.getUuid();

                    GroupHeader groupHeader = this.groupHeadersByGroupElementUuid.get(groupElementUuid);
                    if(groupHeader != null)
                    {
                        groupHeader.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: added %s to %s", attraction, groupHeader));

                        if(!groupedAttractions.contains(groupHeader))
                        {
                            groupedAttractions.add(groupHeader);
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: added %s to GroupedAttractions", groupHeader));
                        }
                    }
                    else
                    {
                        groupHeader = GroupHeader.create(groupElement);
                        this.groupHeadersByGroupElementUuid.put(groupElementUuid, groupHeader);
                        groupedAttractions.add(groupHeader);

                        groupHeader.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: created new %s and added %s", groupHeader, attraction));
                    }
                }

                Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: created [%d] GroupHeaders", groupedAttractions.size()));
                groupedAttractions = this.sortGroupHeadersBasedOnGroupElementsOrder(groupedAttractions, groupType);
                this.addBlueprintsToGroupedAttractions(blueprints, groupedAttractions);

                this.formerAttractions = attractions;
                this.formerGroupedAttractions = groupedAttractions;

                return groupedAttractions;
            }
            else
            {
                Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupElementsByGroupType:: no attractions to group");

                this.formerGroupType = groupType;
                this.formerAttractions = attractions;
                this.formerGroupedAttractions = groupedAttractions;

                return groupedAttractions;
            }
        }
        else
        {
            if(this.attractionOrderHasChanged(attractions))
            {
                for(GroupHeader groupHeader : this.groupHeadersByGroupElementUuid.values())
                {
                    groupHeader.getChildren().clear();
                }

                for(IAttraction attraction : attractions)
                {
                    IElement groupElement = null;
                    switch(groupType)
                    {
                        case LOCATION:
                            groupElement = attraction.getParent();
                            break;

                        case ATTRACTION_CATEGORY:
                            groupElement = attraction.getAttractionCategory();
                            break;

                        case MANUFACTURER:
                            groupElement = attraction.getManufacturer();
                            break;

                        case STATUS:
                            groupElement = attraction.getStatus();
                            break;
                    }
                    UUID groupElementUuid = groupElement.getUuid();

                    GroupHeader groupHeader = this.groupHeadersByGroupElementUuid.get(groupElementUuid);
                    if(groupHeader != null)
                    {
                        if(!groupHeader.getName().equals(groupElement.getName()))
                        {
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: changing name for %s to [%s]...", groupHeader, groupElement.getName()));

                            groupHeader.setName(groupElement.getName());
                        }

                        if(!groupedAttractions.contains(groupHeader))
                        {
                            groupedAttractions.add(groupHeader);
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: added %s to GroupedAttractions", groupHeader));
                        }

                        groupHeader.addChild(attraction);
                    }
                    else
                    {
                        groupHeader = GroupHeader.create(groupElement);
                        this.groupHeadersByGroupElementUuid.put(groupElementUuid, groupHeader);
                        groupedAttractions.add(groupHeader);

                        groupHeader.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: created new %s and added %s", groupHeader, attraction));
                    }
                }
            }
            else
            {
                Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupElementsByGroupType:: attractions have not changed - returning former GroupedAttractions");
                return this.formerGroupedAttractions;
            }
        }

        List<IElement> emptyHeaders = new ArrayList<>();
        for(IElement header : groupedAttractions)
        {
            if(!header.hasChildren())
            {
                emptyHeaders.add(header);
                Log.e(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: %s is empty - marked for removal", header));
            }
        }
        groupedAttractions.removeAll(emptyHeaders);

        Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupElementsByGroupType:: created [%d] GroupHeaders", groupedAttractions.size()));
        groupedAttractions = this.sortGroupHeadersBasedOnGroupElementsOrder(groupedAttractions, groupType);
        this.addBlueprintsToGroupedAttractions(blueprints, groupedAttractions);

        this.formerAttractions = attractions;
        this.formerGroupedAttractions = groupedAttractions;

        return groupedAttractions;
    }

    private boolean attractionOrderHasChanged(List<IAttraction> attractions)
    {
        if(attractions.size() != this.formerAttractions.size())
        {
            return true;
        }
        else
        {
            for(IAttraction attraction : attractions)
            {
                if(!this.formerAttractions.contains(attraction) || !attraction.equals(this.formerAttractions.get(attractions.indexOf(attraction))))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private List<IElement> sortGroupHeadersBasedOnGroupElementsOrder(List<IElement> groupHeaders, GroupType groupType)
    {
        if(groupHeaders.size() > 1)
        {
            List<IElement> sortedGroupHeaders = new ArrayList<>();
            List<IElement> groupElements = new ArrayList<>();

            switch(groupType)
            {
                case YEAR:
                    return groupHeaders;

                case LOCATION:
                    groupElements = App.content.getContentOfType(Park.class);
                    break;

                case ATTRACTION_CATEGORY:
                     groupElements = App.content.getContentOfType(AttractionCategory.class);
                     break;

                case MANUFACTURER:
                    groupElements = App.content.getContentOfType(Manufacturer.class);
                    break;

                case STATUS:
                    groupElements = App.content.getContentOfType(Status.class);
                    break;
            }

            Log.v(Constants.LOG_TAG,  String.format("SortTool.sortGroupHeadersBasedOnGroupElementsOrder::" +
                    " sorting [%d] AttractionCategoryHeaders based on [%d] AttractionCategories", groupHeaders.size(), groupElements.size()));

            for(IElement groupElement : groupElements)
            {
                for(GroupHeader groupHeader : ConvertTool.convertElementsToType(groupHeaders, GroupHeader.class))
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
            Log.v(Constants.LOG_TAG,"SortTool.sortGroupHeadersBasedOnGroupElementsOrder:: not sorted - list contains less than two elements");
            return groupHeaders;
        }
    }

    private void addBlueprintsToGroupedAttractions(Set<IElement> blueprints, List<IElement> groupedAttractions)
    {
        if(!blueprints.isEmpty())
        {
            Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.addBlueprintsToGroupedAttractions:: adding [%d] blueprint(s) with special header", blueprints.size()));

            SpecialGroupHeader blueprintGroupHeader = SpecialGroupHeader.create(App.getContext().getString(R.string.text_blueprint_special_group_header_name));
            blueprintGroupHeader.addChildren(new ArrayList<>(blueprints));
            groupedAttractions.add(blueprintGroupHeader);
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

        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByYear:: adding YearHeaders to [%d] elements...", visits.size()));

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
                    Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByYear:: created new %s", specialGroupHeader));
                }

                specialGroupHeader.addChild(visit);
                groupedVisits.add(specialGroupHeader);
            }
        }

        Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByYear:: [%d] YearHeaders added", groupedVisits.size()));
        return groupedVisits;
    }

    private List<Visit> sortVisitsByDateAccordingToSortOrder(List<Visit> visits)
    {
        if(Visit.getSortOrder().equals(SortOrder.ASCENDING))
        {
            visits = this.sortAscendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("SortTool.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <ascending>", visits.size()));
        }
        else if(Visit.getSortOrder().equals(SortOrder.DESCENDING))
        {
            visits = this.sortDescendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("SortTool.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <descending>", visits.size()));
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
            Log.v(Constants.LOG_TAG, String.format("SortTool.sortDescendingByDate:: parsed date [%s] from name %s", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Visit> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(0, visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("SortTool.sortDescendingByDate:: [%d] visits sorted", visits.size()));
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
            Log.v(Constants.LOG_TAG, String.format("SortTool.sortDescendingByDate:: parsed date [%s] from name %s", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Visit> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("SortTool.sortAscendingByDate:: [%d] visits sorted", visits.size()));

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

            Log.v(Constants.LOG_TAG, String.format("SpecialGroupHeader.getLatestYearHeader:: %s found as latest SpecialGroupHeader in a list of [%d]", latestSpecialGroupHeader, yearHeaders.size()));
        }

        return latestSpecialGroupHeader;
    }
}
