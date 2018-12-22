package de.juliusawen.coastercreditcounter.toolbox;

import de.juliusawen.coastercreditcounter.globals.App;

public abstract class ViewTool
{
    public static int convertDpToPx(int dp)
    {
        return (int) (dp * App.getContext().getResources().getDisplayMetrics().density);
    }

    public static int convertPxToDp(int px)
    {
        return (int) (px / App.getContext().getResources().getDisplayMetrics().density);
    }
}
