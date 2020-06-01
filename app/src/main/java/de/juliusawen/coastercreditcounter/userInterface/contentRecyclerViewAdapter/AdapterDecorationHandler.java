package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;
import android.widget.TextView;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterDecorationHandler extends AdapterPlainHandler
{
    AdapterDecorationHandler(ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(configuration);
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    @Override
    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement element = super.bindViewHolderElement(viewHolder, position);

        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", element, position));

        this.applyTypeface(element, viewHolder.textViewName);
        this.applySpecialStringRecource(element, viewHolder.textViewName);
        this.setDetailsOnElement(element, viewHolder);

        return element;
    }

    @Override
    protected VisitedAttraction bindViewHolderVisitedAttraction(ViewHolderVisitedAttraction viewHolder, int position)
    {
        VisitedAttraction visitedAttraction = super.bindViewHolderVisitedAttraction(viewHolder, position);

        if(this.formatAsPrettyPrint())
        {
            Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]", visitedAttraction, position));
            this.applySpecialStringRecource(visitedAttraction, viewHolder.textViewPrettyPrint);
        }

        return visitedAttraction;
    }

    private void applyTypeface(IElement item, TextView textView)
    {
        textView.setTypeface(null, super.getDecoration().getTypeface(item));
    }

    private void applySpecialStringRecource(IElement item, TextView textView)
    {
        String specialString = super.getDecoration().getSpecialString(item);
        if(specialString != null)
        {
            textView.setText(specialString);
        }
    }

    private void setDetailsOnElement(IElement item, ViewHolderElement viewHolder)
    {
        viewHolder.textViewDetailAbove.setVisibility(View.GONE);
        viewHolder.textViewDetailBelow.setVisibility(View.GONE);

        Map<DetailDisplayMode, Set<DetailType>> detailTypesByDetailDisplayMode = super.getDecoration().getDetailTypesByDetailDisplayMode(item);

        if(detailTypesByDetailDisplayMode.get(DetailDisplayMode.ABOVE).size() > 0)
        {
            viewHolder.textViewDetailAbove.setText(super.getDecoration().getSpannableDetailString(item, detailTypesByDetailDisplayMode.get(DetailDisplayMode.ABOVE)));
            if(viewHolder.textViewDetailAbove.getText().length() != 0)
            {
                viewHolder.textViewDetailAbove.setVisibility(View.VISIBLE);
            }
        }

        if(detailTypesByDetailDisplayMode.get(DetailDisplayMode.BELOW).size() > 0)
        {
            viewHolder.textViewDetailBelow.setText(super.getDecoration().getSpannableDetailString(item, detailTypesByDetailDisplayMode.get(DetailDisplayMode.BELOW)));
            if(viewHolder.textViewDetailBelow.getText().length() != 0)
            {
                viewHolder.textViewDetailBelow.setVisibility(View.VISIBLE);
            }
        }
    }
}