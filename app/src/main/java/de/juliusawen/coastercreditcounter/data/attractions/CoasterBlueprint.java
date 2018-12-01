package de.juliusawen.coastercreditcounter.data.attractions;

import java.util.UUID;

public class CoasterBlueprint extends Coaster implements IBlueprint, ICategorized
{
    protected CoasterBlueprint(String name, UUID uuid)
    {
        super(name, uuid);
    }
}
