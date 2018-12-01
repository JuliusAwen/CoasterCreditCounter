package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
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
    private AttractionCategory defaultAttractionCategory;
    private int defaultIncrement;



    private static Settings instance;

    private static IDatabaseWrapper databaseWrapper;

    static Settings getInstance(IDatabaseWrapper databaseWrapper)
    {
        Settings.databaseWrapper = databaseWrapper;
        Settings.instance = new Settings();
        return instance;
    }

    private Settings()
    {
        Log.i(Constants.LOG_TAG, "Settings.Constructor:: Settings instantiated - fetching settings...");
        Stopwatch stopwatch = new Stopwatch(true);
        Settings.databaseWrapper.fetchSettings(this);
        Log.i(Constants.LOG_TAG, String.format("Settings.Constructor:: initializing settings took [%d]ms", stopwatch.stop()));
    }

    public SortOrder getDefaultSortOrderParkVisits()
    {
        return defaultSortOrderParkVisits;
    }

    void setDefaultSortOrderParkVisits(SortOrder defaultSortOrderParkVisits)
    {
        this.defaultSortOrderParkVisits = defaultSortOrderParkVisits;
    }

    public boolean getExpandLatestYearInListByDefault()
    {
        return expandLatestYearInListByDefault;
    }

    void setExpandLatestYearInListByDefault(boolean expandLatestYearInListByDefault)
    {
        this.expandLatestYearInListByDefault = expandLatestYearInListByDefault;
    }

    public int getFirstDayOfTheWeek()
    {
        return this.firstDayOfTheWeek;
    }

    void setFirstDayOfTheWeek(int firstDayOfTheWeek)
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

    public AttractionCategory getDefaultAttractionCategory()
    {
        return defaultAttractionCategory;
    }

    void setDefaultAttractionCategory(AttractionCategory defaultAttractionCategory)
    {
        this.defaultAttractionCategory = defaultAttractionCategory;
    }

    public int getDefaultIncrement()
    {
        return this.defaultIncrement;
    }

    void setDefaultIncrement(int defaultIncrement)
    {
        this.defaultIncrement = defaultIncrement;
    }
}
