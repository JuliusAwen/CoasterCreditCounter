package de.juliusawen.coastercreditcounter.backend.objects.attractions;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class VisitedAttraction extends Attraction
{
    private final IOnSiteAttraction onSiteAttraction;

    private int rideCount = 0;

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

    public IOnSiteAttraction getOnSiteAttraction()
    {
        return this.onSiteAttraction;
    }

    public int getRideCount()
    {
        return this.rideCount;
    }

    public void increaseRideCount(int increment)
    {
        this.rideCount += increment;
        Log.d(Constants.LOG_TAG, String.format("VisitedAttraction.increaseRideCount:: increased %s's ride count for %s by [%d] to [%d]",
                this.getOnSiteAttraction(), this.getParent() == null ? "[visit not yet set]" : this.getParent().toString(), increment, this.getRideCount()));

        this.onSiteAttraction.increaseTotalRideCount(increment);
    }

    public boolean decreaseRideCount(int decrement)
    {
        if((this.rideCount - decrement) >= 0)
        {
            this.rideCount -= decrement;
            Log.d(Constants.LOG_TAG, String.format("VisitedAttraction.decreaseRideCount:: decreased %s's ride count for %s by [%d] to [%d]",
                    this.getOnSiteAttraction(), this.getParent(), decrement, this.getRideCount()));

            this.onSiteAttraction.decreaseTotalRideCount(decrement);

            return true;
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("VisitedAttraction.decreaseRideCount:: %s's total ride count for [%s] is [%d]: decreasing by [%d] would make it negative - not decreasing",
                    this.getOnSiteAttraction(), this.getParent(), this.getRideCount(), decrement));

            return false;
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