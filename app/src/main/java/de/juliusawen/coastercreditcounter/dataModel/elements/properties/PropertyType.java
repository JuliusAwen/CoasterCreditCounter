package de.juliusawen.coastercreditcounter.dataModel.elements.properties;

import de.juliusawen.coastercreditcounter.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum PropertyType
{
    INVALID,
    CREDIT_TYPE,
    CATEGORY,
    MANUFACTURER,
    MODEL,
    STATUS;

    public static PropertyType getValue(int ordinal)
    {
        if(ButtonFunction.values().length >= ordinal)
        {
            return PropertyType.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }
}
