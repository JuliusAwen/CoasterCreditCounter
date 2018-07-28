package de.juliusawen.coastercreditcounter.Toolbox;

public abstract class Constants
{
    public static final String LOG_TAG = "|------JA.CODING------|";

    public static final String KEY_ELEMENTS = "de.juliusawen.elements";
    public static final String KEY_ELEMENT = "de.juliusawen.element";
    public static final String EXTRA_UUID = "de.juliusawen.uuid";

    public static final int BUTTON_ACCEPT = ButtonFunctions.ACCEPT.ordinal();
    public static final int BUTTON_CANCEL = ButtonFunctions.CANCEL.ordinal();
    public static final int BUTTON_BACK = ButtonFunctions.BACK.ordinal();
    public static final int BUTTON_UP = ButtonFunctions.UP.ordinal();
    public static final int BUTTON_DOWN = ButtonFunctions.DOWN.ordinal();
}

enum ButtonFunctions
{
    ACCEPT,
    CANCEL,
    BACK,
    UP,
    DOWN
}
