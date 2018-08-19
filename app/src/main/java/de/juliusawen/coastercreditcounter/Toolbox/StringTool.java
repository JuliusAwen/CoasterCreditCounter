package de.juliusawen.coastercreditcounter.Toolbox;

import android.text.SpannableString;
import android.text.style.StyleSpan;

public abstract class StringTool
{
    public static SpannableString getSpannableString(String string, int typeface)
    {
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new StyleSpan(typeface), 0, spannableString.length(), 0);

        return spannableString;
    }
}
