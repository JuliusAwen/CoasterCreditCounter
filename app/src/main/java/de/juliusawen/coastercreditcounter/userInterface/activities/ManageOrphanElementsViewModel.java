package de.juliusawen.coastercreditcounter.userInterface.activities;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.orphanElements.OrphanElementType;
import de.juliusawen.coastercreditcounter.tools.menuAgent.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ManageOrphanElementsViewModel extends ViewModel
{
    public OrphanElementType orphanElementTypeToManage;
    public IElement longClickedElement;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public OptionsMenuAgent optionsMenuAgent;
}
