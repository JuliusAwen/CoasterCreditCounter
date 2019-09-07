package de.juliusawen.coastercreditcounter.toolbox;

import android.view.MenuItem;

public interface IMenuAgentClient
{
    boolean handleInvalidOptionsMenuItemSelected(MenuItem item);

    boolean handleMenuItemHelpSelected();

    boolean handleMenuItemExpandAllSelected();
    boolean handleMenuItemCollapseAllSelected();

    boolean handleMenuItemGroupByLocationSelected();
    boolean handleMenuItemGroupByAttractionCategorySelected();
    boolean handleMenuItemGroupByManufacturerSelected();
    boolean handleMenuItemGroupByStatusSelected();

    boolean handleMenuItemSortByYearAscendingSelected();
    boolean handleMenuItemSortByYearDescendingSelected();
    boolean handleMenuItemSortByNameAscendingSelected();
    boolean handleMenuItemSortByNameDescendingSelected();
    boolean handleMenuItemSortByLocationAscendingSelected();
    boolean handleMenuItemSortByLocationDescendingSelected();
    boolean handleMenuItemSortByAttractionCategoryAscendingSelected();
    boolean handleMenuItemSortByAttractionCategoryDescendingSelected();
    boolean handleMenuItemSortByManufacturerAscendingSelected();
    boolean handleMenuItemSortByManufacturerDescendingSelected();

    boolean handleMenuItemGoToCurrentVisitSelected();

    boolean handleMenuItemEnableEditingSelected();
    boolean handleMenuItemDisableEditingSelected();
}
