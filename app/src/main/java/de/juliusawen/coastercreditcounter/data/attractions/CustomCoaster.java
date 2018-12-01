package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class CustomCoaster extends Coaster implements IOnSiteAttraction, ICategorized
{
    private CustomCoaster(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static CustomCoaster create(String name)
    {
        return CustomCoaster.create(name, UUID.randomUUID());
    }

    public static CustomCoaster create(String name, UUID uuid)
    {
        CustomCoaster customCoaster = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            customCoaster = new CustomCoaster(name, uuid);
            Log.v(Constants.LOG_TAG,  String.format("CustomCoaster.create:: %s created.", customCoaster.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("CustomCoaster.create:: invalid name[%s] - customCoaster not created.", name));
        }
        return customCoaster;
    }
}
