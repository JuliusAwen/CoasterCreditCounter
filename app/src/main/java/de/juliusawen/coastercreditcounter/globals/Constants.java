package de.juliusawen.coastercreditcounter.globals;

public abstract class Constants
{
    public static final String LOG_TAG = "JA.C";
    public static final String LOG_DIVIDER = "##### ";

    public static final String KEY_HELP_OVERLAY_IS_VISIBLE = "de.juliusawen.coding.key_help_overlay_is_visible";
    public static final String KEY_HELP_TITLE = "de.juliusawen.coding.key_help_title";
    public static final String KEY_HELP_MESSAGE = "de.juliusawen.coding.key_help_message";

    public static final String EXTRA_ELEMENT_UUID = "de.juliusawen.coding.extra_element_uuid";
    public static final String EXTRA_ELEMENTS_UUIDS = "de.juliusawen.coding.extra_elements_uuids";
    public static final String EXTRA_TOOLBAR_TITLE = "de.juliusawen.coding.extra_toolbar_title";
    public static final String EXTRA_TOOLBAR_SUBTITLE = "de.juliusawen.coding.extra_toolbar_subtitle";

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
    public static final String FRAGMENT_TAG_SHOW_COUNTABLE_ATTRACTIONS = "de.juliusawen.coding.fragment_tag_show_visit_attractions";
    public static final String FRAGMENT_TAG_ALERT_DIALOG = "de.juliusawen.coding.fragment_tag_alert_dialog";

    public static final String SIMPLE_DATE_FORMAT_FULL_PATTERN = "dd. MMMM YYYY";
    public static final String SIMPLE_DATE_FORMAT_YEAR_PATTERN = "YYYY";

    public static final int REQUEST_PICK_LOCATIONS = Request.PICK_LOCATIONS.ordinal();
    public static final int REQUEST_PICK_PARKS = Request.PICK_PARKS.ordinal();
    public static final int REQUEST_PICK_ATTRACTIONS = Request.PICK_ATTRACTIONS.ordinal();
    public static final int REQUEST_ADD_LOCATION = Request.ADD_LOCATION.ordinal();
    public static final int REQUEST_ADD_VISIT = Request.ADD_VISIT.ordinal();
    public static final int REQUEST_SORT_LOCATIONS = Request.SORT_LOCATIONS.ordinal();
    public static final int REQUEST_SORT_PARKS = Request.SORT_PARKS.ordinal();
    public static final int REQUEST_SORT_ATTRACTIONS = Request.SORT_ATTRACTIONS.ordinal();
    public static final int REQUEST_SORT_ATTRACTION_CATEGORIES = Request.SORT_ATTRACTION_CATEGORIES.ordinal();
    public static final int REQUEST_EDIT_ELEMENT = Request.EDIT_ELEMENT.ordinal();
}

enum Request
{
    PICK_LOCATIONS,
    PICK_PARKS,
    PICK_ATTRACTIONS,

    ADD_LOCATION,
    ADD_VISIT,

    SORT_LOCATIONS,
    SORT_PARKS,
    SORT_ATTRACTIONS,
    SORT_ATTRACTION_CATEGORIES,

    EDIT_ELEMENT,
}
