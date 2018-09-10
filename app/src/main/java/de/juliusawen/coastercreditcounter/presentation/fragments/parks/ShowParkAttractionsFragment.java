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

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

import static de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity.content;

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
            this.park = (Park) content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
        }

        this.createAttractionsRecyclerAdapter();

        Log.d(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onCreate:: created fragment for %s", this.park));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, "ShowParkAttractionsFragment.onCreateView:: creating view...");
        return inflater.inflate(R.layout.tab_show_park_attractions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, "ShowParkAttractionsFragment.onViewCreated:: decorating view...");

        if(this.expandableRecyclerAdapter != null)
        {
            Log.d(Constants.LOG_TAG, "ShowParkAttractionsFragment.onViewCreated:: creating RecyclerView...");

            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTabShowPark_Attractions);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(this.expandableRecyclerAdapter);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ShowParkAttractionsFragment.onViewCreated:: ExpandedRecyclerAdapter not set");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case SORT_ELEMENTS:
                this.startSortElementsActivity(this.expandableRecyclerAdapter.getElements());
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

        if(requestCode == Constants.REQUEST_SORT_ELEMENTS)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Log.d(Constants.LOG_TAG, "ShowParkAttractionsFragment.onActivityResult<SortElements>:: replacing <children> with <sorted children>...");

                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> resultElements = content.fetchElementsFromUuidStrings(resultElementsUuidStrings);

                this.park.deleteChildren(resultElements);
                this.park.addChildren(resultElements);
                this.expandableRecyclerAdapter.updateElements(resultElements);

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    Element selectedElement = content.fetchElementFromUuidString(selectedElementUuidString);
                    Log.d(Constants.LOG_TAG, String.format("ShowParkAttractionsFragment.onActivityResult<SortElements>:: scrolling to selected element %s...", selectedElement));
                    this.expandableRecyclerAdapter.smoothScrollToElement(selectedElement);
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.onActivityResult<SortElements>:: no selected element returned");
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
                Toaster.makeToast(getContext(), String.format("ShowAttractions not yet implemented %s", (Element) view.getTag()));
            }

            @Override
            public void onLongClick(final View view, int position)
            {
            }
        };

        this.expandableRecyclerAdapter = new ExpandableRecyclerAdapter(this.park.getChildrenOfInstance(Attraction.class), recyclerOnClickListener);
    }

    private void startSortElementsActivity(List<Element> elementsToSort)
    {
        Log.i(Constants.LOG_TAG, "ShowParkAttractionsFragment.startSortElementsActivity:: starting SortElementsActivity...");
        Intent intent = new Intent(getContext(), SortElementsActivity.class);
        intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
        startActivityForResult(intent, Constants.REQUEST_SORT_ELEMENTS);
    }
}