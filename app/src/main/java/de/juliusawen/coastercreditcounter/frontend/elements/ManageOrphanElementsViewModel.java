package de.juliusawen.coastercreditcounter.frontend.elements;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ManageOrphanElementsViewModel extends ViewModel
{
    AttractionCategory longClickedAttractionCategory;

    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    AttractionCategoryHeaderProvider attractionCategoryHeaderProvider;
}
