package de.juliusawen.coastercreditcounter.data.orphanElements;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.CountableAttraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategoryHeader extends OrphanElement
{
    private AttractionCategory attractionCategory;

    private AttractionCategoryHeader(String name, UUID uuid, AttractionCategory attractionCategory)
    {
        super(name, uuid);
        this.attractionCategory = attractionCategory;
    }

    public AttractionCategory getAttractionCategory()
    {
        return attractionCategory;
    }

    public static AttractionCategoryHeader create(AttractionCategory attractionCategory)
    {
        AttractionCategoryHeader attractionCategoryHeader;
        attractionCategoryHeader = new AttractionCategoryHeader(attractionCategory.getName(), UUID.randomUUID(), attractionCategory);

        Log.v(Constants.LOG_TAG,  String.format("AttractionCategoryHeader.create:: %s created", attractionCategoryHeader.getFullName()));

        return attractionCategoryHeader;
    }

    public static List<Element> fetchAttractionCategoryHeadersFromElements(List<? extends Element> elements)
    {
        if(elements.isEmpty())
        {
            Log.v(Constants.LOG_TAG, "AttractionCategoryHeader.fetchAttractionCategoryHeadersFromElements:: no attractions");
            return new ArrayList<>(elements);
        }

        List<Element> preparedElements = new ArrayList<>();

        if(elements.get(0).isInstance(Attraction.class))
        {
            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeader.fetchAttractionCategoryHeadersFromElements:: fetching headers for #[%d] attractions...", elements.size()));
            List<Attraction> attractions = Element.convertElementsToType(elements, Attraction.class);

            for(Attraction attraction : attractions)
            {
                Element existingCategoryHeader = null;
                for(Element attractionCategoryHeader : preparedElements)
                {
                    if(((AttractionCategoryHeader)attractionCategoryHeader).getAttractionCategory().equals(attraction.getCategory()))
                    {
                        existingCategoryHeader = attractionCategoryHeader;
                    }
                }

                if(existingCategoryHeader != null)
                {
                    existingCategoryHeader.addChildToOrphanElement(attraction);
                }
                else
                {
                    Element attractionCategoryHeader = AttractionCategoryHeader.create(attraction.getCategory());
                    App.content.addOrphanElement(attractionCategoryHeader);
                    attractionCategoryHeader.addChildToOrphanElement(attraction);
                    preparedElements.add(attractionCategoryHeader);
                }
            }
        }
        else if(elements.get(0).isInstance(CountableAttraction.class))
        {
            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeader.fetchAttractionCategoryHeadersFromElements:: adding headers for #[%d] countable attractions...", elements.size()));
            List<CountableAttraction> countableAttractions = Element.convertElementsToType(elements, CountableAttraction.class);

            for(CountableAttraction countableAttraction : countableAttractions)
            {
                Element existingCategoryHeader = null;
                for(Element attractionCategoryHeader : preparedElements)
                {
                    if(((AttractionCategoryHeader)attractionCategoryHeader).getAttractionCategory().equals(countableAttraction.getAttraction().getCategory()))
                    {
                        existingCategoryHeader = attractionCategoryHeader;
                    }
                }

                if(existingCategoryHeader != null)
                {
                    existingCategoryHeader.addChildToOrphanElement(countableAttraction);
                }
                else
                {
                    Element attractionCategoryHeader = AttractionCategoryHeader.create(countableAttraction.getAttraction().getCategory());
                    App.content.addOrphanElement(attractionCategoryHeader);
                    attractionCategoryHeader.addChildToOrphanElement(countableAttraction);
                    preparedElements.add(attractionCategoryHeader);
                }
            }
        }

        preparedElements = new ArrayList<Element>(
                AttractionCategoryHeader.sortAttractionCategoryHeadersBasedOnAttractionCategoriesOrder(Element.convertElementsToType(preparedElements, AttractionCategoryHeader.class)));

        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeader.fetchAttractionCategoryHeadersFromElements:: #[%d] headers added", preparedElements.size()));
        return preparedElements;
    }

    private static List<AttractionCategoryHeader> sortAttractionCategoryHeadersBasedOnAttractionCategoriesOrder(List<AttractionCategoryHeader> attractionCategoryHeaders)
    {
        if(attractionCategoryHeaders.size() > 1)
        {
            List<AttractionCategoryHeader> sortedAttractionCategoryHeaders = new ArrayList<>();
            List<AttractionCategory> attractionCategories = App.content.getAttractionCategories();

            Log.v(Constants.LOG_TAG,  String.format("AttractionCategoryHeader.sortAttractionCategoryHeadersBasedOnAttractionCategoriesOrder::" +
                            " sorting #[%d] AttractionCategoryHeaders based on #[%d] AttractionCategories", attractionCategoryHeaders.size(), attractionCategories.size()));

            for(AttractionCategory attractionCategory : attractionCategories)
            {
                for(AttractionCategoryHeader attractionCategoryHeader : attractionCategoryHeaders)
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
            Log.v(Constants.LOG_TAG,"Element.sortElementsBasedOnComparisonList:: not sorted - list contains less than two elements");
            return attractionCategoryHeaders;
        }
    }

    public static AttractionCategoryHeader getAttractionCategoryHeaderForAttractionCategoryFromElements(List<? extends Element> elements, AttractionCategory attractionCategory)
    {
        for(Element element : elements)
        {
            if(element.isInstance(AttractionCategoryHeader.class))
            {
                if(((AttractionCategoryHeader)element).getAttractionCategory().equals(attractionCategory))
                {
                    return (AttractionCategoryHeader) element;
                }
            }
        }

        return null;
    }

    public static Set<Element> getAttractionCategoryHeadersToExpandAccordingToSettings(List<? extends Element> attractionCategoryHeaders)
    {
        Set<Element> attractionCategoryHeadersToExpand = new HashSet<>();
        for(Element attractionCategoryHeader : attractionCategoryHeaders)
        {
            if(attractionCategoryHeader.isInstance(AttractionCategoryHeader.class))
            {
                if(App.settings.getAttractionCategoriesToExpandByDefault().contains(((AttractionCategoryHeader)attractionCategoryHeader).getAttractionCategory()))
                {
                    attractionCategoryHeadersToExpand.add(attractionCategoryHeader);
                }
            }
        }
        return attractionCategoryHeadersToExpand;
    }
}
