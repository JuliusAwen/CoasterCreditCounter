package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Context;

public abstract class ViewTool
{
    public static int convertDpToPx(Context context, int dp)
    {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static int convertPxToDp(Context context, int px)
    {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }
}
