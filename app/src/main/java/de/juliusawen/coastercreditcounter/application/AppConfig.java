package de.juliusawen.coastercreditcounter.application;

import android.util.Log;

@SuppressWarnings("FieldCanBeLocal") // Want this stuff up here for better overview
public class AppConfig
{
    private final String DATABASE_MOCK = Constants.DATABASE_WRAPPER_DATABASE_MOCK;
    private final String JSON_HANDLER = Constants.DATABASE_WRAPPER_JSON_HANDLER;

    private final String databaseWrapperToUse = JSON_HANDLER;

    private final String contentFileName = "CoasterCreditCounterExport.json";
    private final String settingsFileName = "Settings.json";


    private final boolean isDebugBuild = true;

    //below is just working when isDebugBuild = true

    private final boolean useExternalStorage = true; // use external file location accessable to user to export file to? - default true
    private final boolean createExportFileIfNonexistent = true; // create export.json file if it does not exist? - default true
    private final boolean alwaysImportFromDatabaseMock = false; // import from database mock instead of export.json file on startup? - default false
    private final boolean useDefaultContentFromDatabaseMockOnStartup = false; // // use mocked default content on startup? - default false

    private final boolean useDefaultSettingsOnStartup = false; // use default settings on startup? - default false
    private final boolean saveDefaultSettingsOnStartup = false; // save default settings to settings.json on startup? - default false

    private final boolean createAllDefaultsOnStartup = false; // create defaults on startup? - overrides useDefaultContentFromDatabaseMock, useDefaultSettings and saveDefaultSettings! - default false

    //above is just working when isDebugBuild = true


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
                        "contentFileName [%s]\n" +
                        "settingsFileName [%s]\n" +
                        "isDebugBuild [%S]\n" +
                        "useExternalStorage [%S]\n" +
                        "createExportFileIfNonexistent [%S]\n" +
                        "useDefaultContentFromDatabaseMockOnStartup [%S]\n" +
                        "validateContent [%S]\n" +
                        Constants.LOG_DIVIDER,

                this.databaseWrapperToUse(),
                this.getContentFileName(),
                this.getSettingsFileName(),
                this.isDebugBuild(),
                this.useExternalStorage(),
                this.createExportFileIfNonexistant(),
                this.useDefaultContentFromDatabaseMockOnStartup(),
                this.validateContent()
        );
    }

    public String databaseWrapperToUse()
    {
        return this.databaseWrapperToUse;
    }

    public String getContentFileName()
    {
        return this.contentFileName;
    }

    public String getSettingsFileName()
    {
        return this.settingsFileName;
    }

    public boolean isDebugBuild()
    {
        return this.isDebugBuild;
    }

    public boolean useExternalStorage()
    {
        return this.isDebugBuild && this.useExternalStorage;
    }

    public boolean createExportFileIfNonexistant()
    {
        return this.isDebugBuild && this.createExportFileIfNonexistent;
    }

    public boolean alwaysImportFromDatabaseMock()
    {
        return this.isDebugBuild && this.alwaysImportFromDatabaseMock;
    }

    public boolean useDefaultContentFromDatabaseMockOnStartup()
    {
        return this.isDebugBuild && (this.useDefaultContentFromDatabaseMockOnStartup || this.createAllDefaultsOnStartup);
    }

    public boolean useDefaultSettingsOnStartup()
    {
        return this.isDebugBuild && (this.useDefaultSettingsOnStartup || this.createAllDefaultsOnStartup);
    }

    public boolean saveDefaultSettingsOnStartup()
    {
        return this.isDebugBuild && (this.saveDefaultSettingsOnStartup || this.createAllDefaultsOnStartup);
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
