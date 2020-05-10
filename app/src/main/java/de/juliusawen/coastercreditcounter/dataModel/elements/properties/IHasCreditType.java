package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public interface IHasCreditType extends IElement
{
    CreditType getCreditType();
    void setCreditType(CreditType creditType);
}
