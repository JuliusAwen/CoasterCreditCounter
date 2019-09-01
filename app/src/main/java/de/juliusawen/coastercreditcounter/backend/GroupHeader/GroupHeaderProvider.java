package de.juliusawen.coastercreditcounter.backend.GroupHeader;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
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
    private List<IAttraction> formerAttractions;
    private List<IElement> formerGroupedAttractions;

    private final List<YearHeader> yearHeaders = new ArrayList<>();

    public List<IElement> groupByGroupType(List<IElement> elements, GroupType groupType)
    {
        List<IAttraction> attractions;

        switch(groupType)
        {
            case NONE:
                return elements;

            case YEAR:
                return this.groupByYear(elements);

                default:
                    attractions = ConvertTool.convertElementsToType(elements, IAttraction.class);
                    break;
        }

        List<IElement> groupedAttractions = new ArrayList<>();

        if(this.groupHeadersByGroupElementUuid.isEmpty())
        {
            if(!attractions.isEmpty())
            {
                Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: initalizing GroupHeaders for [%d] attractions...", attractions.size()));

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

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: added %s to %s", attraction, groupHeader));

                        if(!groupedAttractions.contains(groupHeader))
                        {
                            groupedAttractions.add(groupHeader);
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: added %s to GroupedAttractions", groupHeader));
                        }
                    }
                    else
                    {
                        groupHeader = GroupHeader.create(groupElement);
                        this.groupHeadersByGroupElementUuid.put(groupElementUuid, groupHeader);
                        groupedAttractions.add(groupHeader);

                        groupHeader.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: created new %s and added %s", groupHeader, attraction));
                    }
                }

                Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: [%d] GroupHeaders grouped", groupedAttractions.size()));

                groupedAttractions = this.sortGroupHeadersBasedOnGroupElementsOrder(groupedAttractions, groupType);

                this.formerAttractions = attractions;
                this.formerGroupedAttractions = groupedAttractions;

                return groupedAttractions;
            }
            else
            {
                Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupByGroupType:: no attractions to categorize");

                this.formerAttractions = attractions;
                this.formerGroupedAttractions = groupedAttractions;

                return groupedAttractions;
            }
        }
        else
        {
            if(this.attractionsOrderHasChanged(attractions))
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
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: changing name for %s to [%s]...", groupHeader, groupElement.getName()));

                            groupHeader.setName(groupElement.getName());
                        }

                        if(!groupedAttractions.contains(groupHeader))
                        {
                            groupedAttractions.add(groupHeader);
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: added %s to GroupedAttractions", groupHeader));
                        }

                        groupHeader.addChild(attraction);
                    }
                    else
                    {
                        groupHeader = GroupHeader.create(groupElement);
                        this.groupHeadersByGroupElementUuid.put(groupElementUuid, groupHeader);
                        groupedAttractions.add(groupHeader);

                        groupHeader.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: created new %s and added %s", groupHeader, attraction));
                    }
                }
            }
            else
            {
                Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupByGroupType:: attractions have not changed - returning former GroupedAttractions");
                return this.formerGroupedAttractions;
            }
        }

        List<IElement> emptyHeaders = new ArrayList<>();
        for(IElement header : groupedAttractions)
        {
            if(!header.hasChildren())
            {
                emptyHeaders.add(header);
                Log.e(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByGroupType:: %s is empty - marked for deletion", header));
            }
        }
        groupedAttractions.removeAll(emptyHeaders);

        groupedAttractions = this.sortGroupHeadersBasedOnGroupElementsOrder(groupedAttractions, groupType);

        this.formerAttractions = attractions;
        this.formerGroupedAttractions = groupedAttractions;

        return groupedAttractions;
    }

    private boolean attractionsOrderHasChanged(List<IAttraction> attractions)
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

                case ATTRACTION_CATEGORY:
                     groupElements = App.content.getContentOfType(AttractionCategory.class);
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


    private List<IElement> groupByYear(List<IElement> elements)
    {
        List<Visit> visits = ConvertTool.convertElementsToType(elements, Visit.class);

        if(visits.isEmpty())
        {
            Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupByYear:: no elements found");
            return new ArrayList<IElement>(visits);
        }

        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByYear:: adding YearHeaders to [%d] elements...", visits.size()));

        for(YearHeader yearHeader : this.yearHeaders)
        {
            yearHeader.getChildren().clear();
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
                YearHeader yearHeader = null;

                for(YearHeader header : this.yearHeaders)
                {
                    if(header.getName().equals(year))
                    {
                        yearHeader = header;
                        break;
                    }
                }

                if(yearHeader == null)
                {
                    yearHeader = YearHeader.create(year);
                    this.yearHeaders.add(yearHeader);
                    Log.d(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByYear:: created new %s", yearHeader));
                }

                yearHeader.addChild(visit);
                groupedVisits.add(yearHeader);
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

    public YearHeader getLatestYearHeader(List<? extends IElement> yearHeaders)
    {
        YearHeader latestYearHeader = null;

        if(yearHeaders.size() > 0)
        {
            for(IElement yearHeader : yearHeaders)
            {
                if(latestYearHeader == null)
                {
                    latestYearHeader = (YearHeader) yearHeader;
                }
                else if((Integer.valueOf(yearHeader.getName()) > (Integer.valueOf(latestYearHeader.getName()))))
                {
                    latestYearHeader = (YearHeader) yearHeader;
                }
            }

            Log.v(Constants.LOG_TAG, String.format("YearHeader.getLatestYearHeader:: %s found as latest YearHeader in a list of [%d]", latestYearHeader, yearHeaders.size()));
        }

        return latestYearHeader;
    }
}
