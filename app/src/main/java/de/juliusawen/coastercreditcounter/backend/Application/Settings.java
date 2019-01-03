package de.juliusawen.coastercreditcounter.backend.application;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.persistency.Persistence;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Settings
{
//    private boolean jumpToOpenVisitOnStart;

    //ShowPark - Visits
    private SortOrder defaultSortOrderParkVisits;
    private boolean expandLatestYearInListByDefault;

    //Visit
    private int firstDayOfTheWeek;

    //Defaults
    private int defaultIncrement;

    private final Persistence persistence;

    private static Settings instance;

    public static Settings getInstance(Persistence persistence)
    {
        if(Settings.instance == null)
        {
            Settings.instance = new Settings(persistence);
        }
        return instance;
    }

    private Settings(Persistence persistence)
    {
        this.persistence = persistence;
        Log.i(Constants.LOG_TAG,"Settings.Constructor:: <Settings> instantiated");
    }

    public boolean initialize()
    {
        Log.i(Constants.LOG_TAG, "Settings.initialize:: loading Settings...");
        Stopwatch stopwatch = new Stopwatch(true);

        if(persistence.loadSettings(this))
        {
            if(this.validate())
            {
                Visit.setSortOrder(this.getDefaultSortOrderParkVisits());
                Log.i(Constants.LOG_TAG, String.format("Settings.initialize:: loading settings successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Settings.initialize:: validation failed - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Settings.initialize:: loading settings failed - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    private boolean validate()
    {
        Log.i(Constants.LOG_TAG, "Settings.validate:: validating settings...");

        if(this.getDefaultSortOrderParkVisits() == null)
        {
            Log.e(Constants.LOG_TAG, "Settings.validate:: settings validation failed: default sort order for park visits is null");
            return false;
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("Settings.validate:: default sort order for park visits is [%S]", this.getDefaultSortOrderParkVisits().toString()));
        }

        Log.d(Constants.LOG_TAG, String.format("Settings.validate:: expand latest year in visits list [%S]", this.getExpandLatestYearInListByDefault()));

        String dayNames[] = new DateFormatSymbols().getWeekdays();
        Log.d(Constants.LOG_TAG, String.format("Settings.validate:: first day of the week is [%S]", dayNames[this.getFirstDayOfTheWeek()]));

        Log.d(Constants.LOG_TAG, String.format("Settings.validate:: default increment is [%S]", this.getDefaultIncrement()));


        Log.i(Constants.LOG_TAG, "Settings.validate:: settings validation successful");
        return true;
    }

    public JSONObject toJson()
    {
        Log.d(Constants.LOG_TAG, ("Settings.toJson:: creating json object from settings..."));

        try
        {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(Constants.JSON_STRING_DEFAULT_SORT_ORDER, this.getDefaultSortOrderParkVisits().ordinal());
            jsonObject.put(Constants.JSON_STRING_EXPAND_LATEST_YEAR_HEADER, this.getExpandLatestYearInListByDefault());
            jsonObject.put(Constants.JSON_STRING_FIRST_DAY_OF_THE_WEEK, this.getFirstDayOfTheWeek());
            jsonObject.put(Constants.JSON_STRING_DEFAULT_INCREMENT, this.getDefaultIncrement());

            return jsonObject;
        }
        catch(JSONException e)
        {
            e.printStackTrace();

            Log.e(Constants.LOG_TAG, String.format("Element.toJson:: failed with JSONException [%s]", e.getMessage()));
            return null;
        }
    }

    public void useDefaults()
    {
        Log.i(Constants.LOG_TAG, "Settings.useDefaults:: setting defaults...");

        //        this.setJumpToOpenVisitOnStart(false);

        this.setDefaultSortOrderParkVisits(SortOrder.DESCENDING);

        this.setExpandLatestYearInListByDefault(true);

        this.setFirstDayOfTheWeek(Calendar.MONDAY);

        this.setDefaultIncrement(1);
    }

    public SortOrder getDefaultSortOrderParkVisits()
    {
        return defaultSortOrderParkVisits;
    }

    public void setDefaultSortOrderParkVisits(SortOrder defaultSortOrderParkVisits)
    {
        this.defaultSortOrderParkVisits = defaultSortOrderParkVisits;
    }

    public boolean getExpandLatestYearInListByDefault()
    {
        return expandLatestYearInListByDefault;
    }

    public void setExpandLatestYearInListByDefault(boolean expandLatestYearInListByDefault)
    {
        this.expandLatestYearInListByDefault = expandLatestYearInListByDefault;
    }

    public int getFirstDayOfTheWeek()
    {
        return this.firstDayOfTheWeek;
    }

    public void setFirstDayOfTheWeek(int firstDayOfTheWeek)
    {
        this.firstDayOfTheWeek = firstDayOfTheWeek;
    }

//    public boolean jumpToOpenVisitOnStart()
//    {
//        return this.jumpToOpenVisitOnStart;
//    }
//
//    void setJumpToOpenVisitOnStart(boolean jumpToOpenVisitOnStart)
//    {
//        this.jumpToOpenVisitOnStart = jumpToOpenVisitOnStart;
//    }

    public int getDefaultIncrement()
    {
        return this.defaultIncrement;
    }

    public void setDefaultIncrement(int defaultIncrement)
    {
        this.defaultIncrement = defaultIncrement;
    }
}
