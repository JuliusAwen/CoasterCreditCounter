package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;

public class Attraction extends Element
{
    private Location location;
    //private Model model;
    //private Manufacturer manufacturer

    public Attraction(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Attraction createAttraction(String name)
    {
        Attraction attraction = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            Log.v(Constants.LOG_TAG,  String.format("Attraction.createAttraction:: attraction[%s] created.", name));
            attraction = new Attraction(name, UUID.randomUUID());
        }

        return attraction;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }
}
