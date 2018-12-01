package de.juliusawen.coastercreditcounter.data.attractions;

import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;

public interface IAttraction extends IElement
{
    AttractionCategory getAttractionCategory();
    void setAttractionCategory(AttractionCategory attractionCategory);
    int getTotalRideCount();
    void increaseTotalRideCount(int increment);
    void decreaseTotalRideCount(int decrement);
}
