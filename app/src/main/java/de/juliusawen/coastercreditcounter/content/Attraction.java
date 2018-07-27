package de.juliusawen.coastercreditcounter.content;

import java.util.UUID;

public class Attraction extends Element
{
    private Location location;
    //private Model model;
    //private Manufacturer manufacturer

    public Attraction(String name, UUID uuid)
    {
        super(name, uuid);
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
