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

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class ShowParkVisitsFragment extends Fragment
{
    private ExpandableRecyclerAdapter expandableRecyclerAdapter;

    public ShowParkVisitsFragment() {}

    public static ShowParkVisitsFragment newInstance()
    {
        Log.i(Constants.LOG_TAG, "ShowParkVisitsFragment.newInstance:: creating fragment");

        return new ShowParkVisitsFragment();
    }

    public void setExpandableRecyclerView(ExpandableRecyclerAdapter expandableRecyclerAdapter)
    {
        this.expandableRecyclerAdapter = expandableRecyclerAdapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkVisitsFragment.onCreateView:: creating view...");
        View rootView = inflater.inflate(R.layout.tab_show_park_visits, container, false);

        if(this.expandableRecyclerAdapter != null)
        {
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewTabShowPark_Visits);
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
    public void onDetach()
    {
        super.onDetach();
        this.expandableRecyclerAdapter = null;
    }
}