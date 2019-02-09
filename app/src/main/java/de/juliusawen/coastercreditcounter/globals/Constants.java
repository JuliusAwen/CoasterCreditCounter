package de.juliusawen.coastercreditcounter.globals;

public abstract class Constants
{
    public static final String LOG_TAG = "JA.C";
    public static final String LOG_DIVIDER = "##################### ";
    public static final String LOG_DIVIDER_ON_CREATE = "####### ";
    public static final String LOG_DIVIDER_FINISH = "-------";

    public static final String SIMPLE_DATE_FORMAT_DATE_PATTERN = "dd. MMMM yyyy";
    public static final String SIMPLE_DATE_FORMAT_YEAR_PATTERN = "yyyy";
    public static final String SIMPLE_DATE_FORMAT_TIME_PATTERN = "HH:mm:ss";


    public static final String DATABASE_WRAPPER_DATABASE_MOCK = "de.juliusawen.coding.database_wrapper_database_mock";
    public static final String DATABASE_WRAPPER_JSON_HANDLER = "de.juliusawen.coding.database_wrapper_json_handler";

    public static final String PERSISTENCY_SERVICE_NAME = "de.juliusawen.coding.persistency_service";


    public static final String JSON_STRING_LOCATIONS = "locations";
    public static final String JSON_STRING_PARKS = "parks";
    public static final String JSON_STRING_VISITS = "visits";
    public static final String JSON_STRING_RIDES = "rides";
    public static final String JSON_STRING_ATTRACTIONS = "attractions";
    public static final String JSON_STRING_ATTRACTION_BLUEPRINTS = "attraction blueprints";
    public static final String JSON_STRING_COASTER_BLUEPRINTS = "coaster blueprints";
    public static final String JSON_STRING_CUSTOM_ATTRACTIONS = "custom attractions";
    public static final String JSON_STRING_CUSTOM_COASTERS = "custom coasters";
    public static final String JSON_STRING_STOCK_ATTRACTIONS = "stock attractions";

    public static final String JSON_STRING_NAME = "name";
    public static final String JSON_STRING_UUID = "uuid";
    public static final String JSON_STRING_CHILDREN = "children";

    public static final String JSON_STRING_DAY = "day";
    public static final String JSON_STRING_MONTH = "month";
    public static final String JSON_STRING_YEAR = "year";
    public static final String JSON_STRING_HOUR = "hour";
    public static final String JSON_STRING_MINUTE = "minute";
    public static final String JSON_STRING_SECOND = "second";

    public static final String JSON_STRING_RIDES_BY_ATTRACTIONS = "rides by attractions";
    public static final String JSON_STRING_BLUEPRINT = "blueprint";

    public static final String JSON_STRING_UNTRACKED_RIDE_COUNT = "untracked ride count";
    public static final String JSON_STRING_ATTRACTION_CATEGORY = "attraction category";
    public static final String JSON_STRING_ATTRACTION_CATEGORIES = "attraction categories";
    public static final String JSON_STRING_IS_DEFAULT = "is default";
    public static final String JSON_STRING_MANUFACTURER = "manufacturer";


    public static final String JSON_STRING_DEFAULT_SORT_ORDER = "default sort order";
    public static final String JSON_STRING_EXPAND_LATEST_YEAR_HEADER = "expand latest year header";
    public static final String JSON_STRING_FIRST_DAY_OF_THE_WEEK = "first day of the week";
    public static final String JSON_STRING_DEFAULT_INCREMENT = "default increment";



    public static final String ACTION_SAVE = "de.juliusawen.coding.action_save";
    public static final String ACTION_CREATE = "de.juliusawen.coding.action_create";
    public static final String ACTION_UPDATE = "de.juliusawen.coding.action_update";
    public static final String ACTION_DELETE = "de.juliusawen.coding.action_delete";


