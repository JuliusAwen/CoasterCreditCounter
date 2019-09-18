package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;

public class MainActivity extends AppCompatActivity//BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "MainActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            ActivityDistributor.startActivityViaClass(this, NavigationHubActivity.class);
            finish();
        }
    }

//    protected void create()
//    {
//        ActivityDistributor.startActivityViaClass(this, NavigationHubActivity.class);
//        finish();
//    }
}
