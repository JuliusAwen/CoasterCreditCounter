package de.juliusawen.coastercreditcounter.backend.objects.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class Attraction extends Element implements IAttraction
{
    protected AttractionCategory attractionCategory = null;
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
            jsonObject.put(Constants.JSON_STRING_ELEMENT, Element.toJson(this, false));
            jsonObject.put(Constants.JSON_STRING_UNTRACKED_RIDE_COUNT, this.untracktedRideCount);

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
    public void deleteElementAndDescendants()
    {
        Log.d(Constants.LOG_TAG, String.format("Attraction.deleteElementAndDescendants:: deleting %s and children", this));

        for(IElement child : this.getChildren())
        {
            child.deleteElementAndDescendants();
        }

        this.attractionCategory.deleteChild(this);
        this.getParent().deleteChild(this);
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

        Log.d(Constants.LOG_TAG,  String.format("Attraction.setAttractionCategory:: set %s's attraction category to %s", this, attractionCategory));
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
        this.totalRideCount += increment;
        Log.d(Constants.LOG_TAG, String.format("Attraction.increaseTotalRideCount:: increased %s's total ride count by [%d] to [%d] ([%d] rides untracked)"
                , this, increment, this.getTotalRideCount(), this.untracktedRideCount));
    }


    public void decreaseTotalRideCount(int decrement)
    {
        if((this.totalRideCount - decrement) >= 0)
        {
            this.totalRideCount -= decrement;
            Log.d(Constants.LOG_TAG, String.format("Attraction.decreaseTotalRideCount:: decreased %s's total ride count by [%d] to [%d] ([%d] rides untracked)"
                    , this, decrement, this.getTotalRideCount(), this.untracktedRideCount));
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("Attraction.decreaseTotalRideCount:: %s's total ride count is [%d] ([%d] rides untracked):" +
                            " decreasing by [%d] would make it negative - not decreasing", this, this.getTotalRideCount(), this.untracktedRideCount, decrement));
        }
    }
}
