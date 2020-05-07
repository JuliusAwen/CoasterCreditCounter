package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.traits.IOrphan;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 *      Parent: none<br>
 *      Children: Attractions<br>
 */
public class Model extends Element implements IElement, IOrphan, IPersistable
{
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
    public String getFullName()
    {
        return String.format(Locale.getDefault(), "[%s - properties: %s %s %s (%s)]",
                this.toString(),
                this.getCreditType(),
                this.getCategory(),
                this.getManufacturer(),
                this.getUuid());
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
            jsonObject.put(Constants.JSON_STRING_CREDIT_TYPE, this.getCreditType().getUuid());
            jsonObject.put(Constants.JSON_STRING_CATEGORY, this.getCategory().getUuid());
            jsonObject.put(Constants.JSON_STRING_MANUFACTURER, this.getManufacturer().getUuid());

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
