package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class PickElementsActivityViewModel extends ViewModel
{
    public RequestCode requestCode;
    public boolean isSinglePick;
    public List<IElement> elementsToPickFrom;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public OptionsMenuAgent optionsMenuAgent;
}
