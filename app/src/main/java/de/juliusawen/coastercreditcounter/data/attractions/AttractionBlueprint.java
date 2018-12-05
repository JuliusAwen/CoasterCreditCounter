package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionBlueprint extends Attraction implements IBlueprint
{
    private AttractionBlueprint(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static AttractionBlueprint create(String name, UUID uuid)
    {
        AttractionBlueprint attractionBlueprint = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            attractionBlueprint = new AttractionBlueprint(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("AttractionBlueprint.create:: %s created.", attractionBlueprint.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("AttractionBlueprint.create:: invalid name[%s] - AttractionBlueprint not created.", name));
        }
        return attractionBlueprint;
    }
}
