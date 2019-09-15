package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class PopupMenuAgent
{
    public static final PopupItem ADD = PopupItem.ADD;
    public static final PopupItem SORT = PopupItem.SORT;
    public static final PopupItem SORT_LOCATIONS = PopupItem.SORT_LOCATIONS;
    public static final PopupItem SORT_PARKS = PopupItem.SORT_PARKS;
    public static final PopupItem SORT_ATTRACTIONS = PopupItem.SORT_ATTRACTIONS;
    public static final PopupItem EDIT_LOCATION = PopupItem.EDIT_LOCATION;
    public static final PopupItem EDIT_PARK = PopupItem.EDIT_PARK;
    public static final PopupItem EDIT_ELEMENT = PopupItem.EDIT_ELEMENT;
    public static final PopupItem EDIT_CUSTOM_ATTRACTION = PopupItem.EDIT_CUSTOM_ATTRACTION;
    public static final PopupItem REMOVE_ELEMENT = PopupItem.REMOVE_ELEMENT;
    public static final PopupItem RELOCATE_ELEMENT = PopupItem.RELOCATE_ELEMENT;
    public static final PopupItem DELETE_ELEMENT = PopupItem.DELETE_ELEMENT;
    public static final PopupItem DELETE_ATTRACTION = PopupItem.DELETE_ATTRACTION;

    public static final PopupItem ASSIGN_TO_ATTRACTIONS = PopupItem.ASSIGN_TO_ATTRACTIONS;
    public static final PopupItem SET_AS_DEFAULT = PopupItem.SET_AS_DEFAULT;


    private PopupMenu menu;

    private final List<PopupItem> itemsToAdd;
    private final Map<PopupItem, Boolean> setEnabledByItem;
    private final Map<PopupItem, Boolean> setVisibleByItem;
    private final Map<PopupItem, PopupItem> submenuByItem;

    private final Map<PopupItem, Integer> stringResourcesByItem;

    public static PopupMenuAgent getAgent()
    {
        return new PopupMenuAgent();
    }

    private PopupMenuAgent()
    {
        this.itemsToAdd = new LinkedList<>();
        this.setEnabledByItem = new HashMap<>();
        this.setVisibleByItem = new HashMap<>();
        this.submenuByItem = new HashMap<>();

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

        stringResourcesByItem.put(PopupItem.REMOVE_ELEMENT, R.string.menu_item_remove);
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


    public void show(Context context, View view)
    {
        if(context instanceof IPopupMenuAgentClient)
        {
            Log.d(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: showing PopupMenu with [%d] Item(s)", this.itemsToAdd.size()));

            this.menu = new PopupMenu(context, view);

            for(PopupItem item : this.itemsToAdd)
            {
                MenuItem menuItem;
                Menu submenu;

                switch(item)
                {
                    case SORT_ATTRACTIONS:
                    case EDIT_LOCATION:
                    case EDIT_PARK:
                    case EDIT_ELEMENT:
                    case EDIT_CUSTOM_ATTRACTION:
                    case REMOVE_ELEMENT:
                    case RELOCATE_ELEMENT:
                    case DELETE_ELEMENT:
                    case DELETE_ATTRACTION:
                    case ASSIGN_TO_ATTRACTIONS:
                    case SET_AS_DEFAULT:
                        menuItem = this.addItemToMenu(item);
                        this.handleSetEnabled(item, menuItem);
                        this.handleSetVisible(item, menuItem);
                        break;

                    case ADD: //createLocation/Park
                        submenu = this.addSubMenu(item);
                        menuItem = this.addItemToSubMenu(PopupItem.ADD_LOCATION, submenu);
                        menuItem.setEnabled(this.setEnabledByItem.get(PopupItem.ADD_LOCATION) != null ? this.setEnabledByItem.get(PopupItem.ADD_LOCATION) : true);
                        menuItem.setVisible(this.setVisibleByItem.get(PopupItem.ADD_LOCATION) != null ? this.setVisibleByItem.get(PopupItem.ADD_LOCATION) : true);
                        menuItem = this.addItemToSubMenu(PopupItem.ADD_PARK, submenu);
                        menuItem.setEnabled(this.setEnabledByItem.get(PopupItem.ADD_PARK) != null ? this.setEnabledByItem.get(PopupItem.ADD_PARK) : true);
                        menuItem.setVisible(this.setVisibleByItem.get(PopupItem.ADD_PARK) != null ? this.setVisibleByItem.get(PopupItem.ADD_PARK) : true);
                        break;

                    case SORT: //SortLocations/Parks
                        submenu = this.addSubMenu(item);
                        menuItem = this.addItemToSubMenu(PopupItem.SORT_LOCATIONS, submenu);
                        menuItem.setEnabled(this.setEnabledByItem.get(PopupItem.SORT_LOCATIONS) != null ? this.setEnabledByItem.get(PopupItem.SORT_LOCATIONS) : true);
                        menuItem.setVisible(this.setVisibleByItem.get(PopupItem.SORT_LOCATIONS) != null ? this.setVisibleByItem.get(PopupItem.SORT_LOCATIONS) : true);
                        menuItem = this.addItemToSubMenu(PopupItem.SORT_PARKS, submenu);
                        menuItem.setEnabled(this.setEnabledByItem.get(PopupItem.SORT_PARKS) != null ? this.setEnabledByItem.get(PopupItem.SORT_PARKS) : true);
                        menuItem.setVisible(this.setVisibleByItem.get(PopupItem.SORT_PARKS) != null ? this.setVisibleByItem.get(PopupItem.SORT_PARKS) : true);
                        break;

                    default:
                        Log.e(Constants.LOG_TAG, String.format("PopupMenuAgent.show:: unknown PopupItem [%s]", item));
                        return;
                }
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
        Menu subMenu = this.menu.getMenu().addSubMenu(item.ordinal(), item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
        this.menu.getMenu().setGroupEnabled(item.ordinal(), this.setEnabledByItem.get(item) != null ? this.setEnabledByItem.get(item) : true);
        this.menu.getMenu().setGroupVisible(item.ordinal(), this.setVisibleByItem.get(item) != null ? this.setVisibleByItem.get(item) : true);
        return subMenu;
    }

    private MenuItem addItemToSubMenu(PopupItem item, Menu subMenu)
    {
        Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.addItemToSubMenu:: adding [%s] to subbenu", item));
        return subMenu.add(Menu.NONE, item.ordinal(), Menu.NONE, this.stringResourcesByItem.get(item));
    }

    private void handleSetEnabled(PopupItem item, MenuItem menuItem)
    {
        if(this.setEnabledByItem.containsKey(item))
        {
            Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.handleSetEnabled:: setting [%s] enabled [%S]", item, this.setEnabledByItem.get(item)));
            menuItem.setEnabled(this.setEnabledByItem.get(item));
        }
    }

    private void handleSetVisible(PopupItem item, MenuItem menuItem)
    {
        if(this.setVisibleByItem.containsKey(item))
        {
            Log.v(Constants.LOG_TAG, String.format("PopupMenuAgent.handleSetVisible:: setting [%s] visible [%S]", item, this.setVisibleByItem.get(item)));
            menuItem.setVisible(this.setVisibleByItem.get(item));
        }
    }

    public PopupMenuAgent setEnabled(PopupItem item, boolean setEnabled)
    {
        this.setEnabledByItem.put(item, setEnabled);

        return this;
    }

    public PopupMenuAgent setVisible(PopupItem item, boolean setVisible)
    {
        this.setVisibleByItem.put(item, setVisible);

        return this;
    }

    private PopupMenu.OnMenuItemClickListener getMenuItemClickListener(final IPopupMenuAgentClient client)
    {
        return new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if(item.getItemId() <= PopupItem.values().length)
                {
                    Log.i(Constants.LOG_TAG, String.format("PopupMenuAgent.onMenuItemClick:: MenuItem [%s] clicked", PopupItem.values()[item.getItemId()].toString()));

                    switch(PopupItem.values()[item.getItemId()])
                    {
                        case NO_FUNCTION:
                        case SORT:
                        case ADD:
                            break;

                        case ADD_LOCATION:
                            client.handleAddLocationClicked();
                            break;

                        case ADD_PARK:
                            client.handleAddParkClicked();
                            break;

                        case SORT_LOCATIONS:
                            client.handleSortLocationsClicked();
                            break;

                        case SORT_PARKS:
                            client.handleSortParksClicked();
                            break;

                        case SORT_ATTRACTIONS:
                            client.handleSortAttractionsClicked();
                            break;

                        case EDIT_LOCATION:
                            client.handleEditLocationClicked();
                            break;

                        case EDIT_PARK:
                            client.handleEditParkClicked();
                            break;

                        case EDIT_ELEMENT:
                            client.handleEditElementClicked();
                            break;

                        case EDIT_CUSTOM_ATTRACTION:
                            client.handleEditCustomAttractionClicked();
                            break;

                        case REMOVE_ELEMENT:
                            client.handleRemoveElementClicked();
                            break;

                        case RELOCATE_ELEMENT:
                            client.handleRelocateElementClicked();
                            break;

                        case DELETE_ELEMENT:
                            client.handleDeleteElementClicked();
                            break;

                        case DELETE_ATTRACTION:
                            client.handleDeleteAttractionClicked();
                            break;

                        case ASSIGN_TO_ATTRACTIONS:
                            client.handleAssignToAttractionsClicked();
                            break;

                        case SET_AS_DEFAULT:
                            client.handleSetAsDefaultClicked();
                            break;

                        default:
                            return false;
                    }

                    return true;
                }
                else
                {
                    Log.e(Constants.LOG_TAG, "PopupMenuAgent.onMenuItemClick:: MenuItem [%s] not valid");
                    return false;
                }
            }
        };
    }


    private enum PopupItem
    {
        NO_FUNCTION,

        ADD,
        ADD_LOCATION,
        ADD_PARK,

        EDIT_ELEMENT,
        EDIT_LOCATION,
        EDIT_PARK,

        EDIT_CUSTOM_ATTRACTION,

        DELETE_ELEMENT,
        DELETE_ATTRACTION,

        REMOVE_ELEMENT,
        RELOCATE_ELEMENT,

        SORT,
        SORT_LOCATIONS,
        SORT_PARKS,
        SORT_ATTRACTIONS,

        ASSIGN_TO_ATTRACTIONS,
        SET_AS_DEFAULT,
    }
}
