package de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.OrphanElement;
import de.juliusawen.coastercreditcounter.application.Constants;

public class GroupHeader extends OrphanElement implements IGroupHeader
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
