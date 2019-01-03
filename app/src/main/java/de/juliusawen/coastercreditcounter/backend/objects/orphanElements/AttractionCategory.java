package de.juliusawen.coastercreditcounter.backend.objects.orphanElements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class AttractionCategory extends OrphanElement
{
    private static AttractionCategory defaultAttractionCategory = null;

    private AttractionCategory(String name, UUID uuid)
    {
        super(name, uuid);
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

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.JSON_STRING_ELEMENT, Element.toJson(this, true));
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

    public static void setDefault(AttractionCategory attractionCategory)
    {
        AttractionCategory.defaultAttractionCategory = attractionCategory;
        Log.i(Constants.LOG_TAG, String.format("AttractionCategory.setDefault:: set %s as default attraction category", attractionCategory));
    }

    public static AttractionCategory getDefault()
    {
        if(AttractionCategory.defaultAttractionCategory == null)
        {
            AttractionCategory.createAndSetDefault();
        }

        return AttractionCategory.defaultAttractionCategory;
    }

    public static void createAndSetDefault()
    {
        AttractionCategory.setDefault(new AttractionCategory(App.getContext().getString(R.string.name_default_attraction_category), UUID.randomUUID()));
    }
}