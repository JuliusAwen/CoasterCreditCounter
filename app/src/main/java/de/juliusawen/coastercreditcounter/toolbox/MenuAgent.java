package de.juliusawen.coastercreditcounter.toolbox;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.MenuType;

public class MenuAgent
{
    public static final int HELP = Selection.HELP.ordinal();

    public static final int EXPAND_ALL = Selection.EXPAND_ALL.ordinal();
    public static final int COLLAPSE_ALL = Selection.COLLAPSE_ALL.ordinal();

    public static final int SORT = Selection.SORT.ordinal();
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


    private List<Selection> selectionsToAdd;
    private Map<Selection, Boolean> setEnabledBySelection;
    private Map<Selection, Boolean> setVisibleBySelection;
    private Map<Selection, Selection> submenuBySelection;

    private Map<Selection, Integer> stringResourcesBySelection;
    private Map<Selection, Integer> drawableResourcesBySelection;


    public MenuAgent(MenuType menuType)
    {
        this.menuType = menuType;

        this.selectionsToAdd = new LinkedList<>();
        this.setEnabledBySelection = new HashMap<>();
        this.setVisibleBySelection = new HashMap<>();
        this.submenuBySelection = new HashMap<>();

        this.stringResourcesBySelection = this.createStringResourcesBySelectionMap();
        this.drawableResourcesBySelection = this.createDrawableResourcesBySelectionMap();
    }

    private Map<Selection, Integer> createStringResourcesBySelectionMap()
    {
        Map<Selection, Integer> stringResourcesBySelection = new HashMap<>();

        stringResourcesBySelection.put(Selection.HELP, R.string.selection_help);

        stringResourcesBySelection.put(Selection.EXPAND_ALL, R.string.selection_expand_all);
        stringResourcesBySelection.put(Selection.COLLAPSE_ALL, R.string.selection_collapse_all);

        stringResourcesBySelection.put(Selection.SORT, R.string.selection_sort);
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


    public MenuAgent addMenuItem(int menuItem)
    {
        if(Selection.values().length >= menuItem)
        {
            this.selectionsToAdd.add(Selection.values()[menuItem]);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("MenuAgent.addMenuItem:: MenuItem [%d] does not exist - Selection.values().length = [%d]", menuItem, Selection.values().length));
        }

        return this;
    }


    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.create:: adding [%d] MenuItem(s) to [%s]", this.selectionsToAdd.size(), this.menuType));

        menu.clear();

        switch(menuType)
        {
            case OPTIONS_MENU:
            {
                Menu subMenuSortBy = null;
                Menu subMenuGroupBy = null;

                for(Selection selection : this.selectionsToAdd)
                {
                    switch(selection)
                    {
                        case SORT:
                        {
                            this.createMenuSort(selection, menu);
                            break;
                        }

                        case SORT_BY_YEAR:
                        case SORT_BY_NAME:
                        case SORT_BY_LOCATION:
                        case SORT_BY_ATTRACTION_CATEGORY:
                        case SORT_BY_MANUFACTURER:
                        {
                            if(subMenuSortBy == null)
                            {
                                Log.d(Constants.LOG_TAG, "MenuAgent.create:: adding subMenu <sort by>"); //Todo: set to verbose
                                subMenuSortBy = menu.addSubMenu(Selection.SORT_BY.ordinal(), Selection.SORT_BY.ordinal(), Menu.NONE, R.string.selection_sort_by);
                            }
                            this.createMenuSort(selection, subMenuSortBy);
                            this.submenuBySelection.put(selection, Selection.SORT_BY);
                            break;
                        }

                        case GROUP_BY_LOCATION:
                        case GROUP_BY_ATTRACTION_CATEGORY:
                        case GROUP_BY_MANUFACTURER:
                        case GROUP_BY_STATUS:
                        {
                            if(subMenuGroupBy == null)
                            {
                                Log.d(Constants.LOG_TAG, "MenuAgent.create:: adding submenu <group by>"); //Todo: set to verbose
                                subMenuGroupBy = menu.addSubMenu(Selection.GROUP_BY.ordinal(), Selection.GROUP_BY.ordinal(), Menu.NONE, R.string.selection_group_by);
                            }
                            this.addItemToMenu(selection, subMenuGroupBy);
                            this.submenuBySelection.put(selection, Selection.GROUP_BY);
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

        this.selectionsToAdd.clear();
    }

    private void createMenuSort(Selection selection, Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.createMenuSort:: adding SubMenu [%s]", selection)); //Todo: set to verbose
        Menu subMenu = menu.addSubMenu(selection.ordinal(), selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection));

        switch(selection)
        {
            case SORT:
                this.addSortAscendingToSubMenu(Selection.SORT_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(Selection.SORT_DESCENDING, subMenu);
                break;

            case SORT_BY_YEAR:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_YEAR_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_YEAR_DESCENDING, subMenu);
                break;

            case SORT_BY_NAME:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_NAME_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_NAME_DESCENDING, subMenu);
                break;

            case SORT_BY_LOCATION:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_LOCATION_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_LOCATION_DESCENDING, subMenu);
                break;

            case SORT_BY_ATTRACTION_CATEGORY:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_ATTRACTION_CATEGORY_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_ATTRACTION_CATEGORY_DESCENDING, subMenu);
                break;

            case SORT_BY_MANUFACTURER:
                this.addSortAscendingToSubMenu(Selection.SORT_BY_MANUFACTURER_ASCENDING, subMenu);
                this.addSortDescendingToSubMenu(Selection.SORT_BY_MANUFACTURER_DESCENDING, subMenu);
                break;
        }
    }

