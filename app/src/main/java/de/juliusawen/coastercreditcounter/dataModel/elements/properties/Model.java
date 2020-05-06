package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import android.util.Log;

import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;

public class Model extends Element implements IProperty
{
    private static Model defaultModel;

    private CreditType creditType;
    private Category category;
    private Manufacturer manufacturer;

    private Model(String name, CreditType creditType, Category category, Manufacturer manufacturer, UUID uuid)
    {
        super(name, uuid);
        this.creditType = creditType;
        this.category = category;
        this.manufacturer = manufacturer;
    }

    public static Model create(String name, CreditType creditType, Category category, Manufacturer manufacturer)
    {
        return create(name, creditType, category, manufacturer, null);
    }

    public static Model create(String name, CreditType creditType, Category category, Manufacturer manufacturer, UUID uuid)
    {
        Model model = null;
        if(Element.isNameValid(name))
        {
            model = new Model(name, creditType, category, manufacturer, uuid);
            Log.v(Constants.LOG_TAG,  String.format("Model.create:: %s created", manufacturer));
        }
        return model;
    }

    @Override
    public String getFullName()
    {
        return String.format(Locale.getDefault(), "[%s CreditType=%s - Category=%s - Manufacturer=%s (%s)]",
                this.toString(),
                this.getCreditType(),
                this.getCategory(),
                this.getManufacturer(),
                this.getUuid());
    }

    @Override
    public boolean isDefault()
    {
        return false;
    }

    public static Model getDefault()
    {
        if(Model.defaultModel == null)
        {
            Log.w(Constants.LOG_TAG, "Model.getDefault:: no default set - creating default");
            Model.setDefault(Model.create((App.getContext().getString(R.string.default_model_name)), null, null, null));
        }

        return Model.defaultModel;
    }

    public static void setDefault(Model defaultModel)
    {
        Model.defaultModel = defaultModel;
        Log.i(Constants.LOG_TAG, String.format("Model.setDefault:: %s set as default", Model.defaultModel));
    }

    public CreditType getCreditType()
    {
        return this.creditType != null ? this.creditType : CreditType.getDefault();
    }

    public void setCreditType(CreditType creditType)
    {
        this.creditType = creditType;
    }

    public Category getCategory()
    {
        return this.category != null ? this.category : Category.getDefault();
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    public Manufacturer getManufacturer()
    {
        return this.manufacturer != null ? this.manufacturer : Manufacturer.getDefault();
    }

    public void setManufacturer(Manufacturer manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    @Override
    public JSONObject toJson() //throws JSONException
    {
        return null;
    }
}
