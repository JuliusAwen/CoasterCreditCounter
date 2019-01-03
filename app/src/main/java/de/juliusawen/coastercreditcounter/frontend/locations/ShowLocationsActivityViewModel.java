package de.juliusawen.coastercreditcounter.frontend.locations;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ShowLocationsActivityViewModel extends ViewModel
{
    IElement currentElement;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
}
