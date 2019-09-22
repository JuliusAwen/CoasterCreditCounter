package de.juliusawen.coastercreditcounter.dataModel.elements;

import java.util.UUID;

/**
 *
 */
public abstract class OrphanElement extends Element
{
    public OrphanElement(String name, UUID uuid)
    {
        super(name, uuid);
    }
}
