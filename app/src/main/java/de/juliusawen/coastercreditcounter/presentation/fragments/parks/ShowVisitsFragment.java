package de.juliusawen.coastercreditcounter.presentation.fragments.parks;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.data.Park;
import de.juliusawen.coastercreditcounter.data.Visit;
import de.juliusawen.coastercreditcounter.data.YearHeader;
import de.juliusawen.coastercreditcounter.data.requests.GetContentRecyclerAdapterRequest;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.presentation.recycler.ContentRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class ShowVisitsFragment extends Fragment
{
    private Park park;
    private ContentRecyclerAdapter contentRecyclerAdapter;

    public ShowVisitsFragment() {}

    public static ShowVisitsFragment newInstance(String parkUuid)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowVisitsFragment.newInstance:: instantiating fragment...");

        ShowVisitsFragment showVisitsFragment = new ShowVisitsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuid);
        showVisitsFragment.setArguments(args);

        return showVisitsFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onCreateView:: creating view...");
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            this.park = (Park) App.content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
        }

        this.createContentRecyclerAdapter();
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onCreateView:: creating view...");
        return inflater.inflate(R.layout.tab_show_park_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onViewCreated:: decorating view...");

        if(this.contentRecyclerAdapter != null)
        {
            Log.d(Constants.LOG_TAG, "ShowVisitsFragment.onViewCreated:: creating RecyclerView...");

            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTabShowPark_Visits);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(this.contentRecyclerAdapter);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ShowVisitsFragment.onViewCreated:: VisitsRecyclerAdapter not set");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onResume:: updating RecyclerView");
        this.updateContentRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.onOptionItemSelected:: [%s] selected", selection));

        switch(selection)
        {
            case SORT_ASCENDING:
                Visit.setSortOrder(SortOrder.ASCENDING);
                this.updateContentRecyclerView();
                return true;

            case SORT_DESCENDING:
                Visit.setSortOrder(SortOrder.DESCENDING);
                this.updateContentRecyclerView();
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
        this.contentRecyclerAdapter = null;
    }

    private void createContentRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                ActivityTool.startActivityShow(getActivity(), (Element) view.getTag());
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                return false;
            }
        };

        GetContentRecyclerAdapterRequest request = new GetContentRecyclerAdapterRequest();
        request.childrenByParents = this.getSortedVisitsByYearHeaders(this.park);
        request.onChildClickListener = recyclerOnClickListener;
        request.parentsAreExpandable = true;

        this.contentRecyclerAdapter = new ContentRecyclerAdapter(request);
    }

    private void updateContentRecyclerView()
    {
        if(this.park.getChildCountOfInstance(Visit.class) > 0)
        {
            LinkedHashMap<Element, List<Element>> sortedVisitsByYearHeaders = this.getSortedVisitsByYearHeaders(this.park);
            this.expandLatestYearHeaderAccordingToSettings(new ArrayList<>(sortedVisitsByYearHeaders.keySet()));
            this.contentRecyclerAdapter.updateDataSet(sortedVisitsByYearHeaders);
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ShowVisitsFragment.updateContentRecyclerView:: no elements to update");
        }
    }

    private void expandLatestYearHeaderAccordingToSettings(List<Element> yearHeaders)
    {
        if(App.settings.getExpandLatestYearInListByDefault())
        {
            YearHeader latestYearHeader = YearHeader.getLatestYearHeader(yearHeaders);
            Log.v(Constants.LOG_TAG, String.format("ShowVisitsFragment.expandLatestYearHeaderAccordingToSettings:: expanding latest %s according to settings", latestYearHeader));
            this.contentRecyclerAdapter.expandParent(latestYearHeader);
        }
    }

    private LinkedHashMap<Element, List<Element>> getSortedVisitsByYearHeaders(Park park)
    {
        return YearHeader.getVisitsByYearHeaders(Visit.convertToVisits(Visit.sortVisitsByDateAccordingToSortOrder(park.getChildrenOfInstance(Visit.class))));
    }
}