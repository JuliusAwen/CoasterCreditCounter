package de.juliusawen.coastercreditcounter.data.elements.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class Coaster extends StockAttraction
{
    private Coaster(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Coaster create(String name)
    {
        return Coaster.create(name, UUID.randomUUID());
    }

    public static Coaster create(String name, UUID uuid)
    {
        Coaster coaster = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            coaster = new Coaster(name, uuid);
            Log.v(Constants.LOG_TAG,  String.format("Coaster.create:: %s created.", coaster.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Coaster.create:: invalid name[%s] - coaster not created.", name));
        }
        return coaster;
    }
}
