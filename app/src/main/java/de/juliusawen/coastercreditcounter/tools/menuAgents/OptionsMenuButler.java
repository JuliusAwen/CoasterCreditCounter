package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.graphics.Typeface;
import android.view.Menu;

import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.userInterface.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailDisplayMode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;

public class OptionsMenuButler
{
    private BaseActivity master;
    private OptionsMenuAgent optionsMenuAgent;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private RequestCode requestCode;
    private List<IElement> elements;


    public OptionsMenuButler(
            OptionsMenuAgent optionsMenuAgent,
            ContentRecyclerViewAdapter contentRecyclerViewAdapter,
            RequestCode requestCode,
            List<IElement> elements,
            BaseActivity master)
    {
        this.optionsMenuAgent = optionsMenuAgent;
        this.contentRecyclerViewAdapter = contentRecyclerViewAdapter;
        this.requestCode = requestCode;
        this.elements = elements;
        this.master = master;
    }

    public Menu createOptionsMenu(Menu menu)
    {
        switch(this.requestCode)
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
            case PICK_MODEL:
            case MANAGE_MODELS:
            {
                this.optionsMenuAgent
                        .add(OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_NAME, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_NAME_ASCENDING, OptionsItem.SORT_BY_NAME)
                        .addToGroup(OptionsItem.SORT_BY_NAME_DESCENDING, OptionsItem.SORT_BY_NAME)
                        .addToGroup(OptionsItem.SORT_BY_PARK, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_PARK_ASCENDING, OptionsItem.SORT_BY_PARK)
                        .addToGroup(OptionsItem.SORT_BY_PARK_DESCENDING, OptionsItem.SORT_BY_PARK)
                        .addToGroup(OptionsItem.SORT_BY_CREDIT_TYPE, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_CREDIT_TYPE_ASCENDING, OptionsItem.SORT_BY_CREDIT_TYPE)
                        .addToGroup(OptionsItem.SORT_BY_CREDIT_TYPE_DESCENDING, OptionsItem.SORT_BY_CREDIT_TYPE)
                        .addToGroup(OptionsItem.SORT_BY_CATEGORY, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_CATEGORY_ASCENDING, OptionsItem.SORT_BY_CATEGORY)
                        .addToGroup(OptionsItem.SORT_BY_CATEGORY_DESCENDING, OptionsItem.SORT_BY_CATEGORY)
                        .addToGroup(OptionsItem.SORT_BY_MANUFACTURER, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, OptionsItem.SORT_BY_MANUFACTURER)
                        .addToGroup(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, OptionsItem.SORT_BY_MANUFACTURER)
                        .addToGroup(OptionsItem.SORT_BY_MODEL, OptionsItem.SORT_BY)
                        .addToGroup(OptionsItem.SORT_BY_MODEL_ASCENDING, OptionsItem.SORT_BY_MODEL)
                        .addToGroup(OptionsItem.SORT_BY_MODEL_DESCENDING, OptionsItem.SORT_BY_MODEL)
                        .add(OptionsItem.GROUP_BY)
                        .addToGroup(OptionsItem.GROUP_BY_PARK, OptionsItem.GROUP_BY)
                        .addToGroup(OptionsItem.GROUP_BY_CREDIT_TYPE, OptionsItem.GROUP_BY)
                        .addToGroup(OptionsItem.GROUP_BY_CATEGORY, OptionsItem.GROUP_BY)
                        .addToGroup(OptionsItem.GROUP_BY_MANUFACTURER, OptionsItem.GROUP_BY)
                        .addToGroup(OptionsItem.GROUP_BY_MODEL, OptionsItem.GROUP_BY)
                        .addToGroup(OptionsItem.GROUP_BY_STATUS, OptionsItem.GROUP_BY);
                break;
            }

            case PICK_ATTRACTIONS:
            case PICK_CREDIT_TYPE:
            case PICK_CATEGORY:
            case PICK_MANUFACTURER:
            case PICK_STATUS:
            {
                this.optionsMenuAgent
                        .add(OptionsItem.SORT)
                        .addToGroup(OptionsItem.SORT_ASCENDING, OptionsItem.SORT)
                        .addToGroup(OptionsItem.SORT_DESCENDING, OptionsItem.SORT);
                break;
            }
        }

