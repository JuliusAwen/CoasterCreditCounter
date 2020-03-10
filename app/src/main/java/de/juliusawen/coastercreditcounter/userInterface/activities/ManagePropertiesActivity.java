package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCategoryProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasCreditTypeProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasManufacturerProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IHasStatusProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.PropertyType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
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

public class ManagePropertiesActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ManagePropertiesViewModel viewModel;
    private RecyclerView recyclerView;
    private IProperty lastCreatedProperty;


    protected void setContentView()
    {
        setContentView(R.layout.activity_manage_properties);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ManagePropertiesViewModel.class);

        this.viewModel.propertyTypeToManage = PropertyType.values()[getIntent().getIntExtra(Constants.EXTRA_TYPE_TO_MANAGE, -1)];

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
            childTypesToExpand.add(IAttraction.class);

            List<IElement> elementsWithOrderedChildren;

            switch(this.viewModel.propertyTypeToManage)
            {
                case CREDIT_TYPE:
                    elementsWithOrderedChildren = App.content.getContentOfType(CreditType.class);
                    for(IElement element : elementsWithOrderedChildren)
                    {
                        element.reorderChildren(SortTool.sortElements(element.getChildren(), SortType.BY_NAME, SortOrder.ASCENDING));
                    }

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                            elementsWithOrderedChildren,
                            childTypesToExpand)
                            .setTypefaceForContentType(CreditType.class, Typeface.BOLD)
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW);
                    break;

                case CATEGORY:
                    elementsWithOrderedChildren = App.content.getContentOfType(Category.class);
                    for(IElement element : elementsWithOrderedChildren)
                    {
                        element.reorderChildren(SortTool.sortElements(element.getChildren(), SortType.BY_NAME, SortOrder.ASCENDING));
                    }

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                            elementsWithOrderedChildren,
                            childTypesToExpand)
                            .setTypefaceForContentType(Category.class, Typeface.BOLD)
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW);
                    break;

                case MANUFACTURER:
                    elementsWithOrderedChildren = App.content.getContentOfType(Manufacturer.class);
                    for(IElement element : elementsWithOrderedChildren)
                    {
                        element.reorderChildren(SortTool.sortElements(element.getChildren(), SortType.BY_NAME, SortOrder.ASCENDING));
                    }

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                            elementsWithOrderedChildren,
                            childTypesToExpand)
                            .setTypefaceForContentType(Manufacturer.class, Typeface.BOLD)
                            .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW);
                    break;

                case STATUS:
                    elementsWithOrderedChildren = App.content.getContentOfType(Status.class);
                    for(IElement element : elementsWithOrderedChildren)
                    {
                        element.reorderChildren(SortTool.sortElements(element.getChildren(), SortType.BY_NAME, SortOrder.ASCENDING));
                    }
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                            elementsWithOrderedChildren,
                            childTypesToExpand)
                            .setTypefaceForContentType(Status.class, Typeface.BOLD)
                            .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                            .setDisplayModeForDetail(DetailType.LOCATION, DetailDisplayMode.BELOW)
                            .setDisplayModeForDetail(DetailType.CATEGORY, DetailDisplayMode.BELOW);
                    break;
            }
            this.viewModel.contentRecyclerViewAdapter.setSpecialStringResourceForType(IProperty.class, R.string.substitute_properties_default_postfix);
        }

        if(this.viewModel.contentRecyclerViewAdapter != null)
        {
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
            this.recyclerView = findViewById(R.id.recyclerViewManageProperties);
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
        super.setToolbarTitleAndSubtitle(toolbarTitle, getString(R.string.subtitle_management));
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

        Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }

        IElement resultElement = ResultFetcher.fetchResultElement(data);
        switch(RequestCode.values()[requestCode])
        {
            case CREATE_CREDIT_TYPE:
            case CREATE_CATEGORY:
            case CREATE_MANUFACTURER:
            case CREATE_STATUS:
                this.lastCreatedProperty = (IProperty) resultElement;
                updateContentRecyclerView(true);
                break;

            case EDIT_CREDIT_TYPE:
            case EDIT_CATEGORY:
            case EDIT_MANUFACTURER:
            case EDIT_STATUS:
                updateContentRecyclerView(false);
                break;

            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_STATUSES:
                ArrayList<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                App.content.reorderElements(resultElements);
                updateContentRecyclerView(true).scrollToItem(resultElement);
                super.markForUpdate(resultElements);
                break;

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURERS_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                resultElements = ResultFetcher.fetchResultElements(data);
                switch(this.viewModel.propertyTypeToManage)
                {
                    case CREDIT_TYPE:
                        for(IElement element : resultElements)
                        {
                            ((Attraction)element).setCreditType((CreditType) this.viewModel.longClickedElement);
                            super.markForUpdate(element);
                        }
                        break;

                    case CATEGORY:
                        for(IElement element : resultElements)
                        {
                            ((Attraction)element).setCategory((Category)this.viewModel.longClickedElement);
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
                Toaster.makeShortToast(this, getString(R.string.information_assigned_to_attractions, this.viewModel.longClickedElement.getName(), resultElements.size()));
                Log.d(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onActivityResult<ASSIGN_TO_ATTRACTIONS>:: assigned %s to [%d] attractions", this.viewModel.longClickedElement, resultElements.size()));
                updateContentRecyclerView(true);
        }

    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .add(OptionsItem.SORT_CREDIT_TYPES)
                .add(OptionsItem.SORT_CATEGORIES)
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
                .setVisible(OptionsItem.SORT_CREDIT_TYPES, false)
                .setVisible(OptionsItem.SORT_CATEGORIES, false)
                .setVisible(OptionsItem.SORT_MANUFACTURERS, false)
                .setVisible(OptionsItem.SORT_STATUSES, false);

        switch(this.viewModel.propertyTypeToManage)
        {
            case CREDIT_TYPE:
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_CREDIT_TYPES, App.content.getContentOfType(CreditType.class).size() > 1)
                        .setVisible(OptionsItem.SORT_CREDIT_TYPES, true);
                break;

            case CATEGORY:
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_CATEGORIES, App.content.getContentOfType(Category.class).size() > 1)
                        .setVisible(OptionsItem.SORT_CATEGORIES, true);
                break;

            case MANUFACTURER:
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_MANUFACTURERS, App.content.getContentOfType(Manufacturer.class).size() > 1)
                        .setVisible(OptionsItem.SORT_MANUFACTURERS, true);
                break;

            case STATUS:
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_STATUSES, App.content.getContentOfType(Status.class).size() > 1)
                        .setVisible(OptionsItem.SORT_STATUSES, true);
                break;
        }

        boolean propertyIsAssigned = this.propertyIsAssigned();
        return this.viewModel.optionsMenuAgent
                .setVisible(OptionsItem.EXPAND_ALL, propertyIsAssigned && !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .setVisible(OptionsItem.COLLAPSE_ALL, propertyIsAssigned && this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .prepare(menu);
    }

    private boolean propertyIsAssigned()
    {
        Class<? extends IProperty> type;

        switch(this.viewModel.propertyTypeToManage)
        {
            case CREDIT_TYPE:
                type = CreditType.class;
                break;

            case CATEGORY:
                type = Category.class;
                break;

            case MANUFACTURER:
                type = Manufacturer.class;
                break;

            case STATUS:
                type = Status.class;
                break;

            default:
                String message = String.format("Could not assign property - unexpected type [%s]", this.viewModel.propertyTypeToManage);
                Log.e(Constants.LOG_TAG, "ManagePropertiesActivity.propertyIsAssigned:: " + message);
                throw new IllegalStateException(message);
        }

        for(IProperty property : App.content.getContentAsType(type))
        {
            if(property.hasChildren())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        switch(item)
        {
            case SORT_CREDIT_TYPES:
                ActivityDistributor.startActivitySortForResult(
                        this,
                        RequestCode.SORT_CREDIT_TYPES,
                        App.content.getContentOfType(CreditType.class));
                return true;

            case SORT_CATEGORIES:
                ActivityDistributor.startActivitySortForResult(
                        this,
                        RequestCode.SORT_CATEGORIES,
                        App.content.getContentOfType(Category.class));
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
                invalidateOptionsMenu();
                return true;

            case COLLAPSE_ALL:
                this.viewModel.contentRecyclerViewAdapter.collapseAll();
                invalidateOptionsMenu();
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
            if(this.lastCreatedProperty != null)
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

                if(element.isProperty())
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (IElement)view.getTag();

                if(viewModel.longClickedElement.isProperty())
                {
                    boolean isDefault = false;

                    switch(viewModel.propertyTypeToManage)
                    {
                        case CREDIT_TYPE:
                            isDefault = ((CreditType)viewModel.longClickedElement).isDefault();
                            break;

                        case CATEGORY:
                            isDefault = ((Category)viewModel.longClickedElement).isDefault();
                            break;

                        case MANUFACTURER:
                            isDefault = ((Manufacturer)viewModel.longClickedElement).isDefault();
                            break;

                        case STATUS:
                            isDefault = ((Status)viewModel.longClickedElement).isDefault();
                            break;
                    }

                    Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onLongClick:: %s long clicked", viewModel.longClickedElement));


                    PopupMenuAgent.getMenu()
                            .add(PopupItem.ASSIGN_TO_ATTRACTIONS)
                            .add(PopupItem.EDIT_ELEMENT)
                            .add(PopupItem.DELETE_ELEMENT)
                            .add(PopupItem.SET_AS_DEFAULT)
                            .setEnabled(PopupItem.ASSIGN_TO_ATTRACTIONS, !App.content.getContentAsType(IAttraction.class).isEmpty())
                            .setEnabled(PopupItem.DELETE_ELEMENT, !isDefault)
                            .setEnabled(PopupItem.SET_AS_DEFAULT, !isDefault)
                            .setVisible(PopupItem.SET_AS_DEFAULT, !viewModel.propertyTypeToManage.equals(PropertyType.CREDIT_TYPE)) // no option to change CreditTypes' default value --> default is always "no credit"
                            .show(ManagePropertiesActivity.this, view);
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
                switch(viewModel.propertyTypeToManage)
                {
                    case CREDIT_TYPE:
                    {
                        List<IElement> categorizedElements = App.content.getContentOfType(IHasCreditTypeProperty.class);
                        List<IAttraction> categorizedAttractions = new LinkedList<>(ConvertTool.convertElementsToType(categorizedElements, IAttraction.class));

                        for(IAttraction attraction : categorizedAttractions)
                        {
                            if(attraction.getCreditType().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                categorizedElements.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(
                                ManagePropertiesActivity.this,
                                RequestCode.ASSIGN_CATEGORY_TO_ATTRACTIONS,
                                categorizedElements);
                        break;
                    }

                    case CATEGORY:
                    {
                        List<IElement> categorizedElements = App.content.getContentOfType(IHasCategoryProperty.class);
                        List<IAttraction> categorizedAttractions = new LinkedList<>(ConvertTool.convertElementsToType(categorizedElements, IAttraction.class));

                        for(IAttraction attraction : categorizedAttractions)
                        {
                            if(attraction.getCategory().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                categorizedElements.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(
                                ManagePropertiesActivity.this,
                                RequestCode.ASSIGN_CATEGORY_TO_ATTRACTIONS,
                                categorizedElements);
                        break;
                    }

                    case MANUFACTURER:
                    {
                        List<IElement> categorizedElements = App.content.getContentOfType(IHasManufacturerProperty.class);
                        List<IAttraction> categorizedAttractions = new LinkedList<>(ConvertTool.convertElementsToType(categorizedElements, IAttraction.class));
                        for(IAttraction attraction : categorizedAttractions)
                        {
                            if(attraction.getManufacturer().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                categorizedElements.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(
                                ManagePropertiesActivity.this,
                                RequestCode.ASSIGN_MANUFACTURERS_TO_ATTRACTIONS,
                                categorizedElements);
                        break;
                    }

                    case STATUS:
                    {
                        List<IElement> categorizedElements = App.content.getContentOfType(IHasStatusProperty.class);
                        List<IAttraction> categorizedAttractions = new LinkedList<>(ConvertTool.convertElementsToType(categorizedElements, IAttraction.class));

                        for(IAttraction attraction : categorizedAttractions)
                        {
                            if(attraction.getStatus().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned", attraction, viewModel.longClickedElement));
                                categorizedElements.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(
                                ManagePropertiesActivity.this,
                                RequestCode.ASSIGN_STATUS_TO_ATTRACTIONS,
                                categorizedElements);
                        break;
                    }
                }
                break;
            }

            case EDIT_ELEMENT:
            {
                switch(viewModel.propertyTypeToManage)
                {
                    case CREDIT_TYPE:
                        ActivityDistributor.startActivityEditForResult(ManagePropertiesActivity.this, RequestCode.EDIT_CREDIT_TYPE, viewModel.longClickedElement);
                        break;

                    case CATEGORY:
                        ActivityDistributor.startActivityEditForResult(ManagePropertiesActivity.this, RequestCode.EDIT_CATEGORY, viewModel.longClickedElement);
                        break;

                    case MANUFACTURER:
                        ActivityDistributor.startActivityEditForResult(ManagePropertiesActivity.this, RequestCode.EDIT_MANUFACTURER, viewModel.longClickedElement);
                        break;

                    case STATUS:
                        ActivityDistributor.startActivityEditForResult(ManagePropertiesActivity.this, RequestCode.EDIT_STATUS, viewModel.longClickedElement);
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

                    switch(viewModel.propertyTypeToManage)
                    {
                        case CREDIT_TYPE:
                            defaultName = CreditType.getDefault().getName();
                            break;

                        case CATEGORY:
                            defaultName = Category.getDefault().getName();
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

                    alertDialogMessage = getString(R.string.alert_dialog_message_confirm_delete_property_has_children,
                            viewModel.longClickedElement.getChildCount(),
                            viewModel.longClickedElement.getName(),
                            defaultName);
                }
                else
                {
                    alertDialogMessage = getString(R.string.alert_dialog_message_confirm_delete_property_has_no_children,
                            viewModel.longClickedElement.getName());
                }

                AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                        R.drawable.ic_baseline_warning,
                        getString(R.string.alert_dialog_title_delete),
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
                String alterDialogMessage = getString(R.string.alert_dialog_message_confirm_set_as_default, viewModel.longClickedElement.getName());

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
                    switch(this.viewModel.propertyTypeToManage)
                    {
                        case CREDIT_TYPE:
                            // no option to change CreditTypes' default value --> default is always "no credit"
                            break;

                        case CATEGORY:
                            super.markForUpdate(Category.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            Category.setDefault((Category) this.viewModel.longClickedElement);
                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                            break;

                        case MANUFACTURER:
                            super.markForUpdate(Manufacturer.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            Manufacturer.setDefault((Manufacturer) this.viewModel.longClickedElement);
                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                            break;

                        case STATUS:
                            super.markForUpdate(Status.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            Status.setDefault((Status) this.viewModel.longClickedElement);
                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                            break;
                    }
                    Log.d(Constants.LOG_TAG, String.format("ManagePropertiesActivity.handleAlertDialogClick[%s]:: setting %s as default [%s]",
                            requestCode, this.viewModel.longClickedElement, this.viewModel.propertyTypeToManage));
                    break;
                }
            }
            this.updateContentRecyclerView(false);
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.handleActionConfirmed:: deleting %s...", this.viewModel.longClickedElement));

            if(this.viewModel.longClickedElement.hasChildren())
            {
                List<IAttraction> children = new ArrayList<>(this.viewModel.longClickedElement.fetchChildrenAsType(IAttraction.class));

                for(IAttraction child : children)
                {
                    switch(this.viewModel.propertyTypeToManage)
                    {
                        case CREDIT_TYPE:
                            child.setCreditType(CreditType.getDefault());
                            break;

                        case CATEGORY:
                            child.setCategory(Category.getDefault());
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
                Log.i(Constants.LOG_TAG, "ManagePropertiesActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                switch(viewModel.propertyTypeToManage)
                {
                    case CREDIT_TYPE:
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_CREDIT_TYPE, null);
                        break;

                    case CATEGORY:
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_CATEGORY, null);
                        break;

                    case MANUFACTURER:
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_MANUFACTURER, null);
                        break;

                    case STATUS:
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_STATUS, null);
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
            Log.d(Constants.LOG_TAG, "ManagePropertiesActivity.updateContentRecyclerView:: resetting content...");

            switch(this.viewModel.propertyTypeToManage)
            {
                case CREDIT_TYPE:
                    this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(CreditType.class));
                    break;

                case CATEGORY:
                    this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(Category.class));
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
            Log.d(Constants.LOG_TAG, "ManagePropertiesActivity.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }

        return this.viewModel.contentRecyclerViewAdapter;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.returnResult:: returning new %s", this.lastCreatedProperty));
            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.lastCreatedProperty.getUuid().toString());
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
