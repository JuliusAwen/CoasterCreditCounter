package de.juliusawen.coastercreditcounter.backend.objects.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.IOrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.JsonTool;

public class AttractionBlueprint extends Attraction implements IBlueprint, IOrphanElement
{
    private AttractionBlueprint(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_MANUFACTURER, this.getManufacturer().getUuid());
            jsonObject.put(Constants.JSON_STRING_ATTRACTION_CATEGORY, this.getAttractionCategory().getUuid());

            Log.v(Constants.LOG_TAG, String.format("AttractionBlueprint.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("AttractionBlueprint.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }

    public static AttractionBlueprint create(String name, int untrackedRideCount, UUID uuid)
    {
        AttractionBlueprint attractionBlueprint = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            attractionBlueprint = new AttractionBlueprint(name, untrackedRideCount, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("AttractionBlueprint.create:: %s created.", attractionBlueprint.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("AttractionBlueprint.create:: invalid name[%s] - AttractionBlueprint not created.", name));
        }
        return attractionBlueprint;
    }
}
