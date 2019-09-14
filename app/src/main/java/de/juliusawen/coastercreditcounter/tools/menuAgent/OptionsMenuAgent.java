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
    public static final Selection HELP = Selection.HELP;

    public static final Selection EXPAND_ALL = Selection.EXPAND_ALL;
    public static final Selection COLLAPSE_ALL = Selection.COLLAPSE_ALL;

    public static final Selection SORT = Selection.SORT;

    public static final Selection SORT_ATTRACTION_CATEGORIES = Selection.SORT_ATTRACTION_CATEGORIES;
    public static final Selection SORT_MANUFACTURERS = Selection.SORT_MANUFACTURERS;
    public static final Selection SORT_STATUSES = Selection.SORT_STATUSES;

    public static final Selection SORT_BY_YEAR  = Selection.SORT_BY_YEAR;
    public static final Selection SORT_BY_NAME  = Selection.SORT_BY_NAME;
    public static final Selection SORT_BY_LOCATION  = Selection.SORT_BY_LOCATION;
    public static final Selection SORT_BY_ATTRACTION_CATEGORY = Selection.SORT_BY_ATTRACTION_CATEGORY;
    public static final Selection SORT_BY_MANUFACTURER  = Selection.SORT_BY_MANUFACTURER;

    public static final Selection GROUP_BY_LOCATION = Selection.GROUP_BY_LOCATION;
    public static final Selection GROUP_BY_ATTRACTION_CATEGORY = Selection.GROUP_BY_ATTRACTION_CATEGORY;
    public static final Selection GROUP_BY_MANUFACTURER = Selection.GROUP_BY_MANUFACTURER;
    public static final Selection GROUP_BY_STATUS = Selection.GROUP_BY_STATUS;

    public static final Selection GO_TO_CURRENT_VISIT = Selection.GO_TO_CURRENT_VISIT;

    public static final Selection ENABLE_EDITING = Selection.ENABLE_EDITING;
    public static final Selection DISABLE_EDITING = Selection.DISABLE_EDITING;

    private final List<Selection> selectionsToAdd;
    private final Map<Selection, Boolean> setEnabledBySelection;
    private final Map<Selection, Boolean> setVisibleBySelection;
    private final Map<Selection, Selection> submenuBySelection;

    private final Map<Selection, Integer> stringResourcesBySelection;
    private final Map<Selection, Integer> drawableResourcesBySelection;


    public OptionsMenuAgent()
    {
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

        stringResourcesBySelection.put(Selection.SORT_ATTRACTION_CATEGORIES, R.string.selection_sort);
        stringResourcesBySelection.put(Selection.SORT_MANUFACTURERS, R.string.selection_sort);
        stringResourcesBySelection.put(Selection.SORT_STATUSES, R.string.selection_sort);

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


    public OptionsMenuAgent add(Selection selection)
    {
        this.selectionsToAdd.add(selection);

        return this;
    }


    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding [%d] Item(s) to OptionsMenu", this.selectionsToAdd.size()));

        Menu subMenuSortBy = null;
        Menu subMenuGroupBy = null;

        for(Selection selection : this.selectionsToAdd)
        {
            switch(selection)
            {
                case HELP:
                    addHelpToMenu(menu);
                    break;

                case SORT:
                    this.createMenuSort(selection, menu);
                    this.submenuBySelection.put(selection, Selection.SORT);
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
                        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.create:: adding submenu <group by>");
                        subMenuGroupBy = menu.addSubMenu(Selection.GROUP_BY.ordinal(), Selection.GROUP_BY.ordinal(), Menu.NONE, R.string.selection_group_by);
                    }
                    this.addItemToSubMenu(selection, subMenuGroupBy);
                    this.submenuBySelection.put(selection, Selection.GROUP_BY);
                    break;
                }

                case GO_TO_CURRENT_VISIT:
                case ENABLE_EDITING:
                case DISABLE_EDITING:
                    this.addActionItemToMenu(selection, menu);
                    break;

                default:
                    this.addItemToMenu(selection, menu);
                    break;
            }
        }

        this.selectionsToAdd.clear();
    }

    private void addHelpToMenu(Menu menu)
    {
        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.addHelpToSubMenu:: adding HELP");
        menu.add(Menu.NONE, Selection.HELP.ordinal(), 1, R.string.selection_help); // 1 - represents the order: as all other selections are 0 HELP should always be sorted to the bottom
    }

    private void createMenuSort(Selection selection, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.createMenuSort:: adding SubMenu [%s]", selection));
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
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSortAscendingToSubMenu:: adding [%s]", selection));
        subMenu.add(Menu.NONE, selection.ordinal(), Menu.NONE, R.string.selection_sort_ascending);
    }

    private void addSortDescendingToSubMenu(Selection selection, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSortDescendingToSubMenu:: adding [%s]", selection));
        subMenu.add(Menu.NONE, selection.ordinal(), Menu.NONE, R.string.selection_sort_descending);
    }

    private void addItemToSubMenu(Selection selection, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addItemToSubMenu:: adding [%s]", selection));
        subMenu.add(selection.ordinal(), selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection));
    }

    private void addItemToMenu(Selection selection, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addItemToMenu:: adding [%s]", selection));
        menu.add(Menu.NONE, selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection));
    }

    private void addActionItemToMenu(Selection selection, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addActionItemToMenu:: adding [%s]", selection));

        menu.add(Menu.NONE, selection.ordinal(), Menu.NONE, this.stringResourcesBySelection.get(selection))
                .setIcon(DrawableProvider.getColoredDrawable(this.drawableResourcesBySelection.get(selection), R.color.white))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    public OptionsMenuAgent setEnabled(Selection selection, boolean setEnabled)
    {
        this.setEnabledBySelection.put(selection, setEnabled);

        return this;
    }

    public OptionsMenuAgent setVisible(Selection selection, boolean setVisible)
    {
        this.setVisibleBySelection.put(selection, setVisible);

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
                    if(this.submenuBySelection.get(selection).equals(selection))
                    {
                        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<hasSubMenu>:: setting [%s] enabled [%S]", selection, this.setEnabledBySelection.get(selection)));

                        menuItem.setEnabled(this.setEnabledBySelection.get(selection));
                    }
                    else
                    {
                        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<hasSubMenu>:: setting [%s][%s] enabled [%S]",
                                this.submenuBySelection.get(selection), selection, this.setEnabledBySelection.get(selection)));

                        menuItem = menu.findItem(this.submenuBySelection.get(selection).ordinal());
                        if(menuItem != null)
                        {
                            menuItem.getSubMenu().setGroupEnabled(selection.ordinal(), this.setEnabledBySelection.get(selection));
                        }
                    }
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: setting [%s] enabled [%S]", selection, this.setEnabledBySelection.get(selection)));
                    menuItem.setEnabled(this.setEnabledBySelection.get(selection));
                }
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<setEnable>:: MenuItem [%s] not found", selection));
            }
        }

        for(Selection selection : this.setVisibleBySelection.keySet())
        {
            MenuItem menuItem = menu.findItem(selection.ordinal());
            if(menuItem != null)
            {
                Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: setting [%s] visible [%S]", selection, this.setVisibleBySelection.get(selection)));
                menuItem.setVisible(this.setVisibleBySelection.get(selection));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare<setVisible>:: MenuItem [%s] not found", selection));
            }
        }

        this.setEnabledBySelection.clear();
        this.setVisibleBySelection.clear();
    }


    public boolean handleMenuItemSelected(MenuItem item, IOptionsMenuAgentClient client)
    {
        if(item.getItemId() <= Selection.values().length)
        {
            Log.i(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleMenuItemSelected:: MenuItem [%s] selected", Selection.values()[item.getItemId()].toString()));

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
            Log.e(Constants.LOG_TAG, "OptionsMenuAgent.handleMenuItemSelected:: Selection [%s] not valid");
            return false;
        }
    }


    private enum Selection
    {
        NO_FUNCTION,


        //OPTIONS MENU ITEM

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


        //OPTIONS MENU ACTION

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



