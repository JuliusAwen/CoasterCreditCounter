package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum DetailDisplayMode
{
    OFF,
    ABOVE,
    BELOW;

    public static DetailDisplayMode getValue(int ordinal)
    {
        if(DetailDisplayMode.values().length >= ordinal)
        {
            return DetailDisplayMode.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }

    public String toString()
    {
        return String.format("DetailDisplayMode[%s]", this.name());
    }
}
