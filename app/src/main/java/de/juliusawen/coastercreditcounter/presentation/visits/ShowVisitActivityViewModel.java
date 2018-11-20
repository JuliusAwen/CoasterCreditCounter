package de.juliusawen.coastercreditcounter.presentation.visits;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowVisitActivityViewModel extends ViewModel
{
    Visit visit;

    ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    AttractionCategoryHeaderProvider attractionCategoryHeaderProvider;

    @Override
    protected void onCleared()
    {
        this.attractionCategoryHeaderProvider.removeCreatedAttractionCategoryHeaders();
    }
}
