package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowLocationsActivityViewModel extends ViewModel
{
    Element currentElement;
    List<Element> recentElements = new ArrayList<>();
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
