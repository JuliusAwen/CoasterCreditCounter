package de.juliusawen.coastercreditcounter.presentation.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.presentation.activities.Locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_app_name));
        setSupportActionBar(toolbar);

        new OnFetchContent().execute();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        menu.add(0, 1, Menu.NONE, R.string.selection_locations);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == 1)
        {
            Intent intent = new Intent(this, ShowLocationsActivity.class);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, Content.getInstance().getRootElement().getUuid().toString());
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class OnFetchContent extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Content");
            progressDialog.setMessage("fetching...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Content.getInstance();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            Intent intent = new Intent(MainActivity.this, ShowLocationsActivity.class);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, Content.getInstance().getRootElement().getUuid().toString());
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
        }
    }
}
