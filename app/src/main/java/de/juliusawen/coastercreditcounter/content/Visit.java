package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class Visit extends Element
{
    private Calendar calendar;

    private Visit(String name, UUID uuid, Calendar calendar)
    {
        super(name, uuid);
        this.calendar = calendar;
    }

    public static Visit createVisit(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_FULL_PATTERN, Locale.getDefault());
        Visit visit = new Visit(simpleDateFormat.format(calendar.getTime()), UUID.randomUUID(), calendar);
        Log.v(Constants.LOG_TAG,  String.format("Visit.createVisit:: %s created.", visit.getFullName()));

        return visit;
    }

    public Calendar getCalendar()
    {
        return this.calendar;
    }
}
