package de.juliusawen.coastercreditcounter.toolbox;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

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
}
