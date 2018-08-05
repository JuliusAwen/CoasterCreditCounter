package de.juliusawen.coastercreditcounter.presentation.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import de.juliusawen.coastercreditcounter.Toolbox.Toaster;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.presentation.RecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.RecyclerViewTouchListener;

public class BrowseLocationsActivity extends AppCompatActivity implements View.OnClickListener
{
    private Location currentLocation = Content.getInstance().getLocationRoot();
    private List<Location> recentLocations = new ArrayList<>();

    private RecyclerViewAdapter contentRecyclerViewAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_locations);

        this.initializeViews();
        this.refreshViews();
    }

    private void initializeViews()
    {
        LinearLayout linearLayoutActivity = findViewById(R.id.linearLayout_browseLocations);
        View view = getLayoutInflater().inflate(R.layout.layout_browse_locations, linearLayoutActivity, false);
        linearLayoutActivity.addView(view);

        this.createToolbar(view);
        this.createNavigationBar(view);
        this.createContentRecyclerView(view);
    }

    private void refreshViews()
    {
        this.toolbar.setSubtitle(this.currentLocation.getName());
        this.createNavigationBar(this.findViewById(android.R.id.content).getRootView());
        this.contentRecyclerViewAdapter.updateList(new ArrayList<Element>(this.currentLocation.getChildren()));
    }

    private void createToolbar(View view)
    {
        this.toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_browse_locations));

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

    private void createNavigationBar(View view)
    {
        if(this.currentLocation.getParent() != null && !this.recentLocations.contains(this.currentLocation.getParent()))
        {
            this.recentLocations.add(this.currentLocation.getParent());
        }

        LinearLayout linearLayoutNavigationBar = view.findViewById(R.id.linearLayout_navigationBar);
        linearLayoutNavigationBar.invalidate();
        linearLayoutNavigationBar.removeAllViews();

        for (Location location : this.recentLocations)
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_no_border, linearLayoutNavigationBar, false);

            Button button = buttonView.findViewById(R.id.button_noBorder);
            Drawable drawable = this.getWhiteDrawable(getResources().getDrawable(R.drawable.ic_baseline_chevron_left_24px));
            button.setText(location.getName());
            button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            button.setId(Constants.BUTTON_BACK);
            button.setTag(location);
            button.setOnClickListener(this);

            linearLayoutNavigationBar.addView(buttonView);
        }
    }

    private Drawable getWhiteDrawable(Drawable drawable)
    {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.white));

        return drawable;
    }

    private void createContentRecyclerView(View view)
    {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_content);
        this.contentRecyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Element>(this.currentLocation.getChildren()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recyclerView, new RecyclerViewTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                if(view.getTag().getClass().equals(Park.class))
                {
                    Toaster.makeToast(getApplicationContext(), "start ShowParkActivity");
                }
                else
                {
                    BrowseLocationsActivity.this.currentLocation = (Location) view.getTag();
                    BrowseLocationsActivity.this.refreshViews();
                }
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));
        recyclerView.setAdapter(contentRecyclerViewAdapter);
    }

    private Drawable setTintToWhite(Drawable drawable)
    {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.white));

        return drawable;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().getUuidStringsFromElements(this.recentLocations));
        outState.putString(Constants.KEY_CURRENT_ELEMENT, this.currentLocation.getUuid().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.recentLocations = Content.getInstance().getLocationsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentLocation = (Location) Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_CURRENT_ELEMENT)));

        this.refreshViews();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.refreshViews();
    }

    @Override
    public void onClick(View view)
    {
        Location location = (Location) view.getTag();

        int length = this.recentLocations.size() - 1;
        for (int i = length; i >= 0; i--)
        {
            if (this.recentLocations.get(i).equals(location))
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
        this.refreshViews();
    }

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
                Toaster.makeToast(this, "onOptionsItemSelected --> default...!?");

                return super.onOptionsItemSelected(item);
            }
        }
    }
}
