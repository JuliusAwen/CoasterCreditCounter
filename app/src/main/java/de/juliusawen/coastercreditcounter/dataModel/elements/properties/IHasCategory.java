package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IHasCategory extends IElement
{
    Category getCategory();
    void setCategory(Category category);
}
