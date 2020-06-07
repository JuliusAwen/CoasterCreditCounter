package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.dataModel.elements.temporary.BottomSpacer;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterPlainHandler extends AdapterContentHandler
{
    private boolean formatAsPrettyPrint = false;

    private final View.OnClickListener internalOnClickListener;
    private final View.OnLongClickListener internalOnLongClickListener;

    AdapterPlainHandler(ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(configuration);
        this.internalOnClickListener = this.getInternalOnClickListener();
        this.internalOnLongClickListener = this.getInternalOnLongClickListener();
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    @Override
    protected void setContent(List<IElement> content)
    {
        super.setContent(content);

        if(super.useBottomSpacer())
        {
            this.addBottomSpacer();
        }
    }

    @Override
    protected void groupContent(GroupType groupType)
    {
        super.groupContent(groupType);

        if(super.useBottomSpacer())
        {
            this.addBottomSpacer();
        }
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

    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement element = super.getItem(position);
        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", element, position));

        this.setPadding(0, viewHolder);

        viewHolder.itemView.setTag(element);
        viewHolder.itemView.setOnClickListener(this.internalOnClickListener);
        viewHolder.itemView.setOnLongClickListener(this.internalOnLongClickListener);

        viewHolder.imageViewExpandToggle.setTag(element);
        viewHolder.imageViewExpandToggle.setOnClickListener(this.internalOnClickListener);
        viewHolder.imageViewExpandToggle.setOnLongClickListener(this.internalOnLongClickListener);

        viewHolder.textViewName.setText(element.getName());
        viewHolder.linearLayout.setVisibility(View.VISIBLE);

        return element;
    }

    protected void setPadding(int generation, ViewHolderElement viewHolder)
    {
        int padding = ConvertTool.convertDpToPx((int)(App.getContext().getResources().getDimension(R.dimen.standard_padding)
                / App.getContext().getResources().getDisplayMetrics().density))
                * generation;

        viewHolder.linearLayout.setPadding(padding, 0, padding, 0);
    }

    protected VisitedAttraction bindViewHolderVisitedAttraction(ViewHolderVisitedAttraction viewHolder, int position)
    {
        VisitedAttraction visitedAttraction = (VisitedAttraction) super.getItem(position);
        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]", visitedAttraction, position));

        if(!this.formatAsPrettyPrint())
        {
            viewHolder.textViewPrettyPrint.setVisibility(View.GONE);
            viewHolder.linearLayoutEditable.setVisibility(View.VISIBLE);

            viewHolder.linearLayoutCounter.setTag(visitedAttraction);
            viewHolder.linearLayoutCounter.setOnClickListener(this.internalOnClickListener);
            viewHolder.linearLayoutCounter.setOnLongClickListener(this.internalOnLongClickListener);

            viewHolder.textViewName.setText(visitedAttraction.getName());
            viewHolder.textViewCount.setText(String.valueOf(visitedAttraction.fetchTotalRideCount()));

            viewHolder.imageViewIncrease.setTag(visitedAttraction);
            viewHolder.imageViewIncrease.setOnClickListener(super.getOnIncreaseRideCountClickListener());

            viewHolder.imageViewDecrease.setTag(visitedAttraction);
            viewHolder.imageViewDecrease.setOnClickListener(super.getOnDecreaseRideCountClickListener());
        }
        else
        {
            viewHolder.linearLayoutEditable.setVisibility(View.GONE);


            viewHolder.textViewPrettyPrint.setText(
                    App.getContext().getString(R.string.text_visited_attraction_pretty_print, visitedAttraction.fetchTotalRideCount(), visitedAttraction.getName()));
            viewHolder.textViewPrettyPrint.setVisibility(View.VISIBLE);
        }

        return visitedAttraction;
    }

    protected boolean handleOnClick(View view, boolean performExternalClick)
    {
        if(super.hasExternalOnClickListeners() && performExternalClick)
        {
            IElement element = this.fetchItem(view);
            View.OnClickListener externalOnClickListener = this.fetchExternalOnClickListener(element);
            if(externalOnClickListener != null)
            {
                externalOnClickListener.onClick(view);
            }
        }

        return false;
    }

    private View.OnClickListener fetchExternalOnClickListener(IElement item)
    {
        if(super.getExternalOnClickListenersByType().containsKey(item.getClass()))
        {
            return super.getExternalOnClickListenersByType().get(item.getClass());
        }
        else
        {
            for(ElementType elementType : super.getExternalOnClickListenersByType().keySet())
            {
                if(elementType.getType().isAssignableFrom(item.getClass()))
                {
                    return super.getExternalOnClickListenersByType().get(elementType);
                }
            }
        }

        return null;
    }

    protected boolean handleOnLongClick(View view, boolean performExternalLongClick)
    {
        if(performExternalLongClick && super.hasExternalOnClickListeners())
        {
            IElement item = this.fetchItem(view);
            View.OnLongClickListener externalOnLongClickListener = fetchExternalOnLongClickListener(item);
            if(externalOnLongClickListener != null)
            {
                return externalOnLongClickListener.onLongClick(view);
            }
        }

        return false;
    }

    private View.OnLongClickListener fetchExternalOnLongClickListener(IElement item)
    {
        if(super.getExternalOnLongClickListenersByType().containsKey(item.getClass()))
        {
            return super.getExternalOnLongClickListenersByType().get(item.getClass());
        }
        else
        {
            for(ElementType elementType : super.getExternalOnLongClickListenersByType().keySet())
            {
                if(elementType.getType().isAssignableFrom(item.getClass()))
                {
                    return super.getExternalOnLongClickListenersByType().get(elementType);
                }
            }
        }

        return null;
    }

    protected IElement fetchItem(View view)
    {
        Object tag = view.getTag();
        if(IElement.class.isAssignableFrom(tag.getClass()))
        {
            return (IElement) tag;
        }

        throw new IllegalArgumentException(String.format("View tag object's type [%s] is not assignable from IElement", tag.getClass().getSimpleName()));
    }

    protected void setFormatAsPrettyPrint(boolean formatAsPrettyPrint)
    {
        this.formatAsPrettyPrint = formatAsPrettyPrint;
        super.notifyDataSetChanged();
    }

    protected boolean formatAsPrettyPrint()
    {
        return this.formatAsPrettyPrint;
    }

    protected void addBottomSpacer()
    {
        if(!super.content.isEmpty() && !(super.getItem(super.getItemCount() - 1) instanceof BottomSpacer))
        {
            super.insertItem(new BottomSpacer());
            Log.d("added BottomSpacer");
        }
    }

    static class ViewHolderElement extends RecyclerView.ViewHolder
    {
        final LinearLayout linearLayout;
        final TextView textViewName;
        final TextView textViewDetailAbove;
        final TextView textViewDetailBelow;
        final ImageView imageViewExpandToggle;

        final TextView textViewPrettyPrint;

        ViewHolderElement(View view)
        {
            super(view);
            this.linearLayout = view.findViewById(R.id.linearLayoutRecyclerView);
            this.textViewName = view.findViewById(R.id.textViewRecyclerView_Name);
            this.textViewDetailAbove = view.findViewById(R.id.textViewRecyclerView_DetailAbove);
            this.textViewDetailBelow = view.findViewById(R.id.textViewRecyclerView_DetailBelow);
            this.imageViewExpandToggle = view.findViewById(R.id.imageViewRecyclerView);

            this.textViewPrettyPrint = view.findViewById(R.id.textViewRecyclerViewItem_PrettyPrint);
        }
    }

    static class ViewHolderVisitedAttraction extends RecyclerView.ViewHolder
    {
        final LinearLayout linearLayoutEditable;
        final LinearLayout linearLayoutCounter;
        final TextView textViewName;
        final TextView textViewCount;
        final ImageView imageViewDecrease;
        final ImageView imageViewIncrease;

        final TextView textViewPrettyPrint;

        ViewHolderVisitedAttraction(View view)
        {
            super(view);

            this.linearLayoutEditable = view.findViewById(R.id.linearLayoutRecyclerViewVisitedAttraction_OpenForEditing);

            this.linearLayoutCounter = view.findViewById(R.id.linearLayoutRecyclerViewVisitedAttraction_Counter);

            this.textViewName = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_Name);
            this.textViewCount = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_Count);

            this.imageViewIncrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Increase);
            this.imageViewIncrease.setImageDrawable(App.getContext().getDrawable(R.drawable.add_circle_outline));

            this.imageViewDecrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Decrease);
            this.imageViewDecrease.setImageDrawable(App.getContext().getDrawable(R.drawable.remove_circle_outline));


            this.textViewPrettyPrint = view.findViewById(R.id.textViewRecyclerViewItemVisitedAttraction_PrettyPrint);
        }
    }

    static class ViewHolderBottomSpacer extends RecyclerView.ViewHolder
    {
        ViewHolderBottomSpacer(View view)
        {
            super(view);
            view.setClickable(false);
        }
    }
}