package de.juliusawen.coastercreditcounter.toolbox;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import de.juliusawen.coastercreditcounter.R;

public abstract class DrawableTool
{
    public static Drawable setTintToWhite(Context context, Drawable drawable)
    {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.white));

        return drawable;
    }
}
