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

    private View.OnClickListener onIncreaseRideCountClickListener;
    private View.OnClickListener onDecreaseRideCountClickListener;

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

    public View.OnClickListener getOnIncreaseRideCountClickListener()
    {
        return this.onIncreaseRideCountClickListener;
    }

    public ContentRecyclerViewAdapterConfiguration setOnIncreaseRideCountClickListener(View.OnClickListener onIncreaseRideCountClickListener)
    {
        this.onIncreaseRideCountClickListener = onIncreaseRideCountClickListener;
        return this;
    }

    public View.OnClickListener getOnDecreaseRideCountClickListener()
    {
        return this.onDecreaseRideCountClickListener;
    }

    public ContentRecyclerViewAdapterConfiguration setOnDecreaseRideCountClickListener(View.OnClickListener onDecreaseRideCountClickListener)
    {
        this.onDecreaseRideCountClickListener = onDecreaseRideCountClickListener;
        return this;
    }

    LinkedHashSet<ElementType> getRelevantChildTypes()
    {
        return this.relevantChildTypes;
    }

    public void addRelevantChildTypes(LinkedHashSet<ElementType> relevantChildTypes)
    {
        this.relevantChildTypes.addAll(relevantChildTypes);
        Log.v(String.format(Locale.getDefault(), "added [%d] relevant child types", relevantChildTypes.size()));
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
        return String.format(Locale.getDefault(),
                "ContentRecyclerViewConfiguration:\n" +
                "    isSelectable[%S], isMultipleSelection[%S]\n" +
                "    [%d] relevant child type(s)\n"+
                "    [%d] OnElementTypeClickListener(s)\n"+
                "    [%d] OnElementTypeLongClickListener(s)\n" +
                "    OnIncreaseRideCountClickListener added [%S], OnDecreaseRideCountClickListener added [%S]" +

                this.isSelecetable, this.isMultipleSelection,
                this.relevantChildTypes.size(),
                this.onClickListenersByElementType.size(),
                this.onLongClickListenersByElementType.size(),
                this.onIncreaseRideCountClickListener != null, this.onDecreaseRideCountClickListener != null
        );
    }
}

