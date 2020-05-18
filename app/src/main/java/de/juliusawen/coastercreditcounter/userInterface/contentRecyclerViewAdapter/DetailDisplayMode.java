package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.util.Log;

import de.juliusawen.coastercreditcounter.application.Constants;

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
            Log.e(Constants.LOG_TAG, String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }
}
