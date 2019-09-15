package de.juliusawen.coastercreditcounter.tools.menuAgents;

public interface IPopupMenuAgentClient
{
    void handleAddLocationClicked();
    void handleAddParkClicked();
    void handleSortLocationsClicked();
    void handleSortParksClicked();
    void handleSortAttractionsClicked();
    void handleEditLocationClicked();
    void handleEditParkClicked();
    void handleEditElementClicked();
    void handleEditCustomAttractionClicked();
    void handleRemoveElementClicked();
    void handleRelocateElementClicked();
    void handleDeleteElementClicked();
    void handleDeleteAttractionClicked();
    void handleAssignToAttractionsClicked();
    void handleSetAsDefaultClicked();
}
