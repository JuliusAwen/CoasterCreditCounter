package de.juliusawen.coastercreditcounter.dataModel.temporaryElements;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.globals.Constants;

/**
 * Container used in order to be able to sort attractions for every individual visit
 *
 * Parent: Park
 * Children: Rides
 */
public class VisitedAttraction extends Attraction implements ITemporaryElement
{
    private final IOnSiteAttraction onSiteAttraction;
    private int rideCount;

    private VisitedAttraction(String name, IOnSiteAttraction onSiteAttraction, UUID uuid)
    {
        super(name, 0, uuid);
        this.onSiteAttraction = onSiteAttraction;
    }

    public static VisitedAttraction create(IOnSiteAttraction onSiteAttraction)
    {
        VisitedAttraction visitedAttraction;
        visitedAttraction = new VisitedAttraction(onSiteAttraction.getName(), onSiteAttraction, UUID.randomUUID());

        Log.v(Constants.LOG_TAG,  String.format("VisitedAttraction.create:: %s created", visitedAttraction.getFullName()));

        return visitedAttraction;
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObjectRideCountByAttraction = new JSONObject();
            jsonObjectRideCountByAttraction.put(this.onSiteAttraction.getUuid().toString(), this.getRideCount());

            Log.v(Constants.LOG_TAG, String.format("Visit.toJson:: created JSON for %s [%s]", this, jsonObjectRideCountByAttraction.toString()));
            return jsonObjectRideCountByAttraction;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Visit.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }

    public IOnSiteAttraction getOnSiteAttraction()
    {
        return this.onSiteAttraction;
    }

    public int getRideCount()
    {
        return this.rideCount;
    }


    @Override
    public void increaseTotalRideCount(int increment)
    {
        this.rideCount += increment;
        this.onSiteAttraction.increaseTotalRideCount(increment);
    }

    @Override
    public void decreaseTotalRideCount(int decrement)
    {
        if(decrement > 0 && this.rideCount - decrement >= 0)
        {
            this.rideCount -= decrement;
            this.onSiteAttraction.decreaseTotalRideCount(decrement);
        }
    }

    @Override
    public void deleteElementAndDescendants()
    {
        this.onSiteAttraction.decreaseTotalRideCount(this.getChildCount());
        for(IElement ride : new ArrayList<>(this.getChildren()))
        {
            ride.deleteElement();
        }
        super.deleteElement();
    }

    @Override
    public AttractionCategory getAttractionCategory()
    {
        return this.onSiteAttraction.getAttractionCategory();
    }


    @Override
    public Manufacturer getManufacturer()
    {
        return this.onSiteAttraction.getManufacturer();
    }
}