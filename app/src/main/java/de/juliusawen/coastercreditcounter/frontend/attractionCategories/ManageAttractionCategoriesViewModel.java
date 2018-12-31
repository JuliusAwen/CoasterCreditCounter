package de.juliusawen.coastercreditcounter.frontend.attractionCategories;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ManageAttractionCategoriesViewModel extends ViewModel
{
    public AttractionCategory longClickedAttractionCategory;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    public AttractionCategoryHeaderProvider attractionCategoryHeaderProvider;
}
