package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.tools.menuAgent.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowVisitActivityViewModel extends ViewModel
{
    public Visit visit;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public IElement longClickedElement;
    public OptionsMenuAgent optionsMenuAgent;
}
