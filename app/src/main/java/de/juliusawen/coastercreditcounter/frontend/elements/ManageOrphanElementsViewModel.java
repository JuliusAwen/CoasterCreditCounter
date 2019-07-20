package de.juliusawen.coastercreditcounter.frontend.elements;

import androidx.lifecycle.ViewModel;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class ManageOrphanElementsViewModel extends ViewModel
{
    int typeToManage;
    IElement longClickedElement;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
