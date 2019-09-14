package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

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
        request.relevantChildTypes = childTypesToExpand;

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
        request.relevantChildTypes = childTypesToExpand;
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
        request.relevantChildTypes = childTypesToExpand;

        return new ContentRecyclerViewAdapter(request);
    }
}
