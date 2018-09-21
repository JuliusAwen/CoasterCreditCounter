package de.juliusawen.coastercreditcounter.globals;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.presentation.activities.TestActivity;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public abstract class App
{
    public static boolean isInitialized = false;

    public static Content content;
    public static Settings settings;

    @SuppressLint("StaticFieldLeak")
    private static View progressBar;

    @SuppressLint("StaticFieldLeak")
    private static Activity activity;

    public static void initialize(Activity activity)
    {
        Log.i(Constants.LOG_TAG, "App.initialize:: initializing app...");

        App.activity = activity;

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        App.progressBar = activity.getLayoutInflater().inflate(R.layout.progress_bar, viewGroup, false);
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
            App.content = Content.getInstance();

            Log.i(Constants.LOG_TAG, "App.Initialize.doInBackground:: getting instance of <Settings>...");
            App.settings = Settings.getInstance();

            //Todo: remove sleep when tested
            try
            {
                Log.e(Constants.LOG_TAG, "App.Initialize.doInBackground:: sleeping for 1000ms in order to test progress bar...");
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.i(Constants.LOG_TAG, "App.Initialize.onPostExecute:: finishing initialization...");

            super.onPostExecute(aVoid);

            App.progressBar.setVisibility(View.GONE);
            App.progressBar = null;


            Visit.setSortOrder(App.settings.getDefaultSortOrderParkVisits());

            if(Settings.jumpToTestActivityOnStart)
            {
                Log.e(Constants.LOG_TAG, "App.Initialize.onPostExecute:: starting TestActivity");
                activity.startActivity(new Intent(activity, TestActivity.class));
            }
            else
            {
                if(Visit.validateOpenVisit() && App.settings.jumpToOpenVisitOnStart())
                {
                    Log.i(Constants.LOG_TAG, "App.Initialize.onPostExecute:: open visit found - showing visit");
                    ActivityTool.startActivityShow(App.activity, Visit.getOpenVisit());
                }
                else
                {
                    Log.i(Constants.LOG_TAG, "App.Initialize.onPostExecute:: showing root location");
                    ActivityTool.startActivityShow(App.activity, App.content.getRootLocation());
                }
            }


            App.activity = null;
            App.isInitialized = true;
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }
}
