package de.juliusawen.coastercreditcounter.backend.objects.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.JsonTool;

public class Location extends Element
{
    private Location(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Location create(String name, UUID uuid)
    {
        Location location = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            location = new Location(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Location.create:: %s created.", location.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Location.create:: invalid name[%s] - location not created.", name));
        }
        return location;
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            JsonTool.putChildren(jsonObject, this);

            Log.v(Constants.LOG_TAG, String.format("Location.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Location.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }

    public Location getRootLocation()
    {
        if(!this.isRootLocation())
        {
            Log.v(Constants.LOG_TAG,  String.format("Element.getRootLocation:: %s is not root location - calling parent", this));
            return ((Location)super.getParent()).getRootLocation();
        }
        else
        {
            return this;
        }
    }

    public boolean isRootLocation()
    {
        return this.getParent() == null;
    }
}

