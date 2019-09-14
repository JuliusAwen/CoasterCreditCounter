package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgent.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.fragments.ShowParkOverviewFragment;
import de.juliusawen.coastercreditcounter.userInterface.fragments.ShowVisitsFragment;

public class ShowParkActivity extends BaseActivity implements ShowVisitsFragment.ShowVisitsFragmentInteraction, ShowAttractionsFragment.ShowAttractionsFragmentInteraction
{
    private enum Tab
    {
        SHOW_OVERVIEW,
        SHOW_ATTRACTIONS,
        SHOW_VISITS
    }

    private ShowParkActivityViewModel viewModel;
    private ViewPager viewPager;
    
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

            if(this.viewModel.optionsMenuAgent == null)
            {
                this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
            }

            super.addHelpOverlayFragment(null, null);
            super.addToolbar();
            super.addToolbarHomeButton();
            super.addFloatingActionButton();

            this.createTabPagerAdapter();
        }
    }

    @Override
    public void onDestroy()
    {
        getSupportFragmentManager().getFragments().clear();
        super.onDestroy();
    }


    //region --- OPTIONS MENU

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(this.viewModel.optionsMenuAgent.handleOptionsItemSelected(item, this))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleExpandAllSelected()
    {
        switch(this.getCurrentTab())
        {
            case SHOW_OVERVIEW:
            case SHOW_VISITS:
                break;

            case SHOW_ATTRACTIONS:
                ((ShowAttractionsFragment)((TabPagerAdapter)this.viewPager.getAdapter()).getFragment(this.viewPager.getCurrentItem())).handleMenuItemExpandAllSelected();
        }
    }

    @Override
    public void handleCollapseAllSelected()
    {
        switch(this.getCurrentTab())
        {
            case SHOW_OVERVIEW:
            case SHOW_VISITS:
                break;

            case SHOW_ATTRACTIONS:
                ((ShowAttractionsFragment)((TabPagerAdapter)this.viewPager.getAdapter()).getFragment(this.viewPager.getCurrentItem())).handleMenuItemCollapseAllSelected();
        }
    }

    @Override
    public void handleSortAscendingSelected()
    {
        switch(this.getCurrentTab())
        {
            case SHOW_OVERVIEW:
            case SHOW_ATTRACTIONS:
                break;

            case SHOW_VISITS:
                ((ShowVisitsFragment)((TabPagerAdapter)this.viewPager.getAdapter()).getFragment(this.viewPager.getCurrentItem())).handleMenuItemSortAscendingSelected();
        }
    }

    @Override
    public void handleSortDescendingSelected()
    {
        switch(this.getCurrentTab())
        {
            case SHOW_OVERVIEW:
            case SHOW_ATTRACTIONS:
                break;

            case SHOW_VISITS:
                ((ShowVisitsFragment)((TabPagerAdapter)this.viewPager.getAdapter()).getFragment(this.viewPager.getCurrentItem())).handleMenuItemSortDescendingSelected();
        }
    }

    //endregion --- OPTIONS MENU


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        for (Fragment fragment : getSupportFragmentManager().getFragments())
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void markForDeletion(IElement elementToDelete)
    {
        super.markForDeletion(elementToDelete, true);
        super.markForUpdate(elementToDelete.getParent());
    }

    @Override
    public void markForUpdate(IElement elementToUpdate)
    {
        super.markForUpdate(elementToUpdate);
    }

    private Tab getCurrentTab()
    {
        return Tab.values()[this.viewPager.getCurrentItem()];
    }

    private void createTabPagerAdapter()
    {
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(this.getSupportFragmentManager(), this.viewModel.park.getUuid().toString());

        this.viewPager = findViewById(R.id.viewPagerShowPark);
        this.viewPager.setAdapter(tabPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayoutShowPark);
        tabLayout.setupWithViewPager(this.viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++)
        {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Objects.requireNonNull(tab).setCustomView(tabPagerAdapter.getTabTitleView(i));
        }

        this.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position)
            {
                onPageSelectedViewPager(Tab.values()[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        this.viewPager.setCurrentItem(this.viewPager.getCurrentItem());
        this.onPageSelectedViewPager(Tab.values()[this.viewPager.getCurrentItem()]);

        Log.d(Constants.LOG_TAG, String.format("ShowParkActivity.createTabPagerAdapter:: adapter created with [%d] tabs, selected tab[%s]",
                tabLayout.getTabCount(), Tab.values()[this.viewPager.getCurrentItem()]));
    }

    private void onPageSelectedViewPager(Tab tab)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: selected tab [%s]", tab));

        switch(tab)
        {
            case SHOW_OVERVIEW:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_overview));
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_overview)), getString(R.string.help_text_show_park_overview));
                break;

            case SHOW_ATTRACTIONS:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_attractions));
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_attractions)), getString(R.string.help_text_show_attractions));
                break;

            case SHOW_VISITS:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_visits));
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_visits)), getString(R.string.help_text_show_visits));
                break;
        }
        this.decorateFloatingActionButton(tab);
    }

    private void decorateFloatingActionButton(Tab tab)
    {
        switch(tab)
        {
            case SHOW_OVERVIEW:
            {
                super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_comment, R.color.white));
                super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                Toaster.notYetImplemented(ShowParkActivity.this);
                            }
                        });
                break;
            }

            case SHOW_ATTRACTIONS:
            {
                super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
                super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                ActivityDistributor.startActivityCreateForResult(ShowParkActivity.this, RequestCode.CREATE_CUSTOM_ATTRACTION, viewModel.park);
                            }
                        });
                break;
            }

            case SHOW_VISITS:
            {
                super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
                super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                ActivityDistributor.startActivityCreateForResult(ShowParkActivity.this, RequestCode.CREATE_VISIT, viewModel.park);
                            }
                        });
                break;
            }
        }
    }


    public class TabPagerAdapter extends FragmentPagerAdapter
    {
        private final String parkUuid;
        private final Map<Integer, Fragment> fragmentsByPosition = new HashMap<>();

        private final Drawable[] tabTitleDrawables = new Drawable[]
                {
                        DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_home, R.color.white),
                        DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_airline_seat_legroom_extra, R.color.white),
                        DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_local_activity, R.color.white)
                };

        TabPagerAdapter(FragmentManager fragmentManager, String parkUuid)
        {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            Log.d(Constants.LOG_TAG, "ShowParkActivity.TabPagerAdapter.Constructor:: instantiating adapter...");
            this.parkUuid = parkUuid;
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.v(Constants.LOG_TAG, String.format("ShowParkActivity.TabPagerAdapter.getItem:: returning fragment for tab position [%d]", position));

            switch(Tab.values()[position])
            {
                case SHOW_OVERVIEW:
                {
                    ShowParkOverviewFragment showParkOverviewFragment = ShowParkOverviewFragment.newInstance(this.parkUuid);
                    this.fragmentsByPosition.put(position, showParkOverviewFragment);
                    return showParkOverviewFragment;
                }
                case SHOW_ATTRACTIONS:
                {
                    ShowAttractionsFragment showAttractionsFragment = ShowAttractionsFragment.newInstance(this.parkUuid);
                    this.fragmentsByPosition.put(position, showAttractionsFragment);
                    return showAttractionsFragment;
                }
                case SHOW_VISITS:
                {
                    ShowVisitsFragment showVisitsFragment = ShowVisitsFragment.newInstance(this.parkUuid);
                    this.fragmentsByPosition.put(position, showVisitsFragment);
                    return showVisitsFragment;
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
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
        {
            super.destroyItem(container, position, object);
            this.fragmentsByPosition.remove(position);
        }

        @Override
        public int getCount()
        {
            return Tab.values().length;
        }

        public Fragment getFragment(int position)
        {
            return this.fragmentsByPosition.get(position);
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
