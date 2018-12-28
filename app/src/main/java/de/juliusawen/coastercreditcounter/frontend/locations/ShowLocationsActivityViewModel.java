package de.juliusawen.coastercreditcounter.frontend.locations;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ShowLocationsActivityViewModel extends ViewModel
{
    IElement currentElement;
    final List<IElement> recentElements = new ArrayList<>();
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
}
