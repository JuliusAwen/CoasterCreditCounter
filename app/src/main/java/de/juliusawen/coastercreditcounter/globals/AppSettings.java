package de.juliusawen.coastercreditcounter.globals;

public abstract class AppSettings
{
//    public static final String DATABASE_WRAPPER = Constants.DATABASE_WRAPPER_DATABASE_MOCK;
    public static final String DATABASE_WRAPPER = Constants.DATABASE_WRAPPER_JSON_HANDLER;
    public static String exportFileName = "CCCExport.json";
    public static String userSettingsFileName = "UserSettings.json";
}
