package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.temporary.BottomSpacer;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter.interfaces.IContentRecyclerViewAdapter;

abstract class PlainContentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IContentRecyclerViewAdapter
{
    protected RecyclerView recyclerView;

    protected List<IElement> content;
    protected ContentRecyclerViewAdapterConfiguration configuration;

    protected ContentRecyclerViewOnClickListener.CustomItemOnClickListener customItemOnClickListener;

    PlainContentRecyclerViewAdapter(List<IElement> content, ContentRecyclerViewAdapterConfiguration configuration)
    {
        this.content = content;
        this.configuration = configuration;
        this.customItemOnClickListener = configuration.getCustomItemOnClickListener();
        Log.v("instantiated");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView)
    {
        this.recyclerView = null;
        this.content = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount()
    {
        return this.content.size();
    }

    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement element = this.content.get(position);
        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", element, position));

        this.setPadding(0, viewHolder);
        this.setCustomOnClickListeners(viewHolder);
        viewHolder.itemView.setTag(element);
        viewHolder.linearLayoutElement.setVisibility(View.VISIBLE);

        return element;
    }

    protected void setPadding(int generation, ViewHolderElement viewHolder)
    {
        int padding = ConvertTool.convertDpToPx((int)(App.getContext().getResources().getDimension(R.dimen.standard_padding)
                / App.getContext().getResources().getDisplayMetrics().density))
                * generation;

        viewHolder.linearLayoutElement.setPadding(padding, 0, padding, 0);
    }

    private void setCustomOnClickListeners(ViewHolderElement viewHolderElement)
    {
        if(this.customItemOnClickListener != null && !viewHolderElement.itemView.hasOnClickListeners())
        {
            viewHolderElement.itemView.setOnClickListener(new ContentRecyclerViewOnClickListener(this.customItemOnClickListener));
            viewHolderElement.itemView.setOnLongClickListener(new ContentRecyclerViewOnClickListener(this.customItemOnClickListener));
        }
    }

    protected void notifyElementChanged(IElement element)
    {
        super.notifyItemChanged(this.content.indexOf(element));
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

    protected PlainContentRecyclerViewAdapter addBottomSpacer()
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