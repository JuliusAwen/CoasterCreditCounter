package de.juliusawen.coastercreditcounter.toolbox;

import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.DrawableCompat;

import java.util.Objects;

import de.juliusawen.coastercreditcounter.backend.application.App;

public abstract class DrawableTool
{
    public static Drawable getColoredDrawable(int drawableId, int colorId)
    {
        Drawable drawable = DrawableCompat.wrap(Objects.requireNonNull(App.getContext().getDrawable(drawableId)));
        DrawableCompat.setTint(drawable, App.getContext().getColor(colorId));

        return drawable;
    }
}
