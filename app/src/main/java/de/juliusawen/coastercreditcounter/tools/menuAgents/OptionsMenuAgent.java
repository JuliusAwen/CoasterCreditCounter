package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;

public class OptionsMenuAgent
{
    private final Set<OptionsItem> itemsToAdd = new LinkedHashSet<>();
    private final Map<OptionsItem, OptionsItem> groupByItem = new HashMap<>();
    private final Map<OptionsItem, SubMenu> subMenuByGroup = new HashMap<>();

    private final Map<OptionsItem, Integer> stringResourcesByItem = new HashMap<>();
    private final Map<OptionsItem, Integer> drawableResourcesByItem = new HashMap<>();

    private final Map<OptionsItem, Boolean> setEnabledByItem = new HashMap<>();
    private final Map<OptionsItem, Boolean> setVisibleByItem = new HashMap<>();


    public OptionsMenuAgent()
    {
        this.populateStringResourcesByItem();
        this.populateDrawableResourcesByItem();
    }

    private void populateStringResourcesByItem()
    {
        this.stringResourcesByItem.put(OptionsItem.HELP, R.string.menu_item_help);

        this.stringResourcesByItem.put(OptionsItem.EXPAND_ALL, R.string.menu_item_expand_all);
        this.stringResourcesByItem.put(OptionsItem.COLLAPSE_ALL, R.string.menu_item_collapse_all);

        this.stringResourcesByItem.put(OptionsItem.SORT, R.string.menu_item_sort);
        this.stringResourcesByItem.put(OptionsItem.SORT_ASCENDING, R.string.menu_item_sort_ascending);
        this.stringResourcesByItem.put(OptionsItem.SORT_DESCENDING, R.string.menu_item_sort_descending);

        this.stringResourcesByItem.put(OptionsItem.SORT_ATTRACTION_CATEGORIES, R.string.menu_item_sort);
        this.stringResourcesByItem.put(OptionsItem.SORT_MANUFACTURERS, R.string.menu_item_sort);
        this.stringResourcesByItem.put(OptionsItem.SORT_STATUSES, R.string.menu_item_sort);

        this.stringResourcesByItem.put(OptionsItem.SORT_BY_NAME, R.string.menu_item_sort_by_name);
        this.stringResourcesByItem.put(OptionsItem.SORT_BY_NAME_ASCENDING, R.string.menu_item_sort_ascending);
        this.stringResourcesByItem.put(OptionsItem.SORT_BY_NAME_DESCENDING, R.string.menu_item_sort_descending);

        this.stringResourcesByItem.put(OptionsItem.SORT_BY_LOCATION, R.string.menu_item_sort_by_location);
        this.stringResourcesByItem.put(OptionsItem.SORT_BY_LOCATION_ASCENDING, R.string.menu_item_sort_ascending);
        this.stringResourcesByItem.put(OptionsItem.SORT_BY_LOCATION_DESCENDING, R.string.menu_item_sort_descending);

        this.stringResourcesByItem.put(OptionsItem.SORT_BY_ATTRACTION_CATEGORY, R.string.menu_item_sort_by_attraction_category);
        this.stringResourcesByItem.put(OptionsItem.SORT_BY_ATTRACTION_CATEGORY_ASCENDING, R.string.menu_item_sort_ascending);
        this.stringResourcesByItem.put(OptionsItem.SORT_BY_ATTRACTION_CATEGORY_DESCENDING, R.string.menu_item_sort_descending);

        this.stringResourcesByItem.put(OptionsItem.SORT_BY_MANUFACTURER, R.string.menu_item_sort_by_manufacturer);
        this.stringResourcesByItem.put(OptionsItem.SORT_BY_MANUFACTURER_ASCENDING, R.string.menu_item_sort_ascending);
        this.stringResourcesByItem.put(OptionsItem.SORT_BY_MANUFACTURER_DESCENDING, R.string.menu_item_sort_descending);

        this.stringResourcesByItem.put(OptionsItem.GROUP_BY, R.string.menu_item_group_by);
        this.stringResourcesByItem.put(OptionsItem.GROUP_BY_LOCATION, R.string.menu_item_group_by_location);
        this.stringResourcesByItem.put(OptionsItem.GROUP_BY_ATTRACTION_CATEGORY, R.string.menu_item_group_by_attraction_category);
        this.stringResourcesByItem.put(OptionsItem.GROUP_BY_MANUFACTURER, R.string.menu_item_group_by_manufacturer);
        this.stringResourcesByItem.put(OptionsItem.GROUP_BY_STATUS, R.string.menu_item_group_by_status);

        this.stringResourcesByItem.put(OptionsItem.GO_TO_CURRENT_VISIT, R.string.menu_item_go_to_current_visit);

        this.stringResourcesByItem.put(OptionsItem.ENABLE_EDITING, R.string.menu_item_enable_editing);
        this.stringResourcesByItem.put(OptionsItem.DISABLE_EDITING, R.string.menu_item_disable_editing);
    }

    private void populateDrawableResourcesByItem()
    {
        this.drawableResourcesByItem.put(OptionsItem.GO_TO_CURRENT_VISIT, R.drawable.ic_baseline_local_activity);

        this.drawableResourcesByItem.put(OptionsItem.ENABLE_EDITING, R.drawable.ic_baseline_create);
        this.drawableResourcesByItem.put(OptionsItem.DISABLE_EDITING, R.drawable.ic_baseline_block);
    }


    public OptionsMenuAgent add(OptionsItem item)
    {
        this.addItem(item);
        return this;
    }

