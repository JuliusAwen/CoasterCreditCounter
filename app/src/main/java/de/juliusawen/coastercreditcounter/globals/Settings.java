package de.juliusawen.coastercreditcounter.globals;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.content.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public class Settings
{
    private static final Settings instance = new Settings();



    //ShowPark - Attractions
    private List<AttractionCategory> categoriesExpandedByDefault = new ArrayList<>();

    //ShowPark - Visits
    private SortOrder sortOrderVisits;
    private boolean expandLatestYearInListByDefault;



    static Settings getInstance()
    {
        return instance;
    }

    private Settings()
    {
        new DatabaseMock().fetchSettings(this);
    }

    public List<AttractionCategory> getCategoriesExpandedByDefault()
    {
        return categoriesExpandedByDefault;
    }

    void setCategoriesExpandedByDefault(List<AttractionCategory> categoriesExpandedByDefault)
    {
        this.categoriesExpandedByDefault = categoriesExpandedByDefault;
    }

    public SortOrder getSortOrderVisits()
    {
        return sortOrderVisits;
    }

    void setSortOrderVisits(SortOrder sortOrderVisits)
    {
        this.sortOrderVisits = sortOrderVisits;
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
