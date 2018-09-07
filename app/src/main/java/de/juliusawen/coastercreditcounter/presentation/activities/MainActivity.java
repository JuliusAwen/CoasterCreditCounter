package de.juliusawen.coastercreditcounter.presentation.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.presentation.activities.locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class MainActivity extends AppCompatActivity
{
    private Content content;

    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "MainActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_app_name));

        this.progressBar = findViewById(R.id.linearLayoutProgressBar);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Log.i(Constants.LOG_TAG, String.format("MainActivity.onResume:: ContentState.isInitialized[%S]", Content.ContentState.isInitialized));
        if(Content.ContentState.isInitialized)
        {
            this.content = Content.getInstance();
            this.startHubActivity();
        }
        else
        {
            Log.i(Constants.LOG_TAG, "MainActivity.onResume:: fetching content...");
            new FetchContent().execute();
        }
    }

    private void startHubActivity()
    {
        Log.i(Constants.LOG_TAG, "MainActivity.startHubActivity:: starting activty...");

        Intent intent = new Intent(MainActivity.this, ShowLocationsActivity.class);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.content.getRootElement().getUuid().toString());
        startActivity(intent);
    }

    private class FetchContent extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params)
        {

//        try
//        {
//            Thread.sleep(5000);
//        }
//        catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }


            content = Content.getInstance();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.GONE);
            startHubActivity();
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }
}
