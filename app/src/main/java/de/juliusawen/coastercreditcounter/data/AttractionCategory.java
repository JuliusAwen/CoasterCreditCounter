package de.juliusawen.coastercreditcounter.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategory extends OrphanElement
{
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
        else
        {
            Log.v(Constants.LOG_TAG, String.format("AttractionCategory.addAttractionCategoryHeaders:: adding headers for #[%d] attractions...", elements.size()));
            AttractionCategory.removeAllChildren(Attraction.getCategories());

            List<Attraction> attractions = Attraction.convertToAttractions(elements);
            List<Element> preparedElements = new ArrayList<>();

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
                }
            }

            preparedElements = Element.sortElementsBasedOnComparisonList(preparedElements, new ArrayList<Element>(Attraction.getCategories()));

            Log.v(Constants.LOG_TAG, String.format("AttractionCategory.addAttractionCategoryHeaders:: #[%d] headers added", preparedElements.size()));
            return preparedElements;
        }
    }
}
