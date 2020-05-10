package de.juliusawen.coastercreditcounter.userInterface.activities;

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
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
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
    private ManagePropertiesActivityViewModel viewModel;

    protected void setContentView()
    {
        setContentView(R.layout.activity_manage_properties);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ManagePropertiesActivityViewModel.class);

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        RequestCode requestCode = RequestCode.getValue(getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0));
        if(requestCode == RequestCode.PICK_CREDIT_TYPE
                || requestCode == RequestCode.PICK_CATEGORY
                || requestCode == RequestCode.PICK_MANUFACTURER
                || requestCode == RequestCode.PICK_MODEL
                || requestCode == RequestCode.PICK_STATUS)
        {
            this.viewModel.isSelectionMode = true;

            if(this.viewModel.propertiesToSelectFrom == null)
            {
                this.viewModel.propertiesToSelectFrom = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
            }
        }

        if(this.viewModel.propertyTypeToManage == null)
        {
            this.viewModel.propertyTypeToManage = PropertyType.values()[getIntent().getIntExtra(Constants.EXTRA_TYPE_TO_MANAGE, -1)];
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            List<IElement> elements;
            if(this.viewModel.isSelectionMode)
            {
                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                        this.viewModel.propertiesToSelectFrom,
                        new HashSet<Class<? extends IElement>>(),
                        false)
                        .setTypefaceForContentType(this.getPropertyType(), Typeface.BOLD);
            }
            else
            {
                elements = App.content.getContentOfType(this.getPropertyType());
                for(IElement element : elements)
                {
                    element.reorderChildren(SortTool.sortElements(element.getChildren(), SortType.BY_NAME, SortOrder.ASCENDING));
                }
                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                        elements,
                        IAttraction.class)
                        .setTypefaceForContentType(this.getPropertyType(), Typeface.BOLD);

                switch(this.viewModel.propertyTypeToManage)
                {
                    case CREDIT_TYPE:
                    case STATUS:
                    {
                        this.viewModel.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW);
                        break;
                    }

                    case CATEGORY:
                    {
                        this.viewModel.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW);
                        break;
                    }

                    case MANUFACTURER:
                    {
                        this.viewModel.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW);
                        break;
                    }
                }
            }

            this.viewModel.contentRecyclerViewAdapter.setSpecialStringResourceForType(IProperty.class, R.string.substitute_properties_default_postfix);

            if(this.viewModel.propertyTypeToManage.equals(PropertyType.MODEL))
            {
                this.viewModel.contentRecyclerViewAdapter
                        .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                        .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                        .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW);
            }
        }

        if(this.viewModel.contentRecyclerViewAdapter != null)
        {
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
            RecyclerView recyclerView = findViewById(R.id.recyclerViewManageProperties);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);
        }

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_HELP_TITLE)), getIntent().getStringExtra(Constants.EXTRA_HELP_TEXT));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode != RESULT_OK)
        {
            return;
        }

        IElement resultElement = ResultFetcher.fetchResultElement(data);
        switch(RequestCode.values()[requestCode])
        {
            case CREATE_CREDIT_TYPE:
            case CREATE_CATEGORY:
            case CREATE_MANUFACTURER:
            case CREATE_MODEL:
            case CREATE_STATUS:
            {
                this.viewModel.propertyToReturn = resultElement;
                updateContentRecyclerView(true);
                break;
            }

            case EDIT_CREDIT_TYPE:
            case EDIT_CATEGORY:
            case EDIT_MANUFACTURER:
            case EDIT_MODEL:
            case EDIT_STATUS:
            {
                updateContentRecyclerView(false);
                break;
            }

            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:
            {
                ArrayList<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                App.content.reorderElements(resultElements);
                updateContentRecyclerView(true).scrollToItem(resultElement);
                super.markForUpdate(resultElements);
                break;
            }

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
            {
                ArrayList<IElement> resultElements = ResultFetcher.fetchResultElements(data);

                for(IElement element : resultElements)
                {
                    switch(this.viewModel.propertyTypeToManage)
                    {
                        case CREDIT_TYPE:
                            ((Attraction) element).setCreditType((CreditType) this.viewModel.longClickedElement);
                            break;

                        case CATEGORY:
                            ((Attraction) element).setCategory((Category) this.viewModel.longClickedElement);
                            break;

                        case MANUFACTURER:
                            ((Attraction) element).setManufacturer((Manufacturer) this.viewModel.longClickedElement);
                            break;

                        case MODEL:
                            ((Attraction) element).setModel((Model) this.viewModel.longClickedElement);
                            break;

                        case STATUS:
                            ((Attraction) element).setStatus((Status) this.viewModel.longClickedElement);
                            break;
                    }
                    super.markForUpdate(element);
                }

                updateContentRecyclerView(false);
                Toaster.makeShortToast(this, getString(R.string.information_assigned_to_attractions, this.viewModel.longClickedElement.getName(), resultElements.size()));
                Log.d(Constants.LOG_TAG,
                        String.format("ManagePropertiesActivity.onActivityResult<ASSIGN_TO_ATTRACTIONS>:: assigned %s to [%d] attractions",
                        this.viewModel.longClickedElement, resultElements.size()));
                updateContentRecyclerView(true);
            }
        }
    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .add(OptionsItem.SORT_CREDIT_TYPES)
                .add(OptionsItem.SORT_CATEGORIES)
                .add(OptionsItem.SORT_MANUFACTURERS)
                .add(OptionsItem.SORT_MODELS)
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
                .setVisible(OptionsItem.SORT_MODELS, false)
                .setVisible(OptionsItem.SORT_STATUSES, false);

        int propertyCount;
        boolean sortEnabled;

        switch(this.viewModel.propertyTypeToManage)
        {
            case CREDIT_TYPE:
                propertyCount = App.content.getContentOfType(CreditType.class).size();
                sortEnabled = App.preferences.defaultPropertiesAlwaysAtTop() ? propertyCount > 2 : propertyCount > 1;
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_CREDIT_TYPES, sortEnabled)
                        .setVisible(OptionsItem.SORT_CREDIT_TYPES, true);
                break;

            case CATEGORY:
                propertyCount = App.content.getContentOfType(Category.class).size();
                sortEnabled = App.preferences.defaultPropertiesAlwaysAtTop() ? propertyCount > 2 : propertyCount > 1;
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_CATEGORIES, sortEnabled)
                        .setVisible(OptionsItem.SORT_CATEGORIES, true);
                break;

            case MANUFACTURER:
                propertyCount = App.content.getContentOfType(Manufacturer.class).size();
                sortEnabled = App.preferences.defaultPropertiesAlwaysAtTop() ? propertyCount > 2 : propertyCount > 1;
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_MANUFACTURERS, sortEnabled)
                        .setVisible(OptionsItem.SORT_MANUFACTURERS, true);
                break;

            case MODEL:
                propertyCount = App.content.getContentOfType(Model.class).size();
                sortEnabled = App.preferences.defaultPropertiesAlwaysAtTop() ? propertyCount > 2 : propertyCount > 1;
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_MODELS, sortEnabled)
                        .setVisible(OptionsItem.SORT_MODELS, true);
                break;

            case STATUS:
                propertyCount = App.content.getContentOfType(Status.class).size();
                sortEnabled = App.preferences.defaultPropertiesAlwaysAtTop() ? propertyCount > 2 : propertyCount > 1;
                this.viewModel.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_STATUSES, sortEnabled)
                        .setVisible(OptionsItem.SORT_STATUSES, true);
                break;
        }

        boolean anyPropertyHasChildren = this.anyPropertyHasChildren();
        return this.viewModel.optionsMenuAgent
                .setVisible(OptionsItem.EXPAND_ALL, !this.viewModel.isSelectionMode && anyPropertyHasChildren && !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .setVisible(OptionsItem.COLLAPSE_ALL, !this.viewModel.isSelectionMode && anyPropertyHasChildren && this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .prepare(menu);
    }

    private boolean anyPropertyHasChildren()
    {
        for(IProperty property : App.content.getContentAsType(this.getPropertyType()))
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
                ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_CREDIT_TYPES, App.content.getContentOfType(CreditType.class));
                return true;

            case SORT_CATEGORIES:
                ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_CATEGORIES, App.content.getContentOfType(Category.class));
                return true;

            case SORT_MANUFACTURERS:
                ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_MANUFACTURERS, App.content.getContentOfType(Manufacturer.class));
                return true;

            case SORT_MODELS:
                ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_MODELS, App.content.getContentOfType(Model.class));
                return true;

            case SORT_STATUSES:
                ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_STATUSES, App.content.getContentOfType(Status.class));
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
            this.returnResult(RESULT_OK);
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

                Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onClick:: %s clicked", element));

                if(viewModel.isSelectionMode && element.isProperty())
                {
                    viewModel.propertyToReturn = element;
                    returnResult(RESULT_OK);
                }
                else if(element.hasChildren())
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                    if(viewModel.contentRecyclerViewAdapter.isAllExpanded() || viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                    {
                        invalidateOptionsMenu();
                    }
                }
                else if(element.isAttraction())
                {
                    ActivityDistributor.startActivityShow(ManagePropertiesActivity.this, RequestCode.SHOW_ATTRACTION, element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (IElement)view.getTag();

                if(viewModel.longClickedElement.isProperty())
                {
                    boolean longClickedPropertyIsDefault = false;

                    switch(viewModel.propertyTypeToManage)
                    {
                        case CREDIT_TYPE:
                            longClickedPropertyIsDefault = ((CreditType) viewModel.longClickedElement).isDefault();
                            break;

                        case CATEGORY:
                            longClickedPropertyIsDefault = ((Category) viewModel.longClickedElement).isDefault();
                            break;

                        case MANUFACTURER:
                            longClickedPropertyIsDefault = ((Manufacturer) viewModel.longClickedElement).isDefault();
                            break;

                        case MODEL:
                            longClickedPropertyIsDefault = ((Model) viewModel.longClickedElement).isDefault();
                            break;

                        case STATUS:
                            longClickedPropertyIsDefault = ((Status) viewModel.longClickedElement).isDefault();
                            break;
                    }

                    Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.onLongClick:: %s long clicked", viewModel.longClickedElement));


                    PopupMenuAgent popupMenuAgent = PopupMenuAgent.getMenu();

                    if(!viewModel.isSelectionMode)
                    {
                        popupMenuAgent
                                .add(PopupItem.ASSIGN_TO_ATTRACTIONS)
                                .setEnabled(PopupItem.ASSIGN_TO_ATTRACTIONS, !App.content.getContentAsType(IAttraction.class).isEmpty());
                    }

                    boolean isSetAsDefaultVisible = !viewModel.propertyTypeToManage.equals(PropertyType.CREDIT_TYPE) && !viewModel.propertyTypeToManage.equals(PropertyType.MODEL);

                    popupMenuAgent
                            .add(PopupItem.EDIT_ELEMENT)
                            .add(PopupItem.DELETE_ELEMENT)
                            .add(PopupItem.SET_AS_DEFAULT)
                            .setEnabled(PopupItem.DELETE_ELEMENT, !longClickedPropertyIsDefault)
                            .setEnabled(PopupItem.SET_AS_DEFAULT, !longClickedPropertyIsDefault)
                            .setVisible(PopupItem.SET_AS_DEFAULT, isSetAsDefaultVisible)
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
                List<IElement> elementsToAssignTo = new ArrayList<>(App.content.getContentOfType(OnSiteAttraction.class));
                List<IAttraction> possibleAttractionsToAssignTo = new LinkedList<>(ConvertTool.convertElementsToType(elementsToAssignTo, IAttraction.class));

                switch(viewModel.propertyTypeToManage)
                {
                    case CREDIT_TYPE:
                    {
                        for(IAttraction attraction : possibleAttractionsToAssignTo)
                        {
                            if(attraction.getCreditType().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG,
                                        String.format("ManagePropertiesActivity.onMenuItemClick<ASSIGN_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned",
                                                attraction, viewModel.longClickedElement));

                                elementsToAssignTo.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(this, RequestCode.ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS, elementsToAssignTo);
                        break;
                    }

                    case CATEGORY:
                    {
                        for(IAttraction attraction : possibleAttractionsToAssignTo)
                        {
                            if(attraction.getCategory().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG,
                                        String.format("ManagePropertiesActivity.onMenuItemClick<ASSIGN_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned",
                                                attraction, viewModel.longClickedElement));

                                elementsToAssignTo.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(this, RequestCode.ASSIGN_CATEGORY_TO_ATTRACTIONS, elementsToAssignTo);
                        break;
                    }

                    case MANUFACTURER:
                    {
                        for(IAttraction attraction : possibleAttractionsToAssignTo)
                        {
                            if(attraction.getManufacturer().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG,
                                        String.format("ManagePropertiesActivity.onMenuItemClick<ASSIGN_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned",
                                                attraction, viewModel.longClickedElement));

                                elementsToAssignTo.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(this, RequestCode.ASSIGN_MANUFACTURER_TO_ATTRACTIONS, elementsToAssignTo);
                        break;
                    }

                    case MODEL:
                    {
                        for(IAttraction attraction : possibleAttractionsToAssignTo)
                        {
                            if(attraction.getModel().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG,
                                        String.format("ManagePropertiesActivity.onMenuItemClick<ASSIGN_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned",
                                                attraction, viewModel.longClickedElement));

                                elementsToAssignTo.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(this, RequestCode.ASSIGN_MODEL_TO_ATTRACTIONS, elementsToAssignTo);
                        break;
                    }

                    case STATUS:
                    {
                        for(IAttraction attraction : possibleAttractionsToAssignTo)
                        {
                            if(attraction.getStatus().equals(viewModel.longClickedElement))
                            {
                                Log.v(Constants.LOG_TAG,
                                        String.format("ManagePropertiesActivity.onMenuItemClick<ASSIGN_TO_ATTRACTIONS>:: removing %s from pick list - %s is already assigned",
                                                attraction, viewModel.longClickedElement));

                                elementsToAssignTo.remove(attraction);
                            }
                        }

                        ActivityDistributor.startActivityPickForResult(this, RequestCode.ASSIGN_STATUS_TO_ATTRACTIONS, elementsToAssignTo);
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
                        ActivityDistributor.startActivityEditForResult(this, RequestCode.EDIT_CREDIT_TYPE, viewModel.longClickedElement);
                        break;

                    case CATEGORY:
                        ActivityDistributor.startActivityEditForResult(this, RequestCode.EDIT_CATEGORY, viewModel.longClickedElement);
                        break;

                    case MANUFACTURER:
                        ActivityDistributor.startActivityEditForResult(this, RequestCode.EDIT_MANUFACTURER, viewModel.longClickedElement);
                        break;

                    case MODEL:
                    {
                        // for Model's default only name may be changed
                        if(((Model) viewModel.longClickedElement).isDefault())
                        {
                            Intent intent = new Intent(this, EditSimpleElementActivity.class);
                            intent.putExtra(Constants.EXTRA_HINT, getString(R.string.hint_edit_name, viewModel.longClickedElement.getName()));
                            intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, getString(R.string.title_edit_model));
                            intent.putExtra(Constants.EXTRA_ELEMENT_UUID, viewModel.longClickedElement.getUuid().toString());
                            intent.putExtra(Constants.EXTRA_REQUEST_CODE, RequestCode.EDIT_MODEL.ordinal());
                            ActivityDistributor.startActivityViaIntent(this, intent);
                        }
                        else
                        {
                            ActivityDistributor.startActivityEditForResult(this, RequestCode.EDIT_MODEL, viewModel.longClickedElement);
                        }
                        break;
                    }

                    case STATUS:
                        ActivityDistributor.startActivityEditForResult(this, RequestCode.EDIT_STATUS, viewModel.longClickedElement);
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

                        case MODEL:
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
                    alertDialogMessage = getString(R.string.alert_dialog_message_confirm_delete_property_has_no_children, viewModel.longClickedElement.getName());
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
                    super.setFloatingActionButtonVisibility(false);

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
                        case MODEL:
                            // no option to change default value
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

        super.setFloatingActionButtonVisibility(true);

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.handleActionConfirmed:: deleting %s...", this.viewModel.longClickedElement));

            if(this.viewModel.longClickedElement.hasChildren())
            {
                List<IAttraction> children = new ArrayList<>(this.viewModel.longClickedElement.getChildrenAsType(IAttraction.class));

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

                        case MODEL:
                            child.setModel(Model.getDefault());

                        case STATUS:
                            child.setStatus(Status.getDefault());
                            break;
                    }

                    markForUpdate(child);
                }
            }

            super.markForDeletion(this.viewModel.longClickedElement);
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
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_CREDIT_TYPE);
                        break;

                    case CATEGORY:
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_CATEGORY);
                        break;

                    case MANUFACTURER:
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_MANUFACTURER);
                        break;

                    case MODEL:
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_MODEL);
                        break;

                    case STATUS:
                        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, RequestCode.CREATE_STATUS);
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

            List<IElement> elements = App.content.getContentOfType(this.getPropertyType());

            for(IElement element : elements)
            {
                element.reorderChildren(SortTool.sortElements(element.getChildren(), SortType.BY_NAME, SortOrder.ASCENDING));
            }

            this.viewModel.contentRecyclerViewAdapter.setItems(elements);
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ManagePropertiesActivity.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }

        return this.viewModel.contentRecyclerViewAdapter;
    }

    private Class<? extends IProperty> getPropertyType()
    {
        switch(this.viewModel.propertyTypeToManage)
        {
            case CREDIT_TYPE:
                return CreditType.class;

            case CATEGORY:
                return Category.class;

            case MANUFACTURER:
                return Manufacturer.class;

            case MODEL:
                return Model.class;

            case STATUS:
                return Status.class;

            default:
                return null;
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            if(this.viewModel.propertyToReturn != null)
            {
                Log.i(Constants.LOG_TAG, String.format("ManagePropertiesActivity.returnResult:: returning %s", this.viewModel.propertyToReturn));
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.propertyToReturn.getUuid().toString());
            }
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}