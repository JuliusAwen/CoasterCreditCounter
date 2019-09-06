package de.juliusawen.coastercreditcounter.frontend.parks;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.attractions.ShowAttractionsFragment;
import de.juliusawen.coastercreditcounter.frontend.visits.ShowVisitsFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityDistributor;
import de.juliusawen.coastercreditcounter.toolbox.DrawableProvider;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowParkActivity extends BaseActivity implements ShowVisitsFragment.ShowVisitsFragmentInteraction, ShowAttractionsFragment.ShowAttractionsFragmentInteraction
{
    public enum Tab
    {
        SHOW_OVERVIEW,
        SHOW_ATTRACTIONS,
        SHOW_VISITS
    }

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

            if(this.viewModel.currentTab == null)
            {
                this.viewModel.currentTab = Tab.SHOW_OVERVIEW;
            }

            super.addHelpOverlayFragment(null, null)
                    .addToolbar()
                    .addToolbarHomeButton()
                    .addFloatingActionButton();

            this.createTabPagerAdapter();
        }
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

    private void createTabPagerAdapter()
    {
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(this.getSupportFragmentManager(), this.viewModel.park.getUuid().toString());

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
                onPageSelectedViewPager(Tab.values()[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.setCurrentItem(this.viewModel.currentTab.ordinal());
        this.onPageSelectedViewPager(this.viewModel.currentTab);

        Log.d(Constants.LOG_TAG, String.format("ShowParkActivity.createTabPagerAdapter:: adapter created with [%d] tabs, selected tab[%s]", tabLayout.getTabCount(), this.viewModel.currentTab));
    }

    private void onPageSelectedViewPager(Tab tab)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: selected tab [%s]", tab));

        this.viewModel.currentTab = tab;

        switch(tab)
        {
            case SHOW_OVERVIEW:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_overview))
                        .setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_overview)), getText(R.string.help_text_show_park_overview));
                break;

            case SHOW_ATTRACTIONS:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_attractions))
                        .setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_attractions)), getText(R.string.help_text_show_attractions));
                break;

            case SHOW_VISITS:
                super.setToolbarTitleAndSubtitle(this.viewModel.park.getName(), getString(R.string.subtitle_park_show_tab_visits))
                        .setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_visits)), getText(R.string.help_text_show_visits));
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
                super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_comment, R.color.white))
                        .setFloatingActionButtonOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                Toaster.makeToast(ShowParkActivity.this, "fab interaction for Tab SHOW_OVERVIEW not yet implemented");
                            }
                        });
                break;
            }

            case SHOW_ATTRACTIONS:
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
                break;
            }

            case SHOW_VISITS:
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
                break;
            }
        }
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
            Log.v(Constants.LOG_TAG, String.format("ShowParkActivity.TabPagerAdapter.getItem:: returning fragment for tab position [%d]", position));

            switch(Tab.values()[position])
            {
                case SHOW_OVERVIEW:
                {
                    return ShowParkOverviewFragment.newInstance(this.parkUuid);
                }
                case SHOW_ATTRACTIONS:
                {
                    return ShowAttractionsFragment.newInstance(this.parkUuid);
                }
                case SHOW_VISITS:
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

            switch (Tab.values()[position])
            {
                case SHOW_OVERVIEW:
                    this.showParkOverviewFragment = (ShowParkOverviewFragment) createdFragment;
                    break;
                case SHOW_ATTRACTIONS:
                    this.showAttractionsFragment = (ShowAttractionsFragment) createdFragment;
                    break;
                case SHOW_VISITS:
                    this.showVisitsFragment = (ShowVisitsFragment) createdFragment;
                    break;
                default:
                    String errorMessage = String.format(Locale.getDefault(), "ShowParkActivity.TabPagerAdapter.instantiateItem:: tab [%s] does not exist", Tab.values()[position]);
                    Log.e(Constants.LOG_TAG, errorMessage);
                    throw new IllegalStateException(errorMessage);
            }

            return createdFragment;
        }

        @Override
        public int getCount()
        {
            return Tab.values().length;
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
