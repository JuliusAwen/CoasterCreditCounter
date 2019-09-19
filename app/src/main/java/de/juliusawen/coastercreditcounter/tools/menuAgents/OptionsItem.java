package de.juliusawen.coastercreditcounter.tools.menuAgents;

import android.util.Log;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;

public enum OptionsItem
{
    NO_FUNCTION(R.string.menu_item_no_function, -1),


    SORT(R.string.menu_item_sort, -1),

    SORT_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_CREDIT_TYPES(R.string.menu_item_sort, -1),
    SORT_CATEGORIES(R.string.menu_item_sort, -1),
    SORT_MANUFACTURERS(R.string.menu_item_sort, -1),
    SORT_STATUSES(R.string.menu_item_sort, -1),

    SORT_BY(R.string.menu_item_sort_by, -1),

    SORT_BY_NAME(R.string.menu_item_sort_by_name, -1),
    SORT_BY_NAME_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_NAME_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_LOCATION(R.string.menu_item_sort_by_location, -1),
    SORT_BY_LOCATION_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_LOCATION_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_CREDIT_TYPE(R.string.menu_item_sort_by_credit_type, -1),
    SORT_BY_CREDIT_TYPE_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_CREDIT_TYPE_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_CATEGORY(R.string.menu_item_sort_by_category, -1),
    SORT_BY_CATEGORY_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_CATEGORY_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_MANUFACTURER(R.string.menu_item_sort_by_manufacturer, -1),
    SORT_BY_MANUFACTURER_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_MANUFACTURER_DESCENDING(R.string.menu_item_sort_descending, -1),


    GROUP_BY(R.string.menu_item_group_by, -1),
    GROUP_BY_LOCATION(R.string.menu_item_group_by_location, -1),
    GROUP_BY_CREDIT_TYPE(R.string.menu_item_group_by_credit_type, -1),
    GROUP_BY_CATEGORY(R.string.menu_item_group_by_category, -1),
    GROUP_BY_MANUFACTURER(R.string.menu_item_group_by_manufacturer, -1),
    GROUP_BY_STATUS(R.string.menu_item_group_by_status, -1),


    //ACTION MENU ITEMS

    GO_TO_CURRENT_VISIT(R.string.menu_item_go_to_current_visit, R.drawable.ic_baseline_local_activity),
    ENABLE_EDITING(R.string.menu_item_enable_editing, R.drawable.ic_baseline_create),
    DISABLE_EDITING(R.string.menu_item_disable_editing, R.drawable.ic_baseline_block),

    EXPAND_ALL(R.string.menu_item_expand_all, -1),
    COLLAPSE_ALL(R.string.menu_item_collapse_all, -1),

    HELP(R.string.menu_item_help, -1);

    public final int stringResource;
    public final int drawableResource;

    OptionsItem(int stringResource, int drawableResource)
    {
        this.stringResource = stringResource;
        this.drawableResource = drawableResource;
    }

    public static OptionsItem getValue(int ordinal)
    {
        if(OptionsItem.values().length >= ordinal)
        {
            return OptionsItem.values()[ordinal];
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("OptionsItem.getValue:: ordinal [%s] out of bounds (Enum has [%s] values) - returning NO_FUNCTION", ordinal, values().length));
            return NO_FUNCTION;
        }
    }
}
