package de.juliusawen.coastercreditcounter.dataModel.temporaryElements;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.Ride;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.globals.Constants;

/**
 * Container used in order to be able to sort attraction for every individual visit
 *
 * Parent: Park
 * Children: Rides
 */
public class VisitedAttraction extends Attraction implements ITemporaryElement
{
    private final IOnSiteAttraction onSiteAttraction;

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
            JSONObject jsonObject = new JSONObject();

            boolean hasRides = false;
            JSONArray rides = new JSONArray();
            for(IElement ride : this.getChildren())
            {
                rides.put(ride.getUuid());
                hasRides = true;
            }
            jsonObject.put(this.onSiteAttraction.getUuid().toString(), hasRides ? rides : JSONObject.NULL);

            Log.v(Constants.LOG_TAG, String.format("Visit.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
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
        return this.getChildCount();
    }

    public Ride addRide()
    {
        Ride ride = Ride.create((Calendar)((Visit)this.getParent()).getCalendar().clone(), null);
        this.addChildAndSetParent(ride);

        Log.d(Constants.LOG_TAG, String.format("VisitedAttraction.addRide:: added %s to %s for %s", ride, this, this.getOnSiteAttraction().getParent()));

        this.onSiteAttraction.increaseTotalRideCount(App.settings.getDefaultIncrement());

        return ride;
    }

    public Ride deleteLatestRide()
    {
        if(this.hasChildren())
        {
            Ride ride = (Ride)this.getChildren().get(this.getChildCount() -1);
            this.getChildren().remove(ride);

            Log.d(Constants.LOG_TAG, String.format("VisitedAttraction.deleteLatestRide:: removed %s from %s for %s", ride, this, this.getOnSiteAttraction().getParent()));

            this.onSiteAttraction.decreaseTotalRideCount(App.settings.getDefaultIncrement());
            return ride;
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("VisitedAttraction.deleteLatestRide:: no ride to remove from %s for %s", this, this.getOnSiteAttraction().getParent()));
            return null;
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

    @Deprecated
    @Override
    public void increaseTotalRideCount(int increment)
    {
        this.onSiteAttraction.increaseTotalRideCount(increment);
    }

    @Deprecated
    @Override
    public void decreaseTotalRideCount(int decrement)
    {
        this.onSiteAttraction.decreaseTotalRideCount(decrement);
    }
}