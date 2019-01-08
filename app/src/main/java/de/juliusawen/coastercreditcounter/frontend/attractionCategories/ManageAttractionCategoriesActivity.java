package de.juliusawen.coastercreditcounter.frontend.attractionCategories;

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

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.ICategorized;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ManageAttractionCategoriesActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private ManageAttractionCategoriesViewModel viewModel;
    private boolean actionConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ManageAttractionCategoriesActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_attraction_categories);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(ManageAttractionCategoriesViewModel.class);

            if(this.viewModel.attractionCategoryHeaderProvider == null)
            {
                this.viewModel.attractionCategoryHeaderProvider = new AttractionCategoryHeaderProvider();
            }

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                childTypesToExpand.add(ICategorized.class);

                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                        ConvertTool.convertElementsToType(App.content.getAttractionCategories(), IElement.class),
                        null,
                        childTypesToExpand);

                this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategory.class, Typeface.BOLD);
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
            RecyclerView recyclerView = findViewById(R.id.recyclerViewShowAttractionCategories);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(getString(R.string.title_attraction_categories), null);

            super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_attraction_categories)), getString(R.string.help_text_show_attraction_category));

            super.addFloatingActionButton();
            this.decorateFloatingActionButton();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        if(App.content.getAttractionCategories().size() > 1)
        {
            menu.add(Menu.NONE, Selection.SORT_ATTRACTION_CATEGORIES.ordinal(), Menu.NONE, R.string.selection_sort_attraction_categories);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Selection selection = Selection.values()[item.getItemId()];
        Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onOptionItemSelected:: [%S] selected", selection));

        switch(selection)
        {
            case SORT_ATTRACTION_CATEGORIES:
            {
                ActivityTool.startActivitySortForResult(
                        this,
                        Constants.REQUEST_SORT_ATTRACTION_CATEGORIES,
                        new ArrayList<IElement>(App.content.getAttractionCategories()));
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement resultElement = ResultTool.fetchResultElement(data);

            if(requestCode == Constants.REQUEST_CREATE_ATTRACTION_CATEGORY)
            {
                String createdString = data.getStringExtra(Constants.EXTRA_RESULT_STRING);
                Log.d(Constants.LOG_TAG,
                        String.format("ManageAttractionCategoriesActivity.onActivityResult<CreateAttractionCategory>:: creating AttractionCategory [%s]", createdString));

                AttractionCategory attractionCategory = AttractionCategory.create(createdString, null);
                if(attractionCategory != null)
                {
                    App.content.addAttractionCategory(attractionCategory);
                    this.markForCreation(attractionCategory);
                    updateContentRecyclerView(true);
                }
                else
                {
                    Toaster.makeToast(this, getString(R.string.error_creation_failed));

                    Log.e(Constants.LOG_TAG,
                            String.format("ManageAttractionCategoriesActivity.onActivityResult<CreateAttractionCategory>:: not able to create AttractionCategory [%s]", createdString));
                }
            }
            else if(requestCode == Constants.REQUEST_EDIT_ATTRACTION_CATEGORY)
            {
                this.markForUpdate(resultElement);
                updateContentRecyclerView(false);
            }
            else if(requestCode == Constants.REQUEST_SORT_ATTRACTION_CATEGORIES)
            {
                List<IElement> resultElements = ResultTool.fetchResultElements(data);

                App.content.setAttractionCategories(ConvertTool.convertElementsToType(resultElements, AttractionCategory.class));
                this.viewModel.contentRecyclerViewAdapter.setItems(resultElements);

                if(resultElement != null)
                {
                    Log.d(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onActivityResult<SortAttractionCategory>:: scrolling to selected element %s...",
                            resultElement));
                    this.viewModel.contentRecyclerViewAdapter.scrollToItem(resultElement);
                }

                this.markForUpdate(resultElements);
            }
            else if(requestCode == Constants.REQUEST_APPLY_CATEGORY_TO_ATTRACTIONS)
            {
                List<IElement> resultElements = ResultTool.fetchResultElements(data);

                for(IElement element : resultElements)
                {
                    ((Attraction)element).setAttractionCategory(this.viewModel.longClickedAttractionCategory);
                    super.markForUpdate(element);
                }

                Toaster.makeToast(this, getString(R.string.information_count_of_categorized_attractions, this.viewModel.longClickedAttractionCategory.getName(), resultElements.size()));

                Log.d(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onActivityResult<ApplyCategoryToAttractions>:: applied %s to [%d] attractions",
                        this.viewModel.longClickedAttractionCategory, resultElements.size()));

                updateContentRecyclerView(true);
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

                if(AttractionCategory.class.isInstance(element))
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                Element element = (Element)view.getTag();

                if(AttractionCategory.class.isInstance(element))
                {
                    viewModel.longClickedAttractionCategory = (AttractionCategory) element;
                    Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onLongClick:: %s long clicked", viewModel.longClickedAttractionCategory));

                    PopupMenu popupMenu = new PopupMenu(ManageAttractionCategoriesActivity.this, view);

                    popupMenu.getMenu().add(0, Selection.EDIT_ATTRACTION_CATEGORY.ordinal(), Menu.NONE, R.string.selection_edit);

                    popupMenu.getMenu().add(0, Selection.DELETE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_delete)
                            .setEnabled(!viewModel.longClickedAttractionCategory.equals(AttractionCategory.getDefault()));

                    popupMenu.getMenu().add(0, Selection.APPLY_TO_ATTRACTIONS.ordinal(), Menu.NONE, R.string.selection_apply_to_attractions)
                            .setEnabled(!App.content.getContentAsType(ICategorized.class).isEmpty());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            Selection selection = Selection.values()[item.getItemId()];
                            Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onMenuItemClick:: [%S] selected", selection));

                            FragmentManager fragmentManager = getSupportFragmentManager();
                            switch(selection)
                            {
                                case EDIT_ATTRACTION_CATEGORY:
                                {
                                    ActivityTool.startActivityEditForResult(ManageAttractionCategoriesActivity.this,
                                            Constants.REQUEST_EDIT_ATTRACTION_CATEGORY, viewModel.longClickedAttractionCategory);
                                    return true;
                                }

                                case DELETE_ELEMENT:
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
                                            Constants.ALERT_DIALOG_REQUEST_CODE_DELETE);

                                    alertDialogFragmentDelete.setCancelable(false);
                                    alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                                    return true;
                                }

                                case APPLY_TO_ATTRACTIONS:
                                {
                                    List<IElement> attractions = new ArrayList<IElement>(App.content.getContentAsType(ICategorized.class));

                                    for(IAttraction attraction : ConvertTool.convertElementsToType(attractions, IAttraction.class))
                                    {
                                        if(attraction.getAttractionCategory().equals(viewModel.longClickedAttractionCategory))
                                        {
                                            Log.v(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onMenuItemClick<APPLY_TO_ATTRACTIONS>:: " +
                                                    "removing %s from pick list - is already in %s", attraction, viewModel.longClickedAttractionCategory));
                                            attractions.remove(attraction);
                                        }
                                    }

                                    ActivityTool.startActivityPickForResult(
                                            ManageAttractionCategoriesActivity.this,
                                            Constants.REQUEST_APPLY_CATEGORY_TO_ATTRACTIONS,
                                            attractions);

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

        Snackbar snackbar;
        switch(requestCode)
        {
            case Constants.ALERT_DIALOG_REQUEST_CODE_DELETE:
            {
                if(which == DialogInterface.BUTTON_POSITIVE)
                {
                    snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.action_confirm_delete_text, viewModel.longClickedAttractionCategory.getName()),
                            Snackbar.LENGTH_LONG);

                    snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            actionConfirmed = true;
                            Log.i(Constants.LOG_TAG, "ManageAttractionCategoriesActivity.onSnackbarClick:: action <delete> confirmed");
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

                                Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onSnackbarDismissed<delete>:: deleting %s...",
                                        viewModel.longClickedAttractionCategory));

                                List<IAttraction> children = new ArrayList<>(viewModel.longClickedAttractionCategory.getChildrenAsType(IAttraction.class));

                                for(IAttraction child : children)
                                {
                                    child.setAttractionCategory(AttractionCategory.getDefault());
                                    markForUpdate(child);
                                }

                                markForDeletion(viewModel.longClickedAttractionCategory, false);

                                App.content.removeAttractionCategory(viewModel.longClickedAttractionCategory);

                                updateContentRecyclerView(true);
                            }
                            else
                            {
                                Log.d(Constants.LOG_TAG, "ManageAttractionCategoriesActivity.onSnackbarDismissed<delete>:: action not confirmed - doing nothing");
                            }
                        }
                    });

                    snackbar.show();
                }
                break;
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
                Log.i(Constants.LOG_TAG, "ManageAttractionCategoriesViewModel.onClickFloatingActionButton:: FloatingActionButton pressed");
                ActivityTool.startActivityCreateForResult(ManageAttractionCategoriesActivity.this, Constants.REQUEST_CREATE_ATTRACTION_CATEGORY, null);
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d(Constants.LOG_TAG, "ManageAttractionCategoriesViewModel.updateContentRecyclerView:: resetting content...");
            this.viewModel.contentRecyclerViewAdapter.setItems(ConvertTool.convertElementsToType(App.content.getAttractionCategories(), IElement.class));
        }
        else
        {
            Log.d(Constants.LOG_TAG, "ManageAttractionCategoriesViewModel.updateContentRecyclerView:: notifying data set changes...");
            this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
