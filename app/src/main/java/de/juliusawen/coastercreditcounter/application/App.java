package de.juliusawen.coastercreditcounter.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import de.juliusawen.coastercreditcounter.persistence.Persistence;

public class App extends Application
{
    public static boolean isInitialized;
    public static boolean DEBUG;

    public static Persistence persistence;
    public static Content content;

    //TODO: use SharedPreferences
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
        Log.i(Constants.LOG_TAG, "App.onCreate:: creating app...");

        super.onCreate();

        if(LeakCanary.isInAnalyzerProcess(this))
        {
            return;
        }
        LeakCanary.install(this);

        App.isInitialized = false;

        App.instance = this;

        App.config = new AppConfig();
        App.DEBUG = App.config.isDebugBuild();

        App.persistence = Persistence.getInstance();
    }

    public static boolean initialize()
    {
        Log.i(Constants.LOG_TAG, "App.initialize:: initializing <Settings> and <Content>...");

        App.settings = Settings.getInstance(App.persistence);
        App.content = Content.getInstance(App.persistence);

        if(App.settings.initialize() && App.content.initialize())
        {
            App.isInitialized = true;
            return true;
        }
        else
        {
            return false;
        }
    }
}