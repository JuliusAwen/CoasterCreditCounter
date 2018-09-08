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

    public static Visit createVisit(Calendar calendar, Park park)
    {
        Visit visit;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd. MMMM YYYY", Locale.getDefault());
        visit = new Visit(simpleDateFormat.format(calendar.getTime()), UUID.randomUUID(), calendar);
        Log.e(Constants.LOG_TAG,  String.format("Visit.createVisit:: %s created.", visit.getFullName()));

        return visit;
    }
}
