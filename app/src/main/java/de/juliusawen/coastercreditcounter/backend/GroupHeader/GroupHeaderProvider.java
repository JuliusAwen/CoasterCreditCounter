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

public class GroupHeaderProvider
{
    private final DateFormat simpleDateFormat;

    private final Map<UUID, AttractionCategoryHeader> attractionCategoryHeadersByCategoryUuid = new HashMap<>();
    private List<IAttraction> formerAttractions;
    private List<IElement> formerGroupedAttractions;

    private final List<YearHeader> yearHeaders = new ArrayList<>();

    public GroupHeaderProvider()
    {
        this.simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_YEAR_PATTERN, Locale.getDefault());
    }

    public List<IElement> groupByAttractionCategories(List<IAttraction> attractions)
    {
        List<IElement> groupedAttractions = new ArrayList<>();

        if(this.attractionCategoryHeadersByCategoryUuid.isEmpty())
        {
            if(!attractions.isEmpty())
            {
                Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: " +
                        "initalizing AttractionCategoryHeaders for [%d] attractions...", attractions.size()));

                for(IAttraction attraction : attractions)
                {
                    AttractionCategoryHeader header;
                    AttractionCategory category = attraction.getAttractionCategory();
                    UUID categoryUuid = category.getUuid();

                    header = this.attractionCategoryHeadersByCategoryUuid.get(categoryUuid);
                    if(header != null)
                    {
                        header.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: added %s to %s", attraction, header));

                        if(!groupedAttractions.contains(header))
                        {
                            groupedAttractions.add(header);
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: added %s to GroupedAttractions", header));
                        }
                    }
                    else
                    {
                        header = AttractionCategoryHeader.create(category);
                        this.attractionCategoryHeadersByCategoryUuid.put(categoryUuid, header);
                        groupedAttractions.add(header);

                        header.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: created new %s and added %s", header, attraction));
                    }
                }

                Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: [%d] AttractionCategoryHeaders grouped", groupedAttractions.size()));

                groupedAttractions = this.sortAttractionCategoryHeadersBasedOnCategoriesOrder(groupedAttractions);

                this.formerAttractions = attractions;
                this.formerGroupedAttractions = groupedAttractions;

                return groupedAttractions;
            }
            else
            {
                Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupByAttractionCategories:: no attractions to categorize");

                this.formerAttractions = attractions;
                this.formerGroupedAttractions = groupedAttractions;

                return groupedAttractions;
            }
        }
        else
        {
            if(this.attractionsHaveChanged(attractions))
            {
                for(AttractionCategoryHeader attractionCategoryHeader : this.attractionCategoryHeadersByCategoryUuid.values())
                {
                    attractionCategoryHeader.getChildren().clear();
                }

                for(IAttraction attraction : attractions)
                {
                    AttractionCategory category = attraction.getAttractionCategory();
                    UUID categoryUuid = category.getUuid();

                    AttractionCategoryHeader header = this.attractionCategoryHeadersByCategoryUuid.get(categoryUuid);
                    if(header != null)
                    {
                        if(!header.getName().equals(category.getName()))
                        {
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: changing name for %s to [%s]...", header, category.getName()));

                            header.setName(category.getName());
                        }

                        if(!groupedAttractions.contains(header))
                        {
                            groupedAttractions.add(header);
                            Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: added %s to GroupedAttractions", header));
                        }

                        header.addChild(attraction);
                    }
                    else
                    {
                        header = AttractionCategoryHeader.create(category);
                        this.attractionCategoryHeadersByCategoryUuid.put(categoryUuid, header);
                        groupedAttractions.add(header);

                        header.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: created new %s and added %s", header, attraction));
                    }
                }
            }
            else
            {
                Log.v(Constants.LOG_TAG, "GroupHeaderProvider.groupByAttractionCategories:: attractions have not changed - returning former GroupedAttractions");
                return this.formerGroupedAttractions;
            }
        }

        List<IElement> emptyHeaders = new ArrayList<>();
        for(IElement header : groupedAttractions)
        {
            if(!header.hasChildren())
            {
                emptyHeaders.add(header);
                Log.e(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: %s is empty - marked for deletion", header));
            }
        }
        groupedAttractions.removeAll(emptyHeaders);

        groupedAttractions = this.sortAttractionCategoryHeadersBasedOnCategoriesOrder(groupedAttractions);

        this.formerAttractions = attractions;
        this.formerGroupedAttractions = groupedAttractions;

        return groupedAttractions;
    }

    private boolean attractionsHaveChanged(List<IAttraction> attractions)
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

    private List<IElement> sortAttractionCategoryHeadersBasedOnCategoriesOrder(List<IElement> attractionCategoryHeaders)
    {
        if(attractionCategoryHeaders.size() > 1)
        {
            List<IElement> sortedAttractionCategoryHeaders = new ArrayList<>();
            List<AttractionCategory> attractionCategories = App.content.getContentAsType(AttractionCategory.class);

            Log.v(Constants.LOG_TAG,  String.format("GroupHeaderProvider.sortAttractionCategoryHeadersBasedOnCategoriesOrder::" +
                    " sorting [%d] AttractionCategoryHeaders based on [%d] AttractionCategories", attractionCategoryHeaders.size(), attractionCategories.size()));

            List<AttractionCategoryHeader> castedAttractionCategoryHeaders = ConvertTool.convertElementsToType(attractionCategoryHeaders, AttractionCategoryHeader.class);

            for(AttractionCategory attractionCategory : attractionCategories)
            {
                for(AttractionCategoryHeader attractionCategoryHeader : castedAttractionCategoryHeaders)
                {
                    if(attractionCategoryHeader.getAttractionCategory().equals(attractionCategory) && !sortedAttractionCategoryHeaders.contains(attractionCategoryHeader))
                    {
                        sortedAttractionCategoryHeaders.add(attractionCategoryHeader);
                        break;
                    }
                }
            }

            return sortedAttractionCategoryHeaders;
        }
        else
        {
            Log.v(Constants.LOG_TAG,"GroupHeaderProvider.sortAttractionCategoryHeadersBasedOnCategoriesOrder:: not sorted - list contains less than two elements");
            return attractionCategoryHeaders;
        }
    }

    public List<IElement> groupByYear(List<Visit> visits)
    {
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
            String year = String.valueOf(this.simpleDateFormat.format(visit.getCalendar().getTime()));

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
            Log.v(Constants.LOG_TAG, String.format("Visit.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <ascending>", visits.size()));
        }
        else if(Visit.getSortOrder().equals(SortOrder.DESCENDING))
        {
            visits = this.sortDescendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("Visit.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <descending>", visits.size()));
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
            Log.v(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: parsed date [%s] from name %s", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Visit> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(0, visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: [%d] visits sorted", visits.size()));
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
            Log.v(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: parsed date [%s] from name %s", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Visit> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("Visit.sortAscendingByDate:: [%d] visits sorted", visits.size()));

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

            Log.v(Constants.LOG_TAG,  String.format("YearHeader.getLatestYearHeader:: %s found as latest YearHeader in a list of [%d]", latestYearHeader, yearHeaders.size()));
        }

        return latestYearHeader;
    }
}
