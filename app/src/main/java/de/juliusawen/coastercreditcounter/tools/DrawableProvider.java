package de.juliusawen.coastercreditcounter.tools;

import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.DrawableCompat;

import de.juliusawen.coastercreditcounter.application.App;

public abstract class DrawableProvider
{
    public static Drawable getColoredDrawable(int drawableId, int colorId)
    {
        Drawable drawable = DrawableCompat.wrap(App.getContext().getDrawable(drawableId)).mutate();
        DrawableCompat.setTint(drawable, App.getContext().getColor(colorId));

        return drawable;
    }

    public static Drawable getColoredDrawableMutation(int drawableId, int colorId)
    {
        Drawable drawable = DrawableCompat.wrap(App.getContext().getDrawable(drawableId)).mutate();
        DrawableCompat.setTint(drawable, App.getContext().getColor(colorId));

        return drawable;
    }
}
