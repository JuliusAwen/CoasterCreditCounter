package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum GroupType
{
    NONE,
    YEAR,
    PARK,
    CREDIT_TYPE,
    CATEGORY,
    MANUFACTURER,
    MODEL,
    STATUS;

    public static GroupType getValue(int ordinal)
    {
        if(GroupType.values().length >= ordinal)
        {
            return GroupType.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }
}
