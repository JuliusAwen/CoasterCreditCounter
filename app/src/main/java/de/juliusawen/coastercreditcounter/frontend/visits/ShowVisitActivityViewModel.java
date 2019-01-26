package de.juliusawen.coastercreditcounter.frontend.visits;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ShowVisitActivityViewModel extends ViewModel
{
    Visit visit;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
