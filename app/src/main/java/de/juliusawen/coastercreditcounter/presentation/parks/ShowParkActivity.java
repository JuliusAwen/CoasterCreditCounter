package de.juliusawen.coastercreditcounter.presentation.parks;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.attractions.ShowAttractionsFragment;
import de.juliusawen.coastercreditcounter.presentation.visits.ShowVisitsFragment;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowParkActivity extends BaseActivity
{
    private static final int OVERVIEW = 0;
    private static final int ATTRACTIONS = 1;
    private static final int VISITS = 2;
    
    ShowParkActivityViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowParkActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_park);
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ShowParkActivityViewModel.class);
        
        if(this.viewModel.park == null)
        {
            this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }
        
        if(this.viewModel.currentTab == -1)
        {
            this.viewModel.currentTab = OVERVIEW;
        }

        super.addToolbar();
        super.addToolbarHomeButton();

        super.addFloatingActionButton();

        super.addHelpOverlayFragment(null, null);

        this.createTabPagerAdapter();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.viewModel.currentTab == VISITS && this.viewModel.park.getChildCountOfType(Visit.class) > 1)
        {
            menu.add(Menu.NONE, Selection.SORT_ASCENDING.ordinal(), Menu.NONE, R.string.selection_sort_ascending);
            menu.add(Menu.NONE, Selection.SORT_DESCENDING.ordinal(), Menu.NONE, R.string.selection_sort_descending);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments())
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createTabPagerAdapter()
    {
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), this.viewModel.park.getUuid().toString());

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

        viewPager.setCurrentItem(this.viewModel.currentTab);
        this.onPageSelectedViewPager(this.viewModel.currentTab);

        Log.d(Constants.LOG_TAG, String.format("ShowParkActivity.createTabPagerAdapter:: adapter created with [%d] tabs, selected tab[%d]", tabLayout.getTabCount(), this.viewModel.currentTab));
    }

    private void onPageSelectedViewPager(int position)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: selected position [%d]", position));

        switch(position)
        {
            case OVERVIEW:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_overview));
                this.decorateFloatingActionButtonShowParkOverview();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_overview)), getText(R.string.help_text_show_park_overview));
                break;

            case ATTRACTIONS:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_attractions));
                this.decorateFloatingActionButtonShowParkAttractions();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_attractions)), getText(R.string.help_text_show_attractions));
                break;

            case VISITS:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_visits));
                this.decorateFloatingActionButtonShowParkVisits();
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_visits)), getText(R.string.help_text_show_visits));
                break;

            default:
                Log.e(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: tab position [%d] does not exist", position));
        }

        this.viewModel.currentTab = position;
    }

    private void decorateFloatingActionButtonShowParkOverview()
    {
        super.animateFloatingActionButtonTransition(DrawableTool.setTintToWhite(getDrawable(R.drawable.ic_baseline_comment), this));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toaster.makeToast(ShowParkActivity.this, "fab interaction for OVERVIEW not yet implemented");
            }
        });
    }

    private void decorateFloatingActionButtonShowParkAttractions()
    {
        super.animateFloatingActionButtonTransition(DrawableTool.setTintToWhite(getDrawable(R.drawable.ic_baseline_add), this));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toaster.makeToast(ShowParkActivity.this, "fab interaction for ATTRACTIONS not yet implemented");
            }
        });
    }
    private void decorateFloatingActionButtonShowParkVisits()
    {
        super.animateFloatingActionButtonTransition(DrawableTool.setTintToWhite(getDrawable(R.drawable.ic_baseline_add), this));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityTool.startActivityCreateForResult(ShowParkActivity.this, Constants.REQUEST_CREATE_VISIT, viewModel.park);
            }
        });
    }

    public class TabPagerAdapter extends FragmentPagerAdapter
    {
        ShowParkOverviewFragment showParkOverviewFragment;
        ShowAttractionsFragment showAttractionsFragment;
        ShowVisitsFragment showVisitsFragment;

        private String parkUuid;

        private Drawable tabTitleDrawables[] = new Drawable[]
                {
                        DrawableTool.setTintToWhite(getDrawable(R.drawable.ic_baseline_home), ShowParkActivity.this),
                        DrawableTool.setTintToWhite(getDrawable(R.drawable.ic_baseline_airline_seat_legroom_extra), ShowParkActivity.this),
                        DrawableTool.setTintToWhite(getDrawable(R.drawable.ic_baseline_local_activity), ShowParkActivity.this)
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
                    return ShowAttractionsFragment.newInstance(this.parkUuid);
                }
                case(2):
                {
                    return ShowVisitsFragment.newInstance(this.parkUuid);
                }
                default:
                    Log.e(Constants.LOG_TAG, String.format("ShowParkActivity.TabPagerAdapter.getItem:: tab position [%d] does not exist", position));
                    return null;
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position)
        {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

            switch (position)
            {
                case OVERVIEW:
                    this.showParkOverviewFragment = (ShowParkOverviewFragment) createdFragment;
                    break;
                case ATTRACTIONS:
                    this.showAttractionsFragment = (ShowAttractionsFragment) createdFragment;
                    break;
                case VISITS:
                    this.showVisitsFragment = (ShowVisitsFragment) createdFragment;
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
            View view = LayoutInflater.from(ShowParkActivity.this).inflate(R.layout.tab_title, null);
            ImageView imageView = view.findViewById(R.id.imageViewTabTitle);
            imageView.setImageDrawable(tabTitleDrawables[position]);
            return view;
        }
    }
}
