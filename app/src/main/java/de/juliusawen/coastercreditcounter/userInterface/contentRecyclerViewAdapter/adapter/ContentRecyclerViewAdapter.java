package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public class ContentRecyclerViewAdapter extends ExpandableContentRecyclerViewAdapter
{
    protected RecyclerView recyclerView;

    public ContentRecyclerViewAdapter(List<IElement> content, ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(content, configuration);

        Log.wrap(LogLevel.DEBUG,
                String.format("Details:\n\n%s\n\n%s\n\n%s",
                        String.format(Locale.getDefault(), "[%d] Elements", this.content.size()),
                        this.configuration,
                        this.configuration.getDecoration()),
                '=', false);
        Log.wrap(LogLevel.INFO, "instantiated", '#', true);
    }

    @Override
    public int getItemViewType(int position)
    {
        IElement element = this.content.get(position);

        if(element.isVisitedAttraction())
        {
            return ViewType.VISITED_ATTRACTION.ordinal();
        }
        else if(element.isBottomSpacer())
        {
            return ViewType.BOTTOM_SPACER.ordinal();
        }
        else
        {
            return ViewType.ELEMENT.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeOfView)
    {
        RecyclerView.ViewHolder viewHolder;
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        switch(ViewType.getValue(typeOfView))
        {
            case ELEMENT:
                view = layoutInflater.inflate(R.layout.layout_recycler_view_item, viewGroup, false);
                viewHolder = new ViewHolderElement(view);
                break;

            case VISITED_ATTRACTION:
                view = layoutInflater.inflate(R.layout.layout_recycler_view_item_visited_attraction, viewGroup, false);
                viewHolder = new ViewHolderVisitedAttraction(view);
                break;

            case BOTTOM_SPACER:
                view = layoutInflater.inflate(R.layout.layout_bottom_spacer, viewGroup, false);
                viewHolder = new ViewHolderBottomSpacer(view);
                break;

            default:
                throw new IllegalStateException(String.format("unknown ViewType [%s]", ViewType.getValue(typeOfView)));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        ViewType viewType = ViewType.getValue(viewHolder.getItemViewType());
        Log.v(String.format("binding ViewType[%s]", viewType));

        switch (viewType)
        {
            case ELEMENT:
                ViewHolderElement viewHolderElement = (ViewHolderElement) viewHolder;
                this.bindViewHolderElement(viewHolderElement, position);
                break;

//            case VISITED_ATTRACTION:
//                ViewHolderVisitedAttraction viewHolderVisitedAttraction = (ViewHolderVisitedAttraction) viewHolder;
//                this.bindViewHolderVisitedAttraction(viewHolderVisitedAttraction, position);
//                break;

            case BOTTOM_SPACER:
                break;

            default:
                throw new IllegalStateException(String.format("unknown ViewType[%s]", viewType));
        }
    }

    @Override
    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        return super.bindViewHolderElement(viewHolder, position);
    }
}
