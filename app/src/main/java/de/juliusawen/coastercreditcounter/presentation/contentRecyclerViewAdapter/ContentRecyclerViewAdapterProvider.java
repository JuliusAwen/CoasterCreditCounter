package de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter;

import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

public abstract class ContentRecyclerViewAdapterProvider
{
    public static ContentRecyclerViewAdapter getBasicContentRecyclerViewAdapter(
            List<Element> elements,
            RecyclerOnClickListener.OnClickListener onClickListener)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.BASIC;
        request.elements = elements;
        request.onClickListener = onClickListener;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<Element> parentElements,
            Class<? extends Element> childTypeToExtend,
            RecyclerOnClickListener.OnClickListener onChildClickListener)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.EXPANDABLE;
        request.elements = parentElements;
        request.childType = childTypeToExtend;
        request.onClickListener = onChildClickListener;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<Element> parents,
            List<Element> initiallyExtendedParents,
            Class<? extends Element> childTypeToExtend,
            RecyclerOnClickListener.OnClickListener onChildClickListener)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.EXPANDABLE;
        request.elements = parents;
        request.initiallyExpandedElements = initiallyExtendedParents;
        request.childType = childTypeToExtend;
        request.onClickListener = onChildClickListener;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getSelectableContentRecyclerViewAdapter(
            List<Element> elements,
            RecyclerOnClickListener.OnClickListener onClickListener,
            boolean selectMultiple)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.SELECTABLE;
        request.elements = elements;
        request.onClickListener = onClickListener;
        request.selectMultiple = selectMultiple;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getExpandableSelectableContentRecyclerViewAdapter(
            List<Element> parents,
            Class<? extends Element> childType,
            RecyclerOnClickListener.OnClickListener onChildClickListener,
            boolean selectMultiple)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.EXPANDABLE_SELECTABLE;
        request.elements = parents;
        request.childType = childType;
        request.onClickListener = onChildClickListener;
        request.selectMultiple = selectMultiple;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getExpandableSelectableContentRecyclerViewAdapter(
            List<Element> parents,
            List<Element> initiallyExpandedParents,
            Class<? extends Element> childType,
            RecyclerOnClickListener.OnClickListener onChildClickListener,
            boolean selectMultiple)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.EXPANDABLE_SELECTABLE;
        request.elements = parents;
        request.initiallyExpandedElements = initiallyExpandedParents;
        request.childType = childType;
        request.onClickListener = onChildClickListener;
        request.selectMultiple = selectMultiple;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }

    public static ContentRecyclerViewAdapter getExpandableCountableContentRecyclerViewAdapter(
            List<Element> parentElements,
            RecyclerOnClickListener.OnClickListener onChildClickListener)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.COUNTABLE;
        request.elements = parentElements;
        request.onClickListener = onChildClickListener;

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = new ContentRecyclerViewAdapter(request);
        contentRecyclerViewAdapter.setHasStableIds(true);
        return contentRecyclerViewAdapter;
    }
}
