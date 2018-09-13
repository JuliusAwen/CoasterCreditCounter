package de.juliusawen.coastercreditcounter.content;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class Park extends Element
{
    private static List<String> types = new ArrayList<>();
    private String type;

    private Park(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Park create(@NonNull String name)
    {
        Park park = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            park = new Park(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("Park.create:: %s created", park.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Park.create:: invalid name[%s] - park not created", name));
        }
        return park;
    }

    public static List<String> getTypes()
    {
        return Park.types;
    }

    public static void setTypes(List<String> types)
    {
        Park.types = types;
        Log.v(Constants.LOG_TAG,  String.format("Park.setTypes:: #[%d] types set", types.size()));
    }

    public static void addType(String type)
    {
        if(!Park.types.contains(type))
        {
            Park.types.add(type);
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Park.addType:: type [%s] already exists", type));
        }
    }

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        if(Park.types.contains(type))
        {
            this.type = type;
            Log.v(Constants.LOG_TAG,  String.format("Park.setType:: set type [%s] to %s", type, this));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Park.setType:: invalid type [%s]", type));
        }
    }

    public int getAttractionCategoryCount()
    {
        List<AttractionCategory> attractionCategories = new ArrayList<>();
        for(Attraction attraction : Attraction.convertToAttractions(getChildrenOfInstance(Attraction.class)))
        {
            if(!attractionCategories.contains(attraction.getCategory()))
            {
                attractionCategories.add(attraction.getCategory());
            }
        }
        Log.v(Constants.LOG_TAG,  String.format("Park.getAttractionCategoryCount:: #[%d] different AttractionCategories found", attractionCategories.size()));
        return attractionCategories.size();
    }

    public static List<Element> addAttractionCategoryHeaders(List<? extends Element> elements)
    {
        if(elements.isEmpty())
        {
            Log.v(Constants.LOG_TAG, "Park.addAttractionCategoryHeaders:: no attractions found");
            return new ArrayList<>(elements);
        }
        else
        {

            Log.v(Constants.LOG_TAG, String.format("Park.addAttractionCategoryHeaders:: adding headers for #[%d] attractions...", elements.size()));
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

            Log.v(Constants.LOG_TAG, String.format("Park.addAttractionCategoryHeaders:: #[%d] headers added", preparedElements.size()));
            return preparedElements;
        }
    }
}
