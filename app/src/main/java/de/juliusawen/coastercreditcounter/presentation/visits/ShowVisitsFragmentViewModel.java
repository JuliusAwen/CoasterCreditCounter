package de.juliusawen.coastercreditcounter.presentation.visits;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowVisitsFragmentViewModel extends ViewModel
{
    public Park park;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
