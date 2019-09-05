package de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.GroupHeader.GroupHeaderProvider;

class GetContentRecyclerViewAdapterRequest
{
    ContentRecyclerViewAdapter.AdapterType adapterType;

    List<IElement> elements = new ArrayList<>();

    Set<Class<? extends IElement>> relevantChildTypes = new HashSet<>();

    boolean selectMultiple = false;

    GroupHeaderProvider.GroupType groupType = GroupHeaderProvider.GroupType.NONE;
}
