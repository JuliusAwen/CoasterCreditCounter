package de.juliusawen.coastercreditcounter.presentation.activities.manageLocations;

import android.content.Intent;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.Toolbox.ViewTool;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.baseRecycler.BaseRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.baseRecycler.BaseRecyclerTouchListener;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;

public class SortElementsActivity extends AppCompatActivity implements HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Element currentElement;
    private String subtitle;

    private List<Element> elementsToSort;

    private BaseRecyclerAdapter baseRecyclerAdapter;
    private RecyclerView recyclerView;
    private HelpOverlayFragment helpOverlayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort_elements_activity);

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        this.currentElement = Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
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
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutSortElements);
        View sortElementsView = getLayoutInflater().inflate(R.layout.sort_elements_layout, frameLayoutActivity, false);
        frameLayoutActivity.addView(sortElementsView);

        this.createToolbar(sortElementsView);
        this.createActionDialog(sortElementsView);
        this.createContentRecyclerView(sortElementsView);
        this.createFloatingActionButton();
        this.createHelpOverlayFragment(frameLayoutActivity.getId());
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        menu.add(0, Constants.SELECTION_SORT_A_TO_Z, Menu.NONE, R.string.selection_sort_a_to_z);
        menu.add(0, Constants.SELECTION_HELP, Menu.NONE, R.string.selection_help);

        return super.onPrepareOptionsMenu(menu);
    }

    private void createActionDialog(View view)
    {
        ImageButton buttonDown = view.findViewById(R.id.buttonActionDialogUpDown_MoveSelectionDown);
        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_downward));
        buttonDown.setImageDrawable(drawable);
        buttonDown.setId(Constants.BUTTON_MOVE_SELECTION_DOWN);
        buttonDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(baseRecyclerAdapter.selectedElement != null)
                {
                    int position = elementsToSort.indexOf(baseRecyclerAdapter.selectedElement);

                    if(position < elementsToSort.size() - 1)
                    {
                        Collections.swap(elementsToSort, position, position + 1);
                        baseRecyclerAdapter.notifyDataSetChanged();

                        int scrollMargin = ViewTool.getScrollMarginForRecyclerView(recyclerView);
                        if(elementsToSort.size() > position + 1 + scrollMargin)
                        {
                            recyclerView.smoothScrollToPosition(position + 1 + scrollMargin);
                        }
                        else
                        {
                            recyclerView.smoothScrollToPosition(elementsToSort.size() - 1);
                        }
                    }
                }
            }
        });

        ImageButton buttonUp = view.findViewById(R.id.buttonActionDialogUpDown_MoveSelectionUp);
        drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_upward));
        buttonUp.setImageDrawable(drawable);
        buttonUp.setId(Constants.BUTTON_MOVE_SELECTION_UP);
        buttonUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(baseRecyclerAdapter.selectedElement != null)
                {
                    int position = elementsToSort.indexOf(baseRecyclerAdapter.selectedElement);

                    if(position > 0)
                    {
                        Collections.swap(elementsToSort, position, position - 1);
                        baseRecyclerAdapter.notifyDataSetChanged();

                        int scrollMargin = ViewTool.getScrollMarginForRecyclerView(recyclerView);;
                        if(position - 1 - scrollMargin >= 0)
                        {
                            recyclerView.smoothScrollToPosition(position - 1 - scrollMargin);
                        }
                        else
                        {
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }
                }
            }
        });
    }

    private void createContentRecyclerView(View view)
    {
        this.recyclerView = view.findViewById(R.id.recyclerViewSortElements);
        this.baseRecyclerAdapter = new BaseRecyclerAdapter(this.elementsToSort);

        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.recyclerView.addOnItemTouchListener(new BaseRecyclerTouchListener(getApplicationContext(), recyclerView, new BaseRecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                if(view.isSelected())
                {
                    baseRecyclerAdapter.selectedElement = null;
                    baseRecyclerAdapter.selectedView = null;
                }
                else
                {
                    if(baseRecyclerAdapter.selectedView != null)
                    {
                        baseRecyclerAdapter.selectedView.setSelected(false);
                    }

                    baseRecyclerAdapter.selectedElement = (Element) view.getTag();
                    baseRecyclerAdapter.selectedView = view;
                }

                view.setSelected(!view.isSelected());
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));

        this.recyclerView.setAdapter(this.baseRecyclerAdapter);
    }

    private void createFloatingActionButton()
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonSortElements);

        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check));
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

                Intent intent = new Intent();
                if(baseRecyclerAdapter.selectedElement != null)
                {
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, baseRecyclerAdapter.selectedElement.getUuid().toString());
                    setResult(RESULT_OK, intent);
                }
                else
                {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
    }

    private void setFloatingActionButtonVisibility(boolean isVisible)
    {
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButtonSortElements);

        floatingActionButton.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void createHelpOverlayFragment(int frameLayoutId)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        this.helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_sort_elements), false);
        fragmentTransaction.add(frameLayoutId, this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().getUuidStringsFromElements(this.elementsToSort));
        outState.putString(Constants.KEY_CURRENT_ELEMENT, this.currentElement.getUuid().toString());

        if(this.baseRecyclerAdapter.selectedElement == null)
        {
            outState.putString(Constants.KEY_SELECTED_ELEMENT, "");
        }
        else
        {
            outState.putString(Constants.KEY_SELECTED_ELEMENT, this.baseRecyclerAdapter.selectedElement.getUuid().toString());
        }

        outState.putBoolean(Constants.KEY_HELP_VISIBLE, this.helpOverlayFragment.isVisible());
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
            this.baseRecyclerAdapter.selectedElement = null;
        }
        else
        {
            this.baseRecyclerAdapter.selectedElement = Content.getInstance().getElementByUuid(UUID.fromString(selectedElementString));
            this.recyclerView.smoothScrollToPosition(elementsToSort.indexOf(baseRecyclerAdapter.selectedElement));
        }

        this.helpOverlayFragment.setVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));
        this.setFloatingActionButtonVisibility(!savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));

        this.baseRecyclerAdapter.updateList(this.elementsToSort);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == Constants.SELECTION_SORT_A_TO_Z)
        {
            Collections.sort(this.elementsToSort, new Comparator<Element>()
            {
                @Override
                public int compare(Element element1, Element element2)
                {
                    return element1.getName().compareToIgnoreCase(element2.getName());
                }
            });

            this.baseRecyclerAdapter.updateList(this.elementsToSort);
            this.baseRecyclerAdapter.notifyDataSetChanged();
            return true;
        }
        else if(item.getItemId() == Constants.SELECTION_HELP)
        {
            this.helpOverlayFragment.setVisibility(true);
            this.setFloatingActionButtonVisibility(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHelpOverlayFragmentInteraction(View view)
    {
        if(view.getId() == Constants.BUTTON_CLOSE)
        {
            this.helpOverlayFragment.setVisibility(false);
            this.setFloatingActionButtonVisibility(true);
        }
    }

}
