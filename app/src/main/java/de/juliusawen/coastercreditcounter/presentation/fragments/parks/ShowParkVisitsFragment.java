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

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Element;
import de.juliusawen.coastercreditcounter.data.Park;
import de.juliusawen.coastercreditcounter.data.Visit;
import de.juliusawen.coastercreditcounter.data.YearHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.presentation.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class ShowParkVisitsFragment extends Fragment
{
    private Park park;
    private ExpandableRecyclerAdapter visitsRecyclerAdapter;

    public ShowParkVisitsFragment() {}

    public static ShowParkVisitsFragment newInstance(String parkUuid)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowParkVisitsFragment.newInstance:: instantiating fragment...");

        ShowParkVisitsFragment showParkVisitsFragment = new ShowParkVisitsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuid);
        showParkVisitsFragment.setArguments(args);

        return showParkVisitsFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkVisitsFragment.onCreateView:: creating view...");
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            this.park = (Park) App.content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
        }

        this.createVisitsRecyclerAdapter();
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkVisitsFragment.onCreateView:: creating view...");
        return inflater.inflate(R.layout.tab_show_park_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkVisitsFragment.onViewCreated:: decorating view...");

        if(this.visitsRecyclerAdapter != null)
        {
            Log.d(Constants.LOG_TAG, "ShowParkVisitsFragment.onViewCreated:: creating RecyclerView...");

            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTabShowPark_Visits);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(this.visitsRecyclerAdapter);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ShowParkVisitsFragment.onViewCreated:: VisitsRecyclerAdapter not set");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Log.v(Constants.LOG_TAG, "ShowParkVisitsFragment.onResume:: updating RecyclerView");
        this.updateVisitsRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.onOptionItemSelected:: [%s] selected", selection));

        switch(selection)
        {
            case SORT_ASCENDING:
                Visit.setSortOrder(SortOrder.ASCENDING);
                this.updateVisitsRecyclerView();
                return true;

            case SORT_DESCENDING:
                Visit.setSortOrder(SortOrder.DESCENDING);
                this.updateVisitsRecyclerView();
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
        this.visitsRecyclerAdapter = null;
    }

    private void createVisitsRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Element element = (Element) view.getTag();
                if(element.isInstance(Visit.class))
                {
                    ActivityTool.startActivityShow(getActivity(), element);
                }
            }

            @Override
            public boolean onLongClick(final View view, int position)
            {
                return false;
            }
        };

        List<Element> preparedVisits = YearHeader.addYearHeaders(Visit.sortVisitsByDateAccordingToSortOrder(this.park.getChildrenOfInstance(Visit.class)));
        this.visitsRecyclerAdapter = new ExpandableRecyclerAdapter(preparedVisits, recyclerOnClickListener);
    }

    private void updateVisitsRecyclerView()
    {
        if(this.park.getChildCountOfInstance(Visit.class) > 0)
        {
            List<Element> preparedVisits = YearHeader.addYearHeaders(Visit.sortVisitsByDateAccordingToSortOrder(this.park.getChildrenOfInstance(Visit.class)));
            this.expandLatestYearHeaderAccordingToSettings(preparedVisits);
            this.visitsRecyclerAdapter.updateElements(preparedVisits);
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ShowParkVisitsFragment.updateVisitsRecyclerView:: no elements to update");
        }
    }

    private void expandLatestYearHeaderAccordingToSettings(List<Element> yearHeaders)
    {
        if(App.settings.getExpandLatestYearInListByDefault())
        {
            YearHeader latestYearHeader = YearHeader.getLatestYearHeader(yearHeaders);
            Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.expandLatestYearHeaderAccordingToSettings:: expanding latest %s according to settings", latestYearHeader));
            this.visitsRecyclerAdapter.expandElement(latestYearHeader);
        }
    }
}