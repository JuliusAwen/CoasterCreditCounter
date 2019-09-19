package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.orphanElements.IOrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Blueprint for attractions from which StockAttraction is created
 */
public class AttractionBlueprint extends Attraction implements IBlueprint, IOrphanElement
{
    private AttractionBlueprint(String name, UUID uuid)
    {
        super(name, 0, uuid);
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

    public static AttractionBlueprint create(String name, UUID uuid)
    {
        AttractionBlueprint attractionBlueprint = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            attractionBlueprint = new AttractionBlueprint(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("AttractionBlueprint.create:: %s created.", attractionBlueprint.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("AttractionBlueprint.create:: invalid name[%s] - AttractionBlueprint not created.", name));
        }
        return attractionBlueprint;
    }
}
