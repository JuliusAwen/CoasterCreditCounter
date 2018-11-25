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

    private VisitedAttraction(String name, UUID uuid, StockAttraction stockAttraction)
    {
        super(name, uuid);
        this.stockAttraction = stockAttraction;
    }

    public static VisitedAttraction create(StockAttraction stockAttraction)
    {
        VisitedAttraction visitedAttraction;
        visitedAttraction = new VisitedAttraction(stockAttraction.getName(), UUID.randomUUID(), stockAttraction);

        Log.v(Constants.LOG_TAG,  String.format("VisitedAttraction.create:: %s created", visitedAttraction.getFullName()));

        return visitedAttraction;
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

    public StockAttraction getStockAttraction()
    {
        return this.stockAttraction;
    }

    @Override
    public AttractionCategory getAttractionCategory()
    {
        return this.stockAttraction.getAttractionCategory();
    }
}