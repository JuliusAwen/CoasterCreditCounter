package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;

public class Coaster extends Attraction
{
    public Coaster(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Coaster createCoaster(String name)
    {
        Coaster coaster = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.v(Constants.LOG_TAG,  String.format("Coaster.createCoaster:: coaster[%s] created.", name));
            coaster = new Coaster(name, UUID.randomUUID());
        }

        return coaster;
    }
}
