package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowLocationsActivity extends BaseActivity
{
    private ShowLocationsActivityViewModel viewModel;
    private Element longClickedElement;

    private View.OnClickListener onClickListenerNavigationBar;
    private LinearLayout linearLayoutNavigationBar;
    private HorizontalScrollView horizontalScrollViewNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowLocationsActivity.onCreate:: creating activity...");
        setContentView(R.layout.activity_show_locations);
        super.onCreate(savedInstanceState);

        this.linearLayoutNavigationBar = findViewById(R.id.linearLayoutShowLocations_NavigationBar);
        this.horizontalScrollViewNavigationBar = findViewById(R.id.horizontalScrollViewShowLocations_NavigationBar);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowLocations);

        this.viewModel = ViewModelProviders.of(this).get(ShowLocationsActivityViewModel.class);

        if(this.viewModel.currentElement == null)
        {
            String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
            this.viewModel.currentElement = elementUuid != null ? App.content.getElementByUuid(UUID.fromString(elementUuid)) : App.content.getRootLocation();
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                    this.viewModel.currentElement.getChildrenOfType(Location.class),
                    new HashSet<Element>(),
                    Park.class);
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getString(R.string.title_locations_show), null);

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_locations_show)), getString(R.string.help_text_show_locations));

        this.onClickListenerNavigationBar = this.getNavigationBarOnClickListener();

        this.updateActivityView();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.viewModel.currentElement.isRootElement())
        {
            menu.add(Menu.NONE, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit_root_location);
        }

        if(this.viewModel.currentElement.getChildCountOfType(Location.class) > 1)
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
                ActivityTool.startActivityEditForResult(this, Constants.REQUEST_EDIT_ELEMENT, this.viewModel.currentElement);
                return true;

            case SORT_LOCATIONS:
                ActivityTool.startActivitySortForResult(
                        this,
                        Constants.REQUEST_SORT_LOCATIONS,
                        this.viewModel.currentElement.getChildrenOfType(Location.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));
        if(resultCode == RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_ADD_LOCATION)
            {

                //Todo: scroll to returned element
                //                String uuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                //                Element resultElement = App.content.fetchElementByUuidString(uuidString);
                updateContentRecyclerViewAdapter();
                //                this.contentRecyclerViewAdapter.smoothScrollToElement(resultElement);

            }
            else if(requestCode == Constants.REQUEST_SORT_LOCATIONS || requestCode == Constants.REQUEST_SORT_PARKS)
            {
                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> resultElements = App.content.fetchElementsByUuidStrings(resultElementsUuidStrings);

                Element parent = resultElements.get(0).getParent();
                Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult<SortElements>:: replacing children with sorted children in parent %s...", parent));
                parent.deleteChildren(resultElements);
                parent.addChildren(resultElements);
                this.updateContentRecyclerViewAdapter();

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    Element selectedElement = App.content.fetchElementByUuidString(selectedElementUuidString);
                    this.viewModel.contentRecyclerViewAdapter.smoothScrollToElement(selectedElement);
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowLocationsActivity.onActivityResult<SortElements>:: no selected element returned");
                }

            }
            else if(requestCode == Constants.REQUEST_EDIT_ELEMENT)
            {
                Element editedElement = App.content.getElementByUuid(UUID.fromString(data.getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
                this.updateActivityView();
                this.updateContentRecyclerViewAdapter();
                this.viewModel.contentRecyclerViewAdapter.smoothScrollToElement(editedElement);
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
                if(this.viewModel.currentElement.isRootElement())
                {
                    this.onToolbarHomeButtonBackClicked();
                }
                else
                {
                    Element previousElement = this.viewModel.recentElements.get(this.viewModel.recentElements.size() - 2);
                    Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActonKeyDown<KEYCODE_BACK>:: returning to previous element %s", previousElement));
                    this.viewModel.recentElements.remove(this.viewModel.currentElement);
                    this.viewModel.recentElements.remove(previousElement);
                    this.viewModel.currentElement = previousElement;
                    this.updateActivityView();
                    this.updateContentRecyclerViewAdapter();
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

    private void updateActivityView()
    {
        super.animateFloatingActionButtonTransition(null);
        this.updateNavigationBar();
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
                                ActivityTool.startActivityAddForResult(ShowLocationsActivity.this, Constants.REQUEST_ADD_LOCATION, viewModel.currentElement);
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

    private View.OnClickListener getNavigationBarOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.e(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClick:: LinearLayout[%s]", linearLayoutNavigationBar));

                Element element = (Element) view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar.onClick:: %s clicked", element));

                int length = viewModel.recentElements.size() - 1;
                for (int i = length; i >= 0; i--)
                {
                    if(viewModel.recentElements.get(i).equals(element))
                    {
                        viewModel.recentElements.remove(i);
                        break;
                    }
                    else
                    {
                        viewModel.recentElements.remove(i);
                    }
                }
                viewModel.currentElement = element;
                updateActivityView();
                updateContentRecyclerViewAdapter();
            }
        };
    }

    private void updateNavigationBar()
    {
        Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateNavigationBar:: updating NavigationBar...");

        this.linearLayoutNavigationBar.removeAllViews();

        if(this.viewModel.recentElements.isEmpty() && !this.viewModel.currentElement.isRootElement())
        {
            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateNavigationBar:: constructing navigation bar");
            this.viewModel.recentElements.clear();
            this.constructNavigationBar(this.viewModel.currentElement.getParent());
        }

        if(!this.viewModel.recentElements.contains(this.viewModel.currentElement))
        {
            Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: adding CurrentElement %s to RecentElements...", this.viewModel.currentElement));
            this.viewModel.recentElements.add(this.viewModel.currentElement);
        }

        for (Element recentElement : this.viewModel.recentElements)
        {
            Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: creating textView for recent element %s...", recentElement));
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.text_view_navigation_bar, linearLayoutNavigationBar, false);

            if(this.viewModel.recentElements.indexOf(recentElement) != this.viewModel.recentElements.size() -1)
            {
                Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_chevron_right));
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                textView.setText(recentElement.getName());
                textView.setTag(recentElement);
                textView.setOnClickListener(this.onClickListenerNavigationBar);
            }
            else
            {
                Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: %s is current element - applying special treatment", recentElement));
                textView.setText(StringTool.getSpannableString(recentElement.getName(), Typeface.BOLD_ITALIC));
            }

            this.linearLayoutNavigationBar.addView(textView);
        }

        this.linearLayoutNavigationBar.invalidate();

        this.horizontalScrollViewNavigationBar.post(new Runnable()
        {
            @Override
            public void run()
            {
                horizontalScrollViewNavigationBar.fullScroll(View.FOCUS_RIGHT);
            }
        });

        Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.updateNavigationBar:: NavigationBar holds #[%d] elements", this.viewModel.recentElements.size()));
    }

    private void constructNavigationBar(Element element)
    {
        Log.v(Constants.LOG_TAG, String.format("ShowLocationsActivity.constructNavigationBar:: adding %s to recent elements...", element));

        if(!element.isRootElement())
        {
            this.viewModel.recentElements.add(0, element);
            this.constructNavigationBar(element.getParent());
        }
        else
        {
            this.viewModel.recentElements.add(0, element);
        }
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Element element = (Element) view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickLocationRecyclerView:: %s clicked", element));

                if(element.isInstance(Location.class))
                {
                    viewModel.currentElement = element;
                    updateActivityView();
                    updateContentRecyclerViewAdapter();
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
                                    ActivityTool.startActivityEditForResult(ShowLocationsActivity.this, Constants.REQUEST_EDIT_ELEMENT, longClickedElement);
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
                                                    updateContentRecyclerViewAdapter();
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
                                                        updateContentRecyclerViewAdapter();

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
                                                    viewModel.currentElement = longClickedElement.getParent();
                                                    updateContentRecyclerViewAdapter();
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
                                                        updateContentRecyclerViewAdapter();

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
    }

    private void updateContentRecyclerViewAdapter()
    {
        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.updateContentRecyclerViewAdapter:: updating RecyclerView...");
        this.viewModel.contentRecyclerViewAdapter.updateDataSet(this.viewModel.currentElement.getChildrenOfType(Location.class));
        this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
    }
}
