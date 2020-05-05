package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public abstract class ContentRecyclerViewAdapterProvider
{
    public static ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<Class<? extends IElement>> childTypesToExpand)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.contentRecyclerViewAdapterType = ContentRecyclerViewAdapterType.EXPANDABLE;
        request.elements = parentElements;
        request.relevantChildTypesInSortOrder = childTypesToExpand;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Class<? extends IElement> childTypeToExpand)
    {
        Set<Class<? extends IElement>> relevantChildTypes = new HashSet<>();
        relevantChildTypes.add(childTypeToExpand);

        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.contentRecyclerViewAdapterType = ContentRecyclerViewAdapterType.EXPANDABLE;
        request.elements = parentElements;
        request.relevantChildTypesInSortOrder = relevantChildTypes;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getSelectableContentRecyclerViewAdapter(
            List<IElement> elements,
            Set<Class<? extends IElement>> childTypesToExpand,
            boolean selectMultiple)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.contentRecyclerViewAdapterType = ContentRecyclerViewAdapterType.SELECTABLE;
        request.elements = elements;
        request.relevantChildTypesInSortOrder = childTypesToExpand;
        request.selectMultiple = selectMultiple;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getSelectableContentRecyclerViewAdapter(
            List<IElement> elements,
            Class<? extends IElement> childTypeToExpand,
            boolean selectMultiple)
    {
        Set<Class<? extends IElement>> relevantChildren = new HashSet<>();
        relevantChildren.add(childTypeToExpand);

        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.contentRecyclerViewAdapterType = ContentRecyclerViewAdapterType.SELECTABLE;
        request.elements = elements;
        request.relevantChildTypesInSortOrder = relevantChildren;
        request.selectMultiple = selectMultiple;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getCountableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<Class<? extends IElement>> childTypesToExpand)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.contentRecyclerViewAdapterType = ContentRecyclerViewAdapterType.COUNTABLE;
        request.elements = parentElements;
        request.relevantChildTypesInSortOrder = childTypesToExpand;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getCountableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Class<? extends IElement> childTypeToExpand)
    {
        Set<Class<? extends IElement>> relevantChildren = new HashSet<>();
        relevantChildren.add(childTypeToExpand);

        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.contentRecyclerViewAdapterType = ContentRecyclerViewAdapterType.COUNTABLE;
        request.elements = parentElements;
        request.relevantChildTypesInSortOrder = relevantChildren;

        return new ContentRecyclerViewAdapter(request);
    }
}
