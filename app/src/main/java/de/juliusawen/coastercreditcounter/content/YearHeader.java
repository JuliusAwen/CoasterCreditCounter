package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class YearHeader extends TemporaryElement
{
    private YearHeader(String name)
    {
        super(name);
    }

    public static YearHeader createYearHeader(String name)
    {
        YearHeader yearHeader = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            yearHeader = new YearHeader(name);
            Log.v(Constants.LOG_TAG,  String.format("YearHeader.createYearHeader:: %s created.", yearHeader));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("YearHeader.createYearHeader:: invalid name[%s] - yearHeader not created.", name));
        }

        return yearHeader;
    }
}
