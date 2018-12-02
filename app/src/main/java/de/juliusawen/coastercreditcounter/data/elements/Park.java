package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import androidx.annotation.NonNull;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class Park extends Element
{
    private Park(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Park create(@NonNull String name)
    {
        Park park = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            park = new Park(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("Park.create:: %s created", park.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Park.create:: invalid name[%s] - park not created", name));
        }
        return park;
    }

    public JSONObject toJson()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("element", Element.toJson(this, true));

            Log.v(Constants.LOG_TAG, String.format("Park.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Park.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            return null;
        }
    }
}
