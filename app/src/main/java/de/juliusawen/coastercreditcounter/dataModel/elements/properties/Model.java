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

/**
 *      Parent: none<br>
 *      Children: Attractions<br>
 */
public final class Model extends Element implements IProperty
{
    private static Model defaultModel;

    private boolean overrideProperties = true;

    private CreditType creditType;
    private Category category;
    private Manufacturer manufacturer;

    private Model(String name, boolean overrideProperties, UUID uuid)
    {
        super(name, uuid);
        this.setOverrideProperties(overrideProperties);
    }

    public static Model create(String name, boolean overrideProperties)
    {
        return create(name, overrideProperties, null);
    }

    public static Model create(String name, boolean overrideProperties, UUID uuid)
    {
        Model model = null;
        if(Element.isNameValid(name))
        {
            model = new Model(name, overrideProperties, uuid);
            Log.v(Constants.LOG_TAG,  String.format("Model.create:: %s created", model));
        }

        return model;
    }

    @Override
    public String getFullName()
    {
        String base = String.format("%s (%s) overrideProperties[%S]", this.toString(), this.getUuid(), this.overrideProperties());
        String properties = String.format("%s %s %s", this.getCreditType(), this.getCategory(), this.getManufacturer());
        return this.overrideProperties() ? String.format("[%s - %s]", base, properties) : String.format("[%s]", base);
    }

    @Override
    public boolean isDefault()
    {
        return Model.getDefault().equals(this);
    }

    public static Model getDefault()
    {
        if(Model.defaultModel == null)
        {
            Log.w(Constants.LOG_TAG, "Model.getDefault:: no default set - creating default");
            Model.setDefault(Model.create((App.getContext().getString(R.string.default_model_name)), false));
        }

        return Model.defaultModel;
    }

    public static void setDefault(Model defaultModel)
    {
        Model.defaultModel = defaultModel;
        Log.i(Constants.LOG_TAG, String.format("Model.setDefault:: %s set as default", Model.defaultModel.getFullName()));
    }

    public boolean overrideProperties()
    {
        return this.overrideProperties;
    }

    public void setOverrideProperties(boolean overrideProperties)
    {
        this.overrideProperties = overrideProperties;
        Log.d(Constants.LOG_TAG, String.format("Model.setOverrideProperties:: set %s overrideProperties [%S] ", this, overrideProperties));
    }

    public CreditType getCreditType()
    {
        return this.creditType != null ? this.creditType : CreditType.getDefault();
    }

    public void setCreditType(CreditType creditType)
    {
        this.creditType = creditType;
        Log.d(Constants.LOG_TAG,  String.format("Model.setCreditType:: set %s's CreditType to %s", this, this.creditType));
    }

    public Category getCategory()
    {
        return this.category != null ? this.category : Category.getDefault();
    }

    public void setCategory(Category category)
    {
        this.category = category;
        Log.d(Constants.LOG_TAG,  String.format("Model.setCategory:: set %s's Category to %s", this, this.category));
    }

    public Manufacturer getManufacturer()
    {
        return this.manufacturer != null ? this.manufacturer : Manufacturer.getDefault();
    }

    public void setManufacturer(Manufacturer manufacturer)
    {
        this.manufacturer = manufacturer;
        Log.d(Constants.LOG_TAG,  String.format("Model.setManufacturer:: set %s's Manufacturer to %s", this, this.manufacturer));
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);

            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.isDefault());
            jsonObject.put(Constants.JSON_STRING_OVERRIDE_PROPERTIES, this.overrideProperties);

            if(this.overrideProperties())
            {
                jsonObject.put(Constants.JSON_STRING_CREDIT_TYPE, this.getCreditType().getUuid());
                jsonObject.put(Constants.JSON_STRING_CATEGORY, this.getCategory().getUuid());
                jsonObject.put(Constants.JSON_STRING_MANUFACTURER, this.getManufacturer().getUuid());
            }
            else
            {
                jsonObject.put(Constants.JSON_STRING_CREDIT_TYPE, JSONObject.NULL);
                jsonObject.put(Constants.JSON_STRING_CATEGORY, JSONObject.NULL);
                jsonObject.put(Constants.JSON_STRING_MANUFACTURER, JSONObject.NULL);
            }

            Log.v(Constants.LOG_TAG, String.format("Model.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Model.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
