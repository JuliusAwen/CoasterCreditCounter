package de.juliusawen.coastercreditcounter.Toolbox;

public abstract class Constants
{
    public static String LOG_TAG = "JA.CODING";

    public static final String KEY_ELEMENTS = "de.juliusawen.elements";
    public static final String KEY_CURRENT_ELEMENT = "de.juliusawen.current_element";
    public static final String KEY_SELECTED_ELEMENT = "de.juliusawen.selected_element";
    public static final String KEY_HELP_VISIBLE = "de.juliusawen.help_visible";

    public static final String EXTRA_ELEMENT_UUID = "de.juliusawen.uuid";
    public static final String EXTRA_SELECTION = "de.juliusawen.selection";

    public static final String FRAGMENT_ARG_1 = "de.juliusawen.fragment_arg_1";
    public static final String FRAGMENT_ARG_2 = "de.juliusawen.fragment_arg_2";

    public static final String FRAGMENT_TAG_HELP_OVERLAY = "de.juliusawen.help_overlay_fragment";
    public static final String FRAGMENT_TAG_CONFIRM_DIALOG = "de.juliusawen.confirm_dialog_fragment";


    private static final int MODIFIER_BUTTON = 1000;
    private static final int MODIFIER_SELECTION = 2000;

    public static final int CONTENT_TYPE_LOCATION = 10000;
    public static final int CONTENT_TYPE_PARK = 20000;

    public static final int BUTTON_BACK = MODIFIER_BUTTON + ButtonFunctions.BACK.ordinal();
    public static final int BUTTON_MOVE_SELECTION_UP = MODIFIER_BUTTON + ButtonFunctions.MOVE_SELECTION_UP.ordinal();
    public static final int BUTTON_MOVE_SELECTION_DOWN = MODIFIER_BUTTON + ButtonFunctions.MOVE_SELECTION_DOWN.ordinal();
    public static final int BUTTON_CLOSE = MODIFIER_BUTTON + ButtonFunctions.CLOSE.ordinal();
    public static final int BUTTON_CANCEL = MODIFIER_BUTTON + ButtonFunctions.CANCEL.ordinal();
    public static final int BUTTON_OK = MODIFIER_BUTTON + ButtonFunctions.OK.ordinal();

    public static final int SELECTION_ADD = MODIFIER_SELECTION + Selections.ADD.ordinal();
    public static final int SELECTION_INSERT = MODIFIER_SELECTION + Selections.INSERT.ordinal();
    public static final int SELECTION_DELETE = MODIFIER_SELECTION + Selections.DELETE.ordinal();
    public static final int SELECTION_REMOVE = MODIFIER_SELECTION + Selections.REMOVE.ordinal();
    public static final int SELECTION_EDIT = MODIFIER_SELECTION + Selections.EDIT.ordinal();
    public static final int SELECTION_SORT_MANUALLY = MODIFIER_SELECTION + Selections.SORT_MANUALLY.ordinal();
    public static final int SELECTION_SORT_A_TO_Z = MODIFIER_SELECTION + Selections.SORT_A_TO_Z.ordinal();
    public static final int SELECTION_HELP = MODIFIER_SELECTION + Selections.HELP.ordinal();

    public static final int REQUEST_ADD_OR_INSERT_LOCATION = Requests.ADD_OR_INSERT_LOCATION.ordinal();
    public static final int REQUEST_SORT_ELEMENTS = Requests.SORT_ELEMENTS.ordinal();
}

enum ButtonFunctions
{
    MOVE_SELECTION_UP,
    MOVE_SELECTION_DOWN,
    BACK,
    CLOSE,
    CANCEL,
    OK,
}

enum Selections
{
    ADD,
    INSERT,
    DELETE,
    REMOVE,
    EDIT,
    RENAME,
    SORT_MANUALLY,
    SORT_A_TO_Z,
    HELP,
}

enum Requests
{
    ADD_OR_INSERT_LOCATION,
    SORT_ELEMENTS,
}
