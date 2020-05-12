package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.GroupButler;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;

@SuppressWarnings("FieldCanBeLocal")
public class OptionsMenuButler
{
    private BaseActivity master;
    private OptionsMenuAgent optionsMenuAgent;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private RequestCode requestCode;
    private List<IElement> elements;

    private boolean isMenuCreated;

    // find boolean spam at the end of the class

    public OptionsMenuButler(
            OptionsMenuAgent optionsMenuAgent,
            BaseActivity master)
    {
        this.optionsMenuAgent = optionsMenuAgent;
        this.master = master;
    }

    public void initialize(ContentRecyclerViewAdapter contentRecyclerViewAdapter, RequestCode requestCode, List<IElement> elements)
    {
        this.contentRecyclerViewAdapter = contentRecyclerViewAdapter;
        this.requestCode = requestCode;
        this.elements = elements;
    }

    public OptionsMenuButler setContentRecyclerViewAdapter(ContentRecyclerViewAdapter contentRecyclerViewAdapter)
    {
        this.contentRecyclerViewAdapter = contentRecyclerViewAdapter;
        return this;
    }

    public OptionsMenuButler setRequestCode(RequestCode requestCode)
    {
        this.requestCode = requestCode;
        return this;
    }

    public OptionsMenuButler setElements(List<IElement> elements)
    {
        master.invalidateOptionsMenu();
        this.elements = elements;
        return this;
    }

    private boolean isInitialized()
    {
        return this.contentRecyclerViewAdapter != null && this.requestCode != null && this.elements != null;
    }

