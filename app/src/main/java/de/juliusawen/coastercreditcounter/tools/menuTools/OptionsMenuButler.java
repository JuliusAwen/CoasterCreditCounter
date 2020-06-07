package de.juliusawen.coastercreditcounter.tools.menuTools;

import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.userInterface.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewDecorationPresetProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;

@SuppressWarnings("FieldCanBeLocal")
public class OptionsMenuButler
{
    private BaseActivity master;
    private OptionsMenuProvider optionsMenuProvider;

    IBaseViewModel viewModel;

    public OptionsMenuButler(BaseActivity master)
    {
        this.optionsMenuProvider = new OptionsMenuProvider();
        this.master = master;
    }

    public void setViewModel(IBaseViewModel viewModel)
    {
        this.viewModel = viewModel;
    }

    public OptionsItem getOptionsItem(MenuItem item)
    {
        return OptionsItem.getValue(item.getItemId());
    }

    public Menu prepareOptionsMenu(Menu menu)
    {
        if(this.getRequestCode() != null)
        {
            switch(this.getRequestCode())
            {
                case PICK_ATTRACTIONS:
                case PICK_CREDIT_TYPE:
                case PICK_CATEGORY:
                case PICK_MANUFACTURER:
                case PICK_STATUS:
                {
                    sortVisible = true;
                    sortEnabled = this.getElements().size() > 1;
                    sortAscendingVisible = true;
                    sortDescendingVisible = true;
                    break;
                }

                case SORT_LOCATIONS:
                case SORT_PARKS:
                case SORT_ATTRACTIONS:
                case SORT_CREDIT_TYPES:
                case SORT_CATEGORIES:
                case SORT_MANUFACTURERS:
                case SORT_MODELS:
                case SORT_STATUSES:
                {
                    sortVisible = true;
                    sortAscendingVisible = true;
                    sortDescendingVisible = true;
                }

                case PICK_MODEL:
                case MANAGE_MODELS:
                {
                    sortByVisible = true;
                    sortByEnabled = this.getElements().size() > 1;
                    sortByNameVisible = true;
                    sortByCreditTypeVisible = true;
                    sortByCategoryVisible = true;
                    sortByManufacturerVisible = true;

                    groupByVisible = true;
                    groupByNoneVisible = true;
                    groupByCreditTypeVisible = true;
                    groupByCategoryVisible = true;
                    groupByManufacturerVisible = true;

                    expandAndCollapseAllVisible = this.getElements().size() > 1 && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.NONE;
                    break;
                }

                case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
                case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
                case ASSIGN_STATUS_TO_ATTRACTIONS:
                {
                    sortByVisible = true;
                    sortByEnabled = this.getElements().size() > 1;
                    sortByNameVisible = true;
                    sortByParkVisible = true;
                    sortByCreditTypeVisible = true;
                    sortByCategoryVisible = true;
                    sortByManufacturerVisible = true;
                    sortByModelVisible = true;
                    sortByStatusVisible = true;

                    groupByVisible = this.getElements().size() > 1;
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
                    sortEnabled = App.preferences.defaultPropertiesAlwaysAtTop() ? this.getElements().size() > 2 : this.getElements().size() > 1;
                    break;
                }

                case SHOW_LOCATIONS:
                {
                    expandAndCollapseAllVisible = this.getElement().hasChildren();
                    break;
                }

                case SHOW_VISIT:
                {
                    boolean isEditingEnabled = ((Visit) this.getElement()).isEditingEnabled();
                    enableEditingVisible = !isEditingEnabled;
                    disableEditingVisible = isEditingEnabled;

                    expandAndCollapseAllVisible = isEditingEnabled && this.getElement().hasChildrenOfType(VisitedAttraction.class);
                    break;
                }

                case SHOW_PARK_OVERVIEW:
                {
                    sortVisible = false;

                    expandAndCollapseAllVisible = false;
                    break;
                }

                case SHOW_ATTRACTIONS:
                {
                    sortVisible = false;

                    expandAndCollapseAllVisible = this.getElement().getChildCountOfType(Attraction.class) > 1;
                    break;
                }

                case SHOW_VISITS:
                {
                    boolean childCountLargerThanOne = this.getElement().getChildCountOfType(Visit.class) > 1;

                    sortVisible = true;
                    sortEnabled = childCountLargerThanOne;
                    sortAscendingVisible = true;
                    sortDescendingVisible = true;

                    expandAndCollapseAllVisible = childCountLargerThanOne;
                    break;
                }

                case SHOW_CATEGORY:
                {
                    sortByVisible = true;
                    sortByParkVisible = true;
                    sortByCreditTypeVisible = true;
                    sortByManufacturerVisible = true;
                    sortByModelVisible = true;
                    sortByStatusVisible = true;
                    break;
                }

                case SHOW_CREDIT_TYPE:
                {
                    sortByVisible = true;
                    sortByParkVisible = true;
                    sortByCategoryVisible = true;
                    sortByManufacturerVisible = true;
                    sortByModelVisible = true;
                    sortByStatusVisible = true;
                    break;
                }

                case SHOW_MANUFACTURER:
                {
                    sortByVisible = true;
                    sortByParkVisible = true;
                    sortByCategoryVisible = true;
                    sortByCreditTypeVisible = true;
                    sortByModelVisible = true;
                    sortByStatusVisible = true;
                    break;
                }

                case SHOW_MODEL:
                {
                    sortByVisible = true;
                    sortByParkVisible = true;
                    sortByStatusVisible = true;
                    break;
                }

                case SHOW_STATUS:
                {
                    sortByVisible = true;
                    sortByParkVisible = true;
                    sortByCategoryVisible = true;
                    sortByCreditTypeVisible = true;
                    sortByManufacturerVisible = true;
                    sortByModelVisible = true;
                    break;
                }

                case NAVIGATE:
                {
                    goToCurrentVisitVisible = !this.getElements().isEmpty();
                    break;
                }
            }
        }

        boolean noAdapter = this.getContentRecyclerViewAdapter() == null;

        this.optionsMenuProvider

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


                .setVisible(OptionsItem.ENABLE_EDITING, enableEditingVisible)
                .setVisible(OptionsItem.DISABLE_EDITING, disableEditingVisible)

                .setVisible(OptionsItem.GO_TO_CURRENT_VISIT, goToCurrentVisitVisible)


                .setVisible(OptionsItem.SORT_BY_PARK, sortByParkVisible)
                .setEnabled(OptionsItem.SORT_BY_PARK, noAdapter
                        ? sortByParkEnabled
                        : sortByParkEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.PARK)
                .setVisible(OptionsItem.SORT_BY_PARK_ASCENDING, sortByParkAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_PARK_ASCENDING, sortByParkAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_PARK_DESCENDING, sortByParkDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_PARK_DESCENDING, sortByParkDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_CREDIT_TYPE, sortByCreditTypeVisible)
                .setEnabled(OptionsItem.SORT_BY_CREDIT_TYPE, noAdapter
                        ? sortByCreditTypeEnabled
                        : sortByCreditTypeEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.CREDIT_TYPE)
                .setVisible(OptionsItem.SORT_BY_CREDIT_TYPE_ASCENDING, sortByCreditTypeAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_CREDIT_TYPE_ASCENDING, sortByCreditTypeAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_CREDIT_TYPE_DESCENDING, sortByCreditTypeDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_CREDIT_TYPE_DESCENDING, sortByCreditTypeDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_CATEGORY, sortByCategoryVisible)
                .setEnabled(OptionsItem.SORT_BY_CATEGORY, noAdapter
                        ? sortByCategoryEnabled
                        : sortByCategoryEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.CATEGORY)
                .setVisible(OptionsItem.SORT_BY_CATEGORY_ASCENDING, sortByCategoryAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_CATEGORY_ASCENDING, sortByCategoryAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_CATEGORY_DESCENDING, sortByCategoryDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_CATEGORY_DESCENDING, sortByCategoryDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_MANUFACTURER, sortByManufacturerVisible)
                .setEnabled(OptionsItem.SORT_BY_MANUFACTURER, noAdapter
                        ? sortByManufacturerEnabled
                        : sortByManufacturerEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.MANUFACTURER)
                .setVisible(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, sortByManufacturerAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, sortByManufacturerAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, sortByManufacturerDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, sortByManufacturerDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_MODEL, sortByModelVisible)
                .setEnabled(OptionsItem.SORT_BY_MODEL, noAdapter
                        ? sortByModelEnabled
                        : sortByModelEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.MODEL)
                .setVisible(OptionsItem.SORT_BY_MODEL_ASCENDING, sortByModelAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_MODEL_ASCENDING, sortByModelAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_MODEL_DESCENDING, sortByModelDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_MODEL_DESCENDING, sortByModelDescendingEnabled)

                .setVisible(OptionsItem.SORT_BY_STATUS, sortByStatusVisible)
                .setEnabled(OptionsItem.SORT_BY_STATUS, noAdapter
                        ? sortByStatusEnabled
                        : sortByStatusEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.STATUS)
                .setVisible(OptionsItem.SORT_BY_STATUS_ASCENDING, sortByStatusAscendingVisible)
                .setEnabled(OptionsItem.SORT_BY_STATUS_ASCENDING, sortByStatusAscendingEnabled)
                .setVisible(OptionsItem.SORT_BY_STATUS_DESCENDING, sortByStatusDescendingVisible)
                .setEnabled(OptionsItem.SORT_BY_STATUS_DESCENDING, sortByStatusDescendingEnabled)


                .setVisible(OptionsItem.GROUP_BY, groupByVisible && (groupByNoneVisible || groupByParkVisible || groupByCreditTypeVisible || groupByCategoryVisible
                        || groupByManufacturerVisible || groupByModelVisible || groupByStatusVisible))
                .setEnabled(OptionsItem.GROUP_BY, groupByEnabled && (groupByNoneEnabled || groupByParkEnabled || groupByCreditTypeEnabled || groupByCategoryEnabled
                        || groupByManufacturerEnabled || groupByModelEnabled || groupByStatusEnabled))

                .setVisible(OptionsItem.GROUP_BY_NONE, groupByNoneVisible)
                .setEnabled(OptionsItem.GROUP_BY_NONE, noAdapter
                        ? groupByNoneEnabled
                        : groupByNoneEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.NONE)

                .setVisible(OptionsItem.GROUP_BY_PARK, groupByParkVisible)
                .setEnabled(OptionsItem.GROUP_BY_PARK, noAdapter
                        ? groupByParkEnabled
                        : groupByParkEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.PARK)

                .setVisible(OptionsItem.GROUP_BY_CREDIT_TYPE, groupByCreditTypeVisible)
                .setEnabled(OptionsItem.GROUP_BY_CREDIT_TYPE, noAdapter
                        ? groupByCreditTypeEnabled
                        : groupByCreditTypeEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.CREDIT_TYPE)

                .setVisible(OptionsItem.GROUP_BY_CATEGORY, groupByCategoryVisible)
                .setEnabled(OptionsItem.GROUP_BY_CATEGORY, noAdapter
                        ? groupByCategoryEnabled
                        : groupByCategoryEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.CATEGORY)

                .setVisible(OptionsItem.GROUP_BY_MANUFACTURER, groupByManufacturerVisible)
                .setEnabled(OptionsItem.GROUP_BY_MANUFACTURER, noAdapter
                        ? groupByManufacturerEnabled
                        : groupByManufacturerEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.MANUFACTURER)

                .setVisible(OptionsItem.GROUP_BY_MODEL, groupByModelVisible)
                .setEnabled(OptionsItem.GROUP_BY_MODEL, noAdapter
                        ? groupByModelEnabled
                        : groupByModelEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.MODEL)

                .setVisible(OptionsItem.GROUP_BY_STATUS, groupByStatusVisible)
                .setEnabled(OptionsItem.GROUP_BY_STATUS, noAdapter
                        ? groupByStatusEnabled
                        : groupByStatusEnabled && this.getContentRecyclerViewAdapter().getGroupType() != GroupType.STATUS)


                .setVisible(OptionsItem.EXPAND_ALL, noAdapter
                        ? expandAndCollapseAllVisible
                        : expandAndCollapseAllVisible && !this.getContentRecyclerViewAdapter().isAllContentExpanded())

                .setVisible(OptionsItem.COLLAPSE_ALL, noAdapter
                        ? expandAndCollapseAllVisible
                        : expandAndCollapseAllVisible && this.getContentRecyclerViewAdapter().isAllContentExpanded());

        return this.optionsMenuProvider.prepare(menu);
    }

