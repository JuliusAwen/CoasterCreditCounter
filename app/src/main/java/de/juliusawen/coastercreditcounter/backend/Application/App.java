package de.juliusawen.coastercreditcounter.backend.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.juliusawen.coastercreditcounter.backend.persistency.Persistence;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;

public class App extends Application
{
    public static boolean isInitialized = false;
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
        App.instance = this;
        App.config = new AppConfig();
        App.DEBUG = App.config.isDebugBuild();
        App.persistence = Persistence.getInstance();
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

        App.content = Content.getInstance(App.persistence);
        App.settings = Settings.getInstance(App.persistence);

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
}