package de.juliusawen.coastercreditcounter.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class Attraction extends Element
{
    private static List<AttractionCategory> categories = new ArrayList<>();
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
            Log.v(Constants.LOG_TAG,  String.format("Attraction.create:: %s created.", attraction.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Attraction.create:: invalid name[%s] - attraction not created.", name));
        }
        return attraction;
    }

    public static List<AttractionCategory> getCategories()
    {
        return Attraction.categories;
    }

    public static void setCategories(List<AttractionCategory> categories)
    {
        Attraction.categories = categories;
        Log.d(Constants.LOG_TAG,  String.format("Attraction.setCategories:: #[%d] categories set", categories.size()));
    }

    public static void addCategory(AttractionCategory category)
    {
        if(!Attraction.categories.contains(category))
        {
            Attraction.categories.add(category);
            Log.v(Constants.LOG_TAG,  String.format("Attraction.addCategory:: [%s] added", category));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Attraction.addCategory:: [%s] already exists", category));
        }
    }

    public AttractionCategory getCategory()
    {
        return this.category;
    }

    public void setCategory(AttractionCategory category)
    {
        if(Attraction.categories.contains(category))
        {
            this.category = category;
            Log.v(Constants.LOG_TAG,  String.format("Attraction.setCategory:: set [%s] to %s", category, this));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Attraction.setCategory:: invalid [%s]", category));
        }
    }

    public static List<Attraction> convertToAttractions(List<? extends Element> elements)
    {
        List<Attraction> attractions = new ArrayList<>();
        for(Element element : elements)
        {
            if(element.isInstance(Attraction.class) || element.isInstance(Coaster.class))
            {
                attractions.add(0, (Attraction) element);
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
}
