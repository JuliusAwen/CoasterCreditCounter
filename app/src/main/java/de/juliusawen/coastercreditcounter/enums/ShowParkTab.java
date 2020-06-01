package de.juliusawen.coastercreditcounter.enums;

import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum ShowParkTab
{
    SHOW_PARK_OVERVIEW,
    SHOW_ATTRACTIONS,
    SHOW_VISITS;

    public static ShowParkTab getValue(int ordinal)
    {
        if(ShowParkTab.values().length >= ordinal)
        {
            return ShowParkTab.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }

    public String toString()
    {
        return String.format("Tab[%s]", this.name());
    }
}
