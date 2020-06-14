package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OnClickListenerType;

public class ShowVisitActivity extends BaseActivity
{
    private ShowVisitViewModel viewModel;

    protected void setContentView()
    {
        setContentView(R.layout.activity_show_visit);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ShowVisitViewModel.class);

        if(this.viewModel.visit == null)
        {
            this.viewModel.visit = (Visit)App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.getValue(getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0));
            Log.d(String.format("%s", this.viewModel.requestCode));
        }

        if(this.viewModel.adapterFacade == null)
        {
            this.viewModel.adapterFacade = new ContentRecyclerViewAdapterFacade();

            this.viewModel.adapterFacade.getConfiguration()
                    .addOnElementTypeClickListener(ElementType.GROUP_HEADER, super.createOnElementTypeClickListener(ElementType.GROUP_HEADER))
                    .addOnElementTypeClickListener(ElementType.VISITED_ATTRACTION, super.createOnElementTypeClickListener(ElementType.VISITED_ATTRACTION))
                    .addOnElementTypeLongClickListener(ElementType.GROUP_HEADER, super.createOnElementTypeLongClickListener(ElementType.GROUP_HEADER))
                    .addOnClickListener(OnClickListenerType.INCREASE_RIDE_COUNT, this.createIncreaseRideCountOnClickListener())
                    .addOnClickListener(OnClickListenerType.DECREASE_RIDE_COUNT, this.createDecreaseRideCountOnClickListener())
                    .addOnClickListener(OnClickListenerType.REMOVE_VISITED_ATTRACTION, this.createOnRemoveVisitedAttractionClickListener());

            this.viewModel.adapterFacade.createPreconfiguredAdapter(this.viewModel.requestCode);

            this.updateContentRecyclerView();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowVisit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter((ContentRecyclerViewAdapter) this.viewModel.adapterFacade.getAdapter());


        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_show_visit)), getString(R.string.help_text_show_visit));
        super.createToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(this.viewModel.visit.getName(), this.viewModel.visit.getParent().getName());

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();

        super.setOptionsMenuButlerViewModel(this.viewModel);
    }

    @Override
    protected void resume()
    {
        invalidateOptionsMenu();

        if(Visit.isCurrentVisit(this.viewModel.visit))
        {
            this.viewModel.visit.setEditingEnabled(true);
        }

        if(this.viewModel.visit.isEditingEnabled())
        {
            this.viewModel.adapterFacade.getAdapter().setFormatAsPrettyPrint(false);
        }
        else
        {
            this.viewModel.adapterFacade.getAdapter().setFormatAsPrettyPrint(true);
        }

        this.setFloatingActionButtonVisibility(true);

        Log.d(String.format("%s isEditingEnabled[%S]", this.viewModel.visit, this.viewModel.visit.isEditingEnabled()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(super.getOptionsItem(item))
        {
            case ENABLE_EDITING:
            {
                this.viewModel.visit.setEditingEnabled(true);
                invalidateOptionsMenu();
                this.viewModel.adapterFacade.getAdapter().setFormatAsPrettyPrint(false);
                this.setFloatingActionButtonVisibility(true);

                Log.d(String.format("<ENABLE_EDITING> enabled editing for %s", this.viewModel.visit));
                return true;
            }

            case DISABLE_EDITING:
            {
                this.viewModel.visit.setEditingEnabled(false);
                invalidateOptionsMenu();
                this.viewModel.adapterFacade.getAdapter().setFormatAsPrettyPrint(true);
                this.setFloatingActionButtonVisibility(false);

                Log.d(String.format("<DISABLE_EDITING> disabled editing %s", this.viewModel.visit));
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(String.format("%s, %s", RequestCode.getValue(requestCode), StringTool.resultCodeToString(resultCode)));

        if(resultCode == RESULT_OK)
        {
            List<IElement> resultElements = ResultFetcher.fetchResultElements(data);

            switch(RequestCode.getValue(requestCode))
            {
                case PICK_ATTRACTIONS:
                {
                    for(IElement element : resultElements)
                    {
                        VisitedAttraction visitedAttraction = VisitedAttraction.create((OnSiteAttraction) element);
                        this.viewModel.visit.addChildAndSetParent(visitedAttraction);
                    }

                    this.updateContentRecyclerView();
                    super.markForUpdate(this.viewModel.visit);
                    break;
                }

                case SORT_ATTRACTIONS:
                {
                    IElement parent = resultElements.get(0).getParent();
                    if(parent != null)
                    {
                        this.viewModel.visit.reorderChildren(resultElements);
                        Log.d(String.format("<SortAttractions> replaced %s's <children> with <sorted children>", this.viewModel.visit));

                        updateContentRecyclerView();

                        super.markForUpdate(this.viewModel.visit);
                    }
                    break;
                }
            }
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("FloatingActionButton clicked");
                handleOnFloatingActionButtonClick();
            }
        });
    }

    private void handleOnFloatingActionButtonClick()
    {
        ActivityDistributor.startActivityPickForResult(
                ShowVisitActivity.this,
                RequestCode.PICK_ATTRACTIONS,
                new LinkedList<IElement>(getNotYetAddedAttractionsWithDefaultStatus()));
    }

    @Override
    protected void setFloatingActionButtonVisibility(boolean isVisible)
    {
        if(this.allAttractionsAdded() || !this.viewModel.visit.isEditingEnabled())
        {
            super.setFloatingActionButtonVisibility(false);
        }
        else
        {
            super.setFloatingActionButtonVisibility(isVisible);
        }
    }

    @Override
    protected void handleOnElementTypeClick(ElementType elementType, View view)
    {
        IElement element = (IElement) view.getTag();
        switch(elementType)
        {
            case GROUP_HEADER:
                this.handleOnGroupHeaderClick(element);
                break;

            case VISITED_ATTRACTION:
                this.handleOnVisitedAttractionClick(element);
                break;

            default:
                super.handleOnElementTypeClick(elementType, view);
                break;
        }
    }

    private void handleOnGroupHeaderClick(IElement element)
    {
        this.viewModel.adapterFacade.getAdapter().toggleExpansion(element);
        if(this.viewModel.adapterFacade.getAdapter().isAllContentExpanded() || this.viewModel.adapterFacade.getAdapter().isAllContentCollapsed())
        {
            invalidateOptionsMenu();
        }
    }

    private void handleOnVisitedAttractionClick(IElement element)
    {
        Toaster.notYetImplemented(ShowVisitActivity.this);
    }

    @Override
    protected boolean handleOnElementTypeLongClick(ElementType elementType, View view)
    {
        this.viewModel.longClickedElement = (Element) view.getTag();

        if(elementType == ElementType.GROUP_HEADER)
        {
            return this.handleOnGroupHeaderLongClick(view);
        }

        return super.handleOnElementTypeLongClick(elementType, view);
    }

    private boolean handleOnGroupHeaderLongClick(View view)
    {
        boolean sortAttractionsIsEnabled =
                this.viewModel.longClickedElement.getChildCountOfType(Attraction.class) > 1 || this.viewModel.longClickedElement.getChildCountOfType(VisitedAttraction.class) > 1;

        PopupMenuAgent.getMenu()
                .add(PopupItem.SORT_ATTRACTIONS)
                .setEnabled(PopupItem.SORT_ATTRACTIONS, sortAttractionsIsEnabled)
                .show(ShowVisitActivity.this, view);

        return true;
    }

    @Override
    public void handlePopupItemClicked(PopupItem item)
    {
        if(item == PopupItem.SORT_ATTRACTIONS)
        {
            List<IElement> attractions = new ArrayList<>();
            if(this.viewModel.longClickedElement.hasChildrenOfType(Attraction.class))
            {
                attractions = this.viewModel.longClickedElement.getChildrenOfType(Attraction.class);
            }
            else if(this.viewModel.longClickedElement.hasChildrenOfType(VisitedAttraction.class))
            {
                attractions = this.viewModel.longClickedElement.getChildrenOfType(VisitedAttraction.class);
            }
            ActivityDistributor.startActivitySortForResult(ShowVisitActivity.this, RequestCode.SORT_ATTRACTIONS, attractions);
        }
    }

    private View.OnClickListener createIncreaseRideCountOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnIncreaseRideCountClick((VisitedAttraction) view.getTag());
            }
        };
    }

    private void handleOnIncreaseRideCountClick(VisitedAttraction visitedAttraction)
    {
        Log.v(String.format("increasing %s's ride count for %s", visitedAttraction, visitedAttraction.getParent()));
        visitedAttraction.increaseTrackedRideCount(App.preferences.getIncrement());
        this.viewModel.adapterFacade.getAdapter().notifyItemChanged(visitedAttraction);
        super.markForUpdate(ShowVisitActivity.this.viewModel.visit);
    }

    private View.OnClickListener createDecreaseRideCountOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnDecreaseRideCountClick((VisitedAttraction) view.getTag());
            }
        };
    }

    private void handleOnDecreaseRideCountClick(VisitedAttraction visitedAttraction)
    {
        Log.v(String.format("decreasing %s's ride count for %s", visitedAttraction.getOnSiteAttraction(), visitedAttraction.getParent()));
        visitedAttraction.decreaseTrackedRideCount(App.preferences.getIncrement());
        this.viewModel.adapterFacade.getAdapter().notifyItemChanged(visitedAttraction);
        super.markForUpdate(ShowVisitActivity.this.viewModel.visit);
    }

    private View.OnClickListener createOnRemoveVisitedAttractionClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnRemoveVisitedAttractionClick((VisitedAttraction) view.getTag());
            }
        };
    }

    private void handleOnRemoveVisitedAttractionClick(VisitedAttraction visitedAttraction)
    {
        Log.d(String.format("removing %s...", visitedAttraction));
        this.viewModel.adapterFacade.getAdapter().removeItem(visitedAttraction);
        this.setFloatingActionButtonVisibility(true);
        super.markForUpdate(ShowVisitActivity.this.viewModel.visit);
    }


    private boolean allAttractionsAdded()
    {
        if(this.viewModel.visit != null)
        {
            List<IAttraction> notYetAddedAttractions = this.getNotYetAddedAttractionsWithDefaultStatus();
            if(notYetAddedAttractions.size() > 0)
            {
                Log.i(String.format(Locale.getDefault(), "[%d] attractions not added yet", notYetAddedAttractions.size()));
            }
            else
            {
                Log.i("all attractions added");
            }

            return notYetAddedAttractions.isEmpty();
        }
        else
        {
            return false;
        }
    }

    private List<IAttraction> getNotYetAddedAttractionsWithDefaultStatus()
    {
        List<IAttraction> visitedAttractions = new ArrayList<>();
        for(VisitedAttraction visitedAttraction : viewModel.visit.getChildrenAsType(VisitedAttraction.class))
        {
            visitedAttractions.add(visitedAttraction.getOnSiteAttraction());
        }

        List<IAttraction> allAttractions = new LinkedList<IAttraction>(this.viewModel.visit.getParent().getChildrenAsType(OnSiteAttraction.class));
        allAttractions.removeAll(visitedAttractions);

        List<IAttraction> attractionsWithDefaultStatus = new LinkedList<>();
        for(IAttraction attraction : allAttractions)
        {
            if(attraction.getStatus().isDefault())
            {
                attractionsWithDefaultStatus.add(attraction);
            }
        }
        
        return attractionsWithDefaultStatus;
    }

    private void updateContentRecyclerView()
    {
        Log.d("resetting content...");
        this.viewModel.adapterFacade.getAdapter().setContent(this.viewModel.visit.getChildrenOfType(VisitedAttraction.class));
        this.viewModel.adapterFacade.getAdapter().groupContent(GroupType.CATEGORY);
        this.viewModel.adapterFacade.getAdapter().expandAllContent();
    }
}
