package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class Park extends Location
{
    private List<Attraction> attractions;

    private Park(String name, UUID uuid)
    {
        super(name, uuid);
        this.attractions = new ArrayList<>();
    }

    public static Park createPark(String name)
    {
        Park park = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.v(Constants.LOG_TAG,  String.format("Park.createPark:: park[%s] created.", name));
            park = new Park(name, UUID.randomUUID());
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Park.createPark:: invalid name[%s] - park not created.", name));
        }

        return park;
    }

    public List<Attraction> getAttractions()
    {
        return this.attractions;
    }

    public void setAttractions(List<Attraction> attractions)
    {
        Log.d(Constants.LOG_TAG,  String.format("Park.setAttractions:: park[%s] -> attractions cleared.", this.toString()));
        this.attractions.clear();

        for(Attraction attraction : attractions)
        {
            this.addAttraction(attraction);
        }
    }

    public void addAttraction(Attraction attraction)
    {
        Log.d(Constants.LOG_TAG,  String.format("Park.addAttraction:: park[%s] -> attraction[%s] added.", this.toString(), attraction.toString()));
        this.attractions.add(attraction);
    }
}
