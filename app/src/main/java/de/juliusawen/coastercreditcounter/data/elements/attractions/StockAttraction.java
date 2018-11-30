package de.juliusawen.coastercreditcounter.data.elements.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class StockAttraction extends Attraction
{
    private int totalRideCount;

    StockAttraction(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static StockAttraction create(String name)
    {
        StockAttraction stockAttraction = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            stockAttraction = new StockAttraction(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("StockAttraction.create:: %s created", stockAttraction.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("StockAttraction.create:: invalid name [%s] - StockAttraction not created", name));
        }
        return stockAttraction;
    }

    public void setAttractionCategory(AttractionCategory attractionCategory)
    {
        if(super.attractionCategory != null)
        {
            super.attractionCategory.deleteChild(this);
        }

        attractionCategory.addChild(this);
        super.attractionCategory = attractionCategory;

        Log.v(Constants.LOG_TAG,  String.format("Attraction.setAttractionCategory:: set %s to %s", attractionCategory, this));
    }

    public int getTotalRideCount()
    {
        return this.totalRideCount;
    }

    protected void increaseTotalRideCount()
    {
        this.totalRideCount ++;
        Log.d(Constants.LOG_TAG, String.format("StockAttraction.increaseTotalRideCount:: increased %s's total ride count to [%d]", this, this.getTotalRideCount()));
    }

    protected void decreaseTotalRideCount()
    {
        this.totalRideCount --;
        Log.d(Constants.LOG_TAG, String.format("StockAttraction.decreaseTotalRideCount:: decreased %s's total ride count to [%d]", this, this.getTotalRideCount()));
    }
}
