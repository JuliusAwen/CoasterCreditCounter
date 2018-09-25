package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
