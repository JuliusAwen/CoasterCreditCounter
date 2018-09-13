package de.juliusawen.coastercreditcounter.presentation.fragments.parks;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.AttractionCategory;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public  class ShowParkAttractionsFragment extends Fragment
{
    private Park park;
    private ExpandableRecyclerAdapter attractionsRecyclerAdapter;

    public ShowParkAttractionsFragment() {}

    public static ShowParkAttractionsFragment newInstance(String parkUuid)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowParkAttractionsFragment.newInstance:: instantiating fragment...");

        ShowParkAttractionsFragment showParkAttractionsFragment =  new ShowParkAttractionsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuid);
        showParkAttractionsFragment.setArguments(args);

        return showParkAttractionsFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);

        if (getArguments() != null)
        {
            this.park = (Park) App.content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
        }

        this.createAttractionsRecyclerAdapter();

        Log.v(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onCreate:: created fragment for %s", this.park));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.onCreateView:: creating view...");
        return inflater.inflate(R.layout.tab_show_park_attractions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.onViewCreated:: decorating view...");

        if(this.attractionsRecyclerAdapter != null)
        {
            Log.d(Constants.LOG_TAG, "ShowParkAttractionsFragment.onViewCreated:: creating RecyclerView...");

            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTabShowPark_Attractions);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(this.attractionsRecyclerAdapter);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ShowParkAttractionsFragment.onViewCreated:: AttractionsRecyclerAdapter not set");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.onResume:: updating RecyclerView");
        this.updateAttractionsRecyclerView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case SORT_ATTRACTION_CATEGORIES:
                ActivityTool.startActivitySort(
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
        this.attractionsRecyclerAdapter = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
            Element selectedElement = null;
            if(selectedElementUuidString != null)
            {
                selectedElement = App.content.fetchElementByUuidString(selectedElementUuidString);
                Log.d(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onActivityResult<OK>:: selected element %s returned", selectedElement));
            }
            else
            {
                Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.onActivityResult<OK>:: no selected element returned");
            }

            List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
            List<Element> resultElements = App.content.fetchElementsByUuidStrings(resultElementsUuidStrings);
            Collections.reverse(resultElements);

            if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS)
            {
                Log.d(Constants.LOG_TAG, "ShowParkAttractionsFragment.onActivityResult<SortAttractions>:: replacing <children> with <sorted children>...");

                Element parent = resultElements.get(0).getParent();
                if(parent != null)
                {
                    this.park.deleteChildren(resultElements);
                    this.park.addChildren(resultElements);
                }

                this.updateAttractionsRecyclerView();

                if(selectedElement != null)
                {
                    Log.d(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onActivityResult<SortAttractions>:: scrolling to selected element %s...", selectedElement));
                    this.attractionsRecyclerAdapter.smoothScrollToElement(((Attraction)selectedElement).getCategory());
                }

            }
            else if(requestCode == Constants.REQUEST_SORT_ATTRACTION_CATEGORIES)
            {
                Attraction.setCategories(AttractionCategory.convertToAttractionCategories(resultElements));
                this.updateAttractionsRecyclerView();

                if(selectedElement != null)
                {
                    Log.d(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onActivityResult<SortAttractionCategory>:: scrolling to selected element %s...", selectedElement));
                    this.attractionsRecyclerAdapter.smoothScrollToElement(selectedElement);
                }
            }
        }
    }

    private void createAttractionsRecyclerAdapter()
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
                onLongClickAttractionsRecyclerView(view);
                return true;
            }
        };

        this.attractionsRecyclerAdapter = new ExpandableRecyclerAdapter(Park.addAttractionCategoryHeaders(this.park.getChildrenOfInstance(Attraction.class)), recyclerOnClickListener);
    }

    private void onLongClickAttractionsRecyclerView(final View view)
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
                    return onClickMenuItemPopupMenuLongClickAttractionsRecyclerView(item, longClickedElement);
                }
            });
            popupMenu.show();
        }
    }

    private boolean onClickMenuItemPopupMenuLongClickAttractionsRecyclerView(MenuItem item, Element longClickedElement)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onClickMenuItemPopupMenuLongClickAttractionsRecyclerView:: [%S] selected", selection));

        switch (selection)
        {
            case EDIT_ATTRACTION_CATEGORY:
                ActivityTool.startActivityEdit(Objects.requireNonNull(getActivity()), longClickedElement);
                return true;

            case SORT_ATTRACTIONS:
                ActivityTool.startActivitySort(
                        Objects.requireNonNull(getActivity()),
                        Constants.REQUEST_SORT_ATTRACTIONS,
                        longClickedElement.getChildrenOfInstance(Attraction.class));
                return true;

            default:
                return false;
        }
    }

    private void updateAttractionsRecyclerView()
    {
        if(this.park.getChildCountOfInstance(Attraction.class) > 0)
        {
            List<Element> preparedAttractions = Park.addAttractionCategoryHeaders(this.park.getChildrenOfInstance(Attraction.class));
            this.expandAttractionsCategoriesAccordingToSettings();
            this.attractionsRecyclerAdapter.updateElements(preparedAttractions);
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.updateAttractionsRecyclerView:: no elements to update");
        }
    }

    private void expandAttractionsCategoriesAccordingToSettings()
    {
        for(AttractionCategory attractionCategory : App.settings.getAttractionCategoriesToExpandByDefault())
        {
            Log.v(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.expandAttractionsCategoriesAccordingToSettings:: expanding #[%s] according to settings...", attractionCategory));
            this.attractionsRecyclerAdapter.expandElement(attractionCategory);
        }
    }
}