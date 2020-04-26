package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

class GetContentRecyclerViewAdapterRequest
{
    ContentRecyclerViewAdapterType contentRecyclerViewAdapterType;

    List<IElement> elements = new ArrayList<>();

    Set<Class<? extends IElement>> relevantChildTypesInSortOrder = new HashSet<>();

    boolean selectMultiple = false;
}
