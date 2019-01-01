package de.juliusawen.coastercreditcounter.backend.Utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.objects.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.SortTool;

public class AttractionCategoryHeaderProvider
{
    private final Map<UUID, AttractionCategoryHeader> headersByCategoryUuid = new HashMap<>();
    private List<IAttraction> formerAttractions;
    private List<IElement> formerCategorizedAttractions;

    public List<IElement> getCategorizedAttractions(List<IAttraction> attractions)
    {
        List<IElement> categorizedAttractions = new ArrayList<>();

        if(this.headersByCategoryUuid.isEmpty())
        {
            if(!attractions.isEmpty())
            {
                Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions::" +
                        "initalizing AttractionCategoryHeaders for [%d] attractions...", attractions.size()));

                for(IAttraction attraction : attractions)
                {
                    AttractionCategoryHeader header;

                    AttractionCategory category = attraction.getAttractionCategory();

                    UUID categoryUuid = category.getUuid();

                    header = this.headersByCategoryUuid.get(categoryUuid);
                    if(header != null)
                    {
                        header.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: added %s to %s", attraction, header));

                        if(!categorizedAttractions.contains(header))
                        {
                            categorizedAttractions.add(header);
                            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: added %s to CategorizedAttractions", header));
                        }
                    }
                    else
                    {
                        header = AttractionCategoryHeader.create(category);
                        this.headersByCategoryUuid.put(categoryUuid, header);
                        categorizedAttractions.add(header);

                        header.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: created new %s and added %s", header, attraction));
                    }
                }

                Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: " +
                        "[%d] AttractionCategoryHeaders added", categorizedAttractions.size()));

                categorizedAttractions = SortTool.sortAttractionCategoryHeadersBasedOnCategoriesOrder(categorizedAttractions);

                this.formerAttractions = attractions;
                this.formerCategorizedAttractions = categorizedAttractions;

                return categorizedAttractions;
            }
            else
            {
                Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: no attractions to categorize");

                this.formerAttractions = attractions;
                this.formerCategorizedAttractions = categorizedAttractions;

                return categorizedAttractions;
            }
        }
        else
        {
            if(this.attractionsHaveChanged(attractions))
            {
                AttractionCategoryHeader.removeAllChildren(new ArrayList<>(this.headersByCategoryUuid.values()));

                for(IAttraction attraction : attractions)
                {
                    AttractionCategory category = (attraction).getAttractionCategory();

                    UUID categoryUuid = category.getUuid();

                    AttractionCategoryHeader header = this.headersByCategoryUuid.get(categoryUuid);
                    if(header != null)
                    {
                        if(!header.getName().equals(category.getName()))
                        {
                            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: " +
                                    "changing name for %s to [%s]...", header, category.getName()));

                            header.setName(category.getName());
                        }

                        if(!categorizedAttractions.contains(header))
                        {
                            categorizedAttractions.add(header);
                            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: added %s to CategorizedAttractions", header));
                        }

                        header.addChild(attraction);
                    }
                    else
                    {
                        AttractionCategoryHeader newHeader = AttractionCategoryHeader.create(category);
                        this.headersByCategoryUuid.put(categoryUuid, newHeader);
                        categorizedAttractions.add(newHeader);

                        newHeader.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: created new %s and added %s", newHeader, attraction));
                    }
                }
            }
            else
            {
                Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: attractions have not changed - returning former categorized attractions");
                return this.formerCategorizedAttractions;
            }
        }

        List<IElement> emptyHeaders = new ArrayList<>();
        for(IElement header : categorizedAttractions)
        {
            if(!header.hasChildren())
            {
                emptyHeaders.add(header);
                Log.e(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: %s is empty - marked for deletion", header));
            }
        }
        categorizedAttractions.removeAll(emptyHeaders);

        categorizedAttractions = SortTool.sortAttractionCategoryHeadersBasedOnCategoriesOrder(categorizedAttractions);

        this.formerAttractions = attractions;
        this.formerCategorizedAttractions = categorizedAttractions;

        return categorizedAttractions;
    }

    private boolean attractionsHaveChanged(List<IAttraction> attractions)
    {
        for(IAttraction attraction : attractions)
        {
            if(!attraction.equals(this.formerAttractions.get(attractions.indexOf(attraction))))
            {
                return true;
            }
        }

        return false;
    }
}
