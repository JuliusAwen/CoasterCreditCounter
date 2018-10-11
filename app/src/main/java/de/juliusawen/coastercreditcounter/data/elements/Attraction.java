package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class Attraction extends Element
{
    private AttractionCategory category = null;

    Attraction(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Attraction create(String name)
    {
        Attraction attraction = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            attraction = new Attraction(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("Attraction.create:: %s created", attraction.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Attraction.create:: invalid name [%s] - attraction not created", name));
        }
        return attraction;
    }

    public static List<Attraction> convertToAttractions(List<? extends Element> elements)
    {
        List<Attraction> attractions = new ArrayList<>();
        for(Element element : elements)
        {
            if(element.isInstance(Attraction.class) || element.isInstance(Coaster.class))
            {
                attractions.add((Attraction) element);
            }
            else
            {
                String errorMessage = String.format("Attraction.convertToAttractions:: type mismatch - %s is neither of type <Attraction> nor <Coaster>", element);
                Log.e(Constants.LOG_TAG, errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }
        return attractions;
    }


    public static void addCategory(AttractionCategory category)
    {
        if(!AttractionCategory.getAttractionCategories().contains(category))
        {
            AttractionCategory.getAttractionCategories().add(category);
            Log.v(Constants.LOG_TAG,  String.format("Attraction.addCategory:: %s added", category));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Attraction.addCategory:: %s already exists", category));
        }
    }

    public AttractionCategory getCategory()
    {
        return this.category;
    }

    public void setCategory(AttractionCategory category)
    {
        if(AttractionCategory.getAttractionCategories().contains(category))
        {
            this.category = category;
            Log.v(Constants.LOG_TAG,  String.format("Attraction.setCategory:: set %s to %s", category, this));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Attraction.setCategory:: invalid %s", category));
        }
    }

    public static boolean containsAttractionOfCategory(List<Attraction> attractions, AttractionCategory attractionCategory)
    {
        for(Attraction attraction : attractions)
        {
            if(attraction.getCategory().equals(attractionCategory))
            {
                Log.v(Constants.LOG_TAG,  String.format("Attraction.containsAttractionOfCategory:: %s is of %s", attraction, attractionCategory));
                return true;
            }
        }
        Log.v(Constants.LOG_TAG,  String.format("Attraction.containsAttractionOfCategory:: no attraction of %s found", attractionCategory));
        return false;
    }
}
