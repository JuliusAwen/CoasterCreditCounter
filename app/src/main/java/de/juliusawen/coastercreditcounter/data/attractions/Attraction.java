package de.juliusawen.coastercreditcounter.data.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class Attraction extends Element implements IAttraction
{
    protected AttractionCategory attractionCategory = null;
    private int totalRideCount;

    protected Attraction(String name, UUID uuid)
    {
        super(name, uuid);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.JSON_STRING_ELEMENT, Element.toJson(this, false));
            jsonObject.put(Constants.JSON_STRING_ATTRACTION_CATEGORY, this.attractionCategory.getUuid());
            jsonObject.put(Constants.JSON_STRING_TOTAL_RIDE_COUNT, this.totalRideCount);

            Log.v(Constants.LOG_TAG, String.format("Attraction.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Attraction.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }

    public AttractionCategory getAttractionCategory()
    {
        return this.attractionCategory;
    }

    public void setAttractionCategory(AttractionCategory attractionCategory)
    {
        if(this.attractionCategory != null)
        {
            this.attractionCategory.deleteChild(this);
        }

        if(!attractionCategory.containsChild(this))
        {
            attractionCategory.addChild(this);
        }

        this.attractionCategory = attractionCategory;

        Log.v(Constants.LOG_TAG,  String.format("Attraction.setAttractionCategory:: set %s to %s", attractionCategory, this));
    }

    public int getTotalRideCount()
    {
        return this.totalRideCount;
    }

    public void increaseTotalRideCount(int increment)
    {
        this.totalRideCount += increment;
        Log.d(Constants.LOG_TAG, String.format("Attraction.increaseTotalRideCount:: increased %s's total ride count by [%d] to [%d]", this, increment, this.getTotalRideCount()));
    }


    public void decreaseTotalRideCount(int decrement)
    {
        if((this.totalRideCount - decrement) >= 0)
        {
            this.totalRideCount -= decrement;
            Log.d(Constants.LOG_TAG, String.format("Attraction.decreaseTotalRideCount:: decreased %s's total ride count by [%d] to [%d]", this, decrement, this.getTotalRideCount()));
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("Attraction.decreaseTotalRideCount:: %s's total ride count is [%d]: decreasing by [%d] would make it negative - not decreasing",
                    this, decrement, this.getTotalRideCount()));
        }

    }
}
