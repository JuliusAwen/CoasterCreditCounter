package de.juliusawen.coastercreditcounter.toolbox;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;

public abstract class DrawableTool
{
    public static Drawable setTintToWhite(Drawable drawable)
    {
        DrawableTool.setTintToColor(drawable, ContextCompat.getColor(App.applicationContext, R.color.white));
        return drawable;
    }

    public static Drawable setTintToColor(Drawable drawable, int colorId)
    {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(App.applicationContext, colorId));
        return drawable;
    }
}
