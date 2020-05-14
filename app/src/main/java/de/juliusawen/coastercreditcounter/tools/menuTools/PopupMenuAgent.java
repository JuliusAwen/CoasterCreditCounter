package de.juliusawen.coastercreditcounter.tools.menuTools;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.PopupMenu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.tools.logger.Log;

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
                Log.w(String.format(Locale.getDefault(), "Group [#%d - %s] for Item [#%d - %s] already added", group.ordinal(), group, item.ordinal(), item));
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
            Log.w(String.format(Locale.getDefault(), "Item [#%d - %s] already added", item.ordinal(), item));
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
            Log.d(String.format(Locale.getDefault(), "showing PopupMenu with [%d] Item(s)", this.itemsToAdd.size()));

            PopupMenu menu = new PopupMenu(context, view);

            for(PopupItem item : this.itemsToAdd)
            {
                if(isGroup(item))
                {
                    if(this.isGroupInRootMenu(item))
                    {
                        Log.v(String.format(Locale.getDefault(), "adding group [#%d - %s] to ROOT", item.ordinal(), item));
                        this.subMenuByGroup.put(item, menu.getMenu().addSubMenu(item.ordinal(), item.ordinal(), item.ordinal(), item.stringResource));
                    }
                    else //isGroupInSubMenu
                    {
                        if(this.subMenuByGroup.containsKey(this.groupByItem.get(item)))
                        {
                            PopupItem group = this.groupByItem.get(item);
                            Log.v(String.format(Locale.getDefault(), "adding Group [#%d - %s] to Group [#%d - %s]", item.ordinal(), item, group.ordinal(), group));
                            this.subMenuByGroup.put(item, this.subMenuByGroup.get(group).addSubMenu(item.ordinal(), item.ordinal(), item.ordinal(), item.stringResource));
                        }
                        else
                        {
                            Log.e(String.format(Locale.getDefault(), "Group for Group [#%d - %s] not found", item.ordinal(), item));
                            break;
                        }
                    }
                }
                else if(this.isItemInRootMenu(item))
                {
                    Log.v(String.format(Locale.getDefault(), "adding Item [#%d - %s] to ROOT", item.ordinal(), item));
                    menu.getMenu().add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource);
                }
                else //IsItemInGroup
                {
                    if((this.subMenuByGroup.containsKey(this.groupByItem.get(item))))
                    {
                        PopupItem group = this.groupByItem.get(item);
                        Log.v(String.format(Locale.getDefault(), "adding Item [#%d - %s] to Group [#%d - %s]", item.ordinal(), item, group.ordinal(), group));
                        this.subMenuByGroup.get(group).add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource);
                    }
                    else
                    {
                        Log.e(String.format(Locale.getDefault(), "Group for Item [#%d - %s] not found", item.ordinal(), item));
                        break;
                    }
                }
            }

            for(PopupItem item : itemsToSetInvisible)
            {
                Log.v(String.format(Locale.getDefault(), "setting item invisible: [#%d - %s]", item.ordinal(), item));
                menu.getMenu().findItem(item.ordinal()).setVisible(false);
                this.itemsToSetDisabled.remove(item); // no need to set disabled when invisible
            }

            for(PopupItem item : this.itemsToSetDisabled)
            {
                Log.v(String.format(Locale.getDefault(), "setting item disabled: [#%d - %s]",item.ordinal(), item));
                menu.getMenu().findItem(item.ordinal()).setEnabled(false);
            }

            menu.setOnMenuItemClickListener(this.getMenuItemClickListener((IPopupMenuAgentClient) context));
            menu.show();
        }
        else
        {
            Log.e(String.format("[%s] does not implement IPopupMenuAgentClient", context.getClass().getSimpleName()));
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
                Log.i(String.format(Locale.getDefault(), "Item [#%d - %s] in [%s] clicked", popupItem.ordinal(), popupItem, client.getClass().getSimpleName()));

                if(popupItem == PopupItem.NO_FUNCTION)
                {
                    Log.v(String.format(Locale.getDefault(), "Item [#%d - %s] in [%s] has no function", popupItem.ordinal(), popupItem, client.getClass().getSimpleName()));
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
