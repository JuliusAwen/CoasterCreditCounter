package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.globals.persistency.Persistency;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Settings
{
    //App
    public static boolean jumpToTestActivityOnStart = false;

//    private boolean jumpToOpenVisitOnStart;

    //ShowPark - Visits
    private SortOrder defaultSortOrderParkVisits;
    private boolean expandLatestYearInListByDefault;

    //Visit
    private int firstDayOfTheWeek;

    //Defaults
    private int defaultIncrement;

    private static Settings instance;

    static Settings getInstance(Persistency persistency)
    {
        if(Settings.instance == null)
        {
            Settings.instance = new Settings(persistency);
        }
        return instance;
    }

    private Settings(Persistency persistency)
    {
        Log.i(Constants.LOG_TAG, "Settings.Constructor:: Settings instantiated - fetching settings...");

        Stopwatch stopwatch = new Stopwatch(true);
        persistency.loadSettings(this);
        Log.i(Constants.LOG_TAG, String.format("Settings.Constructor:: initializing settings took [%d]ms", stopwatch.stop()));
    }

    public void initialize()
    {
        Visit.setSortOrder(this.getDefaultSortOrderParkVisits());
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
