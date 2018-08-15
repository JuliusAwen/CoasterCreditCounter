package de.juliusawen.coastercreditcounter.Toolbox;

public abstract class Constants
{
    public static final String LOG_TAG = "|------JA.CODING------|";

    public static final String KEY_ELEMENTS = "de.juliusawen.elements";
    public static final String KEY_CURRENT_ELEMENT = "de.juliusawen.current_element";
    public static final String KEY_SELECTED_ELEMENT = "de.juliusawen.selected_element";
    public static final String KEY_HELP_ACTIVE = "de.juliusawen.help_active";
    public static final String EXTRA_UUID = "de.juliusawen.uuid";

    public static final String FRAGMENT_ARG_1 = "de.juliusawen.fragment_arg_1";
    public static final String FRAGMENT_ARG_2 = "de.juliusawen.fragment_arg_2";

    public static final String FRAGMENT_TAG_HELP = "de.juliusawen.help_fragment";

    public static final int BUTTON_ACCEPT = ButtonFunctions.ACCEPT.ordinal();
    public static final int BUTTON_CANCEL = ButtonFunctions.CANCEL.ordinal();
    public static final int BUTTON_BACK = ButtonFunctions.BACK.ordinal();
    public static final int BUTTON_UP = ButtonFunctions.UP.ordinal();
    public static final int BUTTON_DOWN = ButtonFunctions.DOWN.ordinal();
    public static final int BUTTON_CLOSE = ButtonFunctions.CLOSE.ordinal();

}

enum ButtonFunctions
{
    ACCEPT,
    CANCEL,
    BACK,
    UP,
    DOWN,
    CLOSE
}
