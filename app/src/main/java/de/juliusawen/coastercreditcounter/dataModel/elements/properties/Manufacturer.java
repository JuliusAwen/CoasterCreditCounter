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
            Log.d(String.format("%s created", manufacturer.getFullName()));
        }

        return manufacturer;
    }

    @Override
    public boolean isDefault()
    {
        return Manufacturer.getDefault().equals(this);
    }

    public static Manufacturer getDefault()
    {
        if(Manufacturer.defaultManufacturer == null)
        {
            Log.w("no default set - creating default");
            Manufacturer.setDefault(Manufacturer.create((App.getContext().getString(R.string.default_manufacturer_name))));
        }

        return Manufacturer.defaultManufacturer;
    }

    public static void setDefault(Manufacturer defaultManufacturer)
    {
        Manufacturer.defaultManufacturer = defaultManufacturer;
        Log.i(String.format("%s set as default", Manufacturer.defaultManufacturer));
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
