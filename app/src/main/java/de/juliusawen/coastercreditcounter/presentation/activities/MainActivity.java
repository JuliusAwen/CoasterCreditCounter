package de.juliusawen.coastercreditcounter.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.activities.locations.ShowLocationsActivity;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "MainActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_app_name));
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        setIntent(new Intent(MainActivity.this, ShowLocationsActivity.class));

        Log.i(Constants.LOG_TAG, String.format("MainActivity.onResume:: App.isInitialized[%S]", App.isInitialized));
        if(App.isInitialized)
        {
            getIntent().putExtra(Constants.EXTRA_ELEMENT_UUID, App.content.getRootLocation().getUuid().toString());
            Log.i(Constants.LOG_TAG, String.format("MainActivity.onResume:: starting activty [%s]...",
                    StringTool.parseActivityName(Objects.requireNonNull(getIntent().getComponent()).getShortClassName())));
            startActivity(getIntent());
        }
        else
        {
            Log.i(Constants.LOG_TAG, "MainActivity.onResume:: initializing app...");
            App.initialize(this, this);
        }
    }
}
