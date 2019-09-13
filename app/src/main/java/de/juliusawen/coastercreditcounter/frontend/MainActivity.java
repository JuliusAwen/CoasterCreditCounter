package de.juliusawen.coastercreditcounter.frontend;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.frontend.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.frontend.navigationHub.NavigationHubActivity;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "MainActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        ActivityDistributor.startActivityViaClass(this, NavigationHubActivity.class);
    }
}
