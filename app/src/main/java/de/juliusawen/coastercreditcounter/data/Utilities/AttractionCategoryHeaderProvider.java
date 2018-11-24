package de.juliusawen.coastercreditcounter.data.Utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategoryHeaderProvider
{
    private Map<UUID, AttractionCategoryHeader> headersByCategoryUuid = new HashMap<>();
    private List<? extends Attraction> attractions = new ArrayList<>();
    private List<Element> categorizedAttractions = new ArrayList<>();

    public List<Element> getCategorizedAttractions(List<? extends Attraction> attractions)
    {
        if(Element.convertElementsToType(attractions, Attraction.class).equals(this.attractions))
        {
            Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: CategorizedAttractions unchanged");
            return this.categorizedAttractions;
        }
        else
        {
            Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: CategorizedAttractions changed...");
            this.attractions = attractions;
            this.categorizedAttractions.clear();
            return this.categorizeAttractions();
        }
    }

    private List<Element> categorizeAttractions()
    {
        if(this.headersByCategoryUuid.isEmpty())
        {
            if(!this.attractions.isEmpty())
            {
                Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions::" +
                        "initalizing AttractionCategoryHeaders for [%d] attractions...", this.attractions.size()));

                for(Element element : this.attractions)
                {
                    AttractionCategoryHeader header;

                    AttractionCategory category = ((Attraction)element).getAttrationCategory();

                    UUID categoryUuid = category.getUuid();

                    if(this.headersByCategoryUuid.containsKey(categoryUuid))
                    {
                        header = this.headersByCategoryUuid.get(categoryUuid);
                        header.addChild(element);

                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: added %s to %s", element, header));

                        if(!this.categorizedAttractions.contains(header))
                        {
                            this.categorizedAttractions.add(header);
                            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: added %s to return list", header));
                        }
                    }
                    else
                    {
                        header = AttractionCategoryHeader.create(category);
                        App.content.addOrphanElement(header);
                        header.addChild(element);
                        this.headersByCategoryUuid.put(categoryUuid, header);
                        this.categorizedAttractions.add(header);

                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: created new %s and added %s", header, element));
                    }
                }

                this.categorizedAttractions = this.sortHeadersBasedOnCategoriesOrder(this.categorizedAttractions);

                Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: " +
                        "[%d] AttractionCategoryHeaders added", this.categorizedAttractions.size()));

                return this.categorizedAttractions;
            }
            else
            {
                Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.categorizeAttractions:: no attractions to categorize");

                return this.categorizedAttractions;
            }
        }
        else
        {
            AttractionCategoryHeader.removeAllChildren(new ArrayList<>(this.headersByCategoryUuid.values()));

            for(Element element : this.attractions)
            {
                AttractionCategory category = ((Attraction)element).getAttrationCategory();

                UUID categoryUuid = category.getUuid();

                if(this.headersByCategoryUuid.containsKey(categoryUuid))
                {
                    AttractionCategoryHeader header = this.headersByCategoryUuid.get(categoryUuid);

                    if(!header.getName().equals(category.getName()))
                    {
                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: " +
                                "changing name for %s to [%s]...", header, category.getName()));

                        header.setName(category.getName());
                    }

                    if(!this.categorizedAttractions.contains(header))
                    {
                        this.categorizedAttractions.add(header);
                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: added %s to return list", header));
                    }

                    header.addChild(element);
                }
                else
                {
                    AttractionCategoryHeader newHeader = AttractionCategoryHeader.create(category);
                    App.content.addOrphanElement(newHeader);
                    this.headersByCategoryUuid.put(categoryUuid, newHeader);
                    this.categorizedAttractions.add(newHeader);

                    newHeader.addChild(element);

                    Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: created new %s and added %s", newHeader, element));
                }
            }
        }

        List<Element> emptyHeaders = new ArrayList<>();
        for(Element header : this.categorizedAttractions)
        {
            if(!header.hasChildren())
            {
                emptyHeaders.add(header);
                Log.e(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: %s is empty - marked for deletion", header));
            }
        }
        this.categorizedAttractions.removeAll(emptyHeaders);

        this.categorizedAttractions = this.sortHeadersBasedOnCategoriesOrder(this.categorizedAttractions);

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
