package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class SortElementsActivityViewModel extends ViewModel
{
    public List<IElement> elementsToSort;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public IElement selectedElement;
    public IElement defaultProperty;

    public OptionsMenuAgent optionsMenuAgent;
}
