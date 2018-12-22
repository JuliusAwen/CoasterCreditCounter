package de.juliusawen.coastercreditcounter.presentation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        ActivityTool.startNavigationHubActivity(this);

    }
}
