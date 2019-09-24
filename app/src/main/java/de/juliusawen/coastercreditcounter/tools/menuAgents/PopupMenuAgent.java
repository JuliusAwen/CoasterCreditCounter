package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.PopupMenu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.application.Constants;

public class PopupMenuAgent
{
    private final Set<PopupItem> itemsToAdd = new LinkedHashSet<>();
    private final Map<PopupItem, PopupItem> groupByItem = new HashMap<>();
    private final Map<PopupItem, SubMenu> subMenuByGroup = new HashMap<>();

    private final Set<PopupItem> itemsToSetDisabled = new HashSet<>();
    private final Set<PopupItem> itemsToSetInvisible = new HashSet<>();

    public static PopupMenuAgent getMenu()
    {
        return new PopupMenuAgent();
    }

    public PopupMenuAgent add(PopupItem item)
    {
        this.addItem(item);
        return this;
    }

    public PopupMenuAgent addToGroup(PopupItem item, PopupItem group)
    {
        if(this.addItem(item))
        {
            if(!this.groupByItem.containsKey(item))
            {
                this.groupByItem.put(item, group);
            }
            else
            {
                Log.w(Constants.LOG_TAG, String.format("PopupMenuAgent.addToGroup:: Group [#%d - %s] for Item [#%d - %s] already added", group.ordinal(), group, item.ordinal(), item));
            }
        }
        return this;
    }

    private boolean addItem(PopupItem item)
    {
        if(!this.itemsToAdd.contains(item))
        {
            this.itemsToAdd.add(item);
            return true;
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("PopupMenuAgent.add:: Item [#%d - %s] already added", item.ordinal(), item));
            return false;
        }
    }

    public PopupMenuAgent setEnabled(PopupItem item, boolean setEnabled)
    {
        if(!setEnabled)
        {
            this.itemsToSetDisabled.add(item);
        }
        return this;
    }

    public PopupMenuAgent setVisible(PopupItem item, boolean setVisible)
    {
        if(!setVisible)
        {
            this.itemsToSetInvisible.add(item);
        }
        return this;
    }

    public void show(Context context, View view)
    {
        if(context instanceof IPopupMenuAgentClient)
        {
            Log.d(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: showing PopupMenu with [%d] Item(s)", this.itemsToAdd.size()));

            PopupMenu menu = new PopupMenu(context, view);

            for(PopupItem item : this.itemsToAdd)
            {
                if(isGroup(item))
                {
                    if(this.isGroupInRootMenu(item))
                    {
                        Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: adding group [#%d - %s] to ROOT", item.ordinal(), item));
                        this.subMenuByGroup.put(item, menu.getMenu().addSubMenu(item.ordinal(), item.ordinal(), item.ordinal(), item.stringResource));
                    }
                    else //isGroupInSubMenu
                    {
                        if(this.subMenuByGroup.containsKey(this.groupByItem.get(item)))
                        {
                            PopupItem group = this.groupByItem.get(item);
                            Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: adding Group [#%d - %s] to Group [#%d - %s]", item.ordinal(), item, group.ordinal(), group));
                            this.subMenuByGroup.put(item, this.subMenuByGroup.get(group).addSubMenu(item.ordinal(), item.ordinal(), item.ordinal(), item.stringResource));
                        }
                        else
                        {
                            Log.e(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: Group for Group [#%d - %s] not found", item.ordinal(), item));
                            break;
                        }
                    }
                }
                else if(this.isItemInRootMenu(item))
                {
                    Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: adding Item [#%d - %s] to ROOT", item.ordinal(), item));
                    menu.getMenu().add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource);
                }
                else //IsItemInGroup
                {
                    if((this.subMenuByGroup.containsKey(this.groupByItem.get(item))))
                    {
                        PopupItem group = this.groupByItem.get(item);
                        Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: adding Item [#%d - %s] to Group [#%d - %s]", item.ordinal(), item, group.ordinal(), group));
                        this.subMenuByGroup.get(group).add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource);
                    }
                    else
                    {
                        Log.e(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: Group for Item [#%d - %s] not found", item.ordinal(), item));
                        break;
                    }
                }
            }

            for(PopupItem item : itemsToSetInvisible)
            {
                Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: setting item invisible: [#%d - %s]", item.ordinal(), item));
                menu.getMenu().findItem(item.ordinal()).setVisible(false);
                this.itemsToSetDisabled.remove(item); // no need to set disabled when invisible
            }

            for(PopupItem item : this.itemsToSetDisabled)
            {
                Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: setting item disabled: [#%d - %s]",item.ordinal(), item));
                menu.getMenu().findItem(item.ordinal()).setEnabled(false);
            }

            menu.setOnMenuItemClickListener(this.getMenuItemClickListener((IPopupMenuAgentClient) context));
            menu.show();
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: [%s] does not implement IPopupMenuAgentClient", context.getClass().getSimpleName()));
        }
    }
    private boolean isGroup(PopupItem item)
    {
        return this.groupByItem.containsValue(item);
    }

    private boolean isGroupInRootMenu(PopupItem item)
    {
        return isGroup(item) && !this.groupByItem.containsKey(item);
    }

    private boolean isItemInRootMenu(PopupItem item)
    {
        return !this.groupByItem.containsKey(item);
    }

    private PopupMenu.OnMenuItemClickListener getMenuItemClickListener(final IPopupMenuAgentClient client)
    {
        return new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                PopupItem popupItem = PopupItem.getValue(menuItem.getItemId());
                Log.i(Constants.LOG_TAG, String.format("PopupMenuAgent.onMenuItemClick:: Item [#%d - %s] in [%s] clicked", popupItem.ordinal(), popupItem, client.getClass().getSimpleName()));

                if(popupItem == PopupItem.NO_FUNCTION)
                {
                    Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.onMenuItemClick:: Item [#%d - %s] in [%s] has no function", popupItem.ordinal(), popupItem, client.getClass().getSimpleName()));
                }
                else
                {
                    client.handlePopupItemClicked(popupItem);
                }

                return true;
            }
        };
    }
}
