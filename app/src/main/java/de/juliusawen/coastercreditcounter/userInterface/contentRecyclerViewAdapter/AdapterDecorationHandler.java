package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

abstract class AdapterDecorationHandler extends AdapterPlainHandler
{
    AdapterDecorationHandler(ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(configuration);
        Log.frame(LogLevel.VERBOSE, "instantiated", '=', true);
    }

    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        IElement element = super.bindViewHolderElement(viewHolder, position);

        Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", element, position));

        this.applyTypeface(element, viewHolder);
        this.applySpecialStringRecource(element, viewHolder);
        this.setDetails(element, viewHolder);

        return element;
    }

    private void applyTypeface(IElement item, ViewHolderElement viewHolder)
    {
        viewHolder.textViewName.setTypeface(null, super.getDecoration().getTypeface(item));
    }

    private void applySpecialStringRecource(IElement item, ViewHolderElement viewHolder)
    {
        String specialString = super.getDecoration().getSpecialString(item);
        if(specialString != null)
        {
            viewHolder.textViewName.setText(specialString);
        }
    }

    private void setDetails(IElement item, ViewHolderElement viewHolder)
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