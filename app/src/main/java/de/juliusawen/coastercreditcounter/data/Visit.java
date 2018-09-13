package de.juliusawen.coastercreditcounter.data;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public class Visit extends Element
{
    public static SortOrder sortOrder = SortOrder.DESCENDING;

    private static Visit openVisit;

    private Calendar calendar;
    private Map<Element, Integer> rideCountByAttractions = new HashMap<>();

    private Visit(String name, UUID uuid, Calendar calendar)
    {
        super(name, uuid);
        this.calendar = calendar;
    }

    public static Visit create(int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return Visit.createInstance(calendar);
    }

    public static Visit create(Calendar calendar)
    {
        return Visit.createInstance(calendar);
    }

    private static Visit createInstance(Calendar calendar)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_FULL_PATTERN, Locale.getDefault());
        Visit visit = new Visit(simpleDateFormat.format(calendar.getTime()), UUID.randomUUID(), calendar);

        Log.v(Constants.LOG_TAG,  String.format("Visit.createInstance:: %s created.", visit.getFullName()));


        return visit;
    }

    public Calendar getCalendar()
    {
        return this.calendar;
    }

    public void initialize()
    {
        for(Element element : this.getParent().getChildrenOfInstance(Attraction.class))
        {
            this.rideCountByAttractions.put(element, 0);
        }
    }

    public void updateAttractions()
    {
        for(Element element : this.getParent().getChildrenOfInstance(Attraction.class))
        {
            if(!this.rideCountByAttractions.containsKey(element))
            {
                this.rideCountByAttractions.put(element, 0);
            }
        }
    }

    public Map<Element, Integer> getRideCountByAttraction()
    {
        return this.rideCountByAttractions;
    }

    public static void setOpenVisit(Element visit)
    {
        if(visit != null)
        {
            Log.i(Constants.LOG_TAG, String.format("Visit.setOpenVisit:: %s@%s set as open visit", visit, visit.getParent()));
            Visit.openVisit = (Visit) visit;
        }
        else
        {
            Log.i(Constants.LOG_TAG,"Visit.setOpenVisit:: open visit reset");
            Visit.openVisit = null;
        }
    }

    public static Element getOpenVisit()
    {
        return Visit.openVisit;
    }

    private boolean isOpenVisit()
    {
        if(Visit.getOpenVisit() != null)
        {
            return this.equals(Visit.getOpenVisit());
        }
        else
        {
            return false;
        }
    }

    public static boolean validateOpenVisit()
    {
        if(Visit.getOpenVisit() != null)
        {
            Calendar calendar = Calendar.getInstance();
            Calendar visitCalender = Visit.openVisit.getCalendar();

            boolean sameDay = visitCalender.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
            boolean sameMonth =  visitCalender.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);
            boolean sameYear = visitCalender.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);

            if(sameDay && sameMonth && sameYear)
            {
                return true;
            }
            else
            {
                Visit.setOpenVisit(null);
            }
        }
        return false;
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
                String errorMessage = String.format("type mismatch: %s is not of type <Visit>", element);
                Log.e(Constants.LOG_TAG, "Visit.convertToVisits:: " + errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }
        return visits;
    }

    public static List<Element> sortVisitsByDateAccordingToSortOrder(List<Element> visits)
    {
        if(Visit.sortOrder.equals(SortOrder.ASCENDING))
        {
            visits = Visit.sortAscendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <ascending>", visits.size()));
        }
        else if(Visit.sortOrder.equals(SortOrder.DESCENDING))
        {
            visits = Visit.sortDescendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <descending>", visits.size()));
        }

        return visits;
    }

    private static List<Element> sortDescendingByDate(List<Element> elements)
    {
        List<Visit> visits = Visit.convertToVisits(elements);
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        HashMap<String, Visit> visitsByDateString = new HashMap<>();
        for(Visit visit : visits)
        {
            String dateString = simpleDateFormat.format(visit.getCalendar().getTime());
            visitsByDateString.put(dateString, visit);
            Log.v(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: parsed date [%s] from name [%s]", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Element> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: #[%d] visits sorted", elements.size()));
        return sortedVisits;
    }

    private static List<Element> sortAscendingByDate(List<Element> elements)
    {
        List<Visit> visits = Visit.convertToVisits(elements);

        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        HashMap<String, Visit> visitsByDateString = new HashMap<>();

        for(Visit visit : visits)
        {
            String dateString = simpleDateFormat.format(visit.getCalendar().getTime());
            visitsByDateString.put(dateString, visit);
            Log.v(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: parsed date [%s] from name [%s]", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Element> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(0, visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("Visit.sortAscendingByDate:: #[%d] visits sorted", elements.size()));

        return sortedVisits;
    }
}
