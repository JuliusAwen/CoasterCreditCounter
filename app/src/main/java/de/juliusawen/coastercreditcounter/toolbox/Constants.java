package de.juliusawen.coastercreditcounter.toolbox;

public abstract class Constants
{
    public static String LOG_TAG = "JA.CODING";

    public static final String KEY_ELEMENT = "de.juliusawen.key_element";
    public static final String KEY_ELEMENTS = "de.juliusawen.key_elements";
    public static final String KEY_SELECTED_ELEMENT = "de.juliusawen.Key_selected_element";
    public static final String KEY_SELECTED_ELEMENTS = "de.juliusawen.Key_selected_elements";
    public static final String KEY_HELP_VISIBLE = "de.juliusawen.Key_help_visible";

    public static final String EXTRA_MODE = "de.juliusawen.extra_mode";
    public static final String EXTRA_ELEMENT_UUID = "de.juliusawen.extra_uuid";
    public static final String EXTRA_ELEMENTS_UUIDS = "de.juliusawen.extra_uuids";
    public static final String EXTRA_RADIO_BUTTON_STATE = "de.juliusawen.extra_radio_button_state";

    public static final String FRAGMENT_ARG_1 = "de.juliusawen.fragment_arg_1";
    public static final String FRAGMENT_ARG_2 = "de.juliusawen.fragment_arg_2";

    public static final String FRAGMENT_TAG_HELP_OVERLAY = "de.juliusawen.tag_help_overlay_fragment";
    public static final String FRAGMENT_TAG_CONFIRM_DIALOG = "de.juliusawen.tag_confirm_dialog_fragment";

    public static final int VIEW_TYPE_CHILD = 10000;

    public static final int REQUEST_ADD_LOCATION = Request.ADD_LOCATION.ordinal();
    public static final int REQUEST_SORT_ELEMENTS = Request.SORT_ELEMENTS.ordinal();
    public static final int REQUEST_PICK_ELEMENTS = Request.PICK_ELEMENTS.ordinal();
}

enum Request
{
    ADD_LOCATION,
    SORT_ELEMENTS,
    PICK_ELEMENTS,
}

