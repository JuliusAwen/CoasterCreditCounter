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
 * Parent: none
 * Children: Attractions (just for convenience: children are shown in ManageProperty - but are not really used otherwise)
 */
public final class Manufacturer extends Element implements IProperty
{
    private static Manufacturer defaultManufacturer;

    private Manufacturer(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Manufacturer create(String name)
    {
        return Manufacturer.create(name, null);
    }

    public static Manufacturer create(String name, UUID uuid)
    {
        Manufacturer manufacturer = null;
        if(Element.isNameValid(name))
        {
            manufacturer = new Manufacturer(name, uuid);
            Log.v(Constants.LOG_TAG,  String.format("Manufacturer.create:: %s created", manufacturer.getFullName()));
        }

        return manufacturer;
    }

    public static Manufacturer getDefault()
    {
        if(Manufacturer.defaultManufacturer == null)
        {
            Log.w(Constants.LOG_TAG, "Manufacturer.getDefault:: no default set - creating default");
            Manufacturer.setDefault(Manufacturer.create((App.getContext().getString(R.string.default_manufacturer_name))));
        }

        return Manufacturer.defaultManufacturer;
    }

    public static void setDefault(Manufacturer defaultManufacturer)
    {
        Manufacturer.defaultManufacturer = defaultManufacturer;
        Log.i(Constants.LOG_TAG, String.format("Manufacturer.setDefault:: %s set as default", Manufacturer.defaultManufacturer));
    }

    @Override
    public boolean isDefault()
    {
        return Manufacturer.getDefault().equals(this);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_IS_DEFAULT, this.equals(Manufacturer.getDefault()));

            Log.v(Constants.LOG_TAG, String.format("Manufacturer.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Manufacturer.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
