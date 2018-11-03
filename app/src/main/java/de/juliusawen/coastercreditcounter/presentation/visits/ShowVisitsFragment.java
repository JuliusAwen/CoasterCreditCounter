package de.juliusawen.coastercreditcounter.presentation.visits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowVisitsFragment extends Fragment
{
    private ShowVisitsFragmentViewModel viewModel;

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
        Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onCreate:: creating fragment...");
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ShowVisitsFragmentViewModel.class);

        if(this.viewModel.park == null)
        {
            if (getArguments() != null)
            {
                this.viewModel.park = (Park) App.content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
            }
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerAdapter();
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewShowVisits);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);
        Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onViewCreated:: RecyclerView initialized");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_CREATE_VISIT)
            {
                this.updateContentRecyclerView();

                Element visit = App.content.fetchElementByUuidString(data.getStringExtra(Constants.EXTRA_ELEMENT_UUID));
                ActivityTool.startActivityShow(getActivity(), Constants.REQUEST_SHOW_VISIT, visit);
            }
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.viewModel = null;
    }

    private ContentRecyclerViewAdapter createContentRecyclerAdapter()
    {
        List<Element> categorizedVisits = this.fetchCategorizedVisits();

        return ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                categorizedVisits,
                App.settings.getExpandLatestYearInListByDefault() ? Collections.singleton((Element)YearHeader.getLatestYearHeader(categorizedVisits)) : null,
                Visit.class);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityTool.startActivityShow(getActivity(), Constants.REQUEST_SHOW_VISIT, (Element) view.getTag());
            }

            @Override
            public boolean onLongClick(final View view)
            {
                Toaster.makeToast(getContext(), "not yet implemented");
                return false;
            }
        };
    }

    private void updateContentRecyclerView()
    {
        if(this.viewModel.park.getChildCountOfType(Visit.class) > 0)
        {
            List<Element> sortedYearHeaders = this.fetchCategorizedVisits();

            this.viewModel.contentRecyclerViewAdapter.updateContent(sortedYearHeaders);

            if(App.settings.getExpandLatestYearInListByDefault())
            {
                this.viewModel.contentRecyclerViewAdapter.expandParent(YearHeader.getLatestYearHeader(sortedYearHeaders));
            }

            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }
        else
        {
            Log.v(Constants.LOG_TAG, "ShowVisitsFragment.updateContentRecyclerView:: no elements to update");
        }
    }


    private List<Element> fetchCategorizedVisits()
    {
        return YearHeader.fetchYearHeadersFromVisits(Visit.sortVisitsByDateAccordingToSortOrder(this.viewModel.park.getChildrenAsType(Visit.class)));
    }
}