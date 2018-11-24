package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class VisitedAttraction extends Element
{
    private Attraction attraction;

    private VisitedAttraction(String name, UUID uuid, Attraction attraction)
    {
        super(name, uuid);
        this.attraction = attraction;
    }

    public static VisitedAttraction create(Attraction attraction)
    {
        VisitedAttraction visitedAttraction;
        visitedAttraction = new VisitedAttraction(attraction.getName(), UUID.randomUUID(), attraction);

        Log.v(Constants.LOG_TAG,  String.format("VisitedAttraction.create:: %s created", visitedAttraction.getFullName()));

        return visitedAttraction;
    }

    public Attraction getAttraction()
    {
        return this.attraction;
    }

    public AttractionCategory getAttractionCategory()
    {
        return this.attraction.getAttrationCategory();
    }

    public static List<Attraction> getAttractions(List<VisitedAttraction> visitedAttractions)
    {
        List<Attraction> attractions = new ArrayList<>();
        for(VisitedAttraction visitedAttraction : visitedAttractions)
        {
            attractions.add(visitedAttraction.getAttraction());
        }
        return attractions;
    }
}