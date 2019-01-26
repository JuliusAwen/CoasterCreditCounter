package de.juliusawen.coastercreditcounter.toolbox;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.juliusawen.coastercreditcounter.backend.GroupHeader.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class SortTool
{
    public static void sortElementsByNameAscending(List<? extends IElement> elements)
    {
        if(elements.size() > 1)
        {
            Collections.sort(elements, new Comparator<IElement>()
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
    }

    public static void sortElementsByNameDescending(List<? extends IElement> elements)
    {
        if(elements.size() > 1)
        {
            Collections.sort(elements, new Comparator<IElement>()
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
                    " sorting [%d] AttractionCategoryHeaders based on [%d] ATTRACTION_CATEGORY", attractionCategoryHeaders.size(), attractionCategories.size()));

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
}
