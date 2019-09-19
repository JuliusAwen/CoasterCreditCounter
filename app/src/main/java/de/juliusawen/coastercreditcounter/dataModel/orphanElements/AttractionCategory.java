package de.juliusawen.coastercreditcounter.dataModel.orphanElements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

public class AttractionCategory extends OrphanElement
{
    private static AttractionCategory defaultAttractionCategory;

    private AttractionCategory(String name, UUID uuid)
    {
        super(name, uuid);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(AttractionCategory.getDefault()));

            Log.v(Constants.LOG_TAG, String.format("AttractionCategory.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("AttractionCategory.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }

    public static AttractionCategory create(String name, UUID uuid)
    {
        AttractionCategory attractionCategory = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            attractionCategory = new AttractionCategory(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("AttractionCategory.create:: %s created.", attractionCategory));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("AttractionCategory.create:: invalid name[%s] - attractionCategory not created.", name));
        }
        return attractionCategory;
    }

    public static void setDefault(AttractionCategory attractionCategory)
    {
        AttractionCategory.defaultAttractionCategory = attractionCategory;
        Log.d(Constants.LOG_TAG, String.format("AttractionCategory.setDefault:: set %s as default attraction category", attractionCategory));
    }

    public static AttractionCategory getDefault()
    {
        return AttractionCategory.defaultAttractionCategory;
    }

    public static void createAndSetDefault()
    {
        AttractionCategory.setDefault(new AttractionCategory(App.getContext().getString(R.string.name_default_attraction_category), UUID.randomUUID()));
    }
}
