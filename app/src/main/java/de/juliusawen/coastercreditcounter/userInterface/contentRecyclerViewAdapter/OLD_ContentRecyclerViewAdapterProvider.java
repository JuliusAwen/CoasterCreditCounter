package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;

public abstract class OLD_ContentRecyclerViewAdapterProvider
{
    public static OLD_ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<Class<? extends IElement>> childTypesToExpand)
    {
        OLD_GetContentRecyclerViewAdapterRequest request = new OLD_GetContentRecyclerViewAdapterRequest();
        request.OLDContentRecyclerViewAdapterType = OLD_ContentRecyclerViewAdapterType.EXPANDABLE;
        request.elements = parentElements;
        request.relevantChildTypesInSortOrder = childTypesToExpand;

        return new OLD_ContentRecyclerViewAdapter(request);
    }

    public static OLD_ContentRecyclerViewAdapter getExpandableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Class<? extends IElement> childTypeToExpand)
    {
        Set<Class<? extends IElement>> relevantChildTypes = new HashSet<>();
        relevantChildTypes.add(childTypeToExpand);

        OLD_GetContentRecyclerViewAdapterRequest request = new OLD_GetContentRecyclerViewAdapterRequest();
        request.OLDContentRecyclerViewAdapterType = OLD_ContentRecyclerViewAdapterType.EXPANDABLE;
        request.elements = parentElements;
        request.relevantChildTypesInSortOrder = relevantChildTypes;

        return new OLD_ContentRecyclerViewAdapter(request);
    }

    public static OLD_ContentRecyclerViewAdapter getSelectableContentRecyclerViewAdapter(
            List<IElement> elements,
            Set<Class<? extends IElement>> childTypesToExpand,
            boolean selectMultiple)
    {
        OLD_GetContentRecyclerViewAdapterRequest request = new OLD_GetContentRecyclerViewAdapterRequest();
        request.OLDContentRecyclerViewAdapterType = OLD_ContentRecyclerViewAdapterType.SELECTABLE;
        request.elements = elements;
        request.relevantChildTypesInSortOrder = childTypesToExpand;
        request.selectMultiple = selectMultiple;

        return new OLD_ContentRecyclerViewAdapter(request);
    }

    public static OLD_ContentRecyclerViewAdapter getSelectableContentRecyclerViewAdapter(
            List<IElement> elements,
            Class<? extends IElement> childTypeToExpand,
            boolean selectMultiple)
    {
        Set<Class<? extends IElement>> relevantChildren = new HashSet<>();
        relevantChildren.add(childTypeToExpand);

        OLD_GetContentRecyclerViewAdapterRequest request = new OLD_GetContentRecyclerViewAdapterRequest();
        request.OLDContentRecyclerViewAdapterType = OLD_ContentRecyclerViewAdapterType.SELECTABLE;
        request.elements = elements;
        request.relevantChildTypesInSortOrder = relevantChildren;
        request.selectMultiple = selectMultiple;

        return new OLD_ContentRecyclerViewAdapter(request);
    }

    public static OLD_ContentRecyclerViewAdapter getCountableContentRecyclerViewAdapter(
            List<IElement> parentElements,
            Set<Class<? extends IElement>> childTypesToExpand)
    {
        OLD_GetContentRecyclerViewAdapterRequest request = new OLD_GetContentRecyclerViewAdapterRequest();
        request.OLDContentRecyclerViewAdapterType = OLD_ContentRecyclerViewAdapterType.COUNTABLE;
        request.elements = parentElements;
        request.relevantChildTypesInSortOrder = childTypesToExpand;

        return new OLD_ContentRecyclerViewAdapter(request);
    }

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
