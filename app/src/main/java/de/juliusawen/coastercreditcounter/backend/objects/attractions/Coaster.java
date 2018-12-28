package de.juliusawen.coastercreditcounter.backend.objects.attractions;

import java.util.UUID;

public abstract class Coaster extends Attraction
{
    protected Coaster(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }
}
