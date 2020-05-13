package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowAttractionsFragmentViewModel extends ViewModel
{
    public Park park;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public IElement longClickedElement;
    public OptionsMenuProvider optionsMenuProvider;
}
