package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum OnClickListenerType
{
    INCREASE_RIDE_COUNT,
    DECREASE_RIDE_COUNT,
    REMOVE_VISITED_ATTRACTION;

    public static OnClickListenerType getValue(int ordinal)
    {
        if(OnClickListenerType.values().length >= ordinal)
        {
            return OnClickListenerType.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }

    public String toString()
    {
        return String.format("OnClickListenerType[%s]", this.name());
    }
}
