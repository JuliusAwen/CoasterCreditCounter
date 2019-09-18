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

    private final Map<OptionsItem, Boolean> setEnabledByItem = new HashMap<>();
    private final Map<OptionsItem, Boolean> setVisibleByItem = new HashMap<>();

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
                Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.addToGroup:: Group [#%d - %s] for Item [#%d - %s] already added", group.ordinal(), group, item.ordinal(), item));
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
            Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.add:: Item [#%d - %s] already added", item.ordinal(), item));
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

    public Menu create(Menu menu)
    {
        Log.d(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding [%s] Item(s) to OptionsMenu", this.itemsToAdd.size()));

        for(OptionsItem item : this.itemsToAdd)
        {
            if(this.isActionItem(item))
            {
                Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Item [#%d - %s] as ACTION_IF_ROOM to ROOT", item.ordinal(), item));

                menu.add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource)
                        .setIcon(DrawableProvider.getColoredDrawable(item.drawableResource, R.color.white))
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
            else if(isGroup(item))
            {
                if(this.isGroupInRootMenu(item))
                {
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Group [#%d - %s] to ROOT", item.ordinal(), item));
                    this.subMenuByGroup.put(item, menu.addSubMenu(item.ordinal(), item.ordinal(), item.ordinal(), item.stringResource));
                }
                else //isGroupInSubMenu
                {
                    if(this.subMenuByGroup.containsKey(this.groupByItem.get(item)))
                    {
                        OptionsItem group = this.groupByItem.get(item);
                        Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Group [#%d - %s] to Group [#%d - %s]", item.ordinal(), item, group.ordinal(), group));
                        this.subMenuByGroup.put(item, this.subMenuByGroup.get(group).addSubMenu(item.ordinal(), item.ordinal(), item.ordinal(), item.stringResource));
                    }
                    else
                    {
                        Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: Group for Group [#%d - %s] not found", item.ordinal(), item));
                        break;
                    }
                }
            }
            else if(this.isItemInRootMenu(item))
            {
                Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Item [#%d - %s] to ROOT", item.ordinal(), item));
                menu.add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource);
            }
            else //IsItemInGroup
            {
                if((this.subMenuByGroup.containsKey(this.groupByItem.get(item))))
                {
                    OptionsItem group = this.groupByItem.get(item);
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.create:: adding Item [#%d - %s] to Group [#%d - %s]", item.ordinal(), item, group.ordinal(), group));
                    this.subMenuByGroup.get(group).add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource);
                }
                else
                {
                    Log.e(Constants.LOG_TAG, String.format("OptionsMenuAgent.add:: Group for Item [#%d - %s] not found", item.ordinal(), item));
                    break;
                }
            }
        }

        this.itemsToAdd.clear();
        return menu;
    }

    public Menu prepare(Menu menu)
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
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: Group [#%d - %s] enabled [%s]", optionsItem.ordinal(), optionsItem, isEnabled));
                }
                else
                {
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: Item [#%d - %s] enabled [%s]", optionsItem.ordinal(), optionsItem, isEnabled));
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
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: Group [#%d - %s] visible [%s]", optionsItem.ordinal(), optionsItem, isVisible));
                }
                else
                {
                    menuItem.setVisible(isVisible);
                    Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.prepare:: Item [#%d - %s] visible [%s]", optionsItem.ordinal(), optionsItem, isVisible));
                }
            }
        }

        this.setEnabledByItem.clear();
        this.setVisibleByItem.clear();

        return menu;
    }

    private boolean isActionItem(OptionsItem item)
    {
        return item.drawableResource != -1;
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
        OptionsItem optionsItem = OptionsItem.getValue(menuItem.getItemId());

        Log.i(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: Item [#%d - %s] in [%s] selected", optionsItem.ordinal(), optionsItem, client.getClass().getSimpleName()));

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
                Log.v(Constants.LOG_TAG, String.format("OptionsMenuAgent.handleOptionsItemSelected:: Item [#%d - %s] in [%s] has no function", optionsItem.ordinal(), optionsItem, client.getClass().getSimpleName()));
                return true;

            default:
                return client.handleOptionsItemSelected(optionsItem);
        }
    }
}



