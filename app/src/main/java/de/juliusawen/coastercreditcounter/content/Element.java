package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;

public abstract class Element
{
    private String name;
    private UUID uuid;

    public Element(String name, UUID uuid)
    {
        this.setName(name);
        this.uuid = uuid;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        if(!name.trim().isEmpty())
        {
            this.name = name.trim();
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("Element.setName:: name[%s] is invalid.", name));
        }
    }

    public UUID getUuid()
    {
        return this.uuid;
    }
}
