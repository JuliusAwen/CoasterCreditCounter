package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ShowLocationsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowLocationsViewModel viewModel;

    protected void setContentView()
    {
        setContentView(R.layout.activity_show_locations);
    }

    public void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ShowLocationsViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.getValue(getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0));
        }

        if(this.viewModel.currentLocation == null)
        {
            UUID elementUuid = UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID));
            this.viewModel.currentLocation = elementUuid != null
                    ? App.content.getContentByUuid(elementUuid)
                    : App.content.getRootLocation();
        }

        if(this.viewModel.adapterFacade == null)
        {
            this.viewModel.adapterFacade = new ContentRecyclerViewAdapterFacade();
            this.viewModel.adapterFacade.setContent(this.viewModel.currentLocation);

            this.viewModel.adapterFacade.getConfiguration().addOnClickListenerByType(Park.class, this.createOnParkClickListener());
            this.viewModel.adapterFacade.getConfiguration().addOnClickListenerByType(Location.class, this.createOnLocationClickListener());
//            this.viewModel.adapterUtilityWrapper.getConfiguration().addOnLongClickListenerByType(IElement.class, this.createOnLongClickListener());
            this.viewModel.adapterFacade.getConfiguration().addOnLongClickListenerByType(IElement.class, this.createOnLongClickListenerTest());

            this.viewModel.adapterFacade.createPreconfiguredAdapter(this.viewModel.requestCode);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowLocations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.viewModel.adapterFacade.getAdapter());

        if(this.viewModel.currentLocation.isRootLocation())
        {
            this.viewModel.adapterFacade.getAdapter().expandItem(this.viewModel.currentLocation, false);
        }


        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.locations)), getString(R.string.help_text_show_locations));
        super.createToolbar();
        super.addToolbarHomeButton();

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();

        super.setOptionsMenuButlerViewModel(this.viewModel);

        this.enableRelocationMode(this.viewModel.relocationModeEnabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(String.format("RequestCode[%s], ResultCode[%s]", RequestCode.getValue(requestCode), StringTool.resultCodeToString(resultCode)));

        if(resultCode == RESULT_OK)
        {
            switch(RequestCode.getValue(requestCode))
            {
                case CREATE_LOCATION:
                case CREATE_PARK:
                {
                    this.updateContentRecyclerView();
                    this.viewModel.adapterFacade.getAdapter().expandItem(ResultFetcher.fetchResultElement(data).getParent(), true);
                    invalidateOptionsMenu();
                    break;
                }

                case SORT_LOCATIONS:
                case SORT_PARKS:
                {
                    List<IElement> resultElements = ResultFetcher.fetchResultElements(data);

                    IElement parent = resultElements.get(0).getParent();
                    Log.d(String.format("<SortElements> reordering %s's children...", parent));
                    parent.reorderChildren(resultElements);

                    this.updateContentRecyclerView();

                    String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                    if(selectedElementUuidString != null)
                    {
                        IElement selectedElement = App.content.getContentByUuid(UUID.fromString(selectedElementUuidString));
                        this.viewModel.adapterFacade.getAdapter().scrollToItem(selectedElement);
                    }
                    else
                    {
                        Log.v("<SortElements> no selected element returned");
                    }

                    super.markForUpdate(parent);
                    break;
                }

                case EDIT_LOCATION:
                case EDIT_PARK:
                {
                    this.viewModel.adapterFacade.getAdapter().notifyItemChanged(ResultFetcher.fetchResultElement(data));
                    break;
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.i(String.format("<%s> pressed", StringTool.keyCodeToString(keyCode)));

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(this.viewModel.relocationModeEnabled)
            {
                this.enableRelocationMode(false);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.close, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("FloatingActionButton clicked");
                enableRelocationMode(false);
            }
        });
    }

    private void enableRelocationMode(boolean enabled)
    {
        Log.v("enabling relocation mode...");
        this.viewModel.relocationModeEnabled = enabled;

        if(enabled)
        {
            this.viewModel.adapterFacade.getAdapter().selectItem(this.viewModel.longClickedElement);
            super.setToolbarTitleAndSubtitle(getString(R.string.title_relocate), getString(R.string.subtitle_relocate_select_new_parent));
        }
        else
        {
            this.viewModel.adapterFacade.getAdapter().deselectItem(this.viewModel.longClickedElement);
            super.setToolbarTitleAndSubtitle(getString(R.string.locations), "");
        }

        super.setFloatingActionButtonVisibility(enabled);

        Log.d(String.format("selection mode enabled[%S]", enabled));
    }

    private View.OnClickListener createOnLocationClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnLocationClick((IElement) view.getTag());
            }
        };
    }

    private void handleOnLocationClick(IElement element)
    {
        if(!this.viewModel.relocationModeEnabled)
        {
            this.viewModel.adapterFacade.getAdapter().toggleExpansion(element);
            if(this.viewModel.adapterFacade.getAdapter().isAllContentExpanded() || this.viewModel.adapterFacade.getAdapter().isAllContentCollapsed())
            {
                invalidateOptionsMenu();
            }
        }
        else
        {
            handleRelocation(element);
        }
    }

    private View.OnClickListener createOnParkClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnParkClick((IElement) view.getTag());
            }
        };
    }

    private void handleOnParkClick(IElement element)
    {
        if(!this.viewModel.relocationModeEnabled)
        {
            ActivityDistributor.startActivityShow(ShowLocationsActivity.this, RequestCode.SHOW_PARK, element);
        }
        else
        {
            handleRelocation(element);
        }
    }

    private View.OnLongClickListener createOnLongClickListener()
    {
        return new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                return handleOnLongClick(view);
            }
        };
    }

    @Deprecated
    private View.OnLongClickListener createOnLongClickListenerTest()
    {
        return new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                viewModel.adapterFacade.applyDecorationPreset(RequestCode.NAVIGATE);
                viewModel.adapterFacade.getAdapter().notifyDataSetChanged();
                return true;
            }
        };
    }

    private boolean handleOnLongClick(View view)
    {
        this.viewModel.longClickedElement = (Element) view.getTag();
        Log.i(String.format("%s long clicked", this.viewModel.longClickedElement));

        if(!this.viewModel.relocationModeEnabled)
        {
            boolean isLocation = this.viewModel.longClickedElement.isLocation();
            boolean sortLocationsEnabled = isLocation && this.viewModel.longClickedElement.getChildrenOfType(Location.class).size() > 1;
            boolean sortParksEnabled = isLocation && this.viewModel.longClickedElement.getChildrenOfType(Park.class).size() > 1;

            int locationsCount = App.content.getContentOfType(Location.class).size();

            boolean relocateEnabled = !this.viewModel.longClickedElement.isRootLocation()
                    && (this.viewModel.longClickedElement.isLocation()
                        ? (locationsCount - 1) > 1
                        : locationsCount > 1);

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
                    .add(PopupItem.REMOVE_ELEMENT)
                    .add(PopupItem.RELOCATE_ELEMENT)
                    .setVisible(PopupItem.ADD, isLocation)
                    .setEnabled(PopupItem.SORT, isLocation && sortLocationsEnabled || sortParksEnabled)
                    .setVisible(PopupItem.SORT, isLocation)
                    .setEnabled(PopupItem.SORT_LOCATIONS, isLocation && sortLocationsEnabled)
                    .setEnabled(PopupItem.SORT_PARKS, isLocation && sortParksEnabled)
                    .setVisible(PopupItem.EDIT_LOCATION, isLocation)
                    .setVisible(PopupItem.EDIT_PARK, !isLocation)
                    .setEnabled(PopupItem.DELETE_ELEMENT, !this.viewModel.longClickedElement.isRootLocation())
                    .setVisible(PopupItem.REMOVE_ELEMENT, isLocation)
                    .setEnabled(PopupItem.REMOVE_ELEMENT, this.viewModel.longClickedElement.hasChildren() && !this.viewModel.longClickedElement.isRootLocation())
                    .setEnabled(PopupItem.RELOCATE_ELEMENT, relocateEnabled)
                    .show(ShowLocationsActivity.this, view);
        }

        return true;
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
                        this.viewModel.longClickedElement.getChildrenOfType(Location.class));
                break;

            case SORT_PARKS:
                ActivityDistributor.startActivitySortForResult(
                        ShowLocationsActivity.this,
                        RequestCode.SORT_PARKS,
                        this.viewModel.longClickedElement.getChildrenOfType(Park.class));
                break;

            case ADD_LOCATION:
                ActivityDistributor.startActivityCreateForResult(ShowLocationsActivity.this, RequestCode.CREATE_LOCATION, this.viewModel.longClickedElement);
                break;

            case ADD_PARK:
                ActivityDistributor.startActivityCreateForResult(ShowLocationsActivity.this, RequestCode.CREATE_PARK, this.viewModel.longClickedElement);
                break;

            case EDIT_LOCATION:
                ActivityDistributor.startActivityEditForResult(ShowLocationsActivity.this, RequestCode.EDIT_LOCATION, this.viewModel.longClickedElement);
                break;

            case EDIT_PARK:
                ActivityDistributor.startActivityEditForResult(ShowLocationsActivity.this, RequestCode.EDIT_PARK, this.viewModel.longClickedElement);
                break;

            case REMOVE_ELEMENT:
                AlertDialogFragment alertDialogFragmentRemove = AlertDialogFragment.newInstance(
                        R.drawable.warning,
                        getString(R.string.alert_dialog_title_remove),
                        getString(R.string.alert_dialog_message_confirm_remove_location,
                                this.viewModel.longClickedElement.getName(),
                                this.viewModel.longClickedElement.getParent().getName()),
                        getString(R.string.text_accept),
                        getString(R.string.text_cancel),
                        RequestCode.REMOVE,
                        false);

                alertDialogFragmentRemove.setCancelable(false);
                alertDialogFragmentRemove.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
                break;

            case RELOCATE_ELEMENT:
                ShowLocationsActivity.this.enableRelocationMode(true);
                break;

            case DELETE_ELEMENT:
                AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                        R.drawable.warning,
                        getString(R.string.alert_dialog_title_delete),
                        getString(R.string.alert_dialog_message_confirm_delete_with_children, viewModel.longClickedElement.getName()),
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
        if(element.isLocation())
        {
            if(!element.equals(this.viewModel.longClickedElement))
            {
                if(!element.getChildren().contains(this.viewModel.longClickedElement))
                {
                    if(!element.isDescendantOf(this.viewModel.longClickedElement))
                    {
                        this.enableRelocationMode(false);
                        this.viewModel.newParent = element;

                        FragmentManager fragmentManager = getSupportFragmentManager();

                        AlertDialogFragment alertDialogFragmentRelocate = AlertDialogFragment.newInstance(
                                R.drawable.warning,
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
                        Toaster.makeShortToast(this, getString(R.string.error_new_parent_is_own_descendant));
                        this.enableRelocationMode(false);
                    }
                }
                else
                {
                    Log.w(String.format("%s is already located at %s - aborting relocation", this.viewModel.longClickedElement, this.viewModel.newParent));
                    this.enableRelocationMode(false);
                }
            }
            else
            {
                Log.d(String.format("cannot relocate %s to itself - aborting relocation", this.viewModel.longClickedElement));
                this.enableRelocationMode(false);
            }
        }
        else
        {
            Toaster.makeShortToast(this, getString(R.string.error_new_parent_is_not_location));
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
                    super.setFloatingActionButtonVisibility(false);
                    ConfirmSnackbar.Show(
                            Snackbar.make(findViewById(android.R.id.content),
                                    getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                                    Snackbar.LENGTH_LONG),
                            requestCode,
                            ShowLocationsActivity.this);
                    break;

                case REMOVE:
                    super.setFloatingActionButtonVisibility(false);
                    ConfirmSnackbar.Show(
                            Snackbar.make(findViewById(android.R.id.content),
                                    getString(R.string.action_confirm_remove_text, viewModel.longClickedElement.getName()),
                                    Snackbar.LENGTH_LONG),
                            requestCode,
                            ShowLocationsActivity.this);
                    break;

                case RELOCATE:
                    Log.i(String.format("<RELOCATE> relocating %s to %s...", this.viewModel.longClickedElement, this.viewModel.newParent));

                    this.viewModel.longClickedElement.relocate(this.viewModel.newParent);
                    this.viewModel.newParent = null;
                    this.updateContentRecyclerView();
                    break;
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(String.format("handling confirmed action [%s]", requestCode));

        switch(requestCode)
        {
            case DELETE:
            {
                Log.i(String.format("deleting %s...", viewModel.longClickedElement));

                ShowLocationsActivity.super.markForUpdate(this.viewModel.longClickedElement.getParent());
                ShowLocationsActivity.super.markForDeletion(this.viewModel.longClickedElement, true);

                updateContentRecyclerView();
                invalidateOptionsMenu();
                break;
            }

            case REMOVE:
            {
                Log.i(String.format("removing %s...", viewModel.longClickedElement));

                IElement parent = this.viewModel.longClickedElement.getParent();

                ShowLocationsActivity.super.markForUpdate(parent);
                ShowLocationsActivity.super.markForDeletion(this.viewModel.longClickedElement);

                this.viewModel.longClickedElement.remove();

                updateContentRecyclerView();
                invalidateOptionsMenu();
                break;
            }
        }
    }

    private void updateContentRecyclerView()
    {
        Log.d("resetting content...");
        this.viewModel.adapterFacade.getAdapter().setContent(this.viewModel.currentLocation);
    }
}