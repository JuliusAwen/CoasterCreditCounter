package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

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
    private final boolean hasExternalOnClickListeners;

    private final View.OnClickListener internalOnClickListener;
    private final View.OnLongClickListener internalOnLongClickListener;

    private final Map<Class<? extends IElement>, View.OnClickListener> externalOnClickListenersByType = new HashMap<>();
    private final Map<Class<? extends IElement>, View.OnLongClickListener> externalOnLongClickListenersByType = new HashMap<>();

    AdapterPlainHandler(Configuration configuration)
    {
        super(configuration);

        this.hasExternalOnClickListeners = configuration.hasExternalOnClickListeners;
        this.internalOnClickListener = this.getInternalOnClickListener();
        this.internalOnLongClickListener = this.getInternalOnLongClickListener();

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



    protected IElement bindViewHolderElement(final ContentRecyclerViewAdapter.ViewHolderElement viewHolder, int position)
    {
        IElement element = super.getElement(position);
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

    protected void setPadding(int generation, ContentRecyclerViewAdapter.ViewHolderElement viewHolder)
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
            IElement element = this.fetchElement(view);
            View.OnClickListener externalOnClickListener = this.fetchExternalOnClickListener(element);
            if(externalOnClickListener != null)
            {
                externalOnClickListener.onClick(view);
            }
        }
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
        if(performExternalLongClick && this.hasExternalOnClickListeners)
        {
            IElement element = this.fetchElement(view);
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

    protected AdapterPlainHandler addBottomSpacer()
    {
        if(!super.content.isEmpty() && !(super.getElement(super.getItemCount() - 1) instanceof BottomSpacer))
        {
            super.insertElement(new BottomSpacer());
            Log.v("added BottomSpacer");
        }

        return this;
    }
}