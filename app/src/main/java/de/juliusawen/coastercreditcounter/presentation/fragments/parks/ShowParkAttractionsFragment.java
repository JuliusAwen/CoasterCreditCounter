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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.StringTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public  class ShowParkAttractionsFragment extends Fragment
{
    private Park park;
    private ExpandableRecyclerAdapter expandableRecyclerAdapter;

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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTabShowPark_Attractions);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(this.expandableRecyclerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case SORT_ATTRACTION_CATEGORIES:
                this.startSortElementsActivity(new ArrayList<Element>(Attraction.getCategories()));
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
        this.expandableRecyclerAdapter = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
            List<Element> resultElements = App.content.fetchElementsFromUuidStrings(resultElementsUuidStrings);

            if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS)
            {
                Log.d(Constants.LOG_TAG, "ShowParkAttractionsFragment.onActivityResult<SortElements>:: replacing <children> with <sorted children>...");

                Element parent = resultElements.get(0).getParent();
                if(parent != null)
                {
                    this.park.deleteChildren(resultElements);
                    this.park.addChildren(resultElements);
                }

                this.updateExpandableRecyclerView();

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    Element selectedElement = App.content.fetchElementFromUuidString(selectedElementUuidString);
                    Log.d(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onActivityResult<SortElements>:: scrolling to selected element %s...", selectedElement));
                    this.expandableRecyclerAdapter.smoothScrollToElement(selectedElement);
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.onActivityResult<SortElements>:: no selected element returned");
                }
            }
            else if(requestCode == Constants.REQUEST_SORT_ATTRACTION_CATEGORIES)
            {
                Collections.reverse(resultElements);
                Attraction.setCategories(AttractionCategory.convertToAttractionCategories(resultElements));
                this.updateExpandableRecyclerView();
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
            public void onLongClick(final View view, int position)
            {
                Element element = (Element) view.getTag();

                if(element.isInstance(AttractionCategory.class))
                {
                    Toaster.makeToast(getContext(), String.format("SortAttractions not yet implemented %s", (Element) view.getTag()));
                }
            }
        };

        this.expandableRecyclerAdapter = new ExpandableRecyclerAdapter(this.addAttractionCategoryHeaders(this.park.getChildrenOfInstance(Attraction.class)), recyclerOnClickListener);
        this.updateExpandableRecyclerView();
    }

    private List<Element> addAttractionCategoryHeaders(List<Element> elements)
    {
        Log.v(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.addAttractionCategoryHeaders:: adding headers for #[%d] elements...", elements.size()));

        AttractionCategory.removeAllChildren(Attraction.getCategories());

        List<Attraction> attractions = Attraction.convertToAttractions(elements);
        List<Element> preparedElements = new ArrayList<>();

        for(Attraction attraction : attractions)
        {
            Element existingCategory = null;
            for(Element attractionCategory : preparedElements)
            {
                if(attractionCategory.equals(attraction.getCategory()))
                {
                    existingCategory = attractionCategory;
                }
            }

            if(existingCategory != null)
            {
                existingCategory.addChild(attraction);
            }
            else
            {
                Element attractionCategoryHeader = attraction.getCategory();
                attractionCategoryHeader.addChild(attraction);
                preparedElements.add(attractionCategoryHeader);
            }
        }

        preparedElements = Element.sortElementsBasedOnComparisonList(preparedElements, new ArrayList<Element>(Attraction.getCategories()));

        Log.d(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.addAttractionCategoryHeaders:: #[%d] headers added", preparedElements.size()));
        return preparedElements;
    }

    private void updateExpandableRecyclerView()
    {
        List<Element> preparedAttractions = this.addAttractionCategoryHeaders(this.park.getChildrenOfInstance(Attraction.class));
        this.expandAttractionsCategoriesAccordingToSettings();
        this.expandableRecyclerAdapter.updateElements(preparedAttractions);
    }

    private void expandAttractionsCategoriesAccordingToSettings()
    {
        for(AttractionCategory attractionCategory : App.settings.getAttractionCategoriesToExpandByDefault())
        {
            Log.e(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.expandAttractionsCategoriesAccordingToSettings:: expanding #[%s] according to settings...", attractionCategory));
            this.expandableRecyclerAdapter.expandElement(attractionCategory);
        }
    }

    private void startSortElementsActivity(List<Element> elementsToSort)
    {
        Intent intent = new Intent(getContext(), SortElementsActivity.class);
        intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
        Log.i(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.startSortElementsActivity:: starting activty [%s]...",
                StringTool.parseActivityName(Objects.requireNonNull(intent.getComponent()).getShortClassName())));

        startActivityForResult(intent, Constants.REQUEST_SORT_ATTRACTION_CATEGORIES);
    }
}