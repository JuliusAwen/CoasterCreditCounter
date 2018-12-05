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
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.JSON_STRING_ELEMENT, Element.toJson(this, false));
            jsonObject.put(Constants.JSON_STRING_BLUEPRINT, this.blueprint.getUuid());
            jsonObject.put(Constants.JSON_STRING_TOTAL_RIDE_COUNT, this.getTotalRideCount());

            Log.v(Constants.LOG_TAG, String.format("StockAttraction.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("StockAttraction.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }

    @Override
    public AttractionCategory getAttractionCategory()
    {
        return this.blueprint.getAttractionCategory();
    }

    @Override
    public void increaseTotalRideCount(int increment)
    {
        this.blueprint.increaseTotalRideCount(increment);
        super.increaseTotalRideCount(increment);
    }

    @Override
    public void decreaseTotalRideCount(int decrement)
    {
        if((this.blueprint.getTotalRideCount() - decrement) >= 0 && (super.getTotalRideCount() - decrement) >= 0)
        {
            this.blueprint.decreaseTotalRideCount(decrement);
            super.decreaseTotalRideCount(decrement);
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("StockAttraction.decreaseTotalRideCount:: %s's total ride count is [%d]: decreasing by [%d] would make it negative - not decreasing",
                    this, decrement, this.getTotalRideCount()));
        }

    }
}
