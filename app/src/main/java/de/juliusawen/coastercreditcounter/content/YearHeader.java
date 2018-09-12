package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class YearHeader extends Element
{
    private YearHeader(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static YearHeader create(String name)
    {
        YearHeader yearHeader = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            yearHeader = new YearHeader(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("YearHeader.create:: %s created", yearHeader));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("YearHeader.create:: invalid name[%s] - yearHeader not created", name));
        }
        return yearHeader;
    }

    public static YearHeader getLatestYearHeader(List<? extends Element> yearHeaders)
    {
        YearHeader latestYearHeader = null;

        for(Element yearHeader : yearHeaders)
        {
            if(latestYearHeader == null)
            {
                latestYearHeader = (YearHeader) yearHeader;
            }
            else if((Integer.valueOf(yearHeader.getName()) > (Integer.valueOf(latestYearHeader.getName()))))
            {
                latestYearHeader = (YearHeader) yearHeader;
            }
        }

        Log.v(Constants.LOG_TAG,  String.format("YearHeader.getLatestYearHeader:: [%s] to be found latest YearHeader in a list of #[%d]", latestYearHeader, yearHeaders.size()));
        return latestYearHeader;
    }
}
