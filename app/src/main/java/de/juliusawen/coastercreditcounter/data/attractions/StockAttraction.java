package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class StockAttraction extends Attraction implements IOnSiteAttraction
{
    private IBlueprint blueprint;

    private StockAttraction(String name, IBlueprint blueprint, UUID uuid)
    {
        super(name, uuid);
        this.blueprint = blueprint;
    }

    @Override
    public JSONObject toJson()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("element", Element.toJson(this, false));
            jsonObject.put("blueprint", this.blueprint.getUuid());
            jsonObject.put("total ride count", this.getTotalRideCount());

            Log.v(Constants.LOG_TAG, String.format("StockAttraction.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("StockAttraction.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            return null;
        }
    }

    public static StockAttraction create(String name, IBlueprint blueprint, UUID uuid)
    {
        StockAttraction stockAttraction = null;
        name = name.trim();

        if(!name.isEmpty())
        {
            stockAttraction = new StockAttraction(name, blueprint, uuid == null ? UUID.randomUUID() : uuid);
            Log.v(Constants.LOG_TAG,  String.format("StockAttraction.create:: %s created.", stockAttraction.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("StockAttraction.create:: invalid name[%s] - StockAttraction not created.", name));
        }
        return stockAttraction;
    }

    @Override
    public AttractionCategory getAttractionCategory()
    {
        return this.blueprint.getAttractionCategory();
    }
}
