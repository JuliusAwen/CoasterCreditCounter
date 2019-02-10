package de.juliusawen.coastercreditcounter.backend.objects.temporaryElements;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Ride;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
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
        this.addChild(ride);

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
    public AttractionCategory getAttractionCategory()
    {
        return this.onSiteAttraction.getAttractionCategory();
    }

    public static List<IOnSiteAttraction> getOnSiteAttractions(List<VisitedAttraction> visitedAttractions)
    {
        List<IOnSiteAttraction> onSiteAttractions = new ArrayList<>();
        for(VisitedAttraction visitedAttraction : visitedAttractions)
        {
            onSiteAttractions.add(visitedAttraction.getOnSiteAttraction());
        }
        return onSiteAttractions;
    }
}