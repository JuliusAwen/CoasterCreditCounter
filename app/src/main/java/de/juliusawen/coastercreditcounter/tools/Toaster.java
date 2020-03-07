package de.juliusawen.coastercreditcounter.tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import de.juliusawen.coastercreditcounter.application.Constants;

public abstract class Toaster
{
    public static Toast makeShortToast(Context context, String text)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();

        Log.i(Constants.LOG_TAG, String.format("Toaster.makeToast:: showing toast ['%s']", text));
        return toast;
    }

    public static Toast makeLongToast(Context context, String text)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();

        Log.i(Constants.LOG_TAG, String.format("Toaster.makeLongToast:: showing toast ['%s']", text));
        return toast;
    }

    public static void notYetImplemented(Context context)
    {
        Toaster.makeShortToast(context, "not yet implemented");
    }
}
