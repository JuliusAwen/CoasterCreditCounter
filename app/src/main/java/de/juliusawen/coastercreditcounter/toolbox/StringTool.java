package de.juliusawen.coastercreditcounter.toolbox;

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

    public static String parseActivityName(String componentShortClassName)
    {
        int lastIndexOfDot = componentShortClassName.lastIndexOf(".");

        if(lastIndexOfDot != -1)
        {
            return componentShortClassName.substring(lastIndexOfDot + 1);
        }
        else
        {
            return componentShortClassName;
        }
    }
}
