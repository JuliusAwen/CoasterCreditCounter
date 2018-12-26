package de.juliusawen.coastercreditcounter.globals;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.juliusawen.coastercreditcounter.data.persistency.Persistency;

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
        boolean success = App.content.initialize() && App.settings.initialize();

        if(success)
        {
            App.isInitialized = true;
        }

        return success;
    }

    public class AppConfig
    {
        private final String DATABASE_MOCK = Constants.DATABASE_WRAPPER_DATABASE_MOCK;
        private final String JSON_HANDLER = Constants.DATABASE_WRAPPER_JSON_HANDLER;


        private final boolean isDebugBuild = true;
        private final String DATABASE_WRAPPER = DATABASE_MOCK;
        private final boolean jumpToTestActivityOnStart = false;


        private final String contentFileName = "CoasterCreditCounterExport.json";
        private final String settingsFileName = "Settings.json";

        private AppConfig()
        {
            Log.i(Constants.LOG_TAG,"AppConfig.Constructor:: <AppConfig> instantiated");
        }

        public boolean isDebugBuild()
        {
            return this.isDebugBuild;
        }

        public String databaseWrapperToUse()
        {
            return this.DATABASE_WRAPPER;
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
    }
}