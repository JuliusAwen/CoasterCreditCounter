package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import android.arch.lifecycle.ViewModel;
import android.widget.TextView;

import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.SelectableRecyclerAdapter;

public class PickElementsViewModel extends ViewModel
{
    public List<Element> elementsToPickFrom;
    public String toolbarTitle;
    public String toolbarSubtitle;
    public SelectableRecyclerAdapter contentRecyclerAdapter;
    public TextView textViewSelectOrDeselectAll;
}
