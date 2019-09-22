package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.OrphanElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attributes.IPersistable;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

public class Manufacturer extends OrphanElement implements IPersistable
{
    private Manufacturer(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Manufacturer create(String name)
    {
        return Manufacturer.create(name, UUID.randomUUID());
    }

    public static Manufacturer create(String name, UUID uuid)
    {
        Manufacturer manufacturer = null;
        if(Element.nameIsValid(name))
        {
            manufacturer = new Manufacturer(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Manufacturer.create:: %s created", manufacturer));
        }
        return manufacturer;
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(App.settings.getDefaultManufacturer()));

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
}
