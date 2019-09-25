package de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public class GroupHeader extends Element implements IGroupHeader
{
    private final IElement groupElement; // eg. [Category "XYZ] - needed to find GroupHeader for Element when sorting

    private GroupHeader(String name, UUID uuid, IElement groupElement)
    {
        super(name, uuid);
        this.groupElement = groupElement;
    }

    public static GroupHeader create(IElement groupItem)
    {
        GroupHeader groupHeader;
        groupHeader = new GroupHeader(groupItem.getName(), UUID.randomUUID(), groupItem);

        Log.v(Constants.LOG_TAG,  String.format("GroupHeader.create:: %s created", groupHeader.getFullName()));

        return groupHeader;
    }

    public IElement getGroupElement()
    {
        return groupElement;
    }
}
