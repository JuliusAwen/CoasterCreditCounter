package de.juliusawen.coastercreditcounter.presentation.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import java.util.Objects;
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
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public class SortElementsActivity extends AppCompatActivity implements HelpOverlayFragment.OnFragmentInteractionListener
{
    private Element currentElement;
    private String subtitle;

    private List<Element> elementsToSort;

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    private boolean helpOverlayVisible;

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

        if(this.currentElement.getClass().equals(Location.class))
        {
            this.elementsToSort = new ArrayList<Element>(((Location) this.currentElement).getChildren());
        }
        else if(this.currentElement.getClass().equals(Park.class))
        {
            this.elementsToSort = new ArrayList<Element>(((Park) this.currentElement).getAttractions());
        }
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayout_sortElements);
        View sortElementsView = getLayoutInflater().inflate(R.layout.layout_sort_elements, frameLayoutActivity, false);
        frameLayoutActivity.addView(sortElementsView);

        this.createToolbar(sortElementsView);
        this.createActionDialog(sortElementsView);
        this.createContentRecyclerView(sortElementsView);

        this.createFloatingActionButton();
        this.createHelpOverlayFragment();
    }

    private void createToolbar(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_sort_elements));
        toolbar.setSubtitle(this.subtitle);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.options_menu_help_only_items, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void createActionDialog(View view)
    {
        ImageButton buttonDown = view.findViewById(R.id.imageButton_actionDialogLeft);
        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_downward_24px));
        buttonDown.setImageDrawable(drawable);
        buttonDown.setId(Constants.BUTTON_DOWN);
        buttonDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(recyclerViewAdapter.selectedElement != null)
                {
                    int position = elementsToSort.indexOf(recyclerViewAdapter.selectedElement);

                    if(position < elementsToSort.size() - 1)
                    {
                        Collections.swap(elementsToSort, position, position + 1);
                        recyclerView.smoothScrollToPosition(position + 1);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        ImageButton buttonUp = view.findViewById(R.id.imageButton_actionDialogRight);
        drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_upward_24px));
        buttonUp.setImageDrawable(drawable);
        buttonUp.setId(Constants.BUTTON_UP);
        buttonUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(recyclerViewAdapter.selectedElement != null)
                {
                    int position = elementsToSort.indexOf(recyclerViewAdapter.selectedElement);

                    if(position > 0)
                    {
                        Collections.swap(elementsToSort, position, position - 1);
                        recyclerView.smoothScrollToPosition(position - 1);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void createContentRecyclerView(View view)
    {
        this.recyclerView = view.findViewById(R.id.recyclerViewSortElements);
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
                    recyclerViewAdapter.selectedElement = null;
                    recyclerViewAdapter.selectedView = null;
                }
                else
                {
                    if(recyclerViewAdapter.selectedView != null)
                    {
                        recyclerViewAdapter.selectedView.setSelected(false);
                    }

                    recyclerViewAdapter.selectedElement = (Element) view.getTag();
                    recyclerViewAdapter.selectedView = view;
                }

                view.setSelected(!view.isSelected());
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));

        this.recyclerView.setAdapter(this.recyclerViewAdapter);
    }

    private void createFloatingActionButton()
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonSortElements);

        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check_24px));
        floatingActionButton.setImageDrawable(drawable);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(currentElement.getClass().equals(Location.class))
                {
                    ((Location) currentElement).setChildren(new ArrayList<>(Content.getInstance().convertElementsToLocations(elementsToSort)));
                }
                else if(currentElement.getClass().equals(Park.class))
                {
                    ((Park) currentElement).setAttractions(new ArrayList<>(Content.getInstance().convertElementsToAttractions(elementsToSort)));
                }

                finish();
            }
        });
    }

    private void setFloatingActionButtonVisibility(boolean isVisible)
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonSortElements);

        if(isVisible)
        {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
        else
        {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
    }

    private void createHelpOverlayFragment()
    {
        this.helpOverlayVisible = false;

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HelpOverlayFragment helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_sort_elements), this.helpOverlayVisible);
        fragmentTransaction.add(R.id.frameLayout_sortElements, helpOverlayFragment, Constants.FRAGMENT_TAG_HELP);
        fragmentTransaction.commit();
    }

    private void setHelpOverlayFragmentVisibility(boolean isVisible)
    {
        HelpOverlayFragment helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP);

        if(isVisible)
        {
            helpOverlayFragment.fragmentView.setVisibility(View.VISIBLE);
        }
        else
        {
            helpOverlayFragment.fragmentView.setVisibility(View.INVISIBLE);
        }

        this.setFloatingActionButtonVisibility(!isVisible);
        this.helpOverlayVisible = isVisible;
    }

    @Override
    public void onFragmentInteraction(View view)
    {
        if(view.getId() == Constants.BUTTON_CLOSE)
        {
            this.setHelpOverlayFragmentVisibility(false);
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


        outState.putBoolean(Constants.KEY_HELP_ACTIVE, this.helpOverlayVisible);
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

        this.setHelpOverlayFragmentVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_ACTIVE));

        this.recyclerViewAdapter.updateList(this.elementsToSort);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.optionsMenuHelp:
            {
                this.setHelpOverlayFragmentVisibility(true);
            }

            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
