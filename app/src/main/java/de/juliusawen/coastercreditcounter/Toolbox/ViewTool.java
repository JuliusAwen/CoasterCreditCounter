package de.juliusawen.coastercreditcounter.Toolbox;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class ViewTool
{
    public static int getScrollMarginForRecyclerView(RecyclerView recyclerView)
    {
        int firstVisibleViewPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastVisibleViewPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int visibleViewsCount = lastVisibleViewPosition - firstVisibleViewPosition;
        return Math.round(visibleViewsCount / 2);
    }

    public static int convertDpToPx(Context context, int dp)
    {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static int convertPxToDp(Context context, int px)
    {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }
}
