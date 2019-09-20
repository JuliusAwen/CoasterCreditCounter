package de.juliusawen.coastercreditcounter.dataModel.temporaryElements;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IGroupHeader extends IElement, ITemporaryElement
{
    IElement getGroupElement();
}
