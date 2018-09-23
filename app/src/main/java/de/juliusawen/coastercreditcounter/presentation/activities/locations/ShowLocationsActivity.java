package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowLocationsActivity extends BaseActivity
{
    private Element currentElement;
    private List<Element> recentElements = new ArrayList<>();

    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private Element longClickedElement;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowLocationsActivity.onCreate:: creating activity...");
        setContentView(R.layout.activity_show_locations);
        super.onCreate(savedInstanceState);

        Set<Element> initiallyExpandedElements = new HashSet<>();
        if(savedInstanceState != null)
        {
            this.recentElements = App.content.fetchElementsByUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_RECENT_ELEMENTS));
            this.currentElement = App.content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_CURRENT_ELEMENT)));

//            this.contentRecyclerViewAdapter.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(Constants.KEY_RECYCLER_SCROLL_POSITION));
            initiallyExpandedElements = new HashSet<>(App.content.fetchElementsByUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_RECYCLER_EXPANDED_ELEMENTS)));
        }
        else
        {
            this.initializeContent();
        }

        this.createContentRecyclerAdapter(initiallyExpandedElements);

        super.addToolbar();
        super.addToolbarHomeButton();

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();

        this.updateActivityView();

        super.addHelpOverlay(getString(R.string.title_help, getString(R.string.subtitle_locations_show)), getString(R.string.help_text_show_locations));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.currentElement.isRootElement())
        {
            menu.add(Menu.NONE, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit_root_location);
        }

        if(this.currentElement.getChildCountOfType(Location.class) > 1)
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
                ActivityTool.startActivitySortForResult(
                        this,
                        Constants.REQUEST_SORT_LOCATIONS,
                        this.currentElement.getChildrenOfType(Location.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_RECENT_ELEMENTS, App.content.getUuidStringsFromElements(this.recentElements));
        outState.putString(Constants.KEY_CURRENT_ELEMENT, this.currentElement.getUuid().toString());

//        outState.putParcelable(Constants.KEY_RECYCLER_SCROLL_POSITION, this.contentRecyclerViewAdapter.getLayoutManager().onSaveInstanceState());
        outState.putStringArrayList(Constants.KEY_RECYCLER_EXPANDED_ELEMENTS, App.content.getUuidStringsFromElements(this.contentRecyclerViewAdapter.getExpandedElements()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));
        if(requestCode == Constants.REQUEST_ADD_LOCATION)
        {
            if(resultCode == RESULT_OK)
            {
                //Todo: scroll to returned element
//                String uuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
//                Element resultElement = App.content.fetchElementByUuidString(uuidString);
                updateContentRecyclerView();
//                this.contentRecyclerViewAdapter.smoothScrollToElement(resultElement);
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
                this.updateContentRecyclerView();

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    Element selectedElement = App.content.fetchElementByUuidString(selectedElementUuidString);
                    this.contentRecyclerViewAdapter.smoothScrollToElement(selectedElement);
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
                Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onKeyDown<BACK>:: hardware back button pressed");
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
                    this.updateContentRecyclerView();
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
        this.updateNavigationBar();
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(this.currentElement.getName(), null);
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                PopupMenu popupMenu = new PopupMenu(ShowLocationsActivity.this, getFloatingActionButton());

                popupMenu.getMenu().add(0, Selection.ADD_LOCATION.ordinal(), Menu.NONE, R.string.selection_add_location);
                popupMenu.getMenu().add(0, Selection.ADD_PARK.ordinal(), Menu.NONE, R.string.selection_add_park);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Selection selection = Selection.values()[item.getItemId()];
                        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickFloatingActionButton.PopupMenu.onMenuItemClick:: [%S] selected", selection));

                        switch (selection)
                        {
                            case ADD_LOCATION:
                                ActivityTool.startActivityAddForResult(ShowLocationsActivity.this, Constants.REQUEST_ADD_LOCATION, currentElement);
                                return true;

                            case ADD_PARK:
                                Toaster.makeToast(ShowLocationsActivity.this, "AddPark not yet implemented");
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
                        Element element = (Element) view.getTag();

                        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar.onClick:: %s clicked", element));

                        int length = recentElements.size() - 1;
                        for (int i = length; i >= 0; i--)
                        {
                            if(recentElements.get(i).equals(element))
                            {
                                recentElements.remove(i);
                                break;
                            }
                            else
                            {
                                recentElements.remove(i);
                            }
                        }
                        currentElement = element;
                        updateActivityView();
                        updateContentRecyclerView();
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

    private void createContentRecyclerAdapter(Set<Element> initiallyExpandedElements)
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Element element = (Element) view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickLocationRecyclerView:: %s clicked", element));

                if(element.isInstance(Location.class))
                {
                    currentElement = element;
                    updateActivityView();
                    updateContentRecyclerView();
                }
                else if(element.isInstance(Park.class))
                {
                    ActivityTool.startActivityShow(ShowLocationsActivity.this, element);
                }
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                longClickedElement = (Element) view.getTag();
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onLongClickLocationRecyclerView:: %s long clicked", longClickedElement));

                if(longClickedElement.isInstance(Location.class))
                {
                    PopupMenu popupMenu = new PopupMenu(ShowLocationsActivity.this, view);

                    popupMenu.getMenu().add(0, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit_location);
                    popupMenu.getMenu().add(0, Selection.DELETE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_delete_element);

                    if(longClickedElement.hasChildren())
                    {
                        popupMenu.getMenu().add(0, Selection.REMOVE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_remove_element);
                    }

                    if(longClickedElement.getChildCountOfType(Park.class) > 1)
                    {
                        popupMenu.getMenu().add(0, Selection.SORT_PARKS.ordinal(), Menu.NONE, R.string.selection_sort_parks);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            AlertDialog.Builder builder;
                            AlertDialog alertDialog;

                            Selection selection = Selection.values()[item.getItemId()];
                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickMenuItemPopupMenuLongClickLocationRecyclerView:: [%S] selected", selection));

                            switch (selection)
                            {
                                case EDIT_LOCATION:
                                    ActivityTool.startActivityEdit(ShowLocationsActivity.this, longClickedElement);
                                    return true;

                                case DELETE_ELEMENT:
                                    builder = new AlertDialog.Builder(ShowLocationsActivity.this);

                                    builder.setTitle(R.string.alert_dialog_delete_element_title);
                                    builder.setMessage(getString(R.string.alert_dialog_delete_element_message, longClickedElement.getName()));
                                    builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickAlertDialogPositiveDeleteElement:: deleting %s...", longClickedElement));

                                            dialog.dismiss();

                                            if(App.content.deleteElementAndChildren(longClickedElement))
                                            {
                                                if(longClickedElement.deleteElementAndChildren())
                                                {
                                                    updateContentRecyclerView();
                                                }
                                                else
                                                {
                                                    Log.e(Constants.LOG_TAG, String.format(
                                                            "ShowLocationsActivity.onClickAlertDialogPositiveDeleteElement:: deleting %s and children failed - restoring content...",
                                                            longClickedElement));
                                                    App.content.addElementAndChildren(longClickedElement);
                                                    Toaster.makeToast(ShowLocationsActivity.this, getString(R.string.error_text_delete_failed));
                                                }
                                            }
                                            else
                                            {
                                                Toaster.makeToast(ShowLocationsActivity.this, getString(R.string.error_text_delete_failed));

                                                String errorMessage = String.format(
                                                        "ShowLocationsActivity.onClickAlertDialogPositiveDeleteElement:: removing %s and children from content failed!",
                                                        longClickedElement);
                                                Log.e(Constants.LOG_TAG, errorMessage);
                                                throw new IllegalStateException(errorMessage);
                                            }

                                            Snackbar snackbar = Snackbar.make(view, getString(R.string.action_undo_delete_element_text, longClickedElement.getName()), Snackbar.LENGTH_LONG);
                                            snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoDeleteElement:: undo delete [%s]...", longClickedElement));

                                                    if(longClickedElement.undoIsPossible && longClickedElement.undoDeleteElementAndChildren())
                                                    {
                                                        App.content.addElementAndChildren(longClickedElement);
                                                        updateContentRecyclerView();

                                                        Toaster.makeToast(ShowLocationsActivity.this, getString(R.string.action_element_restored_text, longClickedElement.getName()));

//                                                        contentRecyclerViewAdapter.smoothScrollToElement(longClickedElement);
                                                    }
                                                    else
                                                    {
                                                        Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoDeleteElement:: undo delete [%s] failed!", longClickedElement));
                                                        Toaster.makeToast(ShowLocationsActivity.this, getString(R.string.error_text_undo_not_possible));
                                                    }
                                                }
                                            });
                                            snackbar.show();
                                        }
                                    });

                                    builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onClickAlertDialogNegative:: canceled");
                                            dialog.dismiss();
                                        }
                                    });

                                    alertDialog = builder.create();
                                    alertDialog.setIcon(R.drawable.ic_baseline_warning);

                                    alertDialog.show();
                                    return true;

                                case REMOVE_ELEMENT:
                                    builder = new AlertDialog.Builder(ShowLocationsActivity.this);

                                    builder.setTitle(R.string.alert_dialog_remove_element_title);

                                    String alertMessage;
                                    if(longClickedElement.getParent().equals(App.content.getRootLocation()) && longClickedElement.hasChildrenOfInstance(Park.class))
                                    {
                                        alertMessage = getString(R.string.alert_dialog_remove_element_message_parent_is_root, longClickedElement.getName(), longClickedElement.getParent().getName());
                                    }
                                    else
                                    {
                                        alertMessage = getString(R.string.alert_dialog_remove_element_message, longClickedElement.getName(), longClickedElement.getParent().getName());
                                    }

                                    builder.setMessage(alertMessage);

                                    builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing [%s]...", longClickedElement));

                                            dialog.dismiss();

                                            if(App.content.deleteElement(longClickedElement))
                                            {
                                                if(longClickedElement.removeElement())
                                                {
                                                    currentElement = longClickedElement.getParent();
                                                    updateContentRecyclerView();
                                                }
                                                else
                                                {
                                                    Log.e(Constants.LOG_TAG, String.format(
                                                            "ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing %s failed - restoring content...", longClickedElement));
                                                    App.content.addElementAndChildren(longClickedElement);
                                                    Toaster.makeToast(ShowLocationsActivity.this, getString(R.string.error_text_remove_failed));
                                                }
                                            }
                                            else
                                            {
                                                Toaster.makeToast(ShowLocationsActivity.this, getString(R.string.error_text_delete_failed));

                                                String errorMessage = String.format("ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing %s from content failed!", longClickedElement);
                                                Log.e(Constants.LOG_TAG, errorMessage);
                                                throw new IllegalStateException(errorMessage);
                                            }

                                            Snackbar snackbar = Snackbar.make(view, getString(R.string.action_undo_remove_element_text, longClickedElement.getName()), Snackbar.LENGTH_LONG);
                                            snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoRemoveElement:: undo remove [%s]...", longClickedElement));

                                                    if(longClickedElement.undoIsPossible && longClickedElement.undoRemoveElement())
                                                    {
                                                        App.content.addElement(longClickedElement);
                                                        updateContentRecyclerView();

                                                        Toaster.makeToast(ShowLocationsActivity.this, getString(R.string.action_element_restored_text, longClickedElement.getName()));

                                                        //            this.contentRecyclerViewAdapter.smoothScrollToElement(this.longClickedElement);
                                                    }
                                                    else
                                                    {
                                                        Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoRemoveElement:: undo remove [%s] failed!", longClickedElement));
                                                        Toaster.makeToast(ShowLocationsActivity.this, getString(R.string.error_text_undo_not_possible));
                                                    }
                                                }
                                            });
                                            snackbar.show();
                                        }
                                    });

                                    builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onClickAlertDialogNegative:: canceled");
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog = builder.create();
                                    alertDialog.setIcon(R.drawable.ic_baseline_warning);
                                    alertDialog.show();
                                    return true;

                                case SORT_PARKS:
                                    ActivityTool.startActivitySortForResult(
                                            ShowLocationsActivity.this,
                                            Constants.REQUEST_SORT_PARKS,
                                            longClickedElement.getChildrenOfType(Park.class));
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
                return true;
            }
        };

        this.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                        this.currentElement.getChildrenOfType(Location.class),
                        initiallyExpandedElements,
                        Park.class,
                        recyclerOnClickListener);


        RecyclerView recyclerView = findViewById(android.R.id.content).findViewById(R.id.recyclerViewShowLocations);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.contentRecyclerViewAdapter);
    }

    private void updateContentRecyclerView()
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.updateContentRecyclerView:: updating RecyclerView...");
        this.contentRecyclerViewAdapter.updateContent(this.currentElement.getChildrenOfType(Location.class));
        this.contentRecyclerViewAdapter.notifyDataSetChanged();
    }
}
