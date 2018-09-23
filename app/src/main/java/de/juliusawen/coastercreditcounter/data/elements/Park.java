package de.juliusawen.coastercreditcounter.data.elements;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class Park extends Element
{
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

    public int getAttractionCategoryCount()
    {
        List<AttractionCategory> attractionCategories = new ArrayList<>();
        for(Attraction attraction : Attraction.convertToAttractions(getChildrenOfType(Attraction.class)))
        {
            if(!attractionCategories.contains(attraction.getCategory()))
            {
                attractionCategories.add(attraction.getCategory());
            }
        }
        Log.v(Constants.LOG_TAG,  String.format("Park.getAttractionCategoryCount::#[%d] different AttractionCategories found", attractionCategories.size()));
        return attractionCategories.size();
    }
}
