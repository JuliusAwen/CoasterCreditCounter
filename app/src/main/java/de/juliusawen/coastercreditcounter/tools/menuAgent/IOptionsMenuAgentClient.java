package de.juliusawen.coastercreditcounter.tools.menuAgent;

public interface IOptionsMenuAgentClient
{
    void handleHelpSelected();

    void handleExpandAllSelected();
    void handleCollapseAllSelected();

    void handleGroupByLocationSelected();
    void handleGroupByAttractionCategorySelected();
    void handleGroupByManufacturerSelected();
    void handleGroupByStatusSelected();

    void handleSortAscendingSelected();
    void handleSortDescendingSelected();
    void handleSortAttractionCategoriesSelected();
    void handleSortManufacturersSelected();
    void handleSortStatusesSelected();
    void handleSortByYearAscendingSelected();
    void handleSortByYearDescendingSelected();
    void handleSortByNameAscendingSelected();
    void handleSortByNameDescendingSelected();
    void handleSortByLocationAscendingSelected();
    void handleSortByLocationDescendingSelected();
    void handleSortByAttractionCategoryAscendingSelected();
    void handleSortByAttractionCategoryDescendingSelected();
    void handleSortByManufacturerAscendingSelected();
    void handleSortByManufacturerDescendingSelected();

    void handleGoToCurrentVisitSelected();

    void handleEnableEditingSelected();
    void handleDisableEditingSelected();
}
