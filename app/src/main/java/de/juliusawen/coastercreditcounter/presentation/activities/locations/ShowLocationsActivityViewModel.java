package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ShowLocationsActivityViewModel extends ViewModel
{
    Element currentElement;
    List<Element> recentElements = new ArrayList<>();
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
