package de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class GroupHeader extends Element implements IGroupHeader
{
    private final IElement groupElement; // eg. [Category "XYZ] - needed to find GroupHeader for Element when sorting

    private GroupHeader(String name, IElement groupElement, UUID uuid)
    {
        super(name, uuid);
        this.groupElement = groupElement;
    }

    public static GroupHeader create(IElement groupItem)
    {
        GroupHeader groupHeader;
        groupHeader = new GroupHeader(groupItem.getName(), groupItem, null);

        Log.d(String.format("%s created", groupHeader.getFullName()));

        return groupHeader;
    }

    public IElement getGroupElement()
    {
        return groupElement;
    }
}
