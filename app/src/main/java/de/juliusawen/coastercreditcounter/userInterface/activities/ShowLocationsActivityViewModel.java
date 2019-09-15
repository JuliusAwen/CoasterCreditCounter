package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowLocationsActivityViewModel extends ViewModel
{
    public IElement currentLocation;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public IElement longClickedElement;
    public boolean selectionMode;
    public IElement newParent;
    public OptionsMenuAgent optionsMenuAgent;
}
