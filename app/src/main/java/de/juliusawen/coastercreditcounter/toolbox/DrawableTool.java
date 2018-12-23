package de.juliusawen.coastercreditcounter.toolbox;

import android.graphics.drawable.Drawable;

import java.util.Objects;

import androidx.core.graphics.drawable.DrawableCompat;
import de.juliusawen.coastercreditcounter.globals.App;

public abstract class DrawableTool
{
    public static Drawable getColoredDrawable(int drawableId, int colorId)
    {
        Drawable drawable = DrawableCompat.wrap(Objects.requireNonNull(App.getContext().getDrawable(drawableId)));
        DrawableCompat.setTint(drawable, App.getContext().getColor(colorId));

        return drawable;
    }
}
