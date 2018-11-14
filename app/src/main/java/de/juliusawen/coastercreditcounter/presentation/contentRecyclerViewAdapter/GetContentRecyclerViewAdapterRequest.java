package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

class GetContentRecyclerViewAdapterRequest
{
    AdapterType adapterType = null;

    List<Element> elements = new ArrayList<>();

    Set<Element> initiallyExpandedElements = new HashSet<>();

    Class<? extends Element> childType = null;

    boolean selectMultiple = false;
}
