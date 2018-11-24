package de.juliusawen.coastercreditcounter.presentation.orphanElements;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ManageAttractionCategoriesViewModel extends ViewModel
{
    AttractionCategory longClickedAttractionCategory;

    ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    AttractionCategoryHeaderProvider attractionCategoryHeaderProvider;

    @Override
    protected void onCleared()
    {
        this.attractionCategoryHeaderProvider.removeCreatedAttractionCategoryHeadersFromContent();
    }
}
