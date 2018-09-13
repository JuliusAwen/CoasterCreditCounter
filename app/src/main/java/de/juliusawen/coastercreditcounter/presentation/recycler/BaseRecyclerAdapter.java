package de.juliusawen.coastercreditcounter.presentation.recycler;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;

public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.ViewHolder>
{
    private List<Element> elements;
    private RecyclerOnClickListener.OnClickListener onClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout linearLayout;
        TextView textView;

        ViewHolder(LinearLayout linearLayout)
        {
            super(linearLayout);

            this.textView = linearLayout.findViewById(R.id.textViewShowLocationsContentHolder_Parent);
            this.linearLayout = linearLayout;
        }
    }

    public BaseRecyclerAdapter(List<Element> elements, RecyclerOnClickListener.OnClickListener onClickListener)
    {
        this.elements = elements;
        this.onClickListener = onClickListener;
    }

    public void updateList(List<Element> elements)
    {
        this.elements = elements;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.show_locations_content_holder, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position)
    {
        Element element = elements.get(position);

        RecyclerOnClickListener recyclerOnClickListener = new RecyclerOnClickListener(viewHolder, this.onClickListener);

        viewHolder.textView.setText(StringTool.getSpannableString(element.getName(), Typeface.BOLD));
        viewHolder.textView.setTag(element);
        viewHolder.textView.setOnClickListener(recyclerOnClickListener);
        viewHolder.textView.setOnLongClickListener(recyclerOnClickListener);
    }

    @Override
    public int getItemCount()
    {
        return elements.size();
    }
}