package de.juliusawen.coastercreditcounter.globals;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import de.juliusawen.coastercreditcounter.globals.persistency.Persistency;

public class App extends Application
{
    public static boolean isInitialized = false;

    public static Persistency persistency;
    public static Content content;
    public static UserSettings userSettings;

    private static Application application;

    public static Application getApplication()
    {
        return App.application;
    }

    public static Context getContext()
    {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        App.application = this;
        App.persistency = Persistency.getInstance();
    }

    public static boolean initialize()
    {
                    try
                    {
                        Thread.sleep(2000);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }

        App.content = Content.getInstance(App.persistency);
        App.userSettings = UserSettings.getInstance(App.persistency);

        Log.i(Constants.LOG_TAG, "App.initialize:: initializing <Content> and <UserSettings>...");
        return App.content.initialize() && App.userSettings.initialize();
    }
}