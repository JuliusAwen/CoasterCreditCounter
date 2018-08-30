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
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.SelectableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.fragments.HelpOverlayFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ViewTool;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public class SortElementsActivity extends AppCompatActivity implements HelpOverlayFragment.HelpOverlayFragmentInteractionListener
{
    private Element element;
    private String subtitle;
    private List<Element> elementsToSort;
    private SelectableRecyclerAdapter selectableRecyclerAdapter;
    private RecyclerView recyclerView;
    private HelpOverlayFragment helpOverlayFragment;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_elements);

        this.savedInstanceState = savedInstanceState;

        this.initializeContent();
        this.initializeViews();
    }

    private void initializeContent()
    {
        this.element = Content.getInstance().getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        this.subtitle = element.getName();

        if(this.element.getClass().equals(Location.class))
        {
            this.elementsToSort = new ArrayList<Element>(((Location) this.element).getChildren());
        }
    }

    private void initializeViews()
    {
        FrameLayout frameLayoutActivity = findViewById(R.id.frameLayoutSortElements);
        View sortElementsView = getLayoutInflater().inflate(R.layout.layout_sort_elements, frameLayoutActivity, false);
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
        menu.add(0, Selection.SORT_A_TO_Z.ordinal(), Menu.NONE, R.string.selection_sort_a_to_z);
        menu.add(0, Selection.HELP.ordinal(), Menu.NONE, R.string.selection_help);

        return super.onPrepareOptionsMenu(menu);
    }

    private void createActionDialog(View view)
    {
        ImageButton buttonDown = view.findViewById(R.id.buttonActionDialogUpDown_MoveSelectionDown);
        Drawable drawable = DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_arrow_downward));
        buttonDown.setImageDrawable(drawable);
        buttonDown.setId(ButtonFunction.MOVE_SELECTION_DOWN.ordinal());
        buttonDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    int position = elementsToSort.indexOf(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0));

                    if(position < elementsToSort.size() - 1)
                    {
                        Collections.swap(elementsToSort, position, position + 1);
                        selectableRecyclerAdapter.notifyDataSetChanged();

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
        buttonUp.setId(ButtonFunction.MOVE_SELECTION_UP.ordinal());
        buttonUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    int position = elementsToSort.indexOf(selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0));

                    if(position > 0)
                    {
                        Collections.swap(elementsToSort, position, position - 1);
                        selectableRecyclerAdapter.notifyDataSetChanged();

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
        this.selectableRecyclerAdapter = new SelectableRecyclerAdapter(this.elementsToSort, false);

        this.recyclerView = view.findViewById(R.id.recyclerViewSortElements);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(this.selectableRecyclerAdapter);
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
                if(element.getClass().equals(Location.class))
                {
                    ((Location) element).setChildren(new ArrayList<>(Content.getInstance().convertElementsToLocations(elementsToSort)));
                }

                Intent intent = new Intent();
                if(!selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0).getUuid().toString());
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
        if(this.savedInstanceState == null)
        {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            this.helpOverlayFragment = HelpOverlayFragment.newInstance(getText(R.string.help_text_add_location), false);
            fragmentTransaction.add(frameLayoutId, this.helpOverlayFragment, Constants.FRAGMENT_TAG_HELP_OVERLAY);
            fragmentTransaction.commit();
        }
        else
        {
            this.helpOverlayFragment = (HelpOverlayFragment) getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_TAG_HELP_OVERLAY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.KEY_ELEMENTS, Content.getInstance().getUuidStringsFromElements(this.elementsToSort));
        outState.putString(Constants.KEY_ELEMENT, this.element.getUuid().toString());

        if(this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
        {
            outState.putString(Constants.KEY_SELECTED_ELEMENT, "");
        }
        else
        {
            outState.putString(Constants.KEY_SELECTED_ELEMENT, selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().get(0).getUuid().toString());
        }

        outState.putBoolean(Constants.KEY_HELP_VISIBLE, this.helpOverlayFragment.isVisible());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        this.elementsToSort = Content.getInstance().getElementsFromUuidStrings(savedInstanceState.getStringArrayList(Constants.KEY_ELEMENTS));
        this.element = Content.getInstance().getElementByUuid(UUID.fromString(savedInstanceState.getString(Constants.KEY_ELEMENT)));

        String selectedElementString = savedInstanceState.getString(Constants.KEY_SELECTED_ELEMENT);

        if(selectedElementString != null && selectedElementString.isEmpty())
        {
            this.selectableRecyclerAdapter.getSelectedElementsInOrderOfSelection().clear();
        }
        else
        {
            Element element = Content.getInstance().getElementByUuid(UUID.fromString(selectedElementString));
            this.selectableRecyclerAdapter.selectElement(element);
            this.recyclerView.smoothScrollToPosition(elementsToSort.indexOf(element));
        }

        this.helpOverlayFragment.setVisibility(savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));
        this.setFloatingActionButtonVisibility(!savedInstanceState.getBoolean(Constants.KEY_HELP_VISIBLE));

        this.selectableRecyclerAdapter.updateList(this.elementsToSort);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        switch (selection)
        {
            case SORT_A_TO_Z:
                Collections.sort(this.elementsToSort, new Comparator<Element>()
                {
                    @Override
                    public int compare(Element element1, Element element2)
                    {
                        return element1.getName().compareToIgnoreCase(element2.getName());
                    }
                });

                this.selectableRecyclerAdapter.updateList(this.elementsToSort);
                return true;

            case HELP:
                this.helpOverlayFragment.setVisibility(true);
                this.setFloatingActionButtonVisibility(false);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHelpOverlayFragmentInteraction(View view)
    {
        ButtonFunction buttonFunction = ButtonFunction.values()[view.getId()];
        switch (buttonFunction)
        {
            case CLOSE:
                this.helpOverlayFragment.setVisibility(false);
                this.setFloatingActionButtonVisibility(true);
                break;
        }
    }
}
