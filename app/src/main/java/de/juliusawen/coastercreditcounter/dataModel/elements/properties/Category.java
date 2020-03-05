package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

public final class Category extends Element implements IProperty
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
        if(Element.nameIsValid(name))
        {
            category = new Category(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Category.create:: %s created.", category));
        }
        return category;
    }

    public static Category getDefault()
    {
        if(Category.defaultCategory == null)
        {
            Log.w(Constants.LOG_TAG, "Category.getDefault:: no default set - creating default");
            Category.setDefault(Category.create((App.getContext().getString(R.string.default_category_name))));
        }
        return Category.defaultCategory;
    }

    public static void setDefault(Category defaultCategory)
    {
        Category.defaultCategory = defaultCategory;
        Log.i(Constants.LOG_TAG, String.format("Category.setDefault:: %s set as default", defaultCategory));
    }

    public boolean isDefault()
    {
        return Category.getDefault().equals(this);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.isDefault());

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
