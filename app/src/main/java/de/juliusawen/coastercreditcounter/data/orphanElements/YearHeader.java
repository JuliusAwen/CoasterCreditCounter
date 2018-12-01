package de.juliusawen.coastercreditcounter.data.orphanElements;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class YearHeader extends OrphanElement
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

    public static YearHeader getLatestYearHeader(List<? extends IElement> yearHeaders)
    {
        YearHeader latestYearHeader = null;

        if(yearHeaders.size() > 0)
        {
            for(IElement yearHeader : yearHeaders)
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

            Log.v(Constants.LOG_TAG,  String.format("YearHeader.getLatestYearHeader:: %s found as latest YearHeader in a list of [%d]", latestYearHeader, yearHeaders.size()));
        }

        return latestYearHeader;
    }

    public static List<IElement> fetchCategorizedVisits(List<Visit> visits)
    {
        if(visits.isEmpty())
        {
            Log.v(Constants.LOG_TAG, "YearHeader.fetchCategorizedVisits:: no elements found");
            return new ArrayList<IElement>(visits);
        }

        Log.v(Constants.LOG_TAG, String.format("YearHeader.fetchCategorizedVisits:: adding YearHeaders to [%d] elements...", visits.size()));

        List<YearHeader> yearHeaders = App.content.getOrphanElementsAsType(YearHeader.class);

        OrphanElement.removeAllChildren(yearHeaders);

        List<IElement> preparedElements = new ArrayList<>();

        DateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_YEAR_PATTERN, Locale.getDefault());

        for(Visit visit : visits)
        {
            String year = String.valueOf(simpleDateFormat.format(visit.getCalendar().getTime()));

            IElement existingYearHeader = null;
            for(IElement yearHeader : preparedElements)
            {
                if(yearHeader.getName().equals(year))
                {
                    existingYearHeader = yearHeader;
                }
            }

            if(existingYearHeader != null)
            {
                existingYearHeader.addChild(visit);
            }
            else
            {
                YearHeader yearHeader = null;

                for(YearHeader header : yearHeaders)
                {
                    if(header.getName().equals(year))
                    {
                        yearHeader = header;
                        break;
                    }
                }

                if(yearHeader == null)
                {
                    yearHeader = YearHeader.create(year);
                    App.content.addOrphanElement(yearHeader);
                    Log.d(Constants.LOG_TAG, String.format("YearHeader.fetchCategorizedVisits:: created new %s", yearHeader));
                }

                yearHeader.addChild(visit);
                preparedElements.add(yearHeader);
            }
        }

        Log.d(Constants.LOG_TAG, String.format("YearHeader.fetchCategorizedVisits:: [%d] YearHeaders added", preparedElements.size()));
        return preparedElements;
    }
}
