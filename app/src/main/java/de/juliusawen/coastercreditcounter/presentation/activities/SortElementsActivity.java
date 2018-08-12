package de.juliusawen.coastercreditcounter.presentation.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.presentation.RecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.RecyclerViewTouchListener;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpFragment;

public class SortElementsActivity extends AppCompatActivity implements View.OnClickListener, HelpFragment.OnFragmentInteractionListener
{
    private Element currentElement;
    private String subtitle;

    private List<Element> elementsToSort;

    private View sortElementsView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    private boolean helpVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_elements);

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        this.currentElement = Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_UUID)));
        this.subtitle = currentElement.getName();

        if(currentElement.getClass().equals(Location.class))
        {
            this.elementsToSort = new ArrayList<Element>(((Location) currentElement).getChildren());
        }
        else if(currentElement.getClass().equals(Park.class))
        {
            this.elementsToSort = new ArrayList<Element>(((Park) currentElement).getAttractions());
        }
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayout_sortElements);
        this.sortElementsView = getLayoutInflater().inflate(R.layout.layout_sort_elements, frameLayoutActivity, false);
        frameLayoutActivity.addView(sortElementsView);

        this.createToolbar(sortElementsView);
        this.createActionDialogTop(sortElementsView);
        this.createContentRecyclerView(sortElementsView);
        this.createActionDialogBottom(sortElementsView);

        this.createHelpFragment();
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_sort_elements));
        toolbar.setSubtitle(this.subtitle);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.options_menu_sort_elements_items, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void createActionDialogTop(View view)
    {
        ImageButton buttonDown = view.findViewById(R.id.imageButton_actionDialogLeft);
        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_downward_24px));
        buttonDown.setImageDrawable(drawable);
        buttonDown.setId(Constants.BUTTON_DOWN);
        buttonDown.setOnClickListener(this);

        ImageButton buttonUp = view.findViewById(R.id.imageButton_actionDialogRight);
        drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_upward_24px));
        buttonUp.setImageDrawable(drawable);
        buttonUp.setId(Constants.BUTTON_UP);
        buttonUp.setOnClickListener(this);
    }

    private void createActionDialogBottom(View view)
    {
        ImageButton buttonCancel = view.findViewById(R.id.imageButton_actionDialogLeft);
        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_close_24px));
        buttonCancel.setImageDrawable(drawable);
        buttonCancel.setId(Constants.BUTTON_CANCEL);
        buttonCancel.setOnClickListener(this);


        ImageButton buttonAccept = view.findViewById(R.id.imageButton_actionDialogRight);
        drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check_24px));
        buttonAccept.setImageDrawable(drawable);
        buttonAccept.setId(Constants.BUTTON_ACCEPT);
        buttonAccept.setOnClickListener(this);
    }

    private void createContentRecyclerView(View view)
    {
        this.recyclerView = view.findViewById(R.id.recyclerView_content);
        this.recyclerViewAdapter = new RecyclerViewAdapter(this.elementsToSort);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recyclerView, new RecyclerViewTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                if(view.isSelected())
                {
                    SortElementsActivity.this.recyclerViewAdapter.selectedElement = null;
                    SortElementsActivity.this.recyclerViewAdapter.selectedView = null;
                }
                else
                {
                    if(SortElementsActivity.this.recyclerViewAdapter.selectedView != null)
                    {
                        SortElementsActivity.this.recyclerViewAdapter.selectedView.setSelected(false);
                    }

                    SortElementsActivity.this.recyclerViewAdapter.selectedElement = (Element) view.getTag();
                    SortElementsActivity.this.recyclerViewAdapter.selectedView = view;
                }

                view.setSelected(!view.isSelected());
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));

        this.recyclerView.setAdapter(this.recyclerViewAdapter);
    }

    private void createHelpFragment()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HelpFragment helpFragment = HelpFragment.newInstance(getText(R.string.help_text_sort_elements), false);
        fragmentTransaction.add(R.id.frameLayout_sortElements, helpFragment, Constants.FRAGMENT_TAG_HELP);
        fragmentTransaction.commit();

        this.helpVisible = false;
    }

    private void setHelpVisibility(boolean active)
    {
        HelpFragment helpFragment = (HelpFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP);

        if(active)
        {
            helpFragment.fragmentView.setVisibility(View.VISIBLE);
        }
        else
        {
            helpFragment.fragmentView.setVisibility(View.INVISIBLE);
        }

        this.helpVisible = active;
    }

    @Override
    public void onFragmentInteraction(View view)
    {
        if(view.getId() == Constants.BUTTON_CLOSE_HELP_SCREEN)
        {
            this.setHelpVisibility(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().getUuidStringsFromElements(this.elementsToSort));
        outState.putString(Constants.KEY_CURRENT_ELEMENT, this.currentElement.getUuid().toString());

        if(this.recyclerViewAdapter.selectedElement == null)
        {
            outState.putString(Constants.KEY_SELECTED_ELEMENT, "");
        }
        else
        {
            outState.putString(Constants.KEY_SELECTED_ELEMENT, this.recyclerViewAdapter.selectedElement.getUuid().toString());
        }


        outState.putBoolean(Constants.KEY_HELP_ACTIVE, this.helpVisible);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.elementsToSort = Content.getInstance().getElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.currentElement = Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_CURRENT_ELEMENT)));

        String selectedElementString = savedInstanceState.getString(Constants.KEY_SELECTED_ELEMENT);

        if(selectedElementString != null && selectedElementString.isEmpty())
        {
            this.recyclerViewAdapter.selectedElement = null;
        }
        else
        {
            this.recyclerViewAdapter.selectedElement = Content.getInstance().getElementByUuid(UUID.fromString(selectedElementString));
        }

        this.setHelpVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_ACTIVE));

        this.recyclerViewAdapter.updateList(this.elementsToSort);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == Constants.BUTTON_UP || view.getId() == Constants.BUTTON_DOWN)
        {
            if(this.recyclerViewAdapter.selectedElement != null)
            {
                int position = this.elementsToSort.indexOf(this.recyclerViewAdapter.selectedElement);

                if(view.getId() == Constants.BUTTON_UP && position > 0)
                {
                    Collections.swap(this.elementsToSort, position, position - 1);
                    this.recyclerView.smoothScrollToPosition(position - 1);
                    this.recyclerViewAdapter.notifyDataSetChanged();
                }
                else if(view.getId() == Constants.BUTTON_DOWN && position < this.elementsToSort.size() - 1)
                {
                    Collections.swap(this.elementsToSort, position, position + 1);
                    this.recyclerView.smoothScrollToPosition(position + 1);
                    this.recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        }
        else if(view.getId() == Constants.BUTTON_ACCEPT)
        {
            if(currentElement.getClass().equals(Location.class))
            {
                ((Location) currentElement).setChildren(new ArrayList<>(Content.getInstance().convertElementsToLocations(this.elementsToSort)));
            }
            else if(currentElement.getClass().equals(Park.class))
            {
                ((Park) currentElement).setAttractions(new ArrayList<>(Content.getInstance().convertElementsToAttractions(this.elementsToSort)));
            }

            finish();
        }
        else if (view.getId() == Constants.BUTTON_CANCEL)
        {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.optionsMenuHelp:
            {
                this.setHelpVisibility(true);
            }

            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
