package de.juliusawen.coastercreditcounter.tools.logger;

import android.util.Log;

import de.juliusawen.coastercreditcounter.application.Constants;

public enum LogLevel
{
    NONE,
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
            Log.e(Constants.LOG_TAG, String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }
}
