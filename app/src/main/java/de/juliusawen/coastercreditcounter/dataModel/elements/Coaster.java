package de.juliusawen.coastercreditcounter.dataModel.elements;

import java.util.UUID;

/**
 * Abstract base class for coasters - can be CoasterBlueprint or CustomCoaster
 */
public abstract class Coaster extends Attraction
{
    protected Coaster(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }
}
