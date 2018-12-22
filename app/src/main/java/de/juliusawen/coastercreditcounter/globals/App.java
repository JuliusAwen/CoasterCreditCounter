package de.juliusawen.coastercreditcounter.globals;

import android.app.Application;
import android.content.Context;

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
}