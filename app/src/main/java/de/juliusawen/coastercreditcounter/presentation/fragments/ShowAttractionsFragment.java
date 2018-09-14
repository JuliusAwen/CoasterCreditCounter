package de.juliusawen.coastercreditcounter.presentation.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Attraction;
import de.juliusawen.coastercreditcounter.data.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.data.Park;
import de.juliusawen.coastercreditcounter.data.Visit;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.recycler.CountableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public  class ShowAttractionsFragment extends Fragment
{
    public boolean isInitialized = false;

    private Park park;
    private Visit visit;

    private RecyclerView recyclerView;
    private ExpandableRecyclerAdapter expandableRecyclerAdapter;
    private CountableRecyclerAdapter countableRecyclerAdapter;

    public ShowAttractionsFragment() {}

    public static ShowAttractionsFragment newInstance(String parkUuid)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowAttractionsFragment.newInstance:: instantiating fragment for <Park>...");

        ShowAttractionsFragment showAttractionsFragment =  new ShowAttractionsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuid);
        showAttractionsFragment.setArguments(args);

        return showAttractionsFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);

        if (getArguments() != null)
        {
            this.park = (Park) App.content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
            this.createExpandableRecyclerAdapter();
            Log.v(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onCreate:: created fragment for %s", this.park));
        }
    }

    public static ShowAttractionsFragment newInstance()
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowAttractionsFragment.newInstance:: instantiating fragment for <Visit> ...");
        return new ShowAttractionsFragment();
    }

    public void initializeForVisit(Visit visit)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.initializeForVisit:: initializing fragment for %s...", visit));
        this.visit = visit;
        this.createCountableRecyclerAdapter();
        this.recyclerView.setAdapter(this.countableRecyclerAdapter);
        this.isInitialized = true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowAttractionsFragment.onCreateView:: creating view...");
        return inflater.inflate(R.layout.fragment_show_attractions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        if(this.recyclerView == null)
        {
            Log.d(Constants.LOG_TAG, "ShowAttractionsFragment.onViewCreated:: creating RecyclerView...");

            this.recyclerView = view.findViewById(R.id.recyclerViewFragmentShowAttractions);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        }

        if(this.expandableRecyclerAdapter != null)
        {
            this.recyclerView.setAdapter(this.expandableRecyclerAdapter);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.park != null)
        {
            Log.v(Constants.LOG_TAG, "ShowAttractionsFragment.onResume:: updating ExpandableRecyclerView...");
            this.updateExpandableRecyclerView();
        }
        else if(this.visit != null)
        {
            Log.v(Constants.LOG_TAG, "ShowAttractionsFragment.onResume:: updating CountableRecyclerView...");
            this.updateCountableRecyclerView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case SORT_ATTRACTION_CATEGORIES:
                ActivityTool.startActivitySortForResult(
                        Objects.requireNonNull(getActivity()),
                        Constants.REQUEST_SORT_ATTRACTION_CATEGORIES,
                        new ArrayList<Element>(Attraction.getCategories()));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.park = null;
        this.visit = null;
        this.recyclerView = null;
        this.expandableRecyclerAdapter = null;
        this.countableRecyclerAdapter = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
            Element selectedElement = null;
            if(selectedElementUuidString != null)
            {
                selectedElement = App.content.fetchElementByUuidString(selectedElementUuidString);
                Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<OK>:: selected element %s returned", selectedElement));
            }
            else
            {
                Log.v(Constants.LOG_TAG, "ShowAttractionsFragment.onActivityResult<OK>:: no selected element returned");
            }

            List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
            List<Element> resultElements = App.content.fetchElementsByUuidStrings(resultElementsUuidStrings);

            if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS)
            {
                Log.d(Constants.LOG_TAG, "ShowAttractionsFragment.onActivityResult<SortAttractions>:: replacing <children> with <sorted children>...");

                Element parent = resultElements.get(0).getParent();
                if(parent != null)
                {
                    this.park.deleteChildren(resultElements);
                    this.park.addChildren(resultElements);
                }

                this.updateExpandableRecyclerView();

                if(selectedElement != null)
                {
                    Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<SortAttractions>:: scrolling to selected element %s...", selectedElement));
                    this.expandableRecyclerAdapter.smoothScrollToElement(((Attraction)selectedElement).getCategory());
                }

            }
            else if(requestCode == Constants.REQUEST_SORT_ATTRACTION_CATEGORIES)
            {
                Attraction.setCategories(AttractionCategory.convertToAttractionCategories(resultElements));
                this.updateExpandableRecyclerView();

                if(selectedElement != null)
                {
                    Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<SortAttractionCategory>:: scrolling to selected element %s...", selectedElement));
                    this.expandableRecyclerAdapter.smoothScrollToElement(selectedElement);
                }
            }
        }
    }

    private void createExpandableRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Element element = (Element) view.getTag();

                if(element.isInstance(Attraction.class))
                {
                    Toaster.makeToast(getContext(), String.format("ShowAttractions not yet implemented %s", (Element) view.getTag()));
                }
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                onLongClickExpandableRecyclerView(view);
                return true;
            }
        };

        this.expandableRecyclerAdapter =
                new ExpandableRecyclerAdapter(AttractionCategory.addAttractionCategoryHeaders(this.park.getChildrenOfInstance(Attraction.class)), recyclerOnClickListener);
    }

    private void onLongClickExpandableRecyclerView(final View view)
    {
        final Element longClickedElement = (Element) view.getTag();

        if(longClickedElement.isInstance(AttractionCategory.class))
        {
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.getMenu().add(0, Selection.EDIT_ATTRACTION_CATEGORY.ordinal(), Menu.NONE, R.string.selection_edit_attraction_category);

            if(longClickedElement.getChildCountOfInstance(Attraction.class) > 1)
            {
                popupMenu.getMenu().add(0, Selection.SORT_ATTRACTIONS.ordinal(), Menu.NONE, R.string.selection_sort_attractions);
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    return onClickMenuItemPopupMenuLongClickExpandableRecyclerView(item, longClickedElement);
                }
            });
            popupMenu.show();
        }
    }

    private boolean onClickMenuItemPopupMenuLongClickExpandableRecyclerView(MenuItem item, Element longClickedElement)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onClickMenuItemPopupMenuLongClickExpandableRecyclerView:: [%S] selected", selection));

        switch (selection)
        {
            case EDIT_ATTRACTION_CATEGORY:
                ActivityTool.startActivityEdit(Objects.requireNonNull(getActivity()), longClickedElement);
                return true;

            case SORT_ATTRACTIONS:
                ActivityTool.startActivitySortForResult(
                        Objects.requireNonNull(getActivity()),
                        Constants.REQUEST_SORT_ATTRACTIONS,
                        longClickedElement.getChildrenOfInstance(Attraction.class));
                return true;

            default:
                return false;
        }
    }

    private void updateExpandableRecyclerView()
    {
        if(this.park.getChildCountOfInstance(Attraction.class) > 0)
        {
            this.expandAttractionsCategoriesAccordingToSettings(this.park.getChildrenOfInstance(Attraction.class));
            List<Element> preparedAttractions = AttractionCategory.addAttractionCategoryHeaders(this.park.getChildrenOfInstance(Attraction.class));
            this.expandableRecyclerAdapter.updateElements(preparedAttractions);
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ShowAttractionsFragment.updateExpandableRecyclerView:: no elements to update");
        }
    }

    private void createCountableRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {

            }

            @Override
            public boolean onLongClick(final View view, int position)
            {

                return true;
            }
        };

        this.countableRecyclerAdapter =
                new CountableRecyclerAdapter(AttractionCategory.addAttractionCategoryHeaders(new ArrayList<>(this.visit.getRideCountByAttraction().keySet())), recyclerOnClickListener);
    }

    public void updateCountableRecyclerView()
    {
        if(this.visit.getAttractions().size() > 0)
        {
            this.expandAttractionsCategoriesAccordingToSettings(this.visit.getAttractions());
            List<Element> preparedAttractions = AttractionCategory.addAttractionCategoryHeaders(this.visit.getAttractions());
            this.countableRecyclerAdapter.updateElements(preparedAttractions);
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ShowAttractionsFragment.updateCountableRecyclerView:: no elements to update");
        }
    }

    private void expandAttractionsCategoriesAccordingToSettings(List<Element> attractions)
    {
        for(AttractionCategory attractionCategory : App.settings.getAttractionCategoriesToExpandByDefault())
        {
            if(Attraction.containsAttractionOfCategory(Attraction.convertToAttractions(attractions), attractionCategory))
            {
                Log.v(Constants.LOG_TAG, String.format("ShowAttractionsFragment.expandAttractionsCategoriesAccordingToSettings:: expanding #[%s] according to settings...", attractionCategory));
                if(this.expandableRecyclerAdapter != null)
                {
                    this.expandableRecyclerAdapter.expandElement(attractionCategory);
                }
                else if(this.countableRecyclerAdapter != null)
                {
                    this.countableRecyclerAdapter.expandElement(attractionCategory);
                }
            }
        }
    }
}