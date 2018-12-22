package de.juliusawen.coastercreditcounter.globals;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.persistency.Persistency;

public class App extends Application
{
    public static boolean isInitialized = false;

    public static Persistency persistency;
    public static Content content;
    public static UserSettings userSettings;

    @SuppressLint("StaticFieldLeak")
    private static View progressBar;
    @SuppressLint("StaticFieldLeak")
    private static Context activityContext;

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
        application = this;
    }

    public static void initialize(Context context)
    {
        Log.i(Constants.LOG_TAG, "App.initialize:: initializing app...");

        App.activityContext = context;

        App.persistency = Persistency.getInstance();

        ViewGroup viewGroup = ((Activity)context).findViewById(android.R.id.content);
        App.progressBar = ((Activity)context).getLayoutInflater().inflate(R.layout.progress_bar, viewGroup, false);
        viewGroup.addView(App.progressBar);

        new Initialize().execute();
    }

    private static class Initialize extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            Log.i(Constants.LOG_TAG, "App.Initialize.onPreExecute:: preparing initialization...");

            super.onPreExecute();
            App.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Log.i(Constants.LOG_TAG, "App.Initialize.doInBackground:: getting instance of <Content>...");
            App.content = Content.getInstance(App.persistency);
            App.content.initialize();

            Log.i(Constants.LOG_TAG, "App.Initialize.doInBackground:: getting instance of <UserSettings>...");
            App.userSettings = UserSettings.getInstance(App.persistency);
            App.userSettings.initialize();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.i(Constants.LOG_TAG, "App.Initialize.onPostExecute:: finishing initialization...");

            super.onPostExecute(aVoid);

            Log.i(Constants.LOG_TAG, "App.Initialize.onPostExecute:: restarting calling activity...");
            App.activityContext.startActivity(new Intent(App.activityContext, ((Activity) activityContext).getClass()));

            App.progressBar.setVisibility(View.GONE);

            App.progressBar = null;
            App.activityContext = null;
            App.isInitialized = true;
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }
}
