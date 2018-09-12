package de.juliusawen.coastercreditcounter.globals;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public abstract class App
{
    public static boolean isInitialized = false;

    public static Content content;
    public static Settings settings;

    @SuppressLint("StaticFieldLeak")
    private static View progressBar;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static Intent intent;

    public static void initialize(Context context, Activity activity)
    {
        App.context = context;
        App.intent = activity.getIntent();

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        App.progressBar = activity.getLayoutInflater().inflate(R.layout.progress_bar, viewGroup, false);
        viewGroup.addView(App.progressBar);

        if(App.content == null)
        {
            new InitializeApp().execute();
        }
    }

    private static class InitializeApp extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            App.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            App.content = Content.getInstance();
            App.settings = Settings.getInstance();

            try
            {
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
            super.onPostExecute(aVoid);

            App.progressBar.setVisibility(View.GONE);

            Log.i(Constants.LOG_TAG, String.format("App.InitializeApp.onPostExecute:: starting activty [%s]...",
                    StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));
            App.intent.putExtra(Constants.EXTRA_ELEMENT_UUID, App.content.getRootLocation().getUuid().toString());
            App.context.startActivity(App.intent);

            App.context = null;
            App.intent = null;
            App.progressBar = null;

            App.isInitialized = true;
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }
}
