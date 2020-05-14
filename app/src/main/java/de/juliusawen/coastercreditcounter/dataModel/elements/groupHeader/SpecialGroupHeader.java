package de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

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
            Log.d(String.format("%s created", specialGroupHeader.getFullName()));
        }

        return specialGroupHeader;
    }
}
