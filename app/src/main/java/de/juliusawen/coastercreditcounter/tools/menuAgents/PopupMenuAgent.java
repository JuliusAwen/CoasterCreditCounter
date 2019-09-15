package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class PopupMenuAgent
{
    private PopupMenu menu;

    private final List<PopupItem> itemsToAdd;
    private final Set<PopupItem> itemsToSetDisabled;
    private final Set<PopupItem> itemsToSetInvisible;
    private final Map<PopupItem, Integer> stringResourcesByItem;

    public static PopupMenuAgent getMenu()
    {
        return new PopupMenuAgent();
    }

    private PopupMenuAgent()
    {
        this.itemsToAdd = new LinkedList<>();
        this.itemsToSetDisabled = new HashSet<>();
        this.itemsToSetInvisible = new HashSet<>();
        this.stringResourcesByItem = this.initializeStringResourcesByItem();
    }

    private Map<PopupItem, Integer> initializeStringResourcesByItem()
    {
        Map<PopupItem, Integer> stringResourcesByItem = new HashMap<>();

        stringResourcesByItem.put(PopupItem.ADD, R.string.menu_item_add);
        stringResourcesByItem.put(PopupItem.ADD_LOCATION, R.string.menu_item_add_location);
        stringResourcesByItem.put(PopupItem.ADD_PARK, R.string.menu_item_add_park);

        stringResourcesByItem.put(PopupItem.SORT, R.string.menu_item_sort);
        stringResourcesByItem.put(PopupItem.SORT_LOCATIONS, R.string.menu_item_sort_locations);
        stringResourcesByItem.put(PopupItem.SORT_PARKS, R.string.menu_item_sort_parks);
        stringResourcesByItem.put(PopupItem.SORT_ATTRACTIONS, R.string.menu_item_sort_attractions);

        stringResourcesByItem.put(PopupItem.EDIT_LOCATION, R.string.menu_item_edit);
        stringResourcesByItem.put(PopupItem.EDIT_PARK, R.string.menu_item_edit);
        stringResourcesByItem.put(PopupItem.EDIT_ELEMENT, R.string.menu_item_edit);
        stringResourcesByItem.put(PopupItem.EDIT_CUSTOM_ATTRACTION, R.string.menu_item_edit);

        stringResourcesByItem.put(PopupItem.REMOVE_LOCATION, R.string.menu_item_remove);
        stringResourcesByItem.put(PopupItem.RELOCATE_ELEMENT, R.string.menu_item_relocate);

        stringResourcesByItem.put(PopupItem.DELETE_ELEMENT, R.string.menu_item_delete);
        stringResourcesByItem.put(PopupItem.DELETE_ATTRACTION, R.string.menu_item_delete);

        stringResourcesByItem.put(PopupItem.ASSIGN_TO_ATTRACTIONS, R.string.menu_item_assign_to_attractions);
        stringResourcesByItem.put(PopupItem.SET_AS_DEFAULT, R.string.menu_item_set_as_default);

        return stringResourcesByItem;
    }


    public PopupMenuAgent add(PopupItem item)
    {
        this.itemsToAdd.add(item);

        return this;
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

            this.menu = new PopupMenu(context, view);

            for(PopupItem item : this.itemsToAdd)
            {
                Menu submenu;

                switch(item)
                {
                    case SORT_ATTRACTIONS:
                    case EDIT_LOCATION:
                    case EDIT_PARK:
                    case EDIT_ELEMENT:
                    case EDIT_CUSTOM_ATTRACTION:
                    case REMOVE_LOCATION:
                    case RELOCATE_ELEMENT:
                    case DELETE_ELEMENT:
                    case DELETE_ATTRACTION:
                    case ASSIGN_TO_ATTRACTIONS:
                    case SET_AS_DEFAULT:
                        this.addItemToMenu(item);
                        break;

                    case ADD:
                        submenu = this.addSubMenu(item);
                        this.addItemToSubMenu(PopupItem.ADD_LOCATION, submenu);
                        this.addItemToSubMenu(PopupItem.ADD_PARK, submenu);
                        break;

                    case SORT:
                        submenu = this.addSubMenu(item);
                        this.addItemToSubMenu(PopupItem.SORT_LOCATIONS, submenu);
                        this.addItemToSubMenu(PopupItem.SORT_PARKS, submenu);
                        break;

                    default:
                        Log.e(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: PopupItem [%s] can not be shown", item));
                        return;
                }
            }

            for(PopupItem item : itemsToSetInvisible)
            {
                Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: setting item invisible: [%s]", item));
                this.menu.getMenu().findItem(item.ordinal()).setVisible(false);
                this.itemsToSetDisabled.remove(item); // no need to set disabled when invisible
            }

            for(PopupItem item : this.itemsToSetDisabled)
            {
                Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: setting item disabled: [%s]", item));
                this.menu.getMenu().findItem(item.ordinal()).setEnabled(false);
            }

            this.menu.setOnMenuItemClickListener(this.getMenuItemClickListener((IPopupMenuAgentClient) context));
            this.menu.show();
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: [%s] does not implement IPopupMenuAgentClient", context.getClass().getSimpleName()));
        }
    }

    private MenuItem addItemToMenu(PopupItem item)
    {
        Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.addItemToMenu:: adding [%s]", item));
        return this.menu.getMenu().add(Menu.NONE, item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
    }

    private Menu addSubMenu(PopupItem item)
    {
        Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.addSubMenu:: adding submenu [%s]", item));
        return this.menu.getMenu().addSubMenu(item.ordinal(), item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
    }

    private MenuItem addItemToSubMenu(PopupItem item, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.addItemToSubMenu:: adding [%s] to submenu", item));
        return subMenu.add(Menu.NONE, item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
    }

    private PopupMenu.OnMenuItemClickListener getMenuItemClickListener(final IPopupMenuAgentClient client)
    {
        return new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                if(menuItem.getItemId() <= PopupItem.values().length)
                {
                    PopupItem popupItem = PopupItem.values()[menuItem.getItemId()];

                    Log.i(Constants.LOG_TAG, String.format("PopupMenuAgent.onMenuItemClick:: MenuItem [%s] in [%s] clicked", popupItem, client.getClass().getSimpleName()));

                    switch(popupItem)
                    {
                        case NO_FUNCTION:
                        case SORT:
                        case ADD:
                            Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.onMenuItemClick:: MenuItem [%s] in [%s] has no function", popupItem, client.getClass().getSimpleName()));
                            break;

                        default:
                            client.handlePopupItemClicked(popupItem);
                    }

                    return true;
                }
                else
                {
                    Log.e(Constants.LOG_TAG, String.format("PopupMenuAgent.onMenuItemClick:: MenuItem [%d] for [%s] is invalid", menuItem.getItemId(), client.getClass().getSimpleName()));
                    return false;
                }
            }
        };
    }
}
