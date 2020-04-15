package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.PropertyType;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ManagePropertiesViewModel extends ViewModel
{
    public PropertyType propertyTypeToManage;
    public IElement longClickedElement;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public OptionsMenuAgent optionsMenuAgent;
    public IElement propertyToReturn;

    public boolean isSelectionMode = false;
}
