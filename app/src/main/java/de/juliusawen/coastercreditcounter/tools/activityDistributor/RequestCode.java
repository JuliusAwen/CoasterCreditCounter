package de.juliusawen.coastercreditcounter.tools.activityDistributor;

import android.util.Log;

import de.juliusawen.coastercreditcounter.globals.Constants;

public enum RequestCode
{
    INVALID,

    CREATE_LOCATION,
    CREATE_PARK,
    CREATE_VISIT,
    CREATE_CREDIT_TYPE,
    CREATE_CATEGORY,
    CREATE_MANUFACTURER,
    CREATE_STATUS,
    CREATE_CUSTOM_ATTRACTION,

    SHOW_LOCATION,
    SHOW_PARK,
    SHOW_VISIT,

    MANAGE_CREDIT_TYPES,
    MANAGE_CATEGORIES,
    MANAGE_MANUFACTURERS,
    MANAGE_STATUSES,

    ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS,
    ASSIGN_CATEGORY_TO_ATTRACTIONS,
    ASSIGN_MANUFACTURERS_TO_ATTRACTIONS,
    ASSIGN_STATUS_TO_ATTRACTIONS,

    EDIT_LOCATION,
    EDIT_PARK,
    EDIT_CREDIT_TYPE,
    EDIT_CATEGORY,
    EDIT_MANUFACTURER,
    EDIT_STATUS,
    EDIT_CUSTOM_ATTRACTION,

    SORT_LOCATIONS,
    SORT_PARKS,
    SORT_ATTRACTIONS,
    SORT_CREDIT_TYPES,
    SORT_CATEGORIES,
    SORT_MANUFACTURERS,
    SORT_STATUSES,

    PICK_VISIT,
    PICK_ATTRACTIONS,
    PICK_CREDIT_TYPE,
    PICK_CATEGORY,
    PICK_MANUFACTURER,
    PICK_STATUS,

    DELETE,
    REMOVE,

    PERMISSION_CODE_WRITE_EXTERNAL_STORAGE,
    OVERWRITE_FILE,
    OVERWRITE_CONTENT,

    RELOCATE,

    SET_AS_DEFAULT,

    HANDLE_EXISTING_VISIT;

    public static RequestCode getValue(int ordinal)
    {
        if(RequestCode.values().length >= ordinal)
        {
            return RequestCode.values()[ordinal];
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("RequestCode.getValue:: ordinal [%s] out of bounds (Enum has [%s] values) - returning INVALID", ordinal, values().length));
            return INVALID;
        }
    }
}
