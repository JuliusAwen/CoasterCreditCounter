package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.orphanElements.IOrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Blueprint for coasters from which StockAttraction is created
 */
public class CoasterBlueprint extends Coaster implements IBlueprint, IOrphanElement
{
    private CoasterBlueprint(String name, UUID uuid)
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

            Log.v(Constants.LOG_TAG, String.format("CoasterBlueprint.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("CoasterBlueprint.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }

    public static CoasterBlueprint create(String name, UUID uuid)
    {
        CoasterBlueprint coasterBlueprint = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            coasterBlueprint = new CoasterBlueprint(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("CoasterBlueprint.create:: %s created.", coasterBlueprint.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("CoasterBlueprint.create:: invalid name[%s] - CoasterBlueprint not created.", name));
        }
        return coasterBlueprint;
    }
}
