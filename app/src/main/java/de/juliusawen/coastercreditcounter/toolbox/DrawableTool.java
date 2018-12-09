package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import de.juliusawen.coastercreditcounter.R;

public abstract class DrawableTool
{
    public static Drawable setTintToWhite(Drawable drawable, Context context)
    {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.white));

        return drawable;
    }

    public static Drawable setTintToColor(Drawable drawable, int colorId, Context context)
    {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, colorId));

        return drawable;
    }
}
