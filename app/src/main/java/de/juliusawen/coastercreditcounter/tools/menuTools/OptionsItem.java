package de.juliusawen.coastercreditcounter.tools.menuTools;

import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum OptionsItem
{
    NO_FUNCTION(R.string.menu_item_no_function, -1),


    SORT(R.string.menu_item_sort, -1),

    SORT_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_CREDIT_TYPES(R.string.menu_item_sort, -1),
    SORT_CATEGORIES(R.string.menu_item_sort, -1),
    SORT_MANUFACTURERS(R.string.menu_item_sort, -1),
    SORT_MODELS(R.string.menu_item_sort, -1),
    SORT_STATUSES(R.string.menu_item_sort, -1),

    SORT_BY(R.string.menu_item_sort_by, -1),

    SORT_BY_NAME(R.string.menu_item_sort_by_name, -1),
    SORT_BY_NAME_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_NAME_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_PARK(R.string.menu_item_sort_by_location, -1),
    SORT_BY_PARK_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_PARK_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_CREDIT_TYPE(R.string.menu_item_sort_by_credit_type, -1),
    SORT_BY_CREDIT_TYPE_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_CREDIT_TYPE_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_CATEGORY(R.string.menu_item_sort_by_category, -1),
    SORT_BY_CATEGORY_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_CATEGORY_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_MANUFACTURER(R.string.menu_item_sort_by_manufacturer, -1),
    SORT_BY_MANUFACTURER_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_MANUFACTURER_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_MODEL(R.string.menu_item_sort_by_model, -1),
    SORT_BY_MODEL_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_MODEL_DESCENDING(R.string.menu_item_sort_descending, -1),

    SORT_BY_STATUS(R.string.menu_item_sort_by_status, -1),
    SORT_BY_STATUS_ASCENDING(R.string.menu_item_sort_ascending, -1),
    SORT_BY_STATUS_DESCENDING(R.string.menu_item_sort_descending, -1),


    GROUP_BY(R.string.menu_item_group_by, -1),
    GROUP_BY_NONE(R.string.menu_item_group_by_none, -1),
    GROUP_BY_PARK(R.string.menu_item_group_by_park, -1),
    GROUP_BY_CREDIT_TYPE(R.string.menu_item_group_by_credit_type, -1),
    GROUP_BY_CATEGORY(R.string.menu_item_group_by_category, -1),
    GROUP_BY_MANUFACTURER(R.string.menu_item_group_by_manufacturer, -1),
    GROUP_BY_MODEL(R.string.menu_item_group_by_model, -1),
    GROUP_BY_STATUS(R.string.menu_item_group_by_status, -1),


    HELP(R.string.menu_item_help, -1),

    //ACTION MENU ITEMS

    EXPAND_ALL(R.string.menu_item_expand_all, R.drawable.expand),
    COLLAPSE_ALL(R.string.menu_item_collapse_all, R.drawable.collapse),

    GO_TO_CURRENT_VISIT(R.string.menu_item_go_to_current_visit, R.drawable.local_activity),
    GO_TO_MANAGE_PROPERTIES(R.string.menu_item_go_to_manage_elements, R.drawable.build),

    ENABLE_EDITING(R.string.menu_item_enable_editing, R.drawable.create),
    DISABLE_EDITING(R.string.menu_item_disable_editing, R.drawable.block),


    //DEVELOPER OPTIONS
    SHOW_BUILD_CONFIG(R.string.menu_item_developer_options_show_build_config, -1),

    SHOW_LOG(R.string.menu_item_developer_options_show_log, -1),
    SHOW_LOG_VERBOSE(R.string.menu_item_developer_options_show_log_verbose, -1),
    SHOW_LOG_DEBUG(R.string.menu_item_developer_options_show_log_debug, -1),
    SHOW_LOG_INFO(R.string.menu_item_developer_options_show_log_info, -1),
    SHOW_LOG_WARNING(R.string.menu_item_developer_options_show_log_warning, -1),
    SHOW_LOG_ERROR(R.string.menu_item_developer_options_show_log_error, -1),

    SHOW_DUMPED_LOG(R.string.menu_item_developer_options_show_dumped_log, -1);

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
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }

    public String toString()
    {
        return String.format(Locale.getDefault(), "[#%d - %s]", this.ordinal(), this.name());
    }
}
