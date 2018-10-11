package de.juliusawen.coastercreditcounter.presentation.parks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class ShowParkOverviewFragment extends Fragment
{
    private ShowParkOverviewFragmentViewModel viewModel;

    public ShowParkOverviewFragment() {}

    public static ShowParkOverviewFragment newInstance(String parkUuid)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowParkOverviewFragment.newInstance:: instantiating fragment...");

        ShowParkOverviewFragment showParkOverviewFragment = new ShowParkOverviewFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuid);
        showParkOverviewFragment.setArguments(args);

        return showParkOverviewFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ShowParkOverviewFragmentViewModel.class);

        if(this.viewModel.park == null)
        {
            if (getArguments() != null)
            {
                this.viewModel.park = (Park) App.content.getElementByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkOverviewFragment.onCreateView:: creating view...");
        return inflater.inflate(R.layout.fragment_show_park_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkOverviewFragment.onViewCreated:: decorating view...");
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.viewModel = null;
    }
}