package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IHasModel extends IElement
{
    Model getModel();
    void setModel(Model model);
}
