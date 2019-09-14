package de.juliusawen.coastercreditcounter.tools.menuAgent;

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

    private final List<OptionsItem> optionsItemsToAdd;
    private final Map<OptionsItem, Boolean> setEnabledByOptionsItem;
    private final Map<OptionsItem, Boolean> setVisibleByOptionsItem;
    private final Map<OptionsItem, OptionsItem> submenuByOptionsItem;

    private final Map<OptionsItem, Integer> stringResourcesByOptionsItem;
    private final Map<OptionsItem, Integer> drawableResourcesByOptionsItem;


    public OptionsMenuAgent()
    {
        this.optionsItemsToAdd = new LinkedList<>();
        this.setEnabledByOptionsItem = new HashMap<>();
        this.setVisibleByOptionsItem = new HashMap<>();
        this.submenuByOptionsItem = new HashMap<>();

        this.stringResourcesByOptionsItem = this.initializeStringResourcesByOptionsItem();
        this.drawableResourcesByOptionsItem = this.initializeDrawableResourcesByOptionsItem();
    }

    private Map<OptionsItem, Integer> initializeStringResourcesByOptionsItem()
    {
        Map<OptionsItem, Integer> stringResourcesByOptionsItem = new HashMap<>();

        stringResourcesByOptionsItem.put(OptionsItem.HELP, R.string.selection_help);

        stringResourcesByOptionsItem.put(OptionsItem.EXPAND_ALL, R.string.selection_expand_all);
        stringResourcesByOptionsItem.put(OptionsItem.COLLAPSE_ALL, R.string.selection_collapse_all);

        stringResourcesByOptionsItem.put(OptionsItem.SORT, R.string.selection_sort);

        stringResourcesByOptionsItem.put(OptionsItem.SORT_ATTRACTION_CATEGORIES, R.string.selection_sort);
        stringResourcesByOptionsItem.put(OptionsItem.SORT_MANUFACTURERS, R.string.selection_sort);
        stringResourcesByOptionsItem.put(OptionsItem.SORT_STATUSES, R.string.selection_sort);

        stringResourcesByOptionsItem.put(OptionsItem.SORT_BY_YEAR, R.string.selection_sort_by_year);
        stringResourcesByOptionsItem.put(OptionsItem.SORT_BY_NAME, R.string.selection_sort_by_name);
        stringResourcesByOptionsItem.put(OptionsItem.SORT_BY_LOCATION, R.string.selection_sort_by_location);
        stringResourcesByOptionsItem.put(OptionsItem.SORT_BY_ATTRACTION_CATEGORY, R.string.selection_sort_by_attraction_category);
        stringResourcesByOptionsItem.put(OptionsItem.SORT_BY_MANUFACTURER, R.string.selection_sort_by_manufacturer);

        stringResourcesByOptionsItem.put(OptionsItem.GROUP_BY_LOCATION, R.string.selection_group_by_location);
        stringResourcesByOptionsItem.put(OptionsItem.GROUP_BY_ATTRACTION_CATEGORY, R.string.selection_group_by_attraction_category);
        stringResourcesByOptionsItem.put(OptionsItem.GROUP_BY_MANUFACTURER, R.string.selection_group_by_manufacturer);
        stringResourcesByOptionsItem.put(OptionsItem.GROUP_BY_STATUS, R.string.selection_group_by_status);

        stringResourcesByOptionsItem.put(OptionsItem.GO_TO_CURRENT_VISIT, R.string.selection_go_to_current_visit);

        stringResourcesByOptionsItem.put(OptionsItem.ENABLE_EDITING, R.string.selection_enable_editing);
        stringResourcesByOptionsItem.put(OptionsItem.DISABLE_EDITING, R.string.selection_disable_editing);

        return stringResourcesByOptionsItem;
    }

    private Map<OptionsItem, Integer> initializeDrawableResourcesByOptionsItem()
    {
        Map<OptionsItem, Integer> drawableResourcesByOptionsItem = new HashMap<>();

        drawableResourcesByOptionsItem.put(OptionsItem.GO_TO_CURRENT_VISIT, R.drawable.ic_baseline_local_activity);

        drawableResourcesByOptionsItem.put(OptionsItem.ENABLE_EDITING, R.drawable.ic_baseline_create);
        drawableResourcesByOptionsItem.put(OptionsItem.DISABLE_EDITING, R.drawable.ic_baseline_block);

        return drawableResourcesByOptionsItem;
    }


    public OptionsMenuAgent add(OptionsItem optionsItem)
    {
        this.optionsItemsToAdd.add(optionsItem);

        return this;
    }


    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding [%d] Item(s) to OptionsMenu", this.optionsItemsToAdd.size()));

        Menu subMenuSortBy = null;
        Menu subMenuGroupBy = null;

        for(OptionsItem optionsItem : this.optionsItemsToAdd)
        {
            switch(optionsItem)
            {
                case HELP:
                    addHelpToMenu(menu);
                    break;

                case SORT:
                    this.createMenuSort(optionsItem, menu);
                    this.submenuByOptionsItem.put(optionsItem, OptionsItem.SORT);
                    break;

                case SORT_BY_YEAR:
                case SORT_BY_NAME:
                case SORT_BY_LOCATION:
                case SORT_BY_ATTRACTION_CATEGORY:
                case SORT_BY_MANUFACTURER:
                {
                    if(subMenuSortBy == null)
                    {
                        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.create:: adding subMenu <sort by>");
                        subMenuSortBy = menu.addSubMenu(OptionsItem.SORT_BY.ordinal(), OptionsItem.SORT_BY.ordinal(), Menu.NONE, R.string.selection_sort_by);
                    }
                    this.createMenuSort(optionsItem, subMenuSortBy);
                    this.submenuByOptionsItem.put(optionsItem, OptionsItem.SORT_BY);
                    break;
                }

                case GROUP_BY_LOCATION:
                case GROUP_BY_ATTRACTION_CATEGORY:
                case GROUP_BY_MANUFACTURER:
                case GROUP_BY_STATUS:
                {
                    if(subMenuGroupBy == null)
                    {
                        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.create:: adding submenu <group by>");
                        subMenuGroupBy = menu.addSubMenu(OptionsItem.GROUP_BY.ordinal(), OptionsItem.GROUP_BY.ordinal(), Menu.NONE, R.string.selection_group_by);
                    }
                    this.addItemToSubMenu(optionsItem, subMenuGroupBy);
                    this.submenuByOptionsItem.put(optionsItem, OptionsItem.GROUP_BY);
                    break;
                }

                case GO_TO_CURRENT_VISIT:
                case ENABLE_EDITING:
                case DISABLE_EDITING:
                    this.addActionItemToMenu(optionsItem, menu);
                    break;

                default:
                    this.addItemToMenu(optionsItem, menu);
                    break;
            }
        }

        this.optionsItemsToAdd.clear();
    }

    private void addHelpToMenu(Menu menu)
    {
        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.addHelpToSubMenu:: adding HELP");
        menu.add(Menu.NONE, OptionsItem.HELP.ordinal(), 1, R.string.selection_help); // 1 - represents the order: as all other selections are 0 HELP should always be sorted to the bottom
    }

    private void createMenuSort(OptionsItem optionsItem, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.createMenuSort:: adding SubMenu [%s]", optionsItem));
        Menu subMenu = menu.addSubMenu(optionsItem.ordinal(), optionsItem.ordinal(), Menu.NONE, this.stringResourcesByOptionsItem.get(optionsItem));

        switch(optionsItem)
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

    private void addSortAscendingToSubMenu(OptionsItem optionsItem, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSortAscendingToSubMenu:: adding [%s]", optionsItem));
        subMenu.add(Menu.NONE, optionsItem.ordinal(), Menu.NONE, R.string.selection_sort_ascending);
    }

    private void addSortDescendingToSubMenu(OptionsItem optionsItem, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSortDescendingToSubMenu:: adding [%s]", optionsItem));
        subMenu.add(Menu.NONE, optionsItem.ordinal(), Menu.NONE, R.string.selection_sort_descending);
    }

    private void addItemToSubMenu(OptionsItem optionsItem, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addItemToSubMenu:: adding [%s]", optionsItem));
        subMenu.add(optionsItem.ordinal(), optionsItem.ordinal(), Menu.NONE, this.stringResourcesByOptionsItem.get(optionsItem));
    }

    private void addItemToMenu(OptionsItem optionsItem, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addItemToMenu:: adding [%s]", optionsItem));
        menu.add(Menu.NONE, optionsItem.ordinal(), Menu.NONE, this.stringResourcesByOptionsItem.get(optionsItem));
    }

    private void addActionItemToMenu(OptionsItem optionsItem, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addActionItemToMenu:: adding [%s]", optionsItem));

        menu.add(Menu.NONE, optionsItem.ordinal(), Menu.NONE, this.stringResourcesByOptionsItem.get(optionsItem))
                .setIcon(DrawableProvider.getColoredDrawable(this.drawableResourcesByOptionsItem.get(optionsItem), R.color.white))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    public OptionsMenuAgent setEnabled(OptionsItem optionsItem, boolean setEnabled)
    {
        this.setEnabledByOptionsItem.put(optionsItem, setEnabled);

        return this;
    }

    public OptionsMenuAgent setVisible(OptionsItem optionsItem, boolean setVisible)
    {
        this.setVisibleByOptionsItem.put(optionsItem, setVisible);

        return this;
    }

    public void prepare(Menu menu)
    {

        for(OptionsItem optionsItem : this.setEnabledByOptionsItem.keySet())
        {
            MenuItem menuItem = menu.findItem(optionsItem.ordinal());
            if(menuItem != null)
            {
                if(menuItem.hasSubMenu())
                {
                    if(this.submenuByOptionsItem.get(optionsItem).equals(optionsItem))
                    {
                        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<hasSubMenu>:: setting [%s] enabled [%S]", optionsItem, this.setEnabledByOptionsItem.get(optionsItem)));

                        menuItem.setEnabled(this.setEnabledByOptionsItem.get(optionsItem));
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<hasSubMenu>:: setting [%s][%s] enabled [%S]",
                                this.submenuByOptionsItem.get(optionsItem), optionsItem, this.setEnabledByOptionsItem.get(optionsItem)));

                        menuItem = menu.findItem(this.submenuByOptionsItem.get(optionsItem).ordinal());
                        if(menuItem != null)
                        {
                            menuItem.getSubMenu().setGroupEnabled(optionsItem.ordinal(), this.setEnabledByOptionsItem.get(optionsItem));
                        }
                    }
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: setting [%s] enabled [%S]", optionsItem, this.setEnabledByOptionsItem.get(optionsItem)));
                    menuItem.setEnabled(this.setEnabledByOptionsItem.get(optionsItem));
                }
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<setEnable>:: MenuItem [%s] not found", optionsItem));
            }
        }

        for(OptionsItem optionsItem : this.setVisibleByOptionsItem.keySet())
        {
            MenuItem menuItem = menu.findItem(optionsItem.ordinal());
            if(menuItem != null)
            {
                Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: setting [%s] visible [%S]", optionsItem, this.setVisibleByOptionsItem.get(optionsItem)));
                menuItem.setVisible(this.setVisibleByOptionsItem.get(optionsItem));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<setVisible>:: MenuItem [%s] not found", optionsItem));
            }
        }

        this.setEnabledByOptionsItem.clear();
        this.setVisibleByOptionsItem.clear();
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
            Log.e(Constants.LOG_TAG, "OptionsMenuAgent.handleOptionsItemSelected:: OptionsItem [%s] not valid");
            return false;
        }
    }


    private enum OptionsItem
    {
        NO_FUNCTION,


        //NORMAL MENU ITEM

        HELP,

        EXPAND_ALL,
        COLLAPSE_ALL,


        SORT,

        SORT_ASCENDING,
        SORT_DESCENDING,

        SORT_ATTRACTION_CATEGORIES,
        SORT_MANUFACTURERS,
        SORT_STATUSES,

//        SORT_LOCATIONS,
//        SORT_PARKS,
//        SORT_ATTRACTIONS,

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


        //ACTION MENU ITEM

        GO_TO_CURRENT_VISIT,
        ENABLE_EDITING,
        DISABLE_EDITING,


        //POPUP
//        EDIT,
//        DELETE,
//        REMOVE,
//        RELOCATE,

//        ASSIGN_TO_ATTRACTIONS,
//        SET_AS_DEFAULT,


//        CREATE_LOCATION,
//        CREATE_PARK,
    }
}



