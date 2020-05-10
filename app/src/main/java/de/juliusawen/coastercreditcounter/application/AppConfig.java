package de.juliusawen.coastercreditcounter.application;

import android.util.Log;

import de.juliusawen.coastercreditcounter.BuildConfig;

@SuppressWarnings("FieldCanBeLocal") // Want this stuff up here for better overview
public class AppConfig
{
    private final String DATABASE_MOCK = Constants.DATABASE_WRAPPER_DATABASE_MOCK;
    private final String JSON_HANDLER = Constants.DATABASE_WRAPPER_JSON_HANDLER;

    private final String databaseWrapperToUse = JSON_HANDLER;

    private final String exportFileName = "CoasterCreditCounterExport.json";
    private final String preferencesFileName = "Preferences.json";

    //below is only working when isDebugBuild = true

    private final boolean resetToDefaultContentOnStartup = false; // // use mocked default content on startup? (OVERRIDES existing content!)
    private final boolean resetToDefaultPreferencesOnStartup = false; // use default preferences on startup? (OVERWRITES existing preference file!)
    private final boolean alwaysImportFromDatabaseMock = false; // always import from database mock instead of export.json file?

    //above is only working when isDebugBuild = true


    private final boolean validateContent = true; // default true

    public final String dateFormat = "d. MMMM yyyy";
    public final String yearFormat = "yyyy";
    public final String timeFormat = "HH:mm:ss";

    public final int maxCharacterCountForSimpleElementName = 64;
    public final int maxDigitCount = 9;

    public final int maxCharacterCountForShortenedText = 29;
    public final int maxCharacterCountForNote = 512;
    public final int minLinesForNote = 11;
    public final int maxLinesForNote = 11;

    public final int maxHeightForNoteInDP = 120;


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
                        "resetToDefaultContentOnStartup [%S]\n" +
                        "resetToDefaultPreferencesOnStartup [%S]\n" +
                        "alwaysImportFromDatabaseMock [%S]\n" +
                        "validateContent [%S]\n" +
                        Constants.LOG_DIVIDER,

                this.databaseWrapperToUse(),
                this.getExportFileName(),
                this.getPreferencesFileName(),
                this.isDebugBuild(),
                this.resetToDefaultContentOnStartup(),
                this.resetToDefaultPreferencesOnStartup(),
                this.alwaysImportFromDatabaseMock(),
                this.validateContent()
        );
    }

    public String databaseWrapperToUse()
    {
        return this.databaseWrapperToUse;
    }

    public String getExportFileName()
    {
        return this.exportFileName;
    }

    public String getPreferencesFileName()
    {
        return this.preferencesFileName;
    }

    public boolean isDebugBuild()
    {
        return BuildConfig.DEBUG;
    }

    public boolean alwaysImportFromDatabaseMock()
    {
        return BuildConfig.DEBUG && this.alwaysImportFromDatabaseMock;
    }

    public boolean resetToDefaultContentOnStartup()
    {
        return BuildConfig.DEBUG && (this.resetToDefaultContentOnStartup);
    }

    public boolean resetToDefaultPreferencesOnStartup()
    {
        return BuildConfig.DEBUG && (this.resetToDefaultPreferencesOnStartup);
    }

    public boolean validateContent()
    {
        return BuildConfig.DEBUG && this.validateContent;
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
