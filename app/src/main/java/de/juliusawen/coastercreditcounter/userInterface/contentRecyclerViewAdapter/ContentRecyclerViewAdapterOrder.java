package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.BuildConfig;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter.ContentRecyclerViewAdapterConfiguration;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter.ContentRecyclerViewDecoration;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter.ContentRecyclerViewOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter.interfaces.IContentRecyclerViewAdapter;

public class ContentRecyclerViewAdapterOrder
{
    private List<IElement> content;
    private ContentRecyclerViewAdapterConfiguration configuration;

    private boolean presetOrdered = false;

    public ContentRecyclerViewAdapterOrder(List<IElement> content)
    {
        this.content = content;
        this.configuration = new ContentRecyclerViewAdapterConfiguration(new ContentRecyclerViewDecoration());
        Log.v("instantiated");
    }

    public ContentRecyclerViewAdapterOrder servePreset(RequestCode requestCode)
    {
        ContentRecyclerViewAdapterConfigurationPresetProvider.applyConfigurationPreset(this.configuration, requestCode);
        this.presetOrdered = true;
        return this;
    }

    public ContentRecyclerViewAdapterOrder makeItDecorable(ContentRecyclerViewDecoration decoration)
    {
        if(!this.presetOrdered)
        {
            this.configuration.isDecorable = true;
            this.configuration.setDecoration(decoration);
            return this;
        }
        else
        {
            throw new UnsupportedOperationException("already ordered a preset");
        }
    }

    public ContentRecyclerViewAdapterOrder makeItExpandable()
    {
        if(!this.presetOrdered)
        {
            this.configuration.isExpandable = true;
            return this;
        }
        else
        {
            throw new UnsupportedOperationException("already ordered a preset");
        }
    }

    public ContentRecyclerViewAdapterOrder makeItSelectable()
    {
        if(!this.presetOrdered)
        {
            this.configuration.isSelectable = true;
            return this;
        }
        else
        {
            throw new UnsupportedOperationException("already ordered a preset");
        }
    }

    public ContentRecyclerViewAdapterOrder makeItCountable()
    {
        if(!this.presetOrdered)
        {
            this.configuration.isCountable = true;
            return this;
        }
        else
        {
            throw new UnsupportedOperationException("already ordered a preset");
        }
    }

    public ContentRecyclerViewAdapterOrder addOnClickListener(ContentRecyclerViewOnClickListener.CustomItemOnClickListener customItemOnClickListener)
    {
        this.configuration.setCustomItemOnClickListener(customItemOnClickListener);
        return this;
    }

    public <T extends IContentRecyclerViewAdapter> T placeOrderFor(Class<T> type)
    {
        if(this.configuration.validate(!BuildConfig.DEBUG))
        {
            Log.wrap(LogLevel.VERBOSE,
                    String.format("delivering ContentRecyclerViewAdapter with %s:\n\n%s\n\n%s",
                            String.format(Locale.getDefault(), "[%d] Elements", this.content.size()),
                            this.configuration,
                            this.configuration.getDecoration()),
                    '=', false);

            ContentRecyclerViewAdapter adapter = new ContentRecyclerViewAdapter(this.content, this.configuration);
            if(type.isInstance(adapter))
            {
                return type.cast(adapter);
            }

            throw new ClassCastException(String.format("not able to cast: [%s] is not instance of [%s]", adapter.getClass().getSimpleName(), type.getSimpleName()));
        }
        else
        {
            throw new IllegalStateException(String.format("\nContentRecyclerViewAdapterConfiguration not valid:\n%s", this.configuration));
        }
    }
}