    public static final String EXTRA_ELEMENT_UUID = "de.juliusawen.coding.extra_element_uuid";
    public static final String EXTRA_ELEMENTS_UUIDS = "de.juliusawen.coding.extra_elements_uuids";
    public static final String EXTRA_TOOLBAR_TITLE = "de.juliusawen.coding.extra_toolbar_title";
    public static final String EXTRA_TOOLBAR_SUBTITLE = "de.juliusawen.coding.extra_toolbar_subtitle";
    public static final String EXTRA_HELP_TITLE = "de.juliusawen.coding.extra_help_title";
    public static final String EXTRA_HELP_TEXT = "de.juliusawen.coding.extra_help_text";
    public static final String EXTRA_HINT = "de.juliusawen.coding.extra_hint";
    public static final String EXTRA_RESULT_STRING = "de.juliusawen.coding.extra_result_string";
    public static final String EXTRA_ELEMENTS_TO_CREATE_UUIDS = "de.juliusawen.coding.extra_elements_to_create_uuids";
    public static final String EXTRA_ELEMENTS_TO_UPDATE_UUIDS = "de.juliusawen.coding.extra_elements_to_update_uuids";
    public static final String EXTRA_ELEMENTS_TO_DELETE_UUIDS = "de.juliusawen.coding.extra_elements_to_delete_uuids";
    public static final String EXTRA_TYPE_TO_MANAGE = "de.juliusawen.coding.extra_type_to_manage";
    public static final String EXTRA_REQUEST_CODE = "de.juliusawen.coding.extra_request_code";


    public static final String FRAGMENT_ARG_HELP_TITLE = "de.juliusawen.coding.fragment_arg_help_title";
    public static final String FRAGMENT_ARG_HELP_MESSAGE = "de.juliusawen.coding.fragment_arg_help_message";
    public static final String FRAGMENT_ARG_ALERT_DIALOG_ICON_RESOURCE = "de.juliusawen.coding.fragment_arg_alert_dialog_icon_resource";
    public static final String FRAGMENT_ARG_ALERT_DIALOG_TITLE = "de.juliusawen.coding.fragment_arg_alert_dialog_title";
    public static final String FRAGMENT_ARG_ALERT_DIALOG_MESSAGE = "de.juliusawen.coding.fragment_arg_alert_dialog_message";
    public static final String FRAGMENT_ARG_ALERT_DIALOG_POSITIVE_BUTTON_TEXT = "de.juliusawen.coding.fragment_arg_alert_dialog_positive_button_text";
    public static final String FRAGMENT_ARG_ALERT_DIALOG_NEGATIVE_BUTTON_TEXT = "de.juliusawen.coding.fragment_arg_alert_dialog_negative_button_text";
    public static final String FRAGMENT_ARG_ALERT_DIALOG_REQUEST_CODE = "de.juliusawen.coding.fragment_arg_alert_dialog_negative_request_code";
    public static final String FRAGMENT_ARG_PARK_UUID = "de.juliusawen.coding.fragment_arg_park_uuid";


    public static final String FRAGMENT_TAG_HELP_OVERLAY = "de.juliusawen.coding.fragment_tag_help_overlay";
    public static final String FRAGMENT_TAG_CONFIRM_DIALOG = "de.juliusawen.coding.fragment_tag_confirm_dialog";
    public static final String FRAGMENT_TAG_ALERT_DIALOG = "de.juliusawen.coding.fragment_tag_alert_dialog";


    public static  final int SELECTION_ADD = Selection.ADD.ordinal();

    public static  final int SELECTION_CREATE_LOCATION = Selection.CREATE_LOCATION.ordinal();
    public static  final int SELECTION_CREATE_PARK = Selection.CREATE_PARK.ordinal();

    public static  final int SELECTION_EDIT_LOCATION = Selection.EDIT_LOCATION.ordinal();
    public static  final int SELECTION_EDIT_PARK = Selection.EDIT_PARK.ordinal();
    public static  final int SELECTION_EDIT_ELEMENT = Selection.EDIT_ELEMENT.ordinal();

    public static  final int SELECTION_DELETE_ELEMENT = Selection.DELETE_ELEMENT.ordinal();
    public static  final int SELECTION_REMOVE_ELEMENT = Selection.REMOVE_ELEMENT.ordinal();
    public static  final int SELECTION_RELOCATE_ELEMENT = Selection.RELOCATE_ELEMENT.ordinal();

    public static  final int SELECTION_SORT = Selection.SORT.ordinal();
    public static  final int SELECTION_SORT_BY = Selection.SORT_BY.ordinal();
    public static  final int SELECTION_SORT_LOCATIONS = Selection.SORT_LOCATIONS.ordinal();
    public static  final int SELECTION_SORT_PARKS = Selection.SORT_PARKS.ordinal();
    public static  final int SELECTION_SORT_ATTRACTIONS = Selection.SORT_ATTRACTIONS.ordinal();
    public static  final int SELECTION_SORT_ATTRACTION_CATEGORIES = Selection.SORT_ATTRACTION_CATEGORIES.ordinal();
    public static  final int SELECTION_SORT_MANUFACTURERS = Selection.SORT_MANUFACTURERS.ordinal();

