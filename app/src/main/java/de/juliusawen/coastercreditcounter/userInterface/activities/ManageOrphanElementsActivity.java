package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.ICategorized;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.OrphanElementType;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.Status;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.ConfirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.ConfirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailDisplayMode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ManageOrphanElementsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ManageOrphanElementsViewModel viewModel;
    private RecyclerView recyclerView;
    private OrphanElement lastCreatedOrphanElement;


    protected void setContentView()
    {
        setContentView(R.layout.activity_manage_orphan_elements);
    }

    protected void create()
    {
        this.viewModel = ViewModelProviders.of(this).get(ManageOrphanElementsViewModel.class);

        this.viewModel.orphanElementTypeToManage = OrphanElementType.values()[getIntent().getIntExtra(Constants.EXTRA_TYPE_TO_MANAGE, OrphanElementType.NONE.ordinal())];

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
            childTypesToExpand.add(ICategorized.class);

            switch(this.viewModel.orphanElementTypeToManage)
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
        switch(RequestCode.values()[requestCode])
        {
            case CREATE_ATTRACTION_CATEGORY:
            case CREATE_MANUFACTURER:
            case CREATE_STATUS:
                this.lastCreatedOrphanElement = (OrphanElement)resultElement;
                updateContentRecyclerView(true);
                break;

            case EDIT_ATTRACTION_CATEGORY:
            case EDIT_MANUFACTURER:
            case EDIT_STATUS:
                updateContentRecyclerView(false);
                break;

            case SORT_ATTRACTION_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_STATUSES:
                List<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                App.content.reorderElements(resultElements);
                updateContentRecyclerView(true).scrollToItem(resultElement);
                super.markForUpdate(resultElements);
                break;

            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                resultElements = ResultFetcher.fetchResultElements(data);

                switch(this.viewModel.orphanElementTypeToManage)
                {
                    case ATTRACTION_CATEGORY:
                        for(IElement element : resultElements)
                        {
                            ((Attraction)element).setAttractionCategory((AttractionCategory)this.viewModel.longClickedElement);
                            super.markForUpdate(element);
                        }
                        break;

                    case MANUFACTURER:
                        for(IElement element : resultElements)
                        {
                            ((Attraction)element).setManufacturer((Manufacturer)this.viewModel.longClickedElement);
                            super.markForUpdate(element);
                        }
                        break;

                    case STATUS:
                        for(IElement element : resultElements)
                        {
                            ((Attraction)element).setStatus((Status) this.viewModel.longClickedElement);
                            super.markForUpdate(element);
                        }
                        break;
                }

                updateContentRecyclerView(false);
                Toaster.makeToast(this, getString(R.string.information_assigned_to_attractions, this.viewModel.longClickedElement.getName(), resultElements.size()));
                Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult<ASSIGN_TO_ATTRACTIONS>:: assigned %s to [%d] attractions", this.viewModel.longClickedElement, resultElements.size()));
                updateContentRecyclerView(true);
        }

    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .add(OptionsItem.SORT_ATTRACTION_CATEGORIES)
                .add(OptionsItem.SORT_MANUFACTURERS)
                .add(OptionsItem.SORT_STATUSES)
                .add(OptionsItem.EXPAND_ALL)
                .add(OptionsItem.COLLAPSE_ALL)
                .create(menu);
    }

    @Override
    protected  Menu prepareOptionsMenu(Menu menu)
    {
        this.viewModel.optionsMenuAgent
                .setVisible(OptionsItem.SORT_ATTRACTION_CATEGORIES, false)
                .setVisible(OptionsItem.SORT_MANUFACTURERS, false)
                .setVisible(OptionsItem.SORT_STATUSES, false);

        switch(this.viewModel.orphanElementTypeToManage)
        {
            case ATTRACTION_CATEGORY:
            {
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_ATTRACTION_CATEGORIES, App.content.getContentOfType(AttractionCategory.class).size() > 1)
                        .setVisible(OptionsItem.SORT_ATTRACTION_CATEGORIES, true);
                break;
            }

            case MANUFACTURER:
            {
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_MANUFACTURERS, App.content.getContentOfType(Manufacturer.class).size() > 1)
                        .setVisible(OptionsItem.SORT_MANUFACTURERS, true);
                break;
            }

            case STATUS:
            {
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_STATUSES, App.content.getContentOfType(Status.class).size() > 1)
                        .setVisible(OptionsItem.SORT_STATUSES, true);
                break;
            }
        }

        return this.viewModel.optionsMenuAgent
                .setEnabled(OptionsItem.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .setEnabled(OptionsItem.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                .prepare(menu);
    }

    @Override
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        switch(item)
        {
            case SORT_ATTRACTION_CATEGORIES:
                ActivityDistributor.startActivitySortForResult(
                        this,
                        RequestCode.SORT_ATTRACTION_CATEGORIES,
                        App.content.getContentOfType(AttractionCategory.class));
                return true;

            case SORT_MANUFACTURERS:
                ActivityDistributor.startActivitySortForResult(
                        this,
                        RequestCode.SORT_MANUFACTURERS,
                        App.content.getContentOfType(Manufacturer.class));
                return true;

            case SORT_STATUSES:
                ActivityDistributor.startActivitySortForResult(
                        this,
                        RequestCode.SORT_STATUSES,
                        App.content.getContentOfType(Status.class));
                return true;

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

                    switch(viewModel.orphanElementTypeToManage)
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

                    PopupMenuAgent.getMenu()
                            .add(PopupItem.ASSIGN_TO_ATTRACTIONS)
                            .add(PopupItem.EDIT_ELEMENT)
                            .add(PopupItem.DELETE_ELEMENT)
                            .add(PopupItem.SET_AS_DEFAULT)
                            .setEnabled(PopupItem.ASSIGN_TO_ATTRACTIONS, !App.content.getContentAsType(ICategorized.class).isEmpty())
                            .setEnabled(PopupItem.DELETE_ELEMENT, !isDefault)
                            .setEnabled(PopupItem.SET_AS_DEFAULT, !isDefault)
                            .show(ManageOrphanElementsActivity.this, view);
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
            case ASSIGN_TO_ATTRACTIONS:
            {
                List<IElement> attractions = new ArrayList<IElement>(App.content.getContentAsType(ICategorized.class));

                switch(viewModel.orphanElementTypeToManage)
                {
                    case ATTRACTION_CATEGORY:
                    {
                        for(IAttraction attraction : ConvertTool.convertElementsToType(attractions, IAttraction.class))
                        {
                            if(attraction.getAttractionCategory().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                attractions.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(
                                ManageOrphanElementsActivity.this,
                                RequestCode.ASSIGN_CATEGORY_TO_ATTRACTIONS,
                                attractions);

                        break;
                    }

                    case MANUFACTURER:
                    {
                        for(IAttraction attraction : ConvertTool.convertElementsToType(attractions, IAttraction.class))
                        {
                            if(attraction.getManufacturer().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                attractions.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(
                                ManageOrphanElementsActivity.this,
                                RequestCode.ASSIGN_MANUFACTURERS_TO_ATTRACTIONS,
                                attractions);

                        break;
                    }

                    case STATUS:
                    {
                        for(IAttraction attraction : ConvertTool.convertElementsToType(attractions, IAttraction.class))
                        {
                            if(attraction.getManufacturer().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                attractions.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(
                                ManageOrphanElementsActivity.this,
                                RequestCode.ASSIGN_STATUS_TO_ATTRACTIONS,
                                attractions);

                        break;
                    }
                }
                break;
            }

            case EDIT_ELEMENT:
            {
                switch(viewModel.orphanElementTypeToManage)
                {
                    case ATTRACTION_CATEGORY:
                        ActivityDistributor.startActivityEditForResult(ManageOrphanElementsActivity.this, RequestCode.EDIT_ATTRACTION_CATEGORY, viewModel.longClickedElement);
                        break;

                    case MANUFACTURER:
                        ActivityDistributor.startActivityEditForResult(ManageOrphanElementsActivity.this, RequestCode.EDIT_MANUFACTURER, viewModel.longClickedElement);
                        break;

                    case STATUS:
                        ActivityDistributor.startActivityEditForResult(ManageOrphanElementsActivity.this, RequestCode.EDIT_STATUS, viewModel.longClickedElement);
                        break;
                }
                break;
            }

            case DELETE_ELEMENT:
            {
                String alertDialogMessage;
                if(viewModel.longClickedElement.hasChildren())
                {
                    String defaultName;

                    switch(viewModel.orphanElementTypeToManage)
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
                        RequestCode.DELETE,
                        false);

                alertDialogFragmentDelete.setCancelable(false);
                alertDialogFragmentDelete.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
                break;
            }

            case SET_AS_DEFAULT:
            {
                String alterDialogMessage = getString(R.string.alert_dialog_message_set_as_default, viewModel.longClickedElement.getName());

                AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                        R.drawable.ic_baseline_warning,
                        getString(R.string.alert_dialog_title_set_as_default),
                        alterDialogMessage,
                        getString(R.string.text_accept),
                        getString(R.string.text_cancel),
                        RequestCode.SET_AS_DEFAULT,
                        false);

                alertDialogFragmentDelete.setCancelable(false);
                alertDialogFragmentDelete.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
                break;
            }
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
                {
                    ConfirmSnackbar.Show(
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                                    Snackbar.LENGTH_LONG),
                            requestCode,
                            this);
                    break;
                }

                case SET_AS_DEFAULT:
                {
                    switch(this.viewModel.orphanElementTypeToManage)
                    {
                        case ATTRACTION_CATEGORY:
                        {
                            Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.handleAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default AttractionCategory", this.viewModel.longClickedElement));

                            super.markForUpdate(AttractionCategory.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            AttractionCategory.setDefault((AttractionCategory) this.viewModel.longClickedElement);

                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));

                            break;
                        }

                        case MANUFACTURER:
                        {
                            Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.handleAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default Manufacturer", this.viewModel.longClickedElement));

                            super.markForUpdate(Manufacturer.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            Manufacturer.setDefault((Manufacturer) this.viewModel.longClickedElement);

                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));

                            break;
                        }

                        case STATUS:
                        {
                            Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.handleAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default Status", this.viewModel.longClickedElement));

                            super.markForUpdate(Status.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            Status.setDefault((Status) this.viewModel.longClickedElement);

                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));

                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        if(requestCode.equals(RequestCode.DELETE))
        {
            Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.handleActionConfirmed:: deleting %s...", this.viewModel.longClickedElement));

            if(this.viewModel.longClickedElement.hasChildren())
            {
                List<IAttraction> children = new ArrayList<>(this.viewModel.longClickedElement.getChildrenAsType(IAttraction.class));

                for(IAttraction child : children)
                {
                    switch(this.viewModel.orphanElementTypeToManage)
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

            super.markForDeletion(this.viewModel.longClickedElement, false);
            this.updateContentRecyclerView(true);
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

                switch(viewModel.orphanElementTypeToManage)
                {
                    case ATTRACTION_CATEGORY:
                        ActivityDistributor.startActivityCreateForResult(ManageOrphanElementsActivity.this, RequestCode.CREATE_ATTRACTION_CATEGORY, null);
                        break;

                    case MANUFACTURER:
                        ActivityDistributor.startActivityCreateForResult(ManageOrphanElementsActivity.this, RequestCode.CREATE_MANUFACTURER, null);
                        break;

                    case STATUS:
                        ActivityDistributor.startActivityCreateForResult(ManageOrphanElementsActivity.this, RequestCode.CREATE_STATUS, null);
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

            switch(this.viewModel.orphanElementTypeToManage)
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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
