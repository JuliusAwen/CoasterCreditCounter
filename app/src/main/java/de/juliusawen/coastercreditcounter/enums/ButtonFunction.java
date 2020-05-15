package de.juliusawen.coastercreditcounter.enums;

import android.util.Log;

import de.juliusawen.coastercreditcounter.application.Constants;

public enum ButtonFunction
{
    NONE,

    MOVE_SELECTION_UP,
    MOVE_SELECTION_DOWN,

    BACK,
    CLOSE,
    CANCEL,
    OK;

    public static ButtonFunction getValue(int ordinal)
    {
        if(ButtonFunction.values().length >= ordinal)
        {
            return ButtonFunction.values()[ordinal];
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("ButtonFunction.getValue:: ordinal [%s] out of bounds (Enum has [%s] values) - returning NONE", ordinal, values().length));
            return NONE;
        }
    }
}