    public Menu createOptionsMenu(Menu menu)
    {
        if(this.isMenuCreated)
        {
            Log.e(Constants.LOG_TAG, "OptionsMenuButler.serveMenu:: returning already created menu");
            return menu;
        }

        if(this.isInitialized())
        {
            if(this.addSort())
            {
                this.optionsMenuAgent.add(OptionsItem.SORT);
            }

            if(this.addSortAscendingAndDescending())
            {
                this.optionsMenuAgent
                        .addToGroup(OptionsItem.SORT_ASCENDING, OptionsItem.SORT)
                        .addToGroup(OptionsItem.SORT_DESCENDING, OptionsItem.SORT);
            }

            if(this.addSortBy())
            {
                this.optionsMenuAgent.add(OptionsItem.SORT_BY);
            }

            if(this.addSortByName())
            {
                this.optionsMenuAgent
                        .addToGroup(OptionsItem.SORT_BY_NAME, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_NAME_ASCENDING, OptionsItem.SORT_BY_NAME)
                        .addToGroup(OptionsItem.SORT_BY_NAME_DESCENDING, OptionsItem.SORT_BY_NAME);
            }

            if(this.addSortByPark())
            {
                this.optionsMenuAgent
                        .addToGroup(OptionsItem.SORT_BY_PARK, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_PARK_ASCENDING, OptionsItem.SORT_BY_PARK)
                        .addToGroup(OptionsItem.SORT_BY_PARK_DESCENDING, OptionsItem.SORT_BY_PARK);
            }

            if(this.addSortByCreditType())
            {
                this.optionsMenuAgent
                        .addToGroup(OptionsItem.SORT_BY_CREDIT_TYPE, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_CREDIT_TYPE_ASCENDING, OptionsItem.SORT_BY_CREDIT_TYPE)
                        .addToGroup(OptionsItem.SORT_BY_CREDIT_TYPE_DESCENDING, OptionsItem.SORT_BY_CREDIT_TYPE);
            }

            if(this.addSortByCategory())
            {
                this.optionsMenuAgent
                        .addToGroup(OptionsItem.SORT_BY_CATEGORY, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_CATEGORY_ASCENDING, OptionsItem.SORT_BY_CATEGORY)
                        .addToGroup(OptionsItem.SORT_BY_CATEGORY_DESCENDING, OptionsItem.SORT_BY_CATEGORY);
            }

            if(this.addSortByManufacturer())
            {
                this.optionsMenuAgent
                        .addToGroup(OptionsItem.SORT_BY_MANUFACTURER, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, OptionsItem.SORT_BY_MANUFACTURER)
                        .addToGroup(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, OptionsItem.SORT_BY_MANUFACTURER);
            }

            if(this.addSortByModel())
            {
                this.optionsMenuAgent
                        .addToGroup(OptionsItem.SORT_BY_MODEL, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_MODEL_ASCENDING, OptionsItem.SORT_BY_MODEL)
                        .addToGroup(OptionsItem.SORT_BY_MODEL_DESCENDING, OptionsItem.SORT_BY_MODEL);
            }

            if(this.addSortByStatus())
            {
                this.optionsMenuAgent
                        .addToGroup(OptionsItem.SORT_BY_STATUS, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_STATUS_ASCENDING, OptionsItem.SORT_BY_STATUS)
                        .addToGroup(OptionsItem.SORT_BY_STATUS_DESCENDING, OptionsItem.SORT_BY_STATUS);
            }

            if(this.addGroupBy())
            {
                this.optionsMenuAgent.add(OptionsItem.GROUP_BY);
            }

            if(this.addGroupByNone())
            {
                this.optionsMenuAgent.addToGroup(OptionsItem.GROUP_BY_NONE, OptionsItem.GROUP_BY);
            }

            if(this.addGroupByPark())
            {
                this.optionsMenuAgent.addToGroup(OptionsItem.GROUP_BY_PARK, OptionsItem.GROUP_BY);
            }

            if(this.addGroupByCreditType())
            {
                this.optionsMenuAgent.addToGroup(OptionsItem.GROUP_BY_CREDIT_TYPE, OptionsItem.GROUP_BY);
            }

            if(this.addGroupByCategory())
            {
                this.optionsMenuAgent.addToGroup(OptionsItem.GROUP_BY_CATEGORY, OptionsItem.GROUP_BY);
            }

            if(this.addGroupByManufacturer())
            {
                this.optionsMenuAgent.addToGroup(OptionsItem.GROUP_BY_MANUFACTURER, OptionsItem.GROUP_BY);
            }

            if(this.addGroupByModel())
            {
                this.optionsMenuAgent.addToGroup(OptionsItem.GROUP_BY_MODEL, OptionsItem.GROUP_BY);
            }

            if(this.addGroupByStatus())
            {
                this.optionsMenuAgent.addToGroup(OptionsItem.GROUP_BY_STATUS, OptionsItem.GROUP_BY);
            }

            if(this.addExpandAndCollapseAll())
            {
                this.optionsMenuAgent
                        .add(OptionsItem.EXPAND_ALL)
                        .add(OptionsItem.COLLAPSE_ALL);
            }
        }

        this.isMenuCreated = true;

        return this.optionsMenuAgent
                .add(OptionsItem.HELP)
                .create(menu);
    }

