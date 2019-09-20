package de.juliusawen.coastercreditcounter.dataModel.temporaryElements;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class SpecialGroupHeader extends OrphanElement implements IGroupHeader
{
    private SpecialGroupHeader(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static SpecialGroupHeader create(String name)
    {
        SpecialGroupHeader specialGroupHeader = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            specialGroupHeader = new SpecialGroupHeader(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("SpecialGroupHeader.create:: %s created", specialGroupHeader.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("SpecialGroupHeader.create:: invalid name[%s] - specialGroupHeader not created", name));
        }
        return specialGroupHeader;
    }

    @Override
    public IElement getGroupElement()
    {
        return null;
    }
}
