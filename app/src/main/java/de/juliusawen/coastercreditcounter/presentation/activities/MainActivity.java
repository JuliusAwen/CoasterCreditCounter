package de.juliusawen.coastercreditcounter.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.Content;

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

        Intent intent = new Intent(this, BrowseLocationsActivity.class);
        intent.putExtra(Constants.EXTRA_UUID, Content.getInstance().getLocationRoot().getUuid().toString());
        startActivity(intent);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        menu.add(0, 1, Menu.NONE, "Manage Locations");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == 1)
        {
            Intent intent = new Intent(this, BrowseLocationsActivity.class);
            intent.putExtra(Constants.EXTRA_UUID, Content.getInstance().getLocationRoot().getUuid().toString());
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
