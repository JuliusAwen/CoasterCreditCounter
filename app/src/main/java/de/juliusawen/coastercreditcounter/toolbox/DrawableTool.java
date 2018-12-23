package de.juliusawen.coastercreditcounter.toolbox;

import android.graphics.drawable.Drawable;

import java.util.Objects;

import androidx.core.graphics.drawable.DrawableCompat;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.App;

public abstract class DrawableTool
{
    public static Drawable getDrawableInWhite(int drawableId)
    {
        return DrawableTool.getDrawableInColor(drawableId, R.color.white);
    }

    public static Drawable getDrawableInColor(int drawableId, int colorId)
    {
        Drawable drawable = DrawableCompat.wrap(Objects.requireNonNull(App.getContext().getDrawable(drawableId)));
        DrawableCompat.setTint(drawable, App.getContext().getColor(colorId));

        return drawable;
    }
}
