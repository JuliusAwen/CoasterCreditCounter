package de.juliusawen.coastercreditcounter.toolbox;

import android.text.SpannableString;
import android.text.style.StyleSpan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.backend.application.App;

public abstract class StringTool
{
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
}
