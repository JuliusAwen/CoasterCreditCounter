package de.juliusawen.coastercreditcounter.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Settings;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "MainActivity.onCreate:: creating activity...");

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

        Log.i(Constants.LOG_TAG, String.format("MainActivity.onResume:: App.isInitialized[%S]", App.isInitialized));
        if(App.isInitialized)
        {
            if(Settings.jumpToTestActivityOnStart)
            {
                Log.e(Constants.LOG_TAG, "MainActivity.onResume:: starting TestActivity");
                startActivity(new Intent(this, TestActivity.class));
            }
            else
            {
                Log.i(Constants.LOG_TAG, "MainActivity.onResume:: starting HubActivity");


                Log.e(Constants.LOG_TAG, "MainActivity.onResume:: HubActivity not available atm - staring ShowLocationsActivity<root> instead");
                ActivityTool.startActivityShow(this, Constants.REQUEST_SHOW_LOCATION, App.content.getRootLocation());
            }
        }
        else
        {
            Log.i(Constants.LOG_TAG, "MainActivity.onResume:: app needs to initialize");
            App.initialize(this);
        }
    }
}
