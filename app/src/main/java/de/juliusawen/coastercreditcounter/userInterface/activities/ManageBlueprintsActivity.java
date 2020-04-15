package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Blueprint;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.SortTool;
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
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailDisplayMode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ManageBlueprintsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ManageBlueprintsViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    protected void setContentView()
    {
        setContentView(R.layout.activity_manage_blueprints);
    }

    @Override
    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ManageBlueprintsViewModel.class);

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        if(getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0) == RequestCode.PICK_BLUEPRINT.ordinal())
        {
            this.viewModel.isSelectionMode = true;
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            if(this.viewModel.isSelectionMode)
            {
                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(Blueprint.class);

                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                        App.content.getContentOfType(Blueprint.class),
                        childTypesToExpand,
                        false)
                        .setTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                        .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                        .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                        .groupItems(GroupType.CATEGORY);
            }
            else
            {
                List<IElement> elementsWithOrderedChildren = App.content.getContentOfType(Blueprint.class);
                for(IElement element : elementsWithOrderedChildren)
                {
                    element.reorderChildren(SortTool.sortElements(element.getChildren(), SortType.BY_NAME, SortOrder.ASCENDING));
                }

                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(IAttraction.class);

                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                        elementsWithOrderedChildren,
                        childTypesToExpand)
                        .setTypefaceForContentType(GroupHeader.class, Typeface.BOLD)
                        .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                        .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.CREDIT_TYPE, DetailDisplayMode.BELOW)
                        .setDetailTypesAndModeForContentType(Blueprint.class, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                        .setDetailTypesAndModeForContentType(StockAttraction.class, DetailType.LOCATION, DetailDisplayMode.ABOVE)
                        .setDetailTypesAndModeForContentType(StockAttraction.class, DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                        .setDetailTypesAndModeForContentType(StockAttraction.class, DetailType.STATUS, DetailDisplayMode.BELOW)
                        .groupItems(GroupType.CATEGORY);
            }
        }

        if(this.viewModel.contentRecyclerViewAdapter != null)
        {
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
            this.recyclerView = findViewById(R.id.recyclerViewManageBlueprints);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);
        }

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_HELP_TITLE)), getIntent().getStringExtra(Constants.EXTRA_HELP_TEXT));
        super.createToolbar()
                .addToolbarHomeButton()
                .setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));
        super.createFloatingActionButton();

        this.decorateFloatingActionButton();
    }

    @Override
    protected void onDestroy()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(Constants.LOG_TAG, String.format("ManageBlueprintsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode != RESULT_OK)
        {
            return;
        }

        IElement resultElement = ResultFetcher.fetchResultElement(data);
        switch(RequestCode.values()[requestCode])
        {
            case CREATE_ATTRACTION_BLUEPRINT:
                this.viewModel.blueprintToReturn = resultElement;
                updateContentRecyclerView(true);
                break;

            case EDIT_ATTRACTION_BLUEPRINT:
                updateContentRecyclerView(true);
                break;

            case SORT_BLUEPRINTS:
                ArrayList<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                App.content.reorderElements(resultElements);
                updateContentRecyclerView(true).scrollToItem(resultElement);
                super.markForUpdate(resultElements);
                break;
        }
    }

    @Override
    protected Menu createOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .add(OptionsItem.EXPAND_ALL)
                .add(OptionsItem.COLLAPSE_ALL)
                .create(menu);
    }

    @Override
    protected  Menu prepareOptionsMenu(Menu menu)
    {
        return this.viewModel.optionsMenuAgent
                .setVisible(OptionsItem.EXPAND_ALL, App.content.getContentOfType(Blueprint.class).size() > 0 && !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .setVisible(OptionsItem.COLLAPSE_ALL, App.content.getContentOfType(Blueprint.class).size() > 0 && this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
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

            default:
                return super.handleOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            this.returnResult(RESULT_OK);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element)view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ManageBlueprintsActivity.onClick:: %s clicked", element));

                if(viewModel.isSelectionMode && element.isBlueprint())
                {
                    viewModel.blueprintToReturn = element;
                    returnResult(RESULT_OK);
                }
                else if(element.isGroupHeader() || element.hasChildren())
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                    if(viewModel.contentRecyclerViewAdapter.isAllExpanded() || viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                    {
                        invalidateOptionsMenu();
                    }
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (IElement)view.getTag();

                Log.i(Constants.LOG_TAG, String.format("ManageBlueprintsActivity.onLongClick:: %s long clicked", viewModel.longClickedElement));

                if(viewModel.longClickedElement.isBlueprint())
                {
                    PopupMenuAgent.getMenu()
                            .add(PopupItem.EDIT_ELEMENT)
                            .add(PopupItem.DELETE_ELEMENT)
                            .show(ManageBlueprintsActivity.this, view);
                }
                if(viewModel.longClickedElement.isGroupHeader() && viewModel.longClickedElement.getChildCount() > 1)
                {
                    PopupMenuAgent.getMenu()
                            .add(PopupItem.SORT)
                            .show(ManageBlueprintsActivity.this, view);
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
            case EDIT_ELEMENT:
            {
                ActivityDistributor.startActivityEditForResult(ManageBlueprintsActivity.this, RequestCode.EDIT_ATTRACTION_BLUEPRINT, viewModel.longClickedElement);
                break;
            }

            case DELETE_ELEMENT:
            {
                String alertDialogMessage = viewModel.longClickedElement.hasChildren()
                        ? getString(R.string.alert_dialog_message_confirm_delete_blueprint_has_children, viewModel.longClickedElement.getName())
                        : getString(R.string.alert_dialog_message_confirm_delete_blueprint_has_no_children, viewModel.longClickedElement.getName());

                AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(R.drawable.ic_baseline_warning, getString(R.string.alert_dialog_title_delete),
                        alertDialogMessage,
                        getString(R.string.text_accept),
                        getString(R.string.text_cancel),
                        RequestCode.DELETE, false);

                alertDialogFragmentDelete.setCancelable(false);
                alertDialogFragmentDelete.show(getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
                break;
            }

            case SORT:
                ActivityDistributor.startActivitySortForResult(
                        this,
                        RequestCode.SORT_BLUEPRINTS,
                        this.viewModel.longClickedElement.getChildren());
                break;
        }
    }

    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            if(requestCode.equals(RequestCode.DELETE))
            {
                super.setFloatingActionButtonVisibility(false);

                ConfirmSnackbar.Show(
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                                Snackbar.LENGTH_LONG),
                        requestCode,
                        this);
             }

            this.updateContentRecyclerView(false);
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageBlueprintActivity.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        super.setFloatingActionButtonVisibility(true);

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(Constants.LOG_TAG, String.format("ManageBlueprintActivity.handleActionConfirmed:: deleting %s...", this.viewModel.longClickedElement));

            for(IElement child : this.viewModel.longClickedElement.getChildren())
            {
                for(Visit visit : child.getParent().getChildrenAsType(Visit.class))
                {
                    for(VisitedAttraction visitedAttraction : visit.getChildrenAsType(VisitedAttraction.class))
                    {
                        if(visitedAttraction.getOnSiteAttraction().equals(child))
                        {
                            visitedAttraction.deleteElementAndDescendants();
                        }
                    }
                }
            }

            super.markForDeletion(this.viewModel.longClickedElement, true);
            this.viewModel.longClickedElement.deleteElementAndDescendants();
            this.updateContentRecyclerView(true);
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ManageBlueprintsActivity.onClickFloatingActionButton:: FloatingActionButton pressed");
                ActivityDistributor.startActivityCreateForResult(ManageBlueprintsActivity.this, RequestCode.CREATE_ATTRACTION_BLUEPRINT);
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private ContentRecyclerViewAdapter updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(Constants.LOG_TAG, "ManageBlueprintsActivity.updateContentRecyclerView:: resetting content...");

            this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(Blueprint.class));
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ManageBlueprintsActivity.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }

        return this.viewModel.contentRecyclerViewAdapter;
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageBlueprintsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            if(this.viewModel.blueprintToReturn != null)
            {
                Log.i(Constants.LOG_TAG, String.format("ManageBlueprintsActivity.returnResult:: returning last created %s", this.viewModel.blueprintToReturn));
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.blueprintToReturn.getUuid().toString());
            }
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH + this.getClass().getSimpleName());
        finish();
    }
}
