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
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.ICategorized;
import de.juliusawen.coastercreditcounter.backend.elements.Element;
import de.juliusawen.coastercreditcounter.backend.elements.ElementType;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.DetailDisplayMode;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.DetailType;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.frontend.menuAgent.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableProvider;
import de.juliusawen.coastercreditcounter.toolbox.ResultFetcher;
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

            this.viewModel.elementTypeToManage = ElementType.values()[getIntent().getIntExtra(Constants.EXTRA_TYPE_TO_MANAGE, ElementType.NONE.ordinal())];

            if(this.viewModel.optionsMenuAgent == null)
            {
                this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
            }

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(ICategorized.class);

                switch(this.viewModel.elementTypeToManage)
                {
                    case ATTRACTION_CATEGORY:
                    {
                        this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                                App.content.getContentOfType(AttractionCategory.class),
                                childTypesToExpand)
                                .setTypefaceForType(AttractionCategory.class, Typeface.BOLD)
                                .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW);

                        break;
                    }

                    case MANUFACTURER:
                    {
                        this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                                App.content.getContentOfType(Manufacturer.class),
                                childTypesToExpand)
                                .setTypefaceForType(Manufacturer.class, Typeface.BOLD)
                                .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.ABOVE)
                                .setDisplayModeForDetail(DetailType.ATTRACTION_CATEGORY, DetailDisplayMode.BELOW);

                        break;
                    }

                    case STATUS:
                    {
                        this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                                App.content.getContentOfType(Status.class),
                                childTypesToExpand)
                                .setTypefaceForType(Status.class, Typeface.BOLD)
                                .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.ABOVE)
                                .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW);

                        break;
                    }
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

            super.addHelpOverlayFragment(getString(R.string.title_help, helpTitle), helpText);
            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(toolbarTitle, null);
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
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }

        IElement resultElement = ResultFetcher.fetchResultElement(data);

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
            List<IElement> resultElements = ResultFetcher.fetchResultElements(data);

            App.content.reorderElements(resultElements);
            updateContentRecyclerView(true).scrollToItem(resultElement);

            super.markForUpdate(resultElements);
        }
        else if(requestCode == Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS
                || requestCode == Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS
                || requestCode == Constants.REQUEST_CODE_ASSIGN_STATUS_TO_ATTRACTIONS)
        {
            List<IElement> resultElements = ResultFetcher.fetchResultElements(data);

            switch(this.viewModel.elementTypeToManage)
            {
                case ATTRACTION_CATEGORY:
                {
                    for(IElement element : resultElements)
                    {
                        ((Attraction)element).setAttractionCategory((AttractionCategory)this.viewModel.longClickedElement);
                        super.markForUpdate(element);
                    }
                    break;
                }

                case MANUFACTURER:
                {
                    for(IElement element : resultElements)
                    {
                        ((Attraction)element).setManufacturer((Manufacturer)this.viewModel.longClickedElement);
                        super.markForUpdate(element);
                    }
                    break;
                }

                case STATUS:
                {
                    for(IElement element : resultElements)
                    {
                        ((Attraction)element).setStatus((Status) this.viewModel.longClickedElement);
                        super.markForUpdate(element);
                    }

                    break;
                }
            }

            updateContentRecyclerView(false);

            Toaster.makeToast(this, getString(R.string.information_assigned_to_attractions, this.viewModel.longClickedElement.getName(), resultElements.size()));

            Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult<ASSIGN_TO_ATTRACTIONS>:: assigned %s to [%d] attractions", this.viewModel.longClickedElement, resultElements.size()));

            updateContentRecyclerView(true);
        }
    }


    //region --- OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(App.isInitialized)
        {
            this.viewModel.optionsMenuAgent
                    .addMenuItem(OptionsMenuAgent.SORT_ATTRACTION_CATEGORIES)
                    .addMenuItem(OptionsMenuAgent.SORT_MANUFACTURERS)
                    .addMenuItem(OptionsMenuAgent.SORT_STATUSES)
                    .addMenuItem(OptionsMenuAgent.EXPAND_ALL)
                    .addMenuItem(OptionsMenuAgent.COLLAPSE_ALL)
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
                    .setVisible(OptionsMenuAgent.SORT_ATTRACTION_CATEGORIES, false)
                    .setVisible(OptionsMenuAgent.SORT_MANUFACTURERS, false)
                    .setVisible(OptionsMenuAgent.SORT_STATUSES, false);

            switch(this.viewModel.elementTypeToManage)
            {
                case ATTRACTION_CATEGORY:
                {
                    this.viewModel.optionsMenuAgent
                            .setEnabled(OptionsMenuAgent.SORT_ATTRACTION_CATEGORIES, App.content.getContentOfType(AttractionCategory.class).size() > 1)
                            .setVisible(OptionsMenuAgent.SORT_ATTRACTION_CATEGORIES, true);
                    break;
                }

                case MANUFACTURER:
                {
                    this.viewModel.optionsMenuAgent
                            .setEnabled(OptionsMenuAgent.SORT_MANUFACTURERS, App.content.getContentOfType(Manufacturer.class).size() > 1)
                            .setVisible(OptionsMenuAgent.SORT_MANUFACTURERS, true);
                    break;
                }

                case STATUS:
                {
                    this.viewModel.optionsMenuAgent
                            .setEnabled(OptionsMenuAgent.SORT_STATUSES, App.content.getContentOfType(Status.class).size() > 1)
                            .setVisible(OptionsMenuAgent.SORT_STATUSES, true);
                    break;
                }
            }

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
        if(this.viewModel.optionsMenuAgent.handleMenuItemSelected(item, this))
        {
            return true;
        }

        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void handleMenuItemSortAttractionCategoriesSelected()
    {
        ActivityDistributor.startActivitySortForResult(
                this,
                Constants.REQUEST_CODE_SORT_ATTRACTION_CATEGORIES,
                App.content.getContentOfType(AttractionCategory.class));
    }

    @Override
    public void handleMenuItemSortManufacturersSelected()
    {
        ActivityDistributor.startActivitySortForResult(
                this,
                Constants.REQUEST_CODE_SORT_MANUFACTURERS,
                App.content.getContentOfType(Manufacturer.class));
    }

    @Override
    public void handleMenuItemSortStatusesSelected()
    {
        ActivityDistributor.startActivitySortForResult(
                this,
                Constants.REQUEST_CODE_SORT_STATUSES,
                App.content.getContentOfType(Status.class));
    }

    @Override
    public void handleMenuItemExpandAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.expandAll();
    }

    @Override
    public void handleMenuItemCollapseAllSelected()
    {
        this.viewModel.contentRecyclerViewAdapter.collapseAll();
    }

    //endregion --- OPTIONS MENU


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

                    switch(viewModel.elementTypeToManage)
                    {
                        case ATTRACTION_CATEGORY:
                            isDefault = viewModel.longClickedElement.equals(AttractionCategory.getDefault());
                            break;

                        case MANUFACTURER:
                            isDefault = viewModel.longClickedElement.equals(Manufacturer.getDefault());
                            break;

                        case STATUS:
                            isDefault = viewModel.longClickedElement.equals(Status.getDefault());
                            break;
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

                                switch(viewModel.elementTypeToManage)
                                {
                                    case ATTRACTION_CATEGORY:
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

                                        ActivityDistributor.startActivityPickForResult(
                                                ManageOrphanElementsActivity.this,
                                                Constants.REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS,
                                                attractions);

                                        break;
                                    }

                                    case MANUFACTURER:
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

                                        ActivityDistributor.startActivityPickForResult(
                                                ManageOrphanElementsActivity.this,
                                                Constants.REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS,
                                                attractions);

                                        break;
                                    }

                                    case STATUS:
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

                                        ActivityDistributor.startActivityPickForResult(
                                                ManageOrphanElementsActivity.this,
                                                Constants.REQUEST_CODE_ASSIGN_STATUS_TO_ATTRACTIONS,
                                                attractions);

                                        break;
                                    }
                                }

                                return true;
                            }

                            else if(id == Constants.SELECTION_EDIT_ELEMENT)
                            {
                                switch(viewModel.elementTypeToManage)
                                {
                                    case ATTRACTION_CATEGORY:
                                        ActivityDistributor.startActivityEditForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_EDIT_ATTRACTION_CATEGORY, viewModel.longClickedElement);
                                        break;

                                    case MANUFACTURER:
                                        ActivityDistributor.startActivityEditForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_EDIT_MANUFACTURER, viewModel.longClickedElement);
                                        break;

                                    case STATUS:
                                        ActivityDistributor.startActivityEditForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_EDIT_STATUS, viewModel.longClickedElement);
                                        break;
                                }

                                return true;
                            }

                            else if(id == Constants.SELECTION_DELETE_ELEMENT)
                            {
                                String alertDialogMessage;
                                if(viewModel.longClickedElement.hasChildren())
                                {
                                    String defaultName;

                                    switch(viewModel.elementTypeToManage)
                                    {
                                        case ATTRACTION_CATEGORY:
                                            defaultName = AttractionCategory.getDefault().getName();
                                            break;

                                        case MANUFACTURER:
                                            defaultName = Manufacturer.getDefault().getName();
                                            break;

                                        case STATUS:
                                            defaultName = Status.getDefault().getName();
                                            break;

                                            default:
                                                defaultName = getString(R.string.error_missing_text);
                                                break;
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
                                    switch(viewModel.elementTypeToManage)
                                    {
                                        case ATTRACTION_CATEGORY:
                                            child.setAttractionCategory(AttractionCategory.getDefault());
                                            break;

                                        case MANUFACTURER:
                                            child.setManufacturer(Manufacturer.getDefault());
                                            break;

                                        case STATUS:
                                            child.setStatus(Status.getDefault());
                                            break;
                                    }

                                    markForUpdate(child);
                                }
                            }

                            ManageOrphanElementsActivity.super.markForDeletion(viewModel.longClickedElement, false);
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
                switch(this.viewModel.elementTypeToManage)
                {
                    case ATTRACTION_CATEGORY:
                    {
                        Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default AttractionCategory",
                                this.viewModel.longClickedElement));

                        super.markForUpdate(AttractionCategory.getDefault());
                        super.markForUpdate(this.viewModel.longClickedElement);
                        AttractionCategory.setDefault((AttractionCategory)this.viewModel.longClickedElement);

                        Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));

                        break;
                    }

                    case MANUFACTURER:
                    {
                        Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default Manufacturer",
                                this.viewModel.longClickedElement));

                        super.markForUpdate(Manufacturer.getDefault());
                        super.markForUpdate(this.viewModel.longClickedElement);
                        Manufacturer.setDefault((Manufacturer) this.viewModel.longClickedElement);

                        Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));

                        break;
                    }

                    case STATUS:
                    {
                        Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default Status",
                                this.viewModel.longClickedElement));

                        super.markForUpdate(Status.getDefault());
                        super.markForUpdate(this.viewModel.longClickedElement);
                        Status.setDefault((Status) this.viewModel.longClickedElement);

                        Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));

                        break;
                    }
                }
            }
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ManageOrphanElementsActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                switch(viewModel.elementTypeToManage)
                {
                    case ATTRACTION_CATEGORY:
                        ActivityDistributor.startActivityCreateForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY, null);
                        break;

                    case MANUFACTURER:
                        ActivityDistributor.startActivityCreateForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_CREATE_MANUFACTURER, null);
                        break;

                    case STATUS:
                        ActivityDistributor.startActivityCreateForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_CREATE_STATUS, null);
                        break;
                }
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private ContentRecyclerViewAdapter updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(Constants.LOG_TAG, "ManageOrphanElementsActivity.updateContentRecyclerView:: resetting content...");

            switch(this.viewModel.elementTypeToManage)
            {
                case ATTRACTION_CATEGORY:
                    this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(AttractionCategory.class));
                    break;

                case MANUFACTURER:
                    this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(Manufacturer.class));
                    break;

                case STATUS:
                    this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(Status.class));
                    break;
            }
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ManageOrphanElementsActivity.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }

        return this.viewModel.contentRecyclerViewAdapter;
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
