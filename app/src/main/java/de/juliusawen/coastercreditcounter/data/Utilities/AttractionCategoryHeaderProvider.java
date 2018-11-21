package de.juliusawen.coastercreditcounter.data.Utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategoryHeaderProvider
{
    private Map<UUID, AttractionCategoryHeader> headersByCategoryUuid = new HashMap<>();
    private List<? extends Element> attractions = new ArrayList<>();
    private List<Element> categorizedAttractions = new ArrayList<>();

    public List<Element> getCategorizedAttractions(List<Attraction> attractions)
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

    public List<Element> getCategorizedVisitedAttractions(List<VisitedAttraction> visitedAttractions)
    {
        if(Element.convertElementsToType(visitedAttractions, VisitedAttraction.class).equals(this.attractions))
        {
            Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: CategorizedAttractions unchanged");
            return this.categorizedAttractions;
        }
        else
        {
            Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.getCategorizedAttractions:: CategorizedAttractions changed...");
            this.attractions = visitedAttractions;
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
                        "initally fetching AttractionCategoryHeaders for [%d] attractions...", this.attractions.size()));

                for(Element element : this.attractions)
                {
                    AttractionCategoryHeader header;

                    AttractionCategory category =
                            element.isInstance(Attraction.class) ? ((Attraction)element).getCategory() : ((VisitedAttraction)element).getAttraction().getCategory();

                    UUID categoryUuid = category.getUuid();

                    if(this.headersByCategoryUuid.containsKey(categoryUuid))
                    {
                        header = this.headersByCategoryUuid.get(categoryUuid);
                        header.addChildToOrphanElement(element);

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
                        header.addChildToOrphanElement(element);
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
                Log.v(Constants.LOG_TAG, "AttractionCategoryHeaderProvider.categorizeAttractions:: no attractions passed");

                return this.categorizedAttractions;
            }
        }
        else
        {
            for(Element element : this.attractions)
            {
                AttractionCategory category =
                        element.isInstance(Attraction.class) ? ((Attraction)element).getCategory() : ((VisitedAttraction)element).getAttraction().getCategory();

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

                    if(header.containsChild(element))
                    {
                        header.deleteChild(element);
                    }

                    header.addChildToOrphanElement(element);

                    if(!this.categorizedAttractions.contains(header))
                    {
                        this.categorizedAttractions.add(header);
                        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: added %s to return list", header));
                    }
                }
                else
                {
                    AttractionCategoryHeader newHeader = AttractionCategoryHeader.create(category);
                    App.content.addOrphanElement(newHeader);
                    newHeader.addChildToOrphanElement(element);
                    this.headersByCategoryUuid.put(categoryUuid, newHeader);
                    this.categorizedAttractions.add(newHeader);

                    Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: created new %s and added %s", newHeader, element));

                    for(AttractionCategoryHeader existingHeader : this.headersByCategoryUuid.values())
                    {
                        if(!existingHeader.equals(newHeader) && existingHeader.containsChild(element))
                        {
                            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeaderProvider.categorizeAttractions:: %s deleted from %s", element, existingHeader));

                            existingHeader.deleteChild(element);
                            break;
                        }
                    }
                }
            }
        }

        List<Element> emptyHeaders = new ArrayList<>();
        for(Element header : this.categorizedAttractions)
        {
            if(!header.hasChildren())
            {
                emptyHeaders.add(header);
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

    public void removeCreatedAttractionCategoryHeaders()
    {
        Log.v(Constants.LOG_TAG,String.format("AttractionCategoryHeaderProvider.removeCreatedAttractionCategoryHeaders:: removing [%d] headers", this.headersByCategoryUuid.values().size()));

        for(AttractionCategoryHeader header : this.headersByCategoryUuid.values())
        {
            App.content.removeOrphanElement(header);
        }
    }
}
