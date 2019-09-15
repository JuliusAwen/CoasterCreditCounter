package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.fragments.AlertDialogFragment;

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
                this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
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
                    .add(OptionsMenuAgent.EXPAND_ALL)
                    .add(OptionsMenuAgent.COLLAPSE_ALL)
                    .create(menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(App.isInitialized)
        {
            this.viewModel.optionsMenuAgent
                    .setEnabled(OptionsMenuAgent.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                    .setEnabled(OptionsMenuAgent.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                    .prepare(menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(this.viewModel.optionsMenuAgent.handleOptionsItemSelected(item, this))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleExpandAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.expandAll();
    }

    @Override
    public void handleCollapseAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.collapseAll();
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
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == RESULT_OK)
        {
            switch(RequestCode.values()[requestCode])
            {
                case CREATE_LOCATION:
                case CREATE_PARK:
                {
                    IElement resultElement = ResultFetcher.fetchResultElement(data);
                    super.markForUpdate(resultElement.getParent());
                    this.updateContentRecyclerView(true);
                    break;
                }

                case SORT_LOCATIONS:
                case SORT_PARKS:
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
                    break;
                }

                case EDIT_LOCATION:
                case EDIT_PARK:
                {
                    IElement editedElement = ResultFetcher.fetchResultElement(data);

                    super.markForUpdate(editedElement);

                    this.updateContentRecyclerView(false);
                    break;
                }
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
                        ActivityDistributor.startActivityShow(ShowLocationsActivity.this, RequestCode.SHOW_PARK, element);
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
                    boolean isLocation = viewModel.longClickedElement instanceof Location;
                    boolean sortLocationsEnabled = isLocation && viewModel.longClickedElement.getChildrenOfType(Location.class).size() > 1;
                    boolean sortParksEnabled = isLocation && viewModel.longClickedElement.getChildrenOfType(Park.class).size() > 1;

                    PopupMenuAgent.getAgent()
                            .add(PopupMenuAgent.ADD)
                            .add(PopupMenuAgent.SORT)
                            .add(PopupMenuAgent.EDIT_LOCATION)
                            .add(PopupMenuAgent.EDIT_PARK)
                            .add(PopupMenuAgent.DELETE_ELEMENT)
                            .add(PopupMenuAgent.REMOVE_ELEMENT)
                            .add(PopupMenuAgent.RELOCATE_ELEMENT)
                            .setVisible(PopupMenuAgent.ADD, isLocation)
                            .setEnabled(PopupMenuAgent.SORT, isLocation && sortLocationsEnabled || sortParksEnabled)
                            .setVisible(PopupMenuAgent.SORT, isLocation)
                            .setEnabled(PopupMenuAgent.SORT_LOCATIONS, isLocation && sortLocationsEnabled)
                            .setEnabled(PopupMenuAgent.SORT_PARKS, isLocation && sortParksEnabled)
                            .setVisible(PopupMenuAgent.EDIT_LOCATION, isLocation)
                            .setVisible(PopupMenuAgent.EDIT_PARK, !isLocation)
                            .setEnabled(PopupMenuAgent.DELETE_ELEMENT, !isRootLocation(viewModel.longClickedElement))
                            .setVisible(PopupMenuAgent.REMOVE_ELEMENT, isLocation)
                            .setEnabled(PopupMenuAgent.REMOVE_ELEMENT, viewModel.longClickedElement.hasChildren() && !isRootLocation(viewModel.longClickedElement))
                            .setEnabled(PopupMenuAgent.RELOCATE_ELEMENT, !isRootLocation(viewModel.longClickedElement))
                            .show(ShowLocationsActivity.this, view);
                }

                return true;
            }
        };
    }

    @Override
    public void handleSortLocationsClicked()
    {
        ActivityDistributor.startActivitySortForResult(
                ShowLocationsActivity.this,
                RequestCode.SORT_LOCATIONS,
                viewModel.longClickedElement.getChildrenOfType(Location.class));
    }

    @Override
    public void handleSortParksClicked()
    {
        ActivityDistributor.startActivitySortForResult(
                ShowLocationsActivity.this,
                RequestCode.SORT_PARKS,
                viewModel.longClickedElement.getChildrenOfType(Park.class));
    }

    @Override
    public void handleAddLocationClicked()
    {
        ActivityDistributor.startActivityCreateForResult(ShowLocationsActivity.this, RequestCode.CREATE_LOCATION, viewModel.longClickedElement);
    }

    @Override
    public void handleAddParkClicked()
    {
        ActivityDistributor.startActivityCreateForResult(ShowLocationsActivity.this, RequestCode.CREATE_PARK, viewModel.longClickedElement);
    }

    @Override
    public void handleEditLocationClicked()
    {
        ActivityDistributor.startActivityEditForResult(ShowLocationsActivity.this, RequestCode.EDIT_LOCATION, viewModel.longClickedElement);
    }

    @Override
    public void handleEditParkClicked()
    {
        ActivityDistributor.startActivityEditForResult(ShowLocationsActivity.this, RequestCode.EDIT_PARK, viewModel.longClickedElement);
    }

    @Override
    public void handleRemoveElementClicked()
    {
        AlertDialogFragment alertDialogFragmentRemove = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_warning,
                getString(R.string.alert_dialog_title_remove_element),
                getString(R.string.alert_dialog_message_remove_element, viewModel.longClickedElement.getName(), viewModel.longClickedElement.getParent().getName()),
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                RequestCode.REMOVE,
                false);

        alertDialogFragmentRemove.setCancelable(false);
        alertDialogFragmentRemove.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    @Override
    public void handleRelocateElementClicked()
    {
        ShowLocationsActivity.this.setSelectionModeEnabled(true);
    }

    @Override
    public void handleDeleteElementClicked()
    {
        AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                R.drawable.ic_baseline_warning,
                getString(R.string.alert_dialog_title_delete_element),
                getString(R.string.alert_dialog_message_delete_element, viewModel.longClickedElement.getName()),
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                RequestCode.DELETE,
                false);

        alertDialogFragmentDelete.setCancelable(false);
        alertDialogFragmentDelete.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
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
                                RequestCode.RELOCATE,
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

    @Override
    public void onAlertDialogClick(RequestCode requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        Snackbar snackbar;

        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            switch(requestCode)
            {
                case DELETE:
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
                    break;
                }

                case REMOVE:
                {
                    snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.action_confirm_remove_text, viewModel.longClickedElement.getName()), Snackbar.LENGTH_LONG);

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
                    break;
                }

                case RELOCATE:
                {
                    Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onAlertDialogClick<RELOCATE>:: relocating %s to %s...",
                            this.viewModel.longClickedElement, this.viewModel.newParent));

                    this.viewModel.longClickedElement.relocateElement(this.viewModel.newParent);
                    this.viewModel.newParent = null;
                    this.updateContentRecyclerView(true);
                    break;
                }
            }
        }
    }

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
            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.updateContentRecyclerView:: notifying data set changed...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }

    }
}