    public Menu prepareOptionsMenu(Menu menu)
    {
        if(!this.isInitialized())
        {
            Log.e(Constants.LOG_TAG, "OptionsMenuButler.prepareOptionsMenu:: Butler is not initialized");
            return menu;
        }

        switch(this.requestCode)
        {
            case PICK_ATTRACTIONS:
            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:
            {
                sortVisible = true;
                sortEnabled = this.elements.size() > 1;
                sortAscendingVisible = true;
                sortDescendingVisible = true;

                expandAndCollapseAllVisible = this.elements.size() > 1;
                break;
            }

            case PICK_MODEL:
            case MANAGE_MODELS:
            {
                sortByVisible = true;
                sortByEnabled = this.elements.size() > 1;
                sortByNameVisible = true;
                sortByCreditTypeVisible = true;
                sortByCategoryVisible = true;
                sortByManufacturerVisible = true;

                groupByVisible = true;
                groupByNoneVisible = true;
                groupByCreditTypeVisible = true;
                groupByCategoryVisible = true;
                groupByManufacturerVisible = true;

                expandAndCollapseAllVisible = this.contentRecyclerViewAdapter.getGroupType() != GroupType.NONE && this.elements.size() > 1;
                break;
            }

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
            {
                sortByVisible = true;
                sortByEnabled = this.elements.size() > 1;
                sortByNameVisible = true;
                sortByParkVisible = true;
                sortByCreditTypeVisible = true;
                sortByCategoryVisible = true;
                sortByManufacturerVisible = true;
                sortByModelVisible = true;
                sortByStatusVisible = true;

                groupByVisible = this.elements.size() > 1;
                groupByNoneVisible = true;
                groupByParkVisible = true;
                groupByCreditTypeVisible = true;
                groupByCategoryVisible = true;
                groupByManufacturerVisible = true;
                groupByModelVisible = true;
                groupByStatusVisible = true;

                expandAndCollapseAllVisible = this.anyElementHasChildren();
                break;
            }

            case MANAGE_CREDIT_TYPES:
            case MANAGE_CATEGORIES:
            case MANAGE_MANUFACTURERS:
            case MANAGE_STATUSES:
            {
                sortVisible = true;
                sortEnabled = App.preferences.defaultPropertiesAlwaysAtTop() ? this.elements.size() > 2 : this.elements.size() > 1;

                expandAndCollapseAllVisible = this.anyElementHasChildren();
                break;
            }
        }

        return this.optionsMenuAgent

                .setVisible(OptionsItem.SORT, sortVisible)
                .setEnabled(OptionsItem.SORT, sortEnabled)
                .setVisible(OptionsItem.SORT_ASCENDING, sortAscendingVisible)
                .setEnabled(OptionsItem.SORT_ASCENDING, sortAscendingEnabled)
                .setVisible(OptionsItem.SORT_DESCENDING, sortDescendingVisible)
                .setEnabled(OptionsItem.SORT_DESCENDING, sortDescendingEnabled)


                .setVisible(OptionsItem.SORT_BY, sortByVisible && (sortByNameVisible || sortByParkVisible || sortByCreditTypeVisible || sortByCategoryVisible
                        || sortByManufacturerVisible || sortByModelVisible || sortByStatusVisible))
                .setEnabled(OptionsItem.SORT_BY, sortByEnabled && (sortByNameEnabled || sortByParkEnabled || sortByCreditTypeEnabled || sortByCategoryEnabled
                        || sortByManufacturerEnabled || sortByModelEnabled || sortByStatusEnabled))

                .setVisible(OptionsItem.SORT_BY_NAME, sortByNameVisible)
                .setEnabled(OptionsItem.SORT_BY_NAME, sortByNameEnabled)
                .setVisible(OptionsItem.SORT_BY_NAME_ASCENDING, sortByNameAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_NAME_ASCENDING, sortByNameAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_NAME_DESCENDING, sortByNameDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_NAME_DESCENDING, sortByNameDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_PARK, sortByParkVisible)
                .setEnabled(OptionsItem.SORT_BY_PARK, sortByParkEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.PARK)
                .setVisible(OptionsItem.SORT_BY_PARK_ASCENDING, sortByParkAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_PARK_ASCENDING, sortByParkAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_PARK_DESCENDING, sortByParkDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_PARK_DESCENDING, sortByParkDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_CREDIT_TYPE, sortByCreditTypeVisible)
                .setEnabled(OptionsItem.SORT_BY_CREDIT_TYPE, sortByCreditTypeEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.CREDIT_TYPE)
                .setVisible(OptionsItem.SORT_BY_CREDIT_TYPE_ASCENDING, sortByCreditTypeAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_CREDIT_TYPE_ASCENDING, sortByCreditTypeAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_CREDIT_TYPE_DESCENDING, sortByCreditTypeDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_CREDIT_TYPE_DESCENDING, sortByCreditTypeDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_CATEGORY, sortByCategoryVisible)
                .setEnabled(OptionsItem.SORT_BY_CATEGORY, sortByCategoryEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.CATEGORY)
                .setVisible(OptionsItem.SORT_BY_CATEGORY_ASCENDING, sortByCategoryAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_CATEGORY_ASCENDING, sortByCategoryAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_CATEGORY_DESCENDING, sortByCategoryDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_CATEGORY_DESCENDING, sortByCategoryDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_MANUFACTURER, sortByManufacturerVisible)
                .setEnabled(OptionsItem.SORT_BY_MANUFACTURER, sortByManufacturerEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.MANUFACTURER)
                .setVisible(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, sortByManufacturerAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, sortByManufacturerAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, sortByManufacturerDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, sortByManufacturerDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_MODEL, sortByModelVisible)
                .setEnabled(OptionsItem.SORT_BY_MODEL, sortByModelEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.MODEL)
                .setVisible(OptionsItem.SORT_BY_MODEL_ASCENDING, sortByModelAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_MODEL_ASCENDING, sortByModelAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_MODEL_DESCENDING, sortByModelDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_MODEL_DESCENDING, sortByModelDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_STATUS, sortByStatusVisible)
                .setEnabled(OptionsItem.SORT_BY_STATUS, sortByStatusEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.STATUS)
                .setVisible(OptionsItem.SORT_BY_STATUS_ASCENDING, sortByStatusAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_STATUS_ASCENDING, sortByStatusAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_STATUS_DESCENDING, sortByStatusDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_STATUS_DESCENDING, sortByStatusDescendingEnabled)


                .setVisible(OptionsItem.GROUP_BY, groupByVisible&& (groupByNoneVisible || groupByParkVisible || groupByCreditTypeVisible || groupByCategoryVisible
                        || groupByManufacturerVisible || groupByModelVisible || groupByStatusVisible))
                .setEnabled(OptionsItem.GROUP_BY, groupByEnabled && (groupByNoneEnabled || groupByParkEnabled || groupByCreditTypeEnabled || groupByCategoryEnabled
                        || groupByManufacturerEnabled || groupByModelEnabled || groupByStatusEnabled))

                .setVisible(OptionsItem.GROUP_BY_NONE, groupByNoneVisible)
                .setEnabled(OptionsItem.GROUP_BY_NONE, groupByNoneEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.NONE)

                .setVisible(OptionsItem.GROUP_BY_PARK, groupByParkVisible)
                .setEnabled(OptionsItem.GROUP_BY_PARK, groupByParkEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.PARK)

                .setVisible(OptionsItem.GROUP_BY_CREDIT_TYPE, groupByCreditTypeVisible)
                .setEnabled(OptionsItem.GROUP_BY_CREDIT_TYPE, groupByCreditTypeEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.CREDIT_TYPE)

                .setVisible(OptionsItem.GROUP_BY_CATEGORY, groupByCategoryVisible)
                .setEnabled(OptionsItem.GROUP_BY_CATEGORY, groupByCategoryEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.CATEGORY)

                .setVisible(OptionsItem.GROUP_BY_MANUFACTURER, groupByManufacturerVisible)
                .setEnabled(OptionsItem.GROUP_BY_MANUFACTURER, groupByManufacturerEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.MANUFACTURER)

                .setVisible(OptionsItem.GROUP_BY_MODEL, groupByModelVisible)
                .setEnabled(OptionsItem.GROUP_BY_MODEL, groupByModelEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.MODEL)

                .setVisible(OptionsItem.GROUP_BY_STATUS, groupByStatusVisible)
                .setEnabled(OptionsItem.GROUP_BY_STATUS, groupByStatusEnabled && this.contentRecyclerViewAdapter.getGroupType() != GroupType.STATUS)


                .setVisible(OptionsItem.EXPAND_ALL, expandAndCollapseAllVisible && !this.contentRecyclerViewAdapter.isAllExpanded())
                .setVisible(OptionsItem.COLLAPSE_ALL, expandAndCollapseAllVisible && this.contentRecyclerViewAdapter.isAllExpanded())

                .prepare(menu);
    }

