package de.juliusawen.coastercreditcounter.tools;

import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.DrawableCompat;

import java.util.Objects;

import de.juliusawen.coastercreditcounter.application.App;

public abstract class DrawableProvider
{
    public static Drawable getColoredDrawable(int drawableId, int colorId)
    {
        Drawable drawable = DrawableCompat.wrap(Objects.requireNonNull(App.getContext().getDrawable(drawableId)));
        DrawableCompat.setTint(drawable, App.getContext().getColor(colorId));

        return drawable;
    }
}
