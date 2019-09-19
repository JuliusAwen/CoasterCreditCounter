package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/***
 * Parent: Location
 * Children: CustomCoaster, CustomAttraction, StockAttraction, Visit
 */
public class Park extends Element
{
    private Park(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Park create(String name)
    {
        return Park.create(name, UUID.randomUUID());
    }

    public static Park create(String name, UUID uuid)
    {
        Park park = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            park = new Park(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Park.create:: %s created", park.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Park.create:: invalid name[%s] - park not created", name));
        }
        return park;
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            JsonTool.putChildren(jsonObject, this);

            Log.v(Constants.LOG_TAG, String.format("Park.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Park.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
