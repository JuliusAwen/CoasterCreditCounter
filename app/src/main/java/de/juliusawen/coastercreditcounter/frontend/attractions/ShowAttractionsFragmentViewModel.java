package de.juliusawen.coastercreditcounter.frontend.attractions;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowAttractionsFragmentViewModel extends ViewModel
{
    Park park;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
}
