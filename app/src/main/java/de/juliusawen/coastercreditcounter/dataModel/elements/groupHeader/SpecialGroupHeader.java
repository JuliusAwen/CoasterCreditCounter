package de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;

public class SpecialGroupHeader extends Element implements IGroupHeader
{
    private SpecialGroupHeader(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static SpecialGroupHeader create(String name)
    {
        SpecialGroupHeader specialGroupHeader = null;
        if(Element.isNameValid(name))
        {
            specialGroupHeader = new SpecialGroupHeader(name, null);
            Log.v(Constants.LOG_TAG,  String.format("SpecialGroupHeader.create:: %s created", specialGroupHeader.getFullName()));
        }

        return specialGroupHeader;
    }
}
