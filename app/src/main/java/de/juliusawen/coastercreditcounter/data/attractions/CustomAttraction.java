package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class CustomAttraction extends Attraction implements IOnSiteAttraction, ICategorized
{
    private CustomAttraction(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static CustomAttraction create(String name)
    {
        CustomAttraction customAttraction = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            customAttraction = new CustomAttraction(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("CustomAttraction.create:: %s created", customAttraction.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("CustomAttraction.create:: invalid name [%s] - CustomAttraction not created", name));
        }
        return customAttraction;
    }
}
