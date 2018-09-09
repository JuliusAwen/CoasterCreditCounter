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
        Log.e(Constants.LOG_TAG, "ShowParkAttractionsFragment.onCreateView:: creating view...");
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
    public void onDetach()
    {
        super.onDetach();
        this.expandableRecyclerAdapter = null;
    }
}