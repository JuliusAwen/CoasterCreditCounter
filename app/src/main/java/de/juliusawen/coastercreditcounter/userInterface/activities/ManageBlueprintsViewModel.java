package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ManageBlueprintsViewModel extends ViewModel
{
    public IElement longClickedElement;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public OptionsMenuAgent optionsMenuAgent;
    public IElement blueprintToReturn;

    public boolean isSelectionMode = false;
}
