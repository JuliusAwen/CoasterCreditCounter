package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

public class Category extends OrphanElement implements IProperty
{
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
    public boolean isDefault()
    {
        return App.settings.getDefaultCategory().equals(this);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(App.settings.getDefaultCategory()));

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
