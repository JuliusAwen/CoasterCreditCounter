package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
//    private Mode mode;
    private Element currentElement;
    private List<Element> recentElements = new ArrayList<>();
    private RecyclerView recyclerView;
    private ExpandableRecyclerAdapter contentRecyclerAdapter;
    private Element longClickedElement;

    //region @Override
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(Constants.LOG_TAG, "onCreate()");
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowLocationsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_locations);
        CoordinatorLayout coordinatorLayoutActivity = findViewById(R.id.coordinatorLayoutShowLocations);
        View showLocationsView = getLayoutInflater().inflate(R.layout.layout_show_locations, coordinatorLayoutActivity, false);
        coordinatorLayoutActivity.addView(showLocationsView);

        super.onCreate(savedInstanceState);
        super.addToolbar();
        super.addFloatingActionButton();
        super.addHelpOverlay(null, getString(R.string.title_show_locations));

        this.initializeContent();

        this.decorateToolbar();
        this.decorateFloatingActionButton();


        this.updateNavigationBar();
        this.createContentRecyclerView();
    }

    @Override
    protected void onResume()
    {
        Log.e(Constants.LOG_TAG, "onResume()");
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onResume:: called with CurrentElement%s", this.currentElement));

        this.updateContentRecyclerAdapter();
        this.updateNavigationBar();

        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.currentElement.getParent() == null)
        {
            menu.add(0, Selection.EDIT_ELEMENT.ordinal(), Menu.NONE, R.string.selection_rename_root);
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
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.e(Constants.LOG_TAG, "onRestoreInstanceState()");

        super.onRestoreInstanceState(savedInstanceState);

        this.recentElements = super.content.fetchElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentElement = super.content.getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));

        this.updateContentRecyclerAdapter();
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
    //endregion

    private void initializeContent()
    {
        String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        this.currentElement = elementUuid != null ? super.content.getElementByUuid(UUID.fromString(elementUuid)) :  super.content.getRootElement();

        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.initializeContent:: initialized with currentElement%s", this.currentElement));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(getString(R.string.title_show_locations), null);
    }

    //region FLOATING ACTION BUTTON
    private void decorateFloatingActionButton()
    {
        Log.e(Constants.LOG_TAG, "decorateFAB()");
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
                Intent intent = new Intent(getApplicationContext(), AddLocationActivity.class);
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
    //endregion

    //region NAVIGATION BAR
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
                    onClickNavigationBar(view);
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
        updateContentRecyclerAdapter();
        updateNavigationBar();
    }
    //endregion

    //region CONTENT RECYCLER VIEW
    private void createContentRecyclerView()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                onClickContentRecyclerView(view);
            }

            @Override
            public void onLongClick(final View view, int position)
            {
                onLongClickContentRecyclerView(view);
            }
        };

        this.contentRecyclerAdapter = new ExpandableRecyclerAdapter(new ArrayList<>(this.currentElement.getChildren()), recyclerOnClickListener);
        this.recyclerView = this.findViewById(android.R.id.content).findViewById(R.id.recyclerViewShowLocations);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.contentRecyclerAdapter);
    }

    private void onClickContentRecyclerView(View view)
    {
        Element element = (Element) view.getTag();

        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.onClick:: %s clicked", element));

        if(element.isInstance(Location.class))
        {
            this.currentElement = element;
            updateContentRecyclerAdapter();
            updateNavigationBar();
        }
        else if(element.isInstance(Park.class))
        {
            //Todo: implement show park activity
            Toaster.makeToast(getApplicationContext(), "ShowPark not yet implemented");
        }
    }

    private void onLongClickContentRecyclerView(final View view)
    {
        this.longClickedElement = (Element) view.getTag();
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.createContentRecyclerView.onLongClick:: %s long clicked", longClickedElement));

        if(this.longClickedElement.isInstance(Location.class))
        {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);

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
                    return onClickMenuItemPopupMenuLongClickContentRecyclerView(item, view);
                }
            });
            popupMenu.show();
        }
    }

    private boolean onClickMenuItemPopupMenuLongClickContentRecyclerView(MenuItem item, final View view)
    {
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickMenuItemPopupMenuLongClickContentRecyclerView:: [%S] selected", selection));

        switch (selection)
        {
            case EDIT_ELEMENT:
                startEditLocationActivity(this.longClickedElement);
                return true;

            case DELETE_ELEMENT:
                builder = new AlertDialog.Builder(ShowLocationsActivity.this);

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
                builder = new AlertDialog.Builder(ShowLocationsActivity.this);

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

        if(super.content.deleteElementAndChildren(this.longClickedElement))
        {
            if(this.longClickedElement.deleteElementAndChildren())
            {
                updateContentRecyclerAdapter();
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format(
                        "ShowLocationsActivity.onClickAlertDialogPositiveButtonDeleteElement:: deleting %s and children failed - restoring content...",
                        longClickedElement));

                super.content.addElementAndChildren(longClickedElement);
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
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoDeleteElement:: undo delete [%s]...",
                this.longClickedElement));

        if(this.longClickedElement.undoPossible && this.longClickedElement.undoDeleteElementAndChildren())
        {
            super.content.addElementAndChildren(this.longClickedElement);
            updateContentRecyclerAdapter();

            Toaster.makeToast(getApplicationContext(), getString(R.string.action_element_restored_text, longClickedElement.getName()));

            smoothScrollToElement(this.longClickedElement);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoDeleteElement:: undo delete [%s] failed!",
                    this.longClickedElement));

            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_undo_not_possible));
        }
    }

    private void onClickAlertDialogPositiveButtonRemoveElement(DialogInterface dialog, View view)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing [%s]...", this.longClickedElement));

        dialog.dismiss();

        if(super.content.deleteElement(this.longClickedElement))
        {
            if(this.longClickedElement.removeElement())
            {
                this.currentElement = this.longClickedElement.getParent();
                updateContentRecyclerAdapter();
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format(
                        "ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing %s failed - restoring content...",
                        this.longClickedElement));

                super.content.addElementAndChildren(this.longClickedElement);
                Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_remove_failed));
            }
        }
        else
        {
            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_delete_failed));

            String errorMessage = String.format(
                    "ShowLocationsActivity.onClickAlertDialogPositiveButtonRemoveElement:: removing %s from content failed!", this.longClickedElement);

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
            super.content.addElement(this.longClickedElement);
            updateContentRecyclerAdapter();

            Toaster.makeToast(getApplicationContext(), getString(R.string.action_element_restored_text, longClickedElement.getName()));

            smoothScrollToElement(this.longClickedElement);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickSnackbarUndoRemoveElement:: undo remove [%s] failed!",
                    this.longClickedElement));
            Toaster.makeToast(getApplicationContext(), getString(R.string.error_text_undo_not_possible));
        }
    }

    private void onClickAlertDialogNegativeButton(DialogInterface dialog)
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onClickSnackbarUndoRemoveElement:: canceled");
        dialog.dismiss();
    }

    private void updateContentRecyclerAdapter()
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.updateContentRecyclerAdapter:: updating RecyclerView...");
        this.contentRecyclerAdapter.updateList(new ArrayList<>(this.currentElement.getChildrenOfInstance(Location.class)));
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
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onOptionsItemSelected:: starting SortElementsActivity...");
        Intent intent = new Intent(this, SortElementsActivity.class);
        intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
        startActivityForResult(intent, Constants.REQUEST_SORT_ELEMENTS);
    }
    //endregion

    private void smoothScrollToElement(Element element)
    {
        if(this.currentElement.containsChild(element))
        {
            int position = this.currentElement.indexOfChild(element);
            recyclerView.smoothScrollToPosition(position);
        }
    }
}
