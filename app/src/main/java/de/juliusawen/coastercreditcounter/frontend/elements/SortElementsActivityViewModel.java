package de.juliusawen.coastercreditcounter.frontend.elements;

import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class SortElementsActivityViewModel extends ViewModel
{
    List<IElement> elementsToSort;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
