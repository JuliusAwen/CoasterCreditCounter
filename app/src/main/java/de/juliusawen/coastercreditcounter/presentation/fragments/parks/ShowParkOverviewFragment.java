package de.juliusawen.coastercreditcounter.presentation.fragments.parks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.toolbox.Constants;

public class ShowParkOverviewFragment extends Fragment
{
    public ShowParkOverviewFragment() {}

    public static ShowParkOverviewFragment newInstance()
    {
        Log.i(Constants.LOG_TAG, "ShowParkOverviewFragment.newInstance:: creating fragment...");

        return new ShowParkOverviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowParkOverviewFragment.onCreateView:: creating view...");
        View rootView = inflater.inflate(R.layout.tab_show_park_overview, container, false);


        return rootView;
    }
}