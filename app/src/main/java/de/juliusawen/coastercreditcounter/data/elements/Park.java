package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
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
        for(Attraction attraction : getChildrenAsType(Attraction.class))
        {
            if(!attractionCategories.contains(attraction.getCategory()))
            {
                attractionCategories.add(attraction.getCategory());
            }
        }
        Log.v(Constants.LOG_TAG,  String.format("Park.getAttractionCategoryCount::#[%d] distinct AttractionCategories found", attractionCategories.size()));
        return attractionCategories.size();
    }
}
