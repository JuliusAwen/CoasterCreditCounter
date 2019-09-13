package de.juliusawen.coastercreditcounter.frontend.activityDistributor;

public enum RequestCode
{
    INVALID,

    CREATE_LOCATION,
    CREATE_PARK,
    CREATE_VISIT,
    CREATE_ATTRACTION_CATEGORY,
    CREATE_MANUFACTURER,
    CREATE_STATUS,
    CREATE_CUSTOM_ATTRACTION,

    SHOW_LOCATION,
    SHOW_PARK,
    SHOW_VISIT,

    MANAGE_ATTRACTION_CATEGORIES,
    MANAGE_MANUFACTURERS,
    MANAGE_STATUSES,

    ASSIGN_CATEGORY_TO_ATTRACTIONS,
    ASSIGN_MANUFACTURERS_TO_ATTRACTIONS,
    ASSIGN_STATUS_TO_ATTRACTIONS,

    EDIT_LOCATION,
    EDIT_PARK,
    EDIT_ATTRACTION_CATEGORY,
    EDIT_MANUFACTURER,
    EDIT_STATUS,
    EDIT_CUSTOM_ATTRACTION,

    SORT_LOCATIONS,
    SORT_PARKS,
    SORT_ATTRACTIONS,
    SORT_ATTRACTION_CATEGORIES,
    SORT_MANUFACTURERS,
    SORT_STATUSES,

    PICK_LOCATIONS,
    PICK_PARKS,
    PICK_ATTRACTIONS,
    PICK_STATUS,
    PICK_VISIT,
    PICK_MANUFACTURER,
    PICK_ATTRACTION_CATEGORY,

    DELETE,
    REMOVE,

    PERMISSION_CODE_WRITE_EXTERNAL_STORAGE,
    OVERWRITE_FILE,
    OVERWRITE_CONTENT,

    RELOCATE,

    SET_AS_DEFAULT,

    HANDLE_EXISTING_VISIT,
}
