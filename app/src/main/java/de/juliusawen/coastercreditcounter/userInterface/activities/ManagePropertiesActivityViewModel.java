package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.PropertyType;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuButler;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ManagePropertiesActivityViewModel extends ViewModel
{
    public RequestCode requestCode;

    public List<IElement> elements;

    public PropertyType propertyTypeToManage;
    public IElement longClickedElement;
    public IElement propertyToReturn;

    public boolean isSelectionMode = false;

    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public OptionsMenuAgent optionsMenuAgent;
    public OptionsMenuButler optionsMenuButler;
}
