package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public abstract class OLD_ContentRecyclerViewAdapterProvider
{
    public static OLD_ContentRecyclerViewAdapter getCountableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Class<? extends IElement> childTypeToExpand)
    {
        Set<Class<? extends IElement>> relevantChildren = new HashSet<>();
        relevantChildren.add(childTypeToExpand);

        OLD_GetContentRecyclerViewAdapterRequest request = new OLD_GetContentRecyclerViewAdapterRequest();
        request.OLDContentRecyclerViewAdapterType = OLD_ContentRecyclerViewAdapterType.COUNTABLE;
        request.elements = parentElements;
        request.relevantChildTypesInSortOrder = relevantChildren;

        return new OLD_ContentRecyclerViewAdapter(request);
    }
}
