package de.juliusawen.coastercreditcounter.frontend.elements;

import java.util.List;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class PickElementsActivityViewModel extends ViewModel
{
    int requestCode = -1;
    List<IElement> elementsToPickFrom;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    GroupHeader longClickedGroupHeader;
}
