package de.juliusawen.coastercreditcounter.presentation.visits;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowVisitActivityViewModel extends ViewModel
{
    public Visit visit;
    List<Element> attractionCategoryHeaders = new ArrayList<>();
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
