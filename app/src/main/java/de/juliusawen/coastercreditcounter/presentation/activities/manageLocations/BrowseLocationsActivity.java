package de.juliusawen.coastercreditcounter.presentation.activities.manageLocations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;
import de.juliusawen.coastercreditcounter.toolbox.ViewTool;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public class BrowseLocationsActivity extends AppCompatActivity implements HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Location currentLocation = Content.getInstance().getLocationRoot();
    private List<Location> recentLocations = new ArrayList<>();
    private Location longClickedLocation;
    private RecyclerView recyclerView;
    private ExpandableRecyclerAdapter expandableRecyclerAdapter;
    private HelpOverlayFragment helpOverlayFragment;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_locations);

        this.savedInstanceState = savedInstanceState;

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        this.currentLocation = (Location) Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
    }

    private void initializeViews()
    {
        CoordinatorLayout coordinatorLayoutActivity = findViewById(R.id.coordinatorLayoutBrowseLocations);
        View browseLocationsView = getLayoutInflater().inflate(R.layout.layout_browse_locations, coordinatorLayoutActivity, false);
        coordinatorLayoutActivity.addView(browseLocationsView);

        this.createToolbar(browseLocationsView);
        this.createNavigationBar();
        this.createContentRecyclerView(browseLocationsView);
        this.createFloatingActionButton();
        this.createHelpOverlayFragment(coordinatorLayoutActivity.getId());
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_locations));

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
            menu.add(0, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_rename_root);
        }
        if(this.currentLocation.getChildren().size() > 1)
        {
            menu.add(0, Selection.SORT_ELEMENTS.ordinal(), Menu.NONE, R.string.selection_sort_entries);
        }
        menu.add(0, Selection.HELP.ordinal(), Menu.NONE, R.string.selection_help);

        return super.onPrepareOptionsMenu(menu);
    }

    private void createNavigationBar()
    {
        View view = this.findViewById(android.R.id.content).getRootView();

        if(!this.recentLocations.contains(this.currentLocation))
        {
            this.recentLocations.add(this.currentLocation);
        }

        LinearLayout linearLayoutNavigationBar = view.findViewById(R.id.linearLayoutBrowseLocations_NavigationBar);
        linearLayoutNavigationBar.invalidate();
        linearLayoutNavigationBar.removeAllViews();

        for (Location location : this.recentLocations)
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_navigation_bar, linearLayoutNavigationBar, false);

            Button button = buttonView.findViewById(R.id.button_transparent);

            if(this.recentLocations.indexOf(location) != 0)
            {
                Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_chevron_left));
                button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }

            if(this.recentLocations.indexOf(location) != this.recentLocations.size() -1)
            {
                button.setText(location.getName());
            }
            else
            {
                button.setText(StringTool.getSpannableString(location.getName(), Typeface.BOLD_ITALIC));
            }

            button.setId(ButtonFunction.BACK.ordinal());
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
                    updateRecyclerView();
                    createNavigationBar();
                }
            });

            linearLayoutNavigationBar.addView(buttonView);

            final HorizontalScrollView horizontalScrollView = view.findViewById(R.id.horizontalScrollViewBrowseLocations_NavigationBar);
            horizontalScrollView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    horizontalScrollView.fullScroll(View.FOCUS_RIGHT);
                }
            });
        }
    }

    private void createContentRecyclerView(View view)
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                if(view.getTag().getClass().equals(Location.class))
                {
                    currentLocation = (Location) view.getTag();
                    updateRecyclerView();
                    createNavigationBar();

                }
                else if(view.getTag().getClass().equals(Park.class))
                {
                    //Todo: implement show park activity
                    Toaster.makeToast(getApplicationContext(), "ShowPark not yet implemented");
                }
            }

            @Override
            public void onLongClick(final View view, int position)
            {
                longClickedLocation = (Location) view.getTag();

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);

                popupMenu.getMenu().add(0, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit_location);
                popupMenu.getMenu().add(0, Selection.DELETE_LOCATION.ordinal(), Menu.NONE, R.string.selection_delete_location);

                if(!(longClickedLocation).getChildren().isEmpty())
                {
                    popupMenu.getMenu().add(0, Selection.REMOVE_LOCATION.ordinal(), Menu.NONE, R.string.selection_remove_location_level);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Selection selection = Selection.values()[item.getItemId()];

                        AlertDialog.Builder builder;
                        AlertDialog alertDialog;

                        switch (selection)
                        {
                            case EDIT_LOCATION:
                                startEditLocationActivity(longClickedLocation);
                                return true;

                            case DELETE_LOCATION:
                                builder = new AlertDialog.Builder(BrowseLocationsActivity.this);

                                builder.setTitle(R.string.alert_dialog_delete_location_title);
                                builder.setMessage(getString(R.string.alert_dialog_delete_location_message, longClickedLocation.getName()));
                                builder.setPositiveButton(R.string.button_text_accept, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();

                                        if(longClickedLocation.deleteNodeAndChildren())
                                        {
                                            Content.getInstance().deleteLocationAndChildren(longClickedLocation);
                                            updateRecyclerView();
                                        }
                                        else
                                        {
                                            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_delete_failed));
                                        }

                                        Snackbar snackbar = Snackbar.make(view, R.string.action_undo_delete_location_text, Snackbar.LENGTH_LONG);
                                        snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                if(longClickedLocation.undoDeleteNodeAndChildrenPossible && longClickedLocation.undoDeleteNodeAndChildren())
                                                {
                                                    Content.getInstance().addLocationAndChildren(longClickedLocation);
                                                    updateRecyclerView();

                                                    smoothScrollToLocation(longClickedLocation);
                                                }
                                                else
                                                {
                                                    Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_undo_not_possible));
                                                }
                                            }
                                        });
                                        snackbar.show();
                                    }
                                });

                                builder.setNegativeButton(R.string.button_text_cancel, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();
                                    }
                                });

                                alertDialog = builder.create();
                                alertDialog.setIcon(R.drawable.ic_baseline_warning);

                                alertDialog.show();
                                return true;

                            case REMOVE_LOCATION:
                                builder = new AlertDialog.Builder(BrowseLocationsActivity.this);

                                builder.setTitle(R.string.alert_dialog_remove_location_title);
                                builder.setMessage(getString(R.string.alert_dialog_remove_location_level_message, longClickedLocation.getName(), longClickedLocation.getParent().getName()));

                                builder.setPositiveButton(R.string.button_text_accept, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();

                                        if(longClickedLocation.removeNode())
                                        {
                                            Content.getInstance().deleteElement(longClickedLocation);
                                            currentLocation = longClickedLocation.getParent();
                                            updateRecyclerView();

                                        }
                                        else
                                        {
                                            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_remove_failed));
                                        }

                                        Snackbar snackbar = Snackbar.make(view, R.string.action_undo_remove_location_text, Snackbar.LENGTH_LONG);
                                        snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                if(longClickedLocation.undoRemoveNodePossible && longClickedLocation.undoRemoveNode())
                                                {
                                                    Content.getInstance().addElement(longClickedLocation);
                                                    updateRecyclerView();

                                                    smoothScrollToLocation(longClickedLocation);
                                                }
                                                else
                                                {
                                                    Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_undo_not_possible));
                                                }
                                            }
                                        });
                                        snackbar.show();
                                    }
                                });

                                builder.setNegativeButton(R.string.button_text_cancel, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.dismiss();
                                    }
                                });

                                alertDialog = builder.create();
                                alertDialog.setIcon(R.drawable.ic_baseline_warning);

                                alertDialog.show();
                                return true;

                                default:
                                    return false;
                        }

                    }
                });

                popupMenu.show();
            }
        };

        this.expandableRecyclerAdapter = new ExpandableRecyclerAdapter(new ArrayList<Element>(this.currentLocation.getChildren()), recyclerOnClickListener);
        this.recyclerView = view.findViewById(R.id.recyclerViewBrowseLocations);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expandableRecyclerAdapter);
    }

    private void createFloatingActionButton()
    {
        final FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);

        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add));
        floatingActionButton.setImageDrawable(drawable);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), floatingActionButton);

                popupMenu.getMenu().add(0, Selection.ADD_LOCATION.ordinal(), Menu.NONE, R.string.selection_add_location);
                popupMenu.getMenu().add(0, Selection.ADD_PARK.ordinal(), Menu.NONE, R.string.selection_add_park);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Intent intent;

                        Selection selection = Selection.values()[item.getItemId()];
                        switch (selection)
                        {
                            case ADD_LOCATION:
                                intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, currentLocation.getUuid().toString());
                                startActivityForResult(intent, Constants.REQUEST_ADD_LOCATION);
                                return true;

                            case ADD_PARK:
                                //Todo: implement add park activity
                                Toaster.makeToast(getApplicationContext(), "not yet implemented");
                                return true;

                                default:
                                    return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });
    }

    private void setFloatingActionButtonVisibility(boolean isVisible)
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void createHelpOverlayFragment(int frameLayoutId)
    {
        if(this.savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_browse_locations), false);
            fragmentTransaction.add(frameLayoutId, this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.commit();
        }
        else
        {
            this.helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().getUuidStringsFromElements(this.recentLocations));
        outState.putString(Constants.KEY_ELEMENT, this.currentLocation.getUuid().toString());
        outState.putBoolean(Constants.KEY_HELP_VISIBLE, this.helpOverlayFragment.isVisible());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.recentLocations = Content.getInstance().getLocationsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentLocation = (Location) Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
        this.helpOverlayFragment.setVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));
        this.setFloatingActionButtonVisibility(!savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));

        this.updateRecyclerView();
        this.createNavigationBar();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.updateRecyclerView();
        this.createNavigationBar();
    }

    private void updateRecyclerView()
    {
        this.expandableRecyclerAdapter.updateList(new ArrayList<Element>(this.currentLocation.getChildren()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];

        switch(selection)
        {
            case EDIT_LOCATION:
                this.startEditLocationActivity(this.currentLocation);
                return true;

            case SORT_ELEMENTS:
                Intent intent = new Intent(this, SortElementsActivity.class);
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.currentLocation.getUuid().toString());
                startActivityForResult(intent, Constants.REQUEST_SORT_ELEMENTS);
                return true;

            case HELP:
                this.helpOverlayFragment.setVisibility(true);
                this.setFloatingActionButtonVisibility(false);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHelpOverlayFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        switch (buttonFunction)
        {
            case CLOSE:
                this.helpOverlayFragment.setVisibility(false);
                this.setFloatingActionButtonVisibility(true);
                break;
        }
    }

    private void startEditLocationActivity(Element element)
    {
        Intent intent = new Intent(getApplicationContext(), EditLocationActivity.class);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == Constants.REQUEST_ADD_LOCATION || requestCode == Constants.REQUEST_SORT_ELEMENTS)
        {
            if(resultCode == RESULT_OK)
            {
                String uuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                Location receivedLocation = Content.getInstance().getLocationFromUuidString(uuidString);

                this.smoothScrollToLocation(receivedLocation);
            }
        }
    }

    private void smoothScrollToLocation(Location location)
    {
        int position = this.currentLocation.getChildren().indexOf(location);
        int scrollMargin = ViewTool.getScrollMarginForRecyclerView(this.recyclerView);
        if(this.currentLocation.getChildren().size() > position + scrollMargin)
        {
            recyclerView.smoothScrollToPosition(position + scrollMargin);
        }
        else
        {
            recyclerView.smoothScrollToPosition(position);
        }
    }
}
