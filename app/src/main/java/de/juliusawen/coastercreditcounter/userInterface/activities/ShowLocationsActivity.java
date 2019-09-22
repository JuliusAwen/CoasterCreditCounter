package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ShowLocationsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowLocationsActivityViewModel viewModel;
    private RecyclerView recyclerView;


    protected void setContentView()
    {
        setContentView(R.layout.activity_show_locations);
    }

    public void create()
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
                    .setTypefaceForContentType(Location.class, Typeface.BOLD);
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

    @Override
    protected void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .add(OptionsItem.EXPAND_ALL)
                .add(OptionsItem.COLLAPSE_ALL)
                .create(menu);
    }

    @Override
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        switch(item)
        {
            case EXPAND_ALL:
                this.viewModel.contentRecyclerViewAdapter.expandAll();
                return true;

            case COLLAPSE_ALL:
                this.viewModel.contentRecyclerViewAdapter.collapseAll();
                return true;

            default:
                return super.handleOptionsItemSelected(item);
        }
    }


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
                enabled ? getString(R.string.subtitle_relocate_select_new_parent, this.viewModel.longClickedElement.getName()) : null);
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
                    boolean isRootLocation = isLocation && viewModel.longClickedElement.equals(App.content.getRootLocation());
                    boolean sortLocationsEnabled = isLocation && viewModel.longClickedElement.getChildrenOfType(Location.class).size() > 1;
                    boolean sortParksEnabled = isLocation && viewModel.longClickedElement.getChildrenOfType(Park.class).size() > 1;

                    PopupMenuAgent.getMenu()
                            .add(PopupItem.ADD)
                                .addToGroup(PopupItem.ADD_LOCATION, PopupItem.ADD)
                                .addToGroup(PopupItem.ADD_PARK, PopupItem.ADD)
                            .add(PopupItem.SORT)
                                .addToGroup(PopupItem.SORT_LOCATIONS, PopupItem.SORT)
                                .addToGroup(PopupItem.SORT_PARKS, PopupItem.SORT)
                            .add(PopupItem.EDIT_LOCATION)
                            .add(PopupItem.EDIT_PARK)
                            .add(PopupItem.DELETE_ELEMENT)
                            .add(PopupItem.REMOVE_LOCATION)
                            .add(PopupItem.RELOCATE_ELEMENT)
                            .setVisible(PopupItem.ADD, isLocation)
                            .setEnabled(PopupItem.SORT, isLocation && sortLocationsEnabled || sortParksEnabled)
                            .setVisible(PopupItem.SORT, isLocation)
                            .setEnabled(PopupItem.SORT_LOCATIONS, isLocation && sortLocationsEnabled)
                            .setEnabled(PopupItem.SORT_PARKS, isLocation && sortParksEnabled)
                            .setVisible(PopupItem.EDIT_LOCATION, isLocation)
                            .setVisible(PopupItem.EDIT_PARK, !isLocation)
                            .setEnabled(PopupItem.DELETE_ELEMENT, !isRootLocation)
                            .setVisible(PopupItem.REMOVE_LOCATION, isLocation)
                            .setEnabled(PopupItem.REMOVE_LOCATION, viewModel.longClickedElement.hasChildren() && !isRootLocation)
                            .setEnabled(PopupItem.RELOCATE_ELEMENT, !isRootLocation)
                            .show(ShowLocationsActivity.this, view);
                }

                return true;
            }
        };
    }

    @Override
    public void handlePopupItemClicked(PopupItem item)
    {
        switch(item)
        {
            case SORT_LOCATIONS:
                ActivityDistributor.startActivitySortForResult(
                        ShowLocationsActivity.this,
                        RequestCode.SORT_LOCATIONS,
                        viewModel.longClickedElement.getChildrenOfType(Location.class));
                break;

            case SORT_PARKS:
                ActivityDistributor.startActivitySortForResult(
                        ShowLocationsActivity.this,
                        RequestCode.SORT_PARKS,
                        viewModel.longClickedElement.getChildrenOfType(Park.class));
                break;

            case ADD_LOCATION:
                ActivityDistributor.startActivityCreateForResult(ShowLocationsActivity.this, RequestCode.CREATE_LOCATION, viewModel.longClickedElement);
                break;

            case ADD_PARK:
                ActivityDistributor.startActivityCreateForResult(ShowLocationsActivity.this, RequestCode.CREATE_PARK, viewModel.longClickedElement);
                break;

            case EDIT_LOCATION:
                ActivityDistributor.startActivityEditForResult(ShowLocationsActivity.this, RequestCode.EDIT_LOCATION, viewModel.longClickedElement);
                break;

            case EDIT_PARK:
                ActivityDistributor.startActivityEditForResult(ShowLocationsActivity.this, RequestCode.EDIT_PARK, viewModel.longClickedElement);
                break;

            case REMOVE_LOCATION:
                AlertDialogFragment alertDialogFragmentRemove = AlertDialogFragment.newInstance(
                        R.drawable.ic_baseline_warning,
                        getString(R.string.alert_dialog_title_remove_element),
                        getString(R.string.alert_dialog_message_confirm_remove, viewModel.longClickedElement.getName(), viewModel.longClickedElement.getParent().getName()),
                        getString(R.string.text_accept),
                        getString(R.string.text_cancel),
                        RequestCode.REMOVE,
                        false);

                alertDialogFragmentRemove.setCancelable(false);
                alertDialogFragmentRemove.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
                break;

            case RELOCATE_ELEMENT:
                ShowLocationsActivity.this.setSelectionModeEnabled(true);
                break;

            case DELETE_ELEMENT:
                AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                        R.drawable.ic_baseline_warning,
                        getString(R.string.alert_dialog_title_delete_element),
                        getString(R.string.alert_dialog_message_confirm_delete, viewModel.longClickedElement.getName()),
                        getString(R.string.text_accept),
                        getString(R.string.text_cancel),
                        RequestCode.DELETE,
                        false);

                alertDialogFragmentDelete.setCancelable(false);
                alertDialogFragmentDelete.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
                break;
        }
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
                                getString(R.string.alert_dialog_message_confirm_relocate, viewModel.longClickedElement.getName(), element.getName()),
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
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            switch(requestCode)
            {
                case DELETE:
                    ConfirmSnackbar.Show(
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()), Snackbar.LENGTH_LONG),
                            requestCode,
                            ShowLocationsActivity.this);
                    break;

                case REMOVE:
                    ConfirmSnackbar.Show(
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.action_confirm_remove_text, viewModel.longClickedElement.getName()), Snackbar.LENGTH_LONG),
                            requestCode,
                            ShowLocationsActivity.this);
                    break;

                case RELOCATE:
                    Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.handleAlertDialogClick<RELOCATE>:: relocating %s to %s...",
                            this.viewModel.longClickedElement, this.viewModel.newParent));

                    this.viewModel.longClickedElement.relocateElement(this.viewModel.newParent);
                    this.viewModel.newParent = null;
                    this.updateContentRecyclerView(true);
                    break;
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        switch(requestCode)
        {
            case DELETE:
            {
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.handleActionConfirmed:: deleting %s...", viewModel.longClickedElement));

                ShowLocationsActivity.super.markForDeletion(this.viewModel.longClickedElement, true);
                ShowLocationsActivity.super.markForUpdate(this.viewModel.longClickedElement.getParent());

                this.viewModel.longClickedElement.deleteElementAndDescendants();
                updateContentRecyclerView(true);
                break;
            }

            case REMOVE:
            {
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.handleActionConfirmed:: removing %s...", viewModel.longClickedElement));

                IElement parent = this.viewModel.longClickedElement.getParent();

                ShowLocationsActivity.super.markForDeletion(this.viewModel.longClickedElement, false);
                ShowLocationsActivity.super.markForUpdate(parent);

                this.viewModel.longClickedElement.removeElement();

                updateContentRecyclerView(true);
                break;
            }
        }
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