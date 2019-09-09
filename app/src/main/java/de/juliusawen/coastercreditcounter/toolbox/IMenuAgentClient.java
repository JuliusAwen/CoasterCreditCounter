package de.juliusawen.coastercreditcounter.toolbox;

public interface IMenuAgentClient
{


    boolean handleMenuItemExpandAllSelected();
    boolean handleMenuItemCollapseAllSelected();

    boolean handleMenuItemGroupByLocationSelected();
    boolean handleMenuItemGroupByAttractionCategorySelected();
    boolean handleMenuItemGroupByManufacturerSelected();
    boolean handleMenuItemGroupByStatusSelected();

    boolean handleMenuItemSortAscendingSelected();
    boolean handleMenuItemSortDescendingSelected();
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
