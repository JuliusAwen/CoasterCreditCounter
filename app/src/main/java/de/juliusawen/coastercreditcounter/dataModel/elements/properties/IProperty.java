package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.traits.IOrphan;
import de.juliusawen.coastercreditcounter.persistence.IPersistable;

/**
 *      Marks Element as being a Property.<br>
 *      Can be CreditType, Category, Manufacturer, Model or Status<br>
 */
public interface IProperty extends IElement, IOrphan, IPersistable
{
    boolean isDefault();
}
