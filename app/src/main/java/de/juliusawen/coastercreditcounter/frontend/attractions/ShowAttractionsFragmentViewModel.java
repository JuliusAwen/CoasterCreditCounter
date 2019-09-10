package de.juliusawen.coastercreditcounter.frontend.attractions;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.menuAgent.MenuAgent;

public class ShowAttractionsFragmentViewModel extends ViewModel
{
    Park park;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
    MenuAgent optionsMenuAgent;
}
