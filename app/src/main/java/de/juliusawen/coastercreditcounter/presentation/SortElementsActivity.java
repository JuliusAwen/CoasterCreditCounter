package de.juliusawen.coastercreditcounter.presentation;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;

public class SortElementsActivity extends AppCompatActivity implements View.OnClickListener
{

    private String subtitle;
    private List<Element> elementsToSort;
    private Element selectedElement;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_elements);

        this.prepareContent();
        this.refreshViews();
    }

    private void prepareContent()
    {
        Element currentElement = Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_UUID)));
        this.subtitle = currentElement.getName();

        if(currentElement.getClass().equals(Location.class))
        {
            this.elementsToSort = Content.getInstance().convertToElementArrayList(((Location) currentElement).getChildren());
        }
        else if(currentElement.getClass().equals(Park.class))
        {
            this.elementsToSort = Content.getInstance().convertToElementArrayList(((Park) currentElement).getAttractions());
        }

        this.selectedElement = this.elementsToSort.get(0);
    }

    private void refreshViews()
    {
        LinearLayout linearLayoutActivity = findViewById(R.id.linearLayoutSortElements);
        linearLayoutActivity.invalidate();
        linearLayoutActivity.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.layout_sort_elements, linearLayoutActivity, false);
        linearLayoutActivity.addView(view);

        this.addToolbar(view);
        this.decorateActionDialogTop(view);
        this.decorateActionDialogBottom(view);
        this.addContentButtons(view);
    }
    private void addToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_sort_elements));
        toolbar.setSubtitle(this.subtitle);
        setSupportActionBar(toolbar);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_items, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    private void decorateActionDialogTop(View view)
    {
        Button buttonDown = view.findViewById(R.id.buttonActionDialogLeft);
        buttonDown.setText(getString(R.string.button_text_down));
        buttonDown.setId(Constants.BUTTON_DOWN);
//        buttonDown.setOnClickListener(this);

        Button buttonUp = view.findViewById(R.id.buttonActionDialogRight);
        buttonUp.setText(getString(R.string.button_text_up));
        buttonUp.setId(Constants.BUTTON_UP);
    }

    private void decorateActionDialogBottom(View view)
    {
        Button buttonCancel = view.findViewById(R.id.buttonActionDialogLeft);
        buttonCancel.setText(getString(R.string.button_text_cancel));
        buttonCancel.setId(Constants.BUTTON_CANCEL);


        Button buttonAccept = view.findViewById(R.id.buttonActionDialogRight);
        buttonAccept.setText(getString(R.string.button_text_accept));
        buttonAccept.setId(Constants.BUTTON_ACCEPT);
    }

    private void addContentButtons(View view)
    {
        LinearLayout linearLayoutContentContainer = view.findViewById(R.id.linearLayoutContentContainer);

        ScrollView scrollView = view.findViewById(R.id.scrollViewContentContainer);

        for(Element element : this.elementsToSort)
        {
            View buttonView = getLayoutInflater().inflate(R.layout.button_standard, linearLayoutContentContainer, false);

            Button button = buttonView.findViewById(R.id.button);

            if(this.selectedElement.equals(element))
            {
                button.setText(getString(R.string.button_text_selected_element, element.getName()));

//                button.setText(element.getName());
//                button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                this.focusOnView(scrollView, buttonView);
            }
            else
            {
                button.setText(element.getName());
            }

            button.setTag(element);
            button.setOnClickListener(this);

            linearLayoutContentContainer.addView(buttonView);
        }
    }

    private void focusOnView(final ScrollView scrollView, final View view)
    {
        new Handler().post(new Runnable()
        {
            @Override
            public void run()
            {
                int vTop = view.getTop();
                int vBottom = view.getBottom();
                int sWidth = scrollView.getWidth();
                scrollView.smoothScrollTo(0, (vTop + vBottom - sWidth) / 2);
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        @SuppressWarnings("unchecked")
        Element element = (Element) view.getTag();

        this.selectedElement = element;

        this.refreshViews();
    }
}
