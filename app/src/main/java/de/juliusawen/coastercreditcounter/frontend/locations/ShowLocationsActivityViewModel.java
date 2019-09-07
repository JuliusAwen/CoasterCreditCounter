package de.juliusawen.coastercreditcounter.frontend.locations;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowLocationsActivityViewModel extends ViewModel
{
    IElement currentLocation;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
    boolean selectionMode;
    IElement newParent;

    @Override
    public void onCleared()
    {
        super.onCleared();
        this.contentRecyclerViewAdapter = null;
    }
}
