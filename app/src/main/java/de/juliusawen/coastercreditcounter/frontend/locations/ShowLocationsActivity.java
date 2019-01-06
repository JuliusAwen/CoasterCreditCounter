package de.juliusawen.coastercreditcounter.frontend.locations;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Location;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowLocationsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private ShowLocationsActivityViewModel viewModel;
    private boolean actionConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowLocationsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_locations);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            RecyclerView recyclerView = findViewById(R.id.recyclerViewShowLocations);

            this.viewModel = ViewModelProviders.of(this).get(ShowLocationsActivityViewModel.class);

            if(this.viewModel.currentElement == null)
            {
                String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                this.viewModel.currentElement = elementUuid != null ? App.content.getContentByUuid(UUID.fromString(elementUuid)) : App.content.getRootLocation();
            }

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(Location.class);
                childTypesToExpand.add(Park.class);

                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                        this.viewModel.currentElement.getChildrenOfType(Location.class),
                        null,
                        childTypesToExpand);

                this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
                this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Location.class, Typeface.BOLD);
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(getString(R.string.title_locations), null);

            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_locations)), getString(R.string.help_text_show_locations));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        menu.add(Menu.NONE, Selection.EXPAND_ALL.ordinal(), Menu.NONE, R.string.selection_expand_all).setEnabled(!this.viewModel.contentRecyclerViewAdapter.isAllExpanded());
        menu.add(Menu.NONE, Selection.COLLAPSE_ALL.ordinal(), Menu.NONE, R.string.selection_collapse_all).setEnabled(!this.viewModel.contentRecyclerViewAdapter.isAllCollapsed());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case EXPAND_ALL:
                this.viewModel.contentRecyclerViewAdapter.expandAll();
                break;

            case COLLAPSE_ALL:
                this.viewModel.contentRecyclerViewAdapter.collapseAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));
        if(resultCode == RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_CREATE_LOCATION)
            {
                String resultElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                IElement resultElement = App.content.getContentByUuid(UUID.fromString(resultElementUuidString));

                this.setItemsInRecyclerViewAdapter();
                this.viewModel.contentRecyclerViewAdapter.scrollToItem(resultElement);

                super.markForCreation(resultElement);
                super.markForUpdate(resultElement.getParent());
                super.markForUpdate(resultElement.getChildren());
            }
            else if(requestCode == Constants.REQUEST_SORT_LOCATIONS || requestCode == Constants.REQUEST_SORT_PARKS)
            {
                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<IElement> resultElements = App.content.getContentByUuidStrings(resultElementsUuidStrings);

                IElement parent = resultElements.get(0).getParent();
                Log.d(Constants.LOG_TAG, String.format("ShowLocationsActivity.onActivityResult<SortElements>:: replacing children with sorted children in parent %s...", parent));

                parent.reorderChildren(resultElements);
                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.currentElement.getChildrenOfType(Location.class));

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
            else if(requestCode == Constants.REQUEST_EDIT_LOCATION)
            {
                IElement editedElement = App.content.getContentByUuid(UUID.fromString(data.getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
                this.viewModel.contentRecyclerViewAdapter.updateItem(editedElement);

                super.markForUpdate(editedElement);
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
                Element element = (Element) view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickLocationRecyclerView:: %s clicked", element));

                if(Location.class.isInstance(element))
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
                else if(Park.class.isInstance(element))
                {
                    ActivityTool.startActivityShow(ShowLocationsActivity.this, Constants.REQUEST_SHOW_PARK, element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (Element) view.getTag();
                Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onLongClickLocationRecyclerView:: %s long clicked", viewModel.longClickedElement));

                if(Location.class.isInstance(viewModel.longClickedElement))
                {
                    PopupMenu popupMenu = new PopupMenu(ShowLocationsActivity.this, view);

                    Menu submenuAdd = popupMenu.getMenu().addSubMenu(Menu.NONE, Selection.ADD.ordinal(), Menu.NONE, R.string.selection_add);
                    submenuAdd.add(Menu.NONE, Selection.CREATE_LOCATION.ordinal(), Menu.NONE, R.string.selection_add_location);
                    submenuAdd.add(Menu.NONE, Selection.CREATE_PARK.ordinal(), Menu.NONE, R.string.selection_add_park);

                    Menu submenuSort = popupMenu.getMenu().addSubMenu(Menu.NONE, Selection.SORT.ordinal(), Menu.NONE, R.string.selection_sort);
                    submenuSort.add(Menu.NONE, Selection.SORT_LOCATIONS.ordinal(), Menu.NONE, R.string.selection_sort_locations)
                            .setEnabled(viewModel.longClickedElement.hasChildrenOfType(Location.class));
                    submenuSort.add(Menu.NONE, Selection.SORT_PARKS.ordinal(), Menu.NONE, R.string.selection_sort_parks)
                            .setEnabled(viewModel.longClickedElement.hasChildrenOfType(Park.class));

                    Menu submenuMaintain = popupMenu.getMenu().addSubMenu(Menu.NONE, Selection.MAINTAIN.ordinal(), Menu.NONE, R.string.selection_maintain);
                    submenuMaintain.add(Menu.NONE, Selection.EDIT_LOCATION.ordinal(), Menu.NONE, R.string.selection_edit);
                    submenuMaintain.add(Menu.NONE, Selection.DELETE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_delete);
                    submenuMaintain.add(Menu.NONE, Selection.REMOVE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_remove)
                            .setEnabled(viewModel.longClickedElement.hasChildren());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            Selection selection = Selection.values()[item.getItemId()];
                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onClickMenuItemPopupMenuLongClickLocationRecyclerView:: [%S] selected", selection));

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            switch (selection)
                            {
                                case CREATE_LOCATION:
                                    ActivityTool.startActivityCreateForResult(ShowLocationsActivity.this, Constants.REQUEST_CREATE_LOCATION, viewModel.longClickedElement);
                                    return true;

                                case CREATE_PARK:
                                    //Todo: implement create park
                                    Toaster.notYetImplemented(ShowLocationsActivity.this);
                                    return true;

                                case SORT_LOCATIONS:
                                    ActivityTool.startActivitySortForResult(
                                            ShowLocationsActivity.this,
                                            Constants.REQUEST_SORT_LOCATIONS,
                                            viewModel.longClickedElement.getChildrenOfType(Location.class));
                                    return true;

                                case SORT_PARKS:
                                    ActivityTool.startActivitySortForResult(
                                            ShowLocationsActivity.this,
                                            Constants.REQUEST_SORT_PARKS,
                                            viewModel.longClickedElement.getChildrenOfType(Park.class));
                                    return true;

                                case EDIT_LOCATION:
                                    ActivityTool.startActivityEditForResult(ShowLocationsActivity.this, Constants.REQUEST_EDIT_LOCATION, viewModel.longClickedElement);
                                    return true;

                                case DELETE_ELEMENT:
                                    AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                                                    R.drawable.ic_baseline_warning,
                                                    getString(R.string.alert_dialog_delete_element_title),
                                                    getString(R.string.alert_dialog_delete_element_message, viewModel.longClickedElement.getName()),
                                                    getString(R.string.text_accept),
                                                    getString(R.string.text_cancel),
                                                    Constants.ALERT_DIALOG_REQUEST_CODE_DELETE
                                            );

                                    alertDialogFragmentDelete.setCancelable(false);
                                    alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);

                                    return true;

                                case REMOVE_ELEMENT:
                                    String alertDialogMessage;
                                    if(viewModel.longClickedElement.getParent().equals(App.content.getRootLocation()) && viewModel.longClickedElement.hasChildrenOfType(Park.class))
                                    {
                                        alertDialogMessage = getString(R.string.alert_dialog_remove_element_message_parent_is_root, viewModel.longClickedElement.getName(),
                                                        viewModel.longClickedElement.getParent().getName());
                                    }
                                    else
                                    {
                                        alertDialogMessage = getString(R.string.alert_dialog_remove_element_message, viewModel.longClickedElement.getName(),
                                                        viewModel.longClickedElement.getParent().getName());
                                    }

                                    AlertDialogFragment alertDialogFragmentRemove = AlertDialogFragment.newInstance(
                                            R.drawable.ic_baseline_warning,
                                            getString(R.string.alert_dialog_remove_element_title),
                                            alertDialogMessage,
                                            getString(R.string.text_accept),
                                            getString(R.string.text_cancel),
                                            Constants.ALERT_DIALOG_REQUEST_CODE_REMOVE
                                    );
                                    alertDialogFragmentRemove.setCancelable(false);
                                    alertDialogFragmentRemove.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
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

    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        Snackbar snackbar;

        switch(requestCode)
        {
            case Constants.ALERT_DIALOG_REQUEST_CODE_DELETE:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                            Snackbar.LENGTH_LONG);

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

                                viewModel.contentRecyclerViewAdapter.removeItem(viewModel.longClickedElement);

                                ShowLocationsActivity.super.markForDeletion(viewModel.longClickedElement, true);
                                ShowLocationsActivity.super.markForUpdate(viewModel.longClickedElement.getParent());

                                App.content.removeElementAndDescendants(viewModel.longClickedElement);
                                viewModel.longClickedElement.deleteElementAndDescendants();
                            }
                            else
                            {
                                Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onDismissed<DELETE>:: action <DELETE> not confirmed - doing nothing");
                            }
                        }
                    });

                    snackbar.show();
                }
                break;
            }

            case Constants.ALERT_DIALOG_REQUEST_CODE_REMOVE:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.action_confirm_remove_text, viewModel.longClickedElement.getName()),
                            Snackbar.LENGTH_LONG);

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

                                setItemsInRecyclerViewAdapter();

                                ShowLocationsActivity.super.markForDeletion(viewModel.longClickedElement, false);
                                ShowLocationsActivity.super.markForUpdate(viewModel.longClickedElement.getParent());

                                App.content.removeElement(viewModel.longClickedElement);
                                viewModel.longClickedElement.removeElement();
                            }
                            else
                            {
                                Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onDismissed<REMOVE>:: action <REMOVE> not confirmed - doing nothing");
                            }
                        }
                    });

                    snackbar.show();
                }
                break;
            }
        }
    }

    private void setItemsInRecyclerViewAdapter()
    {
        viewModel.contentRecyclerViewAdapter.setItems(viewModel.currentElement.getChildrenOfType(Location.class));
    }
}