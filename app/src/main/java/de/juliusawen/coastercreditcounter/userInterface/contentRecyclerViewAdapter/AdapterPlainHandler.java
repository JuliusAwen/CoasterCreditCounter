package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterPlainHandler extends AdapterContentHandler
{
    private boolean formatAsPrettyPrint = false;

    private final View.OnClickListener internalOnElementTypeClickListener;
    private final View.OnLongClickListener internalOnElementTypeLongClickListener;

    private final View.OnClickListener defaultOnClickListener;
    private final View.OnLongClickListener defaultOnLongClickListener;

    private final Drawable iconIncrease;
    private final Drawable iconDecrease;
    private final Drawable iconRemove;

    AdapterPlainHandler(ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(configuration);

        this.internalOnElementTypeClickListener = this.createInternalOnElementTypeClickListener();
        this.internalOnElementTypeLongClickListener = this.createInternalOnElementTypeLongClickListener();

        this.defaultOnClickListener = this.createDefaultOnClickListener();
        this.defaultOnLongClickListener = this.createDefaultOnLongClickListener();

        this.iconIncrease = App.getContext().getDrawable(R.drawable.add_circle_outline);
        this.iconDecrease = App.getContext().getDrawable(R.drawable.remove_circle_outline);
        this.iconRemove = App.getContext().getDrawable(R.drawable.delete);

        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement element = super.getItem(position);
        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", element, position));

        this.setPadding(0, viewHolder);

        viewHolder.itemView.setTag(element);
        viewHolder.itemView.setOnClickListener(this.internalOnElementTypeClickListener);
        viewHolder.itemView.setOnLongClickListener(this.internalOnElementTypeLongClickListener);

        viewHolder.imageViewExpandToggle.setTag(element);
        viewHolder.imageViewExpandToggle.setOnClickListener(this.internalOnElementTypeClickListener);
        viewHolder.imageViewExpandToggle.setOnLongClickListener(this.internalOnElementTypeLongClickListener);

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
            viewHolder.linearLayoutCounter.setOnClickListener(this.internalOnElementTypeClickListener);
            viewHolder.linearLayoutCounter.setOnLongClickListener(this.internalOnElementTypeLongClickListener);

            viewHolder.textViewName.setText(visitedAttraction.getName());
            viewHolder.textViewCount.setText(String.valueOf(visitedAttraction.fetchTotalRideCount()));

            viewHolder.imageViewIncrease.setTag(visitedAttraction);
            viewHolder.imageViewIncrease.setImageDrawable(this.iconIncrease);
            viewHolder.imageViewIncrease.setOnClickListener(this.fetchExternalOnClickListener(OnClickListenerType.INCREASE_RIDE_COUNT));

            viewHolder.imageViewDecrease.setTag(visitedAttraction);
            if(visitedAttraction.getTrackedRideCount() == 0)
            {
                viewHolder.imageViewDecrease.setImageDrawable(this.iconRemove);
                viewHolder.imageViewDecrease.setOnClickListener(this.fetchExternalOnClickListener(OnClickListenerType.REMOVE_VISITED_ATTRACTION));
            }
            else
            {
                viewHolder.imageViewDecrease.setImageDrawable(this.iconDecrease);
                viewHolder.imageViewDecrease.setOnClickListener(this.fetchExternalOnClickListener(OnClickListenerType.DECREASE_RIDE_COUNT));
            }
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

    private View.OnClickListener createInternalOnElementTypeClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnElementTypeClick(view, true);
            }
        };
    }

    protected boolean handleOnElementTypeClick(View view, boolean performExternalOnElementTypeClick)
    {
        if(performExternalOnElementTypeClick && super.hasExternalOnElementTypeClickListeners())
        {
            IElement element = this.fetchItem(view);
            this.fetchExternalOnElementTypeClickListener(element).onClick(view);
        }

        return false;
    }

    private View.OnClickListener fetchExternalOnElementTypeClickListener(IElement item)
    {
        if(super.getExternalOnClickListenersByElementType().containsKey(item.getClass()))
        {
            return super.getExternalOnClickListenersByElementType().get(item.getClass());
        }
        else
        {
            for(ElementType elementType : super.getExternalOnClickListenersByElementType().keySet())
            {
                if(elementType.getType().isAssignableFrom(item.getClass()))
                {
                    return super.getExternalOnClickListenersByElementType().get(elementType);
                }
            }
        }

        return this.defaultOnClickListener;
    }

    private View.OnLongClickListener createInternalOnElementTypeLongClickListener()
    {
        return new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                return handleOnElementTypeLongClick(view, true);
            }
        };
    }

    protected boolean handleOnElementTypeLongClick(View view, boolean performExternalOnElementTypeLongClick)
    {
        if(performExternalOnElementTypeLongClick && super.hasExternalOnElementTypeClickListeners())
        {
            IElement item = this.fetchItem(view);
            return fetchExternalOnElementTypeLongClickListener(item).onLongClick(view);
        }

        return false;
    }

    private View.OnLongClickListener fetchExternalOnElementTypeLongClickListener(IElement item)
    {
        if(super.getExternalOnLongClickListenersByElementType().containsKey(item.getClass()))
        {
            return super.getExternalOnLongClickListenersByElementType().get(item.getClass());
        }
        else
        {
            for(ElementType elementType : super.getExternalOnLongClickListenersByElementType().keySet())
            {
                if(elementType.getType().isAssignableFrom(item.getClass()))
                {
                    return super.getExternalOnLongClickListenersByElementType().get(elementType);
                }
            }
        }

        return this.defaultOnLongClickListener;
    }

    private View.OnClickListener fetchExternalOnClickListener(OnClickListenerType onClickListenerType)
    {
        if(super.getExternalOnClickListenersByOnClickListenerType().containsKey(onClickListenerType))
        {
            return super.getExternalOnClickListenersByOnClickListenerType().get(onClickListenerType);
        }

        return this.defaultOnClickListener;
    }

    private View.OnClickListener createDefaultOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(String.format("no OnClickListener available for %s", view.getTag()));
            }
        };
    }

    private View.OnLongClickListener createDefaultOnLongClickListener()
    {
        return new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                Log.d(String.format("no OnClickListener available for %s", view.getTag()));
                return true;
            }
        };
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
            this.imageViewDecrease = view.findViewById(R.id.imageViewRecyclerViewItemVisitedAttraction_Decrease);

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