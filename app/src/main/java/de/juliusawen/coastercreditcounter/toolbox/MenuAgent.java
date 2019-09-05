package de.juliusawen.coastercreditcounter.toolbox;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class MenuAgent
{
    public static final int HELP = Selection.HELP.ordinal();

    public static final int EXPAND_ALL = Selection.EXPAND_ALL.ordinal();
    public static final int COLLAPSE_ALL = Selection.COLLAPSE_ALL.ordinal();

    public static final int GROUP_BY_LOCATION = Selection.GROUP_BY_LOCATION.ordinal();
    public static final int GROUP_BY_ATTRACTION_CATEGORY = Selection.GROUP_BY_ATTRACTION_CATEGORY.ordinal();
    public static final int GROUP_BY_MANUFACTURER = Selection.GROUP_BY_MANUFACTURER.ordinal();
    public static final int GROUP_BY_STATUS = Selection.GROUP_BY_STATUS.ordinal();


    private MenuType menuType;
    private Map<Selection, Boolean> setEnabledBySelectionsToAdd;
    private Map<Selection, Integer> stringResourcesBySelection;

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

        stringResourcesBySelection.put(Selection.HELP, R.string.selection_help);

        stringResourcesBySelection.put(Selection.EXPAND_ALL, R.string.selection_expand_all);
        stringResourcesBySelection.put(Selection.COLLAPSE_ALL, R.string.selection_collapse_all);

        stringResourcesBySelection.put(Selection.GROUP_BY_LOCATION, R.string.selection_group_by_location);
        stringResourcesBySelection.put(Selection.GROUP_BY_ATTRACTION_CATEGORY, R.string.selection_group_by_attraction_category);
        stringResourcesBySelection.put(Selection.GROUP_BY_MANUFACTURER, R.string.selection_group_by_manufacturer);
        stringResourcesBySelection.put(Selection.GROUP_BY_STATUS, R.string.selection_group_by_status);

        return stringResourcesBySelection;
    }

    // endregion initialization

    // region setter

    public MenuAgent addMenuItem(int selection)
    {
        return this.addMenuItem(selection, true);
    }

    public MenuAgent addMenuItem(int selection, boolean setEnabled)
    {
        this.setEnabledBySelectionsToAdd.put(Selection.values()[selection], setEnabled);
        return this;
    }

    // endregion setter

    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.create:: adding [%d] MenuItem(s) to [%s]", this.setEnabledBySelectionsToAdd.size(), this.menuType));

        Menu menuToAddTo;

        switch(menuType)
        {
            case OPTIONS_MENU:

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
                            Log.d(Constants.LOG_TAG, "MenuAgent.create<OptionsMenu>:: adding submenu <group by>"); //Todo: set to verbose
                            submenuGroupBy = menu.addSubMenu(R.string.selection_group_by);
                        }

                        menuToAddTo = submenuGroupBy;
                    }

                    this.addItemToMenu(selection, menuToAddTo);
                }
                break;

            case POPUP_MENU:

                break;
        }

        this.setEnabledBySelectionsToAdd.clear();
    }

    private void addItemToMenu(Selection selection, Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemToMenu:: adding MenuItem[%s] - setEnabled[%S]", selection, this.setEnabledBySelectionsToAdd.get(selection))); //Todo: set to verbose
        menu.add(selection.ordinal(), selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection))
                .setEnabled(this.setEnabledBySelectionsToAdd.get(selection));
    }


    public boolean handleMenuItemSelected(MenuItem item, IMenuAgentClient client)
    {
        if(item.getItemId() <= Selection.values().length)
        {
            Log.i(Constants.LOG_TAG, String.format("MenuAgent.handleMenuItemSelected:: MenuItem [%s] selected", Selection.values()[item.getItemId()].toString()));

            switch(Selection.values()[item.getItemId()])
            {
                case NO_FUNCTION:
                    return true;

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
        else
        {
            Log.e(Constants.LOG_TAG, "MenuAgent.handleMenuItemSelected:: Selection [%s] not valid");
            return false;
        }
    }



    public enum MenuType
    {
        OPTIONS_MENU,
        POPUP_MENU
    }

    private enum Selection
    {
        NO_FUNCTION,


        //OptionsMenu FLAT

//        SORT,
//        SORT_LOCATIONS,
//        SORT_PARKS,
//        SORT_ATTRACTIONS,

        EXPAND_ALL,
        COLLAPSE_ALL,

        HELP,


        //OptionMenus DEEP

//        SORT_BY,
//        SORT_BY_NAME_ASCENDING,
//        SORT_BY_NAME_DESCENDING,
//        SORT_BY_YEAR_ASCENDING,
//        SORT_BY_YEAR_DESCENDING,
//        SORT_BY_LOCATION_ASCENDING,
//        SORT_BY_LOCATION_DESCENDING,
//        SORT_BY_MANUFACTURER_ASCENDING,
//        SORT_BY_MANUFACTURER_DESCENDING,
//        SORT_BY_ATTRACTION_CATEGORY_ASCENDING,
//        SORT_BY_ATTRACTION_CATEGORY_DESCENDING,

        GROUP_BY_LOCATION,
        GROUP_BY_MANUFACTURER,
        GROUP_BY_ATTRACTION_CATEGORY,
        GROUP_BY_STATUS,


        //OptionsMenu ACTION

//        SHORTCUT_TO_CURRENT_VISIT,
//        ENABLE_EDITING,
//        DISABLE_EDITING,


        //PopupMenu FLAT

//        EDIT,
//        DELETE,
//        REMOVE,
//        RELOCATE,
//
//        ASSIGN_TO_ATTRACTIONS,
//        SET_AS_DEFAULT,


        //PopupMenu DEEP

//        CREATE_LOCATION,
//        CREATE_PARK,
    }

    public static String getSelectionString(int itemId)
    {
        return Selection.values()[itemId].toString();
    }
}



