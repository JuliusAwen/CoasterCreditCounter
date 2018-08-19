package de.juliusawen.coastercreditcounter.presentation.manageLocations;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import de.juliusawen.coastercreditcounter.presentation.SortElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public class BrowseLocationsActivity extends AppCompatActivity implements HelpOverlayFragment.OnFragmentInteractionListener
{
    private Location currentLocation = Content.getInstance().getLocationRoot();
    private List<Location> recentLocations = new ArrayList<>();

    private Element longClickedElement;

    private RecyclerViewAdapter recyclerViewAdapter;

    private boolean helpOverlayVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_locations);

        this.initializeContent();

        this.initializeViews();
        this.refreshViews();
    }

    private void initializeContent()
    {
        Intent intent = getIntent();

        this.currentLocation = (Location) Content.getInstance().getElementByUuid(UUID.fromString(intent.getStringExtra(Constants.EXTRA_UUID)));
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayout_browseLocations);
        View browseLocationsView = getLayoutInflater().inflate(R.layout.layout_browse_locations, frameLayoutActivity, false);
        frameLayoutActivity.addView(browseLocationsView);

        this.createToolbar(browseLocationsView);
        this.createNavigationBar(browseLocationsView);
        this.createContentRecyclerView(browseLocationsView);

        this.createFloatingActionButton();
        this.createHelpOverlayFragment();
    }

    private void refreshViews()
    {
        this.createNavigationBar(this.findViewById(android.R.id.content).getRootView());
        this.recyclerViewAdapter.updateList(new ArrayList<Element>(this.currentLocation.getChildren()));
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_browse_locations));

        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        if(this.currentLocation.getParent() == null)
        {
            menu.add(0, Constants.SELECTION_RENAME_ROOT, Menu.NONE, R.string.selection_rename_root);
        }
        if(this.currentLocation.getChildren().size() > 1)
        {
            menu.add(0, Constants.SELECTION_SORT_ELEMENTS, Menu.NONE, R.string.selection_sort_entries);
        }
        menu.add(0, Constants.SELECTION_HELP, Menu.NONE, R.string.selection_help);

        return super.onPrepareOptionsMenu(menu);
    }

    private void createNavigationBar(View view)
    {
        if(!this.recentLocations.contains(this.currentLocation))
        {
            this.recentLocations.add(this.currentLocation);
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
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerViewBrowseLocations);

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
                    //Todo: implement show park activity
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
                longClickedElement = (Element) view.getTag();


                //Todo: implement popup menu dynamically

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @SuppressLint("StringFormatMatches")
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch(item.getItemId())
                        {
                            case(R.id.selectionEditLocation):
                            {
                                startEditLocationActivity(longClickedElement);

                                return true;
                            }
                            case(R.id.selectionDeleteLocation):
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(BrowseLocationsActivity.this);

                                builder.setTitle(R.string.alert_dialog_delete_location_title);
                                builder.setMessage(getString(R.string.alert_dialog_delete_location_message, longClickedElement.getName()));

                                builder.setPositiveButton(R.string.button_text_accept, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();

                                        ((Location) longClickedElement).deleteNodeAndChildren();
                                        Content.getInstance().removeLocationAndChildren(longClickedElement);

                                        refreshViews();
                                    }
                                });

                                builder.setNegativeButton(R.string.button_text_cancel, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setIcon(R.drawable.ic_baseline_warning_24px);

                                alertDialog.show();

                                return true;
                            }
                            case(R.id.selectionRemoveLocationLevel):
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(BrowseLocationsActivity.this);

                                builder.setTitle(R.string.alert_dialog_remove_location_title);
                                builder.setMessage(getString(R.string.alert_dialog_remove_location_level_message, longClickedElement.getName(),
                                        ((Location)longClickedElement).getParent().getName()));

                                builder.setPositiveButton(R.string.button_text_accept, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();

                                        ((Location)longClickedElement).removeNode();
                                        Content.getInstance().removeLocation(longClickedElement);

                                        currentLocation = ((Location) longClickedElement).getParent();
                                        refreshViews();
                                    }
                                });

                                builder.setNegativeButton(R.string.button_text_cancel, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.setIcon(R.drawable.ic_baseline_warning_24px);

                                alertDialog.show();

                                return true;
                            }
                            default:
                            {
                                return false;
                            }
                        }
                    }
                });
                menuInflater.inflate(R.menu.selection_edit_mode_location, popupMenu.getMenu());
                popupMenu.show();
            }
        }));

        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void createFloatingActionButton()
    {
        final FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonBrowseLocations);

        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add_24px));
        floatingActionButton.setImageDrawable(drawable);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), floatingActionButton);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Intent intent;

                        switch (item.getItemId())
                        {
                            case R.id.selectionAddLocation:
                                intent = new Intent(getApplicationContext(), AddOrInsertLocationActivity.class);
                                intent.putExtra(Constants.EXTRA_UUID, currentLocation.getUuid().toString());
                                intent.putExtra(Constants.EXTRA_SELECTION, Constants.SELECTION_ADD_LOCATION);
                                startActivity(intent);
                                return true;

                            case R.id.selectionAddPark:

                                //Todo: implement add park activity

                                Toaster.makeToast(getApplicationContext(), "not yet implemented");

                                return true;

                            case R.id.selectionInsertLocation:
                                intent = new Intent(getApplicationContext(), AddOrInsertLocationActivity.class);
                                intent.putExtra(Constants.EXTRA_UUID, currentLocation.getUuid().toString());
                                intent.putExtra(Constants.EXTRA_SELECTION, Constants.SELECTION_INSERT_LOCATION_LEVEL);
                                startActivity(intent);
                                return true;

                            default:
                                return false;
                        }
                    }
                });

                menuInflater.inflate(R.menu.selection_fab_location, popupMenu.getMenu());
                popupMenu.show();
            }
        });
    }

    private void setFloatingActionButtonVisibility(boolean isVisible)
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonBrowseLocations);

        floatingActionButton.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void createHelpOverlayFragment()
    {
        this.helpOverlayVisible = false;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HelpOverlayFragment helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_browse_locations), this.helpOverlayVisible);
        fragmentTransaction.add(R.id.frameLayout_browseLocations, helpOverlayFragment, Constants.FRAGMENT_TAG_HELP);
        fragmentTransaction.commit();
    }

    private void setHelpOverlayFragmentVisibility(boolean isVisible)
    {
        HelpOverlayFragment helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP);

        helpOverlayFragment.fragmentView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);

        this.setFloatingActionButtonVisibility(!isVisible);
        this.helpOverlayVisible = isVisible;
    }

    @Override
    public void onFragmentInteraction(View view)
    {
        if(view.getId() == Constants.BUTTON_CLOSE)
        {
            this.setHelpOverlayFragmentVisibility(false);
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
        this.setHelpOverlayFragmentVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_ACTIVE));

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
        if (item.getItemId() == Constants.SELECTION_RENAME_ROOT)
        {
            this.startEditLocationActivity(this.currentLocation);

            return true;
        }
        else if(item.getItemId() == Constants.SELECTION_SORT_ELEMENTS)
        {
            Intent intent = new Intent(this, SortElementsActivity.class);
            intent.putExtra(Constants.EXTRA_UUID, this.currentLocation.getUuid().toString());
            startActivity(intent);

            return true;
        }
        else if(item.getItemId() == Constants.SELECTION_HELP)
        {
            this.setHelpOverlayFragmentVisibility(true);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startEditLocationActivity(Element element)
    {
        Intent intent = new Intent(getApplicationContext(), EditLocationActivity.class);
        intent.putExtra(Constants.EXTRA_UUID, element.getUuid().toString());
        startActivity(intent);
    }
}
