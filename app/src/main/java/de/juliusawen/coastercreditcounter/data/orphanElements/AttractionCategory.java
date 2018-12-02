package de.juliusawen.coastercreditcounter.data.orphanElements;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategory extends OrphanElement
{
    private static AttractionCategory defaultAttractionCategory = null;

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

    public static void setDefaultAttractionCategory(AttractionCategory attractionCategory)
    {
        AttractionCategory.defaultAttractionCategory = attractionCategory;
    }

    public static AttractionCategory getDefaultAttractionCategory()
    {
        return AttractionCategory.defaultAttractionCategory;
    }
}
