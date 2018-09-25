package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.SelectableRecyclerAdapter;

public class PickElementsActivityViewModel extends ViewModel
{
    public List<Element> elementsToPickFrom;
    public String toolbarTitle;
    public String toolbarSubtitle;
    public SelectableRecyclerAdapter contentRecyclerViewAdapter;
}
