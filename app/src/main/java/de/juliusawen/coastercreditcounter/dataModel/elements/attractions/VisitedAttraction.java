package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;

/**
 * Container for Attractions - used in order to be able to sort attractions for every individual visit without sorting the child attractions for the parent park
 *
 * Parent: Park
 * Children: none
 */
public class VisitedAttraction extends Attraction
{
    private final IOnSiteAttraction onSiteAttraction;

    private VisitedAttraction(IOnSiteAttraction onSiteAttraction, UUID uuid)
    {
        super(onSiteAttraction.getName(), uuid);
        this.onSiteAttraction = onSiteAttraction;
    }

    public static VisitedAttraction create(IOnSiteAttraction onSiteAttraction)
    {
        VisitedAttraction visitedAttraction;
        visitedAttraction = new VisitedAttraction(onSiteAttraction, UUID.randomUUID());

        Log.v(Constants.LOG_TAG,  String.format("VisitedAttraction.create:: %s created", visitedAttraction.getFullName()));

        return visitedAttraction;
    }

    public IOnSiteAttraction getOnSiteAttraction()
    {
        return this.onSiteAttraction;
    }

    @Override
    public int fetchTotalRideCount()
    {
        return this.getTrackedRideCount();
    }

    @Override
    public void increaseTrackedRideCount(int increment)
    {
        super.increaseTrackedRideCount(increment);
        this.getOnSiteAttraction().increaseTrackedRideCount(increment);
    }

    @Override
    public void decreaseTrackedRideCount(int decrement)
    {
        super.decreaseTrackedRideCount(decrement);
        this.getOnSiteAttraction().decreaseTrackedRideCount(decrement);
    }

    @Override
    public int getUntracktedRideCount()
    {
        Log.e(Constants.LOG_TAG, String.format("VisitedAttraction.setUntracktedRideCount:: %s can not have UntracktedRideCount - returning OnSiteAttraction's UntrackedRideDCount", this));
        return this.getOnSiteAttraction().getUntracktedRideCount();
    }

    public void setUntracktedRideCount(int untracktedRideCount)
    {
        String errorMessage = String.format("VisitedAttraction.setUntracktedRideCount:: %s not able to set UntracktedRideCount on VisitedAttractions", this);
        Log.e(Constants.LOG_TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    @Override
    public CreditType getCreditType()
    {
        return this.getOnSiteAttraction().getCreditType();
    }

    @Override
    public void setCreditType(CreditType creditType)
    {
        String errorMessage = String.format("VisitedAttraction.setCreditType:: %s not able to set CreditType on VisitedAttractions", this);
        Log.e(Constants.LOG_TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    @Override
    public Category getCategory()
    {
        return this.getOnSiteAttraction().getCategory();
    }

    @Override
    public void setCategory(Category category)
    {
        String errorMessage = String.format("VisitedAttraction.setCategory:: %s not able to set Category on VisitedAttractions", this);
        Log.e(Constants.LOG_TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }


    @Override
    public Manufacturer getManufacturer()
    {
        return this.getOnSiteAttraction().getManufacturer();
    }

    @Override
    public void setManufacturer(Manufacturer manufacturer)
    {
        String errorMessage = String.format("VisitedAttraction.setManufacturer:: %s not able to set Manufacturer on VisitedAttractions", this);
        Log.e(Constants.LOG_TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    @Override
    public Status getStatus()
    {
        return this.getOnSiteAttraction().getStatus();
    }

    @Override
    public void setStatus(Status status)
    {
        String errorMessage = String.format("VisitedAttraction.setStatus:: %s not able to set Status on VisitedAttractions", this);
        Log.e(Constants.LOG_TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObjectRideCountByAttraction = new JSONObject();
            jsonObjectRideCountByAttraction.put(this.getOnSiteAttraction().getUuid().toString(), this.fetchTotalRideCount());

            Log.v(Constants.LOG_TAG, String.format("VisitedAttraction.toJson:: created JSON for %s [%s]", this, jsonObjectRideCountByAttraction.toString()));
            return jsonObjectRideCountByAttraction;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("VisitedAttraction.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
}