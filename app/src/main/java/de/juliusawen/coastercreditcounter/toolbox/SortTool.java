package de.juliusawen.coastercreditcounter.toolbox;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.backend.GroupHeader.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public abstract class SortTool
{
    public static List<IElement> sortElementsByNameAscending(List<IElement> elements)
    {
        List<IElement> sortedElements = new ArrayList<>(elements); //passed list stays unsorted
        if(elements.size() > 1)
        {
            Collections.sort(sortedElements, new Comparator<IElement>()
            {
                @Override
                public int compare(IElement element1, IElement element2)
                {
                    return element1.getName().compareToIgnoreCase(element2.getName());
                }
            });
            Log.i(Constants.LOG_TAG,  String.format("SortTool.sortElementsByNameAscending:: [%s] elements sorted", elements.size()));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  "SortTool.sortElementsByNameAscending:: not sorted - list contains only one element");
        }

        return sortedElements;
    }

    public static List<IElement> sortElementsByNameDescending(List<IElement> elements)
    {
        List<IElement> sortedElements = new ArrayList<>(elements); //passed list stays unsorted
        if(elements.size() > 1)
        {
            Collections.sort(sortedElements, new Comparator<IElement>()
            {
                @Override
                public int compare(IElement element1, IElement element2)
                {
                    return element2.getName().compareToIgnoreCase(element1.getName());
                }
            });
            Log.i(Constants.LOG_TAG,  String.format("SortTool.sortElementsByNameDescending:: [%s] elements sorted", elements.size()));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  "SortTool.sortElementsByNameDescending:: not sorted - list contains only one element");
        }

        return sortedElements;
    }

    public static List<IElement> sortAttractionsByManufacturerAscending(List<IElement> attractions)
    {
        List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(attractions, IAttraction.class);
        if(attractions.size() > 1)
        {
            Collections.sort(sortedAttractions, new Comparator<IAttraction>()
            {
                @Override
                public int compare(IAttraction attraction1, IAttraction attraction2)
                {
                    return attraction1.getManufacturer().getName().compareToIgnoreCase(attraction2.getManufacturer().getName());
                }
            });
            Log.i(Constants.LOG_TAG,  String.format("SortTool.sortAttractionsByManufacturerAscending:: [%s] attractions sorted", attractions.size()));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  "SortTool.sortAttractionsByManufacturerAscending:: not sorted - list contains only one attraction");
        }

        return ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
    }

    public static List<IElement> sortAttractionsByManufacturerDescending(List<IElement> attractions)
    {
        List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(attractions, IAttraction.class);
        if(attractions.size() > 1)
        {
            Collections.sort(sortedAttractions, new Comparator<IAttraction>()
            {
                @Override
                public int compare(IAttraction attraction1, IAttraction attraction2)
                {
                    return attraction2.getManufacturer().getName().compareToIgnoreCase(attraction1.getManufacturer().getName());
                }
            });
            Log.i(Constants.LOG_TAG,  String.format("SortTool.sortAttractionsByManufacturerDescending:: [%s] attractions sorted", attractions.size()));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  "SortTool.sortAttractionsByManufacturerDescending:: not sorted - list contains only one attraction");
        }

        return ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
    }

    public static List<IElement> sortAttractionsByLocationAscending(List<IElement> attractions)
    {
        List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(attractions, IAttraction.class);
        if(attractions.size() > 1)
        {
            Collections.sort(sortedAttractions, new Comparator<IAttraction>()
            {
                @Override
                public int compare(IAttraction attraction1, IAttraction attraction2)
                {
                    IElement parent1 = attraction1.getParent();
                    IElement parent2 = attraction2.getParent();

                    // sort Blueprints to bottom of list
                    if(parent1 == null && parent2 == null)
                    {
                        return 0;
                    }
                    else if(parent1 == null)
                    {
                        return 1;
                    }
                    else if(parent2 == null)
                    {
                        return -1;
                    }
                    else
                    {
                        return parent1.getName().compareToIgnoreCase(parent2.getName());
                    }

                }
            });
            Log.i(Constants.LOG_TAG,  String.format("SortTool.sortAttractionsByManufacturerAscending:: [%s] attractions sorted", attractions.size()));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  "SortTool.sortAttractionsByManufacturerAscending:: not sorted - list contains only one attraction");
        }

        return ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
    }

    public static List<IElement> sortAttractionsByLocationDescending(List<IElement> attractions)
    {
        List<IAttraction> sortedAttractions = ConvertTool.convertElementsToType(attractions, IAttraction.class);
        if(attractions.size() > 1)
        {
            Collections.sort(sortedAttractions, new Comparator<IAttraction>()
            {
                @Override
                public int compare(IAttraction attraction1, IAttraction attraction2)
                {
                    IElement parent1 = attraction1.getParent();
                    IElement parent2 = attraction2.getParent();

                    // sort Blueprints to top of list
                    if(parent1 == null && parent2 == null)
                    {
                        return 0;
                    }
                    else if(parent2 == null)
                    {
                        return 1;
                    }
                    else if(parent1 == null)
                    {
                        return -1;
                    }
                    else
                    {
                        return parent2.getName().compareToIgnoreCase(parent1.getName());
                    }
                }
            });
            Log.i(Constants.LOG_TAG,  String.format("SortTool.sortAttractionsByManufacturerDescending:: [%s] attractions sorted", attractions.size()));
        }
        else
        {
            Log.v(Constants.LOG_TAG,  "SortTool.sortAttractionsByManufacturerDescending:: not sorted - list contains only one attraction");
        }

        return ConvertTool.convertElementsToType(sortedAttractions, IElement.class);
    }

    public static List<IElement> sortElementsBasedOnComparisonList(List<IElement> elementsToSort, List<IElement> comparisonList)
    {
        if(elementsToSort.size() > 1)
        {
            Log.v(Constants.LOG_TAG,  String.format("SortTool.sortElementsBasedOnComparisonList:: sorted [%d] elements based on comparison list containing [%d] elements",
                    elementsToSort.size(), comparisonList.size()));
            List<IElement> sortedElements = new ArrayList<>();
            for(IElement element : comparisonList)
            {
                if(elementsToSort.contains(element))
                {
                    sortedElements.add(elementsToSort.get(elementsToSort.indexOf(element)));
                }
            }
            return sortedElements;
        }
        else
        {
            Log.v(Constants.LOG_TAG,"SortTool.sortElementsBasedOnComparisonList:: not sorted - list contains only one element");
            return elementsToSort;
        }
    }

    public static List<IElement> sortAttractionCategoryHeadersBasedOnCategoriesOrder(List<IElement> attractionCategoryHeaders)
    {
        if(attractionCategoryHeaders.size() > 1)
        {
            List<IElement> sortedAttractionCategoryHeaders = new ArrayList<>();
            List<AttractionCategory> attractionCategories = App.content.getContentAsType(AttractionCategory.class);

            Log.v(Constants.LOG_TAG,  String.format("SortTool.sortAttractionCategoryHeadersBasedOnCategoriesOrder::" +
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
            Log.v(Constants.LOG_TAG,"SortTool.sortAttractionCategoryHeadersBasedOnCategoriesOrder:: not sorted - list contains less than two elements");
            return attractionCategoryHeaders;
        }
    }

    public static List<Visit> sortVisitsByDateAccordingToSortOrder(List<Visit> visits)
    {
        if(Visit.getSortOrder().equals(SortOrder.ASCENDING))
        {
            visits = SortTool.sortAscendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("SortTool.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <ascending>", visits.size()));
        }
        else if(Visit.getSortOrder().equals(SortOrder.DESCENDING))
        {
            visits = SortTool.sortDescendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("SortTool.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <descending>", visits.size()));
        }

        return visits;
    }

    public static List<Visit> sortDescendingByDate(List<Visit> visits)
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

    public static List<Visit> sortAscendingByDate(List<Visit> visits)
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
}
