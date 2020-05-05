package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;

import static de.juliusawen.coastercreditcounter.application.Constants.LOG_TAG;

public class ShowParkActivity extends BaseActivity
        implements ShowVisitsFragment.ShowVisitsFragmentInteraction, ShowAttractionsFragment.ShowAttractionsFragmentInteraction, ShowParkOverviewFragment.ShowParkOverviewFragmentInteraction
{
    private enum Tab
    {
        SHOW_OVERVIEW,
        SHOW_ATTRACTIONS,
        SHOW_VISITS
    }

    private ShowParkActivityViewModel viewModel;
    private ViewPager viewPager;

    private ShowParkOverviewFragment showParkOverviewFragment;
    private ShowAttractionsFragment showAttractionsFragment;
    private ShowVisitsFragment showVisitsFragment;


    protected void setContentView()
    {
        setContentView(R.layout.activity_show_park);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ShowParkActivityViewModel.class);

        if(this.viewModel.park == null)
        {
            this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        super.createHelpOverlayFragment(null, null);
        super.createToolbar()
                .addToolbarHomeButton()
                .createFloatingActionButton();

        this.createTabPagerAdapter();
    }

    @Override
    public void onDestroy()
    {
        getSupportFragmentManager().getFragments().clear();
        this.showParkOverviewFragment = null;
        this.showAttractionsFragment = null;
        this.showVisitsFragment = null;
        this.viewPager = null;
        super.onDestroy();
    }

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
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        if(super.handleOptionsItemSelected(item))
        {
            return true;
        }

        switch(this.getCurrentTab())
        {
            case SHOW_ATTRACTIONS:
            {
                switch(item)
                {
                    case EXPAND_ALL:
                        this.showAttractionsFragment.expandAll();
                        break;

                    case COLLAPSE_ALL:
                        this.showAttractionsFragment.collapseAll();
                        break;
                }
                break;
            }

            case SHOW_VISITS:
            {
                switch(item)
                {
                    case SORT_ASCENDING:
                        this.showVisitsFragment.sortAscending();
                        break;

                    case SORT_DESCENDING:
                        this.showVisitsFragment.sortDecending();
                        break;

                    case EXPAND_ALL:
                        this.showVisitsFragment.expandAll();
                        break;

                    case COLLAPSE_ALL:
                        this.showVisitsFragment.collapseAll();
                        break;

                    case SORT:
                        break;
                }
                break;
            }

            default:
                return false;
        }

        return true;
    }

    @Override
    public void handlePopupItemClicked(PopupItem item)
    {
        switch(this.getCurrentTab())
        {
            case SHOW_ATTRACTIONS:
            {
                switch(item)
                {
                    case DELETE_ATTRACTION:
                        this.showAttractionsFragment.handlePopupItemDeleteAttractionClicked();
                        break;

                    case EDIT_ATTRACTION:
                        this.showAttractionsFragment.handlePopupItemEditAttractionClicked();
                        break;

                    case SORT_ATTRACTIONS:
                        this.showAttractionsFragment.handlePopupItemSortAttractionsClicked();
                        break;
                }
                break;
            }

            case SHOW_VISITS:
            {
                switch(item)
                {
                    case DELETE_ELEMENT:
                        this.showVisitsFragment.handlePopupItemDeleteElementClicked();
                        break;

                    case EDIT_ELEMENT:
                        this.showVisitsFragment.handlePopupItemEditElementClicked();
                        break;
                }
                break;
            }

            case SHOW_OVERVIEW:
            {
                if(item == PopupItem.DELETE_ELEMENT)
                {
                    this.showParkOverviewFragment.handlePopupItemDeleteElementClicked();
                }
                break;
            }



            default:
                break;
        }
    }

    @Override
    public void setFloatingActionButtonVisibility(boolean isVisible)
    {
        super.setFloatingActionButtonVisibility(isVisible);
    }

    @Override
    public void markForCreation(IElement elementToCreate)
    {
        super.markForCreation(elementToCreate);
    }

    @Override
    public void markForDeletion(IElement elementToDelete, boolean deleteDescendants)
    {
        super.markForDeletion(elementToDelete, deleteDescendants);
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
            (tab).setCustomView(tabPagerAdapter.getTabTitleView(i));
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
                super.setToolbarTitleAndSubtitle(getString(R.string.subtitle_park_show_tab_overview), this.viewModel.park.getName());
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_overview)), getString(R.string.help_text_show_park_overview));
                break;

            case SHOW_ATTRACTIONS:
                super.setToolbarTitleAndSubtitle(getString(R.string.subtitle_park_show_tab_attractions), this.viewModel.park.getName());
                super.setHelpOverlayTitleAndMessage(getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_attractions)), getString(R.string.help_text_show_attractions));
                break;

            case SHOW_VISITS:
                super.setToolbarTitleAndSubtitle(getString(R.string.subtitle_park_show_tab_visits), this.viewModel.park.getName());
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
                                Log.i(LOG_TAG, "ShowParkActivity.onClickFloatingActionButton:: FloatingActionButton clicked");

                                Note note = viewModel.park.getNote();
                                if(note != null)
                                {
                                    ActivityDistributor.startActivityEditForResult(ShowParkActivity.this, RequestCode.EDIT_NOTE, note);
                                }
                                else
                                {
                                    ActivityDistributor.startActivityCreateForResult(ShowParkActivity.this, RequestCode.CREATE_NOTE, ShowParkActivity.this.viewModel.park);
                                }
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
                                ActivityDistributor.startActivityCreateForResult(ShowParkActivity.this, RequestCode.CREATE_ATTRACTION, viewModel.park);
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

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            Fragment instantiatedFragment = (Fragment)super.instantiateItem(container, position);

            switch(Tab.values()[position])
            {
                case SHOW_OVERVIEW:
                {
                    showParkOverviewFragment = (ShowParkOverviewFragment)instantiatedFragment;
                    break;
                }
                case SHOW_ATTRACTIONS:
                {
                    showAttractionsFragment = (ShowAttractionsFragment)instantiatedFragment;
                    break;
                }
                case SHOW_VISITS:
                {
                    showVisitsFragment = (ShowVisitsFragment)instantiatedFragment;
                    break;
                }
            }
            return instantiatedFragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount()
        {
            return Tab.values().length;
        }

        View getTabTitleView(int position)
        {
            View view = LayoutInflater.from(ShowParkActivity.this).inflate(R.layout.layout_tab_title, null);
            ImageView imageView = view.findViewById(R.id.imageViewTabTitle);
            imageView.setImageDrawable(tabTitleDrawables[position]);
            return view;
        }
    }
}
