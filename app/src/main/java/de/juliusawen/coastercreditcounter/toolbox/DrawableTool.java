package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import de.juliusawen.coastercreditcounter.R;

public abstract class DrawableTool
{
    public static Drawable setTintToWhite(Context context, Drawable drawable)
    {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.white));

        return drawable;
    }

    public static Drawable setTintToColor(Context context, Drawable drawable, int colorId)
    {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorId));

        return drawable;
    }
}