        return this.optionsMenuAgent
                .add(OptionsItem.EXPAND_ALL)
                .add(OptionsItem.COLLAPSE_ALL)
                .create(menu);
    }

    public Menu prepareOptionsMenu(Menu menu)
    {
        boolean sortByParkVisible = this.requestCode != RequestCode.PICK_ATTRACTIONS
                && this.requestCode != RequestCode.PICK_MODEL
                && this.requestCode != RequestCode.MANAGE_MODELS;
        boolean sortByParkEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.PARK;

        boolean sortByCreditTypeEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.CREDIT_TYPE;
        boolean sortByCategoryEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.CATEGORY;
        boolean sortByManufacturerEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.MANUFACTURER;

        boolean sortByModelVisible = this.requestCode != RequestCode.PICK_MODEL && this.requestCode != RequestCode.MANAGE_MODELS;
        boolean sortByModelEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.MODEL;

        boolean sortByEnabled = sortByParkEnabled || sortByCreditTypeEnabled || sortByCategoryEnabled || sortByManufacturerEnabled || sortByModelEnabled;

        boolean groupByParkVisible = this.requestCode != RequestCode.PICK_ATTRACTIONS
                && this.requestCode != RequestCode.PICK_MODEL
                && this.requestCode != RequestCode.MANAGE_MODELS;
        boolean groupByParkEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.PARK;

        boolean groupByCreditTypeEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.CREDIT_TYPE;
        boolean groupByCategoryEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.CATEGORY;
        boolean groupByManufacturerEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.MANUFACTURER;

        boolean groupByModelVisible = this.requestCode != RequestCode.PICK_MODEL && this.requestCode != RequestCode.MANAGE_MODELS;
        boolean groupByModelEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.MODEL;

        boolean groupByStatusVisible = this.requestCode != RequestCode.PICK_MODEL && this.requestCode != RequestCode.MANAGE_MODELS;
        boolean groupByStatusEnabled = this.contentRecyclerViewAdapter.getGroupType() != GroupType.STATUS;

        boolean groupByEnabled = groupByParkEnabled || groupByCreditTypeEnabled ||groupByCategoryEnabled || groupByManufacturerEnabled || groupByModelEnabled || groupByStatusEnabled;

        boolean expandAllVisible = (((this.requestCode == RequestCode.PICK_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_CATEGORY_TO_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_MANUFACTURER_TO_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_MODEL_TO_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_STATUS_TO_ATTRACTIONS)
                && this.elements.size() > 1)
                    || this.anyElementHasChildren())
                    && !this.contentRecyclerViewAdapter.isAllExpanded();

        boolean collapseVisible = (((this.requestCode == RequestCode.PICK_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_CATEGORY_TO_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_MANUFACTURER_TO_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_MODEL_TO_ATTRACTIONS
                || this.requestCode == RequestCode.ASSIGN_STATUS_TO_ATTRACTIONS)
                && this.elements.size() > 1)
                    || this.anyElementHasChildren())
                    && this.contentRecyclerViewAdapter.isAllExpanded();

        switch(this.requestCode)
        {
            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_MODEL_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
            case PICK_MODEL:
            case MANAGE_MODELS:
            {
                this.optionsMenuAgent
                        .setEnabled(OptionsItem.SORT_BY, sortByEnabled)
                        .setVisible(OptionsItem.SORT_BY_PARK, sortByParkVisible)
                        .setEnabled(OptionsItem.SORT_BY_PARK, sortByParkEnabled)
                        .setEnabled(OptionsItem.SORT_BY_CREDIT_TYPE, sortByCreditTypeEnabled)
                        .setEnabled(OptionsItem.SORT_BY_CATEGORY, sortByCategoryEnabled)
                        .setEnabled(OptionsItem.SORT_BY_MANUFACTURER, sortByManufacturerEnabled)
                        .setVisible(OptionsItem.SORT_BY_MODEL, sortByModelVisible)
                        .setEnabled(OptionsItem.SORT_BY_MODEL, sortByModelEnabled)
                        .setEnabled(OptionsItem.GROUP_BY, groupByEnabled)
                        .setVisible(OptionsItem.GROUP_BY_PARK, groupByParkVisible)
                        .setEnabled(OptionsItem.GROUP_BY_PARK, groupByParkEnabled)
                        .setEnabled(OptionsItem.GROUP_BY_CREDIT_TYPE, groupByCreditTypeEnabled)
                        .setEnabled(OptionsItem.GROUP_BY_CATEGORY, groupByCategoryEnabled)
                        .setEnabled(OptionsItem.GROUP_BY_MANUFACTURER, groupByManufacturerEnabled)
                        .setVisible(OptionsItem.GROUP_BY_MODEL, groupByModelVisible)
                        .setEnabled(OptionsItem.GROUP_BY_MODEL, groupByModelEnabled)
                        .setVisible(OptionsItem.GROUP_BY_STATUS, groupByStatusVisible)
                        .setEnabled(OptionsItem.GROUP_BY_STATUS, groupByStatusEnabled);
                break;
            }

            default:
                this.optionsMenuAgent.setEnabled(OptionsItem.SORT, this.elements.size() > 1);
                break;
        }

        return this.optionsMenuAgent
                .setVisible(OptionsItem.EXPAND_ALL, expandAllVisible)
                .setVisible(OptionsItem.COLLAPSE_ALL, collapseVisible)
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

    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        switch(item)
        {
            case SORT_BY:
            case GROUP_BY:
            case SORT_BY_CREDIT_TYPE:
            case SORT_BY_CATEGORY:
            case SORT_BY_MANUFACTURER:
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

            case GROUP_BY_PARK:
                this.setDetailModesAndGroupElements(GroupType.PARK);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_CREDIT_TYPE:
                this.setDetailModesAndGroupElements(GroupType.CREDIT_TYPE);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_CATEGORY:
                this.setDetailModesAndGroupElements(GroupType.CATEGORY);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_MANUFACTURER:
                this.setDetailModesAndGroupElements(GroupType.MANUFACTURER);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_MODEL:
                this.setDetailModesAndGroupElements(GroupType.MODEL);
                this.master.invalidateOptionsMenu();
                return true;

            case GROUP_BY_STATUS:
                this.setDetailModesAndGroupElements(GroupType.STATUS);
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

    public void setDetailModesAndGroupElements(GroupType groupType)
    {
        this.contentRecyclerViewAdapter
                .clearTypefacesForContentType()
                .setTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                .clearTypefacesForDetailType()
                .setTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC)
                .clearDetailTypesAndModeForContentType();

        switch(this.requestCode)
        {
            case PICK_ATTRACTIONS:
            {
                switch(groupType)
                {
                    case CREDIT_TYPE:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .groupItems(GroupType.CREDIT_TYPE);
                        break;

                    case CATEGORY:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .groupItems(GroupType.CATEGORY);
                        break;

                    case MANUFACTURER:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .groupItems(GroupType.MANUFACTURER);
                        break;

                    case MODEL:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                                .groupItems(GroupType.MANUFACTURER);
                        break;

                    case STATUS:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .groupItems(GroupType.STATUS);
                        break;
                }

                this.contentRecyclerViewAdapter
                        .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW);
                break;
            }

            case PICK_MODEL:
            case MANAGE_MODELS:
            {
                switch(groupType)
                {
                    case CREDIT_TYPE:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .groupItems(GroupType.CREDIT_TYPE);
                        break;

                    case CATEGORY:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .groupItems(GroupType.CATEGORY);
                        break;

                    case MANUFACTURER:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(Model.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .groupItems(GroupType.MANUFACTURER);
                        break;
                }

                this.contentRecyclerViewAdapter
                        .setTypefaceForContentType(CreditType.class, Typeface.BOLD)
                        .setTypefaceForContentType(Category.class, Typeface.BOLD)
                        .setTypefaceForContentType(Manufacturer.class, Typeface.BOLD)
                        .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                        .setSpecialStringResourceForType(IProperty.class, R.string.substitute_properties_default_postfix);
                break;
            }

            default:
            {
                this.contentRecyclerViewAdapter
                        .setTypefaceForContentType(IProperty.class, Typeface.BOLD);

                switch(groupType)
                {
                    case PARK:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .groupItems(GroupType.PARK);
                        break;

                    case CREDIT_TYPE:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER ,DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .groupItems(GroupType.CREDIT_TYPE);
                        break;

                    case CATEGORY:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .groupItems(GroupType.CATEGORY);
                        break;

                    case MANUFACTURER:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .groupItems(GroupType.MANUFACTURER);
                        break;

                    case MODEL:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .groupItems(GroupType.MODEL);
                        break;

                    case STATUS:
                        this.contentRecyclerViewAdapter
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.LOCATION, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.CATEGORY, DetailDisplayMode.BELOW)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                                .setDetailTypesAndModeForContentType(IAttraction.class, DetailType.MODEL, DetailDisplayMode.ABOVE)
                                .groupItems(GroupType.STATUS);
                        break;
                }
            }
        }
    }
}
