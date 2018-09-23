package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.SelectableRecyclerAdapter;

public class SortElementsViewModel extends ViewModel
{
    public String toolbarTitle;
    public List<Element> elementsToSort;
    public SelectableRecyclerAdapter contentRecyclerAdapter;
}
