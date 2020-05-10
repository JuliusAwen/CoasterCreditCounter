package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IHasManufacturer extends IElement
{
    Manufacturer getManufacturer();
    void setManufacturer(Manufacturer manufacturer);
}
