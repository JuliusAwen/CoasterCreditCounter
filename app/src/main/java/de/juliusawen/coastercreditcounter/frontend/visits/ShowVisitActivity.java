package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ShowVisitActivity extends BaseActivity
{
    private ShowVisitActivityViewModel viewModel;

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
                this.viewModel.visit = (Visit) App.content.getContentByUuidString(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
            }

            if(this.viewModel.attractionCategoryHeaderProvider == null)
            {
                this.viewModel.attractionCategoryHeaderProvider = new AttractionCategoryHeaderProvider();
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
                this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
                this.viewModel.contentRecyclerViewAdapter.setIncreaseRideCountOnClickListener(this.getIncreaseRideCountOnClickListener());
                this.viewModel.contentRecyclerViewAdapter.setDecreaseRideCountOnClickListener(this.getDecreaseRideCountOnClickListener());
            }

            RecyclerView recyclerView = findViewById(R.id.recyclerViewShowVisit);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

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
                        Constants.REQUEST_PICK_ATTRACTIONS,
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

            if(requestCode == Constants.REQUEST_PICK_ATTRACTIONS)
            {
                for(IElement element : resultElements)
                {
                    VisitedAttraction visitedAttraction = VisitedAttraction.create((IOnSiteAttraction) element);
                    this.viewModel.visit.addChildAndSetParent(visitedAttraction);
                    App.content.addElement(visitedAttraction);

                    super.markForCreation(visitedAttraction);
                }

                List<IElement> categorizedAttractions =
                        this.viewModel.attractionCategoryHeaderProvider.getCategorizedAttractions(
                                new ArrayList<IAttraction>(this.viewModel.visit.getChildrenAsType(VisitedAttraction.class)));
                this.viewModel.contentRecyclerViewAdapter.setItems(categorizedAttractions);

                super.markForUpdate(this.viewModel.visit);
            }
            else if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS)
            {
                IElement parent = resultElements.get(0).getParent();
                if(parent != null)
                {
                    this.viewModel.visit.reorderChildren(resultElements);
                    Log.d(Constants.LOG_TAG,
                            String.format("ShowVisitActivity.onActivityResult<SortAttractions>:: replaced %s's <children> with <sorted children>", this.viewModel.visit));

                    this.viewModel.contentRecyclerViewAdapter.updateItem(
                            this.viewModel.attractionCategoryHeaderProvider.getUpdatedAttractionCategoryHeaderForAttractionsOfSameCategory(
                                    ConvertTool.convertElementsToType(resultElements, IAttraction.class)));

                    super.markForUpdate(this.viewModel.visit);
                }
            }
        }
    }

    private ContentRecyclerViewAdapter createContentRecyclerView()
    {
        List<IElement> categorizedVisitedAttractions =
                this.viewModel.attractionCategoryHeaderProvider.getCategorizedAttractions(new ArrayList<IAttraction>(this.viewModel.visit.getChildrenAsType(VisitedAttraction.class)));

        return ContentRecyclerViewAdapterProvider.getCountableContentRecyclerViewAdapter(
                categorizedVisitedAttractions,
                VisitedAttraction.class);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(AttractionCategoryHeader.class.isInstance(element))
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
                else if(Attraction.class.isInstance(element))
                {
                    Toaster.makeToast(ShowVisitActivity.this, element + " clicked");
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                Element element = (Element) view.getTag();

                if(AttractionCategoryHeader.class.isInstance(element))
                {
                    AttractionCategoryHeader.handleOnAttractionCategoryHeaderLongClick(ShowVisitActivity.this, view);
                }
                else if(Attraction.class.isInstance(element))
                {
                    Toaster.makeToast(ShowVisitActivity.this, element + " long clicked");
                }

                return true;
            }
        };
    }

    private View.OnClickListener getIncreaseRideCountOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.v(Constants.LOG_TAG, String.format("ShowVisitActivity.increaseRideCountOnClickListener.onClick:: increasing %s's ride count for %s",
                        visitedAttraction.getOnSiteAttraction(), visitedAttraction.getParent()));

                visitedAttraction.increaseRideCount(App.settings.getDefaultIncrement());
                viewModel.contentRecyclerViewAdapter.updateItem(visitedAttraction);

                ShowVisitActivity.super.markForUpdate(visitedAttraction);
            }
        };
    }

    private View.OnClickListener getDecreaseRideCountOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.v(Constants.LOG_TAG, String.format("ShowVisitActivity.decreaseRideCountOnClickListener.onClick:: decreasing %s's ride count for %s",
                        visitedAttraction.getOnSiteAttraction(), visitedAttraction.getParent()));

                if(visitedAttraction.decreaseRideCount(App.settings.getDefaultIncrement()))
                {
                    viewModel.contentRecyclerViewAdapter.updateItem(visitedAttraction);

                    ShowVisitActivity.super.markForUpdate(visitedAttraction);
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
}
