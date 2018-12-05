package de.juliusawen.coastercreditcounter.presentation.attractions;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ShowAttractionsFragmentViewModel extends ViewModel
{
    Park park;

    ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    AttractionCategoryHeaderProvider attractionCategoryHeaderProvider;

//    @Override
//    protected void onCleared()
//    {
//        this.attractionCategoryHeaderProvider.removeCreatedAttractionCategoryHeadersFromContent();
//    }
}
