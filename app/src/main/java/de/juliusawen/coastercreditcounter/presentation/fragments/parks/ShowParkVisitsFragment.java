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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.content.Visit;
import de.juliusawen.coastercreditcounter.content.YearHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowParkVisitsFragment extends Fragment
{
    private Park park;
    private List<YearHeader> yearHeaders = new ArrayList<>();
    private ExpandableRecyclerAdapter visitsRecyclerAdapter;
    private SortOrder sortOrder = SortOrder.DESCENDING;

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

        this.sortOrder = App.settings.getDefaultSortOrderParkVisits();
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
                this.sortOrder = SortOrder.ASCENDING;
                this.updateVisitsRecyclerView();
                return true;

            case SORT_DESCENDING:
                this.sortOrder = SortOrder.DESCENDING;
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
        this.yearHeaders = null;
        this.visitsRecyclerAdapter = null;
        this.sortOrder = null;
    }

    private void createVisitsRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Toaster.makeToast(getContext(), String.format("ShowVisit not yet implemented %s", (Element) view.getTag()));
            }

            @Override
            public void onLongClick(final View view, int position)
            {
            }
        };

        List<Element> preparedVisits = this.addYearHeaders(this.park.getChildrenOfInstance(Visit.class));
        this.visitsRecyclerAdapter = new ExpandableRecyclerAdapter(preparedVisits, recyclerOnClickListener);
    }

    private List<Element> addYearHeaders(List<Element> elements)
    {
        if(elements.isEmpty())
        {
            Log.v(Constants.LOG_TAG, "ShowParkVisitsFragment.addYearHeaders:: no elements found");
            return elements;
        }

        Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.addYearHeaders:: adding headers for #[%d] elements...", elements.size()));
        YearHeader.removeAllChildren(this.yearHeaders);
        elements = this.sortVisitsByDate(elements);

        List<Visit> visits = Visit.convertToVisits(elements);
        List<Element> preparedElements = new ArrayList<>();

        DateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_YEAR_PATTERN, Locale.getDefault());

        for(Visit visit : visits)
        {
            String year = String.valueOf(simpleDateFormat.format(visit.getCalendar().getTime()));

            Element existingYearHeader = null;
            for(Element yearHeader : preparedElements)
            {
                if(yearHeader.getName().equals(year))
                {
                    existingYearHeader = yearHeader;
                }
            }

            if(existingYearHeader != null)
            {
                existingYearHeader.addChildToOrphanElement(visit);
            }
            else
            {
                YearHeader yearHeader = this.getYearHeader(year);
                yearHeader.addChildToOrphanElement(visit);
                preparedElements.add(yearHeader);
            }
        }

        Log.d(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.addYearHeaders:: #[%d] headers added", preparedElements.size()));
        return preparedElements;
    }

    private YearHeader getYearHeader(String year)
    {
        for(YearHeader yearHeader : this.yearHeaders)
        {
            if(yearHeader.getName().equals(year))
            {
                Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.getYearHeader:: reusing %s", yearHeader));
                return yearHeader;
            }
        }

        YearHeader yearHeader = YearHeader.create(year);
        this.yearHeaders.add(yearHeader);
        Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.getYearHeader:: created new %s", yearHeader));
        return yearHeader;
    }

    private List<Element> sortVisitsByDate(List<Element> visits)
    {
        if(this.sortOrder.equals(SortOrder.ASCENDING))
        {
            visits = Visit.sortAscendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.sortVisitsByDate:: sorted #[%d] visits <ascending>", visits.size()));
        }
        else if(this.sortOrder.equals(SortOrder.DESCENDING))
        {
            visits = Visit.sortDescendingByDate(visits);
            Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.sortVisitsByDate:: sorted #[%d] visits <descending>", visits.size()));
        }

        return visits;
    }

    private void updateVisitsRecyclerView()
    {
        if(this.park.getChildCountOfInstance(Visit.class) > 0)
        {
            List<Element> preparedVisits = this.addYearHeaders(this.park.getChildrenOfInstance(Visit.class));
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