package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Status;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.JsonTool;

/**
 * Abstract base class for all Attractions containing all base methods
 */
public abstract class Attraction extends Element implements IAttraction
{
    private AttractionCategory attractionCategory;
    private Manufacturer manufacturer;
    private Status status;
    private int untracktedRideCount;
    private int totalRideCount;


    protected Attraction(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, uuid);
        this.setUntracktedRideCount(untrackedRideCount);
    }

    @Override
    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();

            JsonTool.putNameAndUuid(jsonObject, this);
            jsonObject.put(Constants.JSON_STRING_MANUFACTURER, this.getManufacturer().getUuid());
            jsonObject.put(Constants.JSON_STRING_ATTRACTION_CATEGORY, this.getAttractionCategory().getUuid());
            jsonObject.put(Constants.JSON_STRING_STATUS, this.getStatus().getUuid());
            jsonObject.put(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT, this.getUntracktedRideCount());

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

    @Override
    public void deleteElement()
    {
        if(this.attractionCategory != null)
        {
            this.attractionCategory.deleteChild(this);
        }

        if(this.manufacturer != null)
        {
            this.manufacturer.deleteChild(this);
        }

        super.deleteElement();
    }

    public AttractionCategory getAttractionCategory()
    {
        return this.attractionCategory == null ? AttractionCategory.getDefault() : this.attractionCategory;
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

        Log.d(Constants.LOG_TAG,  String.format("Attraction.setAttractionCategory:: set %s's attraction category to %s", this, attractionCategory));
    }

    public Manufacturer getManufacturer()
    {
        return this.manufacturer == null ? Manufacturer.getDefault() : this.manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer)
    {
        if(this.manufacturer != null)
        {
            this.manufacturer.deleteChild(this);
        }

        if(!manufacturer.containsChild(this))
        {
            manufacturer.addChild(this);
        }

        this.manufacturer = manufacturer;

        Log.d(Constants.LOG_TAG,  String.format("Attraction.setManufacturer:: set %s's manufacturer to %s", this, manufacturer));
    }

    public Status getStatus()
    {
        return this.status == null ? Status.getDefault() : this.status;
    }

    public void setStatus(Status status)
    {
        if(this.status != null)
        {
            this.status.deleteChild(this);
        }

        if(!status.containsChild(this))
        {
            status.addChild(this);
        }

        this.status = status;
        Log.d(Constants.LOG_TAG,  String.format("Attraction.setStatus:: set %s's status to %s", this, status));
    }

    public int getUntracktedRideCount()
    {
        return this.untracktedRideCount;
    }

    public void setUntracktedRideCount(int untracktedRideCount)
    {
        if(untracktedRideCount != this.untracktedRideCount)
        {
            this.untracktedRideCount = untracktedRideCount;
            Log.v(Constants.LOG_TAG,  String.format("Attraction.setUntracktedRideCount:: set %s's untracked ride count to [%d]", this, this.untracktedRideCount));
        }
    }

    public int getTotalRideCount()
    {
        return this.totalRideCount + this.untracktedRideCount;
    }

    public void increaseTotalRideCount(int increment)
    {
        if(increment > 0)
        {
            this.totalRideCount += increment;
            Log.d(Constants.LOG_TAG, String.format("Attraction.increaseTotalRideCount:: increased %s's total ride count by [%d] to [%d] ([%d] rides untracked)",
                    this, increment, this.getTotalRideCount(), this.untracktedRideCount));
        }
    }


    public void decreaseTotalRideCount(int decrement)
    {
        if(decrement > 0)
        {
            if((this.totalRideCount - decrement) >= 0)
            {
                this.totalRideCount -= decrement;
                Log.d(Constants.LOG_TAG, String.format("Attraction.decreaseTotalRideCount:: decreased %s's total ride count by [%d] to [%d] ([%d] rides untracked)",
                        this, decrement, this.getTotalRideCount(), this.untracktedRideCount));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Attraction.decreaseTotalRideCount:: %s's total ride count is [%d] ([%d] rides untracked): " +
                        "decreasing by [%d] would make it negative - not decreasing", this, this.getTotalRideCount(), this.untracktedRideCount, decrement));
            }
        }
    }
}
