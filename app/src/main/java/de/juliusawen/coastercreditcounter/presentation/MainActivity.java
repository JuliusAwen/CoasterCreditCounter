package de.juliusawen.coastercreditcounter.presentation;

import android.os.Bundle;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.presentation.navigationHub.NavigationHubActivity;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class MainActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            ActivityTool.startActivity(this, NavigationHubActivity.class);
        }
    }
}