    private boolean anyElementHasChildren()
    {
        for(IElement element : this.elements)
        {
            if(element.hasChildren())
            {
                return true;
            }
        }

        return false;
    }

    public boolean handleMenuItemSelected(MenuItem item)
    {
        OptionsItem optionsItem = this.optionsMenuAgent.getOptionsItem(item);
        Log.i(Constants.LOG_TAG, String.format("OptionsMenuButler.handleMenuItemSelected:: OptionsItem [#%d - %s] selected", optionsItem.ordinal(), optionsItem));

        if(optionsItem == OptionsItem.HELP)
        {
            this.master.showHelpOverlayFragment();
            return true;
        }
        else if(!this.isInitialized())
        {
            Log.e(Constants.LOG_TAG, "OptionsMenuButler.handleOptionsItemSelected:: Butler is not initialized");
            return false;
        }

        switch(optionsItem)
        {
            case NO_FUNCTION:

                Log.v(Constants.LOG_TAG, String.format("OptionsMenuButler.handleOptionsItemSelected:: Item [%s] has no function", optionsItem));
                return true;

            case SORT_BY:
            case GROUP_BY:
            case SORT_BY_NAME:
            case SORT_BY_PARK:
            case SORT_BY_CREDIT_TYPE:
            case SORT_BY_CATEGORY:
            case SORT_BY_MANUFACTURER:
            case SORT_BY_MODEL:
            case SORT_BY_STATUS:
                return true;

            case SORT_ASCENDING:
            case SORT_BY_NAME_ASCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_NAME, SortOrder.ASCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_DESCENDING:
            case SORT_BY_NAME_DESCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_NAME, SortOrder.DESCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_PARK_ASCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_PARK, SortOrder.ASCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_PARK_DESCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_PARK, SortOrder.DESCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_CREDIT_TYPE_ASCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_CREDIT_TYPE, SortOrder.ASCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_CREDIT_TYPE_DESCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_CREDIT_TYPE, SortOrder.DESCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_CATEGORY_ASCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_CATEGORY, SortOrder.ASCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_CATEGORY_DESCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_CATEGORY, SortOrder.DESCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_MANUFACTURER_ASCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_MANUFACTURER, SortOrder.ASCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_MANUFACTURER_DESCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_MANUFACTURER, SortOrder.DESCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_MODEL_ASCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_MODEL, SortOrder.ASCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_MODEL_DESCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_MODEL, SortOrder.DESCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_STATUS_ASCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_STATUS, SortOrder.ASCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case SORT_BY_STATUS_DESCENDING:
                this.elements = SortTool.sortElements(this.elements, SortType.BY_STATUS, SortOrder.DESCENDING);
                this.contentRecyclerViewAdapter.setItems(this.elements);
                return true;

            case GROUP_BY_NONE:
                GroupButler.groupElementsAndSetDetailModes(this.contentRecyclerViewAdapter, this.requestCode, GroupType.NONE);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_PARK:
                GroupButler.groupElementsAndSetDetailModes(this.contentRecyclerViewAdapter, this.requestCode, GroupType.PARK);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_CREDIT_TYPE:
                GroupButler.groupElementsAndSetDetailModes(this.contentRecyclerViewAdapter, this.requestCode, GroupType.CREDIT_TYPE);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_CATEGORY:
                GroupButler.groupElementsAndSetDetailModes(this.contentRecyclerViewAdapter, this.requestCode, GroupType.CATEGORY);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_MANUFACTURER:
                GroupButler.groupElementsAndSetDetailModes(this.contentRecyclerViewAdapter, this.requestCode, GroupType.MANUFACTURER);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_MODEL:
                GroupButler.groupElementsAndSetDetailModes(this.contentRecyclerViewAdapter, this.requestCode, GroupType.MODEL);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_STATUS:
                GroupButler.groupElementsAndSetDetailModes(this.contentRecyclerViewAdapter, this.requestCode, GroupType.STATUS);
                this.master.invalidateOptionsMenu();
                return true;

            case EXPAND_ALL:
                this.contentRecyclerViewAdapter.expandAll();
                master.invalidateOptionsMenu();
                return true;

            case COLLAPSE_ALL:
                this.contentRecyclerViewAdapter.collapseAll();
                master.invalidateOptionsMenu();
                return true;

            default:
                return false;
        }
    }


