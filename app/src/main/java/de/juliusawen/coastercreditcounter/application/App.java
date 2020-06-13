package de.juliusawen.coastercreditcounter.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import de.juliusawen.coastercreditcounter.persistence.Persistence;
import de.juliusawen.coastercreditcounter.tools.ExceptionHandler;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.activities.MainActivity;

public class App extends Application
{
    public static boolean isInitialized;

    public static Persistence persistence;
    public static Content content;

    //TODO: use SharedPreferences
    public static AppConfig config;
    public static Preferences preferences;

    private static Application instance;

    public static Context getContext()
    {
        return App.instance.getApplicationContext();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.frame(LogLevel.INFO, "creating...", '#', true);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, MainActivity.class));

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

    public static void terminate()
    {
        ActivityManager activityManager = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null)
        {
            for(ActivityManager.AppTask appTask : activityManager.getAppTasks())
            {
                Log.frame(LogLevel.INFO, String.format("trying to finishAndRemoveTask [%s]", appTask.toString()), '-', true);
                appTask.finishAndRemoveTask();
            }
        }
        else
        {
            Log.frame(LogLevel.INFO, "not able to get ActivityManager - exiting system with -1", '-', true);
            System.exit(-1);
        }
    }
}