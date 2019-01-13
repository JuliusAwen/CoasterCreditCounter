package de.juliusawen.coastercreditcounter.frontend.elements;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ManageOrphanElementsViewModel extends ViewModel
{
    String type_to_manage;

    IElement longClickedElement;

    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    AttractionCategoryHeaderProvider attractionCategoryHeaderProvider;
}
