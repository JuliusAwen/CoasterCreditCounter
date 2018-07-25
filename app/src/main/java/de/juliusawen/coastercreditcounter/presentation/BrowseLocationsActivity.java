package de.juliusawen.coastercreditcounter.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Location;

public class BrowseLocationsActivity extends AppCompatActivity implements View.OnClickListener
{
    Location currentLocation;
    List<Location> recentLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_locations);

        this.currentLocation = Content.getInstance().locationRoot;

        this.refreshViews();
    }

    private void refreshViews()
    {
        this.recentLocations.add(this.currentLocation);

        LinearLayout linearLayoutActivity = findViewById(R.id.linearLayoutBrowseLocations);
        linearLayoutActivity.invalidate();
        linearLayoutActivity.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.layout_browse_locations, linearLayoutActivity, false);
        linearLayoutActivity.addView(view);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.text_browse_locations));
        setSupportActionBar(toolbar);

        LinearLayout linearLayoutNavigationBar = view.findViewById(R.id.linearLayoutNavigationBar);
        this.addNavigationBar(linearLayoutNavigationBar);

        LinearLayout linearLayoutContentBar = view.findViewById(R.id.linearLayoutContentBar);
        this.addContentButtons(linearLayoutContentBar);
    }

    private void addNavigationBar(LinearLayout linearLayout)
    {
        for (Location location : this.recentLocations)
        {
            View view = getLayoutInflater().inflate(R.layout.button_no_border, linearLayout, false);
            Button button = view.findViewById(R.id.buttonNoBorder);

            button.setText(getString(R.string.button_text_back, location.getName()));
            button.setId(Constants.BUTTON_BACK);
            button.setTag(location);

            button.setOnClickListener(this);

            if(location.equals(this.currentLocation))
            {
                button.setClickable(false);
            }

            linearLayout.addView(view);
        }
    }

    private void addContentButtons(LinearLayout linearLayout)
    {
        for(Location location : this.currentLocation.getChildren())
        {
            View view = getLayoutInflater().inflate(R.layout.button_standard, linearLayout, false);

            Button contentButton = view.findViewById(R.id.button);
            contentButton.setText(location.getName());
            contentButton.setTag(location);
            contentButton.setOnClickListener(this);
//            contentButton.setOnLongClickListener(this);

            linearLayout.addView(view);
        }
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
        }

        this.currentLocation = location;
        refreshViews();
    }
}
