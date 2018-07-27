package de.juliusawen.coastercreditcounter.Toolbox;

import android.content.Context;
import android.widget.Toast;

public abstract class Multitool
{
    public static void makeToast(Context context, String text)
    {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
