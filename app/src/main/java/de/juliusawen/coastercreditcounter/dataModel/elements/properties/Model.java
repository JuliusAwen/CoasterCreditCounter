package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.tools.JsonTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

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
            Log.d(String.format("%s created", model));
        }

        return model;
    }

    @Override
    public String getFullName()
    {
        return String.format("[%s %s - %s %s %s (%s)]",
                this.getClass().getSimpleName(),
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
            Log.w("no default set - creating default");
            Model.setDefault(Model.create((App.getContext().getString(R.string.default_model_name))));
        }

        return Model.defaultModel;
    }

    public static void setDefault(Model defaultModel)
    {
        Model.defaultModel = defaultModel;
        Log.i(String.format("%s set as default", Model.defaultModel));
    }

    public boolean isCreditTypeSet()
    {
        return this.creditType != null;
    }

    public CreditType getCreditType()
    {
        return this.creditType;
    }

    public void setCreditType(CreditType creditType)
    {
        this.creditType = creditType;
        Log.d(String.format("set %s's CreditType to %s - setting children...", this, this.creditType));

        for(IAttraction attraction : this.getChildrenAsType(IAttraction.class))
        {
            attraction.setCreditType(this.creditType);
        }
    }

    public boolean isCategorySet()
    {
        return this.category != null;
    }

    public Category getCategory()
    {
        return this.category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
        Log.d(String.format("set %s's Category to %s - setting children...", this, this.category));

        for(IAttraction attraction : this.getChildrenAsType(IAttraction.class))
        {
            attraction.setCategory(this.category);
        }
    }

    public boolean isManufacturerSet()
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
        Log.d(String.format("set %s's Manufacturer to %s - setting in children...", this, this.manufacturer));

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

            jsonObject.put(Constants.JSON_STRING_CREDIT_TYPE, this.isCreditTypeSet() ? this.getCreditType().getUuid() : JSONObject.NULL);
            jsonObject.put(Constants.JSON_STRING_CATEGORY, this.isCategorySet() ? this.getCategory().getUuid() : JSONObject.NULL);
            jsonObject.put(Constants.JSON_STRING_MANUFACTURER, this.isManufacturerSet() ? this.getManufacturer().getUuid() : JSONObject.NULL);

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
