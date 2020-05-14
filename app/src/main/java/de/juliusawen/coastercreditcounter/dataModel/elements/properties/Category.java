package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

/**
 *      Parent: none<br>
 *      Children: Attractions<br>
 */
public final class Category extends Element implements IProperty
{
    private static Category defaultCategory;

    private Category(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Category create(String name)
    {
        return Category.create(name, null);
    }

    public static Category create(String name, UUID uuid)
    {
        Category category = null;
        if(Element.isNameValid(name))
        {
            category = new Category(name, uuid);
            Log.d(String.format("%s created", category));
        }

        return category;
    }

    @Override
    public boolean isDefault()
    {
        return Category.getDefault().equals(this);
    }

    public static Category getDefault()
    {
        if(Category.defaultCategory == null)
        {
            Log.w("no default set - creating default");
            Category.setDefault(Category.create((App.getContext().getString(R.string.default_category_name))));
        }

        return Category.defaultCategory;
    }

    public static void setDefault(Category defaultCategory)
    {
        Category.defaultCategory = defaultCategory;
        Log.i(String.format("%s set as default", Category.defaultCategory));
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.isDefault());

            Log.v(String.format("created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(String.format("creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
