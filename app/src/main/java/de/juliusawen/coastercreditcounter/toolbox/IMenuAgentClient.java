package de.juliusawen.coastercreditcounter.toolbox;

public interface IMenuAgentClient
{
    boolean handleMenuItemExpandAllSelected();
    boolean handleMenuItemCollapseAllSelected();

    boolean handleMenuItemGroupByLocationSelected();
    boolean handleMenuItemGroupByAttractionCategorySelected();
    boolean handleMenuItemGroupByManufacturerSelected();
    boolean handleMenuItemGroupByStatusSelected();

    boolean handleMenuItemHelpSelected();
}
