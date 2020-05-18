package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.util.Log;

import de.juliusawen.coastercreditcounter.application.Constants;

public enum DetailType
{
    NONE,
    LOCATION,
    CREDIT_TYPE,
    CATEGORY,
    MANUFACTURER,
    MODEL,
    STATUS,
    TOTAL_RIDE_COUNT;

    public static DetailType getValue(int ordinal)
    {
        if(DetailType.values().length >= ordinal)
        {
            return DetailType.values()[ordinal];
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }
}
