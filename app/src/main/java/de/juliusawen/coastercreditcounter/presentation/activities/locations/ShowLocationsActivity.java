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
import de.juliusawen.coastercreditcounter.presentation.activities.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.activities.parks.ShowParkActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public class ShowLocationsActivity extends BaseActivity
{
    private Element currentElement;
    private List<Element> recentElements = new ArrayList<>();

    private Element longClickedElement;

    private ExpandableRecyclerAdapter LocationRecyclerAdapter;


    //region @Override
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowLocationsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_locations);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addHelpOverlay(getString(R.string.title_help, getString(R.string.subtitle_show_locations)), getString(R.string.help_text_show_locations));

        super.addToolbar();
        this.decorateToolbar();

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();

        this.updateNavigationBar();
        this.createLocationRecyclerAdapter();
    }

    @Override
    protected void onResume()
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onResume:: called with CurrentElement%s", this.currentElement));
        this.updateActivityView();
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.currentElement.isRootElement())
        {
            menu.add(Menu.NONE, Selection.EDIT_ELEMENT.ordinal(), Menu.NONE, R.string.selection_rename_root_location);
        }

        if(this.currentElement.getChildCountOfInstance(Location.class) > 1)
        {
            menu.add(Menu.NONE, Selection.SORT_ELEMENTS.ordinal(), Menu.NONE, R.string.selection_sort_locations);
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
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getUuidStringsFromElements(this.recentElements));
        outState.putString(Constants.KEY_ELEMENT, this.currentElement.getUuid().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.recentElements = content.fetchElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentElement = content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));
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
                Element resultElement = content.fetchElementFromUuidString(uuidString);
                this.currentElement = resultElement;
                Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult<AddElement>:: scrolling to result element %s...", resultElement));
                this.LocationRecyclerAdapter.smoothScrollToElement(resultElement);
            }
        }
        else if(requestCode == Constants.REQUEST_SORT_ELEMENTS)
        {
            if(resultCode == RESULT_OK)
            {
                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> resultElements = content.fetchElementsFromUuidStrings(resultElementsUuidStrings);
                Element parentElement = resultElements.get(0).getParent();

                Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult<SortElements>:: replacing children with sorted children in parent %s", parentElement));

                parentElement.deleteChildren(resultElements);
                parentElement.addChildren(resultElements);

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    Element selectedElement = content.fetchElementFromUuidString(selectedElementUuidString);
                    Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult<SortElements>:: scrolling to selected element %s...", selectedElement));
                    this.LocationRecyclerAdapter.smoothScrollToElement(selectedElement);
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowLocationsActivity.onActivityResult<SortElements>:: no selected element returned");
                }

            }
        }
    }
    //endregion

    private void initializeContent()
    {
        String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        this.currentElement = elementUuid != null ? content.getElementByUuid(UUID.fromString(elementUuid)) : content.getRootElement();

        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.initializeContent:: initialized with currentElement%s", this.currentElement));
    }

    private void updateActivityView()
    {
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
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onClickFloatingActionButton::");

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), super.getFloatingActionButton());

        popupMenu.getMenu().add(0, Selection.ADD_ELEMENT.ordinal(), Menu.NONE, R.string.selection_add_location);
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
            case ADD_ELEMENT:
                this.startActivityAddLocation();
                return true;

            case ADD_PARK:
                this.startActivityAddPark();
                return true;

            default:
                return false;
        }
    }

    private void startActivityAddLocation()
    {
        Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, currentElement.getUuid().toString());
        startActivityForResult(intent, Constants.REQUEST_ADD_ELEMENT);
    }

    private void startActivityAddPark()
    {
        //Todo: implement add park activity
        Toaster.makeToast(getApplicationContext(), "AddPark not yet implemented");

//        Intent intent = new Intent(getApplicationContext(), AddParkActivity.class);
//        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, currentElement.getUuid().toString());
//        startActivityForResult(intent, Constants.REQUEST_ADD_PARK);
    }
    //endregion

    //region NAVIGATION BAR
    private void updateNavigationBar()
    {
        View view = this.findViewById(android.R.id.content).getRootView();

        LinearLayout linearLayoutNavigationBar = view.findViewById(R.id.linearLayoutShowLocations_NavigationBar);
        linearLayoutNavigationBar.invalidate();
        linearLayoutNavigationBar.removeAllViews();

        if(!this.recentElements.contains(this.currentElement))
        {
            this.recentElements.add(this.currentElement);
        }

        for (Element recentElement : this.recentElements)
        {
            if(this.recentElements.indexOf(recentElement) != this.recentElements.size() -1 || recentElement.isRootElement())
            {
                View buttonView = getLayoutInflater().inflate(R.layout.button_navigation_bar, linearLayoutNavigationBar, false);

                Button button = buttonView.findViewById(R.id.buttonNavigationBar);

                if(this.currentElement.isRootElement() && recentElement.isRootElement())
                {
                    button.setText(StringTool.getSpannableString(getString(R.string.root_location), Typeface.BOLD_ITALIC));
                }
                else
                {
                    Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_chevron_right));
                    button.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    button.setText(recentElement.getName());
                }

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

                linearLayoutNavigationBar.addView(buttonView);
            }
        }

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
            public void onLongClick(final View view, int position)
            {
                onLongClickLocationRecyclerView(view);
            }
        };

        this.LocationRecyclerAdapter = new ExpandableRecyclerAdapter(new ArrayList<>(this.currentElement.getChildrenOfInstance(Location.class)), recyclerOnClickListener);
        RecyclerView recyclerView = this.findViewById(android.R.id.content).findViewById(R.id.recyclerViewShowLocations);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.LocationRecyclerAdapter);
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
            Intent intent = new Intent(this, ShowParkActivity.class);
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            startActivity(intent);
        }
    }

    private void onLongClickLocationRecyclerView(final View view)
    {
        this.longClickedElement = (Element) view.getTag();
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onLongClickLocationRecyclerView:: %s long clicked", longClickedElement));

        if(this.longClickedElement.isInstance(Location.class))
        {
            PopupMenu popupMenu = new PopupMenu(this, view);

            popupMenu.getMenu().add(0, Selection.EDIT_ELEMENT.ordinal(), Menu.NONE, R.string.selection_edit_element);
            popupMenu.getMenu().add(0, Selection.DELETE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_delete_element);

            if(this.longClickedElement.hasChildren())
            {
                popupMenu.getMenu().add(0, Selection.REMOVE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_remove_element);
            }

            if(this.longClickedElement.getChildCountOfInstance(Park.class) > 1)
            {
                popupMenu.getMenu().add(0, Selection.SORT_ELEMENTS.ordinal(), Menu.NONE, R.string.selection_sort_parks);
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
            case EDIT_ELEMENT:
                startEditLocationActivity(this.longClickedElement);
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
                if(this.longClickedElement.getParent().equals(super.content.getRootElement()) && this.longClickedElement.hasChildrenOfInstance(Park.class))
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

            case SORT_ELEMENTS:
                startSortElementsActivity(this.longClickedElement.getChildrenOfInstance(Park.class));

            default:
                return false;
        }
    }

    private void onClickAlertDialogPositiveButtonDeleteElement(DialogInterface dialog, View view)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickAlertDialogPositiveButtonDeleteElement:: deleting %s...", longClickedElement));

        dialog.dismiss();

        if(content.deleteElementAndChildren(this.longClickedElement))
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

                content.addElementAndChildren(longClickedElement);
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

        if(this.longClickedElement.undoPossible && this.longClickedElement.undoDeleteElementAndChildren())
        {
            content.addElementAndChildren(this.longClickedElement);
            updateLocationRecyclerView();

            Toaster.makeToast(getApplicationContext(), getString(R.string.action_element_restored_text, longClickedElement.getName()));

            this.LocationRecyclerAdapter.smoothScrollToElement(this.longClickedElement);
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

        if(content.deleteElement(this.longClickedElement))
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

                content.addElementAndChildren(this.longClickedElement);
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

        if(this.longClickedElement.undoPossible && longClickedElement.undoRemoveElement())
        {
            content.addElement(this.longClickedElement);
            updateLocationRecyclerView();

            Toaster.makeToast(getApplicationContext(), getString(R.string.action_element_restored_text, longClickedElement.getName()));

            this.LocationRecyclerAdapter.smoothScrollToElement(this.longClickedElement);
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
        this.LocationRecyclerAdapter.updateList(new ArrayList<>(this.currentElement.getChildrenOfInstance(Location.class)));
    }
    //endregion

    //region START ACTIVITIES
    private void startEditLocationActivity(Element elementToEdit)
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.startEditLocationActivity:: starting EditLocationsActivity...");
        Intent intent = new Intent(getApplicationContext(), EditLocationActivity.class);
        intent.putExtra(Constants.EXTRA_ELEMENT_UUID, elementToEdit.getUuid().toString());
        startActivity(intent);
    }

    private void startSortElementsActivity(List<Element> elementsToSort)
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.startSortElementsActivity:: starting SortElementsActivity...");
        Intent intent = new Intent(this, SortElementsActivity.class);
        intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
        startActivityForResult(intent, Constants.REQUEST_SORT_ELEMENTS);
    }
    //endregion
}
