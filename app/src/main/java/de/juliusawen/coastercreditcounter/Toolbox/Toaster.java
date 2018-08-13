package de.juliusawen.coastercreditcounter.Toolbox;

import android.content.Context;
import android.widget.Toast;

public abstract class Toaster
{
    public static void makeToast(Context context, String text)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void makeLongToast(Context context, String text)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}
