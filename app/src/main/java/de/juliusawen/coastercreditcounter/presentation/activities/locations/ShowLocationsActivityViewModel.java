package de.juliusawen.coastercreditcounter.presentation.activities.locations;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowLocationsActivityViewModel extends ViewModel
{
    public Element currentElement;
    public List<Element> recentElements = new ArrayList<>();
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
