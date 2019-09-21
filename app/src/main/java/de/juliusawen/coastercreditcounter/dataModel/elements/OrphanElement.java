package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 *
 */
public abstract class OrphanElement extends Element implements IOrphan
{
    public OrphanElement(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            JsonTool.putChildren(jsonObject, this);

            Log.v(Constants.LOG_TAG, String.format("OrphanElement.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("OrphanElement.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
