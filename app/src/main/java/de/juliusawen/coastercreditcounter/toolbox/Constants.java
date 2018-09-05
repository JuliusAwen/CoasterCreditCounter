package de.juliusawen.coastercreditcounter.toolbox;

public abstract class Constants
{
    public static final String LOG_TAG = "JA.C";
    public static final String LOG_DIVIDER = "################### ";

    public static final String KEY_ELEMENT = "de.juliusawen.coding.key_element";
    public static final String KEY_ELEMENTS = "de.juliusawen.coding.key_elements";
    public static final String KEY_SELECTED_ELEMENT = "de.juliusawen.coding.key_selected_element";
    public static final String KEY_SELECTED_ELEMENTS = "de.juliusawen.coding.key_selected_elements";
    public static final String KEY_HELP_VISIBILITY = "de.juliusawen.coding.key_help_visibility";
    public static final String KEY_TITLE = "de.juliusawen.coding.key_title";
    public static final String KEY_SUBTITLE = "de.juliusawen.coding.key_subtitle";
    public static final String KEY_HELP_TEXT = "de.juliusawen.coding.key_help_text";
    public static final String KEY_MODE = "de.juliusawen.coding.key_mode";

    public static final String EXTRA_MODE = "de.juliusawen.coding.extra_mode";
    public static final String EXTRA_ELEMENT_UUID = "de.juliusawen.coding.extra_element_uuid";
    public static final String EXTRA_ELEMENTS_UUIDS = "de.juliusawen.coding.extra_elements_uuids";
    public static final String EXTRA_RADIO_BUTTON_STATE = "de.juliusawen.coding.extra_radio_button_state";

    public static final String FRAGMENT_ARG_1 = "de.juliusawen.coding.fragment_arg_1";
    public static final String FRAGMENT_ARG_2 = "de.juliusawen.coding.fragment_arg_2";

    public static final String FRAGMENT_TAG_CONTENT_HANDLER = "de.juliusawen.coding.tag_content_handler";
    public static final String FRAGMENT_TAG_HELP_OVERLAY = "de.juliusawen.coding.tag_help_overlay_fragment";
    public static final String FRAGMENT_TAG_CONFIRM_DIALOG = "de.juliusawen.coding.tag_confirm_dialog_fragment";

    public static final int VIEW_TYPE_CHILD = 10000;

    public static final int REQUEST_ADD_ELEMENT = Request.ADD_ELEMENT.ordinal();
    public static final int REQUEST_SORT_ELEMENTS = Request.SORT_ELEMENTS.ordinal();
    public static final int REQUEST_PICK_ELEMENTS = Request.PICK_ELEMENTS.ordinal();
}

enum Request
{
    ADD_ELEMENT,
    SORT_ELEMENTS,
    PICK_ELEMENTS,
}

enum ContentType
{
    LOCATION,
    PARK,
}
