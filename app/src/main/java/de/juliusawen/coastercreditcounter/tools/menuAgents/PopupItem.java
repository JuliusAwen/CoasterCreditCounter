package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.util.Log;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;

public enum PopupItem
{
    NO_FUNCTION(R.string.menu_item_no_function),

    SORT(R.string.menu_item_sort),
    SORT_LOCATIONS(R.string.menu_item_sort_locations),
    SORT_PARKS(R.string.menu_item_sort_parks),
    SORT_ATTRACTIONS(R.string.menu_item_sort_attractions),

    ADD(R.string.menu_item_add),
    ADD_LOCATION(R.string.menu_item_add_location),
    ADD_PARK(R.string.menu_item_add_park),

    EDIT_ELEMENT(R.string.menu_item_edit),
    EDIT_LOCATION(R.string.menu_item_edit),
    EDIT_PARK(R.string.menu_item_edit),
    EDIT_CUSTOM_ATTRACTION(R.string.menu_item_edit),

    DELETE_ELEMENT(R.string.menu_item_delete),
    DELETE_ATTRACTION(R.string.menu_item_delete),

    REMOVE_LOCATION(R.string.menu_item_remove),

    RELOCATE_ELEMENT(R.string.menu_item_relocate),

    ASSIGN_TO_ATTRACTIONS(R.string.menu_item_assign_to_attractions),
    SET_AS_DEFAULT(R.string.menu_item_set_as_default);

    public final int stringResource;

    PopupItem(int stringResource)
    {
        this.stringResource = stringResource;
    }

    public static PopupItem getValue(int ordinal)
    {
        if(PopupItem.values().length >= ordinal)
        {
            return PopupItem.values()[ordinal];
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("PopupItem.getValue:: ordinal [%s] out of bounds (Enum has [%s] values) - returning NO_FUNCTION", ordinal, values().length));
            return NO_FUNCTION;
        }
    }
}
