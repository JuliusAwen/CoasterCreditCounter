package de.juliusawen.coastercreditcounter.presentation.activities.parks;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
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
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkAttractionsFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkOverviewFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkVisitsFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowParkActivity extends BaseActivity
{
    private Park park;

    private ExpandableRecyclerAdapter attractionsRecyclerAdapter;
    private ExpandableRecyclerAdapter visitsRecyclerAdapter;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowParkActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_park);
        super.onCreate(savedInstanceState);

        this.initializeContent();

        super.addHelpOverlay(null, null);

        super.addToolbar();
        this.decorateToolbar();

        super.addFloatingActionButton();

        this.createTabsPagerAdapter();
    }

    private void initializeContent()
    {
        String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        this.park = (Park) content.getElementByUuid(UUID.fromString(elementUuid));

        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.initializeContent:: initialized with %s", this.park));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(getString(R.string.title_show_park), null);
        super.addToolbarHomeButton();
    }

    private void createTabsPagerAdapter()
    {
        this.attractionsRecyclerAdapter = this.buildAttractionsRecyclerAdapter();
        this.visitsRecyclerAdapter = this.buildVisitsRecyclerAdapter();

        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), this.attractionsRecyclerAdapter, this.visitsRecyclerAdapter);

        this.viewPager = findViewById(R.id.viewPagerShowPark);
        this.viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayoutShowPark);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabTitleView(i));
        }

        this.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                onPageSelectedViewPager(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(this.viewPager));

        this.viewPager.setCurrentItem(0);
        this.onPageSelectedViewPager(0);

        Log.i(Constants.LOG_TAG, String.format(
                "ShowParkActivity.createTabsPagerAdapter:: adapter created for #[%d] tabs, selected position[%d] by default", tabLayout.getTabCount(), 0));
    }

    private void onPageSelectedViewPager(int position)
    {
        Log.d(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: tab %d selected", position));

        switch(position)
        {
            case 0:
                super.setToolbarTitleAndSubtitle(this.park.getName(), getString(R.string.subtitle_show_park_overview));
                this.decorateFloatingActionButtonShowParkOverview();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_show_park_overview)), getText(R.string.help_text_show_park_overview));
                break;

            case 1:
                super.setToolbarTitleAndSubtitle(this.park.getName(), getString(R.string.subtitle_show_park_attractions));
                this.decorateFloatingActionButtonShowParkAttractions();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_show_park_attractions)), getText(R.string.help_text_show_park_attractions));
                break;

            case 2:
                super.setToolbarTitleAndSubtitle(this.park.getName(), getString(R.string.subtitle_show_park_visits));
                this.decorateFloatingActionButtonShowParkVisits();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_show_park_visits)), getText(R.string.help_text_show_park_visits));
                break;
        }
    }

    private void decorateFloatingActionButtonShowParkOverview()
    {
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_comment)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private void decorateFloatingActionButtonShowParkAttractions()
    {
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private void decorateFloatingActionButtonShowParkVisits()
    {
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    private ExpandableRecyclerAdapter buildAttractionsRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Toaster.makeToast(getApplicationContext(), String.format("ShowAttractions not yet implemented %s", (Element) view.getTag()));
            }

            @Override
            public void onLongClick(final View view, int position)
            {
            }
        };

        return new ExpandableRecyclerAdapter(this.park.getChildrenOfInstance(Attraction.class), recyclerOnClickListener);
    }

    private ExpandableRecyclerAdapter buildVisitsRecyclerAdapter()
    {
        RecyclerOnClickListener.OnClickListener recyclerOnClickListener = new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Toaster.makeToast(getApplicationContext(), String.format("ShowVisits not yet implemented %s", (Element) view.getTag()));
            }

            @Override
            public void onLongClick(final View view, int position)
            {
            }
        };

        return new ExpandableRecyclerAdapter(this.prepareVisitsList(this.park.getChildrenOfInstance(Visit.class)), recyclerOnClickListener);
    }

    private List<Element> prepareVisitsList(List<Element> visits)
    {
        List<Element> preparedVisits = new ArrayList<>();

        preparedVisits.addAll(visits);

        return preparedVisits;
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter
    {
        ShowParkOverviewFragment showParkOverviewFragment;
        ShowParkAttractionsFragment showParkAttractionsFragment;
        ShowParkVisitsFragment showParkVisitsFragment;

        private ExpandableRecyclerAdapter attractionsRecyclerAdapter;
        private ExpandableRecyclerAdapter visitsRecyclerAdapter;

        private Drawable tabTitleDrawables[] = new Drawable[]
                {
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_home)),
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_airline_seat_legroom_extra)),
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_local_activity))
                };

        TabsPagerAdapter(FragmentManager fragmentManager, ExpandableRecyclerAdapter attractionsRecyclerAdapter, ExpandableRecyclerAdapter visitsRecyclerAdapter)
        {
            super(fragmentManager);
            this.attractionsRecyclerAdapter = attractionsRecyclerAdapter;
            this.visitsRecyclerAdapter = visitsRecyclerAdapter;
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.d(Constants.LOG_TAG, String.format("TabsPagerAdapter.getItem:: tab position [%d] selected", position));

            switch(position)
            {
                case(0):
                {
                    return ShowParkOverviewFragment.newInstance();
                }
                case(1):
                {
                    return ShowParkAttractionsFragment.newInstance();
                }
                case(2):
                {
                    return ShowParkVisitsFragment.newInstance();
                }
                default:
                    Log.e(Constants.LOG_TAG, String.format("TabsPagerAdapter.getItem:: tab position [%d] does not exist", position));
                    return null;
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

            switch (position)
            {
                case 0:
                    this.showParkOverviewFragment = (ShowParkOverviewFragment) createdFragment;
                    break;
                case 1:
                    this.showParkAttractionsFragment = (ShowParkAttractionsFragment) createdFragment;
                    this.showParkAttractionsFragment.setExpandableRecyclerAdapter(this.attractionsRecyclerAdapter);
                    break;
                case 2:
                    this.showParkVisitsFragment = (ShowParkVisitsFragment) createdFragment;
                    this.showParkVisitsFragment.setExpandableRecyclerView(this.visitsRecyclerAdapter);
                    break;
            }

            return createdFragment;
        }

        @Override
        public int getCount()
        {
            return this.tabTitleDrawables.length;
        }

        View getTabTitleView(int position)
        {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_title, null);
            ImageView imageView = view.findViewById(R.id.imageViewTabTitle);
            imageView.setImageDrawable(tabTitleDrawables[position]);
            return view;
        }
    }
}
