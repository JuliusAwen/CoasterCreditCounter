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

    public static List<CountableAttraction> convertToCountableAttractions(List<? extends Element> elements)
    {
        List<CountableAttraction> countableAttractions = new ArrayList<>();
        for(Element element : elements)
        {
            if(element.isInstance(CountableAttraction.class))
            {
                countableAttractions.add((CountableAttraction) element);
            }
            else
            {
                String errorMessage = String.format("CountableAttraction.convertToCountableAttractions:: type mismatch - %s is not of type <CountableAttraction>", element);
                Log.e(Constants.LOG_TAG, errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }
        return countableAttractions;
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
