package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

public abstract class ContentRecyclerViewAdapterProvider
{
    public static ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<IElement> initiallyExpandedElements,
            Class<? extends IElement> childTypeToExpand)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.EXPANDABLE;
        request.elements = parentElements;
        request.initiallyExpandedElements = initiallyExpandedElements;
        request.childType = childTypeToExpand;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getSelectableContentRecyclerViewAdapter(
            List<IElement> elements,
            Class<? extends IElement> childType,
            boolean selectMultiple)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.SELECTABLE;
        request.elements = elements;
        request.childType = childType;
        request.selectMultiple = selectMultiple;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getCountableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Class<? extends IElement> childTypeToExpand)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.COUNTABLE;
        request.elements = parentElements;
        request.childType = childTypeToExpand;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }
}
