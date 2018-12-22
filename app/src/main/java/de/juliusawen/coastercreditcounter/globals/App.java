package de.juliusawen.coastercreditcounter.globals;

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

        App.persistency = Persistency.getInstance();

        ViewGroup viewGroup = ((Activity)context).findViewById(android.R.id.content);
        View progressBar = ((Activity)context).getLayoutInflater().inflate(R.layout.progress_bar, viewGroup, false);
        viewGroup.addView(progressBar);


        Object[] params = new Object[2];
        params[0] = context;
        params[1] = progressBar;

        new Initialize().execute(params);
    }

    private static class Initialize extends AsyncTask<Object, Void, Object[]>
    {
        @Override
        protected void onPreExecute()
        {
            Log.i(Constants.LOG_TAG, "App.Initialize.onPreExecute:: preparing initialization...");

            super.onPreExecute();
        }

        @Override
        protected Object[] doInBackground(Object... params)
        {
            View progressBar = (View) params[1];

            progressBar.setVisibility(View.VISIBLE);

            Log.i(Constants.LOG_TAG, "App.Initialize.doInBackground:: getting instance of <Content>...");
            App.content = Content.getInstance(App.persistency);
            App.content.initialize();

            Log.i(Constants.LOG_TAG, "App.Initialize.doInBackground:: getting instance of <UserSettings>...");
            App.userSettings = UserSettings.getInstance(App.persistency);
            App.userSettings.initialize();

            return params;
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            Log.i(Constants.LOG_TAG, "App.Initialize.onPostExecute:: finishing initialization...");

            super.onPostExecute(params);

            Context context = (Context) params[0];
            View progressBar = (View) params[1];

            Log.i(Constants.LOG_TAG, "App.Initialize.onPostExecute:: restarting calling activity...");
            context.startActivity(new Intent(context, ((Activity) context).getClass()));

            progressBar.setVisibility(View.GONE);

            App.isInitialized = true;
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }
}
