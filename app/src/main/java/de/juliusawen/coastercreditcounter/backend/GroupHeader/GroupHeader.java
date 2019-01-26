package de.juliusawen.coastercreditcounter.backend.GroupHeader;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.OrphanElement;

public abstract class GroupHeader extends OrphanElement
{
    public GroupHeader(String name, UUID uuid)
    {
        super(name, uuid);
    }
}
