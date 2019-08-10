package de.juliusawen.coastercreditcounter.frontend.elements;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

class PickElementsActivityViewModel extends ViewModel
{
    int requestCode = -1;
    boolean isSimplePick;
    List<IElement> elementsToPickFrom;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
