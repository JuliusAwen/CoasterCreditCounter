package de.juliusawen.coastercreditcounter.application;

import android.util.Log;

@SuppressWarnings("FieldCanBeLocal") // Want this stuff up here for better overview
public class AppConfig
{
//    private final String DATABASE_MOCK = Constants.DATABASE_WRAPPER_DATABASE_MOCK;
    private final String JSON_HANDLER = Constants.DATABASE_WRAPPER_JSON_HANDLER;

    private final String databaseWrapperToUse = JSON_HANDLER;

    private final String contentExportFileName = "CoasterCreditCounterExport.json";
    private final String preferencesFileName = "Preferences.json";


    private final boolean isDebugBuild = true;

    //below is only working when isDebugBuild = true

    private final boolean useLeakCanary = false;
    private final boolean useExternalStorage = true; // use external file location accessable to user?
    private final boolean createExportFileWithDefaultsIfNotFound = true; // create export.json file if it does not exist?
    private final boolean alwaysLoadFromDatabaseMock = false; // always load from database mock instead of export.json file?
    private final boolean resetToDefaultContentOnStartup = true; // // use mocked default content on startup? (OVERRIDES existing content!)
    private final boolean resetToDefaultPreferencesOnStartup = false; // use default preferences on startup? (OVERWRITES existing preference file!)

    //above is only working when isDebugBuild = true


    private final boolean validateContent = true; // default true

    public final String dateFormat = "d. MMMM yyyy";
    public final String yearFormat = "yyyy";
    public final String timeFormat = "HH:mm:ss";


    AppConfig()
    {
        Log.i(Constants.LOG_TAG, "AppConfig.Constructor:: <AppConfig> instantiated");
        Log.i(Constants.LOG_TAG, String.format("AppConfig.Constructor:: Configuration: \n%s", this));
    }

    @Override
    public String toString()
    {
        return String.format(
                Constants.LOG_DIVIDER + "\n" +
                        "databaseWrapperToUse [%s]\n" +
                        "contentExportFileName [%s]\n" +
                        "preferencesFileName [%s]\n" +
                        "isDebugBuild [%S]\n" +
                        "useLeakCanary [%S]\n" +
                        "useExternalStorage [%S]\n" +
                        "alwaysLoadFromDatabaseMock [%S]\n" +
                        "createExportFileWithDefaultsIfNonexistent [%S]\n" +
                        "useDefaultContentFromDatabaseMockOnStartup [%S]\n" +
                        "useDefaultPreferencesOnStartup [%S]\n" +
                        "validateContent [%S]\n" +
                        Constants.LOG_DIVIDER,

                this.databaseWrapperToUse(),
                this.getContentExportFileName(),
                this.getPreferencesFileName(),
                this.isDebugBuild(),
                this.useLeakCanary(),
                this.useExternalStorage(),
                this.alwaysLoadFromDatabaseMock(),
                this.createExportFileWithDefaultsIfNotFound(),
                this.resetTomDefaultContentOnStartup(),
                this.resetToDefaultPreferencesOnStartup(),
                this.validateContent()
        );
    }

    public String databaseWrapperToUse()
    {
        return this.databaseWrapperToUse;
    }

    public String getContentExportFileName()
    {
        return this.contentExportFileName;
    }

    public String getPreferencesFileName()
    {
        return this.preferencesFileName;
    }

    public boolean isDebugBuild()
    {
        return this.isDebugBuild;
    }

    public boolean useLeakCanary()
    {
        return this.isDebugBuild && this.useLeakCanary;
    }

    public boolean useExternalStorage()
    {
        return this.isDebugBuild && this.useExternalStorage;
    }

    public boolean createExportFileWithDefaultsIfNotFound()
    {
        return this.isDebugBuild && this.createExportFileWithDefaultsIfNotFound;
    }

    public boolean alwaysLoadFromDatabaseMock()
    {
        return this.isDebugBuild && this.alwaysLoadFromDatabaseMock;
    }

    public boolean resetTomDefaultContentOnStartup()
    {
        return this.isDebugBuild && (this.resetToDefaultContentOnStartup);
    }

    public boolean resetToDefaultPreferencesOnStartup()
    {
        return this.isDebugBuild && (this.resetToDefaultPreferencesOnStartup);
    }

    public boolean validateContent()
    {
        return this.isDebugBuild && this.validateContent;
    }

    public String getDateFormat()
    {
        return this.dateFormat;
    }

    public String getYearFormat()
    {
        return this.yearFormat;
    }

    public String getTimeFormat()
    {
        return this.timeFormat;
    }
}
