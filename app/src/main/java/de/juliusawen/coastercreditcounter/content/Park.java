package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;

public class Park extends Location
{
    private List<Attraction> attractions;

    public Park(String name, UUID uuid)
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

        return park;
    }

    public List<Attraction> getAttractions()
    {
        return this.attractions;
    }

    public void setAttractions(List<Attraction> attractions)
    {
        for(Attraction attraction : attractions)
        {
            Log.d(Constants.LOG_TAG,  String.format("Park.setAttraction:: park[%s] -> attraction[%s] set.", this.getName(), attraction.getName()));
        }
        this.attractions = attractions;
    }

    public void addAttraction(Attraction attraction)
    {
        Log.d(Constants.LOG_TAG,  String.format("Park.addAttraction:: park[%s] -> attraction[%s] added.", this.getName(), attraction.getName()));
        this.attractions.add(attraction);
    }
}
