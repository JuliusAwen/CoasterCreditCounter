package de.juliusawen.coastercreditcounter.globals;

public abstract class Constants
{
    public static final String LOG_TAG = "JA.C";
    public static final String LOG_DIVIDER = "##################### ";
    public static final String LOG_DIVIDER_ON_CREATE = "####### ";
    public static final String LOG_DIVIDER_FINISH = "-------";

    public static final String SIMPLE_DATE_FORMAT_FULL_PATTERN = "dd. MMMM yyyy";
    public static final String SIMPLE_DATE_FORMAT_YEAR_PATTERN = "yyyy";


    public static final String DATABASE_WRAPPER_DATABASE_MOCK = "de.juliusawen.coding.database_wrapper_database_mock";
    public static final String DATABASE_WRAPPER_JSON_HANDLER = "de.juliusawen.coding.database_wrapper_json_handler";

    public static final String PERSISTENCY_SERVICE_NAME = "de.juliusawen.coding.persistency_service";


    public static final String JSON_STRING_LOCATIONS = "locations";
    public static final String JSON_STRING_PARKS = "parks";
    public static final String JSON_STRING_VISITS = "visits";
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
    public static final String JSON_STRING_RIDE_COUNT_BY_ATTRACTIONS = "ride count by attractions";
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


    public static  final int SELECTION_ADD = 1;

    public static  final int SELECTION_CREATE_LOCATION = 2;
    public static  final int SELECTION_CREATE_PARK = 3;

    public static  final int SELECTION_EDIT_LOCATION = 4;
    public static  final int SELECTION_EDIT_PARK = 5;
    public static  final int SELECTION_EDIT_ATTRACTION_CATEGORY = 6;

    public static  final int SELECTION_DELETE_ELEMENT = 7;
    public static  final int SELECTION_REMOVE_ELEMENT = 8;
    public static  final int SELECTION_RELOCATE_ELEMENT = 9;

    public static  final int SELECTION_SORT = 10;
    public static  final int SELECTION_SORT_LOCATIONS = 11;
    public static  final int SELECTION_SORT_PARKS = 12;
    public static  final int SELECTION_SORT_ATTRACTIONS = 13;
    public static  final int SELECTION_SORT_ATTRACTION_CATEGORIES = 14;

    public static  final int SELECTION_SORT_ASCENDING = 15;
    public static  final int SELECTION_SORT_DESCENDING = 16;

    public static  final int SELECTION_EXPAND_ALL = 17;
    public static  final int SELECTION_COLLAPSE_ALL = 18;

    public static  final int SELECTION_APPLY_TO_ATTRACTIONS = 19;
    public static  final int SELECTION_SET_AS_DEFAULT = 20;

    public static  final int SELECTION_HELP = 21;



    public static final int REQUEST_CODE_CREATE_LOCATION = 1;
    public static final int REQUEST_CODE_CREATE_PARK = 2;
    public static final int REQUEST_CODE_CREATE_VISIT = 3;
    public static final int REQUEST_CODE_CREATE_ATTRACTION_CATEGORY = 4;

    public static final int REQUEST_CODE_SHOW_LOCATION = 5;
    public static final int REQUEST_CODE_SHOW_PARK = 6;
    public static final int REQUEST_CODE_SHOW_VISIT = 7;

    public static final int REQUEST_CODE_MANAGE_ATTRACTION_CATEGORIES = 8;

    public static final int REQUEST_CODE_APPLY_CATEGORY_TO_ATTRACTIONS = 9;

    public static final int REQUEST_CODE_EDIT_LOCATION = 10;
    public static final int REQUEST_CODE_EDIT_PARK = 11;
    public static final int REQUEST_CODE_EDIT_ATTRACTION_CATEGORY = 12;

    public static final int REQUEST_CODE_SORT_LOCATIONS = 13;
    public static final int REQUEST_CODE_SORT_PARKS = 14;
    public static final int REQUEST_CODE_SORT_ATTRACTIONS = 15;
    public static final int REQUEST_CODE_SORT_ATTRACTION_CATEGORIES = 16;

    public static final int REQUEST_CODE_PICK_LOCATIONS = 17;
    public static final int REQUEST_CODE_PICK_PARKS = 18;
    public static final int REQUEST_CODE_PICK_ATTRACTIONS = 19;

    public static final int REQUEST_CODE_DELETE = 20;
    public static final int REQUEST_CODE_REMOVE = 21;

    public static final int REQUEST_CODE_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = 22;
    public static final int REQUEST_OVERWRITE_FILE = 23;
    public static final int REQUEST_CODE_OVERWRITE_CONTENT = 24;

    public static final int REQUEST_CODE_RELOCATE = 25;

    public static final int REQUEST_CODE_SET_AS_DEFAULT = 25;
}