package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 *      Parent: none<br>
 *      Children: Attractions<br>
 */
public final class Model extends Element implements IProperty, IHasCreditType, IHasCategory, IHasManufacturer
{
    private static Model defaultModel;

    private CreditType creditType;
    private Category category;
    private Manufacturer manufacturer;

    private Model(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Model create(String name)
    {
        return create(name, null);
    }

    public static Model create(String name, UUID uuid)
    {
        Model model = null;
        if(Element.isNameValid(name))
        {
            model = new Model(name, uuid);
            Log.v(Constants.LOG_TAG,  String.format("Model.create:: %s created", model));
        }

        return model;
    }

    @Override
    public String toString()
    {
        return String.format("[%s - %s %s %s]",
                this.getName(),
                this.getCreditType() != null ? this.getCreditType() : "[no CreditType]",
                this.getCategory() != null ? this.getCategory() : "[no Category]",
                this.getManufacturer() != null ? this.getManufacturer() : "[no Manufacturer]");
    }

    @Override
    public String getFullName()
    {
        return String.format("[%s - %s %s %s (%s)]",
                this.getName(),
                this.getCreditType() != null ? this.getCreditType() : "[no CreditType]",
                this.getCategory() != null ? this.getCategory() : "[no Category]",
                this.getManufacturer() != null ? this.getManufacturer() : "[no Manufacturer]",
                this.getUuid());
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
            Model.setDefault(Model.create((App.getContext().getString(R.string.default_model_name))));
        }

        return Model.defaultModel;
    }

    public static void setDefault(Model defaultModel)
    {
        Model.defaultModel = defaultModel;
        Log.i(Constants.LOG_TAG, String.format("Model.setDefault:: %s set as default", Model.defaultModel.getFullName()));
    }

    public boolean creditTypeIsSet()
    {
        return this.getCreditType() != null;
    }

    public CreditType getCreditType()
    {
        return this.creditType;
    }

    public void setCreditType(CreditType creditType)
    {
        this.creditType = creditType;
        Log.d(Constants.LOG_TAG,  String.format("Model.setCreditType:: set %s's CreditType to %s - setting children...", this, this.creditType));

        for(IAttraction attraction : this.getChildrenAsType(IAttraction.class))
        {
            attraction.setCreditType(this.creditType);
        }
    }

    public boolean categoryIsSet()
    {
        return this.getCategory() != null;
    }

    public Category getCategory()
    {
        return this.category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
        Log.d(Constants.LOG_TAG,  String.format("Model.setCategory:: set %s's Category to %s - setting children...", this, this.category));

        for(IAttraction attraction : this.getChildrenAsType(IAttraction.class))
        {
            attraction.setCategory(this.category);
        }
    }

    public boolean manufacturerIsSet()
    {
        return this.manufacturer != null;
    }

    public Manufacturer getManufacturer()
    {
        return this.manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer)
    {
        this.manufacturer = manufacturer;
        Log.d(Constants.LOG_TAG,  String.format("Model.setManufacturer:: set %s's Manufacturer to %s - setting in children...", this, this.manufacturer));

        for(IAttraction attraction : this.getChildrenAsType(IAttraction.class))
        {
            attraction.setManufacturer(this.manufacturer);
        }
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);

            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.isDefault());

            jsonObject.put(Constants.JSON_STRING_CREDIT_TYPE, this.creditTypeIsSet() ? this.getCreditType().getUuid() : JSONObject.NULL);
            jsonObject.put(Constants.JSON_STRING_CATEGORY, this.categoryIsSet() ? this.getCategory().getUuid() : JSONObject.NULL);
            jsonObject.put(Constants.JSON_STRING_MANUFACTURER, this.manufacturerIsSet() ? this.getManufacturer().getUuid() : JSONObject.NULL);

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
