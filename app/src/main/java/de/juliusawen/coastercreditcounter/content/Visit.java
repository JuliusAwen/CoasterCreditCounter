package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

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

    public static List<Visit> convertToVisits(List<? extends Element> elements)
    {
        List<Visit> visits = new ArrayList<>();

        for(Element element : elements)
        {
            if(element.isInstance(Visit.class))
            {
                visits.add(0, (Visit)element);
            }
            else
            {
                String errorMessage = String.format("Visit.convertToVisits:: type mismatch - %s is not of type Visit", element);
                Log.e(Constants.LOG_TAG, errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }

        return visits;
    }

    public static List<Element> sortDateDescending(List<Element> elements)
    {
        List<Visit> visits = Visit.convertToVisits(elements);

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        HashMap<String, Visit> visitsByDateString = new HashMap<>();

        for(Visit visit : visits)
        {
            visitsByDateString.put(simpleDateFormat.format(visit.getCalendar().getTime()), visit);
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Element> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(visitsByDateString.get(dateString));
        }

        return sortedVisits;
    }

    public static List<Element> sortDateAscending(List<Element> elements)
    {
        List<Visit> visits = Visit.convertToVisits(elements);

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        HashMap<String, Visit> visitsByDateString = new HashMap<>();

        for(Visit visit : visits)
        {
            visitsByDateString.put(simpleDateFormat.format(visit.getCalendar().getTime()), visit);
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Element> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(0, visitsByDateString.get(dateString));
        }

        return sortedVisits;
    }
}
