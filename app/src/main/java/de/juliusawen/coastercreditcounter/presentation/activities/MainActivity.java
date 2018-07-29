package de.juliusawen.coastercreditcounter.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.juliusawen.coastercreditcounter.R;

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


//        View buttonView = getLayoutInflater().inflate(R.layout.button_no_border, linearLayoutNavigationBar, false);

        Intent intent = new Intent(this, BrowseLocationsActivity.class);
        startActivity(intent);
    }
}
