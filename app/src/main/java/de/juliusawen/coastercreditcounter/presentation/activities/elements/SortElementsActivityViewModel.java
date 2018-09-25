package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.SelectableRecyclerAdapter;

public class SortElementsActivityViewModel extends ViewModel
{
    public String toolbarTitle;
    public List<Element> elementsToSort;
    public SelectableRecyclerAdapter contentRecyclerAdapter;
}
