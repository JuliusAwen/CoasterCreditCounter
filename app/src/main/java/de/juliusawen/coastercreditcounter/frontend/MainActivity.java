package de.juliusawen.coastercreditcounter.frontend;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.frontend.navigationHub.NavigationHubActivity;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "MainActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        ActivityTool.startActivity(this, NavigationHubActivity.class);
    }
}
