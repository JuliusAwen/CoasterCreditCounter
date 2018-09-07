package de.juliusawen.coastercreditcounter.presentation.activities.parks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;

public class ShowParkActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_show_park);
        CoordinatorLayout coordinatorLayoutActivity = findViewById(R.id.coordinatorLayoutShowLocations);
        coordinatorLayoutActivity.addView(getLayoutInflater().inflate(R.layout.layout_show_park, coordinatorLayoutActivity, false));
        super.onCreate(savedInstanceState);

//        super.addToolbar();
//        this.decorateToolbar();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.viewPagerShowPark);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayoutShowPark);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(getString(R.string.title_show_park), null);
        super.addToolbarHomeButton();
    }

    public static class OverviewFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public OverviewFragment() {}

        public static OverviewFragment newInstance(int sectionNumber)
        {
            OverviewFragment overviewFragment = new OverviewFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            overviewFragment.setArguments(args);
            return overviewFragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.tab_show_park_overview, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);

            if(getArguments() != null)
            {
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            }

            return rootView;
        }
    }

    public static class AttractionsFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public AttractionsFragment() {}

        public static AttractionsFragment newInstance(int sectionNumber)
        {
            AttractionsFragment attractionsFragment = new AttractionsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            attractionsFragment.setArguments(args);
            return attractionsFragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.tab_show_park_attractions, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);

            if(getArguments() != null)
            {
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            }

            return rootView;
        }
    }

    public static class VisitsFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public VisitsFragment() {}

        public static VisitsFragment newInstance(int sectionNumber)
        {
            VisitsFragment visitsFragment = new VisitsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            visitsFragment.setArguments(args);
            return visitsFragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.tab_show_park_visits, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);

            if(getArguments() != null)
            {
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            }

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        SectionsPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case(0):
                {
                    return OverviewFragment.newInstance(position + 1);
                }
                case(1):
                {
                    return AttractionsFragment.newInstance(position + 1);
                }
                default:
                {
                    return VisitsFragment.newInstance(position + 1);
                }
            }
        }

        @Override
        public int getCount()
        {
            return 3;
        }
    }
}
