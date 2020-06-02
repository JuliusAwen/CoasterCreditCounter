package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class ContentRecyclerViewAdapterFacade
{
    private ContentRecyclerViewDecoration decoration;
    private ContentRecyclerViewAdapterConfiguration configuration;
    private ContentRecyclerViewAdapter adapter;

    public ContentRecyclerViewAdapterFacade()
    {
        this.decoration = new ContentRecyclerViewDecoration();
        this.configuration = new ContentRecyclerViewAdapterConfiguration(this.decoration);
    }

    public void createPreconfiguredAdapter(RequestCode requestCode)
    {
        ContentRecyclerViewAdapterConfigurationPresetProvider.applyConfigurationPreset(this.configuration, requestCode);
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode);
        this.adapter = new ContentRecyclerViewAdapter(this.configuration);
    }

    public void createPreconfiguredAdapter(RequestCode requestCode, ElementType elementType)
    {
        ContentRecyclerViewAdapterConfigurationPresetProvider.applyConfigurationPreset(this.configuration, requestCode);
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode, elementType);
        this.adapter = new ContentRecyclerViewAdapter(this.configuration);
    }

    public void createPreconfiguredAdapter(RequestCode requestCode, GroupType groupType)
    {
        ContentRecyclerViewAdapterConfigurationPresetProvider.applyConfigurationPreset(this.configuration, requestCode);
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode, groupType);
        this.adapter = new ContentRecyclerViewAdapter(this.configuration);
    }

    public ContentRecyclerViewAdapterConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public IContentRecyclerViewAdapter getAdapter()
    {
        return this.adapter;
    }

    public void applyPresetDecoration(RequestCode requestCode, GroupType groupType)
    {
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode, groupType);
    }

    public void setSingleElementAsContent(IElement element)
    {
        Log.i(String.format(Locale.getDefault(), "setting %s as content...", element));
        List<IElement> content = new ArrayList<>();
        content.add(element);
        this.adapter.setContent(content);
    }
}
