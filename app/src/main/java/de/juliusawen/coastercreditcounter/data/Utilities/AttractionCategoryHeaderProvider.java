package de.juliusawen.coastercreditcounter.data.Utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategoryHeaderProvider
{
    private Map<UUID, AttractionCategoryHeader> headersByCategoryUuid = new HashMap<>();

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
                        App.content.addOrphanElement(header);
                        this.headersByCategoryUuid.put(categoryUuid, header);
                        categorizedAttractions.add(header);

                        header.addChild(attraction);

                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: created new %s and added %s", header, attraction));
                    }
                }

                Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: " +
                        "[%d] AttractionCategoryHeaders added", categorizedAttractions.size()));

                return this.sortHeadersBasedOnCategoriesOrder(categorizedAttractions);
            }
            else
            {
                Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: no attractions to categorize");

                return categorizedAttractions;
            }
        }
        else
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
                    App.content.addOrphanElement(newHeader);
                    this.headersByCategoryUuid.put(categoryUuid, newHeader);
                    categorizedAttractions.add(newHeader);

                    newHeader.addChild(attraction);

                    Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: created new %s and added %s", newHeader, attraction));
                }
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

        return this.sortHeadersBasedOnCategoriesOrder(categorizedAttractions);


    }

    private List<IElement> sortHeadersBasedOnCategoriesOrder(List<IElement> attractionCategoryHeaders)
    {
        if(attractionCategoryHeaders.size() > 1)
        {
            List<IElement> sortedAttractionCategoryHeaders = new ArrayList<>();
            List<AttractionCategory> attractionCategories = App.content.getAttractionCategories();

            Log.v(Constants.LOG_TAG,  String.format("AttractionCategoryHeaderProvider.sortHeadersBasedOnCategoriesOrder::" +
                    " sorting [%d]AttractionCategoryHeaders based on [%d]AttractionCategories", attractionCategoryHeaders.size(), attractionCategories.size()));

            List<AttractionCategoryHeader> castedAttractionCategoryHeaders = Element.convertElementsToType(attractionCategoryHeaders, AttractionCategoryHeader.class);

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
            Log.v(Constants.LOG_TAG,"AttractionCategoryHeaderProvider.sortHeadersBasedOnCategoriesOrder:: not sorted - list contains less than two elements");
            return attractionCategoryHeaders;
        }
    }

    public void removeCreatedAttractionCategoryHeadersFromContent()
    {
        Log.v(Constants.LOG_TAG,String.format("AttractionCategoryHeaderProvider.removeCreatedAttractionCategoryHeadersFromContent:: " +
                "removing [%d] headers from content...", this.headersByCategoryUuid.values().size()));

        for(AttractionCategoryHeader header : this.headersByCategoryUuid.values())
        {
            App.content.removeOrphanElement(header);
        }
    }
}
