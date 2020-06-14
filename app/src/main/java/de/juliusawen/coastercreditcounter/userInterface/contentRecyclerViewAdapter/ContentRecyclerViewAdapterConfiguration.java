package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class ContentRecyclerViewAdapterConfiguration
{
    private ContentRecyclerViewDecoration contentRecyclerViewDecoration;

    private final Map<ElementType, View.OnClickListener> onClickListenersByElementType = new HashMap<>();
    private final Map<ElementType, View.OnLongClickListener> onLongClickListenersByElementType = new HashMap<>();

    private final Map<OnClickListenerType, View.OnClickListener> onClickListenersByOnClickListenerType = new HashMap<>();

    // ElementTypes that are either expanded/collapsed or selected when parent is clicked
    private final LinkedHashSet<ElementType> relevantChildTypes = new LinkedHashSet<>();

    private boolean isSelecetable = false;
    private boolean isMultipleSelection = false;

    private boolean useBottomSpacer = false;

    public ContentRecyclerViewAdapterConfiguration(ContentRecyclerViewDecoration contentRecyclerViewDecoration)
    {
        this.contentRecyclerViewDecoration = contentRecyclerViewDecoration;
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    public ContentRecyclerViewDecoration getDecoration()
    {
        return this.contentRecyclerViewDecoration;
    }

    Map<ElementType, View.OnClickListener> getOnClickListenersByElementType()
    {
        return this.onClickListenersByElementType;
    }

    Map<ElementType, View.OnLongClickListener> getOnLongClickListenersByElementType()
    {
        return this.onLongClickListenersByElementType;
    }

    public ContentRecyclerViewAdapterConfiguration addOnElementTypeClickListener(ElementType elementType, View.OnClickListener onClickListener)
    {
        this.onClickListenersByElementType.put(elementType, onClickListener);
        Log.d(String.format("%s", elementType));
        return this;
    }

    public ContentRecyclerViewAdapterConfiguration addOnElementTypeLongClickListener(ElementType elementType, View.OnLongClickListener onLongClickListener)
    {
        this.onLongClickListenersByElementType.put(elementType, onLongClickListener);
        Log.d(String.format("%s", elementType));
        return this;
    }

    Map<OnClickListenerType, View.OnClickListener> getOnClickListenersByOnClickListenerType()
    {
        return this.onClickListenersByOnClickListenerType;
    }

    public ContentRecyclerViewAdapterConfiguration addOnClickListener(OnClickListenerType onClickListenerType, View.OnClickListener onClickListener)
    {
        this.onClickListenersByOnClickListenerType.put(onClickListenerType, onClickListener);
        Log.d(String.format("%s", onClickListenerType));
        return this;
    }

    LinkedHashSet<ElementType> getRelevantChildTypes()
    {
        return this.relevantChildTypes;
    }

    public void addRelevantChildTypes(LinkedHashSet<ElementType> relevantChildTypes)
    {
        this.relevantChildTypes.addAll(relevantChildTypes);

        StringBuilder relevantChildTypesString = new StringBuilder();
        for(ElementType elementType : relevantChildTypes)
        {
            relevantChildTypesString.append(String.format(" %s", elementType));
        }
        Log.v(String.format(Locale.getDefault(), "added [%d] relevant child types:%s", relevantChildTypes.size(), relevantChildTypesString));
    }

    public void addRelevantChildType(ElementType relevantChildType)
    {
        this.relevantChildTypes.add(relevantChildType);
        Log.v(String.format("added %s as relevant child type", relevantChildType));
    }

    public boolean isSelecetable()
    {
        return this.isSelecetable;
    }

    public void setSelectable(boolean isSelecetable)
    {
        this.isSelecetable = isSelecetable;
        Log.d(String.format("isSelectable[%S]", isSelecetable));
    }

    public boolean isMultipleSelection()
    {
        return this.isMultipleSelection;
    }

    public void setMultipleSelection(boolean isMultipleSelection)
    {
        this.isMultipleSelection = isMultipleSelection;
        Log.d(String.format("isMultipleSelection[%S]", isMultipleSelection));
    }

    public boolean useBottomSpacer()
    {
        return this.useBottomSpacer;
    }

    public void setBottomSpacer(boolean useBottomSpacer)
    {
        this.useBottomSpacer = useBottomSpacer;
        Log.d(String.format("useBottomSpacer[%S]", useBottomSpacer));
    }

    @Override
    public String toString()
    {
        String indent = "        ";
        StringBuilder childTypesString = new StringBuilder();
        for(ElementType elementType : this.getRelevantChildTypes())
        {
            childTypesString.append(String.format("\n%s%s", indent, elementType));
        }

        StringBuilder onElementTypeClickListenersString = new StringBuilder();
        for(ElementType elementType : this.onClickListenersByElementType.keySet())
        {
            onElementTypeClickListenersString.append(String.format("\n%s%s", indent, elementType));
        }

        StringBuilder onElementTypeLongClickListenersString = new StringBuilder();
        for(ElementType elementType : this.onLongClickListenersByElementType.keySet())
        {
            onElementTypeLongClickListenersString.append(String.format("\n%s%s", indent, elementType));
        }

        StringBuilder onClickListenersString = new StringBuilder();
        for(OnClickListenerType onClickListenerType : this.onClickListenersByOnClickListenerType.keySet())
        {
            onClickListenersString.append(String.format("\n%s%s", indent, onClickListenerType));
        }

        return String.format(Locale.getDefault(),
                "ContentRecyclerViewConfiguration:\n" +
                    "    isSelectable[%S], isMultipleSelection[%S]\n" +
                    "    [%d] relevant child type(s)%s\n"+
                    "    [%d] OnElementTypeClickListener(s)%s\n"+
                    "    [%d] OnElementTypeLongClickListener(s)%s\n" +
                    "    [%d] OnClickListener(s)%s\n",

                this.isSelecetable, this.isMultipleSelection,
                this.relevantChildTypes.size(), childTypesString,
                this.onClickListenersByElementType.size(), onElementTypeClickListenersString,
                this.onLongClickListenersByElementType.size(), onElementTypeLongClickListenersString,
                this.onClickListenersByOnClickListenerType.size(), onClickListenersString
        );
    }
}

