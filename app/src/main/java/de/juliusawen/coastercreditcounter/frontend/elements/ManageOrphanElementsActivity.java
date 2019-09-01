package de.juliusawen.coastercreditcounter.frontend.elements;

import android.app.Activity;
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
import java.util.HashSet;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.GroupHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.ICategorized;
import de.juliusawen.coastercreditcounter.backend.elements.Element;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ManageOrphanElementsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private ManageOrphanElementsViewModel viewModel;
    private RecyclerView recyclerView;
    private boolean actionConfirmed;
    private OrphanElement lastCreatedOrphanElement;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ManageOrphanElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_manage_orphan_elements);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(ManageOrphanElementsViewModel.class);

            this.viewModel.typeToManage = getIntent().getIntExtra(Constants.EXTRA_TYPE_TO_MANAGE, Constants.TYPE_NONE);

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(ICategorized.class);

                if(this.viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                            App.content.getContentOfType(AttractionCategory.class),
                            childTypesToExpand,
                            GroupHeaderProvider.GroupType.NONE);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategory.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.MANUFACTURER, ContentRecyclerViewAdapter.DisplayMode.ABOVE);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.BELOW);
                }
                else if(this.viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                            App.content.getContentOfType(Manufacturer.class),
                            childTypesToExpand,
                            GroupHeaderProvider.GroupType.NONE);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Manufacturer.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.ABOVE);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.ATTRACTION_CATEGORY, ContentRecyclerViewAdapter.DisplayMode.BELOW);
                }
                else if(this.viewModel.typeToManage == Constants.TYPE_STATUS)
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                            App.content.getContentOfType(Status.class),
                            childTypesToExpand,
                            GroupHeaderProvider.GroupType.NONE);

                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(Status.class, Typeface.BOLD);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.LOCATION, ContentRecyclerViewAdapter.DisplayMode.ABOVE);
                    this.viewModel.contentRecyclerViewAdapter.setDisplayModeForDetail(ContentRecyclerViewAdapter.DetailType.STATUS, ContentRecyclerViewAdapter.DisplayMode.BELOW);
                }
            }

            if(this.viewModel.contentRecyclerViewAdapter != null)
            {
                this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
                this.recyclerView = findViewById(R.id.recyclerViewManageOrphanElements);
                this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);
            }

            Intent intent = getIntent();
            String toolbarTitle = intent.getStringExtra(Constants.EXTRA_TOOLBAR_TITLE);
            String helpTitle = intent.getStringExtra(Constants.EXTRA_HELP_TITLE);
            String helpText = intent.getStringExtra(Constants.EXTRA_HELP_TEXT);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(toolbarTitle, null);

            super.addHelpOverlayFragment(getString(R.string.title_help, helpTitle), helpText);

            super.addFloatingActionButton();
            this.decorateFloatingActionButton();
        }
    }

    @Override
    protected void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }

        IElement resultElement = ResultTool.fetchResultElement(data);

        if(requestCode == Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY
                || requestCode == Constants.REQUEST_CODE_CREATE_MANUFACTURER
                || requestCode == Constants.REQUEST_CODE_CREATE_STATUS)
        {
            this.lastCreatedOrphanElement = (OrphanElement)resultElement;
            updateContentRecyclerView(true);
        }
        else if(requestCode == Constants.REQUEST_CODE_EDIT_ATTRACTION_CATEGORY
                || requestCode == Constants.REQUEST_CODE_EDIT_MANUFACTURER
                || requestCode == Constants.REQUEST_CODE_EDIT_STATUS)
        {
            updateContentRecyclerView(false);
        }
        else if(requestCode == Constants.REQUEST_CODE_SORT_ATTRACTION_CATEGORIES
                || requestCode == Constants.REQUEST_CODE_SORT_MANUFACTURERS
                || requestCode == Constants.REQUEST_CODE_SORT_STATUSES)
        {
            List<IElement> resultElements = ResultTool.fetchResultElements(data);

            App.content.reorderElements(resultElements);
            updateContentRecyclerView(true);

            if(resultElement != null)
            {
                Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult<SORT>:: scrolling to selected element %s...", resultElement));
                this.viewModel.contentRecyclerViewAdapter.scrollToItem(resultElement);
            }

            this.markForUpdate(resultElements);
        }
        else if(requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS
                || requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS
                || requestCode == Constants.REQUEST_CODE_ASSIGN_STATUS_TO_ATTRACTIONS)
        {
            List<IElement> resultElements = ResultTool.fetchResultElements(data);

            if(this.viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
            {
                for(IElement element : resultElements)
                {
                    ((Attraction)element).setAttractionCategory((AttractionCategory)this.viewModel.longClickedElement);
                    super.markForUpdate(element);
                }
            }
            else if(this.viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
            {
                for(IElement element : resultElements)
                {
                    ((Attraction)element).setManufacturer((Manufacturer)this.viewModel.longClickedElement);
                    super.markForUpdate(element);
                }
            }
            else if(this.viewModel.typeToManage == Constants.TYPE_STATUS)
            {
                for(IElement element : resultElements)
                {
                    ((Attraction)element).setStatus((Status) this.viewModel.longClickedElement);
                    super.markForUpdate(element);
                }
            }

            updateContentRecyclerView(false);

            Toaster.makeToast(this, getString(R.string.information_assigned_to_attractions, this.viewModel.longClickedElement.getName(), resultElements.size()));

            Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult<ASSIGN_TO_ATTRACTIONS>:: assigned %s to [%d] attractions",
                    this.viewModel.longClickedElement, resultElements.size()));

            updateContentRecyclerView(true);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
        {
            if(App.content.getContentOfType(AttractionCategory.class).size() > 1)
            {
                menu.add(Menu.NONE, Constants.SELECTION_SORT_ATTRACTION_CATEGORIES, Menu.NONE, R.string.selection_sort_attraction_categories);
            }
        }
        else if(this.viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
        {
            if(App.content.getContentOfType(AttractionCategory.class).size() > 1)
            {
                menu.add(Menu.NONE, Constants.SELECTION_SORT_MANUFACTURERS, Menu.NONE, R.string.selection_sort_manufacturers);
            }
        }
        else if(this.viewModel.typeToManage == Constants.TYPE_STATUS)
        {
            if(App.content.getContentOfType(Status.class).size() > 1)
            {
                menu.add(Menu.NONE, Constants.SELECTION_SORT_STATUSES, Menu.NONE, R.string.selection_sort_statuses);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onOptionItemSelected:: [%S] selected", item.getItemId()));

        int id = item.getItemId();
        if(id == Constants.SELECTION_SORT_ATTRACTION_CATEGORIES)
        {
            ActivityTool.startActivitySortForResult(
                    this,
                    Constants.REQUEST_CODE_SORT_ATTRACTION_CATEGORIES,
                    App.content.getContentOfType(AttractionCategory.class));
            return true;
        }
        else if(id == Constants.SELECTION_SORT_MANUFACTURERS)
        {
            ActivityTool.startActivitySortForResult(
                    this,
                    Constants.REQUEST_CODE_SORT_MANUFACTURERS,
                    App.content.getContentOfType(Manufacturer.class));
            return true;
        }
        else if(id == Constants.SELECTION_SORT_STATUSES)
        {
            ActivityTool.startActivitySortForResult(
                    this,
                    Constants.REQUEST_CODE_SORT_STATUSES,
                    App.content.getContentOfType(Status.class));
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(this.lastCreatedOrphanElement != null)
            {
                this.returnResult(RESULT_OK);
            }
            else
            {
                this.returnResult(RESULT_CANCELED);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element)view.getTag();

                if((element instanceof AttractionCategory) || (element instanceof Manufacturer))
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (IElement)view.getTag();

                if((viewModel.longClickedElement instanceof AttractionCategory)
                        || (viewModel.longClickedElement instanceof Manufacturer)
                        || (viewModel.longClickedElement instanceof Status))
                {
                    boolean isDefault = false;

                    if(viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
                    {
                        isDefault = viewModel.longClickedElement.equals(AttractionCategory.getDefault());
                    }
                    else if(viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
                    {
                        isDefault = viewModel.longClickedElement.equals(Manufacturer.getDefault());
                    }
                    else if(viewModel.typeToManage == Constants.TYPE_STATUS)
                    {
                        isDefault = viewModel.longClickedElement.equals(Status.getDefault());
                    }

                    Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onLongClick:: %s long clicked", viewModel.longClickedElement));

                    PopupMenu popupMenu = new PopupMenu(ManageOrphanElementsActivity.this, view);

                    popupMenu.getMenu().add(0, Constants.SELECTION_ASSIGN_TO_ATTRACTIONS, Menu.NONE, R.string.selection_assign_to_attractions)
                            .setEnabled(!App.content.getContentAsType(ICategorized.class).isEmpty());

                    popupMenu.getMenu().add(0, Constants.SELECTION_EDIT_ELEMENT, Menu.NONE, R.string.selection_edit);

                    popupMenu.getMenu().add(0, Constants.SELECTION_DELETE_ELEMENT, Menu.NONE, R.string.selection_delete).setEnabled(!isDefault);

                    popupMenu.getMenu().add(0, Constants.SELECTION_SET_AS_DEFAULT, Menu.NONE, R.string.selection_set_as_default).setEnabled(!isDefault);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick:: [%S] selected", item.getItemId()));

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            int id = item.getItemId();

                            if(id == Constants.SELECTION_ASSIGN_TO_ATTRACTIONS)
                            {
                                List<IElement> attractions = new ArrayList<IElement>(App.content.getContentAsType(ICategorized.class));

                                if(viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
                                {
                                    for(IAttraction attraction : ConvertTool.convertElementsToType(attractions, IAttraction.class))
                                    {
                                        if(attraction.getAttractionCategory().equals(viewModel.longClickedElement))
                                        {
                                            Log.v(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: " +
                                                    "removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                            attractions.remove(attraction);
                                        }
                                    }

                                    ActivityTool.startActivityPickForResult(
                                            ManageOrphanElementsActivity.this,
                                            Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS,
                                            attractions);
                                }
                                else if(viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
                                {
                                    for(IAttraction attraction : ConvertTool.convertElementsToType(attractions, IAttraction.class))
                                    {
                                        if(attraction.getManufacturer().equals(viewModel.longClickedElement))
                                        {
                                            Log.v(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: " +
                                                    "removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                            attractions.remove(attraction);
                                        }
                                    }

                                    ActivityTool.startActivityPickForResult(
                                            ManageOrphanElementsActivity.this,
                                            Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS,
                                            attractions);
                                }
                                else if(viewModel.typeToManage == Constants.TYPE_STATUS)
                                {
                                    for(IAttraction attraction : ConvertTool.convertElementsToType(attractions, IAttraction.class))
                                    {
                                        if(attraction.getManufacturer().equals(viewModel.longClickedElement))
                                        {
                                            Log.v(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: " +
                                                    "removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                            attractions.remove(attraction);
                                        }
                                    }

                                    ActivityTool.startActivityPickForResult(
                                            ManageOrphanElementsActivity.this,
                                            Constants.REQUEST_CODE_ASSIGN_STATUS_TO_ATTRACTIONS,
                                            attractions);
                                }

                                return true;
                            }

                            else if(id == Constants.SELECTION_EDIT_ELEMENT)
                            {
                                if(viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
                                {
                                    ActivityTool.startActivityEditForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_EDIT_ATTRACTION_CATEGORY, viewModel.longClickedElement);
                                }
                                else if(viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
                                {
                                    ActivityTool.startActivityEditForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_EDIT_MANUFACTURER, viewModel.longClickedElement);
                                }
                                else if(viewModel.typeToManage == Constants.TYPE_STATUS)
                                {
                                    ActivityTool.startActivityEditForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_EDIT_STATUS, viewModel.longClickedElement);
                                }

                                return true;
                            }

                            else if(id == Constants.SELECTION_DELETE_ELEMENT)
                            {
                                String alertDialogMessage;
                                if(viewModel.longClickedElement.hasChildren())
                                {
                                    String defaultName;

                                    if(viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
                                    {
                                        defaultName = AttractionCategory.getDefault().getName();
                                    }
                                    else if(viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
                                    {
                                        defaultName = Manufacturer.getDefault().getName();
                                    }
                                    else if(viewModel.typeToManage == Constants.TYPE_STATUS)
                                    {
                                        defaultName = Status.getDefault().getName();
                                    }
                                    else
                                    {
                                        defaultName = getString(R.string.error_missing_text);
                                    }

                                    alertDialogMessage = getString(R.string.alert_dialog_message_delete_orphan_element_has_children,
                                            viewModel.longClickedElement.getChildCount(),
                                            viewModel.longClickedElement.getName(),
                                            defaultName);
                                }
                                else
                                {
                                    alertDialogMessage = getString(R.string.alert_dialog_message_delete_orphan_element_has_no_children,
                                            viewModel.longClickedElement.getName());
                                }

                                AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                                        R.drawable.ic_baseline_warning,
                                        getString(R.string.alert_dialog_title_delete_element),
                                        alertDialogMessage,
                                        getString(R.string.text_accept),
                                        getString(R.string.text_cancel),
                                        Constants.REQUEST_CODE_DELETE,
                                        false);

                                alertDialogFragmentDelete.setCancelable(false);
                                alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                                return true;
                            }
                            else if(id == Constants.SELECTION_SET_AS_DEFAULT)
                            {
                                String alterDialogMessage = getString(R.string.alert_dialog_message_set_as_default, viewModel.longClickedElement.getName());

                                AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                                        R.drawable.ic_baseline_warning,
                                        getString(R.string.alert_dialog_title_set_as_default),
                                        alterDialogMessage,
                                        getString(R.string.text_accept),
                                        getString(R.string.text_cancel),
                                        Constants.REQUEST_CODE_SET_AS_DEFAULT,
                                        false);

                                alertDialogFragmentDelete.setCancelable(false);
                                alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);

                                return true;
                            }
                            else
                            {
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

        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            if(requestCode == Constants.REQUEST_CODE_DELETE)
            {
                Snackbar snackbar = Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                        Snackbar.LENGTH_LONG);

                snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        actionConfirmed = true;
                        Log.i(Constants.LOG_TAG, "ManageOrphanElementsActivity.onSnackbarClick:: action <DELETE> confirmed");
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

                            Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onSnackbarDismissed<DELETE>:: deleting %s...", viewModel.longClickedElement));

                            if(viewModel.longClickedElement.hasChildren())
                            {
                                List<IAttraction> children = new ArrayList<>(viewModel.longClickedElement.getChildrenAsType(IAttraction.class));

                                for(IAttraction child : children)
                                {
                                    if(viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
                                    {
                                        child.setManufacturer(Manufacturer.getDefault());
                                    }
                                    else if(viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
                                    {
                                        child.setAttractionCategory(AttractionCategory.getDefault());
                                    }
                                    else if(viewModel.typeToManage == Constants.TYPE_STATUS)
                                    {
                                        child.setStatus(Status.getDefault());
                                    }

                                    markForUpdate(child);
                                }
                            }

                            markForDeletion(viewModel.longClickedElement, false);
                            updateContentRecyclerView(true);
                        }
                        else
                        {
                            Log.d(Constants.LOG_TAG, "ManageOrphanElementsActivity.onSnackbarDismissed<DELETE>:: action not confirmed - doing nothing");
                        }
                    }
                });

                snackbar.show();
            }
            else if(requestCode == Constants.REQUEST_CODE_SET_AS_DEFAULT)
            {
                if(this.viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
                {
                    Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default AttractionCategory",
                            this.viewModel.longClickedElement));

                    super.markForUpdate(AttractionCategory.getDefault());

                    AttractionCategory.setDefault((AttractionCategory)this.viewModel.longClickedElement);
                    super.markForUpdate(this.viewModel.longClickedElement);

                    Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                }
                else if(this.viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
                {
                    Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default Manufacturer",
                            this.viewModel.longClickedElement));

                    super.markForUpdate(Manufacturer.getDefault());

                    Manufacturer.setDefault((Manufacturer) this.viewModel.longClickedElement);
                    super.markForUpdate(this.viewModel.longClickedElement);

                    Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                }
                else if(this.viewModel.typeToManage == Constants.TYPE_STATUS)
                {
                    Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default Status",
                            this.viewModel.longClickedElement));

                    super.markForUpdate(Status.getDefault());

                    Status.setDefault((Status) this.viewModel.longClickedElement);
                    super.markForUpdate(this.viewModel.longClickedElement);

                    Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                }
            }
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ManageOrphanElementsActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                if(viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
                {
                    ActivityTool.startActivityCreateForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY, null);
                }
                else if(viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
                {
                    ActivityTool.startActivityCreateForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_CREATE_MANUFACTURER, null);
                }
                else if(viewModel.typeToManage == Constants.TYPE_STATUS)
                {
                    ActivityTool.startActivityCreateForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_CREATE_STATUS, null);
                }
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(Constants.LOG_TAG, "ManageOrphanElementsActivity.updateContentRecyclerView:: resetting content...");

            if(this.viewModel.typeToManage == Constants.TYPE_ATTRACTION_CATEGORY)
            {
                this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(AttractionCategory.class));
            }
            else if(this.viewModel.typeToManage == Constants.TYPE_MANUFACTURER)
            {
                this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(Manufacturer.class));
            }
            else if(this.viewModel.typeToManage == Constants.TYPE_STATUS)
            {
                this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(Status.class));
            }
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ManageOrphanElementsActivity.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.returnResult:: returning new %s", this.lastCreatedOrphanElement));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.lastCreatedOrphanElement.getUuid().toString());
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
