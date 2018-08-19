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

    public static final int BUTTON_BACK = ButtonFunctions.BACK.ordinal();
    public static final int BUTTON_UP = ButtonFunctions.UP.ordinal();
    public static final int BUTTON_DOWN = ButtonFunctions.DOWN.ordinal();
    public static final int BUTTON_CLOSE = ButtonFunctions.CLOSE.ordinal();

    public static final int SELECTION_RENAME_ROOT = Selections.RENAME_ROOT.ordinal();
    public static final int SELECTION_SORT_ELEMENTS = Selections.SORT_ELEMENTS.ordinal();
    public static final int SELECTION_SORT_A_TO_Z = Selections.SORT_A_TO_Z.ordinal();
    public static final int SELECTION_HELP = Selections.HELP.ordinal();
    public static final int SELECTION_ADD_LOCATION = Selections.ADD_LOCATION.ordinal();
    public static final int SELECTION_INSERT_LOCATION_LEVEL = Selections.INSERT_LOCATION_LEVEL.ordinal();
    public static final int SELECTION_REMOVE_LOCATION_LEVEL = Selections.REMOVE_LOCATION_LEVEL.ordinal();

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
    RENAME_ROOT,
    SORT_ELEMENTS,
    SORT_A_TO_Z,
    HELP,
    ADD_LOCATION,
    INSERT_LOCATION_LEVEL,
    REMOVE_LOCATION_LEVEL
}
