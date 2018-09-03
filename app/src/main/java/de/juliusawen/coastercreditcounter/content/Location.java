package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class Location extends Element
{
    private Location(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Location createLocation(String name)
    {
        Location location = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.v(Constants.LOG_TAG,  String.format("Location.createLocation:: Location[%s] created.", name));
            location = new Location(name, UUID.randomUUID());
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Location.createLocation:: invalid name[%s] - location not created.", name));
        }

        return location;
    }
}

