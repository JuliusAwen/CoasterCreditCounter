package de.juliusawen.coastercreditcounter.presentation.activities.parks;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;

public class ShowParkActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_show_park);
        super.onCreate(savedInstanceState);

        super.addToolbar();
        this.decorateToolbar();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.viewPagerShowPark);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayoutShowPark);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Objects.requireNonNull(tab).setCustomView(sectionsPagerAdapter.getTabView(i));
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle("<your park name here>", null);
        super.addToolbarHomeButton();
    }

    public static class OverviewFragment extends Fragment
    {
        public OverviewFragment() {}

        public static OverviewFragment newInstance(int sectionNumber)
        {
            OverviewFragment overviewFragment = new OverviewFragment();
            Bundle args = new Bundle();
            args.putInt(Constants.FRAGMENT_ARG_SECTION_NUMBER, sectionNumber);
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
                textView.setText("OVERVIEW");
            }

            return rootView;
        }
    }

    public static class AttractionsFragment extends Fragment
    {
        public AttractionsFragment() {}

        public static AttractionsFragment newInstance(int sectionNumber)
        {
            AttractionsFragment attractionsFragment = new AttractionsFragment();
            Bundle args = new Bundle();
            args.putInt(Constants.FRAGMENT_ARG_SECTION_NUMBER, sectionNumber);
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
                textView.setText("ATTRACTIONS");
            }

            return rootView;
        }
    }

    public static class VisitsFragment extends Fragment
    {
        public VisitsFragment() {}

        public static VisitsFragment newInstance(int sectionNumber)
        {
            VisitsFragment visitsFragment = new VisitsFragment();
            Bundle args = new Bundle();
            args.putInt(Constants.FRAGMENT_ARG_SECTION_NUMBER, sectionNumber);
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
                textView.setText("VISITS");
            }

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private Drawable tabTitleDrawables[] = new Drawable[]
                {
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_home)),
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_airline_seat_legroom_extra)),
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_local_activity))
                };

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
            return this.tabTitleDrawables.length;
        }

        View getTabView(int position)
        {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_title, null);
            ImageView imageView = view.findViewById(R.id.imageViewTabTitle);
            imageView.setImageDrawable(tabTitleDrawables[position]);
            return view;
        }
    }
}
