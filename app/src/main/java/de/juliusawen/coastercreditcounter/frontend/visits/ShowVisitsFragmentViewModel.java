package de.juliusawen.coastercreditcounter.frontend.visits;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ShowVisitsFragmentViewModel extends ViewModel
{
    Park park;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    IElement longClickedElement;
}
