package de.juliusawen.coastercreditcounter.backend.orphanElements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.JsonTool;

public class Manufacturer extends OrphanElement implements IOrphanElement
{
    private static Manufacturer defaultManufacturer;

    private Manufacturer(String name, UUID uuid)
    {
        super(name, uuid);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(Manufacturer.getDefault()));

            Log.v(Constants.LOG_TAG, String.format("Manufacturer.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Manufacturer.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
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
