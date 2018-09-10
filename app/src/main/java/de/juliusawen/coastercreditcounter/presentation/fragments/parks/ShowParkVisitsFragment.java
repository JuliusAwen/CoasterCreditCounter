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
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Settings;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

import static de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity.content;

public class ShowParkVisitsFragment extends Fragment
{
    private Park park;
    private List<YearHeader> yearHeaders = new ArrayList<>();
    private ExpandableRecyclerAdapter expandableRecyclerAdapter;

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
        Log.d(Constants.LOG_TAG, "ShowParkVisitsFragment.onCreateView:: creating view...");
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            this.park = (Park) content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
        }

        this.createVisitsRecyclerAdapter();

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, "ShowParkVisitsFragment.onCreateView:: creating view...");
        return inflater.inflate(R.layout.tab_show_park_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_TAG, "ShowParkVisitsFragment.onViewCreated:: decorating view...");

        if(this.expandableRecyclerAdapter != null)
        {
            Log.d(Constants.LOG_TAG, "ShowParkVisitsFragment.onViewCreated:: creating RecyclerView...");

            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTabShowPark_Visits);
            recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(this.expandableRecyclerAdapter);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "ShowParkVisitsFragment.onViewCreated:: ExpandedRecyclerAdapter not set");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case SORT_ASCENDING:
                this.expandableRecyclerAdapter.updateElements(this.prepareVisitsList(Visit.sortDateAscending(this.park.getChildrenOfInstance(Visit.class))));
                return true;

            case SORT_DESCENDING:
                this.expandableRecyclerAdapter.updateElements(this.prepareVisitsList(Visit.sortDateDescending(this.park.getChildrenOfInstance(Visit.class))));
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
        this.expandableRecyclerAdapter = null;
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

        List<Element> preparedVisitsList = this.prepareVisitsList(Visit.sortDateDescending(this.park.getChildrenOfInstance(Visit.class)));

        if(Settings.sortOrderVisits.equals(SortOrder.ASCENDING))
        {
            preparedVisitsList = this.prepareVisitsList(Visit.sortDateAscending(this.park.getChildrenOfInstance(Visit.class)));
        }

        this.expandableRecyclerAdapter = new ExpandableRecyclerAdapter(preparedVisitsList, recyclerOnClickListener);

        if(Settings.expandFirstYearInListByDefault && !preparedVisitsList.isEmpty())
        {
            this.expandableRecyclerAdapter.expandElement(preparedVisitsList.get(0));
        }
    }

    private void onClickRecyclerView(View view)
    {

    }

    private List<Element> prepareVisitsList(List<Element> elements)
    {
        Log.d(Constants.LOG_TAG, "ShowParkVisitsFragment.prepareVisitsList:: preparing list...");

        List<Visit> visits = Visit.convertToVisits(elements);
        List<Element> preparedVisits = new ArrayList<>();

        DateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_YEAR_PATTERN, Locale.getDefault());

        for(Visit visit : visits)
        {
            String year = String.valueOf(simpleDateFormat.format(visit.getCalendar().getTime()));

            Element existingYearHeader = null;
            for(Element yearHeader : preparedVisits)
            {
                if(yearHeader.getName().equals(year))
                {
                    existingYearHeader = yearHeader;
                }
            }

            if(existingYearHeader != null)
            {
                existingYearHeader.addChild(visit);
            }
            else
            {
                YearHeader yearHeader = this.getYearHeader(year);
                yearHeader.addChild(visit);
                preparedVisits.add(yearHeader);
            }

        }

        return preparedVisits;
    }

    private YearHeader getYearHeader(String year)
    {
        for(YearHeader yearHeader : this.yearHeaders)
        {
            if(yearHeader.getName().equals(year))
            {
                Log.v(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.getYearHeader:: reusing %s", yearHeader));
                yearHeader.getChildren().clear();
                return yearHeader;
            }
        }

        YearHeader yearHeader = YearHeader.createYearHeader(year);
        this.yearHeaders.add(yearHeader);
        Log.d(Constants.LOG_TAG, String.format("ShowParkVisitsFragment.getYearHeader:: created new %s", yearHeader));
        return yearHeader;
    }
}