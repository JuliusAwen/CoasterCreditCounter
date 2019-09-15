package de.juliusawen.coastercreditcounter.dataModel.temporaryElements;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class SpecialGroupHeader extends OrphanElement implements IGroupHeader, ITemporaryElement
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
            Log.v(Constants.LOG_TAG,  String.format("SpecialGroupHeader.show:: %s created", specialGroupHeader.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("SpecialGroupHeader.show:: invalid name[%s] - specialGroupHeader not created", name));
        }
        return specialGroupHeader;
    }
}