    private boolean anyElementHasChildren()
    {
        for(IElement element : this.getElements())
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
        OptionsItem optionsItem = this.getOptionsItem(item);
        Log.i(String.format("OptionsItem %s selected", optionsItem));

        switch(optionsItem)
        {
            case NO_FUNCTION:
            case SORT:
            case SORT_BY:
            case GROUP_BY:
            case SORT_BY_NAME:
            case SORT_BY_PARK:
            case SORT_BY_CREDIT_TYPE:
            case SORT_BY_CATEGORY:
            case SORT_BY_MANUFACTURER:
            case SORT_BY_MODEL:
            case SORT_BY_STATUS:
                Log.v(String.format("OptionsItem [%s] has no function", optionsItem));
                return true;

            case HELP:
                this.master.showHelpOverlayFragment();
                return true;
        }

        if(this.getContentRecyclerViewAdapter() != null && this.getElements() != null)
        {
            master.invalidateOptionsMenu();

            switch(optionsItem)
            {
                case SORT_ASCENDING:
                case SORT_BY_NAME_ASCENDING:
                    this.viewModel.setElements(SortTool.sortElements(this.getElements(), SortType.BY_NAME, SortOrder.ASCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_DESCENDING:
                case SORT_BY_NAME_DESCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_NAME, SortOrder.DESCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_PARK_ASCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_PARK, SortOrder.ASCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_PARK_DESCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_PARK, SortOrder.DESCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_CREDIT_TYPE_ASCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_CREDIT_TYPE, SortOrder.ASCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_CREDIT_TYPE_DESCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_CREDIT_TYPE, SortOrder.DESCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_CATEGORY_ASCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_CATEGORY, SortOrder.ASCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_CATEGORY_DESCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_CATEGORY, SortOrder.DESCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_MANUFACTURER_ASCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_MANUFACTURER, SortOrder.ASCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_MANUFACTURER_DESCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_MANUFACTURER, SortOrder.DESCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_MODEL_ASCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_MODEL, SortOrder.ASCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_MODEL_DESCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_MODEL, SortOrder.DESCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_STATUS_ASCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_STATUS, SortOrder.ASCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;

                case SORT_BY_STATUS_DESCENDING:
                    this.setElements(SortTool.sortElements(this.getElements(), SortType.BY_STATUS, SortOrder.DESCENDING));
                    this.getContentRecyclerViewAdapter().setContent(this.getElements());
                    return true;
            }
        }

        if(this.getContentRecyclerViewAdapter() != null && this.getRequestCode() != null && this.getElements() != null)
        {
            this.master.invalidateOptionsMenu();

            GroupType groupType = null;
            switch(optionsItem)
            {
                case GROUP_BY_NONE:
                    groupType = GroupType.NONE;
                    break;

                case GROUP_BY_PARK:
                    groupType = GroupType.PARK;
                    break;

                case GROUP_BY_CREDIT_TYPE:
                    groupType = GroupType.CREDIT_TYPE;
                    break;

                case GROUP_BY_CATEGORY:
                    groupType = GroupType.CATEGORY;
                    break;

                case GROUP_BY_MANUFACTURER:
                    groupType = GroupType.MANUFACTURER;
                    break;

                case GROUP_BY_MODEL:
                    groupType = GroupType.MODEL;
                    break;

                case GROUP_BY_STATUS:
                    groupType = GroupType.STATUS;
                    break;
            }

            if(groupType != null)
            {
                ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(
                        this.viewModel.getContentRecyclerViewAdapterConfiguration().getDecoration(),
                        this.getRequestCode(),
                        groupType);

                this.getContentRecyclerViewAdapter().groupContent(groupType);

                return true;
            }
        }

        if(this.getContentRecyclerViewAdapter() != null)
        {
            master.invalidateOptionsMenu();

            switch(optionsItem)
            {
                case EXPAND_ALL:
                    this.getContentRecyclerViewAdapter().expandAllContent();
                    return true;

                case COLLAPSE_ALL:
                    this.getContentRecyclerViewAdapter().collapseAllContent();
                    return true;
            }
        }

        Log.v(String.format("OptionsItem %s not handled by Butler", optionsItem));
        return false;
    }

    public Menu createOptionsMenu(Menu menu)
    {
        if(this.getRequestCode() != null)
        {
            if(this.addSort())
            {
                this.optionsMenuProvider.add(OptionsItem.SORT);
            }

            if(this.addSortAscendingAndDescending())
            {
                this.optionsMenuProvider
                        .addToGroup(OptionsItem.SORT, OptionsItem.SORT_ASCENDING)
                        .addToGroup(OptionsItem.SORT, OptionsItem.SORT_DESCENDING);
            }

            if(this.addSortBy())
            {
                this.optionsMenuProvider.add(OptionsItem.SORT_BY);
            }

            if(this.addSortByName())
            {
                this.optionsMenuProvider
                        .addToGroup(OptionsItem.SORT_BY, OptionsItem.SORT_BY_NAME)
                        .addToGroup(OptionsItem.SORT_BY_NAME, OptionsItem.SORT_BY_NAME_ASCENDING)
                        .addToGroup(OptionsItem.SORT_BY_NAME, OptionsItem.SORT_BY_NAME_DESCENDING);
            }

            if(this.addSortByPark())
            {
                this.optionsMenuProvider
                        .addToGroup(OptionsItem.SORT_BY, OptionsItem.SORT_BY_PARK)
                        .addToGroup(OptionsItem.SORT_BY_PARK, OptionsItem.SORT_BY_PARK_ASCENDING)
                        .addToGroup(OptionsItem.SORT_BY_PARK, OptionsItem.SORT_BY_PARK_DESCENDING);
            }

            if(this.addSortByCreditType())
            {
                this.optionsMenuProvider
                        .addToGroup(OptionsItem.SORT_BY, OptionsItem.SORT_BY_CREDIT_TYPE)
                        .addToGroup(OptionsItem.SORT_BY_CREDIT_TYPE, OptionsItem.SORT_BY_CREDIT_TYPE_ASCENDING)
                        .addToGroup(OptionsItem.SORT_BY_CREDIT_TYPE, OptionsItem.SORT_BY_CREDIT_TYPE_DESCENDING);
            }

            if(this.addSortByCategory())
            {
                this.optionsMenuProvider
                        .addToGroup(OptionsItem.SORT_BY, OptionsItem.SORT_BY_CATEGORY)
                        .addToGroup(OptionsItem.SORT_BY_CATEGORY, OptionsItem.SORT_BY_CATEGORY_ASCENDING)
                        .addToGroup(OptionsItem.SORT_BY_CATEGORY, OptionsItem.SORT_BY_CATEGORY_DESCENDING);
            }

            if(this.addSortByManufacturer())
            {
                this.optionsMenuProvider
                        .addToGroup(OptionsItem.SORT_BY, OptionsItem.SORT_BY_MANUFACTURER)
                        .addToGroup(OptionsItem.SORT_BY_MANUFACTURER, OptionsItem.SORT_BY_MANUFACTURER_ASCENDING)
                        .addToGroup(OptionsItem.SORT_BY_MANUFACTURER, OptionsItem.SORT_BY_MANUFACTURER_DESCENDING);
            }

            if(this.addSortByModel())
            {
                this.optionsMenuProvider
                        .addToGroup(OptionsItem.SORT_BY, OptionsItem.SORT_BY_MODEL)
                        .addToGroup(OptionsItem.SORT_BY_MODEL, OptionsItem.SORT_BY_MODEL_ASCENDING)
                        .addToGroup(OptionsItem.SORT_BY_MODEL, OptionsItem.SORT_BY_MODEL_DESCENDING);
            }

            if(this.addSortByStatus())
            {
                this.optionsMenuProvider
                        .addToGroup(OptionsItem.SORT_BY, OptionsItem.SORT_BY_STATUS)
                        .addToGroup(OptionsItem.SORT_BY_STATUS, OptionsItem.SORT_BY_STATUS_ASCENDING)
                        .addToGroup(OptionsItem.SORT_BY_STATUS, OptionsItem.SORT_BY_STATUS_DESCENDING);
            }

            if(this.addGroupBy())
            {
                this.optionsMenuProvider.add(OptionsItem.GROUP_BY);
            }

            if(this.addGroupByNone())
            {
                this.optionsMenuProvider.addToGroup(OptionsItem.GROUP_BY, OptionsItem.GROUP_BY_NONE);
            }

            if(this.addGroupByPark())
            {
                this.optionsMenuProvider.addToGroup(OptionsItem.GROUP_BY, OptionsItem.GROUP_BY_PARK);
            }

            if(this.addGroupByCreditType())
            {
                this.optionsMenuProvider.addToGroup(OptionsItem.GROUP_BY, OptionsItem.GROUP_BY_CREDIT_TYPE);
            }

            if(this.addGroupByCategory())
            {
                this.optionsMenuProvider.addToGroup(OptionsItem.GROUP_BY, OptionsItem.GROUP_BY_CATEGORY);
            }

            if(this.addGroupByManufacturer())
            {
                this.optionsMenuProvider.addToGroup(OptionsItem.GROUP_BY, OptionsItem.GROUP_BY_MANUFACTURER);
            }

            if(this.addGroupByModel())
            {
                this.optionsMenuProvider.addToGroup(OptionsItem.GROUP_BY, OptionsItem.GROUP_BY_MODEL);
            }

            if(this.addGroupByStatus())
            {
                this.optionsMenuProvider.addToGroup(OptionsItem.GROUP_BY, OptionsItem.GROUP_BY_STATUS);
            }

            if(this.addExpandAndCollapseAll())
            {
                this.optionsMenuProvider
                        .add(OptionsItem.EXPAND_ALL)
                        .add(OptionsItem.COLLAPSE_ALL);
            }

            if(this.addEnableAndDisableEditing())
            {
                this.optionsMenuProvider
                        .add(OptionsItem.DISABLE_EDITING)
                        .add(OptionsItem.ENABLE_EDITING);
            }

            if(this.addGoToCurrentVisit())
            {
                this.optionsMenuProvider
                        .add(OptionsItem.GO_TO_CURRENT_VISIT);
            }

            if(this.addDeveloperOptions())
            {
                this.optionsMenuProvider
                        .add(OptionsItem.SHOW_BUILD_CONFIG)
                        .add(OptionsItem.SHOW_LOG)
                        .addToGroup(OptionsItem.SHOW_LOG, OptionsItem.SHOW_LOG_VERBOSE)
                        .addToGroup(OptionsItem.SHOW_LOG, OptionsItem.SHOW_LOG_DEBUG)
                        .addToGroup(OptionsItem.SHOW_LOG, OptionsItem.SHOW_LOG_INFO)
                        .addToGroup(OptionsItem.SHOW_LOG, OptionsItem.SHOW_LOG_WARNING)
                        .addToGroup(OptionsItem.SHOW_LOG, OptionsItem.SHOW_LOG_ERROR);
            }
        }

        return this.optionsMenuProvider
                .add(OptionsItem.HELP)
                .create(menu);
    }

    private boolean addSort()
    {
        switch(this.getRequestCode())
        {
            case PICK_ATTRACTIONS:
            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:

            case SORT_LOCATIONS:
            case SORT_PARKS:
            case SORT_ATTRACTIONS:
            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:

            case MANAGE_CREDIT_TYPES:
            case MANAGE_CATEGORIES:
            case MANAGE_MANUFACTURERS:
            case MANAGE_STATUSES:

            case SHOW_ATTRACTIONS:
            case SHOW_VISITS:
            case SHOW_PARK_OVERVIEW:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortAscendingAndDescending()
    {
        switch(this.getRequestCode())
        {
            case PICK_ATTRACTIONS:
            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:

            case SORT_LOCATIONS:
            case SORT_PARKS:
            case SORT_ATTRACTIONS:
            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:

            case SHOW_ATTRACTIONS:
            case SHOW_VISITS:
            case SHOW_PARK_OVERVIEW:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortBy()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_CATEGORY:
            case SHOW_CREDIT_TYPE:
            case SHOW_MANUFACTURER:
            case SHOW_MODEL:
            case SHOW_STATUS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByName()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_CATEGORY:
            case SHOW_CREDIT_TYPE:
            case SHOW_MANUFACTURER:
            case SHOW_MODEL:
            case SHOW_STATUS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByPark()
    {
        switch(this.getRequestCode())
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_CATEGORY:
            case SHOW_CREDIT_TYPE:
            case SHOW_MANUFACTURER:
            case SHOW_MODEL:
            case SHOW_STATUS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByCreditType()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_CATEGORY:
            case SHOW_MANUFACTURER:
            case SHOW_STATUS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByCategory()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_CREDIT_TYPE:
            case SHOW_MANUFACTURER:
            case SHOW_STATUS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByManufacturer()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_CATEGORY:
            case SHOW_CREDIT_TYPE:
            case SHOW_STATUS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByModel()
    {
        switch(this.getRequestCode())
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_CATEGORY:
            case SHOW_CREDIT_TYPE:
            case SHOW_MANUFACTURER:
            case SHOW_STATUS:
                return true;

            default:
                return false;
        }
    }

    private boolean addSortByStatus()
    {
        switch(this.getRequestCode())
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_CATEGORY:
            case SHOW_CREDIT_TYPE:
            case SHOW_MANUFACTURER:
            case SHOW_MODEL:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupBy()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByNone()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByPark()
    {
        switch(this.getRequestCode())
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByCreditType()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByCategory()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByManufacturer()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByModel()
    {
        switch(this.getRequestCode())
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addGroupByStatus()
    {
        switch(this.getRequestCode())
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
                return true;

            default:
                return false;
        }
    }

    private boolean addExpandAndCollapseAll()
    {
        switch(this.getRequestCode())
        {
            case PICK_MODEL:
            case MANAGE_MODELS:

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:

            case SHOW_LOCATIONS:
            case SHOW_VISIT:
            case SHOW_ATTRACTIONS:
            case SHOW_VISITS:
            case SHOW_PARK_OVERVIEW:
                return true;

            default:
                return false;
        }
    }

    private boolean addEnableAndDisableEditing()
    {
        return this.getRequestCode() == RequestCode.SHOW_VISIT;
    }

    private boolean addGoToCurrentVisit()
    {
        return this.getRequestCode() == RequestCode.NAVIGATE;
    }

    private boolean addDeveloperOptions()
    {
        return this.getRequestCode() == RequestCode.DEVELOPER_OPTIONS;
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

    private boolean goToCurrentVisitVisible = false;

    private boolean enableEditingVisible = false;
    private boolean disableEditingVisible = false;

    private void setElements(List<IElement> elements)
    {
        if(this.viewModel != null)
        {
            this.viewModel.setElements(elements);
        }
    }

    private RequestCode getRequestCode()
    {
        return this.viewModel != null
                ? this.viewModel.getRequestCode()
                : null;
    }

    private IContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return this.viewModel != null
                ? this.viewModel.getContentRecyclerViewAdapter()
                : null;
    }

    private List<IElement> getElements()
    {
        return this.viewModel != null
                ? this.viewModel.getElements()
                : null;
    }

    private IElement getElement()
    {
        return this.viewModel != null
                ? this.viewModel.getElement()
                : null;
    }
}