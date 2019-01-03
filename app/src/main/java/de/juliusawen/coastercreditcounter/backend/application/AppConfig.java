package de.juliusawen.coastercreditcounter.backend.application;

import android.util.Log;

import androidx.annotation.NonNull;
import de.juliusawen.coastercreditcounter.globals.Constants;

@SuppressWarnings("FieldCanBeLocal")
public class AppConfig
{
    private final String DATABASE_MOCK = Constants.DATABASE_WRAPPER_DATABASE_MOCK;
    private final String JSON_HANDLER = Constants.DATABASE_WRAPPER_JSON_HANDLER;

    private final String databaseWrapperToUse = JSON_HANDLER;

    private final boolean isDebugBuild = true;
    private final boolean useExternalStorage = true;
    private final boolean reinitializeContentFromDatabaseMock = false;
    private final boolean createExportFileIfNotExists = true;

    private final boolean validateContent = true;

    private final boolean jumpToTestActivityOnStart = false;

    private final String contentFileName = "CoasterCreditCounterExport.json";
    private final String settingsFileName = "Settings.json";

    AppConfig()
    {
        Log.i(Constants.LOG_TAG, "AppConfig.Constructor:: <AppConfig> instantiated");
        Log.i(Constants.LOG_TAG, String.format("AppConfig.Constructor:: Configuration: \n%s", this));
    }

    @NonNull
    @Override
    public String toString()
    {
        return String.format(
                Constants.LOG_DIVIDER + "\n" +
                        "databaseWrapperToUse [%s]\n" +
                        "isDebugBuild [%S]\n" +
                        "useExternalStorage [%S]\n" +
                        "reinitializeContentFromDatabaseMock [%S]\n" +
                        "createExportFileIfNotExists [%S]\n" +
                        "validateContent [%S]\n" +
                        "jumpToTestActivityOnStart [%S]\n" +
                        "contentFileName [%s]\n" +
                        "settingsFileName [%s]\n" +
                        Constants.LOG_DIVIDER,
                this.databaseWrapperToUse(),
                this.isDebugBuild(),
                this.useExternalStorage(),
                this.reinitializeContentFromDatabaseMock(),
                this.createExportFileIfNotExists(),
                this.validateContent(),
                this.jumpToTestActivityOnStart(),
                this.getContentFileName(),
                this.getSettingsFileName()
        );
    }

    boolean isDebugBuild()
    {
        return this.isDebugBuild;
    }

    public String databaseWrapperToUse()
    {
        return this.databaseWrapperToUse;
    }

    public boolean jumpToTestActivityOnStart()
    {
        return this.jumpToTestActivityOnStart;
    }

    public String getContentFileName()
    {
        return this.contentFileName;
    }

    public String getSettingsFileName()
    {
        return this.settingsFileName;
    }

    public boolean createExportFileIfNotExists()
    {
        return createExportFileIfNotExists;
    }

    public boolean useExternalStorage()
    {
        return useExternalStorage;
    }

    public boolean validateContent()
    {
        return this.validateContent;
    }

    public boolean reinitializeContentFromDatabaseMock()
    {
        return reinitializeContentFromDatabaseMock;
    }
}
