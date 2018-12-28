package de.juliusawen.coastercreditcounter.backend.objects.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionBlueprint extends Attraction implements IBlueprint
{
    private AttractionBlueprint(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }

    public static AttractionBlueprint create(String name, int untrackedRideCount, UUID uuid)
    {
        AttractionBlueprint attractionBlueprint = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            attractionBlueprint = new AttractionBlueprint(name, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("AttractionBlueprint.create:: %s created.", attractionBlueprint.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("AttractionBlueprint.create:: invalid name[%s] - AttractionBlueprint not created.", name));
        }
        return attractionBlueprint;
    }
}
