package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;

public class Category extends OrphanElement
{
    private static Category defaultCategory;

    private Category(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Category create(String name)
    {
        return Category.create(name, UUID.randomUUID());
    }

    public static Category create(String name, UUID uuid)
    {
        Category category = null;
        if(StringTool.nameIsValid(name))
        {
            category = new Category(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Category.create:: %s created.", category));
        }
        return category;
    }

    public static void setDefault(Category category)
    {
        Category.defaultCategory = category;
        Log.d(Constants.LOG_TAG, String.format("Category.setDefault:: set %s as default category", category));
    }

    public static Category getDefault()
    {
        if(Category.defaultCategory == null)
        {
            Category.createAndSetDefault();
        }
        return Category.defaultCategory;
    }

    public static void createAndSetDefault()
    {
        Category.setDefault(new Category(App.getContext().getString(R.string.name_default_category), UUID.randomUUID()));
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(Category.getDefault()));

            Log.v(Constants.LOG_TAG, String.format("Category.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Category.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
