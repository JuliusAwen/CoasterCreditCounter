package de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

class GetContentRecyclerViewAdapterRequest
{
    AdapterType adapterType = AdapterType.EXPANDABLE;

    List<IElement> elements = new ArrayList<>();

    Set<IElement> initiallyExpandedElements = new HashSet<>();

    Set<Class<? extends IElement>> relevantChildTypes = new HashSet<>();

    boolean selectMultiple = false;
}
