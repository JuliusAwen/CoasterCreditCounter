package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

class GetContentRecyclerViewAdapterRequest
{
    public AdapterType adapterType;
    public List<Element> elements;
    public List<Element> initiallyExpandedElements;
    public Class<? extends Element> childType;
    public RecyclerOnClickListener.OnClickListener onClickListener;
    public boolean selectMultiple;
}
