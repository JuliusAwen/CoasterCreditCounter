package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.temporary.BottomSpacer;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

abstract class AdapterBaseHandler extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    protected RecyclerView recyclerView;

    protected List<IElement> content;

    private final View.OnClickListener internalOnClickListener;
    private final View.OnLongClickListener internalOnLongClickListener;

    private final Map<Class<? extends IElement>, View.OnClickListener> externalOnClickListenersByType = new HashMap<>();
    private final Map<Class<? extends IElement>, View.OnLongClickListener> externalOnLongClickListenersByType = new HashMap<>();

    AdapterBaseHandler(List<IElement> content, AdapterConfiguration adapterConfiguration)
    {
        this.content = content;
        this.internalOnClickListener = this.getInternalOnClickListener();
        this.internalOnLongClickListener = this.getInternalOnLongClickListener();
        this.externalOnClickListenersByType.putAll(adapterConfiguration.getOnClickListenersByType());
        this.externalOnLongClickListenersByType.putAll(adapterConfiguration.getOnLongClickListenersByType());

        Log.v("instantiated");
    }

    private View.OnClickListener getInternalOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnClick(view, true);
            }
        };
    }

    private View.OnLongClickListener getInternalOnLongClickListener()
    {
        return new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                return handleOnLongClick(view, true);
            }
        };
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount()
    {
        return this.content.size();
    }

    protected IElement bindViewHolderElement(final ContentRecyclerViewAdapter.ViewHolderElement viewHolder, int position)
    {
        IElement element = this.content.get(position);
        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", element, position));

        this.setPadding(0, viewHolder);
        this.setOnClickListeners(viewHolder);

        viewHolder.itemView.setTag(element);
        viewHolder.textViewName.setText(element.getName());
        viewHolder.linearLayout.setVisibility(View.VISIBLE);

        return element;
    }

    protected void setPadding(int generation, ContentRecyclerViewAdapter.ViewHolderElement viewHolder)
    {
        int padding = ConvertTool.convertDpToPx((int)(App.getContext().getResources().getDimension(R.dimen.standard_padding)
                / App.getContext().getResources().getDisplayMetrics().density))
                * generation;

        viewHolder.linearLayout.setPadding(padding, 0, padding, 0);
    }

    private void setOnClickListeners(ContentRecyclerViewAdapter.ViewHolderElement viewHolderElement)
    {
        viewHolderElement.itemView.setOnClickListener(this.internalOnClickListener);
        viewHolderElement.itemView.setOnLongClickListener(this.internalOnLongClickListener);
    }

    protected IElement handleOnClick(View view, boolean performExternalClick)
    {
        IElement element = this.fetchElement(view);

        if(performExternalClick)
        {
            View.OnClickListener externalOnClickListener = this.fetchExternalOnClickListener(element);
            if(externalOnClickListener != null)
            {
                externalOnClickListener.onClick(view);
            }
        }

        return element;
    }

    private View.OnClickListener fetchExternalOnClickListener(IElement element)
    {
        if(this.externalOnClickListenersByType.containsKey(element.getClass()))
        {
            return this.externalOnClickListenersByType.get(element.getClass());
        }
        else
        {
            for(Class<? extends IElement> type : this.externalOnClickListenersByType.keySet())
            {
                if(type.isAssignableFrom(element.getClass()))
                {
                    return this.externalOnClickListenersByType.get(type);
                }
            }
        }

        return null;
    }

    protected boolean handleOnLongClick(View view, boolean performExternalLongClick)
    {
        IElement element = this.fetchElement(view);
        if(performExternalLongClick)
        {
            View.OnLongClickListener externalOnLongClickListener = fetchExternalOnLongClickListener(element);
            if(externalOnLongClickListener != null)
            {
                return externalOnLongClickListener.onLongClick(view);
            }
        }

        return false;
    }

    private View.OnLongClickListener fetchExternalOnLongClickListener(IElement element)
    {
        if(this.externalOnLongClickListenersByType.containsKey(element.getClass()))
        {
            return this.externalOnLongClickListenersByType.get(element.getClass());
        }
        else
        {
            for(Class<? extends IElement> type : this.externalOnLongClickListenersByType.keySet())
            {
                if(type.isAssignableFrom(element.getClass()))
                {
                    return this.externalOnLongClickListenersByType.get(type);
                }
            }
        }

        return null;
    }

    protected IElement fetchElement(View view)
    {
        Object tag = view.getTag();
        if(IElement.class.isAssignableFrom(tag.getClass()))
        {
            return (IElement) tag;
        }

        throw new IllegalArgumentException(String.format("View tag object's type [%s] is not assignable from IElement", tag.getClass().getSimpleName()));
    }

    public void notifyElementInserted(IElement element)
    {
        super.notifyItemInserted(this.content.indexOf(element));
    }

    public void notifyElementChanged(IElement element)
    {
        super.notifyItemChanged(this.content.indexOf(element));
    }

    public void notifyElementRemoved(IElement element)
    {
        super.notifyItemRemoved(this.content.indexOf(element));
    }

    private void swapItems(IElement item1, IElement item2)
    {
        int index1 = this.content.indexOf(item1);
        int index2 = this.content.indexOf(item2);

        Collections.swap(this.content, index1, index2);
        notifyItemMoved(index1, index2);
        this.scrollToElement(item1);
    }

    protected void scrollToElement(IElement element)
    {
        if(element != null && this.content.contains(element) && this.recyclerView != null)
        {
            Log.d(String.format("scrolling to %s", element));
            recyclerView.scrollToPosition(content.indexOf(element));
        }
    }

    protected AdapterBaseHandler addBottomSpacer()
    {
        if(!this.content.isEmpty() && !(this.content.get(this.content.size() - 1) instanceof BottomSpacer))
        {
            this.content.add(new BottomSpacer());
            notifyItemInserted(this.content.size() - 1);
            Log.v("added BottomSpacer");
        }

        return this;
    }
}