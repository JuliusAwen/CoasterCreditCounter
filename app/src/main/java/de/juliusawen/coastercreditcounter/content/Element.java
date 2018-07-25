package de.juliusawen.coastercreditcounter.content;

import java.util.UUID;

public abstract class Element
{
    private String name;
    private UUID uuid;

    public Element(String name, UUID uuid)
    {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }
}
