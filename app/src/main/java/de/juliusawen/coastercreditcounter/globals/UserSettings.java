package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.globals.persistency.Persistency;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class UserSettings
{
//    private boolean jumpToOpenVisitOnStart;

    //ShowPark - Visits
    private SortOrder defaultSortOrderParkVisits;
    private boolean expandLatestYearInListByDefault;

    //Visit
    private int firstDayOfTheWeek;

    //Defaults
    private int defaultIncrement;

    private Persistency persistency;

    private static UserSettings instance;

    public static UserSettings getInstance(Persistency persistency)
    {
        if(UserSettings.instance == null)
        {
            UserSettings.instance = new UserSettings(persistency);
        }
        return instance;
    }

    private UserSettings(Persistency persistency)
    {
        this.persistency = persistency;
        Log.i(Constants.LOG_TAG,"UserSettings.Constructor:: <UserSettings> instantiated");
    }

    public boolean initialize()
    {
        Log.i(Constants.LOG_TAG, "UserSettings.initialize:: loading UserSettings...");
        Stopwatch stopwatch = new Stopwatch(true);

        if(persistency.loadUserSettings(this))
        {
            if(this.validate())
            {
                Visit.setSortOrder(this.getDefaultSortOrderParkVisits());
                Log.i(Constants.LOG_TAG, String.format("UserSettings.initialize:: loading UserSettings successful - took [%d]ms", stopwatch.stop()));
                return true;
            }
            else
            {
                Log.i(Constants.LOG_TAG, String.format("UserSettings.initialize:: loading UserSettings failed: validation failed - took [%d]ms", stopwatch.stop()));
            }
        }
        else
        {
            Log.i(Constants.LOG_TAG, String.format("UserSettings.initialize:: loading UserSettings failed - took [%d]ms", stopwatch.stop()));
        }

        return false;
    }

    private boolean validate()
    {
        Log.i(Constants.LOG_TAG, "UserSettings.validate:: validating user settings...");

        if(this.getDefaultSortOrderParkVisits() == null)
        {
            Log.e(Constants.LOG_TAG, "UserSettings.validate:: validating user settings failed: default sort order for park visits is null");
            return false;
        }
        else
        {
            Log.d(Constants.LOG_TAG, String.format("UserSettings.validate:: default sort order for park visits is [%S]", this.getDefaultSortOrderParkVisits().toString()));
        }

        Log.d(Constants.LOG_TAG, String.format("UserSettings.validate:: expand latest year in visits list [%S]", this.getExpandLatestYearInListByDefault()));

        String dayNames[] = new DateFormatSymbols().getWeekdays();
        Log.d(Constants.LOG_TAG, String.format("UserSettings.validate:: first day of the week is [%S]", dayNames[this.getFirstDayOfTheWeek()]));

        Log.d(Constants.LOG_TAG, String.format("UserSettings.validate:: default increment is [%S]", this.getDefaultIncrement()));


        Log.i(Constants.LOG_TAG, "UserSettings.validate:: validating user settings successful");
        return true;
    }

    public JSONObject toJson()
    {
        return null;
    }

    public void useDefaults()
    {
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
