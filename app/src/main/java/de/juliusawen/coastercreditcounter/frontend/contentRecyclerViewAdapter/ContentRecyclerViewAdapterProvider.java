package de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter;

import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.globals.enums.AdapterType;

public abstract class ContentRecyclerViewAdapterProvider
{
    public static ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<Class<? extends IElement>> childTypesToExpand,
            int groupByType)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.EXPANDABLE;
        request.elements = parentElements;
        request.relevantChildTypes = childTypesToExpand;
        request.groupType = groupByType;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getSelectableContentRecyclerViewAdapter(
            List<IElement> elements,
            Set<Class<? extends IElement>> childTypesToExpand,
            boolean selectMultiple,
            int groupByType)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.SELECTABLE;
        request.elements = elements;
        request.relevantChildTypes = childTypesToExpand;
        request.selectMultiple = selectMultiple;
        request.groupType = groupByType;

        return new ContentRecyclerViewAdapter(request);
    }

    public static ContentRecyclerViewAdapter getCountableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<Class<? extends IElement>> childTypesToExpand,
            int groupByType)
    {
        GetContentRecyclerViewAdapterRequest request = new GetContentRecyclerViewAdapterRequest();
        request.adapterType = AdapterType.COUNTABLE;
        request.elements = parentElements;
        request.relevantChildTypes = childTypesToExpand;
        request.groupType = groupByType;

        return new ContentRecyclerViewAdapter(request);
    }
}
