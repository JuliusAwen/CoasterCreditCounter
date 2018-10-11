package de.juliusawen.coastercreditcounter.presentation.attractions;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowAttractionsFragmentViewModel extends ViewModel
{
    public Park park;
    List<Element> attractionCategoryHeaders = new ArrayList<>();
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
