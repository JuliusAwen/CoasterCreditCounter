package de.juliusawen.coastercreditcounter.tools.activityDistributor;

import de.juliusawen.coastercreditcounter.tools.logger.Log;

public enum RequestCode
{
    INVALID,

    NAVIGATE,

    CREATE_LOCATION,
    CREATE_PARK,
    CREATE_VISIT,
    CREATE_CREDIT_TYPE,
    CREATE_CATEGORY,
    CREATE_MANUFACTURER,
    CREATE_MODEL,
    CREATE_STATUS,
    CREATE_ATTRACTION,
    CREATE_NOTE,

    SHOW_LOCATIONS,
    SHOW_VISIT,
    SHOW_ATTRACTION,

    SHOW_PARK,
    SHOW_PARK_OVERVIEW,
    SHOW_VISITS,
    SHOW_ATTRACTIONS,

    SHOW_CREDIT_TYPE,
    SHOW_CATEGORY,
    SHOW_MANUFACTURER,
    SHOW_MODEL,
    SHOW_STATUS,

    MANAGE_CREDIT_TYPES,
    MANAGE_CATEGORIES,
    MANAGE_MANUFACTURERS,
    MANAGE_MODELS,
    MANAGE_STATUSES,

    ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS,
    ASSIGN_CATEGORY_TO_ATTRACTIONS,
    ASSIGN_MANUFACTURER_TO_ATTRACTIONS,
    ASSIGN_STATUS_TO_ATTRACTIONS,

    EDIT_LOCATION,
    EDIT_PARK,
    EDIT_CREDIT_TYPE,
    EDIT_CATEGORY,
    EDIT_MANUFACTURER,
    EDIT_MODEL,
    EDIT_STATUS,
    EDIT_ATTRACTION,
    EDIT_NOTE,

    SORT_LOCATIONS,
    SORT_PARKS,
    SORT_ATTRACTIONS,
    SORT_CREDIT_TYPES,
    SORT_CATEGORIES,
    SORT_MANUFACTURERS,
    SORT_MODELS,
    SORT_STATUSES,

    PICK_VISIT,
    PICK_ATTRACTIONS,
    PICK_CREDIT_TYPE,
    PICK_CATEGORY,
    PICK_MANUFACTURER,
    PICK_MODEL,
    PICK_STATUS,

    DELETE,
    REMOVE,

    EXPORT_CONTENT,
    IMPORT_CONTENT,
    PICK_IMPORT_FILE_LOCATION,
    PICK_IMPORT_FILE,
    PICK_EXPORT_FILE_LOCATION,

    RELOCATE,

    SET_AS_DEFAULT,

    HANDLE_EXISTING_VISIT,

    DEVELOPER_OPTIONS;

    public static RequestCode getValue(int ordinal)
    {
        if(RequestCode.values().length >= ordinal)
        {
            return RequestCode.values()[ordinal];
        }
        else
        {
            Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
            return values()[0];
        }
    }

    public String toString()
    {
        return String.format("RequestCode[%s]", this.name());
    }
}
