package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

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
        Log.i(Constants.LOG_TAG,"UserSettings.Constructor:: <UserSettings> instantiated");
        this.persistency = persistency;
    }

    public boolean initialize()
    {
        Log.i(Constants.LOG_TAG, "UserSettings.initialize:: loading UserSettings...");
        Stopwatch stopwatch = new Stopwatch(true);
        persistency.loadSettings(this);
        Log.i(Constants.LOG_TAG, String.format("UserSettings.Constructor:: loading UserSettings took [%d]ms", stopwatch.stop()));

        Visit.setSortOrder(this.getDefaultSortOrderParkVisits());

        return this.validate();
    }

    private boolean validate()
    {
        //Todo: implement
        return true;
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
