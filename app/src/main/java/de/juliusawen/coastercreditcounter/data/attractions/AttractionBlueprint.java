package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionBlueprint extends Attraction implements IBlueprint
{
    protected AttractionBlueprint(String name, UUID uuid)
    {
        super(name, uuid);
    }

    @Override
    public JSONObject toJson()
    {
        try
        {
            JSONObject jsonObjectAttraction = new JSONObject();
            jsonObjectAttraction.put("element", Element.toJson(this, false));
            jsonObjectAttraction.put("attraction category", this.attractionCategory.getUuid());

            Log.v(Constants.LOG_TAG, String.format("AttractionBlueprint.toJson:: created JSON for %s [%s]", this, jsonObjectAttraction.toString()));
            return jsonObjectAttraction;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("AttractionBlueprint.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            return null;
        }
    }
}
