package de.juliusawen.coastercreditcounter.backend.objects.orphanElements;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class Manufacturer extends OrphanElement implements IOrphanElement
{
    private static Manufacturer defaultManufacturer;

    private Manufacturer(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Manufacturer create(String name, UUID uuid)
    {
        Manufacturer manufacturer = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            manufacturer = new Manufacturer(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Manufacturer.create:: %s created", manufacturer));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Manufacturer.create:: invalid name[%s] - manufacturer not created", name));
        }
        return manufacturer;
    }

    public static void setDefault(Manufacturer manufacturer)
    {
        Manufacturer.defaultManufacturer = manufacturer;
        Log.i(Constants.LOG_TAG, String.format("Manufacturer.setDefault:: set %s as default manufacturer", manufacturer));
    }

    public static Manufacturer getDefault()
    {
        return Manufacturer.defaultManufacturer;
    }

    public static void createAndSetDefault()
    {
        Manufacturer.setDefault(new Manufacturer(App.getContext().getString(R.string.name_default_manufacturer), UUID.randomUUID()));
    }
}
