package de.juliusawen.coastercreditcounter.presentation.elements;

import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class PickElementsActivityViewModel extends ViewModel
{
    List<IElement> elementsToPickFrom;
    String toolbarTitle;
    String toolbarSubtitle;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
