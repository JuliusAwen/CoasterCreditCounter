package de.juliusawen.coastercreditcounter.presentation.elements;

import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class SortElementsActivityViewModel extends ViewModel
{
    String toolbarTitle;
    List<IElement> elementsToSort;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
