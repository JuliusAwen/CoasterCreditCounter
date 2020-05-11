package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.tools.ExceptionHandler;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "MainActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, MainActivity.class));
        ActivityDistributor.startActivityViaClass(this, NavigationHubActivity.class);
        finish();
    }
}
