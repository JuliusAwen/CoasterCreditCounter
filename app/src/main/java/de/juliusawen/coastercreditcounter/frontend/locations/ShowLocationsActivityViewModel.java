package de.juliusawen.coastercreditcounter.frontend.locations;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.toolbox.MenuAgent;

public class ShowLocationsActivityViewModel extends ViewModel
{
    IElement currentLocation;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
    boolean selectionMode;
    IElement newParent;
    MenuAgent optionsMenuAgent;
}
