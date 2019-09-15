package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;

public class OptionsMenuAgent
{
    public static final OptionsItem HELP = OptionsItem.HELP;

    public static final OptionsItem EXPAND_ALL = OptionsItem.EXPAND_ALL;
    public static final OptionsItem COLLAPSE_ALL = OptionsItem.COLLAPSE_ALL;

    public static final OptionsItem SORT = OptionsItem.SORT;

    public static final OptionsItem SORT_ATTRACTION_CATEGORIES = OptionsItem.SORT_ATTRACTION_CATEGORIES;
    public static final OptionsItem SORT_MANUFACTURERS = OptionsItem.SORT_MANUFACTURERS;
    public static final OptionsItem SORT_STATUSES = OptionsItem.SORT_STATUSES;

    public static final OptionsItem SORT_BY_YEAR  = OptionsItem.SORT_BY_YEAR;
    public static final OptionsItem SORT_BY_NAME  = OptionsItem.SORT_BY_NAME;
    public static final OptionsItem SORT_BY_LOCATION  = OptionsItem.SORT_BY_LOCATION;
    public static final OptionsItem SORT_BY_ATTRACTION_CATEGORY = OptionsItem.SORT_BY_ATTRACTION_CATEGORY;
    public static final OptionsItem SORT_BY_MANUFACTURER  = OptionsItem.SORT_BY_MANUFACTURER;

    public static final OptionsItem GROUP_BY_LOCATION = OptionsItem.GROUP_BY_LOCATION;
    public static final OptionsItem GROUP_BY_ATTRACTION_CATEGORY = OptionsItem.GROUP_BY_ATTRACTION_CATEGORY;
    public static final OptionsItem GROUP_BY_MANUFACTURER = OptionsItem.GROUP_BY_MANUFACTURER;
    public static final OptionsItem GROUP_BY_STATUS = OptionsItem.GROUP_BY_STATUS;

    public static final OptionsItem GO_TO_CURRENT_VISIT = OptionsItem.GO_TO_CURRENT_VISIT;

    public static final OptionsItem ENABLE_EDITING = OptionsItem.ENABLE_EDITING;
    public static final OptionsItem DISABLE_EDITING = OptionsItem.DISABLE_EDITING;


    private final List<OptionsItem> itemsToAdd;
    private final Map<OptionsItem, Boolean> setEnabledByItem;
    private final Map<OptionsItem, Boolean> setVisibleByItem;
    private final Map<OptionsItem, OptionsItem> submenuByItem;

    private final Map<OptionsItem, Integer> stringResourcesByItem;
    private final Map<OptionsItem, Integer> drawableResourcesByItem;


    public OptionsMenuAgent()
    {
        this.itemsToAdd = new LinkedList<>();
        this.setEnabledByItem = new HashMap<>();
        this.setVisibleByItem = new HashMap<>();
        this.submenuByItem = new HashMap<>();

        this.stringResourcesByItem = this.initializeStringResourcesByItem();
        this.drawableResourcesByItem = this.initializeDrawableResourcesByItem();
    }

    private Map<OptionsItem, Integer> initializeStringResourcesByItem()
    {
        Map<OptionsItem, Integer> stringResourcesByItem = new HashMap<>();

        stringResourcesByItem.put(OptionsItem.HELP, R.string.menu_item_help);

        stringResourcesByItem.put(OptionsItem.EXPAND_ALL, R.string.menu_item_expand_all);
        stringResourcesByItem.put(OptionsItem.COLLAPSE_ALL, R.string.menu_item_collapse_all);

        stringResourcesByItem.put(OptionsItem.SORT, R.string.menu_item_sort);

        stringResourcesByItem.put(OptionsItem.SORT_ATTRACTION_CATEGORIES, R.string.menu_item_sort);
        stringResourcesByItem.put(OptionsItem.SORT_MANUFACTURERS, R.string.menu_item_sort);
        stringResourcesByItem.put(OptionsItem.SORT_STATUSES, R.string.menu_item_sort);

        stringResourcesByItem.put(OptionsItem.SORT_BY_YEAR, R.string.menu_item_sort_by_year);
        stringResourcesByItem.put(OptionsItem.SORT_BY_NAME, R.string.menu_item_sort_by_name);
        stringResourcesByItem.put(OptionsItem.SORT_BY_LOCATION, R.string.menu_item_sort_by_location);
        stringResourcesByItem.put(OptionsItem.SORT_BY_ATTRACTION_CATEGORY, R.string.menu_item_sort_by_attraction_category);
        stringResourcesByItem.put(OptionsItem.SORT_BY_MANUFACTURER, R.string.menu_item_sort_by_manufacturer);

        stringResourcesByItem.put(OptionsItem.GROUP_BY_LOCATION, R.string.menu_item_group_by_location);
        stringResourcesByItem.put(OptionsItem.GROUP_BY_ATTRACTION_CATEGORY, R.string.menu_item_group_by_attraction_category);
        stringResourcesByItem.put(OptionsItem.GROUP_BY_MANUFACTURER, R.string.menu_item_group_by_manufacturer);
        stringResourcesByItem.put(OptionsItem.GROUP_BY_STATUS, R.string.menu_item_group_by_status);

        stringResourcesByItem.put(OptionsItem.GO_TO_CURRENT_VISIT, R.string.menu_item_go_to_current_visit);

        stringResourcesByItem.put(OptionsItem.ENABLE_EDITING, R.string.menu_item_enable_editing);
        stringResourcesByItem.put(OptionsItem.DISABLE_EDITING, R.string.menu_item_disable_editing);

        return stringResourcesByItem;
    }

