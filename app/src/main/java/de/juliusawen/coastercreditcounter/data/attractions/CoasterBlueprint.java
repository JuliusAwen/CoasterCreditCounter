package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class CoasterBlueprint extends Coaster implements IBlueprint
{
    private CoasterBlueprint(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static CoasterBlueprint create(String name, UUID uuid)
    {
        CoasterBlueprint coasterBlueprint = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            coasterBlueprint = new CoasterBlueprint(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("CustomCoaster.create:: %s created.", coasterBlueprint.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("CustomCoaster.create:: invalid name[%s] - coasterBlueprint not created.", name));
        }
        return coasterBlueprint;
    }
}
