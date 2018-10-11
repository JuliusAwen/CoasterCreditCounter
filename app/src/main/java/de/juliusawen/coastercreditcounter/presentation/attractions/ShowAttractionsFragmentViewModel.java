package de.juliusawen.coastercreditcounter.presentation.attractions;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowAttractionsFragmentViewModel extends ViewModel
{
    public Park park;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
