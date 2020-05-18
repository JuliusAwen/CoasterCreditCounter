package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public class OLD_GetContentRecyclerViewAdapterRequest
{
    public OLD_ContentRecyclerViewAdapterType OLDContentRecyclerViewAdapterType;

    public List<IElement> elements = new ArrayList<>();

    public Set<Class<? extends IElement>> relevantChildTypesInSortOrder = new HashSet<>();

    public boolean selectMultiple = false;
}
