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

    public static final int SORT_BY_YEAR  = Selection.SORT_BY_YEAR.ordinal();
    public static final int SORT_BY_NAME  = Selection.SORT_BY_NAME.ordinal();
    public static final int SORT_BY_LOCATION  = Selection.SORT_BY_LOCATION.ordinal();
    public static final int SORT_BY_ATTRACTION_CATEGORY = Selection.SORT_BY_ATTRACTION_CATEGORY.ordinal();
    public static final int SORT_BY_MANUFACTURER  = Selection.SORT_BY_MANUFACTURER.ordinal();

    public static final int GROUP_BY_LOCATION = Selection.GROUP_BY_LOCATION.ordinal();
    public static final int GROUP_BY_ATTRACTION_CATEGORY = Selection.GROUP_BY_ATTRACTION_CATEGORY.ordinal();
    public static final int GROUP_BY_MANUFACTURER = Selection.GROUP_BY_MANUFACTURER.ordinal();
    public static final int GROUP_BY_STATUS = Selection.GROUP_BY_STATUS.ordinal();

    public static final int GO_TO_CURRENT_VISIT = Selection.GO_TO_CURRENT_VISIT.ordinal();

    public static final int ENABLE_EDITING = Selection.ENABLE_EDITING.ordinal();
    public static final int DISABLE_EDITING = Selection.DISABLE_EDITING.ordinal();



    private MenuType menuType;

    private Map<Selection, Boolean> setEnabledBySelectionsToAdd;

    private Map<Selection, Integer> stringResourcesBySelection;
    private Map<Selection, Integer> drawableResourcesBySelection;


    public MenuAgent(MenuType menuType)
    {
        this.menuType = menuType;

        this.setEnabledBySelectionsToAdd = new LinkedHashMap<>();

        this.stringResourcesBySelection = this.createStringResourcesBySelectionMap();
        this.drawableResourcesBySelection = this.createDrawableResourcesBySelectionMap();
    }

    private Map<Selection, Integer> createStringResourcesBySelectionMap()
    {
        Map<Selection, Integer> stringResourcesBySelection = new HashMap<>();

        stringResourcesBySelection.put(Selection.HELP, R.string.selection_help);

        stringResourcesBySelection.put(Selection.EXPAND_ALL, R.string.selection_expand_all);
        stringResourcesBySelection.put(Selection.COLLAPSE_ALL, R.string.selection_collapse_all);

        stringResourcesBySelection.put(Selection.SORT_BY_YEAR, R.string.selection_sort_by_year);
        stringResourcesBySelection.put(Selection.SORT_BY_NAME, R.string.selection_sort_by_name);
        stringResourcesBySelection.put(Selection.SORT_BY_LOCATION, R.string.selection_sort_by_location);
        stringResourcesBySelection.put(Selection.SORT_BY_ATTRACTION_CATEGORY, R.string.selection_sort_by_attraction_category);
        stringResourcesBySelection.put(Selection.SORT_BY_MANUFACTURER, R.string.selection_sort_by_manufacturer);

        stringResourcesBySelection.put(Selection.GROUP_BY_LOCATION, R.string.selection_group_by_location);
        stringResourcesBySelection.put(Selection.GROUP_BY_ATTRACTION_CATEGORY, R.string.selection_group_by_attraction_category);
        stringResourcesBySelection.put(Selection.GROUP_BY_MANUFACTURER, R.string.selection_group_by_manufacturer);
        stringResourcesBySelection.put(Selection.GROUP_BY_STATUS, R.string.selection_group_by_status);

        stringResourcesBySelection.put(Selection.GO_TO_CURRENT_VISIT, R.string.selection_go_to_current_visit);

        stringResourcesBySelection.put(Selection.ENABLE_EDITING, R.string.selection_enable_editing);
        stringResourcesBySelection.put(Selection.DISABLE_EDITING, R.string.selection_disable_editing);

        return stringResourcesBySelection;
    }

    private Map<Selection, Integer> createDrawableResourcesBySelectionMap()
    {
        Map<Selection, Integer> drawableResourcesBySelection = new HashMap<>();

        drawableResourcesBySelection.put(Selection.GO_TO_CURRENT_VISIT, R.drawable.ic_baseline_local_activity);

        drawableResourcesBySelection.put(Selection.ENABLE_EDITING, R.drawable.ic_baseline_create);
        drawableResourcesBySelection.put(Selection.DISABLE_EDITING, R.drawable.ic_baseline_block);

        return drawableResourcesBySelection;
    }


    public MenuAgent addMenuItem(int selection)
    {
        return this.addMenuItem(selection, true);
    }

    public MenuAgent addMenuItem(int selection, boolean setEnabled)
    {
        this.setEnabledBySelectionsToAdd.put(Selection.values()[selection], setEnabled);
        return this;
    }


    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.create:: adding [%d] MenuItem(s) to [%s]", this.setEnabledBySelectionsToAdd.size(), this.menuType));

        menu.clear();

        switch(menuType)
        {
            case OPTIONS_MENU:
            {
                Menu subMenuSortBy = null;
                Menu subMenuGroupBy = null;

                for(Selection selection : this.setEnabledBySelectionsToAdd.keySet())
                {
                    switch(selection)
                    {
                        case SORT_BY_YEAR:
                        case SORT_BY_NAME:
                        case SORT_BY_LOCATION:
                        case SORT_BY_ATTRACTION_CATEGORY:
                        case SORT_BY_MANUFACTURER:
                        {
                            this.createMenuSortBy(selection, menu, subMenuSortBy);
                            break;
                        }

                        case GROUP_BY_LOCATION:
                        case GROUP_BY_ATTRACTION_CATEGORY:
                        case GROUP_BY_MANUFACTURER:
                        case GROUP_BY_STATUS:
                        {
                            this.createMenuGroupBy(selection, menu, subMenuGroupBy);
                            break;
                        }

                        case GO_TO_CURRENT_VISIT:
                        case ENABLE_EDITING:
                        case DISABLE_EDITING:
                            this.addActionItemToMenu(selection, menu);
                            break;

                        default:
                        {
                            this.addItemToMenu(selection, menu);
                            break;
                        }
                    }
                }
                break;
            }
            case POPUP_MENU:
            {
                break;
            }
        }

        this.setEnabledBySelectionsToAdd.clear();
    }

    private void createMenuSortBy(Selection selection, Menu menu, Menu subMenuSortBy)
    {
        if(subMenuSortBy == null)
        {
            Log.d(Constants.LOG_TAG, "MenuAgent.createMenuSortBy:: adding submenu <sort by>"); //Todo: set to verbose
            subMenuSortBy = menu.addSubMenu(Selection.SORT_BY.ordinal(), Selection.SORT_BY.ordinal(), Menu.NONE, R.string.selection_sort_by);
        }

        Log.d(Constants.LOG_TAG, String.format("MenuAgent.createMenuSortBy:: adding SubMenu [%s] - setEnabled[%S]", selection, this.setEnabledBySelectionsToAdd.get(selection))); //Todo: set to verbose
        Menu subSubMenuSortBy = subMenuSortBy.addSubMenu(selection.ordinal(), selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection));

        switch(selection)
        {
            case SORT_BY_YEAR:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_YEAR_ASCENDING, subSubMenuSortBy);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_YEAR_DESCENDING, subSubMenuSortBy);
                break;

            case SORT_BY_NAME:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_NAME_ASCENDING, subSubMenuSortBy);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_NAME_DESCENDING, subSubMenuSortBy);
                break;

            case SORT_BY_LOCATION:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_LOCATION_ASCENDING, subSubMenuSortBy);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_LOCATION_DESCENDING, subSubMenuSortBy);
                break;

            case SORT_BY_ATTRACTION_CATEGORY:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_ATTRACTION_CATEGORY_ASCENDING, subSubMenuSortBy);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_ATTRACTION_CATEGORY_DESCENDING, subSubMenuSortBy);
                break;

            case SORT_BY_MANUFACTURER:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_MANUFACTURER_ASCENDING, subSubMenuSortBy);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_MANUFACTURER_DESCENDING, subSubMenuSortBy);
                break;
        }

        subMenuSortBy.setGroupEnabled(selection.ordinal(), setEnabledBySelectionsToAdd.get(selection));
    }

    private void addSortAscendingToSubMenu(Selection selection, Menu subMenu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addSortAscendingToSubMenu:: adding MenuItem [%s]", selection)); //Todo: set to verbose
        subMenu.add(Menu.NONE, selection.ordinal(), Menu.NONE, R.string.selection_sort_ascending);
    }

    private void addSortDescendingToSubMenu(Selection selection, Menu subMenu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addSortDescendingToSubMenu:: adding MenuItem [%s]", selection)); //Todo: set to verbose
        subMenu.add(Menu.NONE, selection.ordinal(), Menu.NONE, R.string.selection_sort_descending);
    }

    private void createMenuGroupBy(Selection selection, Menu menu, Menu subMenuGroupBy)
    {
        if(subMenuGroupBy == null)
        {
            Log.d(Constants.LOG_TAG, "MenuAgent.createMenuGroupBy:: adding submenu <group by>"); //Todo: set to verbose
            subMenuGroupBy = menu.addSubMenu(Selection.GROUP_BY.ordinal(), Selection.GROUP_BY.ordinal(), Menu.NONE, R.string.selection_group_by);
        }

        this.addItemToMenu(selection, subMenuGroupBy);
    }

    private void addItemToMenu(Selection selection, Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemToMenu:: adding MenuItem [%s] - setEnabled[%S]", selection, this.setEnabledBySelectionsToAdd.get(selection))); //Todo: set to verbose
        menu.add(selection.ordinal(), selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection))
                .setEnabled(this.setEnabledBySelectionsToAdd.get(selection));
    }

    private void addActionItemToMenu(Selection selection, Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addActionItemToMenu:: adding MenuItem [%s]", selection)); //Todo: set to verbose

        menu.add(Menu.NONE, selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection))
                .setIcon(DrawableProvider.getColoredDrawable(this.drawableResourcesBySelection.get(selection), R.color.white))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }


    public boolean handleMenuItemSelected(MenuItem item, IMenuAgentClient client)
    {
        if(item.getItemId() <= Selection.values().length)
        {
            Log.i(Constants.LOG_TAG, String.format("MenuAgent.handleMenuItemSelected:: MenuItem [%s] selected", Selection.values()[item.getItemId()].toString()));

            switch(Selection.values()[item.getItemId()])
            {
                case NO_FUNCTION:
                case SORT_BY:
                case GROUP_BY:
                case SORT_BY_YEAR:
                case SORT_BY_NAME:
                case SORT_BY_LOCATION:
                case SORT_BY_ATTRACTION_CATEGORY:
                case SORT_BY_MANUFACTURER:
                    return true;
                case HELP:
                    return client.handleOptionsMenuItemHelpSelected();
                case EXPAND_ALL:
                    return client.handleOptionsMenuItemExpandAllSelected();
                case COLLAPSE_ALL:
                    return client.handleOptionsMenuItemCollapseAllSelected();
                case GROUP_BY_LOCATION:
                    return client.handleOptionsMenuItemGroupByLocationSelected();
                case GROUP_BY_ATTRACTION_CATEGORY:
                    return client.handleOptionsMenuItemGroupByAttractionCategorySelected();
                case GROUP_BY_MANUFACTURER:
                    return client.handleOptionsMenuItemGroupByManufacturerSelected();
                case GROUP_BY_STATUS:
                    return client.handleOptionsMenuItemGroupByStatusSelected();
                case SORT_BY_YEAR_ASCENDING:
                    return client.handleOptionsMenuItemSortByYearAscendingSelected();
                case SORT_BY_YEAR_DESCENDING:
                    return client.handleOptionsMenuItemSortByYearDescendingSelected();
                case SORT_BY_NAME_ASCENDING:
                    return client.handleOptionsMenuItemSortByNameAscendingSelected();
                case SORT_BY_NAME_DESCENDING:
                    return client.handleOptionsMenuItemSortByNameDescendingSelected();
                case SORT_BY_LOCATION_ASCENDING:
                    return client.handleOptionsMenuItemSortByLocationAscendingSelected();
                case SORT_BY_LOCATION_DESCENDING:
                    return client.handleOptionsMenuItemSortByLocationDescendingSelected();
                case SORT_BY_ATTRACTION_CATEGORY_ASCENDING:
                    return client.handleOptionsMenuItemSortByAttractionCategoryAscendingSelected();
                case SORT_BY_ATTRACTION_CATEGORY_DESCENDING:
                    return client.handleOptionsMenuItemSortByAttractionCategoryDescendingSelected();
                case SORT_BY_MANUFACTURER_ASCENDING:
                    return client.handleOptionsMenuItemSortByManufacturerAscendingSelected();
                case SORT_BY_MANUFACTURER_DESCENDING:
                    return client.handleOptionsMenuItemSortByManufacturerDescendingSelected();
                case GO_TO_CURRENT_VISIT:
                    return client.handleOptionsMenuItemGoToCurrentVisitSelected();
                case ENABLE_EDITING:
                    return client.handleOptionsMenuItemEnableEditingSelected();
                case DISABLE_EDITING:
                    return client.handleOptionsMenuItemDisableEditingSelected();



                default:
                    return client.handleInvalidOptionsMenuItemSelected(item);
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


        //OPTIONS MENU

        HELP,

        EXPAND_ALL,
        COLLAPSE_ALL,


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


        //OPTIONS ACTION

        GO_TO_CURRENT_VISIT,
        ENABLE_EDITING,
        DISABLE_EDITING,


        //POPUP
//        EDIT,
//        DELETE,
//        REMOVE,
//        RELOCATE,

//        SORT,
//        SORT_LOCATIONS,
//        SORT_PARKS,
//        SORT_ATTRACTIONS,

//        ASSIGN_TO_ATTRACTIONS,
//        SET_AS_DEFAULT,


//        CREATE_LOCATION,
//        CREATE_PARK,
    }
}



