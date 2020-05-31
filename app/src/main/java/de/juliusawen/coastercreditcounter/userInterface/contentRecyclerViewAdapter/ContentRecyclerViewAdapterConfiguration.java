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
    private final Map<ElementType, View.OnClickListener> onClickListenersByElementType = new HashMap<>();
    private final Map<ElementType, View.OnLongClickListener> onLongClickListenersByElementType = new HashMap<>();

    private ContentRecyclerViewDecoration contentRecyclerViewDecoration;

    private final LinkedHashSet<ElementType> relevantChildTypes = new LinkedHashSet<>();

    private boolean isSelecetable = false;
    private boolean isMultipleSelection = false;

    private boolean useBottomSpacer = false;

    public ContentRecyclerViewAdapterConfiguration(ContentRecyclerViewDecoration contentRecyclerViewDecoration)
    {
        this.contentRecyclerViewDecoration = contentRecyclerViewDecoration;
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
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
        Log.v(String.format("%s", elementType));
        return this;
    }

    public ContentRecyclerViewAdapterConfiguration addOnElementTypeLongClickListener(ElementType elementType, View.OnLongClickListener onLongClickListener)
    {
        this.onLongClickListenersByElementType.put(elementType, onLongClickListener);
        Log.v(String.format("%s", elementType));
        return this;
    }

    public ContentRecyclerViewDecoration getDecoration()
    {
        return this.contentRecyclerViewDecoration;
    }

    public void setContentRecyclerViewDecoration(ContentRecyclerViewDecoration contentRecyclerViewDecoration)
    {
        this.contentRecyclerViewDecoration = contentRecyclerViewDecoration;
    }

    public boolean isSelecetable()
    {
        return this.isSelecetable;
    }

    public void setSelectable(boolean isSelecetable)
    {
        this.isSelecetable = isSelecetable;
    }

    public boolean isMultipleSelection()
    {
        return this.isMultipleSelection;
    }

    public void setMultipleSelection(boolean isMultipleSelection)
    {
        this.isMultipleSelection = isMultipleSelection;
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

    public boolean useBottomSpacer()
    {
        return this.useBottomSpacer;
    }

    public void setBottomSpacer(boolean useBottomSpacer)
    {
        this.useBottomSpacer = useBottomSpacer;
    }

    @Override
    public String toString()
    {
        StringBuilder childTypesString = new StringBuilder();
        for(ElementType elementType : this.getRelevantChildTypes())
        {
            childTypesString.append(String.format("        %s\n", elementType));
        }

        StringBuilder onClickListenerTypesString = new StringBuilder();
        for(ElementType elementType : this.onClickListenersByElementType.keySet())
        {
            onClickListenerTypesString.append(String.format("        %s\n", elementType));
        }

        StringBuilder onLongClickListenerTypesString = new StringBuilder();
        for(ElementType elementType : this.onLongClickListenersByElementType.keySet())
        {
            onLongClickListenerTypesString.append(String.format("        %s\n", elementType));
        }

        return String.format(Locale.getDefault(),
                        "ContentRecyclerViewConfiguration:\n" +
                        "    isSelectable[%S], isMultipleSelection[%S]\n" +
                        "    [%d] relevant child types\n%s"+
                        "    [%d] types with OnClickListeners\n%s"+
                        "    [%d] types with OnLongClickListeners\n%s",

                this.isSelecetable, this.isMultipleSelection,
                this.relevantChildTypes.size(), childTypesString,
                this.onClickListenersByElementType.size(), onClickListenerTypesString,
                this.onLongClickListenersByElementType.size(), onLongClickListenerTypesString
        );
    }
}

