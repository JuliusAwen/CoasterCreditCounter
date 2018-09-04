package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class Coaster extends Attraction
{
    private Coaster(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Coaster createCoaster(String name)
    {
        Coaster coaster = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            coaster = new Coaster(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("Coaster.createCoaster:: %s created.", coaster.getFullName()));
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Coaster.createCoaster:: invalid name[%s] - coaster not created.", name));
        }

        return coaster;
    }
}
