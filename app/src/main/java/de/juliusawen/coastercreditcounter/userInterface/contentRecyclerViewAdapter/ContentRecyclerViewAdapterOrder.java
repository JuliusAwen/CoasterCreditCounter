package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.BuildConfig;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class ContentRecyclerViewAdapterOrder
{
    private List<IElement> content;
    private AdapterConfiguration adapterConfiguration;

    private boolean presetOrdered = false;

    public ContentRecyclerViewAdapterOrder(List<IElement> content)
    {
        this.content = content;
        this.adapterConfiguration = new AdapterConfiguration(new RecyclerViewDecoration());
        Log.v("instantiated");
    }

    public AdapterConfiguration getAdapterConfiguration()
    {
        return this.adapterConfiguration;
    }

    public ContentRecyclerViewAdapterOrder servePreset(RequestCode requestCode)
    {
        ConfigurationPresetProvider.applyConfigurationPreset(this.adapterConfiguration, requestCode);
        this.presetOrdered = true;
        return this;
    }

    public ContentRecyclerViewAdapterOrder makeItDecorable(RecyclerViewDecoration recyclerViewDecoration)
    {
        if(!this.presetOrdered)
        {
            this.adapterConfiguration.isDecorable = true;
            this.adapterConfiguration.setRecyclerViewDecoration(recyclerViewDecoration);
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
            this.adapterConfiguration.isExpandable = true;
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
            this.adapterConfiguration.isSelectable = true;
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
            this.adapterConfiguration.isCountable = true;
            return this;
        }
        else
        {
            throw new UnsupportedOperationException("already ordered a preset");
        }
    }

    public ContentRecyclerViewAdapterOrder addOnClickListenerForType(Class<? extends IElement> type, View.OnClickListener onClickListener)
    {
        this.adapterConfiguration.addOnClickListenerByType(type, onClickListener);
        return this;
    }

    public ContentRecyclerViewAdapterOrder addOnLongClickListenerForType(Class<? extends IElement> type, View.OnLongClickListener onLongClickListener)
    {
        this.adapterConfiguration.addOnLongClickListenerByType(type, onLongClickListener);
        return this;
    }

    public <T extends IContentRecyclerViewAdapter> T placeOrderFor(Class<T> type)
    {
        if(this.adapterConfiguration.validate(!BuildConfig.DEBUG))
        {
            Log.wrap(LogLevel.VERBOSE,
                    String.format("delivering ContentRecyclerViewAdapter with %s:\n\n%s\n\n%s",
                            String.format(Locale.getDefault(), "[%d] Elements", this.content.size()),
                            this.adapterConfiguration,
                            this.adapterConfiguration.getRecyclerViewDecoration()),
                    '=', false);

            ContentRecyclerViewAdapter adapter = new ContentRecyclerViewAdapter(this.content, this.adapterConfiguration);
            if(type.isInstance(adapter))
            {
                return type.cast(adapter);
            }

            throw new ClassCastException(String.format("not able to cast: [%s] is not instance of [%s]", adapter.getClass().getSimpleName(), type.getSimpleName()));
        }
        else
        {
            throw new IllegalStateException(
                    String.format("\nContentRecyclerViewAdapterConfiguration not valid:\n%s\n\n%s",
                        this.adapterConfiguration.isSelectable,
                        this.adapterConfiguration.validate(false)));
        }
    }
}
