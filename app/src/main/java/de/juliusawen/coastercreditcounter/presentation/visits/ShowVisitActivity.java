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
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.CountableAttraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;

public class ShowVisitActivity extends BaseActivity
{
    private ShowVisitActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "ShowVisitActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_visit);
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ShowVisitActivityViewModel.class);

        if(this.viewModel.visit == null)
        {
            this.viewModel.visit = (Visit) App.content.getElementByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(this.viewModel.visit.getName(), this.viewModel.visit.getParent().getName());

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_visit_show)), getString(R.string.help_text_not_available));

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerView();
        }
        else
        {
            updateContentRecyclerView();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowVisit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        this.handleFloatingActionButtonVisibility();
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

                List<Element> allAttractions = new ArrayList<>(viewModel.visit.getParent().getChildrenOfType(Attraction.class));
                List<Attraction> addedAttractions = new ArrayList<>(CountableAttraction.getAttractions(viewModel.visit.getChildrenAsType(CountableAttraction.class)));
                allAttractions.removeAll(addedAttractions);

                ActivityTool.startActivityPickForResult(
                        ShowVisitActivity.this,
                        Constants.REQUEST_PICK_ATTRACTIONS,
                        getAttractionsWithCategoryHeaders(allAttractions));
            }
        });
    }

    private void handleFloatingActionButtonVisibility()
    {
        if(!this.allAttractionsAdded())
        {
            super.setFloatingActionButtonVisibility(true);
            this.viewModel.contentRecyclerViewAdapter.useBottomSpacer(true);
        }
        else
        {
            super.setFloatingActionButtonVisibility(false);
            this.viewModel.contentRecyclerViewAdapter.useBottomSpacer(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_PICK_ATTRACTIONS)
            {
                List<Element> selectedElements = App.content.fetchElementsByUuidStrings(data.getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));

                for(Element element : selectedElements)
                {
                    this.viewModel.visit.addChild(CountableAttraction.create((Attraction)element));
                }

                this.updateContentRecyclerView();
                this.handleFloatingActionButtonVisibility();
            }
        }
    }

    private ContentRecyclerViewAdapter createContentRecyclerView()
    {
        List<Element> categorizedAttractions = this.getAttractionsWithCategoryHeaders(this.viewModel.visit.getChildrenOfType(CountableAttraction.class));
        return ContentRecyclerViewAdapterProvider.getCountableContentRecyclerViewAdapter(
                categorizedAttractions,
                AttractionCategoryHeader.getAttractionCategoryHeadersToExpandAccordingToSettings(categorizedAttractions),
                CountableAttraction.class);
    }

    public void updateContentRecyclerView()
    {
        List<Element> categorizedCountableAttractions = this.getAttractionsWithCategoryHeaders(new ArrayList<>(this.viewModel.visit.getChildrenOfType(CountableAttraction.class)));
        this.viewModel.contentRecyclerViewAdapter.updateContent(categorizedCountableAttractions);
        this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
    }

    private boolean allAttractionsAdded()
    {
        if(this.viewModel.visit != null)
        {
            List<Attraction> allAttractions = new ArrayList<>(this.viewModel.visit.getParent().getChildrenAsType(Attraction.class));
            List<Attraction> addedAttractions = new ArrayList<>(CountableAttraction.getAttractions(this.viewModel.visit.getChildrenAsType(CountableAttraction.class)));
            allAttractions.removeAll(addedAttractions);

            Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.allAttractionsAdded:: [%d] attractions not added yet", allAttractions.size()));
            return allAttractions.size() <= 0;
        }
        else
        {
            return false;
        }
    }

    private List<Element> getAttractionsWithCategoryHeaders(List<Element> attractions)
    {
        if(!this.viewModel.attractionCategoryHeaders.isEmpty())
        {
            App.content.removeOrphanElements(Element.convertElementsToType(this.viewModel.attractionCategoryHeaders, OrphanElement.class));
        }
        this.viewModel.attractionCategoryHeaders = AttractionCategoryHeader.fetchAttractionCategoryHeadersFromElements(attractions);
        return this.viewModel.attractionCategoryHeaders;
    }
}
