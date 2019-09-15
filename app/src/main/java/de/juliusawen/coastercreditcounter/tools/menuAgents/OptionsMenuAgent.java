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
    private final List<OptionsItem> itemsToAdd;
    private final Map<OptionsItem, Boolean> setEnabledByItem;
    private final Map<OptionsItem, Boolean> setVisibleByItem;
    private final Map<OptionsItem, OptionsItem> submenuByItem;

    private final Map<OptionsItem, Integer> stringResourcesByItem;
    private final Map<OptionsItem, Integer> drawableResourcesByActionItem;


    public OptionsMenuAgent()
    {
        this.itemsToAdd = new LinkedList<>();
        this.setEnabledByItem = new HashMap<>();
        this.setVisibleByItem = new HashMap<>();
        this.submenuByItem = new HashMap<>();

        this.stringResourcesByItem = this.initializeStringResourcesByItem();
        this.drawableResourcesByActionItem = this.initializeDrawableResourcesByActionItem();
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

    private Map<OptionsItem, Integer> initializeDrawableResourcesByActionItem()
    {
        Map<OptionsItem, Integer> drawableResourcesByActionItem = new HashMap<>();

        drawableResourcesByActionItem.put(OptionsItem.GO_TO_CURRENT_VISIT, R.drawable.ic_baseline_local_activity);

        drawableResourcesByActionItem.put(OptionsItem.ENABLE_EDITING, R.drawable.ic_baseline_create);
        drawableResourcesByActionItem.put(OptionsItem.DISABLE_EDITING, R.drawable.ic_baseline_block);

        return drawableResourcesByActionItem;
    }


    public OptionsMenuAgent add(OptionsItem item)
    {
        this.itemsToAdd.add(item);

        return this;
    }

    public void create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding [%d] Item(s) to OptionsMenu", this.itemsToAdd.size()));

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
                    this.addSubMenuSort(item, menu);
                    this.submenuByItem.put(item, OptionsItem.SORT);
                    break;

                case SORT_BY_NAME:
                case SORT_BY_LOCATION:
                case SORT_BY_ATTRACTION_CATEGORY:
                case SORT_BY_MANUFACTURER:
                {
                    if(subMenuSortBy == null)
                    {
                        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.create:: adding subMenu <sort by>");
                        subMenuSortBy = menu.addSubMenu(OptionsItem.SORT_BY.ordinal(), OptionsItem.SORT_BY.ordinal(), Menu.NONE, R.string.menu_item_sort_by);
                    }
                    this.addSubMenuSort(item, subMenuSortBy);
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
                        Log.v(Constants.LOG_TAG, "OptionsMenuAgent.create:: adding submenu <group by>");
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

                case EXPAND_ALL:
                case COLLAPSE_ALL:
                case SORT_ATTRACTION_CATEGORIES:
                case SORT_MANUFACTURERS:
                case SORT_STATUSES:
                    this.addItemToMenu(item, menu);
                    break;

                default:
                    Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: OptionsItem [%s] can not be created this way", item));
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

    private void addSubMenuSort(OptionsItem item, Menu menu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSubMenuSort:: adding SubMenu [%s]", item));
        Menu subMenu = menu.addSubMenu(item.ordinal(), item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));

        switch(item)
        {
            case SORT:
                this.addSortAscendingToSubMenuSort(OptionsItem.SORT_ASCENDING, subMenu);
                this.addSortDescendingToSubMenuSort(OptionsItem.SORT_DESCENDING, subMenu);
                break;

            case SORT_BY_NAME:
                this.addSortAscendingToSubMenuSort(OptionsItem.SORT_BY_NAME_ASCENDING, subMenu);
                this.addSortDescendingToSubMenuSort(OptionsItem.SORT_BY_NAME_DESCENDING, subMenu);
                break;

            case SORT_BY_LOCATION:
                this.addSortAscendingToSubMenuSort(OptionsItem.SORT_BY_LOCATION_ASCENDING, subMenu);
                this.addSortDescendingToSubMenuSort(OptionsItem.SORT_BY_LOCATION_DESCENDING, subMenu);
                break;

            case SORT_BY_ATTRACTION_CATEGORY:
                this.addSortAscendingToSubMenuSort(OptionsItem.SORT_BY_ATTRACTION_CATEGORY_ASCENDING, subMenu);
                this.addSortDescendingToSubMenuSort(OptionsItem.SORT_BY_ATTRACTION_CATEGORY_DESCENDING, subMenu);
                break;

            case SORT_BY_MANUFACTURER:
                this.addSortAscendingToSubMenuSort(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, subMenu);
                this.addSortDescendingToSubMenuSort(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, subMenu);
                break;
        }
    }

    private void addSortAscendingToSubMenuSort(OptionsItem item, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSortAscendingToSubMenuSort:: adding [%s]", item));
        subMenu.add(Menu.NONE, item.ordinal(), Menu.NONE, R.string.menu_item_sort_ascending);
    }

    private void addSortDescendingToSubMenuSort(OptionsItem item, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.addSortDescendingToSubMenuSort:: adding [%s]", item));
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
                .setIcon(DrawableProvider.getColoredDrawable(this.drawableResourcesByActionItem.get(item), R.color.white))
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


    public boolean handleOptionsItemSelected(MenuItem menuItem, IOptionsMenuAgentClient client)
    {
        if(menuItem.getItemId() <= OptionsItem.values().length)
        {
            OptionsItem optionsItem = OptionsItem.values()[menuItem.getItemId()];

            Log.i(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: MenuItem [%s] in [%s] selected", optionsItem, client.getClass().getSimpleName()));

            switch(optionsItem)
            {
                //add case for OptionsItems with no function here
                case NO_FUNCTION:
                case SORT:
                case SORT_BY:
                case GROUP_BY:
                case SORT_BY_NAME:
                case SORT_BY_LOCATION:
                case SORT_BY_ATTRACTION_CATEGORY:
                case SORT_BY_MANUFACTURER:
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: MenuItem [%s] in [%s] has no function", optionsItem, client.getClass().getSimpleName()));
                    return true;

                default:
                    return client.handleOptionsItemSelected(optionsItem);
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: MenuItem [%d] for [%s] is invalid", menuItem.getItemId(), client.getClass().getSimpleName()));
            return false;
        }
    }
}



