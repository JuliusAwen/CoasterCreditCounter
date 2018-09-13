package de.juliusawen.coastercreditcounter.globals;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.data.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public class Settings
{
   //ShowPark - Attractions
    private List<AttractionCategory> attractionCategoriesToExpandByDefault = new ArrayList<>();

    //ShowPark - Visits
    private SortOrder defaultSortOrderParkVisits;
    private boolean expandLatestYearInListByDefault;



    private static final Settings instance = new Settings();

    static Settings getInstance()
    {
        return instance;
    }

    private Settings()
    {
        DatabaseMock.getInstance().fetchSettings(this);
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
}
