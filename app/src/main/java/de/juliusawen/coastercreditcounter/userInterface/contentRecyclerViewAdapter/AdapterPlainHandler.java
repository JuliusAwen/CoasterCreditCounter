package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.temporary.BottomSpacer;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterPlainHandler extends AdapterContentHandler
{
    private boolean hasExternalOnClickListeners;

    private final View.OnClickListener internalOnClickListener;
    private final View.OnLongClickListener internalOnLongClickListener;

    private final Map<Class<? extends IElement>, View.OnClickListener> externalOnClickListenersByType = new HashMap<>();
    private final Map<Class<? extends IElement>, View.OnLongClickListener> externalOnLongClickListenersByType = new HashMap<>();

    AdapterPlainHandler()
    {
        super();
        this.internalOnClickListener = this.getInternalOnClickListener();
        this.internalOnLongClickListener = this.getInternalOnLongClickListener();
        Log.v("instantiated");
    }

    protected void configure(Configuration configuration)
    {
        super.configure(configuration);

        this.hasExternalOnClickListeners = configuration.hasExternalOnClickListeners;
        if(this.hasExternalOnClickListeners)
        {
            this.externalOnClickListenersByType.putAll(configuration.getOnClickListenersByType());
            this.externalOnLongClickListenersByType.putAll(configuration.getOnLongClickListenersByType());

            Log.wrap(LogLevel.VERBOSE,
                    String.format(Locale.getDefault(), "Instantiated with [%d] external OnClickListeners and [%d] external OnLongClickListeners",
                            this.externalOnClickListenersByType.size(),
                            this.externalOnLongClickListenersByType.size()),
                    '=', false);
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
        IElement item = super.getItem(position);
        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", item, position));

        this.setPadding(0, viewHolder);

        viewHolder.itemView.setTag(item);
        viewHolder.itemView.setOnClickListener(this.internalOnClickListener);
        viewHolder.itemView.setOnLongClickListener(this.internalOnLongClickListener);

        viewHolder.imageViewExpandToggle.setTag(item);
        viewHolder.imageViewExpandToggle.setOnClickListener(this.internalOnClickListener);
        viewHolder.imageViewExpandToggle.setOnLongClickListener(this.internalOnLongClickListener);

        viewHolder.textViewName.setText(item.getName());
        viewHolder.linearLayout.setVisibility(View.VISIBLE);

        return item;
    }

    protected void setPadding(int generation, ViewHolderElement viewHolder)
    {
        int padding = ConvertTool.convertDpToPx((int)(App.getContext().getResources().getDimension(R.dimen.standard_padding)
                / App.getContext().getResources().getDisplayMetrics().density))
                * generation;

        viewHolder.linearLayout.setPadding(padding, 0, padding, 0);
    }

    protected void handleOnClick(View view, boolean performExternalClick)
    {
        if(performExternalClick && this.hasExternalOnClickListeners)
        {
            IElement element = this.fetchItem(view);
            View.OnClickListener externalOnClickListener = this.fetchExternalOnClickListener(element);
            if(externalOnClickListener != null)
            {
                externalOnClickListener.onClick(view);
            }
        }
    }

    private View.OnClickListener fetchExternalOnClickListener(IElement item)
    {
        if(this.externalOnClickListenersByType.containsKey(item.getClass()))
        {
            return this.externalOnClickListenersByType.get(item.getClass());
        }
        else
        {
            for(Class<? extends IElement> type : this.externalOnClickListenersByType.keySet())
            {
                if(type.isAssignableFrom(item.getClass()))
                {
                    return this.externalOnClickListenersByType.get(type);
                }
            }
        }

        return null;
    }

    protected boolean handleOnLongClick(View view, boolean performExternalLongClick)
    {
        if(performExternalLongClick && this.hasExternalOnClickListeners)
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
        if(this.externalOnLongClickListenersByType.containsKey(item.getClass()))
        {
            return this.externalOnLongClickListenersByType.get(item.getClass());
        }
        else
        {
            for(Class<? extends IElement> type : this.externalOnLongClickListenersByType.keySet())
            {
                if(type.isAssignableFrom(item.getClass()))
                {
                    return this.externalOnLongClickListenersByType.get(type);
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

    protected AdapterPlainHandler addBottomSpacer()
    {
        if(!super.content.isEmpty() && !(super.getItem(super.getItemCount() - 1) instanceof BottomSpacer))
        {
            super.insertItem(new BottomSpacer());
            Log.v("added BottomSpacer");
        }

        return this;
    }

    static class ViewHolderElement extends RecyclerView.ViewHolder
    {
        final LinearLayout linearLayout;
        final TextView textViewDetailAbove;
        final TextView textViewName;
        final TextView textViewDetailBelow;
        final ImageView imageViewExpandToggle;

        final TextView textViewPrettyPrint;


        ViewHolderElement(View view)
        {
            super(view);
            this.linearLayout = view.findViewById(R.id.linearLayoutRecyclerView);
            this.textViewDetailAbove = view.findViewById(R.id.textViewRecyclerView_DetailAbove);
            this.textViewName = view.findViewById(R.id.textViewRecyclerView_Name);
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