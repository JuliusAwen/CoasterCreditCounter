package de.juliusawen.coastercreditcounter.dataModel.elements;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.StringTool;

public class SpecialGroupHeader extends OrphanElement implements IGroupHeader
{
    private SpecialGroupHeader(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static SpecialGroupHeader create(String name)
    {
        SpecialGroupHeader specialGroupHeader = null;
        if(StringTool.nameIsValid(name))
        {
            specialGroupHeader = new SpecialGroupHeader(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("SpecialGroupHeader.create:: %s created", specialGroupHeader.getFullName()));
        }
        return specialGroupHeader;
    }
}
