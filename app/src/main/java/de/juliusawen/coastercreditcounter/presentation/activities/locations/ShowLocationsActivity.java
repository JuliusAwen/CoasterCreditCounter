package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.data.Location;
import de.juliusawen.coastercreditcounter.data.Park;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowLocationsActivity extends BaseActivity
{
    private Element currentElement;
    private List<Element> recentElements = new ArrayList<>();

    private ExpandableRecyclerAdapter locationsRecyclerAdapter;
    private Element longClickedElement;


    //region @Override
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowLocationsActivity.onCreate:: creating activity...");
        setContentView(R.layout.activity_show_locations);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addToolbar();
        super.addToolbarHomeButton();

        super.addHelpOverlay(getString(R.string.title_help, getString(R.string.subtitle_show_locations)), getString(R.string.help_text_show_locations));

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();

        this.createLocationRecyclerAdapter();
    }

    @Override
    protected void onResume()
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onResume:: CurrentElement %s", this.currentElement));
        this.updateActivityView();
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.currentElement.isRootElement())
        {
            menu.add(Menu.NONE, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit_root_location);
        }

        if(this.currentElement.getChildCountOfInstance(Location.class) > 1)
        {
            menu.add(Menu.NONE, Selection.SORT_LOCATIONS.ordinal(), Menu.NONE, R.string.selection_sort_locations);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case EDIT_LOCATION:
                ActivityTool.startActivityEdit(this, this.currentElement);
                return true;

            case SORT_LOCATIONS:
                ActivityTool.startActivitySort(
                        this,
                        Constants.REQUEST_SORT_LOCATIONS,
                        this.currentElement.getChildrenOfInstance(Location.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getUuidStringsFromElements(this.recentElements));
        outState.putString(Constants.KEY_ELEMENT, this.currentElement.getUuid().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.recentElements = App.content.fetchElementsByUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentElement = App.content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));
        if(requestCode == Constants.REQUEST_ADD_LOCATION)
        {
            if(resultCode == RESULT_OK)
            {
                String uuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                Element resultElement = App.content.fetchElementByUuidString(uuidString);
                updateLocationRecyclerView();
                this.locationsRecyclerAdapter.smoothScrollToElement(resultElement);
            }
        }
        else if(requestCode == Constants.REQUEST_SORT_LOCATIONS || requestCode == Constants.REQUEST_SORT_PARKS)
        {
            if(resultCode == RESULT_OK)
            {
                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> resultElements = App.content.fetchElementsByUuidStrings(resultElementsUuidStrings);

                Element parent = resultElements.get(0).getParent();
                Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult<SortElements>:: replacing children with sorted children in parent %s...", parent));
                parent.deleteChildren(resultElements);
                parent.addChildren(resultElements);
                this.updateLocationRecyclerView();

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    Element selectedElement = App.content.fetchElementByUuidString(selectedElementUuidString);
                    this.locationsRecyclerAdapter.smoothScrollToElement(selectedElement);
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowLocationsActivity.onActivityResult<SortElements>:: no selected element returned");
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onActonKeyDown:: hardware back button pressed");
                if(this.currentElement.isRootElement())
                {
                    this.onToolbarHomeButtonBackClicked();
                }
                else
                {
                    Element previousElement = this.recentElements.get(this.recentElements.size() - 2);
                    Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActonKeyDown<KEYCODE_BACK>:: returning to previous element %s", previousElement));
                    this.recentElements.remove(this.currentElement);
                    this.recentElements.remove(previousElement);
                    this.currentElement = previousElement;
                    this.updateActivityView();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onToolbarHomeButtonBackClicked()
    {
        Log.i(Constants.LOG_TAG, "ShowParkActivity.onToolbarHomeButtonBackClicked:: staring HubActivity");
        Log.e(Constants.LOG_TAG, "ShowParkActivity.onToolbarHomeButtonBackClicked:: HubActivity not available atm - staring ShowLocationsActivity<root> instead");
        ActivityTool.startActivityShow(this, App.content.getRootLocation());
    }

    //endregion

    private void initializeContent()
    {
        String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        this.currentElement = elementUuid != null ? App.content.getElementByUuid(UUID.fromString(elementUuid)) : App.content.getRootLocation();

        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.initializeContent:: initialized with currentElement %s", this.currentElement));
    }

    private void updateActivityView()
    {
        super.animateFloatingActionButton(null);
        this.decorateToolbar();
        this.updateLocationRecyclerView();
        this.updateNavigationBar();
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.currentElement.getName(), null);
    }

    //region FLOATING ACTION BUTTON
    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onClickFloatingActionButton();
            }
        });
    }

    private void onClickFloatingActionButton()
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), super.getFloatingActionButton());

        popupMenu.getMenu().add(0, Selection.ADD_LOCATION.ordinal(), Menu.NONE, R.string.selection_add_location);
        popupMenu.getMenu().add(0, Selection.ADD_PARK.ordinal(), Menu.NONE, R.string.selection_add_park);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                return onMenuItemClickPopupMenuFloatingActionButton(item);
            }
        });

        popupMenu.show();
    }

    private boolean onMenuItemClickPopupMenuFloatingActionButton(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickFloatingActionButton.PopupMenu.onMenuItemClick:: [%S] selected", selection));

        switch (selection)
        {
            case ADD_LOCATION:
                ActivityTool.startActivityAdd(this, Constants.REQUEST_ADD_LOCATION, this.currentElement);
                return true;

            case ADD_PARK:
                Toaster.makeToast(this, "AddPark not yet implemented");
                return true;

            default:
                return false;
        }
    }

    //endregion

    //region NAVIGATION BAR
    private void updateNavigationBar()
    {
        Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateNavigationBar:: updating NavigationBar...");

        LinearLayout linearLayoutNavigationBar = findViewById(R.id.linearLayoutShowLocations_NavigationBar);
        linearLayoutNavigationBar.invalidate();
        linearLayoutNavigationBar.removeAllViews();

        if(this.recentElements.isEmpty() && !this.currentElement.isRootElement())
        {
            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateNavigationBar:: constructing navigation bar");
            this.recentElements.clear();
            this.constructNavigationBar(this.currentElement.getParent());
        }

        if(!this.recentElements.contains(this.currentElement))
        {
            Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: adding current element %s to recent elements...", this.currentElement));
            this.recentElements.add(this.currentElement);
        }

        for (Element recentElement : this.recentElements)
        {
            Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: creating view for recent element %s...", recentElement));
            View buttonView = getLayoutInflater().inflate(R.layout.button_navigation_bar, linearLayoutNavigationBar, false);
            Button button = buttonView.findViewById(R.id.buttonNavigationBar);

            if(this.recentElements.indexOf(recentElement) != this.recentElements.size() -1)
            {
                Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_chevron_right));
                button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                button.setText(recentElement.getName());
                button.setId(ButtonFunction.BACK.ordinal());
                button.setTag(recentElement);
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        onClickNavigationBar(view);
                    }
                });
            }
            else
            {
                Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: %s is current element - applying special treatment", recentElement));
                button.setText(StringTool.getSpannableString(recentElement.getName(), Typeface.BOLD_ITALIC));
            }
            linearLayoutNavigationBar.addView(buttonView);
        }

        final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScrollViewShowLocations_NavigationBar);
        horizontalScrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                horizontalScrollView.fullScroll(View.FOCUS_RIGHT);
            }
        });

        Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: NavigationBar holds #[%d] elements", this.recentElements.size()));
    }

    private void constructNavigationBar(Element element)
    {
        Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.constructNavigationBar:: adding %s to recent elements...", element));

        if(!element.isRootElement())
        {
            this.recentElements.add(0, element);
            this.constructNavigationBar(element.getParent());
        }
        else
        {
            this.recentElements.add(0, element);
        }
    }

    private void onClickNavigationBar(View view)
    {
        Element element = (Element) view.getTag();

        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar.onClick:: %s clicked", element));

        int length = this.recentElements.size() - 1;
        for (int i = length; i >= 0; i--)
        {
            if(this.recentElements.get(i).equals(element))
            {
                this.recentElements.remove(i);
                break;
            }
            else
            {
                this.recentElements.remove(i);
            }
        }
        this.currentElement = element;
        this.updateActivityView();
    }
    //endregion

    //region CONTENT RECYCLER VIEW
    private void createLocationRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                onClickLocationRecyclerView(view);
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                onLongClickLocationRecyclerView(view);
                return true;
            }
        };

        this.locationsRecyclerAdapter = new ExpandableRecyclerAdapter(new ArrayList<>(this.currentElement.getChildrenOfInstance(Location.class)), recyclerOnClickListener);
        RecyclerView recyclerView = findViewById(android.R.id.content).findViewById(R.id.recyclerViewShowLocations);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.locationsRecyclerAdapter);
    }

    private void onClickLocationRecyclerView(View view)
    {
        Element element = (Element) view.getTag();

        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickLocationRecyclerView:: %s clicked", element));

        if(element.isInstance(Location.class))
        {
            this.currentElement = element;
            this.updateActivityView();
        }
        else if(element.isInstance(Park.class))
        {
            ActivityTool.startActivityShow(this, element);
        }
    }

    private void onLongClickLocationRecyclerView(final View view)
    {
        this.longClickedElement = (Element) view.getTag();
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onLongClickLocationRecyclerView:: %s long clicked", longClickedElement));

        if(this.longClickedElement.isInstance(Location.class))
        {
            PopupMenu popupMenu = new PopupMenu(this, view);

            popupMenu.getMenu().add(0, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit_location);
            popupMenu.getMenu().add(0, Selection.DELETE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_delete_element);

            if(this.longClickedElement.hasChildren())
            {
                popupMenu.getMenu().add(0, Selection.REMOVE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_remove_element);
            }

            if(this.longClickedElement.getChildCountOfInstance(Park.class) > 1)
            {
                popupMenu.getMenu().add(0, Selection.SORT_PARKS.ordinal(), Menu.NONE, R.string.selection_sort_parks);
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    return onClickMenuItemPopupMenuLongClickLocationRecyclerView(item, view);
                }
            });
            popupMenu.show();
        }
    }

    private boolean onClickMenuItemPopupMenuLongClickLocationRecyclerView(MenuItem item, final View view)
    {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickMenuItemPopupMenuLongClickLocationRecyclerView:: [%S] selected", selection));

        switch (selection)
        {
            case EDIT_LOCATION:
                ActivityTool.startActivityEdit(this, this.longClickedElement);
                return true;

            case DELETE_ELEMENT:
                builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.alert_dialog_delete_element_title);
                builder.setMessage(getString(R.string.alert_dialog_delete_element_message, this.longClickedElement.getName()));
                builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        onClickAlertDialogPositiveButtonDeleteElement(dialog, view);
                    }
                });

                builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        onClickAlertDialogNegativeButton(dialog);
                    }
                });

                alertDialog = builder.create();
                alertDialog.setIcon(R.drawable.ic_baseline_warning);

                alertDialog.show();
                return true;

            case REMOVE_ELEMENT:
                builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.alert_dialog_remove_element_title);

                String alertMessage;
                if(this.longClickedElement.getParent().equals(App.content.getRootLocation()) && this.longClickedElement.hasChildrenOfInstance(Park.class))
                {
                    alertMessage = getString(R.string.alert_dialog_remove_element_message_parent_is_root, this.longClickedElement.getName(), this.longClickedElement.getParent().getName());
                }
                else
                {
                    alertMessage = getString(R.string.alert_dialog_remove_element_message, this.longClickedElement.getName(), this.longClickedElement.getParent().getName());
                }

                builder.setMessage(alertMessage);

                builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        onClickAlertDialogPositiveButtonRemoveElement(dialog, view);
                    }
                });

                builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        onClickAlertDialogNegativeButton(dialog);
                    }
                });
                alertDialog = builder.create();
                alertDialog.setIcon(R.drawable.ic_baseline_warning);
                alertDialog.show();
                return true;

            case SORT_PARKS:
                ActivityTool.startActivitySort(
                        this,
                        Constants.REQUEST_SORT_PARKS,
                        this.longClickedElement.getChildrenOfInstance(Park.class));
                return true;

            default:
                return false;
        }
    }

    private void onClickAlertDialogPositiveButtonDeleteElement(DialogInterface dialog, View view)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickAlertDialogPositiveButtonDeleteElement:: deleting %s...", longClickedElement));

        dialog.dismiss();

        if(App.content.deleteElementAndChildren(this.longClickedElement))
        {
            if(this.longClickedElement.deleteElementAndChildren())
            {
                updateLocationRecyclerView();
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format(
                        "ShowLocationsActivity.onClickAlertDialogPositiveButtonDeleteElement:: deleting %s and children failed - restoring content...",
                        longClickedElement));
                App.content.addElementAndChildren(longClickedElement);
                Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_delete_failed));
            }
        }
        else
        {
            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_delete_failed));

            String errorMessage = String.format(
                    "ShowLocationsActivity.onClickAlertDialogPositiveButtonDeleteElement:: removing %s and children from content failed!",
                    this.longClickedElement);
            Log.e(Constants.LOG_TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        Snackbar snackbar = Snackbar.make(view, getString(R.string.action_undo_delete_element_text, this.longClickedElement.getName()), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onClickSnackbarUndoDeleteElement();
            }
        });
        snackbar.show();
    }

    private void onClickSnackbarUndoDeleteElement()
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoDeleteElement:: undo delete [%s]...", this.longClickedElement));

        if(this.longClickedElement.undoIsPossible && this.longClickedElement.undoDeleteElementAndChildren())
        {
            App.content.addElementAndChildren(this.longClickedElement);
            updateLocationRecyclerView();

            Toaster.makeToast(getApplicationContext(), getString(R.string.action_element_restored_text, longClickedElement.getName()));

            this.locationsRecyclerAdapter.smoothScrollToElement(this.longClickedElement);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoDeleteElement:: undo delete [%s] failed!", this.longClickedElement));
            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_undo_not_possible));
        }
    }

    private void onClickAlertDialogPositiveButtonRemoveElement(DialogInterface dialog, View view)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing [%s]...", this.longClickedElement));

        dialog.dismiss();

        if(App.content.deleteElement(this.longClickedElement))
        {
            if(this.longClickedElement.removeElement())
            {
                this.currentElement = this.longClickedElement.getParent();
                updateLocationRecyclerView();
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format(
                        "ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing %s failed - restoring content...",
                        this.longClickedElement));
                App.content.addElementAndChildren(this.longClickedElement);
                Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_remove_failed));
            }
        }
        else
        {
            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_delete_failed));

            String errorMessage = String.format("ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing %s from content failed!", this.longClickedElement);
            Log.e(Constants.LOG_TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        Snackbar snackbar = Snackbar.make(view, getString(R.string.action_undo_remove_element_text, this.longClickedElement.getName()), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onClickSnackbarUndoRemoveElement();
            }
        });
        snackbar.show();
    }

    private void onClickSnackbarUndoRemoveElement()
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoRemoveElement:: undo remove [%s]...", this.longClickedElement));

        if(this.longClickedElement.undoIsPossible && longClickedElement.undoRemoveElement())
        {
            App.content.addElement(this.longClickedElement);
            updateLocationRecyclerView();

            Toaster.makeToast(getApplicationContext(), getString(R.string.action_element_restored_text, longClickedElement.getName()));

            this.locationsRecyclerAdapter.smoothScrollToElement(this.longClickedElement);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoRemoveElement:: undo remove [%s] failed!", this.longClickedElement));
            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_undo_not_possible));
        }
    }

    private void onClickAlertDialogNegativeButton(DialogInterface dialog)
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onClickAlertDialogNegativeButton:: canceled");
        dialog.dismiss();
    }

    private void updateLocationRecyclerView()
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.updateLocationRecyclerView:: updating RecyclerView...");
        this.locationsRecyclerAdapter.updateElements(this.currentElement.getChildrenOfInstance(Location.class));
    }
    //endregion
}
