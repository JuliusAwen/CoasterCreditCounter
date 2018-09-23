package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

class GetContentRecyclerViewAdapterRequest
{
    public AdapterType adapterType;
    public List<Element> elements = new ArrayList<>();
    public Set<Element> initiallyExpandedElements = new HashSet<>();
    public Class<? extends Element> childType;
    public RecyclerOnClickListener.OnClickListener onClickListener;
    public boolean selectMultiple;
}
