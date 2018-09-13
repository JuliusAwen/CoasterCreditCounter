package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.data.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Settings
{
    //App
    private boolean goToOpenVisitWhenOpeningApp;


    //ShowPark - Attractions
    private List<AttractionCategory> attractionCategoriesToExpandByDefault = new ArrayList<>();

    //ShowPark - Visits
    private SortOrder defaultSortOrderParkVisits;
    private boolean expandLatestYearInListByDefault;

    //Visit
    private int firstDayOfTheWeek;

    private static final Settings instance = new Settings();

    static Settings getInstance()
    {
        return instance;
    }

    private Settings()
    {
        Log.i(Constants.LOG_TAG, "Settings.Constructor:: Settings instantiated - fetching settings...");
        Stopwatch stopwatch = new Stopwatch(true);
        DatabaseMock.getInstance().fetchSettings(this);
        Log.i(Constants.LOG_TAG, String.format("Settings.Constructor:: initializing content took [%d]ms", stopwatch.stop()));
    }

    public List<AttractionCategory> getAttractionCategoriesToExpandByDefault()
    {
        return attractionCategoriesToExpandByDefault;
    }

    void setAttractionCategoriesToExpandByDefault(List<AttractionCategory> attractionCategoriesToExpandByDefault)
    {
        this.attractionCategoriesToExpandByDefault = attractionCategoriesToExpandByDefault;
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

    public boolean goToOpenVisitWhenOpeningApp()
    {
        return this.goToOpenVisitWhenOpeningApp;
    }

    void setGoToOpenVisitWhenOpeningApp(boolean goToOpenVisitWhenOpeningApp)
    {
        this.goToOpenVisitWhenOpeningApp = goToOpenVisitWhenOpeningApp;
    }

}
