package de.juliusawen.coastercreditcounter.tools;

import android.content.res.Resources;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public abstract class StringTool
{
    public static SpannableString getSpannableStringWithTypeface(String string, int typeface)
    {
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new StyleSpan(typeface), 0, spannableString.length(), 0);
        return spannableString;
    }

    public static SpannableString getSpannableStringWithColor(String string, int color)
    {
        SpannableString spannableString = new SpannableString(string);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, spannableString.length(), 0);
        return spannableString;
    }

    public static SpannableString buildSpannableStringWithTypeface(String originalString, String substring, int typeface)
    {
        SpannableString spannableString = new SpannableString(originalString);

        if(originalString.contains(substring))
        {
            int start = originalString.indexOf(substring);
            int end = start + substring.length();
            spannableString.setSpan(new StyleSpan(typeface), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }

    public static SpannableString buildSpannableStringWithTypefaces(String originalString, Map<String, Integer> typefacesBySubstring)
    {
        SpannableString spannableString = new SpannableString(originalString);
        for(String substring : typefacesBySubstring.keySet())
        {
            if(originalString.contains(substring))
            {
                int start = originalString.indexOf(substring);
                int end = start + substring.length();
                spannableString.setSpan(new StyleSpan(typefacesBySubstring.get(substring)), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

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

    public static String fetchSimpleDate(Calendar calendar)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(App.config.getDateFormat(), Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String fetchSimpleYear(Calendar calendar)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(App.config.getYearFormat(), Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String fetchSimpleTime(Calendar calendar)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(App.config.getTimeFormat(), Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getStringResource(int resourceId)
    {
        try
        {
            return App.getContext().getString(resourceId);
        }
        catch(Resources.NotFoundException rnfe)
        {
            Log.e(String.format(Locale.getDefault(), "resourceId[%d] not found", resourceId), rnfe);
            return App.getContext().getString(R.string.error_missing_text);
        }
    }

    public static String keyCodeToString(int keyCode)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            return "BACK";
        }
        else
        {
            Log.e(String.format(Locale.getDefault(), "unknown KeyCode[%d]", keyCode));
            return "unknown KeyCode";
        }
    }

    public static String resultCodeToString(int resultCode)
    {
        switch(resultCode)
        {
            case -1:
                return "RESULT_OK";

            case 0:
                return "RESULT_CANCELED";

            default:
                Log.e(String.format(Locale.getDefault(), "unknown ResultCode[%d]", resultCode));
                return "unknown ResultCode";
        }
    }


    private static final String loremIpsum = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat," +
            " sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet." +
            " Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua." +
            " At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet," +
            " consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores" +
            " et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \n" + "\n" + "Duis autem vel eum iriure dolor in hendrerit in vulputate" +
            " velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue" +
            " duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat" +
            " volutpat.   \n" + "\n" + "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum" +
            " iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit" +
            " praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.   \n" + "\n" + "Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming" +
            " id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam" +
            " erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure" +
            " dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis.   \n" + "\n" + "At vero eos et accusam et justo duo dolores et" +
            " ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod" +
            " tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea" +
            " takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, At accusam aliquyam diam diam dolore dolores duo eirmod eos erat," +
            " et nonumy sed tempor et et invidunt justo labore Stet clita ea et gubergren, kasd magna no rebum. sanctus sea sed takimata ut vero voluptua. est Lorem ipsum dolor sit amet." +
            " Lorem ipsum dolor sit amet, consetetur quis nostrud. ";

    public static String getLoremIpsum(int words)
    {
        if(words < 1)
        {
            words = 2;
        }
        else if(words > 500)
        {
            words = 500;
        }

        int indexOfSpace = loremIpsum.indexOf(" ");
        while(--words > 0 && indexOfSpace != -1)
        {
            indexOfSpace = loremIpsum.indexOf(" ", indexOfSpace + 1);
        }
        return loremIpsum.substring(0, indexOfSpace);
    }
}
