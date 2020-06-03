package de.juliusawen.coastercreditcounter.tools.menuTools;

import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class OptionsMenuProvider
{
    private final Set<OptionsItem> itemsToAdd = new LinkedHashSet<>();

    private final Map<OptionsItem, OptionsItem> groupByItem = new HashMap<>();
    private final Map<OptionsItem, SubMenu> subMenuByGroup = new HashMap<>();

    private final Map<OptionsItem, Boolean> setEnabledByItem = new HashMap<>();
    private final Map<OptionsItem, Boolean> setVisibleByItem = new HashMap<>();

    public OptionsMenuProvider add(OptionsItem item)
    {
        this.addItem(item);
        return this;
    }

    public OptionsMenuProvider addToGroup(OptionsItem group, OptionsItem item)
    {
        this.addItem(item);
        this.groupByItem.put(item, group);
        return this;
    }

    private void addItem(OptionsItem item)
    {
        this.itemsToAdd.add(item);
    }

    public OptionsMenuProvider setEnabled(OptionsItem item, boolean setEnabled)
    {
        this.setEnabledByItem.put(item, setEnabled);
        return this;
    }

    public OptionsMenuProvider setVisible(OptionsItem item, boolean setVisible)
    {
        this.setVisibleByItem.put(item, setVisible);
        return this;
    }

    public Menu create(Menu menu)
    {
        Log.d(String.format("adding [%s] Item(s) to OptionsMenu", this.itemsToAdd.size()));

        for(OptionsItem item : this.itemsToAdd)
        {
            if(this.isActionItem(item))
            {
                Log.v(String.format(Locale.getDefault(), "adding ITEM %s as ACTION_IF_ROOM to ROOT", item));

                menu.add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource)
                        .setIcon(DrawableProvider.getColoredDrawable(item.drawableResource, R.color.white))
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
            else if(isGroup(item))
            {
                if(this.isGroupInRootMenu(item))
                {
                    Log.v(String.format("adding GROUP %s to ROOT", item));
                    this.subMenuByGroup.put(item, menu.addSubMenu(item.ordinal(), item.ordinal(), item.ordinal(), item.stringResource));
                }
                else //isGroupInSubMenu
                {
                    if(this.subMenuByGroup.containsKey(this.groupByItem.get(item)))
                    {
                        OptionsItem group = this.groupByItem.get(item);
                        Log.v(String.format("adding SUBGROUP %s to GROUP %s", item, group));
                        this.subMenuByGroup.put(item, this.subMenuByGroup.get(group).addSubMenu(item.ordinal(), item.ordinal(), item.ordinal(), item.stringResource));
                    }
                    else
                    {
                        Log.e(String.format("SUBGROUP for GROUP %s not found", item));
                        break;
                    }
                }
            }
            else if(this.isItemInRootMenu(item))
            {
                Log.v(String.format("adding ITEM %s to ROOT", item));
                menu.add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource);
            }
            else //IsItemInGroup
            {
                if((this.subMenuByGroup.containsKey(this.groupByItem.get(item))))
                {
                    OptionsItem group = this.groupByItem.get(item);
                    Log.v(String.format("adding ITEM %s to GROUP %s", item, group));
                    this.subMenuByGroup.get(group).add(Menu.NONE, item.ordinal(), item.ordinal(), item.stringResource);
                }
                else
                {
                    Log.e(String.format("GROUP for ITEM %s not found", item));
                    break;
                }
            }
        }

        this.itemsToAdd.clear();
        this.groupByItem.clear();
        this.subMenuByGroup.clear();

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
                    Log.v(String.format("GROUP %s enabled [%s]", optionsItem, isEnabled));
                }
                else
                {
                    Log.v(String.format("ITEM %s enabled [%s]", optionsItem, isEnabled));
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
                    Log.v(String.format("GROUP %s visible [%s]", optionsItem, isVisible));
                }
                else
                {
                    menuItem.setVisible(isVisible);
                    Log.v(String.format("ITEM %s visible [%s]", optionsItem, isVisible));
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

    public void clear()
    {
        this.itemsToAdd.clear();
        this.groupByItem.clear();
        this.subMenuByGroup.clear();

        Log.i("cleared");
    }
}