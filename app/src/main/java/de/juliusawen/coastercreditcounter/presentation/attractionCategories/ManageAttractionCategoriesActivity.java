package de.juliusawen.coastercreditcounter.presentation.attractionCategories;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.data.attractions.Attraction;
import de.juliusawen.coastercreditcounter.data.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.data.attractions.ICategorized;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.presentation.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.presentation.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class ManageAttractionCategoriesActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener
{
    private ManageAttractionCategoriesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ManageAttractionCategoriesActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_show_attraction_categories);
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ManageAttractionCategoriesViewModel.class);

        if(this.viewModel.attractionCategoryHeaderProvider == null)
        {
            this.viewModel.attractionCategoryHeaderProvider = new AttractionCategoryHeaderProvider();
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                    new ArrayList<IElement>(App.content.getAttractionCategories()),
                    null,
                    ICategorized.class);
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());
        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowAttractionCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        super.addToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getString(R.string.title_attraction_categories), null);

        super.addHelpOverlayFragment(getString(R.string.title_help, getString(R.string.title_attraction_categories)), getString(R.string.help_text_not_available));

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();
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
            IElement selectedElement = ResultTool.fetchSelectedElement(data);

            if(requestCode == Constants.REQUEST_CREATE_ATTRACTION_CATEGORY)
            {
                String createdString = data.getStringExtra(Constants.EXTRA_RESULT_STRING);
                Log.d(Constants.LOG_TAG,
                        String.format("ManageAttractionCategoriesActivity.onActivityResult<CreateAttractionCategory>:: creating AttractionCategory [%s]", createdString));

                AttractionCategory attractionCategory = AttractionCategory.create(createdString, null);
                if(attractionCategory != null)
                {
                    App.content.addAttractionCategory(attractionCategory);

                    this.updateContentRecyclerView();
                    this.viewModel.contentRecyclerViewAdapter.scrollToElement(attractionCategory);
                }
                else
                {
                    Toaster.makeToast(this, getString(R.string.error_text_creation_failed));

                    Log.e(Constants.LOG_TAG,
                            String.format("ManageAttractionCategoriesActivity.onActivityResult<CreateAttractionCategory>:: not able to create AttractionCategory [%s]", createdString));
                }
            }
            else if(requestCode == Constants.REQUEST_EDIT_ATTRACTION_CATEGORY)
            {
                this.updateContentRecyclerView();
                this.viewModel.contentRecyclerViewAdapter.scrollToElement(selectedElement);
            }
            else if(requestCode == Constants.REQUEST_SORT_ATTRACTION_CATEGORIES)
            {
                List<IElement> resultElements = ResultTool.fetchResultElements(data);

                App.content.setAttractionCategories(Element.convertElementsToType(resultElements, AttractionCategory.class));
                this.updateContentRecyclerView();

                if(selectedElement != null)
                {
                    Log.d(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onActivityResult<SortAttractionCategory>:: scrolling to selected element %s...",
                            selectedElement));
                    this.viewModel.contentRecyclerViewAdapter.scrollToElement(selectedElement);
                }
            }
            else if(requestCode == Constants.APPLY_CATEGORY_TO_ATTRACTIONS)
            {
                List<IElement> resultElements = ResultTool.fetchResultElements(data);

                for(IElement element : resultElements)
                {
                    ((Attraction)element).setAttractionCategory(this.viewModel.longClickedAttractionCategory);
                }

                Toaster.makeToast(this, getString(R.string.information_count_of_categorized_attractions, this.viewModel.longClickedAttractionCategory.getName(), resultElements.size()));
                this.updateContentRecyclerView();

                Log.d(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onActivityResult<ApplyCategoryToAttractions>:: applied %s to [%d] attractions",
                        this.viewModel.longClickedAttractionCategory, resultElements.size()));
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
                viewModel.longClickedAttractionCategory = (AttractionCategory) view.getTag();
                Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onLongClickLocationRecyclerView:: %s long clicked", viewModel.longClickedAttractionCategory));

                PopupMenu popupMenu = new PopupMenu(ManageAttractionCategoriesActivity.this, view);

                popupMenu.getMenu().add(0, Selection.EDIT_ATTRACTION_CATEGORY.ordinal(), Menu.NONE, R.string.selection_edit);

                popupMenu.getMenu().add(0, Selection.DELETE_ELEMENT.ordinal(), Menu.NONE, R.string.selection_delete)
                        .setEnabled(!viewModel.longClickedAttractionCategory.equals(App.settings.getDefaultAttractionCategory()));

                popupMenu.getMenu().add(0, Selection.APPLY_CATEGORY_TO_ATTRACTIONS.ordinal(), Menu.NONE, R.string.selection_apply_category_to_attractions)
                        .setEnabled(!App.content.getContentAsType(ICategorized.class).isEmpty());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Selection selection = Selection.values()[item.getItemId()];
                        Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onClickMenuItemPopupMenuLongClickContentRecyclerView:: [%S] selected", selection));

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
                                if(viewModel.longClickedAttractionCategory.getChildCount() != 0)
                                {
                                    alertDialogMessage = getString(R.string.alert_dialog_delete_attraction_category_with_children_message,
                                            viewModel.longClickedAttractionCategory.getChildCount(),
                                            viewModel.longClickedAttractionCategory.getName());
                                }
                                else
                                {
                                    alertDialogMessage = getString(R.string.alert_dialog_delete_attraction_category_without_children_message,
                                            viewModel.longClickedAttractionCategory.getName());
                                }

                                AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                                        R.drawable.ic_baseline_warning,
                                        getString(R.string.alert_dialog_delete_element_title),
                                        alertDialogMessage,
                                        getString(R.string.text_accept),
                                        getString(R.string.text_cancel),
                                        Constants.ALERT_DIALOG_REQUEST_CODE_DELETE);

                                alertDialogFragmentDelete.setCancelable(false);
                                alertDialogFragmentDelete.show(fragmentManager, Constants.FRAGMENT_TAG_ALERT_DIALOG);
                                return true;
                            }

                            case APPLY_CATEGORY_TO_ATTRACTIONS:
                            {
                                List<IElement> attractionCategoryHeaders =
                                        viewModel.attractionCategoryHeaderProvider.getCategorizedAttractions(new ArrayList<IAttraction>(App.content.getContentAsType(ICategorized.class)));
                                for(IElement attractionCategoryHeader : attractionCategoryHeaders)
                                {
                                    if(((AttractionCategoryHeader)attractionCategoryHeader).getAttractionCategory().equals(viewModel.longClickedAttractionCategory))
                                    {
                                        attractionCategoryHeaders.remove(attractionCategoryHeader);
                                        break;
                                    }
                                }

                                ActivityTool.startActivityPickForResult(
                                        ManageAttractionCategoriesActivity.this,
                                        Constants.APPLY_CATEGORY_TO_ATTRACTIONS,
                                        attractionCategoryHeaders);
                                return true;
                            }

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();

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
                    Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onAlertDialogClick:: deleting %s...", viewModel.longClickedAttractionCategory));

                    final List<IAttraction> children = new ArrayList<>(viewModel.longClickedAttractionCategory.getChildrenAsType(IAttraction.class));
                    final int index = viewModel.contentRecyclerViewAdapter.getContent().indexOf(viewModel.longClickedAttractionCategory);

                    for(IAttraction child : children)
                    {
                        child.setAttractionCategory(App.settings.getDefaultAttractionCategory());
                    }
                    App.content.removeAttractionCategory(viewModel.longClickedAttractionCategory);
                    updateContentRecyclerView();

                    snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.action_undo_delete_text, viewModel.longClickedAttractionCategory.getName()),
                            Snackbar.LENGTH_LONG);

                    snackbar.setAction(R.string.action_undo_title, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            Log.i(Constants.LOG_TAG, String.format("ManageAttractionCategoriesActivity.onAlertDialogClick:: undo delete %s...", viewModel.longClickedAttractionCategory));

                            for(IAttraction child : children)
                            {
                                child.setAttractionCategory(viewModel.longClickedAttractionCategory);
                            }

                            App.content.addAttractionCategory(index, viewModel.longClickedAttractionCategory);
                            updateContentRecyclerView();
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
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_add)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i(Constants.LOG_TAG, "ManageAttractionCategoriesViewModel.onClickFloatingActionButton:: FloatingActionButton pressed");
                ActivityTool.startActivityCreateForResult(ManageAttractionCategoriesActivity.this, Constants.REQUEST_CREATE_ATTRACTION_CATEGORY,null);
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void updateContentRecyclerView()
    {
        Log.i(Constants.LOG_TAG, "ManageAttractionCategoriesViewModel.updateContentRecyclerView:: updating RecyclerView...");

        this.viewModel.contentRecyclerViewAdapter.updateContent(new ArrayList<IElement>(App.content.getAttractionCategories()));
        this.viewModel.contentRecyclerViewAdapter.notifyDataSetChanged();
    }
}
