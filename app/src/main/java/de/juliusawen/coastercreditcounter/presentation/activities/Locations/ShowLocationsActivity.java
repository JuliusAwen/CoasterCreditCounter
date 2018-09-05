package de.juliusawen.coastercreditcounter.presentation.activities.Locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Mode;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public class ShowLocationsActivity extends BaseActivity
{
    private Mode mode;
    private Element currentElement;
    private List<Element> recentElements = new ArrayList<>();
    private RecyclerView recyclerView;
    private ExpandableRecyclerAdapter expandableRecyclerAdapter;
    private Element longClickedElement;
    private MenuItem menuItemSwitchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, Constants.LOG_DIVIDER);
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onCreate:: creating activity...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_locations);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(!super.isInitialized)
        {
            this.initializeContent();
            this.initializeViews();
        }
    }

    @Override
    protected void onResume()
    {
        Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onResume:: called with %s", this.currentElement));


        if(super.content != null)
        {
            this.updateExpandableRecyclerAdapter();
            this.updateNavigationBar();
        }

        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        menu.add(0, Selection.SWITCH_MODE.ordinal(), Menu.NONE, R.string.selection_switch_mode);
        this.menuItemSwitchMode = menu.findItem(Selection.SWITCH_MODE.ordinal());
        this.menuItemSwitchMode.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        this.setMenuItemSwitchModeIcon();

        if(this.mode.equals(Mode.MANAGE))
        {
            if(this.currentElement.getParent() == null)
            {
                menu.add(0, Selection.EDIT_ELEMENT.ordinal(), Menu.NONE, R.string.selection_rename_root);
            }
        }

        if(this.currentElement.getChildCountOfInstance(Location.class) > 1)
        {
            menu.add(0, Selection.SORT_ELEMENTS.ordinal(), Menu.NONE, R.string.selection_sort_locations);
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
            case SWITCH_MODE:
                switch(this.mode)
                {
                    case MANAGE:
                        this.mode = Mode.BROWSE;
                        super.setFloatingActionButtonVisibility(false);
                        break;

                    case BROWSE:
                        this.mode = Mode.MANAGE;
                        super.setFloatingActionButtonVisibility(true);
                        break;
                }
                super.setToolbarTitle(this.fetchToolbarTitle());
                super.setHelpOverlayText(this.fetchHelpOverlayText());
                this.setMenuItemSwitchModeIcon();
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onOptionItemSelected:: mode switched to [%S]", this.mode));
                return true;

            case EDIT_ELEMENT:
                this.startEditLocationActivity(this.currentElement);
                return true;

            case SORT_ELEMENTS:
                this.startSortElementsActivity(this.currentElement.getChildrenOfInstance(Location.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.e(Constants.LOG_TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getUuidStringsFromElements(this.recentElements));
        outState.putString(Constants.KEY_ELEMENT, this.currentElement.getUuid().toString());
        outState.putInt(Constants.KEY_MODE, this.mode.ordinal());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.e(Constants.LOG_TAG, "onRestoreInstanceState()");

        super.onRestoreInstanceState(savedInstanceState);

        this.recentElements = super.content.fetchElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentElement = super.content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
        this.mode = Mode.values()[savedInstanceState.getInt(Constants.KEY_MODE)];

        this.updateExpandableRecyclerAdapter();
        this.updateNavigationBar();
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(requestCode == Constants.REQUEST_ADD_ELEMENT)
        {
            if(resultCode == RESULT_OK)
            {
                String uuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                Element resultElement = super.content.fetchElementFromUuidString(uuidString);
                this.currentElement = resultElement;

                this.smoothScrollToElement(resultElement);
            }
        }
        else if(requestCode == Constants.REQUEST_SORT_ELEMENTS)
        {
            if(resultCode == RESULT_OK)
            {
                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> resultElements = super.content.fetchElementsFromUuidStrings(resultElementsUuidStrings);
                Element parentElement = resultElements.get(0).getParent();

                parentElement.deleteChildren(resultElements);
                parentElement.addChildren(resultElements);

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                Element selectedElement = super.content.fetchElementFromUuidString(selectedElementUuidString);
                if(selectedElement != null)
                {
                    this.smoothScrollToElement(selectedElement);
                }
            }
        }
    }

    private void initializeContent()
    {
        this.mode = Mode.values()[getIntent().getIntExtra(Constants.EXTRA_MODE, Mode.BROWSE.ordinal())];

        String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        if(elementUuid != null)
        {
            this.currentElement = super.content.getElementByUuid(UUID.fromString(elementUuid));
        }
        else
        {
            this.currentElement = super.content.getRootElement();
        }

        Log.e(Constants.LOG_TAG, String.format("InitializeContent currentElement%s", this.currentElement));
    }

    private void initializeViews()
    {
        Log.e(Constants.LOG_TAG, "initializeViews");
        CoordinatorLayout coordinatorLayoutActivity = findViewById(R.id.coordinatorLayoutShowLocations);
        View showLocationsView = getLayoutInflater().inflate(R.layout.layout_show_locations, coordinatorLayoutActivity, false);
        coordinatorLayoutActivity.addView(showLocationsView);

        super.createToolbar(this.fetchToolbarTitle(), null, true);
        this.createFloatingActionButton();
        this.updateNavigationBar();
        this.createContentRecyclerView(showLocationsView);
        super.createHelpOverlayFragment(this.fetchHelpOverlayText(), false);
    }

    private void setMenuItemSwitchModeIcon()
    {
        switch(this.mode)
        {
            case MANAGE:
                this.menuItemSwitchMode.setIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_cancel)));
                break;

            case BROWSE:
                this.menuItemSwitchMode.setIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_create)));
                break;
        }
    }

    private void updateNavigationBar()
    {
        View view = this.findViewById(android.R.id.content).getRootView();

        if(!this.recentElements.contains(this.currentElement))
        {
            this.recentElements.add(this.currentElement);
        }

        LinearLayout linearLayoutNavigationBar = view.findViewById(R.id.linearLayoutShowLocations_NavigationBar);
        linearLayoutNavigationBar.invalidate();
        linearLayoutNavigationBar.removeAllViews();

        for (Element recentElement : this.recentElements)
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_navigation_bar, linearLayoutNavigationBar, false);

            Button button = buttonView.findViewById(R.id.buttonNavigationBar);

            if(this.recentElements.indexOf(recentElement) != 0)
            {
                Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_chevron_left));
                button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }

            if(this.recentElements.indexOf(recentElement) != this.recentElements.size() -1)
            {
                button.setText(recentElement.getName());
            }
            else
            {
                button.setText(StringTool.getSpannableString(recentElement.getName(), Typeface.BOLD_ITALIC));
            }

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
                        if (recentElements.get(i).equals(element))
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
                    updateExpandableRecyclerAdapter();
                    updateNavigationBar();
                }
            });

            linearLayoutNavigationBar.addView(buttonView);

            final HorizontalScrollView horizontalScrollView = view.findViewById(R.id.horizontalScrollViewShowLocations_NavigationBar);
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

    private void createFloatingActionButton()
    {
        final FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        super.createFloatingActionButton(floatingActionButton, DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ShowLocationsActivity.createFloatingActionButton.onClick:: FloatingActionButton clicked");

                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), floatingActionButton);

                popupMenu.getMenu().add(0, Selection.ADD_ELEMENT.ordinal(), Menu.NONE, R.string.selection_add_location);
                popupMenu.getMenu().add(0, Selection.ADD_PARK.ordinal(), Menu.NONE, R.string.selection_add_park);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Intent intent;

                        Selection selection = Selection.values()[item.getItemId()];
                        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createFloatingActionButton.PopupMenu.onMenuItemClick:: [%S] selected", selection));

                        switch (selection)
                        {
                            case ADD_ELEMENT:
                                intent = new Intent(getApplicationContext(), AddLocationActivity.class);
                                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, currentElement.getUuid().toString());
                                startActivityForResult(intent, Constants.REQUEST_ADD_ELEMENT);
                                return true;

                            case ADD_PARK:
                                //Todo: implement add park activity
                                Toaster.makeToast(getApplicationContext(), "AddPark not yet implemented");
                                return true;

                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
            }
        };

        super.setFloatingActionButtonOnClickListener(onClickListener);

        if(this.mode.equals(Mode.BROWSE))
        {
            super.setFloatingActionButtonVisibility(false);
        }
    }

    private void createContentRecyclerView(View view)
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Element element = (Element) view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.onClick:: %s clicked", element));

                if(element.isInstance(Location.class))
                {
                    currentElement = element;
                    updateExpandableRecyclerAdapter();
                    updateNavigationBar();
                }
                else if(element.isInstance(Park.class))
                {
                    //Todo: implement show park activity
                    Toaster.makeToast(getApplicationContext(), "ShowPark not yet implemented");
                }
            }

            @Override
            public void onLongClick(final View view, int position)
            {
                longClickedElement = (Element) view.getTag();
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.onLongClick:: %s long clicked", longClickedElement));


                if(longClickedElement.isInstance(Location.class))
                {
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);

                    if(mode == Mode.MANAGE)
                    {
                        popupMenu.getMenu().add(0, Selection.EDIT_ELEMENT.ordinal(), Menu.NONE, R.string.selection_edit_element);
                        popupMenu.getMenu().add(0, Selection.DELETE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_delete_element);

                        if(longClickedElement.hasChildren())
                        {
                            popupMenu.getMenu().add(0, Selection.REMOVE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_remove_element);
                        }
                    }

                    if(longClickedElement.getChildCountOfInstance(Park.class) > 1)
                    {
                        popupMenu.getMenu().add(0, Selection.SORT_ELEMENTS.ordinal(), Menu.NONE, R.string.selection_sort_parks);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            AlertDialog.Builder builder;
                            AlertDialog alertDialog;

                            Selection selection = Selection.values()[item.getItemId()];
                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.PopupMenu.onMenuItemClick:: [%S] selected", selection));

                            switch (selection)
                            {
                                case EDIT_ELEMENT:
                                    startEditLocationActivity(longClickedElement);
                                    return true;

                                case DELETE_ELEMENT:
                                    builder = new AlertDialog.Builder(ShowLocationsActivity.this);

                                    builder.setTitle(R.string.alert_dialog_delete_element_title);
                                    builder.setMessage(getString(R.string.alert_dialog_delete_element_message, longClickedElement.getName()));
                                    builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.AlertDialog.onClick:: deleting %s...", longClickedElement));

                                            dialog.dismiss();

                                            if(content.deleteElementAndChildren(longClickedElement))
                                            {
                                                if(longClickedElement.deleteElementAndChildren())
                                                {
                                                    updateExpandableRecyclerAdapter();
                                                }
                                                else
                                                {
                                                    Log.e(Constants.LOG_TAG, String.format(
                                                            "ShowLocationsActivity.createContentRecyclerView.AlertDialog.onClick:: deleting %s and children failed - restoring content...",
                                                            longClickedElement));

                                                    content.addElementAndChildren(longClickedElement);
                                                    Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_delete_failed));
                                                }
                                            }
                                            else
                                            {
                                                Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_delete_failed));

                                                String errorMessage = String.format(
                                                        "ShowLocationsActivity.createContentRecyclerView.AlertDialog.onClick:: removing %s and children from content failed!",
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
                                                    Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.Snackbar.onClick:: undo delete [%s]...",
                                                            longClickedElement));

                                                    if(longClickedElement.undoPossible && longClickedElement.undoDeleteElementAndChildren())
                                                    {
                                                        content.addElementAndChildren(longClickedElement);
                                                        updateExpandableRecyclerAdapter();

                                                        Toaster.makeToast(getApplicationContext(), getString(R.string.action_element_restored_text, longClickedElement.getName()));

                                                        smoothScrollToElement(longClickedElement);
                                                    }
                                                    else
                                                    {
                                                        Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.Snackbar.onClick:: undo delete [%s] failed!",
                                                                longClickedElement));

                                                        Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_undo_not_possible));
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
                                            Log.i(Constants.LOG_TAG, "ShowLocationsActivity.createContentRecyclerView.AlertDialog.onClick:: canceled");
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
                                    builder.setMessage(getString(R.string.alert_dialog_remove_element_message, longClickedElement.getName(), longClickedElement.getParent().getName()));

                                    builder.setPositiveButton(R.string.text_accept, new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.AlertDialog.onClick:: removing [%s]...", longClickedElement));

                                            dialog.dismiss();

                                            if(content.deleteElement(longClickedElement))
                                            {
                                                if( longClickedElement.removeElement())
                                                {
                                                    currentElement = longClickedElement.getParent();
                                                    updateExpandableRecyclerAdapter();
                                                }
                                                else
                                                {
                                                    Log.e(Constants.LOG_TAG, String.format(
                                                            "ShowLocationsActivity.createContentRecyclerView.AlertDialog.onClick:: removing %s failed - restoring content...",
                                                            longClickedElement));

                                                    content.addElementAndChildren(longClickedElement);
                                                    Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_remove_failed));
                                                }
                                            }
                                            else
                                            {
                                                Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_delete_failed));

                                                String errorMessage = String.format(
                                                        "ShowLocationsActivity.createContentRecyclerView.AlertDialog.onClick:: removing %s from content failed!", longClickedElement);

                                                Log.e(Constants.LOG_TAG, errorMessage);
                                                throw new IllegalStateException(errorMessage);
                                            }

                                            Snackbar snackbar = Snackbar.make(view, getString(R.string.action_undo_remove_element_text, longClickedElement.getName()), Snackbar.LENGTH_LONG);
                                            snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.Snackbar.onClick:: undo remove [%s]...", longClickedElement));

                                                    if(longClickedElement.undoPossible && longClickedElement.undoRemoveElement())
                                                    {
                                                        content.addElement(longClickedElement);
                                                        updateExpandableRecyclerAdapter();

                                                        Toaster.makeToast(getApplicationContext(), getString(R.string.action_element_restored_text, longClickedElement.getName()));

                                                        smoothScrollToElement(longClickedElement);
                                                    }
                                                    else
                                                    {
                                                        Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.Snackbar.onClick:: undo remove [%s] failed!",
                                                                longClickedElement));
                                                        Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_undo_not_possible));
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
                                            Log.i(Constants.LOG_TAG, "ShowLocationsActivity.createContentRecyclerView.AlertDialog.onClick:: canceled");
                                            dialog.dismiss();
                                        }
                                    });

                                    alertDialog = builder.create();
                                    alertDialog.setIcon(R.drawable.ic_baseline_warning);

                                    alertDialog.show();
                                    return true;

                                case SORT_ELEMENTS:
                                    startSortElementsActivity(longClickedElement.getChildrenOfInstance(Park.class));

                                default:
                                    return false;
                            }

                        }
                    });
                    popupMenu.show();
                }
            }
        };

        this.expandableRecyclerAdapter = new ExpandableRecyclerAdapter(new ArrayList<>(this.currentElement.getChildren()), recyclerOnClickListener);
        this.recyclerView = view.findViewById(R.id.recyclerViewShowLocations);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(expandableRecyclerAdapter);
    }

    private void updateExpandableRecyclerAdapter()
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.updateExpandableRecyclerAdapter:: updating RecyclerView...");
        this.expandableRecyclerAdapter.updateList(new ArrayList<>(this.currentElement.getChildrenOfInstance(Location.class)));
    }

    private String fetchToolbarTitle()
    {
        return this.mode.equals(Mode.MANAGE) ? getString(R.string.title_manage_locations) : getString(R.string.title_browse_locations);
    }

    private CharSequence fetchHelpOverlayText()
    {
        return  this.mode.equals(Mode.MANAGE) ? getText(R.string.help_text_manage_locations) : getText(R.string.help_text_browse_locations);
    }

    private void startEditLocationActivity(Element elementToEdit)
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.startEditLocationActivity:: starting EditLocationsActivity...");
        Intent intent = new Intent(getApplicationContext(), EditLocationActivity.class);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, elementToEdit.getUuid().toString());
        startActivity(intent);
    }

    private void startSortElementsActivity(List<Element> elementsToSort)
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onOptionsItemSelected:: starting SortElementsActivity...");
        Intent intent = new Intent(this, SortElementsActivity.class);
        intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
        startActivityForResult(intent, Constants.REQUEST_SORT_ELEMENTS);
    }

    private void smoothScrollToElement(Element element)
    {
        if(this.currentElement.containsChild(element))
        {
            int position = this.currentElement.indexOfChild(element);
            recyclerView.smoothScrollToPosition(position);
        }
    }
}
