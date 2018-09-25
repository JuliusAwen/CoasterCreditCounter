package de.juliusawen.coastercreditcounter.presentation.fragments.parks;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowAttractionsFragmentViewModel extends ViewModel
{
    public Element element;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;
}
