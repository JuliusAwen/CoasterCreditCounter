package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class Attraction extends Element
{
    Attraction(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Attraction createAttraction(String name)
    {
        Attraction attraction = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.v(Constants.LOG_TAG,  String.format("Attraction.createAttraction:: attraction[%s] created.", name));
            attraction = new Attraction(name, UUID.randomUUID());
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Attraction.createAttraction:: invalid name[%s] - attraction not created.", name));
        }

        return attraction;
    }
}
