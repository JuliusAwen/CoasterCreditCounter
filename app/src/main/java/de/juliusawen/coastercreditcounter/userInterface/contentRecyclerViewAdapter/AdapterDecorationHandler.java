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
    private boolean isDecorable;
    private Decoration decoration;

    AdapterDecorationHandler()
    {
        super();
        Log.v("instantiated");
    }

    protected void configure(Configuration configuration)
    {
        super.configure(configuration);

        this.isDecorable = configuration.isDecorable;
        if(this.isDecorable)
        {
            this.decoration = configuration.getDecoration();
            Log.wrap(LogLevel.VERBOSE, String.format("instantiated [%s] with \n%s", this.getClass().getSimpleName(), this.decoration), '=', false);
        }
    }

    protected IElement bindViewHolderElement(final ContentRecyclerViewAdapter.ViewHolderElement viewHolder, int position)
    {
        IElement element = super.bindViewHolderElement(viewHolder, position);

        if(this.isDecorable)
        {
            Log.v(String.format(Locale.getDefault(), "binding %s for position [%d]...", element, position));

            this.applyTypeface(element, viewHolder);
            this.applySpecialStringRecource(element, viewHolder);
            this.setDetails(element, viewHolder);
        }

        return element;
    }

    private void applyTypeface(IElement element, ContentRecyclerViewAdapter.ViewHolderElement viewHolder)
    {
        viewHolder.textViewName.setTypeface(null, this.decoration.getTypeface(element));
    }

    private void applySpecialStringRecource(IElement element, ContentRecyclerViewAdapter.ViewHolderElement viewHolder)
    {
        String specialString = this.decoration.getSpecialString(element);
        if(specialString != null)
        {
            viewHolder.textViewName.setText(specialString);
        }
    }

    private void setDetails(IElement element, ContentRecyclerViewAdapter.ViewHolderElement viewHolder)
    {
        viewHolder.textViewDetailAbove.setVisibility(View.GONE);
        viewHolder.textViewDetailBelow.setVisibility(View.GONE);

        Map<DetailDisplayMode, Set<DetailType>> detailTypesByDetailDisplayMode = this.decoration.getDetailTypesByDetailDisplayMode(element);

        if(detailTypesByDetailDisplayMode.get(DetailDisplayMode.ABOVE).size() > 0)
        {
            viewHolder.textViewDetailAbove.setText(this.decoration.getSpannableDetailString(element, detailTypesByDetailDisplayMode.get(DetailDisplayMode.ABOVE)));
            if(viewHolder.textViewDetailAbove.getText().length() != 0)
            {
                viewHolder.textViewDetailAbove.setVisibility(View.VISIBLE);
            }
        }

        if(detailTypesByDetailDisplayMode.get(DetailDisplayMode.BELOW).size() > 0)
        {
            viewHolder.textViewDetailBelow.setText(this.decoration.getSpannableDetailString(element, detailTypesByDetailDisplayMode.get(DetailDisplayMode.BELOW)));
            if(viewHolder.textViewDetailBelow.getText().length() != 0)
            {
                viewHolder.textViewDetailBelow.setVisibility(View.VISIBLE);
            }
        }
    }
}