    public static  final int SELECTION_ASCENDING = Selection.ASCENDING.ordinal();
    public static  final int SELECTION_DESCENDING = Selection.DESCENDING.ordinal();

    public static  final int SELECTION_SORT_BY_MANUFACTURER = Selection.SORT_BY_MANUFACTURER.ordinal();
    public static  final int SELECTION_SORT_BY_MANUFACTURER_ASCENDING = Selection.SORT_BY_MANUFACTURER_ASCENDING.ordinal();
    public static  final int SELECTION_SORT_BY_MANUFACTURER_DESCENDING = Selection.SORT_BY_MANUFACTURER_DESCENDING.ordinal();

    public static  final int SELECTION_SORT_BY_LOCATION = Selection.SORT_BY_LOCATION.ordinal();
    public static  final int SELECTION_SORT_BY_LOCATION_ASCENDING = Selection.SORT_BY_LOCATION_ASCENDING.ordinal();
    public static  final int SELECTION_SORT_BY_LOCATION_DESCENDING = Selection.SORT_BY_LOCATION_DESCENDING.ordinal();

    public static  final int SELECTION_EXPAND_ALL = Selection.EXPAND_ALL.ordinal();
    public static  final int SELECTION_COLLAPSE_ALL = Selection.COLLAPSE_ALL.ordinal();

    public static  final int SELECTION_ASSIGN_TO_ATTRACTIONS = Selection.ASSIGN_TO_ATTRACTIONS.ordinal();
    public static  final int SELECTION_SET_AS_DEFAULT = Selection.SET_AS_DEFAULT.ordinal();

    public static  final int SELECTION_HELP = Selection.HELP.ordinal();



    public static final int REQUEST_CODE_CREATE_LOCATION = RequestCode.CREATE_LOCATION.ordinal();
    public static final int REQUEST_CODE_CREATE_PARK = RequestCode.CREATE_PARK.ordinal();
    public static final int REQUEST_CODE_CREATE_VISIT = RequestCode.CREATE_VISIT.ordinal();
    public static final int REQUEST_CODE_CREATE_ATTRACTION_CATEGORY = RequestCode.CREATE_ATTRACTION_CATEGORY.ordinal();
    public static final int REQUEST_CODE_CREATE_MANUFACTURER = RequestCode.CREATE_MANUFACTURER.ordinal();

    public static final int REQUEST_CODE_SHOW_LOCATION = RequestCode.SHOW_LOCATION.ordinal();
    public static final int REQUEST_CODE_SHOW_PARK = RequestCode.SHOW_PARK.ordinal();
    public static final int REQUEST_CODE_SHOW_VISIT = RequestCode.SHOW_VISIT.ordinal();

    public static final int REQUEST_CODE_MANAGE_ATTRACTION_CATEGORIES = RequestCode.MANAGE_ATTRACTION_CATEGORIES.ordinal();
    public static final int REQUEST_CODE_MANAGE_MANUFACTURERS = RequestCode.MANAGE_MANUFACTURERS.ordinal();

    public static final int REQUEST_CODE_ASSIGN_CATEGORY_TO_ATTRACTIONS = RequestCode.ASSIGN_CATEGORY_TO_ATTRACTIONS.ordinal();
    public static final int REQUEST_CODE_ASSIGN_MANUFACTURERS_TO_ATTRACTIONS = RequestCode.ASSIGN_MANUFACTURERS_TO_ATTRACTIONS.ordinal();

    public static final int REQUEST_CODE_EDIT_LOCATION = RequestCode.EDIT_LOCATION.ordinal();
    public static final int REQUEST_CODE_EDIT_PARK = RequestCode.EDIT_PARK.ordinal();
    public static final int REQUEST_CODE_EDIT_ATTRACTION_CATEGORY = RequestCode.EDIT_ATTRACTION_CATEGORY.ordinal();
    public static final int REQUEST_CODE_EDIT_MANUFACTURER = RequestCode.EDIT_MANUFACTURER.ordinal();

    public static final int REQUEST_CODE_SORT_LOCATIONS = RequestCode.SORT_LOCATIONS.ordinal();
    public static final int REQUEST_CODE_SORT_PARKS = RequestCode.SORT_PARKS.ordinal();
    public static final int REQUEST_CODE_SORT_ATTRACTIONS = RequestCode.SORT_ATTRACTIONS.ordinal();
    public static final int REQUEST_CODE_SORT_ATTRACTION_CATEGORIES = RequestCode.SORT_ATTRACTION_CATEGORIES.ordinal();
    public static final int REQUEST_CODE_SORT_MANUFACTURERS = RequestCode.SORT_MANUFACTURERS.ordinal();


