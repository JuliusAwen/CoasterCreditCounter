package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum ElementType
{
    IELEMENT,

    LOCATION,
    PARK,
    VISI,

    IPROPERTY,
    CREDIT_TYPE,
    CATEGORY,
    MANUFACTURER,
    MODEL,
    STATUS;

    public static ElementType getValue(int ordinal)
    {
        if(ElementType.values().length >= ordinal)
        {
            return ElementType.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }
}
