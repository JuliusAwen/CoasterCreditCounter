package de.juliusawen.coastercreditcounter.frontend.visits;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.GroupHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.Element;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Ride;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

import static de.juliusawen.coastercreditcounter.globals.Constants.LOG_TAG;

public class ShowVisitActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private ShowVisitActivityViewModel viewModel;
    private RecyclerView recyclerView;
    private boolean actionConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowVisitActivity.onCreate:: creating activity...");

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

        invalidateOptionsMenu();

        if(App.isInitialized)
        {
            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerView();
                this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(GroupHeader.class, Typeface.BOLD);
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
            this.viewModel.contentRecyclerViewAdapter.addRideOnClickListener(this.getAddRideOnClickListener());
            this.viewModel.contentRecyclerViewAdapter.deleteRideOnClickListener(this.getRemoveRideOnClickListener());

            this.recyclerView = findViewById(R.id.recyclerViewShowVisit);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            if(this.allAttractionsAdded())
            {
                super.disableFloatingActionButton();
            }
            else
            {
                super.addFloatingActionButton();
                this.decorateFloatingActionButton();
                this.viewModel.contentRecyclerViewAdapter.addBottomSpacer();
            }

            if(this.viewModel.visit.isEditingEnabled())
            {
                super.setFloatingActionButtonVisibility(true);
            }
            else
            {
                super.setFloatingActionButtonVisibility(false);
                this.viewModel.contentRecyclerViewAdapter.formatAsPrettyPrint(true);
            }

            Log.d(LOG_TAG, String.format("ShowVisitActivity.onResume:: %s isEditingEnabled[%S]", this.viewModel.visit, this.viewModel.visit.isEditingEnabled()));
        }
    }

    @Override
    protected void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(this.viewModel.visit.isEditingEnabled())
        {
            menu.add(Menu.NONE, Constants.SELECTION_DISABLE_EDITING, Menu.NONE, "disable editing")
                    .setIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_block, R.color.white))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        else
        {
            menu.add(Menu.NONE, Constants.SELECTION_ENABLE_EDITING, Menu.NONE, "enable for editing")
                    .setIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_create, R.color.white))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        menu.add(Menu.NONE, Constants.SELECTION_EXPAND_ALL, Menu.NONE, R.string.selection_expand_all).setEnabled(!this.viewModel.contentRecyclerViewAdapter.isAllExpanded());
        menu.add(Menu.NONE, Constants.SELECTION_COLLAPSE_ALL, Menu.NONE, R.string.selection_collapse_all).setEnabled(!this.viewModel.contentRecyclerViewAdapter.isAllCollapsed());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == Constants.SELECTION_EXPAND_ALL)
        {
            this.viewModel.contentRecyclerViewAdapter.expandAll();
        }
        else if(item.getItemId() == Constants.SELECTION_COLLAPSE_ALL)
        {
            this.viewModel.contentRecyclerViewAdapter.collapseAll();
        }
        else if(item.getItemId() == Constants.SELECTION_ENABLE_EDITING)
        {
            super.setFloatingActionButtonVisibility(true);
            this.viewModel.visit.setEditingEnabled(true);
            this.viewModel.contentRecyclerViewAdapter.formatAsPrettyPrint(false);
            invalidateOptionsMenu();

            Log.d(LOG_TAG, String.format("ShowVisitActivity.onOptionsItemSelected<ENABLE_EDITING>:: enabled editing for %s", this.viewModel.visit));

            return true;
        }
        else if(item.getItemId() == Constants.SELECTION_DISABLE_EDITING)
        {
            super.setFloatingActionButtonVisibility(false);
            this.viewModel.visit.setEditingEnabled(false);
            this.viewModel.contentRecyclerViewAdapter.formatAsPrettyPrint(true);
            invalidateOptionsMenu();

            Log.d(LOG_TAG, String.format("ShowVisitActivity.onOptionsItemSelected<DISABLE_EDITING>:: disabled editing %s", this.viewModel.visit));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(LOG_TAG, "ShowVisitActivity.onClickFloatingActionButton:: FloatingActionButton pressed");

                ActivityTool.startActivityPickForResult(
                        ShowVisitActivity.this,
                        Constants.REQUEST_CODE_PICK_ATTRACTIONS,
                        new ArrayList<IElement>(getNotYetAddedAttractions()));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(LOG_TAG, String.format("ShowVisitActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

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
                    Log.d(LOG_TAG,
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
                GroupHeaderProvider.GroupType.ATTRACTION_CATEGORY);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element instanceof GroupHeader)
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
                viewModel.longClickedElement = (Element) view.getTag();

                if(viewModel.longClickedElement instanceof GroupHeader)
                {
                    GroupHeader.handleOnGroupHeaderLongClick(ShowVisitActivity.this, view);
                }
                else if(viewModel.longClickedElement instanceof Attraction)
                {
                    PopupMenu popupMenu = getRecyclerViewItemPopupMenu(view);
                    popupMenu.setOnMenuItemClickListener(getOnMenuItemClickListener());
                    popupMenu.show();
                    return false;
                }

                return true;
            }
        };
    }

    private PopupMenu getRecyclerViewItemPopupMenu(View view)
    {
        PopupMenu popupMenu = new PopupMenu(this, view);
        this.populatePopupMenu(popupMenu.getMenu());
        return popupMenu;
    }

    private void populatePopupMenu(Menu menu)
    {
        menu.add(Menu.NONE, Constants.SELECTION_DELETE_ELEMENT, Menu.NONE, R.string.selection_delete);
    }

    private PopupMenu.OnMenuItemClickListener getOnMenuItemClickListener()
    {
        return new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Log.i(LOG_TAG, String.format("ShowVisitActivity.onPopupMenuItemLongClick:: [%S] selected", item.getItemId()));

                int id = item.getItemId();
                if(id == Constants.SELECTION_DELETE_ELEMENT)
                {
                    //let user verify delete when any rides are counted
                    if(viewModel.longClickedElement.getChildCount() > 0)
                    {
                        AlertDialogFragment alertDialogFragmentDelete =
                                AlertDialogFragment.newInstance(
                                        R.drawable.ic_baseline_warning,
                                        getString(R.string.alert_dialog_title_delete_element),
                                        getString(R.string.alert_dialog_message_delete_visited_attraction, viewModel.longClickedElement.getName()),
                                        getString(R.string.text_accept),
                                        getString(R.string.text_cancel),
                                        Constants.REQUEST_CODE_DELETE,
                                        false);

                        alertDialogFragmentDelete.setCancelable(false);
                        alertDialogFragmentDelete.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);

                        return true;
                    }
                    else
                    {
                        deleteVisitedAttraction();
                    }
                }

                return false;
            }
        };
    }

    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        Snackbar snackbar;

        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            if(requestCode == Constants.REQUEST_CODE_DELETE)
            {
                snackbar = Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                        Snackbar.LENGTH_LONG);

                snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        actionConfirmed = true;
                        Log.i(LOG_TAG, "ShowVisitActivity.onSnackbarClick<DELETE>:: action <DELETE> confirmed");
                    }
                });

                snackbar.addCallback(new Snackbar.Callback()
                {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event)
                    {
                        if(actionConfirmed)
                        {
                            actionConfirmed = false;
                            deleteVisitedAttraction();
                        }
                        else
                        {
                            Log.d(LOG_TAG, "ShowVisitActivity.onDismissed<DELETE>:: action <DELETE> not confirmed - doing nothing");
                        }
                    }
                });

                snackbar.show();
            }
        }
    }

    private void deleteVisitedAttraction()
    {
        Log.i(LOG_TAG, String.format("ShowVisitActivity.onDismissed<DELETE>:: deleting %s...", viewModel.longClickedElement));

        ShowVisitActivity.super.markForDeletion(this.viewModel.longClickedElement, true);
        ShowVisitActivity.super.markForUpdate(this.viewModel.longClickedElement.getParent());
        this.viewModel.longClickedElement.deleteElementAndDescendants();
        updateContentRecyclerView(true);
    }

    private View.OnClickListener getAddRideOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.v(LOG_TAG, String.format("ShowVisitActivity.getAddRideOnClickListener.onClick:: adding ride to %s for %s", visitedAttraction, visitedAttraction.getParent()));

                Ride ride = visitedAttraction.addRide();

                ShowVisitActivity.super.markForCreation(ride);
                ShowVisitActivity.super.markForUpdate(ShowVisitActivity.this.viewModel.visit);
                updateContentRecyclerView(false);
            }
        };
    }

    private View.OnClickListener getRemoveRideOnClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisitedAttraction visitedAttraction = (VisitedAttraction) view.getTag();

                Log.v(LOG_TAG, String.format("ShowVisitActivity.getRemoveRideOnClickListener.onClick:: deleting latest ride on %s for %s",
                        visitedAttraction.getOnSiteAttraction(), visitedAttraction.getParent()));

                Ride ride = visitedAttraction.deleteLatestRide();
                if(ride != null)
                {
                    ShowVisitActivity.super.markForDeletion(ride, false);
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
            List<IAttraction> notYetAddedAttractions = this.getNotYetAddedAttractions();
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

    private List<IAttraction> getNotYetAddedAttractions()
    {
        List<IAttraction> visitedAttractions = new ArrayList<>();
        for(VisitedAttraction visitedAttraction : viewModel.visit.getChildrenAsType(VisitedAttraction.class))
        {
            visitedAttractions.add(visitedAttraction.getOnSiteAttraction());
        }

        List<IAttraction> allAttractions = new ArrayList<IAttraction>(this.viewModel.visit.getParent().getChildrenAsType(IOnSiteAttraction.class));

        allAttractions.removeAll(visitedAttractions);

        return allAttractions;
    }

    private void updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(LOG_TAG, "ShowVisitActivity.updateContentRecyclerView:: resetting content...");
            this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.visit.getChildrenOfType(VisitedAttraction.class));
        }
        else
        {
            Log.d(LOG_TAG, "ShowVisitActivity.updateContentRecyclerView:: notifying data set changed...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
