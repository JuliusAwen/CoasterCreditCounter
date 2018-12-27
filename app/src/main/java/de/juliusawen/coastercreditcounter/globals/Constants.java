package de.juliusawen.coastercreditcounter.globals;

public abstract class Constants
{
    public static final String LOG_TAG = "JA.C";
    public static final String LOG_DIVIDER_ON_CREATE = "####### ";
    public static final String LOG_DIVIDER_FINISH = "-------";


    public static final String DATABASE_WRAPPER_DATABASE_MOCK = "de.juliusawen.coding.database_wrapper_database_mock";
    public static final String DATABASE_WRAPPER_JSON_HANDLER = "de.juliusawen.coding.database_wrapper_json_handler";


    public static final String JSON_STRING_LOCATIONS = "locations";
    public static final String JSON_STRING_PARKS = "parks";
    public static final String JSON_STRING_VISITS = "visits";
    public static final String JSON_STRING_ATTRACTIONS = "attractions";
    public static final String JSON_STRING_ATTRACTION_BLUEPRINTS = "attraction blueprints";
    public static final String JSON_STRING_COASTER_BLUEPRINTS = "coaster blueprints";
    public static final String JSON_STRING_CUSTOM_ATTRACTIONS = "custom attractions";
    public static final String JSON_STRING_CUSTOM_COASTERS = "custom coasters";
    public static final String JSON_STRING_STOCK_ATTRACTIONS = "stock attractions";

    public static final String JSON_STRING_ELEMENT = "element";
    public static final String JSON_STRING_NAME = "name";
    public static final String JSON_STRING_UUID = "uuid";
    public static final String JSON_STRING_PARENT = "parent";
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


    public static final String JSON_STRING_DEFAULT_SORT_ORDER = "default sort order";
    public static final String JSON_STRING_EXPAND_LATEST_YEAR_HEADER = "expand latest year header";
    public static final String JSON_STRING_FIRST_DAY_OF_THE_WEEK = "first day of the week";
    public static final String JSON_STRING_DEFAULT_INCREMENT = "default increment";



    public static final String EXTRA_ELEMENT_UUID = "de.juliusawen.coding.extra_element_uuid";
    public static final String EXTRA_ELEMENTS_UUIDS = "de.juliusawen.coding.extra_elements_uuids";
    public static final String EXTRA_TOOLBAR_TITLE = "de.juliusawen.coding.extra_toolbar_title";
    public static final String EXTRA_TOOLBAR_SUBTITLE = "de.juliusawen.coding.extra_toolbar_subtitle";
    public static final String EXTRA_HELP_TITLE = "de.juliusawen.coding.extra_help_title";
    public static final String EXTRA_HELP_TEXT = "de.juliusawen.coding.extra_help_text";
    public static final String EXTRA_RESULT_STRING = "de.juliusawen.coding.extra_result_string";


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


    public static final String SIMPLE_DATE_FORMAT_FULL_PATTERN = "dd. MMMM YYYY";
    public static final String SIMPLE_DATE_FORMAT_YEAR_PATTERN = "YYYY";



    public static final int ALERT_DIALOG_REQUEST_CODE_DELETE = 0;
    public static final int ALERT_DIALOG_REQUEST_CODE_REMOVE = 1;
    public static final int ALERT_DIALOG_REQUEST_CODE_OVERWRITE_FILE = 3;
    public static final int ALERT_DIALOG_REQUEST_CODE_OVERWRITE_CONTENT = 4;


    public static final int REQUEST_CREATE_LOCATION = Request.CREATE_LOCATION.ordinal();
    public static final int REQUEST_CREATE_VISIT = Request.CREATE_VISIT.ordinal();
    public static final int REQUEST_CREATE_ATTRACTION_CATEGORY = Request.CREATE_ATTRACTION_CATEGORY.ordinal();

    public static final int REQUEST_SHOW_LOCATION = Request.SHOW_LOCATION.ordinal();
    public static final int REQUEST_SHOW_PARK = Request.SHOW_PARK.ordinal();
    public static final int REQUEST_SHOW_VISIT = Request.SHOW_VISIT.ordinal();

    public static final int REQUEST_MANAGE_ATTRACTION_CATEGORIES = Request.MANAGE_ATTRACTION_CATEGORIES.ordinal();

    public static final int REQUEST_APPLY_CATEGORY_TO_ATTRACTIONS = Request.APPLY_CATEGORY_TO_ATTRACTIONS.ordinal();

    public static final int REQUEST_EDIT_LOCATION = Request.EDIT_LOCATION.ordinal();
    public static final int REQUEST_EDIT_ATTRACTION_CATEGORY = Request.EDIT_ATTRACTION_CATEGORY.ordinal();

    public static final int REQUEST_SORT_LOCATIONS = Request.SORT_LOCATIONS.ordinal();
    public static final int REQUEST_SORT_PARKS = Request.SORT_PARKS.ordinal();
    public static final int REQUEST_SORT_ATTRACTIONS = Request.SORT_ATTRACTIONS.ordinal();
    public static final int REQUEST_SORT_ATTRACTION_CATEGORIES = Request.SORT_ATTRACTION_CATEGORIES.ordinal();

    public static final int REQUEST_PICK_LOCATIONS = Request.PICK_LOCATIONS.ordinal();
    public static final int REQUEST_PICK_PARKS = Request.PICK_PARKS.ordinal();
    public static final int REQUEST_PICK_ATTRACTIONS = Request.PICK_ATTRACTIONS.ordinal();

    public static final int REQUEST_PERMISSION_CODE_WRITE_EXTERNAL_STORAGE = Request.PERMISSION_WRITE_EXTERNAL_STORAGE.ordinal();
}
enum Request
{
    CREATE_LOCATION,
    CREATE_VISIT, CREATE_ATTRACTION_CATEGORY,

    SHOW_LOCATION,
    SHOW_PARK,
    SHOW_VISIT,

    MANAGE_ATTRACTION_CATEGORIES,

    APPLY_CATEGORY_TO_ATTRACTIONS,

    EDIT_LOCATION,
    EDIT_ATTRACTION_CATEGORY,

    SORT_LOCATIONS,
    SORT_PARKS,
    SORT_ATTRACTIONS,
    SORT_ATTRACTION_CATEGORIES,

    PICK_LOCATIONS,
    PICK_PARKS,
    PICK_ATTRACTIONS,

    PERMISSION_WRITE_EXTERNAL_STORAGE,
}
