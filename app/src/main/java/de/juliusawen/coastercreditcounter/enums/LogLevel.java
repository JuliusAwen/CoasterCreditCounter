package de.juliusawen.coastercreditcounter.enums;

import android.util.Log;

import de.juliusawen.coastercreditcounter.application.Constants;

public enum LogLevel
{
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR;

    public static LogLevel getValue(int ordinal)
    {
        if(LogLevel.values().length >= ordinal)
        {
            return LogLevel.values()[ordinal];
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("LogLevel.getValue:: ordinal [%s] out of bounds (Enum has [%s] values) - returning VERBOSE", ordinal, values().length));
            return VERBOSE;
        }
    }
}
