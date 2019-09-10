package de.juliusawen.coastercreditcounter.frontend.visits;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.menuAgent.MenuAgent;

public class ShowVisitActivityViewModel extends ViewModel
{
    Visit visit;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
    MenuAgent optionsMenuAgent;
}
