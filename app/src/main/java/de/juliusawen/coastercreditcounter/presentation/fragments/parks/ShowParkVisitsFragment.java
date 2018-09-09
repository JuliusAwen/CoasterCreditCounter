package de.juliusawen.coastercreditcounter.presentation.fragments.parks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

import static de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity.content;

public class ShowParkVisitsFragment extends Fragment
{
    private Park park;
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
    public void onDetach()
    {
        super.onDetach();
        this.park = null;
        this.expandableRecyclerAdapter = null;
    }

    private void createVisitsRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Toaster.makeToast(getContext(), String.format("ShowVisits not yet implemented %s", (Element) view.getTag()));
            }

            @Override
            public void onLongClick(final View view, int position)
            {
            }
        };

        List<Element> preparedVisitsList = this.prepareVisitsList(this.park.getChildrenOfInstance(Visit.class));

        Element firstElement = null;
        if(!preparedVisitsList.isEmpty())
        {
            firstElement = preparedVisitsList.get(0);
        }

        this.expandableRecyclerAdapter = new ExpandableRecyclerAdapter(preparedVisitsList, recyclerOnClickListener);

        if(firstElement != null)
        {
            this.expandableRecyclerAdapter.expandElement(firstElement);
        }
    }

    private List<Element> prepareVisitsList(List<Element> visits)
    {
        Log.d(Constants.LOG_TAG, "ShowParkVisitsFragment.prepareVisitsList:: preparing list...");

        List<Element> preparedVisits = new ArrayList<>();
        DateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_YEAR_PATTERN, Locale.getDefault());

        for(Element element : visits)
        {
            if(element.isInstance(Visit.class))
            {
                Visit visit = (Visit) element;
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
                    existingYearHeader.addChild(element);
                }
                else
                {
                    YearHeader yearHeader = YearHeader.createYearHeader(year);
                    yearHeader.addChild(element);
                    preparedVisits.add(yearHeader);
                }
            }
            else
            {
                String errorMessage = String.format(Locale.getDefault(), "element %s is not instance of Visit", element);
                Log.e(Constants.LOG_TAG, errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }

        Element.sortYearsDescending(preparedVisits);

        return preparedVisits;
    }
}