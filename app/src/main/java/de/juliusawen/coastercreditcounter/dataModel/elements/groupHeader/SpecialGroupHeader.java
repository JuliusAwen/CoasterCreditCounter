package de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.OrphanElement;
import de.juliusawen.coastercreditcounter.application.Constants;

public class SpecialGroupHeader extends OrphanElement implements IGroupHeader
{
    private SpecialGroupHeader(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static SpecialGroupHeader create(String name)
    {
        SpecialGroupHeader specialGroupHeader = null;
        if(Element.nameIsValid(name))
        {
            specialGroupHeader = new SpecialGroupHeader(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("SpecialGroupHeader.create:: %s created", specialGroupHeader.getFullName()));
        }
        return specialGroupHeader;
    }
}
