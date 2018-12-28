package de.juliusawen.coastercreditcounter.backend.objects.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class CoasterBlueprint extends Coaster implements IBlueprint
{
    private CoasterBlueprint(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }

    public static CoasterBlueprint create(String name, int untrackedRideCount, UUID uuid)
    {
        CoasterBlueprint coasterBlueprint = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            coasterBlueprint = new CoasterBlueprint(name, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("CoasterBlueprint.create:: %s created.", coasterBlueprint.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("CoasterBlueprint.create:: invalid name[%s] - CoasterBlueprint not created.", name));
        }
        return coasterBlueprint;
    }
}
