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
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ICountableRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IDecorableContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IDecorableExpandableContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.IExpandableContentRecyclerViewAdapter;

public class ContentRecyclerViewAdapter extends ExpandableContentRecyclerViewAdapter
        implements IContentRecyclerViewAdapter, IDecorableContentRecyclerViewAdapter, IExpandableContentRecyclerViewAdapter, ICountableRecyclerViewAdapter,
        IDecorableExpandableContentRecyclerViewAdapter

{
    protected RecyclerView recyclerView;

    public ContentRecyclerViewAdapter(List<IElement> content, ContentRecyclerViewAdapterConfiguration configuration)
    {
        super(content, configuration);

        Log.wrap(LogLevel.DEBUG,
                String.format("Details:\n\n%s\n\n%s\n\n%s",
                        String.format(Locale.getDefault(), "[%d] Elements", this.content.size()),
                        configuration,
                        configuration.getDecoration()),
                '=', false);
        Log.wrap(LogLevel.INFO, "instantiated", '#', true);
    }

    @Override
    public int getItemViewType(int position)
    {
        IElement element = this.content.get(position);

        if(element.isVisitedAttraction())
        {
            return ItemViewType.VISITED_ATTRACTION.ordinal();
        }
        else if(element.isBottomSpacer())
        {
            return ItemViewType.BOTTOM_SPACER.ordinal();
        }
        else
        {
            return ItemViewType.ELEMENT.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeOfView)
    {
        RecyclerView.ViewHolder viewHolder;
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        switch(ItemViewType.getValue(typeOfView))
        {
            case ELEMENT:
                view = layoutInflater.inflate(R.layout.layout_recycler_view_item_view_type_element, viewGroup, false);
                viewHolder = new ViewHolderElement(view);
                break;

            case VISITED_ATTRACTION:
                view = layoutInflater.inflate(R.layout.layout_recycler_view_item_view_type_visited_attraction, viewGroup, false);
                viewHolder = new ViewHolderVisitedAttraction(view);
                break;

            case BOTTOM_SPACER:
                view = layoutInflater.inflate(R.layout.layout_bottom_spacer, viewGroup, false);
                viewHolder = new ViewHolderBottomSpacer(view);
                break;

            default:
                throw new IllegalStateException(String.format("unknown ViewType [%s]", ItemViewType.getValue(typeOfView)));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        ItemViewType itemViewType = ItemViewType.getValue(viewHolder.getItemViewType());
        Log.v(String.format("binding ViewType[%s]", itemViewType));

        switch (itemViewType)
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
                throw new IllegalStateException(String.format("unknown ViewType[%s]", itemViewType));
        }
    }

    @Override
    protected IElement bindViewHolderElement(final ViewHolderElement viewHolder, int position)
    {
        return super.bindViewHolderElement(viewHolder, position);
    }
}
