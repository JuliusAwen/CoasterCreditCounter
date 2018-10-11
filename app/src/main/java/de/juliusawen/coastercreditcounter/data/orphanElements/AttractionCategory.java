package de.juliusawen.coastercreditcounter.data.orphanElements;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Coaster;
import de.juliusawen.coastercreditcounter.data.elements.CountableAttraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategory extends OrphanElement
{
    private static List<AttractionCategory> attractionCategories = new ArrayList<>();

    private AttractionCategory(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static AttractionCategory create(String name)
    {
        AttractionCategory attractionCategory = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            attractionCategory = new AttractionCategory(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("AttractionCategory.create:: %s created.", attractionCategory));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("AttractionCategory.create:: invalid name[%s] - attractionCategory not created.", name));
        }
        return attractionCategory;
    }

    public static List<AttractionCategory> getAttractionCategories()
    {
        return AttractionCategory.attractionCategories;
    }

    public static void setAttractionCategories(List<AttractionCategory> attractionCategories)
    {
        AttractionCategory.attractionCategories = attractionCategories;
        Log.d(Constants.LOG_TAG,  String.format("Attraction.setAttractionCategories:: #[%d] attractionCategories set", attractionCategories.size()));
    }


    public static List<AttractionCategory> convertToAttractionCategories(List<? extends Element> elements)
    {
        List<AttractionCategory> attractionCategories = new ArrayList<>();
        for(Element element : elements)
        {
            if(element.isInstance(AttractionCategory.class))
            {
                attractionCategories.add((AttractionCategory) element);
            }
            else
            {
                String errorMessage = String.format("type mismatch: %s is not of type <AttractionCategory>", element);
                Log.e(Constants.LOG_TAG, "Attraction.convertToAttractionCategories:: " + errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }
        return attractionCategories;
    }

    public static List<Element> addAttractionCategoryHeaders(List<? extends Element> elements)
    {
        if(elements.isEmpty())
        {
            Log.v(Constants.LOG_TAG, "AttractionCategory.addAttractionCategoryHeaders:: no attractions found");
            return new ArrayList<>(elements);
        }

        List<Element> preparedElements = new ArrayList<>();
        AttractionCategory.removeAllChildren(AttractionCategory.getAttractionCategories());

        if(elements.get(0).isInstance(Attraction.class))
        {
            Log.v(Constants.LOG_TAG, String.format("AttractionCategory.addAttractionCategoryHeaders:: adding headers for #[%d] attractions...", elements.size()));
            List<Attraction> attractions = Attraction.convertToAttractions(elements);

            for(Attraction attraction : attractions)
            {
                Element existingCategory = null;
                for(Element attractionCategory : preparedElements)
                {
                    if(attractionCategory.equals(attraction.getCategory()))
                    {
                        existingCategory = attractionCategory;
                    }
                }

                if(existingCategory != null)
                {
                    existingCategory.addChildToOrphanElement(attraction);
                }
                else
                {
                    Element attractionCategoryHeader = attraction.getCategory();
                    attractionCategoryHeader.addChildToOrphanElement(attraction);
                    preparedElements.add(attractionCategoryHeader);

                    //Todo: remove when new way to pass elements around is implemented
                    App.content.addElement(attractionCategoryHeader);
                }
            }
        }
        else if(elements.get(0).isInstance(CountableAttraction.class))
        {
            Log.v(Constants.LOG_TAG, String.format("AttractionCategory.addAttractionCategoryHeaders:: adding headers for #[%d] countable attractions...", elements.size()));
            List<CountableAttraction> countableAttractions = CountableAttraction.convertToCountableAttractions(elements);

            for(CountableAttraction countableAttraction : countableAttractions)
            {
                Element existingCategory = null;
                for(Element attractionCategory : preparedElements)
                {
                    if(attractionCategory.equals(countableAttraction.getAttraction().getCategory()))
                    {
                        existingCategory = attractionCategory;
                    }
                }

                if(existingCategory != null)
                {
                    existingCategory.addChildToOrphanElement(countableAttraction);
                }
                else
                {
                    Element attractionCategoryHeader = countableAttraction.getAttraction().getCategory();
                    attractionCategoryHeader.addChildToOrphanElement(countableAttraction);
                    preparedElements.add(attractionCategoryHeader);

                    //Todo: remove when new way to pass elements around is implemented
                    App.content.addElement(attractionCategoryHeader);
                }
            }
        }

        preparedElements = Element.sortElementsBasedOnComparisonList(preparedElements, new ArrayList<Element>(AttractionCategory.getAttractionCategories()));

        Log.v(Constants.LOG_TAG, String.format("AttractionCategory.addAttractionCategoryHeaders:: #[%d] headers added", preparedElements.size()));
        return preparedElements;
    }

    public static Set<Element> getAttractionCategoriesToExpandAccordingToSettings(List<? extends Element> attractions)
    {
        Set<Element> attractionCategoriesToExpand = new HashSet<>();
        for(Element attraction : attractions)
        {
            AttractionCategory attractionCategory;
            if(attraction.isInstance(Attraction.class) || attraction.isInstance(Coaster.class))
            {
                attractionCategory = ((Attraction)attraction).getCategory();
            }
            else if(attraction.isInstance(CountableAttraction.class))
            {
                attractionCategory = ((CountableAttraction)attraction).getAttraction().getCategory();
            }
            else
            {
                String errorMessage = String.format("type mismatch - %s is not of type <Attraction>, <Coaster> or <CountableAttraction>", attraction);
                Log.e(Constants.LOG_TAG, "AttractionCategory.getAttractionCategoriesToExpandAccordingToSettings:: " + errorMessage);
                throw new IllegalStateException(errorMessage);
            }

            if(App.settings.getAttractionCategoriesToExpandByDefault().contains(attractionCategory))
            {
                attractionCategoriesToExpand.add(attractionCategory);
            }
        }
        return attractionCategoriesToExpand;
    }
}