    private Map<OptionsItem, Integer> initializeDrawableResourcesByItem()
    {
        Map<OptionsItem, Integer> drawableResourcesByItem = new HashMap<>();

        drawableResourcesByItem.put(OptionsItem.GO_TO_CURRENT_VISIT, R.drawable.ic_baseline_local_activity);

        drawableResourcesByItem.put(OptionsItem.ENABLE_EDITING, R.drawable.ic_baseline_create);
        drawableResourcesByItem.put(OptionsItem.DISABLE_EDITING, R.drawable.ic_baseline_block);

        return drawableResourcesByItem;
    }


    public OptionsMenuAgent add(OptionsItem item)
    {
        this.itemsToAdd.add(item);

        return this;
    }

    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("OptionsMenuAgent.show:: adding [%d] Item(s) to OptionsMenu", this.itemsToAdd.size()));

        Menu subMenuSortBy = null;
        Menu subMenuGroupBy = null;

        for(OptionsItem item : this.itemsToAdd)
        {
            switch(item)
            {
                case HELP:
                    addHelpToMenu(menu);
                    break;

                case SORT:
                    this.createMenuSort(item, menu);
                    this.submenuByItem.put(item, OptionsItem.SORT);
                    break;

                case SORT_BY_YEAR:
                case SORT_BY_NAME:
                case SORT_BY_LOCATION:
                case SORT_BY_ATTRACTION_CATEGORY:
                case SORT_BY_MANUFACTURER:
                {
                    if(subMenuSortBy == null)
                    {
                        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.show:: adding subMenu <sort by>");
                        subMenuSortBy = menu.addSubMenu(OptionsItem.SORT_BY.ordinal(), OptionsItem.SORT_BY.ordinal(), Menu.NONE, R.string.menu_item_sort_by);
                    }
                    this.createMenuSort(item, subMenuSortBy);
                    this.submenuByItem.put(item, OptionsItem.SORT_BY);
                    break;
                }

                case GROUP_BY_LOCATION:
                case GROUP_BY_ATTRACTION_CATEGORY:
                case GROUP_BY_MANUFACTURER:
                case GROUP_BY_STATUS:
                {
                    if(subMenuGroupBy == null)
                    {
                        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.show:: adding submenu <group by>");
                        subMenuGroupBy = menu.addSubMenu(OptionsItem.GROUP_BY.ordinal(), OptionsItem.GROUP_BY.ordinal(), Menu.NONE, R.string.menu_item_group_by);
                    }
                    this.addItemToSubMenu(item, subMenuGroupBy);
                    this.submenuByItem.put(item, OptionsItem.GROUP_BY);
                    break;
                }

                case GO_TO_CURRENT_VISIT:
                case ENABLE_EDITING:
                case DISABLE_EDITING:
                    this.addActionItemToMenu(item, menu);
                    break;

                default:
                    this.addItemToMenu(item, menu);
                    break;
            }
        }

        this.itemsToAdd.clear();
    }

    private void addHelpToMenu(Menu menu)
    {
        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.addHelpToSubMenu:: adding HELP");
        menu.add(Menu.NONE, OptionsItem.HELP.ordinal(), 1, R.string.menu_item_help); // 1 - represents the order: as all other selections are 0 HELP should always be sorted to the bottom
    }

    private void createMenuSort(OptionsItem item, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.createMenuSort:: adding SubMenu [%s]", item));
        Menu subMenu = menu.addSubMenu(item.ordinal(), item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));

        switch(item)
        {
            case SORT:
                this.addSortAscendingToSubMenu(OptionsItem.SORT_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(OptionsItem.SORT_DESCENDING, subMenu);
                break;

            case SORT_BY_YEAR:
                this.addSortAscendingToSubMenu(OptionsItem.SORT_BY_YEAR_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(OptionsItem.SORT_BY_YEAR_DESCENDING, subMenu);
                break;

            case SORT_BY_NAME:
                this.addSortAscendingToSubMenu(OptionsItem.SORT_BY_NAME_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(OptionsItem.SORT_BY_NAME_DESCENDING, subMenu);
                break;

            case SORT_BY_LOCATION:
                this.addSortAscendingToSubMenu(OptionsItem.SORT_BY_LOCATION_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(OptionsItem.SORT_BY_LOCATION_DESCENDING, subMenu);
                break;

            case SORT_BY_ATTRACTION_CATEGORY:
                this.addSortAscendingToSubMenu(OptionsItem.SORT_BY_ATTRACTION_CATEGORY_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(OptionsItem.SORT_BY_ATTRACTION_CATEGORY_DESCENDING, subMenu);
                break;

            case SORT_BY_MANUFACTURER:
                this.addSortAscendingToSubMenu(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, subMenu);
                break;
        }
    }

    private void addSortAscendingToSubMenu(OptionsItem item, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSortAscendingToSubMenu:: adding [%s]", item));
        subMenu.add(Menu.NONE, item.ordinal(), Menu.NONE, R.string.menu_item_sort_ascending);
    }

    private void addSortDescendingToSubMenu(OptionsItem item, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSortDescendingToSubMenu:: adding [%s]", item));
        subMenu.add(Menu.NONE, item.ordinal(), Menu.NONE, R.string.menu_item_sort_descending);
    }

    private void addItemToSubMenu(OptionsItem item, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addItemToSubMenu:: adding [%s]", item));
        subMenu.add(item.ordinal(), item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
    }

    private void addItemToMenu(OptionsItem item, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addItemToMenu:: adding [%s]", item));
        menu.add(Menu.NONE, item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
    }

    private void addActionItemToMenu(OptionsItem item, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addActionItemToMenu:: adding [%s]", item));

        menu.add(Menu.NONE, item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item))
                .setIcon(DrawableProvider.getColoredDrawable(this.drawableResourcesByItem.get(item), R.color.white))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    public OptionsMenuAgent setEnabled(OptionsItem item, boolean setEnabled)
    {
        this.setEnabledByItem.put(item, setEnabled);

        return this;
    }

    public OptionsMenuAgent setVisible(OptionsItem item, boolean setVisible)
    {
        this.setVisibleByItem.put(item, setVisible);

        return this;
    }

    public void prepare(Menu menu)
    {

        for(OptionsItem optionsItem : this.setEnabledByItem.keySet())
        {
            MenuItem menuItem = menu.findItem(optionsItem.ordinal());
            if(menuItem != null)
            {
                if(menuItem.hasSubMenu())
                {
                    if(this.submenuByItem.get(optionsItem).equals(optionsItem))
                    {
                        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<hasSubMenu>:: setting [%s] enabled [%S]", optionsItem, this.setEnabledByItem.get(optionsItem)));

                        menuItem.setEnabled(this.setEnabledByItem.get(optionsItem));
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<hasSubMenu>:: setting [%s][%s] enabled [%S]",
                                this.submenuByItem.get(optionsItem), optionsItem, this.setEnabledByItem.get(optionsItem)));

                        menuItem = menu.findItem(this.submenuByItem.get(optionsItem).ordinal());
                        if(menuItem != null)
                        {
                            menuItem.getSubMenu().setGroupEnabled(optionsItem.ordinal(), this.setEnabledByItem.get(optionsItem));
                        }
                    }
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: setting [%s] enabled [%S]", optionsItem, this.setEnabledByItem.get(optionsItem)));
                    menuItem.setEnabled(this.setEnabledByItem.get(optionsItem));
                }
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<setEnable>:: MenuItem [%s] not found", optionsItem));
            }
        }

        for(OptionsItem optionsItem : this.setVisibleByItem.keySet())
        {
            MenuItem menuItem = menu.findItem(optionsItem.ordinal());
            if(menuItem != null)
            {
                Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: setting [%s] visible [%S]", optionsItem, this.setVisibleByItem.get(optionsItem)));
                menuItem.setVisible(this.setVisibleByItem.get(optionsItem));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<setVisible>:: MenuItem [%s] not found", optionsItem));
            }
        }

        this.setEnabledByItem.clear();
        this.setVisibleByItem.clear();
    }


    public boolean handleOptionsItemSelected(MenuItem item, IOptionsMenuAgentClient client)
    {
        if(item.getItemId() <= OptionsItem.values().length)
        {
            Log.i(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: MenuItem [%s] selected", OptionsItem.values()[item.getItemId()].toString()));

            switch(OptionsItem.values()[item.getItemId()])
            {
                //add case for OptionsItems with no function here
                case NO_FUNCTION:
                case SORT:
                case SORT_BY:
                case GROUP_BY:
                case SORT_BY_YEAR:
                case SORT_BY_NAME:
                case SORT_BY_LOCATION:
                case SORT_BY_ATTRACTION_CATEGORY:
                case SORT_BY_MANUFACTURER:
                    return true;

                case HELP:
                    client.handleHelpSelected();
                    break;
                case EXPAND_ALL:
                    client.handleExpandAllSelected();
                    break;
                case COLLAPSE_ALL:
                    client.handleCollapseAllSelected();
                    break;
                case GROUP_BY_LOCATION:
                    client.handleGroupByLocationSelected();
                    break;
                case GROUP_BY_ATTRACTION_CATEGORY:
                    client.handleGroupByAttractionCategorySelected();
                    break;
                case GROUP_BY_MANUFACTURER:
                    client.handleGroupByManufacturerSelected();
                    break;
                case GROUP_BY_STATUS:
                    client.handleGroupByStatusSelected();
                    break;
                case SORT_ASCENDING:
                    client.handleSortAscendingSelected();
                    break;
                case SORT_DESCENDING:
                    client.handleSortDescendingSelected();
                    break;
                case SORT_ATTRACTION_CATEGORIES:
                    client.handleSortAttractionCategoriesSelected();
                    break;
                case SORT_MANUFACTURERS:
                    client.handleSortManufacturersSelected();
                    break;
                case SORT_STATUSES:
                    client.handleSortStatusesSelected();
                    break;
                case SORT_BY_YEAR_ASCENDING:
                    client.handleSortByYearAscendingSelected();
                    break;
                case SORT_BY_YEAR_DESCENDING:
                    client.handleSortByYearDescendingSelected();
                    break;
                case SORT_BY_NAME_ASCENDING:
                    client.handleSortByNameAscendingSelected();
                    break;
                case SORT_BY_NAME_DESCENDING:
                    client.handleSortByNameDescendingSelected();
                    break;
                case SORT_BY_LOCATION_ASCENDING:
                    client.handleSortByLocationAscendingSelected();
                    break;
                case SORT_BY_LOCATION_DESCENDING:
                    client.handleSortByLocationDescendingSelected();
                    break;
                case SORT_BY_ATTRACTION_CATEGORY_ASCENDING:
                    client.handleSortByAttractionCategoryAscendingSelected();
                    break;
                case SORT_BY_ATTRACTION_CATEGORY_DESCENDING:
                    client.handleSortByAttractionCategoryDescendingSelected();
                    break;
                case SORT_BY_MANUFACTURER_ASCENDING:
                    client.handleSortByManufacturerAscendingSelected();
                    break;
                case SORT_BY_MANUFACTURER_DESCENDING:
                    client.handleSortByManufacturerDescendingSelected();
                    break;
                case GO_TO_CURRENT_VISIT:
                    client.handleGoToCurrentVisitSelected();
                    break;
                case ENABLE_EDITING:
                    client.handleEnableEditingSelected();
                    break;
                case DISABLE_EDITING:
                    client.handleDisableEditingSelected();
                    break;

                default:
                    return false;
            }

            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, "OptionsMenuAgent.handleOptionsItemSelected:: MenuItem [%s] not valid");
            return false;
        }
    }


    private enum OptionsItem
    {
        NO_FUNCTION,


        //NORMAL MENU ITEMS

        HELP,

        EXPAND_ALL,
        COLLAPSE_ALL,


        SORT,

        SORT_ASCENDING,
        SORT_DESCENDING,

        SORT_ATTRACTION_CATEGORIES,
        SORT_MANUFACTURERS,
        SORT_STATUSES,

        SORT_BY,

        SORT_BY_YEAR,
        SORT_BY_YEAR_ASCENDING,
        SORT_BY_YEAR_DESCENDING,

        SORT_BY_NAME,
        SORT_BY_NAME_ASCENDING,
        SORT_BY_NAME_DESCENDING,

        SORT_BY_LOCATION,
        SORT_BY_LOCATION_ASCENDING,
        SORT_BY_LOCATION_DESCENDING,

        SORT_BY_ATTRACTION_CATEGORY,
        SORT_BY_ATTRACTION_CATEGORY_ASCENDING,
        SORT_BY_ATTRACTION_CATEGORY_DESCENDING,

        SORT_BY_MANUFACTURER,
        SORT_BY_MANUFACTURER_ASCENDING,
        SORT_BY_MANUFACTURER_DESCENDING,


        GROUP_BY,
        GROUP_BY_LOCATION,
        GROUP_BY_MANUFACTURER,
        GROUP_BY_ATTRACTION_CATEGORY,
        GROUP_BY_STATUS,


        //ACTION MENU ITEMS

        GO_TO_CURRENT_VISIT,
        ENABLE_EDITING,
        DISABLE_EDITING,
    }
}



