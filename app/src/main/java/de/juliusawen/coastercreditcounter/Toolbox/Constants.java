package de.juliusawen.coastercreditcounter.Toolbox;

public abstract class Constants
{
    public static final String LOG_TAG = "JA.CODING";

    public static final String KEY_ELEMENTS = "de.juliusawen.elements";
    public static final String KEY_CURRENT_ELEMENT = "de.juliusawen.current_element";
    public static final String KEY_SELECTED_ELEMENT = "de.juliusawen.selected_element";
    public static final String KEY_HELP_ACTIVE = "de.juliusawen.help_active";

    public static final String EXTRA_UUID = "de.juliusawen.uuid";
    public static final String EXTRA_SELECTION = "de.juliusawen.selection";

    public static final String FRAGMENT_ARG_1 = "de.juliusawen.fragment_arg_1";
    public static final String FRAGMENT_ARG_2 = "de.juliusawen.fragment_arg_2";

    public static final String FRAGMENT_TAG_HELP = "de.juliusawen.help_fragment";


    private static final int MODIFIER_BUTTON = 1000;
    private static final int MODIFIER_SELECTION = 2000;

    public static final int CONTENT_TYPE_LOCATION = 10000;
    public static final int CONTENT_TYPE_PARK = 20000;

    public static final int BUTTON_BACK = MODIFIER_BUTTON + ButtonFunctions.BACK.ordinal();
    public static final int BUTTON_UP = MODIFIER_BUTTON + ButtonFunctions.UP.ordinal();
    public static final int BUTTON_DOWN = MODIFIER_BUTTON + ButtonFunctions.DOWN.ordinal();
    public static final int BUTTON_CLOSE = MODIFIER_BUTTON + ButtonFunctions.CLOSE.ordinal();

    public static final int SELECTION_ADD = MODIFIER_SELECTION + Selections.ADD.ordinal();
    public static final int SELECTION_INSERT = MODIFIER_SELECTION + Selections.INSERT.ordinal();
    public static final int SELECTION_DELETE = MODIFIER_SELECTION + Selections.DELETE.ordinal();
    public static final int SELECTION_REMOVE = MODIFIER_SELECTION + Selections.REMOVE.ordinal();
    public static final int SELECTION_EDIT = MODIFIER_SELECTION + Selections.EDIT.ordinal();
    public static final int SELECTION_SORT_MANUALLY = MODIFIER_SELECTION + Selections.SORT_MANUALLY.ordinal();
    public static final int SELECTION_SORT_A_TO_Z = MODIFIER_SELECTION + Selections.SORT_A_TO_Z.ordinal();
    public static final int SELECTION_HELP = MODIFIER_SELECTION + Selections.HELP.ordinal();
}

enum ButtonFunctions
{
    BACK,
    UP,
    DOWN,
    CLOSE
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

enum ContentTypes
{
    ELEMENT,
    LOCATION,
    PARK,
    ATTRACTION,
    COASTER
}
