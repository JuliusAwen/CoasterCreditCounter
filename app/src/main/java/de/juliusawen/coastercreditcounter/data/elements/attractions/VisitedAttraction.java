package de.juliusawen.coastercreditcounter.data.elements.attractions;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class VisitedAttraction extends Attraction
{
    private StockAttraction stockAttraction;

    private int rideCount;

    private VisitedAttraction(String name, UUID uuid, StockAttraction stockAttraction, int rideCount)
    {
        super(name, uuid);
        this.stockAttraction = stockAttraction;
        this.rideCount = rideCount;
    }

    public static VisitedAttraction create(StockAttraction stockAttraction)
    {
        VisitedAttraction visitedAttraction;
        visitedAttraction = new VisitedAttraction(stockAttraction.getName(), UUID.randomUUID(), stockAttraction, 0);

        Log.v(Constants.LOG_TAG,  String.format("VisitedAttraction.create:: %s created", visitedAttraction.getFullName()));

        return visitedAttraction;
    }

    public static VisitedAttraction create(StockAttraction stockAttraction, int initialRideCount)
    {
        VisitedAttraction visitedAttraction;
        visitedAttraction = new VisitedAttraction(stockAttraction.getName(), UUID.randomUUID(), stockAttraction, initialRideCount);

        Log.v(Constants.LOG_TAG,  String.format("VisitedAttraction.create:: %s created with initial ride count [%d]", visitedAttraction.getFullName(), initialRideCount));

        return visitedAttraction;
    }

    public StockAttraction getStockAttraction()
    {
        return this.stockAttraction;
    }

    public int getRideCount()
    {
        return this.rideCount;
    }

    public void increaseRideCount()
    {
        this.rideCount ++;
        this.stockAttraction.increaseTotalRideCount();
    }

    public void decreaseRideCount()
    {
        this.rideCount --;
        this.stockAttraction.decreaseTotalRideCount();
    }

    @Override
    public AttractionCategory getAttractionCategory()
    {
        return this.stockAttraction.getAttractionCategory();
    }

    public static List<StockAttraction> getStockAttractions(List<VisitedAttraction> visitedAttractions)
    {
        List<StockAttraction> attractions = new ArrayList<>();
        for(VisitedAttraction visitedAttraction : visitedAttractions)
        {
            attractions.add(visitedAttraction.getStockAttraction());
        }
        return attractions;
    }
}