package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class Park extends Element
{
    private Park(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Park createPark(String name)
    {
        Park park = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.v(Constants.LOG_TAG,  String.format("Park.createPark:: park[%s] created.", name));
            park = new Park(name, UUID.randomUUID());
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Park.createPark:: invalid name[%s] - park not created.", name));
        }

        return park;
    }
}
