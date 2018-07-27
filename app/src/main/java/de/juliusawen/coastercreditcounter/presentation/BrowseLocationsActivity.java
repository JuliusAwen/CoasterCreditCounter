package de.juliusawen.coastercreditcounter.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;

public class BrowseLocationsActivity extends AppCompatActivity implements View.OnClickListener
{
    Element currentElement = Content.getInstance().getLocationRoot();
    List<Element> recentElements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_locations);

        this.refreshViews();
    }

    private void refreshViews()
    {
        LinearLayout linearLayoutActivity = findViewById(R.id.linearLayoutBrowseLocations);
        linearLayoutActivity.invalidate();
        linearLayoutActivity.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.layout_browse_locations, linearLayoutActivity, false);
        linearLayoutActivity.addView(view);

        this.addToolbar(view);
        this.addNavigationBar(view);
        this.addContentButtons(view);
    }

    private void addToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.text_browse_locations));
        setSupportActionBar(toolbar);
    }

    private void addNavigationBar(View view)
    {
        LinearLayout linearLayoutNavigationBar = view.findViewById(R.id.linearLayoutNavigationBar);

        if(!this.recentElements.contains(this.currentElement))
        {
            this.recentElements.add(this.currentElement);
        }

        for (Element element : this.recentElements)
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_no_border, linearLayoutNavigationBar, false);
            Button button = buttonView.findViewById(R.id.buttonNoBorder);

            button.setText(getString(R.string.button_text_back, element.getName()));
            button.setId(Constants.BUTTON_BACK);
            button.setTag(element);

            button.setOnClickListener(this);

            if(element.equals(this.currentElement))
            {
                button.setClickable(false);
            }

            linearLayoutNavigationBar.addView(buttonView);
        }
    }

    private void addContentButtons(View view)
    {
        LinearLayout linearLayoutContentBar = view.findViewById(R.id.linearLayoutContentBar);

        for(Element element : ((Location) this.currentElement).getChildren())
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_standard, linearLayoutContentBar, false);

            Button contentButton = buttonView.findViewById(R.id.button);
            contentButton.setText(element.getName());
            contentButton.setTag(element);
            contentButton.setOnClickListener(this);
//            contentButton.setOnLongClickListener(this);

            linearLayoutContentBar.addView(buttonView);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().convertToUuidStringArrayList(this.recentElements));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        List<String> strings = savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS);
        this.recentElements = Content.getInstance().getElementsFromUuidStringArrayList(strings);
        this.currentElement = this.recentElements.get(this.recentElements.size() - 1);

        this.refreshViews();
    }

    @Override
    public void onClick(View view)
    {
        @SuppressWarnings("unchecked")
        Element element = (Location) view.getTag();

        if(view.getId() == Constants.BUTTON_BACK)
        {
            int length =  this.recentElements.size()-1;
            for (int i = length; i >= 0  ; i--)
            {
                if(this.recentElements.get(i).equals(element))
                {
                    this.recentElements.remove(i);
                    break;
                }
                else
                {
                    this.recentElements.remove(i);
                }
            }
        }

        this.currentElement = element;
        refreshViews();
    }
}
