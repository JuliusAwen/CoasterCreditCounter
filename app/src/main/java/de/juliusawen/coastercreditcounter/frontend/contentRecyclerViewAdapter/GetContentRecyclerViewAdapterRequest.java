package de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

class GetContentRecyclerViewAdapterRequest
{
    AdapterType adapterType = null;

    List<IElement> elements = new ArrayList<>();

    Set<IElement> initiallyExpandedElements = new HashSet<>();

    Class<? extends IElement> childType = null;

    boolean selectMultiple = false;
}
