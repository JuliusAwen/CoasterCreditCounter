package de.juliusawen.coastercreditcounter.frontend.spinnerAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import de.juliusawen.coastercreditcounter.backend.elements.IElement;

public class SpinnerAdapter extends ArrayAdapter<IElement>
{

    private List<IElement> elements;

    public SpinnerAdapter(Context context, int textViewResourceId, List<IElement> elements)
    {
        super(context, textViewResourceId, elements);
        this.elements = elements;
    }

    @Override
    public int getCount()
    {
        return elements.size();
    }

    @Override
    public IElement getItem(int position)
    {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(elements.get(position).getName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
    {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setText(elements.get(position).getName());

        return textView;
    }
}
