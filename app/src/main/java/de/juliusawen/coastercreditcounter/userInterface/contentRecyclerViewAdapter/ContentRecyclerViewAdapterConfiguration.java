package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class ContentRecyclerViewAdapterConfiguration
{
    private final Map<Class<? extends IElement>, View.OnClickListener> onClickListenersByType = new HashMap<>();
    private final Map<Class<? extends IElement>, View.OnLongClickListener> onLongClickListenersByType = new HashMap<>();

    private ContentRecyclerViewDecoration contentRecyclerViewDecoration;

    private final LinkedHashSet<Class<? extends IElement>> relevantChildTypes = new LinkedHashSet<>();

    private boolean isSelecetable = false;
    private boolean isMultipleSelection = false;

    private boolean useBottomSpacer = false;

    public ContentRecyclerViewAdapterConfiguration(ContentRecyclerViewDecoration contentRecyclerViewDecoration)
    {
        this.contentRecyclerViewDecoration = contentRecyclerViewDecoration;
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    Map<Class<? extends IElement>, View.OnClickListener> getOnClickListenersByType()
    {
        return this.onClickListenersByType;
    }

    Map<Class<? extends IElement>, View.OnLongClickListener> getOnLongClickListenersByType()
    {
        return this.onLongClickListenersByType;
    }

    public ContentRecyclerViewAdapterConfiguration addOnClickListenerByType(Class<? extends IElement> type, View.OnClickListener onClickListener)
    {
        this.onClickListenersByType.put(type, onClickListener);
        Log.v(String.format("for [%s]", type.getSimpleName()));
        return this;
    }

    public ContentRecyclerViewAdapterConfiguration addOnLongClickListenerByType(Class<? extends IElement> type, View.OnLongClickListener onLongClickListener)
    {
        this.onLongClickListenersByType.put(type, onLongClickListener);
        Log.v(String.format("for [%s]", type.getSimpleName()));
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

    LinkedHashSet<Class<? extends IElement>> getRelevantChildTypes()
    {
        return this.relevantChildTypes;
    }

    public void addRelevantChildTypes(LinkedHashSet<Class<? extends IElement>> relevantChildTypes)
    {
        this.relevantChildTypes.addAll(relevantChildTypes);

        StringBuilder relevantChildTypesString = new StringBuilder();
        for(Class<? extends IElement> type : relevantChildTypes)
        {
            relevantChildTypesString.append(String.format(" [%s]", type.getSimpleName()));
        }

        Log.v(String.format(Locale.getDefault(), "added [%d] relevant child types:%s", relevantChildTypes.size(), relevantChildTypesString));
    }

    public void addRelevantChildType(Class<? extends IElement> relevantChildType)
    {
        this.relevantChildTypes.add(relevantChildType);
        Log.v(String.format("added [%s] as relevant child type", relevantChildType));
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
        for(Class<? extends IElement> type : this.getRelevantChildTypes())
        {
            childTypesString.append(String.format("        [%s]\n", type.getSimpleName()));
        }

        StringBuilder onClickListenerTypesString = new StringBuilder();
        for(Class<? extends IElement> type : this.onClickListenersByType.keySet())
        {
            onClickListenerTypesString.append(String.format("        [%s]\n", type.getSimpleName()));
        }

        StringBuilder onLongClickListenerTypesString = new StringBuilder();
        for(Class<? extends IElement> type : this.onLongClickListenersByType.keySet())
        {
            onLongClickListenerTypesString.append(String.format("        [%s]\n", type.getSimpleName()));
        }

        return String.format(Locale.getDefault(),
                        "ContentRecyclerViewConfiguration:\n" +
                        "    isSelectable[%S], isMultipleSelection[%S]\n" +
                        "    [%d] relevant child types\n%s"+
                        "    [%d] types with OnClickListeners\n%s"+
                        "    [%d] types with OnLongClickListeners\n%s",

                this.isSelecetable, this.isMultipleSelection,
                this.relevantChildTypes.size(), childTypesString,
                this.onClickListenersByType.size(), onClickListenerTypesString,
                this.onLongClickListenersByType.size(), onLongClickListenerTypesString
        );
    }
}

