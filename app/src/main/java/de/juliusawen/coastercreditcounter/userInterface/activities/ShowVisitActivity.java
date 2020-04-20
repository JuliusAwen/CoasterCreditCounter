package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

import static de.juliusawen.coastercreditcounter.application.Constants.LOG_TAG;

public class ShowVisitActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowVisitActivityViewModel viewModel;
    private RecyclerView recyclerView;


    protected void setContentView()
    {
        setContentView(R.layout.activity_show_visit);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ShowVisitActivityViewModel.class);

        if(this.viewModel.visit == null)
        {
            this.viewModel.visit = (Visit)App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
        }

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_show_visit)), getString(R.string.help_text_show_visit));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(this.viewModel.visit.getName(), this.viewModel.visit.getParent().getName())
                .setToolbarOnClickListener(this.getToolbarOnClickListener());
    }

    @Override
    protected void resume()
    {
        invalidateOptionsMenu();

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerView()
                    .setTypefaceForContentType(GroupHeader.class, Typeface.BOLD);
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener())
                .addIncreaseRideCountOnClickListener(this.getIncreaseRideCountOnClickListener())
                .addDecreaseRideCountOnClickListener(this.getDecreaseRideCountOnClickListener());

        this.recyclerView = findViewById(R.id.recyclerViewShowVisit);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        if(Visit.isCurrentVisit(this.viewModel.visit))
        {
            this.viewModel.visit.setEditingEnabled(true);
        }

        if(this.viewModel.visit.isEditingEnabled())
        {
            this.viewModel.contentRecyclerViewAdapter.setFormatAsPrettyPrint(false);
        }
        else
        {
            this.viewModel.contentRecyclerViewAdapter.setFormatAsPrettyPrint(true);
        }

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();
        this.handleFloatingActionButtonVisibility();

        Log.d(LOG_TAG, String.format("ShowVisitActivity.resume:: %s isEditingEnabled[%S]", this.viewModel.visit, this.viewModel.visit.isEditingEnabled()));

        this.decorateFloatingActionButton();
    }

    @Override
    public void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .add(OptionsItem.DISABLE_EDITING)
                .add(OptionsItem.ENABLE_EDITING)
                .add(OptionsItem.EXPAND_ALL)
                .add(OptionsItem.COLLAPSE_ALL)
                .create(menu);
    }

    @Override
    protected Menu prepareOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .setVisible(OptionsItem.DISABLE_EDITING, this.viewModel.visit.isEditingEnabled())
                .setVisible(OptionsItem.ENABLE_EDITING, !this.viewModel.visit.isEditingEnabled())
                .setVisible(OptionsItem.EXPAND_ALL, this.viewModel.visit.isEditingEnabled()
                        && this.viewModel.visit.hasChildrenOfType(VisitedAttraction.class)
                        && !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .setVisible(OptionsItem.COLLAPSE_ALL, this.viewModel.visit.isEditingEnabled()
                        && this.viewModel.visit.hasChildrenOfType(VisitedAttraction.class)
                        && this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .prepare(menu);
    }

    @Override
    public boolean handleOptionsItemSelected(OptionsItem item)
    {
        switch(item)
        {
            case EXPAND_ALL:
                this.viewModel.contentRecyclerViewAdapter.expandAll();
                invalidateOptionsMenu();
                return true;

            case COLLAPSE_ALL:
                this.viewModel.contentRecyclerViewAdapter.collapseAll();
                invalidateOptionsMenu();
                return true;

            case ENABLE_EDITING:
                this.viewModel.visit.setEditingEnabled(true);
                invalidateOptionsMenu();
                this.viewModel.contentRecyclerViewAdapter.setFormatAsPrettyPrint(false);
                this.handleFloatingActionButtonVisibility();
                Log.d(LOG_TAG, String.format("ShowVisitActivity.onOptionsItemSelected<ENABLE_EDITING>:: enabled editing for %s", this.viewModel.visit));
                return true;

            case DISABLE_EDITING:
                this.viewModel.visit.setEditingEnabled(false);
                invalidateOptionsMenu();
                this.viewModel.contentRecyclerViewAdapter.setFormatAsPrettyPrint(true);
                this.handleFloatingActionButtonVisibility();
                Log.d(LOG_TAG, String.format("ShowVisitActivity.onOptionsItemSelected<DISABLE_EDITING>:: disabled editing %s", this.viewModel.visit));
                return true;

            default:
                return super.handleOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(LOG_TAG, String.format("ShowVisitActivity.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode == RESULT_OK)
        {
            List<IElement> resultElements = ResultFetcher.fetchResultElements(data);

            switch(RequestCode.values()[requestCode])
            {
                case PICK_ATTRACTIONS:
                {
                    for(IElement element : resultElements)
                    {
                        VisitedAttraction visitedAttraction = VisitedAttraction.create((IOnSiteAttraction) element);
                        this.viewModel.visit.addChildAndSetParent(visitedAttraction);
                    }

                    this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.visit.getChildrenOfType(VisitedAttraction.class));

                    super.markForUpdate(this.viewModel.visit);
                    break;
                }

                case SORT_ATTRACTIONS:
                {
                    IElement parent = resultElements.get(0).getParent();
                    if(parent != null)
                    {
                        this.viewModel.visit.reorderChildren(resultElements);
                        Log.d(LOG_TAG,
                                String.format("ShowVisitActivity.onActivityResult<SortAttractions>:: replaced %s's <children> with <sorted children>", this.viewModel.visit));

                        updateContentRecyclerView(true);

                        super.markForUpdate(this.viewModel.visit);
                    }
                    break;
                }
            }
        }
    }

    private View.OnClickListener getToolbarOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityDistributor.startActivityShow(ShowVisitActivity.this, RequestCode.SHOW_PARK, viewModel.visit.getParent(), true);
            }
        };
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(LOG_TAG, "ShowVisitActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                ActivityDistributor.startActivityPickForResult(
                        ShowVisitActivity.this,
                        RequestCode.PICK_ATTRACTIONS,
                        new LinkedList<IElement>(getNotYetAddedAttractionsWithDefaultStatus()));

            }
        });
    }

    private void handleFloatingActionButtonVisibility()
    {
        if(this.allAttractionsAdded() || !this.viewModel.visit.isEditingEnabled())
        {
            super.setFloatingActionButtonVisibility(false);
        }
        else
        {
            super.setFloatingActionButtonVisibility(true);
            this.viewModel.contentRecyclerViewAdapter.addBottomSpacer();
        }
    }

    private ContentRecyclerViewAdapter createContentRecyclerView()
    {
        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
        childTypesToExpand.add(VisitedAttraction.class);

        return ContentRecyclerViewAdapterProvider.getCountableContentRecyclerViewAdapter(this.viewModel.visit.getChildrenOfType(VisitedAttraction.class), childTypesToExpand)
                .groupItems(GroupType.CATEGORY);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element.isGroupHeader())
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                    if(viewModel.contentRecyclerViewAdapter.isAllExpanded() || viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                    {
                        invalidateOptionsMenu();
                    }
                }
                else if(element.isAttraction())
                {
                    Toaster.makeShortToast(ShowVisitActivity.this, element + " clicked");
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                viewModel.longClickedElement = (Element) view.getTag();

                if(viewModel.longClickedElement.isGroupHeader())
                {
                    boolean sortAttractionsIsEnabled =
                            viewModel.longClickedElement.getChildCountOfType(Attraction.class) > 1 || viewModel.longClickedElement.getChildCountOfType(VisitedAttraction.class) > 1;

                    PopupMenuAgent.getMenu()
                            .add(PopupItem.SORT_ATTRACTIONS)
                            .setEnabled(PopupItem.SORT_ATTRACTIONS, sortAttractionsIsEnabled)
                            .show(ShowVisitActivity.this, view);
                }
                else if(viewModel.longClickedElement.isAttraction())
                {
                    PopupMenuAgent.getMenu()
                            .add(PopupItem.REMOVE_ELEMENT)
                            .show(ShowVisitActivity.this, view);
                }

                return true;
            }
        };
    }

    @Override
    public void handlePopupItemClicked(PopupItem item)
    {
        switch(item)
        {
            case SORT_ATTRACTIONS:
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
                break;

            case REMOVE_ELEMENT:
                if(((VisitedAttraction)this.viewModel.longClickedElement).fetchTotalRideCount() > 0)
                {
                    AlertDialogFragment alertDialogFragmentRemove =
                            AlertDialogFragment.newInstance(
                                    R.drawable.ic_baseline_warning,
                                    getString(R.string.alert_dialog_title_remove),
                                    getString(R.string.alert_dialog_message_remove_visited_attraction, viewModel.longClickedElement.getName()),
                                    getString(R.string.text_accept),
                                    getString(R.string.text_cancel),
                                    RequestCode.REMOVE,
                                    false);

                    alertDialogFragmentRemove.setCancelable(false);
                    alertDialogFragmentRemove.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
                }
                else
                {
                    removeVisitedAttraction();
                }
                break;
        }
    }


    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            if(requestCode == RequestCode.REMOVE)
            {
                super.setFloatingActionButtonVisibility(false);

                ConfirmSnackbar.Show(
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                getString(R.string.action_confirm_remove_text, viewModel.longClickedElement.getName()),
                                Snackbar.LENGTH_LONG),
                        requestCode,
                        this);
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitActivity.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        if(requestCode == RequestCode.REMOVE)
        {
            removeVisitedAttraction();
        }
        super.setFloatingActionButtonVisibility(true);
    }

    private void removeVisitedAttraction()
    {
        Log.i(LOG_TAG, String.format("ShowVisitActivity.removeVisitedAttraction:: removing %s...", viewModel.longClickedElement));

        super.markForUpdate(this.viewModel.longClickedElement.getParent());
        this.viewModel.longClickedElement.deleteElementAndDescendants();
        updateContentRecyclerView(true);
        this.handleFloatingActionButtonVisibility();
    }

    private View.OnClickListener getIncreaseRideCountOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.v(LOG_TAG, String.format("ShowVisitActivity.getIncreaseRideCountOnClickListener.onClick:: increasing %s's ride count for %s", visitedAttraction, visitedAttraction.getParent()));

                visitedAttraction.increaseTrackedRideCount(App.preferences.getIncrement());
                ShowVisitActivity.super.markForUpdate(ShowVisitActivity.this.viewModel.visit);
                updateContentRecyclerView(false);
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

                Log.v(LOG_TAG, String.format("ShowVisitActivity.getDecreaseRideCountOnClickListener.onClick:: decreasing %s's ride count for %s",
                        visitedAttraction.getOnSiteAttraction(), visitedAttraction.getParent()));

                visitedAttraction.decreaseTrackedRideCount(App.preferences.getIncrement());
                ShowVisitActivity.super.markForUpdate(ShowVisitActivity.this.viewModel.visit);
                updateContentRecyclerView(false);
            }
        };
    }

    private boolean allAttractionsAdded()
    {
        if(this.viewModel.visit != null)
        {
            List<IAttraction> notYetAddedAttractions = this.getNotYetAddedAttractionsWithDefaultStatus();
            if(notYetAddedAttractions.size() > 0)
            {
                Log.i(LOG_TAG, String.format("ShowVisitActivity.allAttractionsAdded:: [%d] attractions not added yet", notYetAddedAttractions.size()));
            }
            else
            {
                Log.i(LOG_TAG, "ShowVisitActivity.allAttractionsAdded:: all attractions added");
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

        List<IAttraction> allAttractions = new LinkedList<IAttraction>(this.viewModel.visit.getParent().getChildrenAsType(IOnSiteAttraction.class));
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

    private void updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(LOG_TAG, "ShowVisitActivity.updateContentRecyclerView:: resetting content...");
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.visit.getChildrenOfType(VisitedAttraction.class))
                    .expandAll();

            if(!this.allAttractionsAdded())
            {
                this.viewModel.contentRecyclerViewAdapter.addBottomSpacer();
            }
        }
        else
        {
            Log.d(LOG_TAG, "ShowVisitActivity.updateContentRecyclerView:: notifying data set changed...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
