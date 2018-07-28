package de.juliusawen.coastercreditcounter.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.Multitool;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;

public class BrowseLocationsActivity extends AppCompatActivity implements View.OnClickListener//, View.OnLongClickListener
{
    Location currentLocation = Content.getInstance().getLocationRoot();
    List<Location> recentLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_locations);

        this.refreshViews();
    }

    private void refreshViews()
    {
        LinearLayout linearLayoutActivity = findViewById(R.id.linearLayoutBrowseLocations);
        linearLayoutActivity.invalidate();
        linearLayoutActivity.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.layout_browse_locations, linearLayoutActivity, false);
        linearLayoutActivity.addView(view);

        this.addToolbar(view);
        this.addNavigationBar(view);
        this.addContentButtons(view);
    }

    private void addToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_browse_locations));
        toolbar.setSubtitle(this.currentLocation.getName());

        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(this.currentLocation.getChildren().size() > 1)
        {
            getMenuInflater().inflate(R.menu.toolbar_items, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void addNavigationBar(View view)
    {
        if(this.currentLocation.getParent() != null && !this.recentLocations.contains(this.currentLocation.getParent()))
        {
            this.recentLocations.add(this.currentLocation.getParent());
        }

        LinearLayout linearLayoutNavigationBar = view.findViewById(R.id.linearLayoutNavigationBar);

        for (Location location : this.recentLocations)
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_no_border, linearLayoutNavigationBar, false);

            Button button = buttonView.findViewById(R.id.buttonNoBorder);
            button.setText(getString(R.string.button_text_back, location.getName()));
            button.setId(Constants.BUTTON_BACK);
            button.setTag(location);
            button.setOnClickListener(this);

            linearLayoutNavigationBar.addView(buttonView);
        }
    }

    private void addContentButtons(View view)
    {
        LinearLayout linearLayoutContentBar = view.findViewById(R.id.linearLayoutContentContainer);

        for(Location location : this.currentLocation.getChildren())
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_standard, linearLayoutContentBar, false);

            Button contentButton = buttonView.findViewById(R.id.button);
            contentButton.setText(location.getName());
            contentButton.setTag(location);
            contentButton.setOnClickListener(this);
//            contentButton.setOnLongClickListener(this);

            linearLayoutContentBar.addView(buttonView);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().convertToUuidStringArrayList(this.recentLocations));
        outState.putString(Constants.KEY_ELEMENT, this.currentLocation.getUuid().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.recentLocations = Content.getInstance().getLocationsFromUuidStringArrayList(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentLocation = (Location) Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));

        this.refreshViews();
    }

    @Override
    public void onClick(View view)
    {
        @SuppressWarnings("unchecked")
        Location location = (Location) view.getTag();

        if(view.getId() == Constants.BUTTON_BACK)
        {
            int length =  this.recentLocations.size()-1;
            for (int i = length; i >= 0  ; i--)
            {
                if(this.recentLocations.get(i).equals(location))
                {
                    this.recentLocations.remove(i);
                    break;
                }
                else
                {
                    this.recentLocations.remove(i);
                }
            }

            this.currentLocation = location;

        }
        else if(location.getClass().equals(Park.class))
        {
            //TODO: start ShowParkActivity
            Multitool.makeToast(this, location.getName() + ": start ShowParkActivity");
        }
        else
        {
            this.currentLocation = location;
        }

        refreshViews();
    }

//    @Override
//    public boolean onLongClick(View view)
//    {
//        Multitool.makeToast(this, ((Element) view.getTag()).getName() + ": long clicked");
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.actionSettingsSort:
            {
                Intent intent = new Intent(this, SortElementsActivity.class);
                intent.putExtra(Constants.EXTRA_UUID, this.currentLocation.getUuid().toString());
                startActivity(intent);

                return true;
            }

            default:
            {
                Multitool.makeToast(this, "onOptionsItemSelected --> default...!?");

                return super.onOptionsItemSelected(item);
            }
        }
    }
}
