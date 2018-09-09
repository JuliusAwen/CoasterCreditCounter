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

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.presentation.activities.elements.SortElementsActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

import static de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity.content;

public  class ShowParkAttractionsFragment extends Fragment
{
    private ExpandableRecyclerAdapter expandableRecyclerAdapter;

    public ShowParkAttractionsFragment() {}

    public static ShowParkAttractionsFragment newInstance()
    {
        Log.i(Constants.LOG_TAG, "ShowParkAttractionsFragment.newInstance:: creating fragment");

        return new ShowParkAttractionsFragment();
    }

    public void setExpandableRecyclerAdapter(ExpandableRecyclerAdapter expandableRecyclerAdapter)
    {
        this.expandableRecyclerAdapter = expandableRecyclerAdapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkAttractionsFragment.onCreateView:: creating view...");
        View rootView = inflater.inflate(R.layout.tab_show_park_attractions, container, false);

        if(this.expandableRecyclerAdapter != null)
        {
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewTabShowPark_Attractions);
            recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            recyclerView.setAdapter(this.expandableRecyclerAdapter);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ShowParkVisitsFragment.onCreateView:: ExpandedRecyclerAdapter not set");
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.onOptionItemSelected:: [%S] selected", selection));

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
        this.expandableRecyclerAdapter = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(requestCode == Constants.REQUEST_SORT_ELEMENTS)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Log.d(Constants.LOG_TAG, "ShowParkVisitsFragment.onActivityResult<SortElements>:: replacing children with sorted children");

                List<String> resultElementsUuidStrings = data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS);
                List<Element> resultElements = content.fetchElementsFromUuidStrings(resultElementsUuidStrings);
                Element parentElement = resultElements.get(0).getParent();

                parentElement.deleteChildren(resultElements);
                parentElement.addChildren(resultElements);
                this.expandableRecyclerAdapter.updateList(resultElements);

                String selectedElementUuidString = data.getStringExtra(Constants.EXTRA_ELEMENT_UUID);
                if(selectedElementUuidString != null)
                {
                    Element selectedElement = content.fetchElementFromUuidString(selectedElementUuidString);
                    Log.d(Constants.LOG_TAG, String.format("ShowParkActivity.onActivityResult<SortElements>:: scrolling to selected element %s...", selectedElement));
                    this.expandableRecyclerAdapter.smoothScrollToElement(selectedElement);
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowParkActivity.onActivityResult<SortElements>:: no selected element returned");
                }
            }
        }
    }

    private void startSortElementsActivity(List<Element> elementsToSort)
    {
        Log.i(Constants.LOG_TAG, "ShowParkAttractionsFragment.startSortElementsActivity:: starting SortElementsActivity...");
        Intent intent = new Intent(getContext(), SortElementsActivity.class);
        intent.putStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS, Content.getUuidStringsFromElements(elementsToSort));
        startActivityForResult(intent, Constants.REQUEST_SORT_ELEMENTS);
    }
}