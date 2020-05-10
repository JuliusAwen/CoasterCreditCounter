package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IHasStatus extends IElement
{
    Status getStatus();
    void setStatus(Status status);
}
