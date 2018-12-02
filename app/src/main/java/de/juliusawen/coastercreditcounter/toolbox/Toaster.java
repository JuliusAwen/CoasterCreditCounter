package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class Toaster
{
    public static void makeToast(Context context, String text)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();

        Log.i(Constants.LOG_TAG, String.format("Toaster.makeToast:: showed toast ['%s']", text));
    }

    public static void makeLongToast(Context context, String text)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();

        Log.i(Constants.LOG_TAG, String.format("Toaster.makeLongToast:: showed toast ['%s']", text));
    }
}
