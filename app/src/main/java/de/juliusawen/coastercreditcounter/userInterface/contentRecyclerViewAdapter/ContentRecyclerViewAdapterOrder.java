package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import android.view.View;

import java.util.ArrayList;
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
    private Configuration configuration;
    private IContentRecyclerViewAdapter contentRecyclerViewAdapter;

    public ContentRecyclerViewAdapterOrder(IElement element)
    {
        List<IElement> content = new ArrayList<>();
        content.add(element);

        this.initialize(content);
    }

    public ContentRecyclerViewAdapterOrder(List<IElement> content)
    {
        this.initialize(content);
    }

    private void initialize(List<IElement> content)
    {
        Log.d(String.format(Locale.getDefault(), "initializing with [%d] Elements - instantiating dependencies...", content.size()));
        this.content = content;
        this.configuration = new Configuration();
        this.contentRecyclerViewAdapter = new ContentRecyclerViewAdapter();
    }

    public ContentRecyclerViewAdapterOrder servePreset(RequestCode requestCode)
    {
        ConfigurationPresetProvider.applyPreset(this.configuration, requestCode);
        this.configuration.setDecoration(DecorationPresetProvider.createPresetDecoration(requestCode));
        return this;
    }

    public ContentRecyclerViewAdapterOrder addOnClickListenerForType(Class<? extends IElement> type, View.OnClickListener onClickListener)
    {
        this.configuration.addOnClickListenerByType(type, onClickListener);
        return this;
    }

    public ContentRecyclerViewAdapterOrder addOnLongClickListenerForType(Class<? extends IElement> type, View.OnLongClickListener onLongClickListener)
    {
        this.configuration.addOnLongClickListenerByType(type, onLongClickListener);
        return this;
    }

    public IContentRecyclerViewAdapter placeOrder()
    {
        if(this.configuration.validate(!BuildConfig.DEBUG))
        {
            Log.wrap(LogLevel.VERBOSE,
                    String.format("delivering ContentRecyclerViewAdapter with %s:\n\n%s\n\n%s",
                            String.format(Locale.getDefault(), "[%d] Elements", this.content.size()),
                            this.configuration,
                            this.configuration.getDecoration()),
                    '=', false);

            this.contentRecyclerViewAdapter.configure(this.configuration);
            this.contentRecyclerViewAdapter.setContent(this.content);
            return this.contentRecyclerViewAdapter;
        }
        else
        {
            throw new IllegalStateException(
                    String.format("\nContentRecyclerViewAdapterConfiguration not valid:\n%s\n\n%s",
                        this.configuration.isSelectable,
                        this.configuration.validate(false)));
        }
    }
}
