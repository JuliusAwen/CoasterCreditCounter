package de.juliusawen.coastercreditcounter.frontend.elements;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.toolbox.MenuAgent;

public class SortElementsActivityViewModel extends ViewModel
{
    List<IElement> elementsToSort;
    ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    MenuAgent optionsMenuAgent;
}
