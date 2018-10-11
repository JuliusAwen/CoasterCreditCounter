package de.juliusawen.coastercreditcounter.presentation.visits;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowVisitActivityViewModel extends ViewModel
{
    public Visit visit;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
