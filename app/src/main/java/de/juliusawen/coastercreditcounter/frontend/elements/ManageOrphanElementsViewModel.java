package de.juliusawen.coastercreditcounter.frontend.elements;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.ElementType;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.menuAgent.OptionsMenuAgent;

public class ManageOrphanElementsViewModel extends ViewModel
{
    ElementType elementTypeToManage;
    IElement longClickedElement;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    OptionsMenuAgent optionsMenuAgent;
}