    public OptionsMenuAgent addToGroup(OptionsItem item, OptionsItem group)
    {
        if(this.addItem(item))
        {
            if(!this.groupByItem.containsKey(item))
            {
                this.groupByItem.put(item, group);
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.addToGroup:: Group [%s] for Item [%s] already added", group, item));
            }
        }
        return this;
    }

    private boolean addItem(OptionsItem item)
    {
        if(!this.itemsToAdd.contains(item))
        {
            this.itemsToAdd.add(item);
            return true;
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.add:: Item [%s] already added", item));
            return false;
        }
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

    public void create(Menu menu)
    {
        for(OptionsItem item : this.itemsToAdd)
        {
            if(item.equals(OptionsItem.HELP))
            {
                final int highestSortOrder = 1;
                Log.v(Constants.LOG_TAG, "OptionsMenuAgent.create:: adding Item HELP to bottom of menu");
                menu.add(Menu.NONE, OptionsItem.HELP.ordinal(), highestSortOrder, R.string.menu_item_help);
            }
            else if(this.drawableResourcesByItem.containsKey(item))
            {
                Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Item [%s] as ACTION_IF_ROOM to ROOT", item));

                menu.add(Menu.NONE, item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item))
                        .setIcon(DrawableProvider.getColoredDrawable(this.drawableResourcesByItem.get(item), R.color.white))
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
            else if(isGroup(item))
            {
                if(this.isGroupInRootMenu(item))
                {
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Group [%s] to ROOT", item));
                    this.subMenuByGroup.put(item, menu.addSubMenu(item.ordinal(), item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item)));
                }
                else //isGroupInSubMenu
                {
                    if(this.subMenuByGroup.containsKey(this.groupByItem.get(item)))
                    {
                        OptionsItem group = this.groupByItem.get(item);
                        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Group [%s] to Group [%s]", item, group));
                        this.subMenuByGroup.put(item, this.subMenuByGroup.get(group).addSubMenu(item.ordinal(), item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item)));
                    }
                    else
                    {
                        Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: Group for Group [%s] not found", item));
                        break;
                    }
                }
            }
            else if(this.isItemInRootMenu(item))
            {
                Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Item [%s] to ROOT", item));
                menu.add(Menu.NONE, item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
            }
            else //IsItemInGroup
            {
                if((this.subMenuByGroup.containsKey(this.groupByItem.get(item))))
                {
                    OptionsItem group = this.groupByItem.get(item);
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Item [%s] to Group [%s]", item, group));
                    this.subMenuByGroup.get(group).add(Menu.NONE, item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
                }
                else
                {
                    Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.add:: Group for Item [%s] not found", item));
                    break;
                }
            }
        }

        this.itemsToAdd.clear();
    }

    public void prepare(Menu menu)
    {
        for(OptionsItem optionsItem : this.setEnabledByItem.keySet())
        {
            MenuItem menuItem = menu.findItem(optionsItem.ordinal());
            if(menuItem != null)
            {
                boolean isEnabled = this.setEnabledByItem.get(optionsItem);
                if(this.isGroup(optionsItem))
                {
                    if(this.isGroupInRootMenu(optionsItem))
                    {
                        menu.setGroupEnabled(optionsItem.ordinal(), isEnabled);
                    }
                    else //isGroupInGroup
                    {
                        OptionsItem group = this.groupByItem.get(optionsItem);
                        this.subMenuByGroup.get(group).setGroupEnabled(optionsItem.ordinal(), isEnabled);
                    }
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: Group [%s] enabled [%s]", optionsItem, isEnabled));
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: Item [%s] enabled [%s]", optionsItem, isEnabled));
                    menuItem.setEnabled(isEnabled);
                }
            }
        }

        for(OptionsItem optionsItem : this.setVisibleByItem.keySet())
        {
            MenuItem menuItem = menu.findItem(optionsItem.ordinal());
            if(menuItem != null)
            {
                boolean isVisible = this.setVisibleByItem.get(optionsItem);
                if(this.isGroup(optionsItem))
                {
                    if(this.isGroupInRootMenu(optionsItem))
                    {
                        menu.setGroupVisible(optionsItem.ordinal(), isVisible);
                    }
                    else //isGroupInGroup
                    {
                        OptionsItem group = this.groupByItem.get(optionsItem);
                        this.subMenuByGroup.get(group).setGroupVisible(optionsItem.ordinal(), isVisible);
                    }
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: Group [%s] visible [%s]", optionsItem, isVisible));
                }
                else
                {
                    menuItem.setVisible(isVisible);
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: Item [%s] visible [%s]", optionsItem, isVisible));
                }
            }
        }

        this.setEnabledByItem.clear();
        this.setVisibleByItem.clear();
    }

    private boolean isGroup(OptionsItem item)
    {
        return this.groupByItem.containsValue(item);
    }

    private boolean isGroupInRootMenu(OptionsItem item)
    {
        return isGroup(item) && !this.groupByItem.containsKey(item);
    }

    private boolean isItemInRootMenu(OptionsItem item)
    {
        return !this.groupByItem.containsKey(item);
    }


    public boolean handleOptionsItemSelected(MenuItem menuItem, IOptionsMenuAgentClient client)
    {
        if(menuItem.getItemId() <= OptionsItem.values().length)
        {
            OptionsItem optionsItem = OptionsItem.values()[menuItem.getItemId()];

            Log.i(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: Item [%s] in [%s] selected", optionsItem, client.getClass().getSimpleName()));

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
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: Item [%s] in [%s] has no function", optionsItem, client.getClass().getSimpleName()));
                    return true;

                default:
                    return client.handleOptionsItemSelected(optionsItem);
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: Item [%d] for [%s] is invalid", menuItem.getItemId(), client.getClass().getSimpleName()));
            return false;
        }
    }
}



