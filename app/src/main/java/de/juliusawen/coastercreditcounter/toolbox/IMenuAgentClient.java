package de.juliusawen.coastercreditcounter.toolbox;

import android.view.MenuItem;

public interface IMenuAgentClient
{
    boolean handleInvalidOptionsMenuItemSelected(MenuItem item);

    boolean handleOptionsMenuItemHelpSelected();

    boolean handleOptionsMenuItemExpandAllSelected();
    boolean handleOptionsMenuItemCollapseAllSelected();

    boolean handleOptionsMenuItemGroupByLocationSelected();
    boolean handleOptionsMenuItemGroupByAttractionCategorySelected();
    boolean handleOptionsMenuItemGroupByManufacturerSelected();
    boolean handleOptionsMenuItemGroupByStatusSelected();

    boolean handleOptionsMenuItemSortByYearAscendingSelected();
    boolean handleOptionsMenuItemSortByYearDescendingSelected();
    boolean handleOptionsMenuItemSortByNameAscendingSelected();
    boolean handleOptionsMenuItemSortByNameDescendingSelected();
    boolean handleOptionsMenuItemSortByLocationAscendingSelected();
    boolean handleOptionsMenuItemSortByLocationDescendingSelected();
    boolean handleOptionsMenuItemSortByAttractionCategoryAscendingSelected();
    boolean handleOptionsMenuItemSortByAttractionCategoryDescendingSelected();
    boolean handleOptionsMenuItemSortByManufacturerAscendingSelected();
    boolean handleOptionsMenuItemSortByManufacturerDescendingSelected();

    boolean handleOptionsMenuItemGoToCurrentVisitSelected();

    boolean handleOptionsMenuItemEnableEditingSelected();
    boolean handleOptionsMenuItemDisableEditingSelected();
}
