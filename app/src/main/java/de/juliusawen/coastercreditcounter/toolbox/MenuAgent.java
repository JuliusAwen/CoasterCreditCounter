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

    private Map<Selection, Integer> flatMenuItemStringResourcesBySelection;
    private Map<Selection, Integer> deepMenuItemStringResourcesBySelection;

    private List<Selection> optionsMenuFlatSelections;
    private List<Selection> optionsMenuDeepSelections;
    private List<Selection> optionsMenuActionSelections;

    private List<Selection> popupMenuFlatSelections;
    private List<Selection> popupMenuDeepSelections;

    private Map<Selection, Pair<Boolean, Boolean>> addAndSetEnabledPairsByMenuItemSelection;

    public MenuAgent(MenuType menuType)
    {
        this.menuType = menuType;

        this.flatMenuItemStringResourcesBySelection = this.createFlatMenuItemsStringResourcesByMenuItem();
        this.deepMenuItemStringResourcesBySelection = this.createDeepMenuItemsStringResourcesByMenuItem();

        this.optionsMenuFlatSelections = this.createOptionsMenuFlatSelections();
        this.optionsMenuDeepSelections = this.createOptionsMenuDeepSelections();
        this.optionsMenuActionSelections = this.createOptionsMenuActionSelections();

        this.popupMenuFlatSelections = this.createPopupMenuFlatSelections();
        this.popupMenuDeepSelections = this.createPopupMenuDeepSelections();

        this.initializeAddAndSetEnabledPairsBySelection();
    }

    // region initialization

    private Map<Selection, Integer> createFlatMenuItemsStringResourcesByMenuItem()
    {
        Map<Selection, Integer> flatMenuItemStringResourcesByMenuItem = new HashMap<>();

        flatMenuItemStringResourcesByMenuItem.put(Selection.EXPAND_ALL, R.string.flat_menu_item_expand_all);
        flatMenuItemStringResourcesByMenuItem.put(Selection.COLLAPSE_ALL, R.string.flat_menu_item_collapse_all);

        flatMenuItemStringResourcesByMenuItem.put(Selection.GROUP_BY_LOCATION, R.string.flat_menu_item_group_by_location);
        flatMenuItemStringResourcesByMenuItem.put(Selection.GROUP_BY_ATTRACTION_CATEGORY, R.string.flat_menu_item_group_by_attraction_category);
        flatMenuItemStringResourcesByMenuItem.put(Selection.GROUP_BY_MANUFACTURER, R.string.flat_menu_item_group_by_manufacturer);
        flatMenuItemStringResourcesByMenuItem.put(Selection.GROUP_BY_STATUS, R.string.flat_menu_item_group_by_status);

        return flatMenuItemStringResourcesByMenuItem;
    }

    private Map<Selection, Integer> createDeepMenuItemsStringResourcesByMenuItem()
    {
        Map<Selection, Integer> deepMenuItemStringResourcesByMenuItem = new HashMap<>();

        deepMenuItemStringResourcesByMenuItem.put(Selection.GROUP_BY_LOCATION, R.string.deep_menu_item_group_by_location);
        deepMenuItemStringResourcesByMenuItem.put(Selection.GROUP_BY_ATTRACTION_CATEGORY, R.string.deep_menu_item_group_by_attraction_category);
        deepMenuItemStringResourcesByMenuItem.put(Selection.GROUP_BY_MANUFACTURER, R.string.deep_menu_item_group_by_manufacturer);
        deepMenuItemStringResourcesByMenuItem.put(Selection.GROUP_BY_STATUS, R.string.deep_menu_item_group_by_status);

        return deepMenuItemStringResourcesByMenuItem;
    }

    private List<Selection> createOptionsMenuFlatSelections()
    {
        List<Selection> optionsMenuFlatSelections = new LinkedList<>();

//        optionsMenuFlatSelections.add(Selection.SORT);

        optionsMenuFlatSelections.add(Selection.EXPAND_ALL);
        optionsMenuFlatSelections.add(Selection.COLLAPSE_ALL);

        return optionsMenuFlatSelections;
    }

    private List<Selection> createOptionsMenuDeepSelections()
    {
        List<Selection> optionsMenuDeepSelections = new LinkedList<>();

//        optionsMenuDeepSelections.add(Selection.SORT_BY);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_NAME_ASCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_NAME_DESCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_YEAR_ASCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_YEAR_DESCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_LOCATION_ASCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_LOCATION_DESCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_MANUFACTURER_ASCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_MANUFACTURER_DESCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_ATTRACTION_CATEGORY_ASCENDING);
//        optionsMenuDeepSelections.add(Selection.SORT_BY_ATTRACTION_CATEGORY_DESCENDING);

        optionsMenuDeepSelections.add(Selection.GROUP_BY_LOCATION);
        optionsMenuDeepSelections.add(Selection.GROUP_BY_ATTRACTION_CATEGORY);
        optionsMenuDeepSelections.add(Selection.GROUP_BY_MANUFACTURER);
        optionsMenuDeepSelections.add(Selection.GROUP_BY_STATUS);

        return optionsMenuDeepSelections;
    }

    private List<Selection> createOptionsMenuActionSelections()
    {
        List<Selection> optionsMenuActionSelections = new LinkedList<>();

//        optionsMenuActionSelections.add(Selection.SHORTCUT_TO_CURRENT_VISIT);
//        optionsMenuActionSelections.add(Selection.ENABLE_EDITING);
//        optionsMenuActionSelections.add(Selection.DISABLE_EDITING);

        return optionsMenuActionSelections;
    }

    private List<Selection> createPopupMenuFlatSelections()
    {
        List<Selection> popupMenuFlatSelections = new LinkedList<>();

//        this.popupMenuFlatSelections.add(Selection.EDIT);
//        this.popupMenuFlatSelections.add(Selection.DELETE);
//        this.popupMenuFlatSelections.add(Selection.REMOVE);
//        this.popupMenuFlatSelections.add(Selection.RELOCATE);
//        this.popupMenuFlatSelections.add(Selection.ASSIGN_TO_ATTRACTIONS);
//        this.popupMenuFlatSelections.add(Selection.SET_AS_DEFAULT);

        return popupMenuFlatSelections;
    }

    private List<Selection> createPopupMenuDeepSelections()
    {
        List<Selection> popupMenuDeepSelections = new LinkedList<>();

//        this.popupMenuDeepSelections.add(Selection.SORT_LOCATIONS);
//        this.popupMenuDeepSelections.add(Selection.SORT_PARKS);
//        this.popupMenuDeepSelections.add(Selection.CREATE_LOCATION);
//        this.popupMenuDeepSelections.add(Selection.CREATE_PARK);

        return popupMenuDeepSelections;
    }

    private void initializeAddAndSetEnabledPairsBySelection()
    {
        this.addAndSetEnabledPairsByMenuItemSelection = new HashMap<>();
        this.initializeAllSelectionGroups();
    }

    private void initializeAllSelectionGroups()
    {
        this.initializeSelectionGroup(this.optionsMenuFlatSelections);
        this.initializeSelectionGroup(this.optionsMenuDeepSelections);
        this.initializeSelectionGroup(this.optionsMenuActionSelections);

        this.initializeSelectionGroup(this.popupMenuFlatSelections);
        this.initializeSelectionGroup(this.popupMenuDeepSelections);
    }

    private void initializeSelectionGroup(List<Selection> selections)
    {
        for(Selection selection : selections)
        {
            this.addAndSetEnabledPairsByMenuItemSelection.put(selection, new Pair<>(false, false));
        }
    }

    // endregion initialization

    // region setter

    public MenuAgent addMenuItemExpandAll(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItemSelection.put(Selection.EXPAND_ALL, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemCollapseAll(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItemSelection.put(Selection.COLLAPSE_ALL, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemGroupByLocation(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItemSelection.put(Selection.GROUP_BY_LOCATION, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemGroupByAttractionCategory(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItemSelection.put(Selection.GROUP_BY_ATTRACTION_CATEGORY, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemGroupByManufacturer(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItemSelection.put(Selection.GROUP_BY_MANUFACTURER, new Pair<>(true, setEnabled));
        return this;
    }

    public MenuAgent addMenuItemGroupByStatus(boolean setEnabled)
    {
        this.addAndSetEnabledPairsByMenuItemSelection.put(Selection.GROUP_BY_STATUS, new Pair<>(true, setEnabled));
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

                menu.add(Menu.NONE, Selection.HELP.ordinal(), Menu.NONE, R.string.flat_menu_item_help);
                break;

            case POPUP_MENU:

                break;
        }

        this.initializeAllSelectionGroups();
    }

    private void addFlatItemsToOptionsMenu(Menu menu)
    {
        this.addItemsToMenu(this.optionsMenuFlatSelections, menu, false);
    }

    private void addDeepItemsToOptionsMenu(Menu menu)
    {
        this.addGroupByItemsToOptionsMenu(menu);
    }

    private void addGroupByItemsToOptionsMenu(Menu menu)
    {
        List<Selection> groupByItemsToAdd = new LinkedList<>();

        if(this.addAndSetEnabledPairsByMenuItemSelection.get(Selection.GROUP_BY_LOCATION).first)
        {
            groupByItemsToAdd.add(Selection.GROUP_BY_LOCATION);
        }

        if(this.addAndSetEnabledPairsByMenuItemSelection.get(Selection.GROUP_BY_ATTRACTION_CATEGORY).first)
        {
            groupByItemsToAdd.add(Selection.GROUP_BY_ATTRACTION_CATEGORY);
        }

        if(this.addAndSetEnabledPairsByMenuItemSelection.get(Selection.GROUP_BY_MANUFACTURER).first)
        {
            groupByItemsToAdd.add(Selection.GROUP_BY_MANUFACTURER);
        }

        if(this.addAndSetEnabledPairsByMenuItemSelection.get(Selection.GROUP_BY_STATUS).first)
        {
            groupByItemsToAdd.add(Selection.GROUP_BY_STATUS);
        }

        boolean menuIsDeep = groupByItemsToAdd.size() > 1;

        Menu groupByMenu = menuIsDeep
                ? menu.addSubMenu(R.string.deep_menu_item_group_by)
                : menu;

        this.addItemsToMenu(groupByItemsToAdd, groupByMenu, menuIsDeep);
    }

    private void addItemsToMenu(List<Selection> selections, Menu menu, boolean menuIsDeep)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemsToMenu:: adding [%d] item(s) to menu (isDeep = [%S])", selections.size(), menuIsDeep));

        for(Selection selection : selections)
        {
            Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemsToMenu:: adding item [%s]", selection)); //Todo: set to verbose!

            if(this.addAndSetEnabledPairsByMenuItemSelection.get(selection).first)
            {
                menu.add(selection.ordinal(), selection.ordinal(), Menu.NONE, menuIsDeep
                        ? this.deepMenuItemStringResourcesBySelection.get(selection)
                        : this.flatMenuItemStringResourcesBySelection.get(selection))
                        .setEnabled(this.addAndSetEnabledPairsByMenuItemSelection.get(selection).second);
            }
        }
    }


    public boolean handleMenuItemSelected(android.view.MenuItem item, IMenuAgentClient client)
    {
        Log.i(Constants.LOG_TAG, String.format("MenuAgent.handleMenuItemSelected:: Selection [%s] selected", Selection.values()[item.getItemId()].toString()));

        switch(Selection.values()[item.getItemId()])
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

    public enum Selection
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
        return Selection.values()[itemId].toString();
    }
}



