package de.juliusawen.coastercreditcounter.frontend.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.Element;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Location;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.MenuType;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;
import de.juliusawen.coastercreditcounter.toolbox.MenuAgent;
import de.juliusawen.coastercreditcounter.toolbox.ResultFetcher;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowLocationsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private ShowLocationsActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private boolean actionConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowLocationsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_locations);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(ShowLocationsActivityViewModel.class);

            if(this.viewModel.optionsMenuAgent == null)
            {
                this.viewModel.optionsMenuAgent = new MenuAgent(MenuType.OPTIONS_MENU);
            }

            if(this.viewModel.currentLocation == null)
            {
                String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                this.viewModel.currentLocation = elementUuid != null ? App.content.getContentByUuid(UUID.fromString(elementUuid)) : App.content.getRootLocation();
            }

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(Location.class);
                childTypesToExpand.add(Park.class);

                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                        new ArrayList<>(Collections.singleton(this.viewModel.currentLocation)),
                        childTypesToExpand)
                        .setTypefaceForType(Location.class, Typeface.BOLD);
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());

            this.recyclerView = findViewById(R.id.recyclerViewShowLocations);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_locations)), getString(R.string.help_text_show_locations));

            this.setSelectionModeEnabled(this.viewModel.selectionMode);

        }
    }

    @Override
    protected void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }


    //region OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(App.isInitialized)
        {
            this.viewModel.optionsMenuAgent
                    .addMenuItem(MenuAgent.EXPAND_ALL)
                    .addMenuItem(MenuAgent.COLLAPSE_ALL)
                    .create(menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        this.viewModel.optionsMenuAgent
                .setEnabled(MenuAgent.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .setEnabled(MenuAgent.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                .prepare(menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(this.viewModel.optionsMenuAgent.handleMenuItemSelected(item, this))
        {
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean handleMenuItemExpandAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.expandAll();
        return true;
    }

    @Override
    public boolean handleMenuItemCollapseAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.collapseAll();
        return true;
    }

    //endregion OPTIONS MENU


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(this.viewModel.selectionMode)
            {
                this.setSelectionModeEnabled(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setSelectionModeEnabled(boolean enabled)
    {
        Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.setSelectionModeEnabled:: selection mode enabled[%S]", enabled));
        ShowLocationsActivity.super.setToolbarTitleAndSubtitle(
                enabled ? getString(R.string.title_relocate) : getString(R.string.title_locations),
                enabled ? getString(R.string.subtitle_relocate, this.viewModel.longClickedElement.getName()) : null);
        this.viewModel.selectionMode = enabled;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == RESULT_OK)
        {

            if(requestCode == Constants.REQUEST_CODE_CREATE_LOCATION || requestCode == Constants.REQUEST_CODE_CREATE_PARK)
            {
                IElement resultElement = ResultFetcher.fetchResultElement(data);

                super.markForUpdate(resultElement.getParent());

                this.updateContentRecyclerView(true);
            }
            else if(requestCode == Constants.REQUEST_CODE_SORT_LOCATIONS || requestCode == Constants.REQUEST_CODE_SORT_PARKS)
            {
                List<IElement> resultElements = ResultFetcher.fetchResultElements(data);

                IElement parent = resultElements.get(0).getParent();
                Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult<SortElements>:: reordering %s's children...", parent));
                parent.reorderChildren(resultElements);

                this.updateContentRecyclerView(true);

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    IElement selectedElement = App.content.getContentByUuid(UUID.fromString(selectedElementUuidString));
                    this.viewModel.contentRecyclerViewAdapter.scrollToItem(selectedElement);
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowLocationsActivity.onActivityResult<SortElements>:: no selected element returned");
                }

                super.markForUpdate(parent);
            }
            else if(requestCode == Constants.REQUEST_CODE_EDIT_LOCATION || requestCode == Constants.REQUEST_CODE_EDIT_PARK)
            {
                IElement editedElement = ResultFetcher.fetchResultElement(data);

                super.markForUpdate(editedElement);

                this.updateContentRecyclerView(false);
            }
        }
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                IElement element = (IElement) view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickRecyclerView:: %s clicked", element));

                if(!viewModel.selectionMode)
                {
                    if(element instanceof Location)
                    {
                        viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                    }
                    else if(element instanceof Park)
                    {
                        ActivityDistributor.startActivityShow(ShowLocationsActivity.this, Constants.REQUEST_CODE_SHOW_PARK, element);
                    }
                }
                else
                {
                    handleRelocation(element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (Element) view.getTag();
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onLongClickRecyclerView:: %s long clicked", viewModel.longClickedElement));

                if(!viewModel.selectionMode)
                {
                    PopupMenu popupMenu = getRecyclerViewItemPopupMenu(view);
                    popupMenu.setOnMenuItemClickListener(getOnMenuItemClickListener());
                    popupMenu.show();
                }

                return true;
            }
        };
    }

    private void handleRelocation(IElement element)
    {
        if(element instanceof Location)
        {
            if(!element.equals(this.viewModel.longClickedElement))
            {
                if(!element.getChildren().contains(this.viewModel.longClickedElement))
                {
                    if(!element.isDescendantOf(this.viewModel.longClickedElement))
                    {
                        this.setSelectionModeEnabled(false);
                        this.viewModel.newParent = element;

                        FragmentManager fragmentManager = getSupportFragmentManager();

                        AlertDialogFragment alertDialogFragmentRelocate = AlertDialogFragment.newInstance(
                                R.drawable.ic_baseline_warning,
                                getString(R.string.alert_dialog_title_relocate),
                                getString(R.string.alert_dialog_message_relocate, viewModel.longClickedElement.getName(), element.getName()),
                                getString(R.string.text_accept),
                                getString(R.string.text_cancel),
                                Constants.REQUEST_CODE_RELOCATE,
                                false
                        );

                        alertDialogFragmentRelocate.setCancelable(false);
                        alertDialogFragmentRelocate.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                    }
                    else
                    {
                        Toaster.makeToast(this, getString(R.string.error_new_parent_is_own_descendant));
                    }
                }
                else
                {
                    this.setSelectionModeEnabled(false);
                }
            }
        }
        else
        {
            Toaster.makeToast(this, getString(R.string.error_new_parent_is_not_location));
        }
    }

    private PopupMenu getRecyclerViewItemPopupMenu(View view)
    {
        PopupMenu popupMenu = new PopupMenu(ShowLocationsActivity.this, view);
        this.populatePopupMenu(popupMenu.getMenu());
        return popupMenu;
    }

    private void populatePopupMenu(Menu menu)
    {
        if(this.viewModel.longClickedElement instanceof Location)
        {
            Menu submenuAdd = menu.addSubMenu(Menu.NONE, Constants.SELECTION_ADD, Menu.NONE, R.string.selection_add);
            submenuAdd.add(Menu.NONE, Constants.SELECTION_CREATE_LOCATION, Menu.NONE, R.string.selection_add_location);
            submenuAdd.add(Menu.NONE, Constants.SELECTION_CREATE_PARK, Menu.NONE, R.string.selection_add_park);

            boolean sortLocationsEnabled = this.viewModel.longClickedElement.getChildrenOfType(Location.class).size() > 1;
            boolean sortParksEnabled = this.viewModel.longClickedElement.getChildrenOfType(Park.class).size() > 1;
            if(sortLocationsEnabled || sortParksEnabled)
            {
                Menu submenuSort = menu.addSubMenu(Menu.NONE, Constants.SELECTION_SORT, Menu.NONE, R.string.selection_sort);

                submenuSort.add(Menu.NONE, Constants.SELECTION_SORT_LOCATIONS, Menu.NONE, R.string.selection_sort_locations).setEnabled(sortLocationsEnabled);
                submenuSort.add(Menu.NONE, Constants.SELECTION_SORT_PARKS, Menu.NONE, R.string.selection_sort_parks).setEnabled(sortParksEnabled);
            }
            else
            {
                menu.add(Menu.NONE, Constants.SELECTION_SORT, Menu.NONE, R.string.selection_sort).setEnabled(false);
            }
        }

        if(this.viewModel.longClickedElement instanceof Location)
        {
            menu.add(Menu.NONE, Constants.SELECTION_EDIT_LOCATION, Menu.NONE, R.string.selection_edit);
        }
        else if(this.viewModel.longClickedElement instanceof Park)
        {
            menu.add(Menu.NONE, Constants.SELECTION_EDIT_PARK, Menu.NONE, R.string.selection_edit);
        }

        menu.add(Menu.NONE, Constants.SELECTION_DELETE_ELEMENT, Menu.NONE, R.string.selection_delete).setEnabled(!isRootLocation(this.viewModel.longClickedElement));

        if(this.viewModel.longClickedElement instanceof Location)
        {
            menu.add(Menu.NONE, Constants.SELECTION_REMOVE_ELEMENT, Menu.NONE, R.string.selection_remove)
                    .setEnabled(this.viewModel.longClickedElement.hasChildren() && !isRootLocation(this.viewModel.longClickedElement));
        }

        menu.add(Menu.NONE, Constants.SELECTION_RELOCATE_ELEMENT, Menu.NONE, R.string.selection_relocate)
                .setEnabled(!isRootLocation(this.viewModel.longClickedElement));
    }

    private PopupMenu.OnMenuItemClickListener getOnMenuItemClickListener()
    {
        return new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onPopupMenuItemLongClick:: [%S] selected", item.getItemId()));

                FragmentManager fragmentManager = getSupportFragmentManager();

                int id = item.getItemId();
                if(id == Constants.SELECTION_CREATE_LOCATION)
                {
                    ActivityDistributor.startActivityCreateForResult(ShowLocationsActivity.this, Constants.REQUEST_CODE_CREATE_LOCATION, viewModel.longClickedElement);
                    return true;
                }
                else if(id == Constants.SELECTION_CREATE_PARK)
                {
                    ActivityDistributor.startActivityCreateForResult(ShowLocationsActivity.this, Constants.REQUEST_CODE_CREATE_PARK, viewModel.longClickedElement);
                    return true;
                }
                else if(id == Constants.SELECTION_EDIT_LOCATION)
                {
                    ActivityDistributor.startActivityEditForResult(ShowLocationsActivity.this, Constants.REQUEST_CODE_EDIT_LOCATION, viewModel.longClickedElement);
                    return true;
                }
                else if(id == Constants.SELECTION_EDIT_PARK)
                {
                    ActivityDistributor.startActivityEditForResult(ShowLocationsActivity.this, Constants.REQUEST_CODE_EDIT_PARK, viewModel.longClickedElement);
                    return true;
                }
                else if(id == Constants.SELECTION_DELETE_ELEMENT)
                {
                    AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                            R.drawable.ic_baseline_warning,
                            getString(R.string.alert_dialog_title_delete_element),
                            getString(R.string.alert_dialog_message_delete_element, viewModel.longClickedElement.getName()),
                            getString(R.string.text_accept),
                            getString(R.string.text_cancel),
                            Constants.REQUEST_CODE_DELETE,
                            false);


                    alertDialogFragmentDelete.setCancelable(false);
                    alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);

                    return true;
                }
                else if(id == Constants.SELECTION_REMOVE_ELEMENT)
                {
                    AlertDialogFragment alertDialogFragmentRemove = AlertDialogFragment.newInstance(
                            R.drawable.ic_baseline_warning,
                            getString(R.string.alert_dialog_title_remove_element),
                            getString(R.string.alert_dialog_message_remove_element, viewModel.longClickedElement.getName(), viewModel.longClickedElement.getParent().getName()),
                            getString(R.string.text_accept),
                            getString(R.string.text_cancel),
                            Constants.REQUEST_CODE_REMOVE,
                            false);

                    alertDialogFragmentRemove.setCancelable(false);
                    alertDialogFragmentRemove.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                    return true;
                }
                else if(id == Constants.SELECTION_RELOCATE_ELEMENT)
                {
                    ShowLocationsActivity.this.setSelectionModeEnabled(true);
                    return true;
                }
                else if(id == Constants.SELECTION_SORT_LOCATIONS)
                {
                    ActivityDistributor.startActivitySortForResult(ShowLocationsActivity.this, Constants.REQUEST_CODE_SORT_LOCATIONS, viewModel.longClickedElement.getChildrenOfType(Location.class));
                    return true;
                }
                else if(id == Constants.SELECTION_SORT_PARKS)
                {
                    ActivityDistributor.startActivitySortForResult(ShowLocationsActivity.this, Constants.REQUEST_CODE_SORT_PARKS, viewModel.longClickedElement.getChildrenOfType(Park.class));
                    return true;
                }
                else
                {
                    return false;
                }
            }
        };
    }

    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        Snackbar snackbar;

        if(which == DialogInterface.BUTTON_POSITIVE)
        {

            if(requestCode == Constants.REQUEST_CODE_DELETE)
            {
                snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()), Snackbar.LENGTH_LONG);

                snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        actionConfirmed = true;
                        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onSnackbarClick<DELETE>:: action <DELETE> confirmed");
                    }
                });

                snackbar.addCallback(new Snackbar.Callback()
                {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event)
                    {
                        if(actionConfirmed)
                        {
                            actionConfirmed = false;

                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onDismissed<DELETE>:: deleting %s...", viewModel.longClickedElement));

                            ShowLocationsActivity.super.markForDeletion(viewModel.longClickedElement, true);
                            ShowLocationsActivity.super.markForUpdate(viewModel.longClickedElement.getParent());

                            viewModel.longClickedElement.deleteElementAndDescendants();
                            updateContentRecyclerView(true);
                        }
                        else
                        {
                            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onDismissed<DELETE>:: action <DELETE> not confirmed - doing nothing");
                        }
                    }
                });

                snackbar.show();
            }
            else if(requestCode == Constants.REQUEST_CODE_REMOVE)
            {
                snackbar = Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.action_confirm_remove_text, viewModel.longClickedElement.getName()), Snackbar.LENGTH_LONG);

                snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        actionConfirmed = true;
                        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onSnackbarClick<REMOVE>:: action <REMOVE> confirmed");
                    }
                });

                snackbar.addCallback(new Snackbar.Callback()
                {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event)
                    {
                        if(actionConfirmed)
                        {
                            actionConfirmed = false;

                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onSnackbarDismissed<REMOVE>:: removing %s...", viewModel.longClickedElement));

                            IElement parent = viewModel.longClickedElement.getParent();

                            ShowLocationsActivity.super.markForDeletion(viewModel.longClickedElement, false);
                            ShowLocationsActivity.super.markForUpdate(parent);

                            viewModel.longClickedElement.removeElement();

                            updateContentRecyclerView(true);
                        }
                        else
                        {
                            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onDismissed<REMOVE>:: action <REMOVE> not confirmed - doing nothing");
                        }
                    }
                });

                snackbar.show();
            }
            else if(requestCode == Constants.REQUEST_CODE_RELOCATE)
            {
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onAlertDialogClick<RELOCATE>:: relocating %s to %s...",
                        this.viewModel.longClickedElement, this.viewModel.newParent));

                this.viewModel.longClickedElement.relocateElement(this.viewModel.newParent);
                this.viewModel.newParent = null;
                this.updateContentRecyclerView(true);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isRootLocation(IElement element)
    {
        return element.equals(App.content.getRootLocation());
    }

    private void updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateContentRecyclerView:: resetting content...");
            this.viewModel.contentRecyclerViewAdapter.setItems(new ArrayList<>(Collections.singleton(viewModel.currentLocation)));
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }

    }
}