    private boolean addSort()
    {
        switch(this.requestCode)
        {
            case PICK_ATTRACTIONS:
            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:

            case MANAGE_CREDIT_TYPES:
            case MANAGE_CATEGORIES:
            case MANAGE_MANUFACTURERS:
            case MANAGE_STATUSES:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortAscendingAndDescending()
    {
        switch(this.requestCode)
        {
            case PICK_ATTRACTIONS:
            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortBy()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByName()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByPark()
    {
        switch(this.requestCode)
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByCreditType()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByCategory()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByManufacturer()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByModel()
    {
        switch(this.requestCode)
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByStatus()
    {
        switch(this.requestCode)
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupBy()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByNone()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByPark()
    {
        switch(this.requestCode)
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByCreditType()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByCategory()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByManufacturer()
    {
        switch(this.requestCode)
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByModel()
    {
        switch(this.requestCode)
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByStatus()
    {
        switch(this.requestCode)
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addExpandAndCollapseAll()
    {
        switch(this.requestCode)
        {
            case PICK_ATTRACTIONS:
            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:

            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case MANAGE_CREDIT_TYPES:
            case MANAGE_CATEGORIES:
            case MANAGE_MANUFACTURERS:
            case MANAGE_STATUSES:
                return true;

            default:
                return false;
        }
    }

    private boolean sortVisible = false;
    private boolean sortEnabled = true;
    private boolean sortAscendingVisible = false;
    private boolean sortAscendingEnabled = true;
    private boolean sortDescendingVisible = false;
    private boolean sortDescendingEnabled = true;


    private boolean sortByVisible = false;
    private boolean sortByEnabled = true;

    private boolean sortByNameVisible = false;
    private boolean sortByNameEnabled = true;
    private boolean sortByNameAscendingVisible = true;
    private boolean sortByNameAscendingEnabled = true;
    private boolean sortByNameDescendingVisible = true;
    private boolean sortByNameDescendingEnabled = true;

    private boolean sortByParkVisible = false;
    private boolean sortByParkEnabled = true;
    private boolean sortByParkAscendingVisible = true;
    private boolean sortByParkAscendingEnabled = true;
    private boolean sortByParkDescendingVisible = true;
    private boolean sortByParkDescendingEnabled = true;

    private boolean sortByCreditTypeVisible = false;
    private boolean sortByCreditTypeEnabled = true;
    private boolean sortByCreditTypeAscendingVisible = true;
    private boolean sortByCreditTypeAscendingEnabled = true;
    private boolean sortByCreditTypeDescendingVisible = true;
    private boolean sortByCreditTypeDescendingEnabled = true;

    private boolean sortByCategoryVisible = false;
    private boolean sortByCategoryEnabled = true;
    private boolean sortByCategoryAscendingVisible = true;
    private boolean sortByCategoryAscendingEnabled = true;
    private boolean sortByCategoryDescendingVisible = true;
    private boolean sortByCategoryDescendingEnabled = true;

    private boolean sortByManufacturerVisible = false;
    private boolean sortByManufacturerEnabled = true;
    private boolean sortByManufacturerAscendingVisible = true;
    private boolean sortByManufacturerAscendingEnabled = true;
    private boolean sortByManufacturerDescendingVisible = true;
    private boolean sortByManufacturerDescendingEnabled = true;

    private boolean sortByModelVisible = false;
    private boolean sortByModelEnabled = true;
    private boolean sortByModelAscendingVisible = true;
    private boolean sortByModelAscendingEnabled = true;
    private boolean sortByModelDescendingVisible = true;
    private boolean sortByModelDescendingEnabled = true;

    private boolean sortByStatusVisible = false;
    private boolean sortByStatusEnabled = true;
    private boolean sortByStatusAscendingVisible = true;
    private boolean sortByStatusAscendingEnabled = true;
    private boolean sortByStatusDescendingVisible = true;
    private boolean sortByStatusDescendingEnabled = true;


    private boolean groupByVisible = false;
    private boolean groupByEnabled = true;

    private boolean groupByNoneVisible = false;
    private boolean groupByNoneEnabled = true;

    private boolean groupByParkVisible = false;
    private boolean groupByParkEnabled = true;

    private boolean groupByCreditTypeVisible = false;
    private boolean groupByCreditTypeEnabled = true;

    private boolean groupByCategoryVisible = false;
    private boolean groupByCategoryEnabled = true;

    private boolean groupByManufacturerVisible = false;
    private boolean groupByManufacturerEnabled = true;

    private boolean groupByModelVisible = false;
    private boolean groupByModelEnabled = true;

    private boolean groupByStatusVisible = false;
    private boolean groupByStatusEnabled = true;

    private boolean expandAndCollapseAllVisible = false;
}