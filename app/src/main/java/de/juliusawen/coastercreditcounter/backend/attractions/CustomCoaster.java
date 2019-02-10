package de.juliusawen.coastercreditcounter.backend.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

/**
 * Individual coaster located at a particular park
 *
 * Parent: Park
 * Children: none
 */
public class CustomCoaster extends Coaster implements IOnSiteAttraction, ICategorized
{
    private CustomCoaster(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }

    public static CustomCoaster create(String name, int untrackedRideCount, UUID uuid)
    {
        CustomCoaster customCoaster = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            customCoaster = new CustomCoaster(name, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("CustomCoaster.create:: %s created.", customCoaster.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("CustomCoaster.create:: invalid name[%s] - CustomCoaster not created.", name));
        }
        return customCoaster;
    }
}
