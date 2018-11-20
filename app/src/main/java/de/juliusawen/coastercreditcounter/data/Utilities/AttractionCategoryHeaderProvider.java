package de.juliusawen.coastercreditcounter.data.Utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategoryHeaderProvider
{
    private Map<UUID, AttractionCategoryHeader> headersByCategoryUuid = new HashMap<>();
    private List<Attraction> attractions = new ArrayList<>();
    private List<Element> categorizedAttractions = new ArrayList<>();

    public List<Element> getCategorizedAttractions(List<Attraction> attractions)
    {
        if(attractions.equals(this.attractions))
        {
            Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: CategorizedAttractions unchanged");
            return this.categorizedAttractions;
        }
        else
        {
            Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: CategorizedAttractions changed...");
            this.attractions = attractions;
        }

        if(this.headersByCategoryUuid.isEmpty())
        {
            if(!attractions.isEmpty())
            {
                Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions::" +
                        "initally fetching AttractionCategoryHeaders for [%d] attractions...", attractions.size()));

                for(Attraction attraction : attractions)
                {

                    AttractionCategoryHeader header;
                    UUID categoryUuid = attraction.getCategory().getUuid();

                    if(this.headersByCategoryUuid.containsKey(categoryUuid))
                    {
                        header = this.headersByCategoryUuid.get(categoryUuid);
                        header.addChildToOrphanElement(attraction);

                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: added %s to %s", attraction, header));
                    }
                    else
                    {
                        header = AttractionCategoryHeader.create(attraction.getCategory());
                        App.content.addOrphanElement(header);
                        header.addChildToOrphanElement(attraction);
                        this.headersByCategoryUuid.put(categoryUuid, header);
                        this.categorizedAttractions.add(header);

                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: created new %s and added %s", header, attraction));
                    }
                }

                this.categorizedAttractions = this.sortHeadersBasedOnCategoriesOrder(this.categorizedAttractions);

                Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: [%d] AttractionCategoryHeaders added", categorizedAttractions.size()));
                return categorizedAttractions;
            }
            else
            {
                Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: no attractions passed");

                return this.categorizedAttractions;
            }
        }
        else
        {
            for(Attraction attraction : attractions)
            {
                UUID categoryUuid = attraction.getCategory().getUuid();

                if(this.headersByCategoryUuid.containsKey(categoryUuid))
                {
                    AttractionCategoryHeader header = this.headersByCategoryUuid.get(categoryUuid);

//                    if(!header.getName().equals(attraction.getCategory().getName()))
//                    {
//                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: " +
//                                "changing name for %s to [%s]...", header, attraction.getCategory().getName()));
//
//                        header.setName(attraction.getCategory().getName());
//                    }

                    if(header.containsChild(attraction))
                    {
                        header.deleteChild(attraction);
                    }

                    header.addChildToOrphanElement(attraction);
                }
                else
                {
                    AttractionCategoryHeader newHeader = AttractionCategoryHeader.create(attraction.getCategory());
                    App.content.addOrphanElement(newHeader);
                    newHeader.addChildToOrphanElement(attraction);
                    this.headersByCategoryUuid.put(categoryUuid, newHeader);
                    this.categorizedAttractions.add(newHeader);

                    Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: created new %s and added %s", newHeader, attraction));

                    for(AttractionCategoryHeader existingHeader : this.headersByCategoryUuid.values())
                    {
                        if(!existingHeader.equals(newHeader) && existingHeader.containsChild(attraction))
                        {
                            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.getCategorizedAttractions:: %s deleted from %s", attraction, existingHeader));

                            existingHeader.deleteChild(attraction);
                            break;
                        }
                    }
                }
            }
        }

//        return this.sortHeadersBasedOnCategoriesOrder(this.categorizedAttractions);
        return this.categorizedAttractions;
    }


    private List<Element> sortHeadersBasedOnCategoriesOrder(List<Element> attractionCategoryHeaders)
    {
        if(attractionCategoryHeaders.size() > 1)
        {
            List<Element> sortedAttractionCategoryHeaders = new ArrayList<>();
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

    public void removeCreatedAttractionCategoryHeaders()
    {
        Log.v(Constants.LOG_TAG,String.format("AttractionCategoryHeaderProvider.removeCreatedAttractionCategoryHeaders:: removing [%d] headers", this.headersByCategoryUuid.values().size()));

        for(AttractionCategoryHeader header : this.headersByCategoryUuid.values())
        {
            App.content.removeOrphanElement(header);
        }
    }
}
