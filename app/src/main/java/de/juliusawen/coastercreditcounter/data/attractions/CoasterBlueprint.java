package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
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

    @Override
    public JSONObject toJson()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("element", Element.toJson(this, false));
            jsonObject.put("attraction category", this.attractionCategory.getUuid());

            Log.v(Constants.LOG_TAG, String.format("CoasterBlueprint.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("CoasterBlueprint.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            return null;
        }
    }
}
