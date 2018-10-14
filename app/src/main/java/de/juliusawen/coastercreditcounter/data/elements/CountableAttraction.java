package de.juliusawen.coastercreditcounter.data.elements;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class CountableAttraction extends Element
{
    private Attraction attraction;

    private CountableAttraction(String name, UUID uuid, Attraction attraction)
    {
        super(name, uuid);
        this.attraction = attraction;
    }

    public static CountableAttraction create(Attraction attraction)
    {
        CountableAttraction countableAttraction;
        countableAttraction = new CountableAttraction(attraction.getName(), UUID.randomUUID(), attraction);

        Log.v(Constants.LOG_TAG,  String.format("CountableAttraction.create:: %s created", countableAttraction.getFullName()));

        return countableAttraction;
    }

    public Attraction getAttraction()
    {
        return attraction;
    }

    public static List<Attraction> getAttractions(List<CountableAttraction> countableAttractions)
    {
        List<Attraction> attractions = new ArrayList<>();
        for(CountableAttraction countableAttraction : countableAttractions)
        {
            attractions.add(countableAttraction.getAttraction());
        }
        return attractions;
    }
}
