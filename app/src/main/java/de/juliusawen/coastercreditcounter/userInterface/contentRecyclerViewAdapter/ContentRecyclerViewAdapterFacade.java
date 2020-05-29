package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class ContentRecyclerViewAdapterFacade
{
    private ContentRecyclerViewDecoration decoration;
    private ContentRecyclerViewAdapterConfiguration configuration;
    private ContentRecyclerViewAdapter adapter;

    private List<IElement> content = new ArrayList<>();

    public ContentRecyclerViewAdapterFacade()
    {
        this.decoration = new ContentRecyclerViewDecoration();
        this.configuration = new ContentRecyclerViewAdapterConfiguration(this.decoration);
    }

    public void createAdapter(ContentRecyclerViewAdapterConfiguration configuration)
    {
        this.configuration = configuration;
        this.decoration = configuration.getDecoration();
        this.adapter = new ContentRecyclerViewAdapter(configuration);
    }

    public void createDefaultAdapter()
    {
        this.adapter = new ContentRecyclerViewAdapter(this.configuration);
    }

    public void createPreconfiguredAdapter(RequestCode requestCode)
    {
        ContentRecyclerViewConfigurationPresetProvider.applyPreset(this.configuration, requestCode);
        ContentRecyclerViewDecorationPresetProvider.applyPreset(this.decoration, requestCode);
        this.adapter = new ContentRecyclerViewAdapter(this.configuration);
        this.adapter.setContent(this.content);
    }

    public ContentRecyclerViewDecoration getDecoration()
    {
        return this.decoration;
    }

    public ContentRecyclerViewAdapterConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public ContentRecyclerViewAdapter getAdapter()
    {
        return this.adapter;
    }

    public void setContent(IElement element)
    {
        List<IElement> content = new ArrayList<>();
        content.add(element);
        this.setContent(content);
    }

    public void setContent(List<IElement> content)
    {
        this.content = content;
        if(this.adapter != null)
        {
            this.adapter.setContent(content);
        }
    }

    public void applyDecorationPreset(RequestCode requestCode)
    {
        ContentRecyclerViewDecorationPresetProvider.applyPreset(this.decoration, requestCode);

        if(this.adapter != null)
        {
            this.adapter.notifyDataSetChanged();
        }
    }
}
