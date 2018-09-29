package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

public abstract class ContentRecyclerViewAdapterProvider
{
    public static ContentRecyclerViewAdapter getBasicContentRecyclerViewAdapter(
            List<Element> elements)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.BASIC;
        request.elements = elements;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<Element> parentElements,
            Set<Element> initiallyExpandedElements,
            Class<? extends Element> childTypeToExpand)
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
            List<Element> elements,
            boolean selectMultiple)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.SELECTABLE;
        request.elements = elements;
        request.selectMultiple = selectMultiple;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getExpandableSelectableContentRecyclerViewAdapter(
            List<Element> parents,
            Class<? extends Element> childType,
            boolean selectMultiple)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.EXPANDABLE_SELECTABLE;
        request.elements = parents;
        request.childType = childType;
        request.selectMultiple = selectMultiple;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getExpandableCountableContentRecyclerViewAdapter(
            List<Element> parentElements,
            Set<Element> initiallyExpandedElements,
            Class<? extends Element> childTypeToExpand)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.COUNTABLE;
        request.elements = parentElements;
        request.initiallyExpandedElements = initiallyExpandedElements;
        request.childType = childTypeToExpand;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }
}
