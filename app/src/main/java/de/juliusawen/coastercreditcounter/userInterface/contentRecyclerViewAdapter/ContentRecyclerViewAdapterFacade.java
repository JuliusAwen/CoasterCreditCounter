package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

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

    public IContentRecyclerViewAdapter createDefaultAdapter()
    {
        this.adapter = new ContentRecyclerViewAdapter(this.configuration);
        return this.adapter;
    }

    public IContentRecyclerViewAdapter createPreconfiguredAdapter(RequestCode requestCode)
    {
        ContentRecyclerViewAdapterConfigurationPresetProvider.applyConfigurationPreset(this.configuration, requestCode);
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode);
        this.adapter = new ContentRecyclerViewAdapter(this.configuration);
        return this.adapter;
    }

    public IContentRecyclerViewAdapter createPreconfiguredAdapter(RequestCode requestCode, ElementType elementType)
    {
        ContentRecyclerViewAdapterConfigurationPresetProvider.applyConfigurationPreset(this.configuration, requestCode);
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode, elementType);
        this.adapter = new ContentRecyclerViewAdapter(this.configuration);
        return this.adapter;
    }

    public ContentRecyclerViewDecoration getDecoration()
    {
        return this.decoration;
    }

    public ContentRecyclerViewAdapterConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public IContentRecyclerViewAdapter getAdapter()
    {
        return this.adapter;
    }

    public ContentRecyclerViewAdapterFacade applyDecorationPreset(RequestCode requestCode)
    {
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode);

        if(this.adapter != null)
        {
            this.adapter.notifyDataSetChanged();
        }

        return this;
    }

    public ContentRecyclerViewAdapterFacade applyDecorationPreset(RequestCode requestCode, ElementType elementType)
    {
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode, elementType);

        if(this.adapter != null)
        {
            this.adapter.notifyDataSetChanged();
        }

        return this;
    }

    public ContentRecyclerViewAdapterFacade setDetailModesAndGroupContent(RequestCode requestCode, GroupType groupType)
    {
        ContentRecyclerViewDecorationPresetProvider.applyDecorationPreset(this.decoration, requestCode, groupType);
        this.adapter.groupContent(groupType);

        return this;
    }
}
