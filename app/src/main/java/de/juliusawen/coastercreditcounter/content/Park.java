package de.juliusawen.coastercreditcounter.content;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Park extends Location
{
    private List<Attraction> attractions;

    public Park(String name, UUID uuid)
    {
        super(name, uuid);
        this.attractions = new ArrayList<>();
    }

    public List<Attraction> getAttractions()
    {
        return this.attractions;
    }

    public void addAttraction(Attraction attraction)
    {
        this.attractions.add(attraction);
    }
}
