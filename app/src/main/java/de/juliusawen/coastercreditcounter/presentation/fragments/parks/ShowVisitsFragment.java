package de.juliusawen.coastercreditcounter.presentation.fragments.parks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.orphanElements.YearHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class ShowVisitsFragment extends Fragment
{
    private Park park;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;

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

        if(this.contentRecyclerViewAdapter != null)
        {
            Log.d(Constants.LOG_TAG, "ShowVisitsFragment.onViewCreated:: creating RecyclerView...");

            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTabShowPark_Visits);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(this.contentRecyclerViewAdapter);

            if(savedInstanceState != null)
            {
                this.contentRecyclerViewAdapter.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(Constants.KEY_RECYCLER_SCROLL_POSITION));
            }
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

//        this.updateContentRecyclerView();

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
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.KEY_RECYCLER_SCROLL_POSITION, this.contentRecyclerViewAdapter.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.park = null;
        this.contentRecyclerViewAdapter = null;
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

        List<Element> sortedYearHeaders = this.getSortedYearHeadersForParkVisits(this.park);
        Set<Element> initiallyExpandedElements = new HashSet<>();

        if(App.settings.getExpandLatestYearInListByDefault())
        {
            initiallyExpandedElements.add(YearHeader.getLatestYearHeader(sortedYearHeaders));
        }

        this.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                sortedYearHeaders,
                initiallyExpandedElements,
                Visit.class,
                recyclerOnClickListener);
}

    private void updateContentRecyclerView()
    {
        if(this.park.getChildCountOfType(Visit.class) > 0)
        {
            List<Element> sortedYearHeaders = this.getSortedYearHeadersForParkVisits(this.park);

            this.contentRecyclerViewAdapter.updateContent(sortedYearHeaders);
            this.contentRecyclerViewAdapter.notifyDataSetChanged();
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ShowVisitsFragment.updateContentRecyclerView:: no elements to update");
        }
    }


    private List<Element> getSortedYearHeadersForParkVisits(Park park)
    {
        return YearHeader.addYearHeaders(Visit.sortVisitsByDateAccordingToSortOrder(this.park.getChildrenOfType(Visit.class)));
    }
}