    public static final int REQUEST_CODE_PICK_LOCATIONS = RequestCode.PICK_LOCATIONS.ordinal();
    public static final int REQUEST_CODE_PICK_PARKS = RequestCode.PICK_PARKS.ordinal();
    public static final int REQUEST_CODE_PICK_ATTRACTIONS = RequestCode.PICK_ATTRACTIONS.ordinal();

    public static final int REQUEST_CODE_DELETE = RequestCode.DELETE.ordinal();
    public static final int REQUEST_CODE_REMOVE = RequestCode.REMOVE.ordinal();

    public static final int REQUEST_CODE_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = RequestCode.PERMISSION_CODE_WRITE_EXTERNAL_STORAGE.ordinal();
    public static final int REQUEST_OVERWRITE_FILE = RequestCode.OVERWRITE_FILE.ordinal();
    public static final int REQUEST_CODE_OVERWRITE_CONTENT = RequestCode.OVERWRITE_CONTENT.ordinal();

    public static final int REQUEST_CODE_RELOCATE = RequestCode.RELOCATE.ordinal();

    public static final int REQUEST_CODE_SET_AS_DEFAULT = RequestCode.SET_AS_DEFAULT.ordinal();



    public static final int TYPE_NONE = Type.NONE.ordinal();
    public static final int TYPE_ATTRACTION_CATEGORY = Type.ATTRACTION_CATEGORY.ordinal();
    public static final int TYPE_MANUFACTURER = Type.MANUFACTURER.ordinal();
    public static final int TYPE_YEAR = Type.YEAR.ordinal();

    private enum Selection
    {
        ADD,

        CREATE_LOCATION,
        CREATE_PARK,

        EDIT_LOCATION,
        EDIT_PARK,
        EDIT_ELEMENT,

        DELETE_ELEMENT,
        REMOVE_ELEMENT,
        RELOCATE_ELEMENT,

        SORT,
        SORT_BY,
        SORT_LOCATIONS,
        SORT_PARKS,
        SORT_ATTRACTIONS,
        SORT_ATTRACTION_CATEGORIES,
        SORT_MANUFACTURERS,

        ASCENDING,
        DESCENDING,

        SORT_BY_MANUFACTURER,
        SORT_BY_MANUFACTURER_ASCENDING,
        SORT_BY_MANUFACTURER_DESCENDING,

        SORT_BY_LOCATION,
        SORT_BY_LOCATION_ASCENDING,
        SORT_BY_LOCATION_DESCENDING,

        EXPAND_ALL,
        COLLAPSE_ALL,

        ASSIGN_TO_ATTRACTIONS,
        SET_AS_DEFAULT,

        HELP,
    }

    private enum RequestCode
    {
        CREATE_LOCATION,
        CREATE_PARK,
        CREATE_VISIT,
        CREATE_ATTRACTION_CATEGORY,
        CREATE_MANUFACTURER,

        SHOW_LOCATION,
        SHOW_PARK,
        SHOW_VISIT,

        MANAGE_ATTRACTION_CATEGORIES,
        MANAGE_MANUFACTURERS,

        ASSIGN_CATEGORY_TO_ATTRACTIONS,
        ASSIGN_MANUFACTURERS_TO_ATTRACTIONS,

        EDIT_LOCATION,
        EDIT_PARK,
        EDIT_ATTRACTION_CATEGORY,
        EDIT_MANUFACTURER,

        SORT_LOCATIONS,
        SORT_PARKS,
        SORT_ATTRACTIONS,
        SORT_ATTRACTION_CATEGORIES,
        SORT_MANUFACTURERS,

        PICK_LOCATIONS,
        PICK_PARKS,
        PICK_ATTRACTIONS,

        DELETE,
        REMOVE,

        PERMISSION_CODE_WRITE_EXTERNAL_STORAGE,
        OVERWRITE_FILE,
        OVERWRITE_CONTENT,

        RELOCATE,

        SET_AS_DEFAULT,
    }

    private enum Type
    {
        NONE,
        ATTRACTION_CATEGORY,
        MANUFACTURER,
        LOCATION,
        YEAR,
    }
}

