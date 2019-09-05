package de.juliusawen.coastercreditcounter.toolbox;

import android.util.Log;
import android.view.Menu;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class MenuAgent
{
    private MenuType menuType;
    private Map<Selection, Integer> stringResourcesBySelection;

    Map<Selection, Boolean> setEnabledBySelectionsToAdd;

    public MenuAgent(MenuType menuType)
    {
        this.menuType = menuType;
        this.setEnabledBySelectionsToAdd = new LinkedHashMap<>();

        this.stringResourcesBySelection = this.createStringResourcesBySelectionMap();
    }

    // region initialization

    private Map<Selection, Integer> createStringResourcesBySelectionMap()
    {
        Map<Selection, Integer> stringResourcesBySelection = new HashMap<>();

        stringResourcesBySelection.put(Selection.EXPAND_ALL, R.string.flat_menu_item_expand_all);
        stringResourcesBySelection.put(Selection.COLLAPSE_ALL, R.string.flat_menu_item_collapse_all);

        stringResourcesBySelection.put(Selection.GROUP_BY_LOCATION, R.string.deep_menu_item_group_by_location);
        stringResourcesBySelection.put(Selection.GROUP_BY_ATTRACTION_CATEGORY, R.string.deep_menu_item_group_by_attraction_category);
        stringResourcesBySelection.put(Selection.GROUP_BY_MANUFACTURER, R.string.deep_menu_item_group_by_manufacturer);
        stringResourcesBySelection.put(Selection.GROUP_BY_STATUS, R.string.deep_menu_item_group_by_status);

        return stringResourcesBySelection;
    }

    // endregion initialization

    // region setter

    public MenuAgent addMenuItemExpandAll(boolean setEnabled)
    {
        this.setEnabledBySelectionsToAdd.put(Selection.EXPAND_ALL, setEnabled);
        return this;
    }

    public MenuAgent addMenuItemCollapseAll(boolean setEnabled)
    {
        this.setEnabledBySelectionsToAdd.put(Selection.COLLAPSE_ALL, setEnabled);
        return this;
    }

    public MenuAgent addMenuItemGroupByLocation(boolean setEnabled)
    {
        this.setEnabledBySelectionsToAdd.put(Selection.GROUP_BY_LOCATION, setEnabled);
        return this;
    }

    public MenuAgent addMenuItemGroupByAttractionCategory(boolean setEnabled)
    {
        this.setEnabledBySelectionsToAdd.put(Selection.GROUP_BY_ATTRACTION_CATEGORY, setEnabled);
        return this;
    }

    public MenuAgent addMenuItemGroupByManufacturer(boolean setEnabled)
    {
        this.setEnabledBySelectionsToAdd.put(Selection.GROUP_BY_MANUFACTURER, setEnabled);
        return this;
    }

    public MenuAgent addMenuItemGroupByStatus(boolean setEnabled)
    {
        this.setEnabledBySelectionsToAdd.put(Selection.GROUP_BY_STATUS, setEnabled);
        return this;
    }



    // endregion setter

    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.create:: creating menu for type [%s]", this.menuType));

        switch(this.menuType)
        {
            case OPTIONS_MENU:

                this.addItemsToMenu(menu);
                menu.add(Menu.NONE, Selection.HELP.ordinal(), Menu.NONE, R.string.flat_menu_item_help);
                break;

            case POPUP_MENU:

                break;
        }

        this.setEnabledBySelectionsToAdd.clear();
    }

    private void addItemsToMenu(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemsToMenu:: adding [%d] item(s) to menu", this.setEnabledBySelectionsToAdd.size()));

        Menu menuToAddTo;
        Menu submenuGroupBy = null;

        for(Selection selection : this.setEnabledBySelectionsToAdd.keySet())
        {
            menuToAddTo = menu;

            if(selection.equals(Selection.GROUP_BY_LOCATION)
                    || selection.equals(Selection.GROUP_BY_ATTRACTION_CATEGORY)
                    || selection.equals(Selection.GROUP_BY_MANUFACTURER)
                    || selection.equals(Selection.GROUP_BY_STATUS))
            {
                if(submenuGroupBy == null)
                {
                    Log.d(Constants.LOG_TAG, "MenuAgent.addItemsToMenu:: adding submenu <group by>"); //Todo: set to verbose
                    submenuGroupBy = menu.addSubMenu(R.string.deep_menu_item_group_by);
                }

                menuToAddTo = submenuGroupBy;
            }

            Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemsToMenu:: adding item [%s] - setEnabled[%S]", selection, this.setEnabledBySelectionsToAdd.get(selection))); //Todo: set to verbose
            menuToAddTo.add(selection.ordinal(), selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection))
                    .setEnabled(this.setEnabledBySelectionsToAdd.get(selection));
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



