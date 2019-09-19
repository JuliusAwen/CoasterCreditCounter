package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

/**
 * Individual attraction located at a particular park
 *
 * Parent: Park
 * Children: none
 */
public class CustomAttraction extends Attraction implements IOnSiteAttraction, ICategorized
{
    private CustomAttraction(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }

    public static CustomAttraction create(String name, int untrackedRideCount, UUID uuid)
    {
        CustomAttraction customAttraction = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            customAttraction = new CustomAttraction(name, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("CustomAttraction.create:: %s created", customAttraction.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("CustomAttraction.create:: invalid name [%s] - CustomAttraction not created", name));
        }
        return customAttraction;
    }
}
