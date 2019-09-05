package de.juliusawen.coastercreditcounter.toolbox;

public interface IMenuAgentClient
{
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
}
