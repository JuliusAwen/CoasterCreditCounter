package de.juliusawen.coastercreditcounter.data.attractions;

import java.util.UUID;

public class AttractionBlueprint extends Attraction implements IBlueprint, ICategorized
{
    protected AttractionBlueprint(String name, UUID uuid)
    {
        super(name, uuid);
    }
}
