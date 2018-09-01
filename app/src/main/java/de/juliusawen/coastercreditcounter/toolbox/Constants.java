package de.juliusawen.coastercreditcounter.toolbox;

public abstract class Constants
{
    public static String LOG_TAG = "JA.CODING";

    public static final String KEY_ELEMENT = "de.juliusawen.element";
    public static final String KEY_ELEMENTS = "de.juliusawen.elements";
    public static final String KEY_SELECTED_ELEMENT = "de.juliusawen.selected_element";
    public static final String KEY_SELECTED_ELEMENTS = "de.juliusawen.selected_elements";
    public static final String KEY_HELP_VISIBLE = "de.juliusawen.help_visible";

    public static final String EXTRA_ELEMENT_UUID = "de.juliusawen.uuid";
    public static final String EXTRA_ELEMENTS_UUIDS = "de.juliusawen.uuids";
    public static final String EXTRA_RADIO_BUTTON_STATE = "de.juliusawen.radio_button_state";

    public static final String FRAGMENT_ARG_1 = "de.juliusawen.fragment_arg_1";
    public static final String FRAGMENT_ARG_2 = "de.juliusawen.fragment_arg_2";

    public static final String FRAGMENT_TAG_HELP_OVERLAY = "de.juliusawen.help_overlay_fragment";
    public static final String FRAGMENT_TAG_CONFIRM_DIALOG = "de.juliusawen.confirm_dialog_fragment";

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

