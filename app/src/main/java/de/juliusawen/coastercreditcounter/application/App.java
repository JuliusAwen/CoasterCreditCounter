package de.juliusawen.coastercreditcounter.application;

import android.app.Application;
import android.content.Context;

import de.juliusawen.coastercreditcounter.persistence.Persistence;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class App extends Application
{
    public static boolean isInitialized;

    public static Persistence persistence;
    public static Content content;

    //TODO: use SharedPreferences
    public static AppConfig config;
    public static Preferences preferences;

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
        Log.frame(LogLevel.INFO, "instantiated", '#', true);
        super.onCreate();

        App.isInitialized = false;
        App.instance = this;
        App.config = new AppConfig();
        App.persistence = Persistence.getInstance();
    }

    public static boolean initialize()
    {
        Log.i("initializing <Preferences> and <Content>...");

        if(!App.config.logDetailsOnStartup)
        {
            Log.restrictLogging(LogLevel.INFO, "avoiding log spam during initialization");
        }

        App.preferences = Preferences.getInstance(App.persistence);
        App.content = Content.getInstance(App.persistence);

        boolean success = false;
        if(App.preferences.initialize() && App.content.initialize())
        {
            App.isInitialized = true;
            success = true;
        }

        Log.restrictLogging(LogLevel.NONE, "initialization done");

        return success;
    }
}