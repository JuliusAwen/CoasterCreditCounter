package de.juliusawen.coastercreditcounter.tools.logger;

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
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }
}
