package de.juliusawen.coastercreditcounter.backend.GroupHeader;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.SortTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class GroupHeaderProvider
{
    private final Map<UUID, AttractionCategoryHeader> attractionCategoryHeadersByCategoryUuid = new HashMap<>();
    private List<IAttraction> formerAttractions;
    private List<IElement> formerGroupedAttractions;

    private final List<YearHeader> yearHeaders = new ArrayList<>();

    public List<IElement> groupByAttractionCategories(List<IAttraction> attractions)
    {
        List<IElement> groupedAttractions = new ArrayList<>();

        if(this.attractionCategoryHeadersByCategoryUuid.isEmpty())
        {
            if(!attractions.isEmpty())
            {
                Log.v(Constants.LOG_TAG, String.format("GroupHeaderProvider.groupByAttractionCategories:: initalizing AttractionCategoryHeaders for [%d] attractions...", attractions.size()));

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

                groupedAttractions = SortTool.sortAttractionCategoryHeadersBasedOnCategoriesOrder(groupedAttractions);

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
            if(this.attractionsOrderHasChanged(attractions))
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

        groupedAttractions = SortTool.sortAttractionCategoryHeadersBasedOnCategoriesOrder(groupedAttractions);

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

        visits = SortTool.sortVisitsByDateAccordingToSortOrder(visits);

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
