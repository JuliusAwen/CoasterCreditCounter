package de.juliusawen.coastercreditcounter.presentation.orphanElements;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowAttractionCategoriesViewModel extends ViewModel
{
    public AttractionCategory longClickedAttractionCategory;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
