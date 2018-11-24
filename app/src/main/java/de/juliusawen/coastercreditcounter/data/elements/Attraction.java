package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class Attraction extends Element
{
    private AttractionCategory attractionCategory = null;

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

    public AttractionCategory getAttrationCategory()
    {
        return this.attractionCategory;
    }

    public void setAttractionCategory(AttractionCategory attractionCategory)
    {
        if(this.attractionCategory != null)
        {
            this.attractionCategory.deleteChild(this);
        }

        attractionCategory.addChildToOrphanElement(this);
        this.attractionCategory = attractionCategory;

        Log.v(Constants.LOG_TAG,  String.format("Attraction.setAttractionCategory:: set %s to %s", attractionCategory, this));
    }
}
