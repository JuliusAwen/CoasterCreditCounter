package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Ride;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.objects.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowVisitActivity extends BaseActivity
{
    private ShowVisitActivityViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowVisitActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_visit);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(ShowVisitActivityViewModel.class);

            if(this.viewModel.visit == null)
            {
                this.viewModel.visit = (Visit) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            }

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(this.viewModel.visit.getName(), this.viewModel.visit.getParent().getName());

            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_visit_show)), getString(R.string.help_text_show_visit));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(App.isInitialized)
        {
            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerView();
                this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategoryHeader.class, Typeface.BOLD);
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
            this.viewModel.contentRecyclerViewAdapter.setIncreaseRideCountOnClickListener(this.getAddRideOnClickListener());
            this.viewModel.contentRecyclerViewAdapter.setDecreaseRideCountOnClickListener(this.getDeleteRideOnClickListener());

            this.recyclerView = findViewById(R.id.recyclerViewShowVisit);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            if(!this.allAttractionsAdded())
            {
                super.addFloatingActionButton();
                this.decorateFloatingActionButton();
                this.viewModel.contentRecyclerViewAdapter.addBottomSpacer();
            }
            else
            {
                super.disableFloatingActionButton();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ShowVisitActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                List<IAttraction> allAttractions = new ArrayList<IAttraction>(viewModel.visit.getParent().getChildrenAsType(IOnSiteAttraction.class));
                List<IAttraction> visitedAttractions = new ArrayList<IAttraction>(VisitedAttraction.getOnSiteAttractions(viewModel.visit.getChildrenAsType(VisitedAttraction.class)));
                allAttractions.removeAll(visitedAttractions);

                ActivityTool.startActivityPickForResult(
                        ShowVisitActivity.this,
                        Constants.REQUEST_CODE_PICK_ATTRACTIONS,
                        new ArrayList<IElement>(allAttractions));
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            List<IElement> resultElements = ResultTool.fetchResultElements(data);

            if(requestCode == Constants.REQUEST_CODE_PICK_ATTRACTIONS)
            {
                for(IElement element : resultElements)
                {
                    VisitedAttraction visitedAttraction = VisitedAttraction.create((IOnSiteAttraction) element);
                    this.viewModel.visit.addChildAndSetParent(visitedAttraction);

                    super.markForCreation(visitedAttraction);
                }

                this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.visit.getChildrenOfType(VisitedAttraction.class));

                super.markForUpdate(this.viewModel.visit);
            }
            else if(requestCode == Constants.REQUEST_CODE_SORT_ATTRACTIONS)
            {
                IElement parent = resultElements.get(0).getParent();
                if(parent != null)
                {
                    this.viewModel.visit.reorderChildren(resultElements);
                    Log.d(Constants.LOG_TAG,
                            String.format("ShowVisitActivity.onActivityResult<SortAttractions>:: replaced %s's <children> with <sorted children>", this.viewModel.visit));

                    updateContentRecyclerView(true);

                    super.markForUpdate(this.viewModel.visit);
                }
            }
        }
    }

    private ContentRecyclerViewAdapter createContentRecyclerView()
    {
        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
        childTypesToExpand.add(VisitedAttraction.class);

        return ContentRecyclerViewAdapterProvider.getCountableContentRecyclerViewAdapter(
                this.viewModel.visit.getChildrenOfType(VisitedAttraction.class),
                childTypesToExpand,
                Constants.TYPE_ATTRACTION_CATEGORY);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element instanceof AttractionCategoryHeader)
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
                else if(element instanceof Attraction)
                {
                    Toaster.makeToast(ShowVisitActivity.this, element + " clicked");
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element instanceof AttractionCategoryHeader)
                {
                    AttractionCategoryHeader.handleOnAttractionCategoryHeaderLongClick(ShowVisitActivity.this, view);
                }
                else if(element instanceof Attraction)
                {
                    Toaster.makeToast(ShowVisitActivity.this, element + " long clicked");
                }

                return true;
            }
        };
    }

    private View.OnClickListener getAddRideOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.v(Constants.LOG_TAG, String.format("ShowVisitActivity.getAddRideOnClickListener.onClick:: adding ride to %s for %s", visitedAttraction, visitedAttraction.getParent()));

                Ride ride = visitedAttraction.addRide();

                ShowVisitActivity.super.markForCreation(ride);
                ShowVisitActivity.super.markForUpdate(ShowVisitActivity.this.viewModel.visit);
                updateContentRecyclerView(false);
            }
        };
    }

    private View.OnClickListener getDeleteRideOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.v(Constants.LOG_TAG, String.format("ShowVisitActivity.getDeleteRideOnClickListener.onClick:: deleting latest ride on %s for %s",
                        visitedAttraction.getOnSiteAttraction(), visitedAttraction.getParent()));

                if(visitedAttraction.deleteLatestRide())
                {
                    ShowVisitActivity.super.markForUpdate(ShowVisitActivity.this.viewModel.visit);
                    updateContentRecyclerView(false);
                }
            }
        };
    }

    private boolean allAttractionsAdded()
    {
        if(this.viewModel.visit != null)
        {
            List<IAttraction> allAttractions = new ArrayList<IAttraction>(this.viewModel.visit.getParent().getChildrenAsType(IOnSiteAttraction.class));
            List<IAttraction> addedAttractions = new ArrayList<IAttraction>(VisitedAttraction.getOnSiteAttractions(viewModel.visit.getChildrenAsType(VisitedAttraction.class)));
            allAttractions.removeAll(addedAttractions);

            if(allAttractions.size() > 0)
            {
                Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.allAttractionsAdded:: [%d] attractions not added yet", allAttractions.size()));
            }
            else
            {
                Log.i(Constants.LOG_TAG, "ShowVisitActivity.allAttractionsAdded:: all attractions added");
            }

            return allAttractions.isEmpty();
        }
        else
        {
            return false;
        }
    }

    private void updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(Constants.LOG_TAG, "ShowVisitActivity.updateContentRecyclerView:: resetting content...");
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.visit.getChildrenOfType(VisitedAttraction.class));
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ShowVisitActivity.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
