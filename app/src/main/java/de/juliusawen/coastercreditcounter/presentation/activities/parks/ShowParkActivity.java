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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.content.Visit;
import de.juliusawen.coastercreditcounter.content.YearHeader;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.ExpandableRecyclerAdapter;
import de.juliusawen.coastercreditcounter.presentation.adapters.recycler.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkAttractionsFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkOverviewFragment;
import de.juliusawen.coastercreditcounter.presentation.fragments.parks.ShowParkVisitsFragment;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;
import de.juliusawen.coastercreditcounter.toolbox.enums.Selection;

public class ShowParkActivity extends BaseActivity
{
    private Park park;

    private static final int OVERVIEW = 0;
    private static final int ATTRACTIONS = 1;
    private static final int VISITS = 2;

    private int currentTab = OVERVIEW;

    private ExpandableRecyclerAdapter attractionsRecyclerAdapter;

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

        if(this.currentTab == ATTRACTIONS && this.park.getChildCountOfInstance(Attraction.class) > 1)
        {
            menu.add(Menu.NONE, Selection.SORT_ELEMENTS.ordinal(), Menu.NONE, R.string.selection_sort_attractions);
        }

        return super.onPrepareOptionsMenu(menu);
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

    private void createTabPagerAdapter()
    {
        this.attractionsRecyclerAdapter = this.buildAttractionsRecyclerAdapter();
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), this.attractionsRecyclerAdapter, this.buildVisitsRecyclerAdapter());

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

        Log.i(Constants.LOG_TAG, String.format("ShowParkActivity.createTabPagerAdapter:: adapter created for #[%d] tabs, selected position[%d] by default", tabLayout.getTabCount(), currentTab));
    }

    private void onPageSelectedViewPager(int position)
    {
        Log.d(Constants.LOG_TAG, String.format("ShowParkActivity.onPageSelectedViewPager:: tab position %d selected", position));

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

        List<Element> preparedVisitsList = this.prepareVisitsList(this.park.getChildrenOfInstance(Visit.class));

        Element firstElement = null;
        if(!preparedVisitsList.isEmpty())
        {
             firstElement = preparedVisitsList.get(0);
        }

        ExpandableRecyclerAdapter expandableRecyclerAdapter = new ExpandableRecyclerAdapter(preparedVisitsList, recyclerOnClickListener);

        if(firstElement != null)
        {
            expandableRecyclerAdapter.expandElement(firstElement);
        }

        return expandableRecyclerAdapter;
    }

    private List<Element> prepareVisitsList(List<Element> visits)
    {
        List<Element> preparedVisits = new ArrayList<>();
        DateFormat simpleDateFormat = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT_YEAR_PATTERN, Locale.getDefault());

        for(Element element : visits)
        {
            if(element.isInstance(Visit.class))
            {
                Visit visit = (Visit) element;
                String year = String.valueOf(simpleDateFormat.format(visit.getCalendar().getTime()));


                Element existingYearHeader = null;
                for(Element yearHeader : preparedVisits)
                {
                    if(yearHeader.getName().equals(year))
                    {
                        existingYearHeader = yearHeader;
                    }
                }

                if(existingYearHeader != null)
                {
                    existingYearHeader.addChild(element);
                }
                else
                {
                    YearHeader yearHeader = YearHeader.createYearHeader(year);
                    yearHeader.addChild(element);
                    preparedVisits.add(yearHeader);
                }
            }
            else
            {
                String errorMessage = String.format(Locale.getDefault(), "element %s is not instance of Visit", element);
                Log.e(Constants.LOG_TAG, errorMessage);
                throw new IllegalStateException(errorMessage);
            }
        }

        Element.sortYearsDescending(preparedVisits);

        return preparedVisits;
    }

     public class TabPagerAdapter extends FragmentPagerAdapter
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

        TabPagerAdapter(FragmentManager fragmentManager, ExpandableRecyclerAdapter attractionsRecyclerAdapter, ExpandableRecyclerAdapter visitsRecyclerAdapter)
        {
            super(fragmentManager);
            this.attractionsRecyclerAdapter = attractionsRecyclerAdapter;
            this.visitsRecyclerAdapter = visitsRecyclerAdapter;
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.d(Constants.LOG_TAG, String.format("ShowParkActivity.TabPagerAdapter.getItem:: tab position [%d] selected", position));

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
                    this.showParkAttractionsFragment.setExpandableRecyclerAdapter(this.attractionsRecyclerAdapter);
                    this.showParkAttractionsFragment.setHasOptionsMenu(true);
                    break;
                case VISITS:
                    this.showParkVisitsFragment = (ShowParkVisitsFragment) createdFragment;
                    this.showParkVisitsFragment.setExpandableRecyclerView(this.visitsRecyclerAdapter);
                    this.showParkVisitsFragment.setHasOptionsMenu(true);
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
