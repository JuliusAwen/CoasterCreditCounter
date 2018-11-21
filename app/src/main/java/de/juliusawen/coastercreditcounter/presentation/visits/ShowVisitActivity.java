package de.juliusawen.coastercreditcounter.presentation.visits;

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
import de.juliusawen.coastercreditcounter.data.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.elements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
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

        this.viewModel = ViewModelProviders.of(this).get(ShowVisitActivityViewModel.class);

        if(this.viewModel.visit == null)
        {
            this.viewModel.visit = (Visit) App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        if(this.viewModel.attractionCategoryHeaderProvider == null)
        {
            this.viewModel.attractionCategoryHeaderProvider = new AttractionCategoryHeaderProvider();
        }

        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(this.viewModel.visit.getName(), this.viewModel.visit.getParent().getName());

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_visit_show)), getString(R.string.help_text_not_available));
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerView();
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowVisit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        if(!this.allAttractionsAdded())
        {
            super.addFloatingActionButton();
            this.decorateFloatingActionButton();
            this.viewModel.contentRecyclerViewAdapter.useBottomSpacer(true);
        }
        else
        {
            super.disableFloatingActionButton();
            this.viewModel.contentRecyclerViewAdapter.useBottomSpacer(false);
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ShowVisitActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                List<Attraction> allAttractions = new ArrayList<>(viewModel.visit.getParent().getChildrenAsType(Attraction.class));
                List<Attraction> addedAttractions = new ArrayList<>(VisitedAttraction.getAttractions(viewModel.visit.getChildrenAsType(VisitedAttraction.class)));
                allAttractions.removeAll(addedAttractions);

                ActivityTool.startActivityPickForResult(
                        ShowVisitActivity.this,
                        Constants.REQUEST_PICK_ATTRACTIONS,
                        ShowVisitActivity.this.viewModel.attractionCategoryHeaderProvider.getCategorizedAttractions(allAttractions));
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
            List<Element> resultElements = ResultTool.fetchResultElements(data);

            if(requestCode == Constants.REQUEST_PICK_ATTRACTIONS)
            {
                for(Element element : resultElements)
                {
                    Element visitedAttraction = VisitedAttraction.create((Attraction)element);
                    this.viewModel.visit.addChild(visitedAttraction);
                    App.content.addElement(visitedAttraction);
                }

                this.updateContentRecyclerView();
            }
            else if(requestCode == Constants.REQUEST_SORT_ATTRACTIONS)
            {
                Element parent = resultElements.get(0).getParent();
                if(parent != null)
                {
                    this.viewModel.visit.reorderChildren(resultElements);
                    Log.d(Constants.LOG_TAG,
                            String.format("ShowVisitActivity.onActivityResult<SortAttractions>:: replaced %s's <children> with <sorted children>", this.viewModel.visit));

                    this.updateContentRecyclerView();
                }
            }
        }
    }

    private ContentRecyclerViewAdapter createContentRecyclerView()
    {
        List<Element> categorizedVisitedAttractions =
                this.viewModel.attractionCategoryHeaderProvider.getCategorizedVisitedAttractions(this.viewModel.visit.getChildrenAsType(VisitedAttraction.class));
        return ContentRecyclerViewAdapterProvider.getCountableContentRecyclerViewAdapter(
                categorizedVisitedAttractions,
                null,
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

                if(element.isInstance(AttractionCategoryHeader.class))
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
                else if(element.isInstance(VisitedAttraction.class))
                {
                    Toaster.makeToast(ShowVisitActivity.this, element.getName() + " clicked");
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element.isInstance(AttractionCategoryHeader.class))
                {
                    AttractionCategoryHeader.handleOnAttractionCategoryHeaderLongClick(ShowVisitActivity.this, view);
                }
                else if(element.isInstance(VisitedAttraction.class))
                {
                    Toaster.makeToast(ShowVisitActivity.this, element.getName() + " long clicked");
                }

                return true;
            }
        };
    }

    private void updateContentRecyclerView()
    {
        Log.i(Constants.LOG_TAG, "ShowVisitActivity.updateContentRecyclerView:: updating RecyclerView...");

        List<Element> categorizedAttractions =
                this.viewModel.attractionCategoryHeaderProvider.getCategorizedVisitedAttractions(this.viewModel.visit.getChildrenAsType(VisitedAttraction.class));
        this.viewModel.contentRecyclerViewAdapter.updateContent(categorizedAttractions);
        this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
    }

    private boolean allAttractionsAdded()
    {
        if(this.viewModel.visit != null)
        {
            List<Attraction> allAttractions = new ArrayList<>(this.viewModel.visit.getParent().getChildrenAsType(Attraction.class));
            List<Attraction> addedAttractions = new ArrayList<>(VisitedAttraction.getAttractions(viewModel.visit.getChildrenAsType(VisitedAttraction.class)));
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
