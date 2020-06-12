package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.enums.ShowParkTab;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupItem;

public class ShowParkActivity extends BaseActivity implements
        ShowVisitsFragment.ShowVisitsFragmentInteraction,
        ShowAttractionsFragment.ShowAttractionsFragmentInteraction,
        ShowParkOverviewFragment.ShowParkOverviewFragmentInteraction
{
    private ShowParkSharedViewModel viewModel;
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
        this.viewModel = new ViewModelProvider(this).get(ShowParkSharedViewModel.class);

        if(this.viewModel.park == null)
        {
            this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.createHelpOverlayFragment(null, null);
        super.createToolbar();
        super.addToolbarHomeButton();
        super.createFloatingActionButton();

        super.setOptionsMenuButlerViewModel(this.viewModel);

        this.createTabPagerAdapter();
        this.viewPager.setCurrentItem(getIntent().getIntExtra(Constants.EXTRA_DEFAULT_TAB, 0));
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
    protected boolean handleOptionsItemSelected(MenuItem item)
    {
        if(this.getCurrentTab() == ShowParkTab.SHOW_VISITS)
        {
            switch(super.getOptionsItem(item))
            {
                case SORT_ASCENDING:
                    this.showVisitsFragment.sortAscending();
                    return true;

                case SORT_DESCENDING:
                    this.showVisitsFragment.sortDecending();
                    return true;
            }
        }

        return super.handleOptionsItemSelected(item);
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

            case SHOW_PARK_OVERVIEW:
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
    public View.OnClickListener createOnElementTypeClickListener(ElementType elementType)
    {
        return super.createOnElementTypeClickListener(elementType);
    }

    @Override
    protected void handleOnElementTypeClick(ElementType elementType, View view)
    {
        switch(this.getCurrentTab())
        {
            case SHOW_PARK_OVERVIEW:
                break;

            case SHOW_ATTRACTIONS:
                this.showAttractionsFragment.handleOnElementTypeClick(elementType, view);
                break;

            case SHOW_VISITS:
                this.showVisitsFragment.handleOnElementTypeClick(elementType, view);
                break;

            default:
                super.handleOnElementTypeClick(elementType, view);
        }
    }

    @Override
    public View.OnLongClickListener createOnElementTypeLongClickListener(ElementType elementType)
    {
        return super.createOnElementTypeLongClickListener(elementType);
    }

    @Override
    protected boolean handleOnElementTypeLongClick(ElementType elementType, View view)
    {
        switch(this.getCurrentTab())
        {
            case SHOW_ATTRACTIONS:
                return this.showAttractionsFragment.handleOnElementTypeLongClick(elementType, view);

            case SHOW_VISITS:
                return this.showVisitsFragment.handleOnElementTypeLongClick(elementType, view);

            default:
                return super.handleOnElementTypeLongClick(elementType, view);
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

    private ShowParkTab getCurrentTab()
    {
        return ShowParkTab.getValue(this.viewPager.getCurrentItem());
    }

    private void createTabPagerAdapter()
    {
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(this.getSupportFragmentManager());

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
                onPageSelectedViewPager(ShowParkTab.values()[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        this.viewPager.setCurrentItem(this.viewPager.getCurrentItem());
        this.onPageSelectedViewPager(ShowParkTab.getValue(this.viewPager.getCurrentItem()));

        Log.d(String.format(Locale.getDefault(), "created with [%d] tabs, selected %s", tabLayout.getTabCount(), ShowParkTab.getValue(this.viewPager.getCurrentItem())));
    }

    private void onPageSelectedViewPager(ShowParkTab showParkTab)
    {
        Log.i(String.format("selected %s", showParkTab));

        switch(showParkTab)
        {
            case SHOW_PARK_OVERVIEW:
                super.setToolbarTitleAndSubtitle(getString(R.string.subtitle_park_show_tab_overview), this.viewModel.park.getName());
                super.setHelpOverlayTitleAndMessage(
                        getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_overview)),
                        getString(R.string.help_text_show_park_overview));
                break;

            case SHOW_ATTRACTIONS:
                super.setToolbarTitleAndSubtitle(getString(R.string.subtitle_park_show_tab_attractions), this.viewModel.park.getName());
                super.setHelpOverlayTitleAndMessage(
                        getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_attractions)),
                        getString(R.string.help_text_show_attractions));
                break;

            case SHOW_VISITS:
                super.setToolbarTitleAndSubtitle(getString(R.string.subtitle_park_show_tab_visits), this.viewModel.park.getName());
                super.setHelpOverlayTitleAndMessage(
                        getString(R.string.title_help, getString(R.string.subtitle_park_show_tab_visits)),
                        getString(R.string.help_text_show_visits));
                break;
        }

        this.decorateFloatingActionButton(showParkTab);
    }

    private void decorateFloatingActionButton(ShowParkTab showParkTab)
    {
        switch(showParkTab)
        {
            case SHOW_PARK_OVERVIEW:
            {
                super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.comment, R.color.white));
                super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Log.i("FloatingActionButton clicked");

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
                super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.add, R.color.white));
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
                super.animateFloatingActionButtonTransition(DrawableProvider.getColoredDrawable(R.drawable.add, R.color.white));
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
        private final Drawable[] tabTitleDrawables = new Drawable[]
                {
                        DrawableProvider.getColoredDrawable(R.drawable.home, R.color.white),
                        DrawableProvider.getColoredDrawable(R.drawable.airline_seat_legroom_extra, R.color.white),
                        DrawableProvider.getColoredDrawable(R.drawable.local_activity, R.color.white)
                };

        TabPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            Log.d("ShowParkActivity.TabPagerAdapter.Constructor:: instantiating adapter...");
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.v(String.format(Locale.getDefault(), "returning fragment for tab position [%d]", position));

            switch(ShowParkTab.getValue(position))
            {
                case SHOW_PARK_OVERVIEW:
                {
                    return ShowParkOverviewFragment.newInstance();
                }

                case SHOW_ATTRACTIONS:
                {
                    return ShowAttractionsFragment.newInstance();
                }

                case SHOW_VISITS:
                {
                    return ShowVisitsFragment.newInstance();
                }

                default:
                    Log.e(String.format(Locale.getDefault(), "tab position [%d] does not exist", position));
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            Fragment instantiatedFragment = (Fragment)super.instantiateItem(container, position);

            switch(ShowParkTab.getValue(position))
            {
                case SHOW_PARK_OVERVIEW:
                {
                    showParkOverviewFragment = (ShowParkOverviewFragment) instantiatedFragment;
                    break;
                }

                case SHOW_ATTRACTIONS:
                {
                    showAttractionsFragment = (ShowAttractionsFragment) instantiatedFragment;
                    break;
                }

                case SHOW_VISITS:
                {
                    showVisitsFragment = (ShowVisitsFragment) instantiatedFragment;
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
            return ShowParkTab.values().length;
        }

        View getTabTitleView(int position)
        {
            View view = LayoutInflater.from(ShowParkActivity.this).inflate(R.layout.layout_tab_title, null);
            ImageView imageView = view.findViewById(R.id.imageViewTabTitle);
            imageView.setImageDrawable(this.tabTitleDrawables[position]);

            return view;
        }
    }
}
