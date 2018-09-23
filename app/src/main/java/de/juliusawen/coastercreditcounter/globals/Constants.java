package de.juliusawen.coastercreditcounter.globals;

public abstract class Constants
{
    public static final String LOG_TAG = "JA.C";
    public static final String LOG_DIVIDER = "##### ";

    public static final String KEY_ELEMENT = "de.juliusawen.coding.key_element";
    public static final String KEY_ELEMENTS = "de.juliusawen.coding.key_elements";
    public static final String KEY_CURRENT_ELEMENT = "de.juliusawen.coding.key_current_element";
    public static final String KEY_RECENT_ELEMENTS = "de.juliusawen.coding.key_recent_elements";

    public static final String KEY_RECYCLER_SCROLL_POSITION = "de.juliusawen.coding.extra_recycler_scroll_position";
    public static final String KEY_RECYCLER_EXPANDED_ELEMENTS = "de.juliusawen.coding.key_recycler_expanded_elements";


    public static final String KEY_SELECTED_ELEMENT = "de.juliusawen.coding.key_selected_element";
    public static final String KEY_SELECTED_ELEMENTS = "de.juliusawen.coding.key_selected_elements";
    public static final String KEY_HELP_OVERLAY_IS_VISIBLE = "de.juliusawen.coding.key_help_overlay_is_visible";
    public static final String KEY_TOOLBAR_TITLE = "de.juliusawen.coding.key_title";
    public static final String KEY_TOOLBAR_SUBTITLE = "de.juliusawen.coding.key_subtitle";
    public static final String KEY_HELP_TITLE = "de.juliusawen.coding.key_help_title";
    public static final String KEY_HELP_MESSAGE = "de.juliusawen.coding.key_help_message";
    public static final String KEY_RADIO_BUTTON_STATE = "de.juliusawen.coding.extra_radio_button_state";


    public static final String EXTRA_ELEMENT_UUID = "de.juliusawen.coding.extra_element_uuid";
    public static final String EXTRA_ELEMENTS_UUIDS = "de.juliusawen.coding.extra_elements_uuids";
    public static final String EXTRA_TOOLBAR_TITLE = "de.juliusawen.coding.extra_toolbar_title";
    public static final String EXTRA_TOOLBAR_SUBTITLE = "de.juliusawen.coding.extra_toolbar_subtitle";

    public static final String FRAGMENT_ARG_HELP_TITLE = "de.juliusawen.coding.fragment_arg_help_title";
    public static final String FRAGMENT_ARG_HELP_MESSAGE = "de.juliusawen.coding.fragment_arg_help_message";
    public static final String FRAGMENT_ARG_PARK_UUID = "de.juliusawen.coding.fragment_arg_park_uuid";

    public static final String FRAGMENT_TAG_HELP_OVERLAY = "de.juliusawen.coding.fragment_tag_help_overlay";
    public static final String FRAGMENT_TAG_CONFIRM_DIALOG = "de.juliusawen.coding.fragment_tag_confirm_dialog";
    public static final String FRAGMENT_TAG_SHOW_PARK_ATTRACTIONS = "de.juliusawen.coding.fragment_tag_show_park_attractions";
    public static final String FRAGMENT_TAG_SHOW_VISIT_ATTRACTIONS = "de.juliusawen.coding.fragment_tag_show_visit_attractions";

    public static final String SIMPLE_DATE_FORMAT_FULL_PATTERN = "dd. MMMM YYYY";
    public static final String SIMPLE_DATE_FORMAT_YEAR_PATTERN = "YYYY";

    public static final int VIEW_TYPE_CHILD = 10000;

    public static final int REQUEST_ADD_LOCATION = Request.ADD_LOCATION.ordinal();
    public static final int REQUEST_SORT_LOCATIONS = Request.SORT_LOCATIONS.ordinal();
    public static final int REQUEST_SORT_PARKS = Request.SORT_PARKS.ordinal();
    public static final int REQUEST_SORT_ATTRACTIONS = Request.SORT_ATTRACTIONS.ordinal();
    public static final int REQUEST_SORT_ATTRACTION_CATEGORIES = Request.SORT_ATTRACTION_CATEGORIES.ordinal();
    public static final int REQUEST_PICK_LOCATIONS = Request.PICK_LOCATIONS.ordinal();
    public static final int REQUEST_PICK_PARKS = Request.PICK_PARKS.ordinal();
    public static final int REQUEST_PICK_ATTRACTIONS = Request.PICK_ATTRACTIONS.ordinal();
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
    SORT_ATTRACTION_CATEGORIES
}
