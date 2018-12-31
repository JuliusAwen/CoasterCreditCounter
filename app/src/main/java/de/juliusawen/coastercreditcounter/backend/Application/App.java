package de.juliusawen.coastercreditcounter.backend.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import de.juliusawen.coastercreditcounter.backend.persistency.Persistency;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;

public class App extends Application
{
    public static boolean isInitialized;
    public static boolean DEBUG;

    public static Persistency persistency;
    public static Content content;

    public static AppConfig config;
    public static Settings settings;

    private static Application instance;

    public static Application getInstance()
    {
        return App.instance;
    }

    public static Context getContext()
    {
        return getInstance().getApplicationContext();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        App.instance = this;
        App.config = new AppConfig();
        App.DEBUG = App.config.isDebugBuild();
        App.persistency = Persistency.getInstance();
    }

    public static boolean initialize()
    {
//                    try
//                    {
//                        Thread.sleep(2000);
//                    }
//                    catch(InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }

        App.content = Content.getInstance(App.persistency);
        App.settings = Settings.getInstance(App.persistency);

        Log.i(Constants.LOG_TAG, "App.initialize:: initializing <Content> and <Settings>...");

        if(App.content.initialize() && App.settings.initialize())
        {
            App.isInitialized = true;
            return true;
        }
        else
        {
            return false;
        }
    }

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

        private AppConfig()
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

        private boolean isDebugBuild()
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
}