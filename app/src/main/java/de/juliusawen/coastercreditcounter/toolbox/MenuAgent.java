package de.juliusawen.coastercreditcounter.toolbox;

import android.util.Log;
import android.util.Pair;
import android.view.Menu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class MenuAgent
{
    private MenuType menuType;

    private Map<MenuItem, Integer> flatMenuItemStringResourcesByMenuItem;
    private Map<MenuItem, Integer> deepMenuItemStringResourcesByMenuItem;

    private List<MenuItem> optionsMenuFlatMenuItems;
    private List<MenuItem> optionsMenuDeepMenuItems;
    private List<MenuItem> optionsMenuActionMenuItems;

    private List<MenuItem> popupMenuFlatMenuItems;
    private List<MenuItem> popupMenuDeepMenuItems;

    private Map<MenuItem, Pair<Boolean, Boolean>> addAndSetEnabledPairsByMenuItem;

    public MenuAgent(MenuType menuType)
    {
        this.menuType = menuType;

        this.flatMenuItemStringResourcesByMenuItem = this.createFlatMenuItemsStringResourcesByMenuItem();
        this.deepMenuItemStringResourcesByMenuItem = this.createDeepMenuItemsStringResourcesByMenuItem();

        this.optionsMenuFlatMenuItems = this.createOptionsMenuFlatMenuItems();
        this.optionsMenuDeepMenuItems = this.createOptionsMenuDeepMenuItems();
        this.optionsMenuActionMenuItems = this.createOptionsMenuActionMenuItems();

        this.popupMenuFlatMenuItems = this.createPopupMenuFlatMenuItems();
        this.popupMenuDeepMenuItems = this.createPopupMenuDeepMenuItems();

        this.initializeAddAndSetEnabledPairsByMenuItem();
    }

    // region initialization

    private Map<MenuItem, Integer> createFlatMenuItemsStringResourcesByMenuItem()
    {
        Map<MenuItem, Integer> flatMenuItemStringResourcesByMenuItem = new HashMap<>();

        flatMenuItemStringResourcesByMenuItem.put(MenuItem.EXPAND_ALL, R.string.flat_menu_item_expand_all);
        flatMenuItemStringResourcesByMenuItem.put(MenuItem.COLLAPSE_ALL, R.string.flat_menu_item_collapse_all);

        flatMenuItemStringResourcesByMenuItem.put(MenuItem.GROUP_BY_LOCATION, R.string.flat_menu_item_group_by_location);
        flatMenuItemStringResourcesByMenuItem.put(MenuItem.GROUP_BY_ATTRACTION_CATEGORY, R.string.flat_menu_item_group_by_attraction_category);
        flatMenuItemStringResourcesByMenuItem.put(MenuItem.GROUP_BY_MANUFACTURER, R.string.flat_menu_item_group_by_manufacturer);
        flatMenuItemStringResourcesByMenuItem.put(MenuItem.GROUP_BY_STATUS, R.string.flat_menu_item_group_by_status);

        return flatMenuItemStringResourcesByMenuItem;
    }

    private Map<MenuItem, Integer> createDeepMenuItemsStringResourcesByMenuItem()
    {
        Map<MenuItem, Integer> deepMenuItemStringResourcesByMenuItem = new HashMap<>();

        deepMenuItemStringResourcesByMenuItem.put(MenuItem.GROUP_BY_LOCATION, R.string.deep_menu_item_group_by_location);
        deepMenuItemStringResourcesByMenuItem.put(MenuItem.GROUP_BY_ATTRACTION_CATEGORY, R.string.deep_menu_item_group_by_attraction_category);
        deepMenuItemStringResourcesByMenuItem.put(MenuItem.GROUP_BY_MANUFACTURER, R.string.deep_menu_item_group_by_manufacturer);
        deepMenuItemStringResourcesByMenuItem.put(MenuItem.GROUP_BY_STATUS, R.string.deep_menu_item_group_by_status);

        return deepMenuItemStringResourcesByMenuItem;
    }

    private List<MenuItem> createOptionsMenuFlatMenuItems()
    {
        List<MenuItem> optionsMenuFlatMenuItems = new LinkedList<>();

//        optionsMenuFlatMenuItems.add(MenuItem.SORT);

        optionsMenuFlatMenuItems.add(MenuItem.EXPAND_ALL);
        optionsMenuFlatMenuItems.add(MenuItem.COLLAPSE_ALL);

        return optionsMenuFlatMenuItems;
    }

    private List<MenuItem> createOptionsMenuDeepMenuItems()
    {
        List<MenuItem> optionsMenuDeepMenuItems = new LinkedList<>();

//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_NAME_ASCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_NAME_DESCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_YEAR_ASCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_YEAR_DESCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_LOCATION_ASCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_LOCATION_DESCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_MANUFACTURER_ASCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_MANUFACTURER_DESCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_ATTRACTION_CATEGORY_ASCENDING);
//        optionsMenuDeepMenuItems.add(MenuItem.SORT_BY_ATTRACTION_CATEGORY_DESCENDING);

        optionsMenuDeepMenuItems.add(MenuItem.GROUP_BY_LOCATION);
        optionsMenuDeepMenuItems.add(MenuItem.GROUP_BY_ATTRACTION_CATEGORY);
        optionsMenuDeepMenuItems.add(MenuItem.GROUP_BY_MANUFACTURER);
        optionsMenuDeepMenuItems.add(MenuItem.GROUP_BY_STATUS);

        return optionsMenuDeepMenuItems;
    }

    private List<MenuItem> createOptionsMenuActionMenuItems()
    {
        List<MenuItem> optionsMenuActionMenuItems = new LinkedList<>();

//        optionsMenuActionMenuItems.add(MenuItem.SHORTCUT_TO_CURRENT_VISIT);
//        optionsMenuActionMenuItems.add(MenuItem.ENABLE_EDITING);
//        optionsMenuActionMenuItems.add(MenuItem.DISABLE_EDITING);

        return optionsMenuActionMenuItems;
    }

    private List<MenuItem> createPopupMenuFlatMenuItems()
    {
        List<MenuItem> popupMenuFlatMenuItems = new LinkedList<>();

//        this.popupMenuFlatMenuItems.add(MenuItem.EDIT);
//        this.popupMenuFlatMenuItems.add(MenuItem.DELETE);
//        this.popupMenuFlatMenuItems.add(MenuItem.REMOVE);
//        this.popupMenuFlatMenuItems.add(MenuItem.RELOCATE);
//        this.popupMenuFlatMenuItems.add(MenuItem.ASSIGN_TO_ATTRACTIONS);
//        this.popupMenuFlatMenuItems.add(MenuItem.SET_AS_DEFAULT);

        return popupMenuFlatMenuItems;
    }

    private List<MenuItem> createPopupMenuDeepMenuItems()
    {
        List<MenuItem> popupMenuDeepMenuItems = new LinkedList<>();

//        this.popupMenuDeepMenuItems.add(MenuItem.SORT_LOCATIONS);
//        this.popupMenuDeepMenuItems.add(MenuItem.SORT_PARKS);
//        this.popupMenuDeepMenuItems.add(MenuItem.CREATE_LOCATION);
//        this.popupMenuDeepMenuItems.add(MenuItem.CREATE_PARK);

        return popupMenuDeepMenuItems;
    }

    private void initializeAddAndSetEnabledPairsByMenuItem()
    {
        this.addAndSetEnabledPairsByMenuItem = new HashMap<>();
        this.initializeAllMenuItemGroups();
    }

    private void initializeAllMenuItemGroups()
    {
        this.initializeMenuItemGroup(this.optionsMenuFlatMenuItems);
        this.initializeMenuItemGroup(this.optionsMenuDeepMenuItems);
        this.initializeMenuItemGroup(this.optionsMenuActionMenuItems);

        this.initializeMenuItemGroup(this.popupMenuFlatMenuItems);
        this.initializeMenuItemGroup(this.popupMenuDeepMenuItems);
    }

    private void initializeMenuItemGroup(List<MenuItem> menuItems)
    {
        for(MenuItem menuItem : menuItems)
        {
            this.addAndSetEnabledPairsByMenuItem.put(menuItem, new Pair<>(false, false));
        }
    }

    // endregion initialization

    // region setter

    public MenuAgent addItemExpandAllToOptionsMenu(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItem.put(MenuItem.EXPAND_ALL, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemCollapseAll(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItem.put(MenuItem.COLLAPSE_ALL, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemGroupByLocation(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItem.put(MenuItem.GROUP_BY_LOCATION, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemGroupByAttractionCategory(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItem.put(MenuItem.GROUP_BY_ATTRACTION_CATEGORY, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemGroupByManufacturer(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItem.put(MenuItem.GROUP_BY_MANUFACTURER, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemGroupByStatus(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItem.put(MenuItem.GROUP_BY_STATUS, new Pair<>(true, setEnabled));
        return this;
    }



    // endregion setter

    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.create:: creating menu for type [%s]", this.menuType));

        switch(this.menuType)
        {
            case OPTIONS_MENU:

                this.addDeepItemsToOptionsMenu(menu);
                this.addFlatItemsToOptionsMenu(menu);

                menu.add(Menu.NONE, MenuItem.HELP.ordinal(), Menu.NONE, R.string.flat_menu_item_help);
                break;

            case POPUP_MENU:

                break;
        }

        this.initializeAllMenuItemGroups();
    }

    private void addFlatItemsToOptionsMenu(Menu menu)
    {
        this.addItemsToMenu(this.optionsMenuFlatMenuItems, menu, false);
    }

    private void addDeepItemsToOptionsMenu(Menu menu)
    {
        this.addGroupByItemsToOptionsMenu(menu);
    }

    private void addGroupByItemsToOptionsMenu(Menu menu)
    {
        List<MenuItem> groupByItemsToAdd = new LinkedList<>();

        if(this.addAndSetEnabledPairsByMenuItem.get(MenuItem.GROUP_BY_LOCATION).first)
        {
            groupByItemsToAdd.add(MenuItem.GROUP_BY_LOCATION);
        }

        if(this.addAndSetEnabledPairsByMenuItem.get(MenuItem.GROUP_BY_ATTRACTION_CATEGORY).first)
        {
            groupByItemsToAdd.add(MenuItem.GROUP_BY_ATTRACTION_CATEGORY);
        }

        if(this.addAndSetEnabledPairsByMenuItem.get(MenuItem.GROUP_BY_MANUFACTURER).first)
        {
            groupByItemsToAdd.add(MenuItem.GROUP_BY_MANUFACTURER);
        }

        if(this.addAndSetEnabledPairsByMenuItem.get(MenuItem.GROUP_BY_STATUS).first)
        {
            groupByItemsToAdd.add(MenuItem.GROUP_BY_STATUS);
        }

        boolean menuIsDeep = groupByItemsToAdd.size() > 1;

        Menu groupByMenu = menuIsDeep
                ? menu.addSubMenu(R.string.deep_menu_item_group_by)
                : menu;

        this.addItemsToMenu(groupByItemsToAdd, groupByMenu, menuIsDeep);
    }

    private void addItemsToMenu(List<MenuItem> menuItems, Menu menu, boolean menuIsDeep)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemsToMenu:: adding [%d] item(s) to menu (isDeep = [%S])", menuItems.size(), menuIsDeep));

        for(MenuItem menuItem : menuItems)
        {
            Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemsToMenu:: adding item [%s]", menuItem)); //Todo: set to verbose!

            if(this.addAndSetEnabledPairsByMenuItem.get(menuItem).first)
            {
                menu.add(menuItem.ordinal(), menuItem.ordinal(), Menu.NONE, menuIsDeep
                        ? this.deepMenuItemStringResourcesByMenuItem.get(menuItem)
                        : this.flatMenuItemStringResourcesByMenuItem.get(menuItem))
                        .setEnabled(this.addAndSetEnabledPairsByMenuItem.get(menuItem).second);
            }
        }
    }


    public boolean handleMenuItemSelected(android.view.MenuItem item, IMenuAgentClient client)
    {
        Log.i(Constants.LOG_TAG, String.format("MenuAgent.handleMenuItemSelected:: MenuItem [%s] selected", MenuItem.values()[item.getItemId()].toString()));

        switch(MenuItem.values()[item.getItemId()])
        {
            case HELP:
                return client.handleMenuItemHelpSelected();

            case EXPAND_ALL:
                return client.handleMenuItemExpandAllSelected();

            case COLLAPSE_ALL:
                return client.handleMenuItemCollapseAllSelected();

            case GROUP_BY_LOCATION:
                return client.handleMenuItemGroupByLocationSelected();

            case GROUP_BY_ATTRACTION_CATEGORY:
                return client.handleMenuItemGroupByAttractionCategorySelected();

            case GROUP_BY_MANUFACTURER:
                return client.handleMenuItemGroupByManufacturerSelected();

            case GROUP_BY_STATUS:
                return client.handleMenuItemGroupByStatusSelected();



                default:
                    return false;
        }
    }



    public enum MenuType
    {
        OPTIONS_MENU,
        POPUP_MENU
    }

    public enum MenuItem
    {
        NO_FUNCTION,


        //OptionsMenu FLAT

        SORT,
        SORT_LOCATIONS,
        SORT_PARKS,
        SORT_ATTRACTIONS,

        EXPAND_ALL,
        COLLAPSE_ALL,

        HELP,


        //OptionMenus DEEP

        SORT_BY,
        SORT_BY_NAME_ASCENDING,
        SORT_BY_NAME_DESCENDING,
        SORT_BY_YEAR_ASCENDING,
        SORT_BY_YEAR_DESCENDING,
        SORT_BY_LOCATION_ASCENDING,
        SORT_BY_LOCATION_DESCENDING,
        SORT_BY_MANUFACTURER_ASCENDING,
        SORT_BY_MANUFACTURER_DESCENDING,
        SORT_BY_ATTRACTION_CATEGORY_ASCENDING,
        SORT_BY_ATTRACTION_CATEGORY_DESCENDING,

        GROUP_BY_LOCATION,
        GROUP_BY_MANUFACTURER,
        GROUP_BY_ATTRACTION_CATEGORY,
        GROUP_BY_STATUS,


        //OptionsMenu ACTION

        SHORTCUT_TO_CURRENT_VISIT,
        ENABLE_EDITING,
        DISABLE_EDITING,


        //PopupMenu FLAT

        EDIT,
        DELETE,
        REMOVE,
        RELOCATE,

        ASSIGN_TO_ATTRACTIONS,
        SET_AS_DEFAULT,


        //PopupMenu DEEP

        CREATE_LOCATION,
        CREATE_PARK,
    }

    public static String getMenuItemString(int itemId)
    {
        return MenuItem.values()[itemId].toString();
    }
}



