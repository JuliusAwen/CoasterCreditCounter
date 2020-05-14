package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.tools.ExceptionHandler;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.frame(LogLevel.INFO, "creating...", '#', false);

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this, MainActivity.class));
        ActivityDistributor.startActivityViaClass(this, NavigationHubActivity.class);
        finish();
    }
}
