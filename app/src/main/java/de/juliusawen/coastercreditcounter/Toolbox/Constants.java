package de.juliusawen.coastercreditcounter.Toolbox;

public abstract class Constants
{
    public static final String LOG_TAG = "|------JA.CODING------|";

    public static final int BUTTON_PICK = ButtonFunctions.PICK.ordinal();
    public static final int BUTTON_DONE = ButtonFunctions.DONE.ordinal();
    public static final int BUTTON_BACK = ButtonFunctions.BACK.ordinal();
    public static final int BUTTON_UP = ButtonFunctions.UP.ordinal();
    public static final int BUTTON_DOWN = ButtonFunctions.DOWN.ordinal();
}

enum ButtonFunctions
{
    PICK,
    DONE,
    BACK,
    UP,
    DOWN
}
