package de.juliusawen.coastercreditcounter.frontend.elements;

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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.AttractionBlueprint;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.CoasterBlueprint;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.ICategorized;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ManageOrphanElementsActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private ManageOrphanElementsViewModel viewModel;
    private RecyclerView recyclerView;
    private boolean actionConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ManageOrphanElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_attraction_categories);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(ManageOrphanElementsViewModel.class);

            if(this.viewModel.attractionCategoryHeaderProvider == null)
            {
                this.viewModel.attractionCategoryHeaderProvider = new AttractionCategoryHeaderProvider();
            }

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(ICategorized.class);

                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                        App.content.getContentOfType(AttractionCategory.class),
                        null,
                        childTypesToExpand);

                this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategory.class, Typeface.BOLD);

                Set<Class<? extends IAttraction>> typesToDisplayManufacturer = new HashSet<>();
                typesToDisplayManufacturer.add(CustomCoaster.class);
                typesToDisplayManufacturer.add(CustomAttraction.class);
                typesToDisplayManufacturer.add(CoasterBlueprint.class);
                typesToDisplayManufacturer.add(AttractionBlueprint.class);
                this.viewModel.contentRecyclerViewAdapter.setTypesToDisplayManufacturer(typesToDisplayManufacturer);

                Set<Class<? extends IAttraction>> typesToDisplayLocation = new HashSet<>();
                typesToDisplayLocation.add(CustomCoaster.class);
                typesToDisplayLocation.add(CustomAttraction.class);
                typesToDisplayLocation.add(StockAttraction.class);
                typesToDisplayLocation.add(CoasterBlueprint.class);
                typesToDisplayLocation.add(AttractionBlueprint.class);
                this.viewModel.contentRecyclerViewAdapter.setTypesToDisplayLocation(typesToDisplayLocation);
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
            this.recyclerView = findViewById(R.id.recyclerViewShowAttractionCategories);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(getString(R.string.title_attraction_categories), null);

            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_attraction_categories)), getString(R.string.help_text_manage_attraction_category));

            super.addFloatingActionButton();
            this.decorateFloatingActionButton();
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

        if(App.content.getContentOfType(AttractionCategory.class).size() > 1)
        {
            menu.add(Menu.NONE, Constants.SELECTION_SORT_ATTRACTION_CATEGORIES, Menu.NONE, R.string.selection_sort_attraction_categories);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onOptionItemSelected:: [%S] selected", item.getItemId()));

        switch(item.getItemId())
        {
            case Constants.SELECTION_SORT_ATTRACTION_CATEGORIES:
            {
                ActivityTool.startActivitySortForResult(
                        this,
                        Constants.REQUEST_CODE_SORT_ATTRACTION_CATEGORIES,
                        App.content.getContentOfType(AttractionCategory.class));
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement resultElement = ResultTool.fetchResultElement(data);

            switch(requestCode)
            {
                case Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY:
                {
                    String createdString = data.getStringExtra(Constants.EXTRA_RESULT_STRING);
                    Log.d(Constants.LOG_TAG,
                            String.format("ManageOrphanElementsActivity.onActivityResult<CreateAttractionCategory>:: creating AttractionCategory [%s]", createdString));

                    AttractionCategory attractionCategory = AttractionCategory.create(createdString, null);
                    if(attractionCategory != null)
                    {
                        App.content.addElement(attractionCategory);
                        this.markForCreation(attractionCategory);
                        updateContentRecyclerView(true);
                    }
                    else
                    {
                        Toaster.makeToast(this, getString(R.string.error_creation_failed));

                        Log.e(Constants.LOG_TAG,
                                String.format("ManageOrphanElementsActivity.onActivityResult<CreateAttractionCategory>:: not able to create AttractionCategory [%s]", createdString));
                    }
                    break;
                }

                case Constants.REQUEST_CODE_EDIT_ATTRACTION_CATEGORY:
                {
                    this.markForUpdate(resultElement);
                    updateContentRecyclerView(false);
                    break;
                }

                case Constants.REQUEST_CODE_SORT_ATTRACTION_CATEGORIES:
                {
                    List<IElement> resultElements = ResultTool.fetchResultElements(data);

                    App.content.reorderElements(resultElements);
                    updateContentRecyclerView(true);

                    if(resultElement != null)
                    {
                        Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult<SortAttractionCategory>:: scrolling to selected element %s...",
                                resultElement));
                        this.viewModel.contentRecyclerViewAdapter.scrollToItem(resultElement);
                    }

                    this.markForUpdate(resultElements);
                    break;
                }

                case Constants.REQUEST_CODE_APPLY_CATEGORY_TO_ATTRACTIONS:
                {
                    List<IElement> resultElements = ResultTool.fetchResultElements(data);

                    for(IElement element : resultElements)
                    {
                        ((Attraction)element).setAttractionCategory(this.viewModel.longClickedAttractionCategory);
                        super.markForUpdate(element);
                    }

                    Toaster.makeToast(this, getString(R.string.information_applied_category_to_attractions, this.viewModel.longClickedAttractionCategory.getName(), resultElements.size()));

                    Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onActivityResult<ApplyCategoryToAttractions>:: applied %s to [%d] attractions",
                            this.viewModel.longClickedAttractionCategory, resultElements.size()));

                    updateContentRecyclerView(true);
                    break;
                }
            }
        }
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element)view.getTag();

                if(element instanceof AttractionCategory)
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                Element element = (Element)view.getTag();

                if(element instanceof AttractionCategory)
                {
                    viewModel.longClickedAttractionCategory = (AttractionCategory) element;
                    Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onLongClick:: %s long clicked", viewModel.longClickedAttractionCategory));

                    PopupMenu popupMenu = new PopupMenu(ManageOrphanElementsActivity.this, view);

                    popupMenu.getMenu().add(0, Constants.SELECTION_APPLY_TO_ATTRACTIONS, Menu.NONE, R.string.selection_apply_to_attractions)
                            .setEnabled(!App.content.getContentAsType(ICategorized.class).isEmpty());

                    popupMenu.getMenu().add(0, Constants.SELECTION_EDIT_ATTRACTION_CATEGORY, Menu.NONE, R.string.selection_edit);

                    popupMenu.getMenu().add(0, Constants.SELECTION_DELETE_ELEMENT, Menu.NONE, R.string.selection_delete)
                            .setEnabled(!viewModel.longClickedAttractionCategory.equals(AttractionCategory.getDefault()));

                    popupMenu.getMenu().add(0, Constants.SELECTION_SET_AS_DEFAULT, Menu.NONE, R.string.selection_set_as_default)
                            .setEnabled(!viewModel.longClickedAttractionCategory.equals(AttractionCategory.getDefault()));

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick:: [%S] selected", item.getItemId()));

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            switch(item.getItemId())
                            {
                                case Constants.SELECTION_APPLY_TO_ATTRACTIONS:
                                {
                                    List<IElement> attractions = new ArrayList<IElement>(App.content.getContentAsType(ICategorized.class));

                                    for(IAttraction attraction : ConvertTool.convertElementsToType(attractions, IAttraction.class))
                                    {
                                        if(attraction.getAttractionCategory().equals(viewModel.longClickedAttractionCategory))
                                        {
                                            Log.v(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: " +
                                                    "removing %s from pick list - is already in %s", attraction, viewModel.longClickedAttractionCategory));
                                            attractions.remove(attraction);
                                        }
                                    }

                                    ActivityTool.startActivityPickForResult(
                                            ManageOrphanElementsActivity.this,
                                            Constants.REQUEST_CODE_APPLY_CATEGORY_TO_ATTRACTIONS,
                                            attractions);

                                    return true;
                                }

                                case Constants.SELECTION_EDIT_ATTRACTION_CATEGORY:
                                {
                                    ActivityTool.startActivityEditForResult(ManageOrphanElementsActivity.this,
                                            Constants.REQUEST_CODE_EDIT_ATTRACTION_CATEGORY, viewModel.longClickedAttractionCategory);
                                    return true;
                                }

                                case Constants.SELECTION_DELETE_ELEMENT:
                                {
                                    String alertDialogMessage;
                                    if(viewModel.longClickedAttractionCategory.hasChildren())
                                    {
                                        alertDialogMessage = getString(R.string.alert_dialog_message_delete_attraction_category_with_children,
                                                viewModel.longClickedAttractionCategory.getChildCount(),
                                                viewModel.longClickedAttractionCategory.getName(),
                                                AttractionCategory.getDefault().getName());
                                    }
                                    else
                                    {
                                        alertDialogMessage = getString(R.string.alert_dialog_message_delete_attraction_category_without_children,
                                                viewModel.longClickedAttractionCategory.getName());
                                    }

                                    AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                                            R.drawable.ic_baseline_warning,
                                            getString(R.string.alert_dialog_title_delete_element),
                                            alertDialogMessage,
                                            getString(R.string.text_accept),
                                            getString(R.string.text_cancel),
                                            Constants.REQUEST_CODE_DELETE);

                                    alertDialogFragmentDelete.setCancelable(false);
                                    alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                                    return true;
                                }

                                case Constants.SELECTION_SET_AS_DEFAULT:
                                {
                                    String alterDialogMessage = getString(R.string.alert_dialog_message_set_as_default, viewModel.longClickedAttractionCategory.getName());

                                    AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                                            R.drawable.ic_baseline_warning,
                                            getString(R.string.alert_dialog_title_set_as_default),
                                            alterDialogMessage,
                                            getString(R.string.text_accept),
                                            getString(R.string.text_cancel),
                                            Constants.REQUEST_CODE_SET_AS_DEFAULT);

                                    alertDialogFragmentDelete.setCancelable(false);
                                    alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);

                                    return true;
                                }

                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
                return true;
            }
        };
    }

    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            switch(requestCode)
            {
                case Constants.REQUEST_CODE_DELETE:
                {
                    Snackbar snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.action_confirm_delete_text, viewModel.longClickedAttractionCategory.getName()),
                            Snackbar.LENGTH_LONG);

                    snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            actionConfirmed = true;
                            Log.i(Constants.LOG_TAG, "ManageOrphanElementsActivity.onSnackbarClick:: action <DELETE> confirmed");
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

                                Log.i(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onSnackbarDismissed<DELETE>:: deleting %s...",
                                        viewModel.longClickedAttractionCategory));

                                List<IAttraction> children = new ArrayList<>(viewModel.longClickedAttractionCategory.getChildrenAsType(IAttraction.class));

                                for(IAttraction child : children)
                                {
                                    child.setAttractionCategory(AttractionCategory.getDefault());
                                    markForUpdate(child);
                                }

                                markForDeletion(viewModel.longClickedAttractionCategory, false);

                                App.content.removeElement(viewModel.longClickedAttractionCategory);

                                updateContentRecyclerView(true);
                            }
                            else
                            {
                                Log.d(Constants.LOG_TAG, "ManageOrphanElementsActivity.onSnackbarDismissed<DELETE>:: action not confirmed - doing nothing");
                            }
                        }
                    });

                    snackbar.show();
                    break;
                }

                case Constants.REQUEST_CODE_SET_AS_DEFAULT:
                {
                    Log.d(Constants.LOG_TAG, String.format("ManageOrphanElementsActivity.onAlertDialogClick<SET_AS_DEFAULT>:: setting %s as default AttractionCategory",
                            this.viewModel.longClickedAttractionCategory));

                    super.markForUpdate(AttractionCategory.getDefault());

                    AttractionCategory.setDefault(this.viewModel.longClickedAttractionCategory);
                    super.markForUpdate(this.viewModel.longClickedAttractionCategory);

                    Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedAttractionCategory.getName()));

                    break;
                }
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
                Log.i(Constants.LOG_TAG, "ManageOrphanElementsViewModel.onClickFloatingActionButton:: FloatingActionButton pressed");
                ActivityTool.startActivityCreateForResult(ManageOrphanElementsActivity.this, Constants.REQUEST_CODE_CREATE_ATTRACTION_CATEGORY, null);
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(Constants.LOG_TAG, "ManageOrphanElementsViewModel.updateContentRecyclerView:: resetting content...");
            this.viewModel.contentRecyclerViewAdapter.setItems(App.content.getContentOfType(AttractionCategory.class));
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ManageOrphanElementsViewModel.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
