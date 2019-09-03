package de.juliusawen.coastercreditcounter.frontend.parks;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.attractions.ShowAttractionsFragment;
import de.juliusawen.coastercreditcounter.frontend.visits.ShowVisitsFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;
import de.juliusawen.coastercreditcounter.toolbox.DrawableProvider;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowParkActivity extends BaseActivity implements ShowVisitsFragment.ShowVisitsFragmentInteraction, ShowAttractionsFragment.ShowAttractionsFragmentInteraction
{
    private static final int TAB_OVERVIEW = 0;
    private static final int TAB_ATTRACTIONS = 1;
    private static final int TAB_VISITS = 2;

    private ViewPager viewPager;
    
    private ShowParkActivityViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowParkActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_park);
        super.onCreate(savedInstanceState);
        
        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(ShowParkActivityViewModel.class);

            if(this.viewModel.park == null)
            {
                this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            }

            if(this.viewModel.currentTab == -1)
            {
                this.viewModel.currentTab = TAB_OVERVIEW;
            }

            super.addHelpOverlayFragment(null, null)
                    .addToolbar()
                    .addToolbarHomeButton()
                    .addFloatingActionButton();

            this.createTabPagerAdapter();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.viewModel.currentTab == TAB_VISITS && this.viewModel.park.getChildCountOfType(Visit.class) > 1)
        {
            menu.add(Menu.NONE, Constants.SELECTION_SORT_BY_YEAR_ASCENDING, Menu.NONE, R.string.selection_sort_ascending);
            menu.add(Menu.NONE, Constants.SELECTION_SORT_BY_YEAR_DESCENDING, Menu.NONE, R.string.selection_sort_descending);
        }
        else if(this.viewModel.currentTab == TAB_ATTRACTIONS)
        {
            ShowAttractionsFragment showAttractionsFragment = (ShowAttractionsFragment)this.viewPager.getAdapter().instantiateItem(this.viewPager, viewPager.getCurrentItem());

            menu.add(Menu.NONE, Constants.SELECTION_EXPAND_ALL, Menu.NONE, R.string.selection_expand_all).setEnabled(!showAttractionsFragment.isAllExpanded());
            menu.add(Menu.NONE, Constants.SELECTION_COLLAPSE_ALL, Menu.NONE, R.string.selection_collapse_all).setEnabled(!showAttractionsFragment.isAllCollapsed());
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

        this.viewPager = findViewById(R.id.viewPagerShowPark);
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
            case TAB_OVERVIEW:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_overview))
                        .setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_overview)), getText(R.string.help_text_show_park_overview));
                this.decorateFloatingActionButtonShowParkOverview();
                break;

            case TAB_ATTRACTIONS:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_attractions))
                        .setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_attractions)), getText(R.string.help_text_show_attractions));
                this.decorateFloatingActionButtonShowParkAttractions();
                break;

            case TAB_VISITS:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_visits))
                        .setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_visits)), getText(R.string.help_text_show_visits));
                this.decorateFloatingActionButtonShowParkVisits();
                break;

            default:
                Log.e(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: tab position [%d] does not exist", position));
        }

        this.viewModel.currentTab = position;
    }

    private void decorateFloatingActionButtonShowParkOverview()
    {
        super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_comment, R.color.white))
                .setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toaster.makeToast(ShowParkActivity.this, "fab interaction for TAB_OVERVIEW not yet implemented");
            }
        });
    }

    private void decorateFloatingActionButtonShowParkAttractions()
    {
        super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white))
                .setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityDistributor.startActivityCreateForResult(ShowParkActivity.this, Constants.REQUEST_CODE_CREATE_CUSTOM_ATTRACTION, viewModel.park);
            }
        });
    }
    private void decorateFloatingActionButtonShowParkVisits()
    {
        super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white))
                .setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityDistributor.startActivityCreateForResult(ShowParkActivity.this, Constants.REQUEST_CODE_CREATE_VISIT, viewModel.park);
            }
        });
    }

    @Override
    public void deleteElement(IElement elementToDelete)
    {
        super.markForDeletion(elementToDelete, true)
                .markForUpdate(elementToDelete.getParent());
    }

    @Override
    public void updateElement(IElement elementToUpdate)
    {
        super.markForUpdate(elementToUpdate);
    }

    public class TabPagerAdapter extends FragmentPagerAdapter
    {
        ShowParkOverviewFragment showParkOverviewFragment;
        ShowAttractionsFragment showAttractionsFragment;
        ShowVisitsFragment showVisitsFragment;

        private final String parkUuid;

        private final Drawable[] tabTitleDrawables = new Drawable[]
                {
                        DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_home, R.color.white),
                        DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_airline_seat_legroom_extra, R.color.white),
                        DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_local_activity, R.color.white)
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
                case TAB_OVERVIEW:
                    this.showParkOverviewFragment = (ShowParkOverviewFragment) createdFragment;
                    break;
                case TAB_ATTRACTIONS:
                    this.showAttractionsFragment = (ShowAttractionsFragment) createdFragment;
                    break;
                case TAB_VISITS:
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
