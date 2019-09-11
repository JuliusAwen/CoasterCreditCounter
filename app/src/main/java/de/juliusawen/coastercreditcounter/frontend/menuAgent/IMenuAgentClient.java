package de.juliusawen.coastercreditcounter.frontend.menuAgent;

public interface IMenuAgentClient
{
    void handleMenuItemHelpSelected();

    void handleMenuItemExpandAllSelected();
    void handleMenuItemCollapseAllSelected();

    void handleMenuItemGroupByLocationSelected();
    void handleMenuItemGroupByAttractionCategorySelected();
    void handleMenuItemGroupByManufacturerSelected();
    void handleMenuItemGroupByStatusSelected();

    void handleMenuItemSortAscendingSelected();
    void handleMenuItemSortDescendingSelected();
    void handleMenuItemSortAttractionCategoriesSelected();
    void handleMenuItemSortManufacturersSelected();
    void handleMenuItemSortStatusesSelected();
    void handleMenuItemSortByYearAscendingSelected();
    void handleMenuItemSortByYearDescendingSelected();
    void handleMenuItemSortByNameAscendingSelected();
    void handleMenuItemSortByNameDescendingSelected();
    void handleMenuItemSortByLocationAscendingSelected();
    void handleMenuItemSortByLocationDescendingSelected();
    void handleMenuItemSortByAttractionCategoryAscendingSelected();
    void handleMenuItemSortByAttractionCategoryDescendingSelected();
    void handleMenuItemSortByManufacturerAscendingSelected();
    void handleMenuItemSortByManufacturerDescendingSelected();

    void handleMenuItemGoToCurrentVisitSelected();

    void handleMenuItemEnableEditingSelected();
    void handleMenuItemDisableEditingSelected();
}
