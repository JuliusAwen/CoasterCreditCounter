package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategoryProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditTypeProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturerProperty;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Blueprint for attractions from which StockAttraction is created
 *
 *  * Parent: none
 *  * Children: none
 */
public class Blueprint extends Attraction implements IHasCreditTypeProperty, IHasCategoryProperty, IHasManufacturerProperty
{
    private Blueprint(String name, UUID uuid)
    {
        super(name, 0, uuid);
    }

    public static Blueprint create(String name)
    {
        return Blueprint.create(name, UUID.randomUUID());
    }

    public static Blueprint create(String name, UUID uuid)
    {
        Blueprint blueprint = null;
        if(Element.nameIsValid(name))
        {
            blueprint = new Blueprint(name, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("Blueprint.create:: %s created.", blueprint.getFullName()));
        }
        return blueprint;
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

            Log.v(Constants.LOG_TAG, String.format("Blueprint.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Blueprint.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}
