package de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter;

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

class ContentRecyclerViewAdapter extends AdapterExpansionHandler implements IContentRecyclerViewAdapter
{
    ContentRecyclerViewAdapter()
    {
        super();
        Log.frame(LogLevel.INFO, "instantiated", '#', true);
    }

    @Override
    public void configure(Configuration configuration)
    {
        Log.v("configuring...");
        super.configure(configuration);
        Log.wrap(LogLevel.DEBUG, String.format("Configuration details:\n\n%s\n\n%s", configuration, configuration.getDecoration()), '=', false);
    }

    @Override
    public void setContent(List<IElement> content)
    {
        Log.d(String.format(Locale.getDefault(), "setting [%d] Items...", content.size()));
        super.setContent(content);
    }

    @Override
    public void notifyContentChanged()
    {
        Log.d("notifying...");
        super.notifyContentChanged();
    }

    @Override
    public void groupContent(GroupType groupType)
    {
        Log.d(String.format("grouping Content by GroupType[%s]...", groupType));
        super.groupContent(groupType);
    }

    public void insertItem(IElement element)
    {
        Log.d(String.format("inserting %s...", element));
        super.insertItem(element);
    }

    public void insertItem(int position, IElement element)
    {
        Log.d(String.format(Locale.getDefault(), "inserting %s at position[%d]...", element, position));
        super.insertItem(position, element);
    }

    public void notifyItemChanged(IElement element)
    {
        Log.d(String.format("notifying %s changed...", element));
        super.notifyItemChanged(element);
    }

    public void removeItem(IElement element)
    {
        Log.d(String.format("removing %s...", element));
        super.removeItem(element);
    }

    @Override
    public int getItemViewType(int position)
    {
        IElement element = super.getItem(position);

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
        Log.v(String.format(Locale.getDefault(), "binding ViewType[%s] for position[%d]...", itemViewType, position));

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


    private enum ItemViewType
    {
        UNDETERMINED,
        ELEMENT,
        VISITED_ATTRACTION,
        BOTTOM_SPACER;

        static ItemViewType getValue(int ordinal)
        {
            if(ItemViewType.values().length >= ordinal)
            {
                return ItemViewType.values()[ordinal];
            }
            else
            {
                Log.e(String.format("ordinal [%s] out of bounds (Enum has [%s] values) - returning [%s]", ordinal, values().length, values()[0]));
                return values()[0];
            }
        }
    }
}