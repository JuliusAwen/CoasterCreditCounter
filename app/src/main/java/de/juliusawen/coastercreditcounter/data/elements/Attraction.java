package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

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

    public AttractionCategory getCategory()
    {
        return this.category;
    }

    public void setCategory(AttractionCategory category)
    {
        this.category = category;
        Log.v(Constants.LOG_TAG,  String.format("Attraction.setCategory:: set %s to %s", category, this));
    }
}
