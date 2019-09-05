package de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter;

import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.GroupHeader.GroupHeaderProvider;

public abstract class ContentRecyclerViewAdapterProvider
{
    public static ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<Class<? extends IElement>> childTypesToExpand,
            GroupHeaderProvider.GroupType groupType)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = ContentRecyclerViewAdapter.AdapterType.EXPANDABLE;
        request.elements = parentElements;
        request.relevantChildTypes = childTypesToExpand;
        request.groupType = groupType;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getSelectableContentRecyclerViewAdapter(
            List<IElement> elements,
            Set<Class<? extends IElement>> childTypesToExpand,
            boolean selectMultiple,
            GroupHeaderProvider.GroupType groupType)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = ContentRecyclerViewAdapter.AdapterType.SELECTABLE;
        request.elements = elements;
        request.relevantChildTypes = childTypesToExpand;
        request.selectMultiple = selectMultiple;
        request.groupType = groupType;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getCountableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<Class<? extends IElement>> childTypesToExpand,
            GroupHeaderProvider.GroupType groupType)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = ContentRecyclerViewAdapter.AdapterType.COUNTABLE;
        request.elements = parentElements;
        request.relevantChildTypes = childTypesToExpand;
        request.groupType = groupType;

        return new ContentRecyclerViewAdapter(request);
    }
}
