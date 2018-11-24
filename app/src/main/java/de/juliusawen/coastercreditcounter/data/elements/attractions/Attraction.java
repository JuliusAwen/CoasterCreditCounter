package de.juliusawen.coastercreditcounter.data.elements.attractions;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;

public abstract class Attraction extends Element
{
    protected AttractionCategory attractionCategory = null;

    public Attraction(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public AttractionCategory getAttrationCategory()
    {
        return this.attractionCategory;
    }
}
