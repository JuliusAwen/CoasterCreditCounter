package de.juliusawen.coastercreditcounter.presentation.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.Toolbox.Toaster;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.presentation.RecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.RecyclerViewTouchListener;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpFragment;

public class BrowseLocationsActivity extends AppCompatActivity implements HelpFragment.OnFragmentInteractionListener
{
    private Location currentLocation = Content.getInstance().getLocationRoot();
    private List<Location> recentLocations = new ArrayList<>();

    private RecyclerViewAdapter recyclerViewAdapter;
    private Toolbar toolbar;

    private boolean helpOverlayVisible;

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
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayout_browseLocations);
        View browseLocationsView = getLayoutInflater().inflate(R.layout.layout_browse_locations, frameLayoutActivity, false);
        frameLayoutActivity.addView(browseLocationsView);

        this.createToolbar(browseLocationsView);
        this.createNavigationBar(browseLocationsView);
        this.createContentRecyclerView(browseLocationsView);
        this.createFloatingActionButton(frameLayoutActivity);

        this.createHelpOverlay();
    }

    private void refreshViews()
    {
        this.toolbar.setSubtitle(this.currentLocation.getName());
        this.createNavigationBar(this.findViewById(android.R.id.content).getRootView());
        this.recyclerViewAdapter.updateList(new ArrayList<Element>(this.currentLocation.getChildren()));
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
        getMenuInflater().inflate(R.menu.options_menu_browse_locations_items, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void createNavigationBar(View view)
    {
        if(this.currentLocation.getParent() != null && !this.recentLocations.contains(this.currentLocation.getParent()))
        {
            this.recentLocations.add(this.currentLocation.getParent());
        }

        LinearLayout linearLayoutNavigationBar = view.findViewById(R.id.linearLayoutBrowseLocationsNavigationBar);
        linearLayoutNavigationBar.invalidate();
        linearLayoutNavigationBar.removeAllViews();

        for (Location location : this.recentLocations)
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_no_border, linearLayoutNavigationBar, false);

            Button button = buttonView.findViewById(R.id.button_noBorder);
            Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_chevron_left_24px));
            button.setText(location.getName());
            button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            button.setId(Constants.BUTTON_BACK);
            button.setTag(location);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Location location = (Location) view.getTag();

                    int length = recentLocations.size() - 1;
                    for (int i = length; i >= 0; i--)
                    {
                        if (recentLocations.get(i).equals(location))
                        {
                            recentLocations.remove(i);
                            break;
                        }
                        else
                        {
                            recentLocations.remove(i);
                        }
                    }

                    currentLocation = location;
                    refreshViews();
                }
            });

            linearLayoutNavigationBar.addView(buttonView);
        }
    }

    private void createContentRecyclerView(View view)
    {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSortElements);
        this.recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Element>(this.currentLocation.getChildren()));
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
                    currentLocation = (Location) view.getTag();
                    refreshViews();
                }
            }

            @Override
            public void onLongClick(View view, int position)
            {
                //Todo: implement fragment selection edit/delete location (plus remove location)
                //Todo: implement confirmation to delete
                Toaster.makeToast(getApplicationContext(), "show fragment select edit/delete");

                ((Location) view.getTag()).deleteNodeAndChildren();

                recyclerViewAdapter.notifyDataSetChanged();
                refreshViews();
            }
        }));

        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void createFloatingActionButton(View view)
    {
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButtonBrowseLocations);

        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add_24px));
        floatingActionButton.setImageDrawable(drawable);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Todo: implement fragment selection add location/park
                Toaster.makeToast(getApplicationContext(), "show fragment select add location/park");

                Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                intent.putExtra(Constants.EXTRA_UUID, currentLocation.getUuid().toString());
                startActivity(intent);
            }
        });
    }

    private void setFloatingActionButtonVisibility(boolean isVisible)
    {
        FloatingActionButton floatingActionButton = (this.findViewById(android.R.id.content).getRootView()).findViewById(R.id.floatingActionButtonBrowseLocations);

        if(isVisible)
        {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        else
        {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
    }

    private void createHelpOverlay()
    {
        this.helpOverlayVisible = false;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HelpFragment helpFragment = HelpFragment.newInstance(getText(R.string.help_text_browse_locations), this.helpOverlayVisible);
        fragmentTransaction.add(R.id.frameLayout_browseLocations, helpFragment, Constants.FRAGMENT_TAG_HELP);
        fragmentTransaction.commit();
    }

    private void setHelpOverlayVisibility(boolean isVisible)
    {
        HelpFragment helpFragment = (HelpFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP);

        if(isVisible)
        {
            helpFragment.fragmentView.setVisibility(View.VISIBLE);
        }
        else
        {
            helpFragment.fragmentView.setVisibility(View.INVISIBLE);
        }

        this.setFloatingActionButtonVisibility(!isVisible);
        this.helpOverlayVisible = isVisible;
    }

    @Override
    public void onFragmentInteraction(View view)
    {
        if(view.getId() == Constants.BUTTON_CLOSE_HELP_OVERLAY)
        {
            this.setHelpOverlayVisibility(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().getUuidStringsFromElements(this.recentLocations));
        outState.putString(Constants.KEY_CURRENT_ELEMENT, this.currentLocation.getUuid().toString());
        outState.putBoolean(Constants.KEY_HELP_ACTIVE, this.helpOverlayVisible);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.recentLocations = Content.getInstance().getLocationsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentLocation = (Location) Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_CURRENT_ELEMENT)));
        this.setHelpOverlayVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_ACTIVE));

        this.refreshViews();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.refreshViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.optionsMenuSort:
            {
                if(this.currentLocation.getChildren().size() > 1)
                {
                    Intent intent = new Intent(this, SortElementsActivity.class);
                    intent.putExtra(Constants.EXTRA_UUID, this.currentLocation.getUuid().toString());
                    startActivity(intent);
                }
                else
                {
                    Toaster.makeToast(this, "nothing to sort here... Move along!");
                }

                return true;
            }

            case R.id.optionsMenuHelp:
            {
                this.setHelpOverlayVisibility(true);

                return true;
            }

            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
