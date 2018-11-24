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
        Coaster coaster = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            coaster = new Coaster(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("Coaster.create:: %s created.", coaster.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Coaster.create:: invalid name[%s] - coaster not created.", name));
        }
        return coaster;
    }
}
