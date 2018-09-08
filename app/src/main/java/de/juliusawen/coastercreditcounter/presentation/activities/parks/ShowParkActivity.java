package de.juliusawen.coastercreditcounter.presentation.activities.parks;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.content.Visit;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowParkActivity extends BaseActivity
{
    private Park park;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowParkActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_park);
        super.onCreate(savedInstanceState);
        this.initializeContent();

        super.addToolbar();
        this.decorateToolbar();

        this.createSectionsPagerAdapter();
    }

    private void initializeContent()
    {
        String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        this.park = (Park) content.getElementByUuid(UUID.fromString(elementUuid));

        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.initializeContent:: initialized with %s", this.park));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(park.getName(), null);
        super.addToolbarHomeButton();
    }

    private void createSectionsPagerAdapter()
    {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this.park.getUuid().toString());

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

        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.createSectionsPagerAdapter:: adapter created for #[%d] tabs", tabLayout.getTabCount()));
    }

    //region TAB FRAGMENTS
    public static class OverviewFragment extends Fragment
    {
        public OverviewFragment() {}

        public static OverviewFragment newInstance(String parkUuidString)
        {
            Log.i(Constants.LOG_TAG, "OverviewFragment.newInstance:: creating fragment...");

            OverviewFragment overviewFragment = new OverviewFragment();
            Bundle args = new Bundle();
            args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuidString);
            overviewFragment.setArguments(args);
            return overviewFragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            Log.e(Constants.LOG_TAG, "OverviewFragment.onCreateView:: creating view...");

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

        public static AttractionsFragment newInstance(String parkUuidString)
        {
            Log.i(Constants.LOG_TAG, "AttractionsFragment.newInstance:: creating fragment");

            AttractionsFragment attractionsFragment = new AttractionsFragment();
            Bundle args = new Bundle();
            args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuidString);
            attractionsFragment.setArguments(args);
            return attractionsFragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            Log.d(Constants.LOG_TAG, "AttractionsFragment.onCreateView:: creating view...");

            View rootView = inflater.inflate(R.layout.tab_show_park_attractions, container, false);

            if(getArguments() != null)
            {
                String parkUuid = getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID);
                Element park = content.getElementByUuid(UUID.fromString(parkUuid));

                AttractionsFragment.createAttractionsRecyclerAdapter(rootView, park);
            }

            return rootView;
        }

        private static void createAttractionsRecyclerAdapter(final View rootView, Element park)
        {
            RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
            {
                @Override
                public void onClick(View view, int position)
                {
                    Toaster.makeToast(rootView.getContext(), "ShowAttraction not yet implemented");
                }

                @Override
                public void onLongClick(final View view, int position)
                {
                }
            };

            ExpandableRecyclerAdapter attractionsRecyclerAdapter = new ExpandableRecyclerAdapter(park.getChildrenOfInstance(Attraction.class), recyclerOnClickListener);
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewTabShowPark_Attractions);
            recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            recyclerView.setAdapter(attractionsRecyclerAdapter);
        }
    }

    public static class VisitsFragment extends Fragment
    {
        public VisitsFragment() {}

        public static VisitsFragment newInstance(String parkUuidString)
        {
            Log.d(Constants.LOG_TAG, "VisitsFragment.newInstance:: creating fragment");

            VisitsFragment visitsFragment = new VisitsFragment();
            Bundle args = new Bundle();
            args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuidString);
            visitsFragment.setArguments(args);
            return visitsFragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            Log.v(Constants.LOG_TAG, "VisitsFragment.onCreateView:: creating view...");

            View rootView = inflater.inflate(R.layout.tab_show_park_visits, container, false);

            if(getArguments() != null)
            {
                String parkUuid = getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID);
                Element park = content.getElementByUuid(UUID.fromString(parkUuid));

                VisitsFragment.createVisistsRecyclerAdapter(rootView, park);
            }


            return rootView;
        }

        private static void createVisistsRecyclerAdapter(final View rootView, Element park)
        {
            RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
            {
                @Override
                public void onClick(View view, int position)
                {
                    Toaster.makeToast(rootView.getContext(), "ShowVisit not yet implemented");
                }

                @Override
                public void onLongClick(final View view, int position)
                {
                }
            };

            ExpandableRecyclerAdapter visitsRecyclerAdapter = new ExpandableRecyclerAdapter(park.getChildrenOfInstance(Visit.class), recyclerOnClickListener);
            RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewTabShowPark_Visits);
            recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            recyclerView.setAdapter(visitsRecyclerAdapter);
        }
    }
    //endregion

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private String parkUuidString;

        private Drawable tabTitleDrawables[] = new Drawable[]
                {
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_home)),
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_airline_seat_legroom_extra)),
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_local_activity))
                };

        SectionsPagerAdapter(FragmentManager fragmentManager, String parkUuidString)
        {
            super(fragmentManager);
            this.parkUuidString = parkUuidString;
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.d(Constants.LOG_TAG, String.format("SectionsPagerAdapter.getItem:: tab position [%d] selected", position));

            switch(position)
            {
                case(0):
                {
                    return OverviewFragment.newInstance(this.parkUuidString);
                }
                case(1):
                {
                    return AttractionsFragment.newInstance(this.parkUuidString);
                }
                default:
                {
                    return VisitsFragment.newInstance(this.parkUuidString);
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
