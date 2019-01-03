package de.juliusawen.coastercreditcounter.backend.objects.elements;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;
import de.juliusawen.coastercreditcounter.backend.objects.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public class Visit extends Element
{
    private static Visit openVisit;
    private static SortOrder sortOrder = SortOrder.DESCENDING;

    private final Calendar calendar;

    private Visit(String name, UUID uuid, Calendar calendar)
    {
        super(name, uuid);
        this.calendar = calendar;
    }

    public static Visit create(int year, int month, int day, UUID uuid)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return Visit.createInstance(calendar, uuid);
    }

    public static Visit create(Calendar calendar, UUID uuid)
    {
        return Visit.createInstance(calendar, uuid);
    }

    private static Visit createInstance(Calendar calendar, UUID uuid)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_FULL_PATTERN, Locale.getDefault());
        Visit visit = new Visit(simpleDateFormat.format(calendar.getTime()), uuid == null ? UUID.randomUUID() : uuid, calendar);

        Log.v(Constants.LOG_TAG,  String.format("Visit.createInstance:: %s created.", visit.getFullName()));
        return visit;
    }


    @Override
    @NonNull
    public String toString()
    {
        if(this.getParent() != null)
        {
            return String.format(Locale.getDefault(), "[%s \"%s\" @ %s]", this.getClass().getSimpleName(), this.getName(), this.getParent().getName());
        }
        else
        {
            return super.toString();
        }
    }

    public JSONObject toJson() throws JSONException
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.JSON_STRING_ELEMENT, Element.toJson(this, false));

            jsonObject.put(Constants.JSON_STRING_DAY, this.getCalendar().get(Calendar.DAY_OF_MONTH));
            jsonObject.put(Constants.JSON_STRING_MONTH, this.getCalendar().get(Calendar.MONTH));
            jsonObject.put(Constants.JSON_STRING_YEAR, this.getCalendar().get(Calendar.YEAR));

            int counter = 0;
            JSONArray jsonArrayRideCountByAttraction = new JSONArray();
            for(VisitedAttraction visitedAttraction : this.getChildrenAsType(VisitedAttraction.class))
            {
                JSONObject jsonObjectRideCountByAttraction = new JSONObject();
                jsonObjectRideCountByAttraction.put(visitedAttraction.getOnSiteAttraction().getUuid().toString(), visitedAttraction.getRideCount());
                jsonArrayRideCountByAttraction.put(jsonObjectRideCountByAttraction);
                counter ++;
            }
            jsonObject.put(Constants.JSON_STRING_RIDE_COUNT_BY_ATTRACTIONS, counter > 0 ? jsonArrayRideCountByAttraction : JSONObject.NULL);

            Log.v(Constants.LOG_TAG, String.format("Visit.toJson:: created JSON for %s [%s]", this, jsonObject.toString()));
            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, String.format("Visit.toJson:: creation for %s failed with JSONException [%s]", this, e.getMessage()));
            throw e;
        }
    }
    public Calendar getCalendar()
    {
        return this.calendar;
    }

    public static void setOpenVisit(Element visit)
    {
        if(visit != null)
        {
            Log.i(Constants.LOG_TAG, String.format("Visit.setOpenVisit:: %s set as open visit", visit));
            Visit.openVisit = (Visit) visit;
        }
        else
        {
            Log.i(Constants.LOG_TAG,"Visit.setOpenVisit:: open visit cleared");
            Visit.openVisit = null;
        }
    }

    public static Element getOpenVisit()
    {
        return Visit.openVisit;
    }

    private boolean isOpenVisit()
    {
        return Visit.getOpenVisit() != null && this.equals(Visit.getOpenVisit());
    }

    public static boolean validateOpenVisit()
    {
        if(Visit.getOpenVisit() != null)
        {
            Calendar calendar = Calendar.getInstance();
            Calendar visitCalender = Visit.openVisit.getCalendar();

            if(Visit.isSameDay(calendar, visitCalender))
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

    public static boolean isSameDay(Calendar calendar, Calendar compareCalendar)
    {
        return calendar.get(Calendar.YEAR) == compareCalendar.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == compareCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public static SortOrder getSortOrder()
    {
        return Visit.sortOrder;
    }

    public static void setSortOrder(SortOrder sortOrder)
    {
        Log.d(Constants.LOG_TAG, String.format("Visit.setSortOrder:: sort order set to [%s]", sortOrder.toString()));
        Visit.sortOrder = sortOrder;
    }

    public static List<Visit> sortVisitsByDateAccordingToSortOrder(List<Visit> visits)
    {
        if(Visit.getSortOrder().equals(SortOrder.ASCENDING))
        {
            visits = Visit.sortAscendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("Visit.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <ascending>", visits.size()));
        }
        else if(Visit.getSortOrder().equals(SortOrder.DESCENDING))
        {
            visits = Visit.sortDescendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("Visit.sortVisitsByDateAccordingToSortOrder:: sorted #[%d] visits <descending>", visits.size()));
        }

        return visits;
    }

    private static List<Visit> sortDescendingByDate(List<Visit> visits)
    {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        HashMap<String, Visit> visitsByDateString = new HashMap<>();
        for(Visit visit : visits)
        {
            String dateString = simpleDateFormat.format(visit.getCalendar().getTime());
            visitsByDateString.put(dateString, visit);
            Log.v(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: parsed date [%s] from name %s", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Visit> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(0, visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: [%d] visits sorted", visits.size()));
        return sortedVisits;
    }

    private static List<Visit> sortAscendingByDate(List<Visit> visits)
    {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        HashMap<String, Visit> visitsByDateString = new HashMap<>();

        for(Visit visit : visits)
        {
            String dateString = simpleDateFormat.format(visit.getCalendar().getTime());
            visitsByDateString.put(dateString, visit);
            Log.v(Constants.LOG_TAG, String.format("Visit.sortDescendingByDate:: parsed date [%s] from name %s", dateString, visit));
        }

        List<String> dateStrings = new ArrayList<>(visitsByDateString.keySet());
        Collections.sort(dateStrings);

        List<Visit> sortedVisits = new ArrayList<>();
        for(String dateString : dateStrings)
        {
            sortedVisits.add(visitsByDateString.get(dateString));
        }

        Log.i(Constants.LOG_TAG, String.format("Visit.sortAscendingByDate:: [%d] visits sorted", visits.size()));

        return sortedVisits;
    }
}
