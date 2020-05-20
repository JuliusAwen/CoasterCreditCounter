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
    private Configuration configuration;


    public ContentRecyclerViewAdapterOrder(List<IElement> content)
    {
        this.content = content;
        this.configuration = new Configuration();
        Log.d(String.format(Locale.getDefault(), "instantiated with [%d] Elements", content.size()));
    }

    public ContentRecyclerViewAdapterOrder servePreset(RequestCode requestCode)
    {
        this.configuration = ConfigurationPresetProvider.createPresetConfiguration(requestCode);
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
            throw new IllegalStateException(
                    String.format("\nContentRecyclerViewAdapterConfiguration not valid:\n%s\n\n%s",
                        this.configuration.isSelectable,
                        this.configuration.validate(false)));
        }
    }
}
