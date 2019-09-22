package de.juliusawen.coastercreditcounter.application;

import android.util.Log;

@SuppressWarnings("FieldCanBeLocal") //
public class AppConfig
{
    private final String DATABASE_MOCK = Constants.DATABASE_WRAPPER_DATABASE_MOCK;
    private final String JSON_HANDLER = Constants.DATABASE_WRAPPER_JSON_HANDLER;

    private final String databaseWrapperToUse = JSON_HANDLER;

    private final String contentFileName = "CoasterCreditCounterExport.json";
    private final String settingsFileName = "Settings.json";


    private final boolean isDebugBuild = true;

    //below is just working when isDebugBuild = true

    private final boolean useExternalStorage = true;
    private final boolean alwaysImportFromDatabaseMock = false;
    private final boolean createExportFileIfNonexistent = true;
    private final boolean useDefaultContentFromDatabaseMockOnStartup = false;

    //above is just working when isDebugBuild = true


    private final boolean validateContent = true;

    public final String dateFormat = "d. MMMM yyyy";
    public final String yearFormat = "yyyy";
    public final String timeFormat = "HH:mm";


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
                this.createExportFileIfNotExists(),
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

    public boolean alwaysImportFromDatabaseMock()
    {
        return this.isDebugBuild && this.alwaysImportFromDatabaseMock;
    }

    public boolean createExportFileIfNotExists()
    {
        return this.isDebugBuild && this.createExportFileIfNonexistent;
    }

    public boolean useDefaultContentFromDatabaseMockOnStartup()
    {
        return this.isDebugBuild && this.useDefaultContentFromDatabaseMockOnStartup;
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
