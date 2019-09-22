package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/***
 * Parent: Location
 * Children: CustomAttraction, StockAttraction, Visit
 */
public class Park extends Element implements IPersistable
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
        if(Element.nameIsValid(name))
        {
            park = new Park(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Park.create:: %s created", park.getFullName()));
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
