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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.content.Visit;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkAttractionsFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkOverviewFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkVisitsFragment;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowParkActivity extends BaseActivity
{
    private Park park;

    private static final int OVERVIEW = 0;
    private static final int ATTRACTIONS = 1;
    private static final int VISITS = 2;

    private int currentTab = OVERVIEW;

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

        this.createTabPagerAdapter();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.currentTab == ATTRACTIONS && this.park.getAttractionCategoryCount() > 1)
        {
            menu.add(Menu.NONE, Selection.SORT_ATTRACTION_CATEGORIES.ordinal(), Menu.NONE, R.string.selection_sort_categories);
        }

        if(this.currentTab == VISITS && this.park.getChildCountOfInstance(Visit.class) > 1)
        {
            menu.add(Menu.NONE, Selection.SORT_ASCENDING.ordinal(), Menu.NONE, R.string.selection_sort_ascending);
            menu.add(Menu.NONE, Selection.SORT_DESCENDING.ordinal(), Menu.NONE, R.string.selection_sort_descending);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void initializeContent()
    {
        String elementUuid = getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID);
        this.park = (Park) App.content.getElementByUuid(UUID.fromString(elementUuid));

        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.initializeContent:: initialized with %s and current tab[%d] selected", this.park, this.currentTab));
    }

    private void decorateToolbar()
    {
        super.setToolbarTitleAndSubtitle(getString(R.string.title_show_park), null);
        super.addToolbarHomeButton();
    }

    private void createTabPagerAdapter()
    {
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), this.park.getUuid().toString());

        ViewPager viewPager = findViewById(R.id.viewPagerShowPark);
        viewPager.setAdapter(tabPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayoutShowPark);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Objects.requireNonNull(tab).setCustomView(tabPagerAdapter.getTabTitleView(i));
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
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

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.setCurrentItem(currentTab);
        this.onPageSelectedViewPager(currentTab);

        Log.d(Constants.LOG_TAG, String.format("ShowParkActivity.createTabPagerAdapter:: adapter created for #[%d] tabs, selected position[%d] by default", tabLayout.getTabCount(), currentTab));
    }

    private void onPageSelectedViewPager(int position)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: selected position [%d]", position));

        switch(position)
        {
            case OVERVIEW:
                super.setToolbarTitleAndSubtitle(this.park.getName(), getString(R.string.subtitle_show_park_overview));
                this.decorateFloatingActionButtonShowParkOverview();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_show_park_overview)), getText(R.string.help_text_show_park_overview));
                break;

            case ATTRACTIONS:
                super.setToolbarTitleAndSubtitle(this.park.getName(), getString(R.string.subtitle_show_park_attractions));
                this.decorateFloatingActionButtonShowParkAttractions();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_show_park_attractions)), getText(R.string.help_text_show_park_attractions));
                break;

            case VISITS:
                super.setToolbarTitleAndSubtitle(this.park.getName(), getString(R.string.subtitle_show_park_visits));
                this.decorateFloatingActionButtonShowParkVisits();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_show_park_visits)), getText(R.string.help_text_show_park_visits));
                break;

            default:
                Log.e(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: tab position [%d] does not exist", position));
        }

        this.currentTab = position;
    }

    private void decorateFloatingActionButtonShowParkOverview()
    {
        super.animateFloatingActionButton(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_comment)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toaster.makeToast(getApplicationContext(), "fab interaction for OVERVIEW not yet implemented");
            }
        });
    }

    private void decorateFloatingActionButtonShowParkAttractions()
    {
        super.animateFloatingActionButton(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toaster.makeToast(getApplicationContext(), "fab interaction for ATTRACTIONS not yet implemented");
            }
        });
    }
    private void decorateFloatingActionButtonShowParkVisits()
    {
        super.animateFloatingActionButton(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    public class TabPagerAdapter extends FragmentPagerAdapter
    {
        ShowParkOverviewFragment showParkOverviewFragment;
        ShowParkAttractionsFragment showParkAttractionsFragment;
        ShowParkVisitsFragment showParkVisitsFragment;

        private String parkUuid;

        private Drawable tabTitleDrawables[] = new Drawable[]
                {
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_home)),
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_airline_seat_legroom_extra)),
                        DrawableTool.setTintToWhite(getApplicationContext(), getDrawable(R.drawable.ic_baseline_local_activity))
                };

        TabPagerAdapter(FragmentManager fragmentManager, String parkUuid)
        {
            super(fragmentManager);
            Log.d(Constants.LOG_TAG, "ShowParkActivity.TabPagerAdapter.Constructor:: instantiating adapter...");
            this.parkUuid = parkUuid;
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.v(Constants.LOG_TAG, String.format("ShowParkActivity.TabPagerAdapter.getItem:: returning fragment for position [%d]", position));

            switch(position)
            {
                case(0):
                {
                    return ShowParkOverviewFragment.newInstance(this.parkUuid);
                }
                case(1):
                {
                    return ShowParkAttractionsFragment.newInstance(this.parkUuid);
                }
                case(2):
                {
                    return ShowParkVisitsFragment.newInstance(this.parkUuid);
                }
                default:
                    Log.e(Constants.LOG_TAG, String.format("ShowParkActivity.TabPagerAdapter.getItem:: tab position [%d] does not exist", position));
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
                case OVERVIEW:
                    this.showParkOverviewFragment = (ShowParkOverviewFragment) createdFragment;
                    break;
                case ATTRACTIONS:
                    this.showParkAttractionsFragment = (ShowParkAttractionsFragment) createdFragment;
                    break;
                case VISITS:
                    this.showParkVisitsFragment = (ShowParkVisitsFragment) createdFragment;
                    break;
                default:
                    String errorMessage = String.format(Locale.getDefault(), "ShowParkActivity.TabPagerAdapter.instantiateItem:: tab position [%d] does not exist", position);
                    Log.e(Constants.LOG_TAG, errorMessage);
                    throw new IllegalStateException(errorMessage);
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
