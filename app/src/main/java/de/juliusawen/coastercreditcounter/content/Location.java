package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class Location extends Element
{
    private Location(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Location create(String name)
    {
        Location location = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            location = new Location(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("Location.create:: %s created.", location.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Location.create:: invalid name[%s] - location not created.", name));
        }
        return location;
    }
}