    private void addSortAscendingToSubMenu(Selection selection, Menu subMenu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addSortAscendingToSubMenu:: adding [%s]", selection)); //Todo: set to verbose
        subMenu.add(Menu.NONE, selection.ordinal(), Menu.NONE, R.string.selection_sort_ascending);
    }

    private void addSortDescendingToSubMenu(Selection selection, Menu subMenu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addSortDescendingToSubMenu:: adding [%s]", selection)); //Todo: set to verbose
        subMenu.add(Menu.NONE, selection.ordinal(), Menu.NONE, R.string.selection_sort_descending);
    }

    private void addItemToMenu(Selection selection, Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addItemToMenu:: adding [%s]", selection)); //Todo: set to verbose
        menu.add(selection.ordinal(), selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection));
    }

    private void addActionItemToMenu(Selection selection, Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("MenuAgent.addActionItemToMenu:: adding [%s]", selection)); //Todo: set to verbose

        menu.add(Menu.NONE, selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection))
                .setIcon(DrawableProvider.getColoredDrawable(this.drawableResourcesBySelection.get(selection), R.color.white))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    public MenuAgent setEnabled(int menuItem, boolean setEnabled)
    {
        if(Selection.values().length >= menuItem)
        {
            this.setEnabledBySelection.put(Selection.values()[menuItem], setEnabled);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("MenuAgent.setEnabled:: MenuItem [%d] does not exist - Selection.values().length = [%d]", menuItem, Selection.values().length));
        }

        return this;
    }

    public MenuAgent setVisible(int menuItem, boolean setVisible)
    {
        if(Selection.values().length >= menuItem)
        {
            this.setVisibleBySelection.put(Selection.values()[menuItem], setVisible);
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("MenuAgent.setVisible:: MenuItem [%d] does not exist - Selection.values().length = [%d]", menuItem, Selection.values().length));
        }

        return this;
    }

    public void prepare(Menu menu)
    {

        for(Selection selection : this.setEnabledBySelection.keySet())
        {
            MenuItem menuItem = menu.findItem(selection.ordinal());
            if(menuItem != null)
            {
                if(menuItem.hasSubMenu())
                {
                    Log.d(Constants.LOG_TAG, String.format("MenuAgent.prepare:: setting [%s][%s] enabled [%S]",
                            this.submenuBySelection.get(selection), selection, this.setEnabledBySelection.get(selection)));

                    menuItem = menu.findItem(this.submenuBySelection.get(selection).ordinal());
                    if(menuItem != null)
                    {
                        menuItem.getSubMenu().setGroupEnabled(selection.ordinal(), this.setEnabledBySelection.get(selection));
                    }
                }
                else
                {
                    Log.d(Constants.LOG_TAG, String.format("MenuAgent.prepare:: setting [%s] enabled [%S]", selection, this.setEnabledBySelection.get(selection)));
                    menuItem.setEnabled(this.setEnabledBySelection.get(selection));
                }
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("MenuAgent.prepare<setEnable>:: MenuItem [%s] not found", selection));
            }
        }

        for(Selection selection : this.setVisibleBySelection.keySet())
        {
            MenuItem menuItem = menu.findItem(selection.ordinal());
            if(menuItem != null)
            {
                Log.d(Constants.LOG_TAG, String.format("MenuAgent.prepare:: setting [%s] visible [%S]", selection, this.setVisibleBySelection.get(selection)));
                menuItem.setVisible(this.setVisibleBySelection.get(selection));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("MenuAgent.prepare<setVisible>:: MenuItem [%s] not found", selection));
            }
        }

        this.setEnabledBySelection.clear();
        this.setVisibleBySelection.clear();
    }


    public boolean handleMenuItemSelected(MenuItem item, IMenuAgentClient client)
    {
        if(item.getItemId() <= Selection.values().length)
        {
            Log.i(Constants.LOG_TAG, String.format("MenuAgent.handleMenuItemSelected:: MenuItem [%s] selected", Selection.values()[item.getItemId()].toString()));

            switch(Selection.values()[item.getItemId()])
            {
                //add case for selections with no function here
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
                case SORT_ASCENDING:
                    return client.handleMenuItemSortAscendingSelected();
                case SORT_DESCENDING:
                    return client.handleMenuItemSortDescendingSelected();
                case SORT_BY_YEAR_ASCENDING:
                    return client.handleMenuItemSortByYearAscendingSelected();
                case SORT_BY_YEAR_DESCENDING:
                    return client.handleMenuItemSortByYearDescendingSelected();
                case SORT_BY_NAME_ASCENDING:
                    return client.handleMenuItemSortByNameAscendingSelected();
                case SORT_BY_NAME_DESCENDING:
                    return client.handleMenuItemSortByNameDescendingSelected();
                case SORT_BY_LOCATION_ASCENDING:
                    return client.handleMenuItemSortByLocationAscendingSelected();
                case SORT_BY_LOCATION_DESCENDING:
                    return client.handleMenuItemSortByLocationDescendingSelected();
                case SORT_BY_ATTRACTION_CATEGORY_ASCENDING:
                    return client.handleMenuItemSortByAttractionCategoryAscendingSelected();
                case SORT_BY_ATTRACTION_CATEGORY_DESCENDING:
                    return client.handleMenuItemSortByAttractionCategoryDescendingSelected();
                case SORT_BY_MANUFACTURER_ASCENDING:
                    return client.handleMenuItemSortByManufacturerAscendingSelected();
                case SORT_BY_MANUFACTURER_DESCENDING:
                    return client.handleMenuItemSortByManufacturerDescendingSelected();
                case GO_TO_CURRENT_VISIT:
                    return client.handleMenuItemGoToCurrentVisitSelected();
                case ENABLE_EDITING:
                    return client.handleMenuItemEnableEditingSelected();
                case DISABLE_EDITING:
                    return client.handleMenuItemDisableEditingSelected();



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


    private enum Selection
    {
        NO_FUNCTION,


        //OPTIONS MENU

        HELP,

        EXPAND_ALL,
        COLLAPSE_ALL,


        SORT_BY,

        SORT,
        SORT_ASCENDING,
        SORT_DESCENDING,

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

//        SORT_LOCATIONS,
//        SORT_PARKS,
//        SORT_ATTRACTIONS,

//        ASSIGN_TO_ATTRACTIONS,
//        SET_AS_DEFAULT,


//        CREATE_LOCATION,
//        CREATE_PARK,
    }
}



