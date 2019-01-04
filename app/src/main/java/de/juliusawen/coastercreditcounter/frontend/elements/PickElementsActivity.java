package de.juliusawen.coastercreditcounter.frontend.elements;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.Utilities.AttractionCategoryHeaderProvider;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.backend.objects.temporaryElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.frontend.BaseActivity;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

public class PickElementsActivity extends BaseActivity
{
    private PickElementsActivityViewModel viewModel;
    private TextView textViewSelectOrDeselectAll;
    private RadioButton radioButtonSelectOrDeselectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "PickElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_pick_elements);
        super.onCreate(savedInstanceState);

        if(App.isInitialized)
        {
            this.viewModel = ViewModelProviders.of(this).get(PickElementsActivityViewModel.class);

            if(this.viewModel.elementsToPickFrom == null)
            {
                this.viewModel.elementsToPickFrom = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
            }

            if(this.viewModel.attractionCategoryHeaderProvider == null)
            {
                this.viewModel.attractionCategoryHeaderProvider = new AttractionCategoryHeaderProvider();
            }

            if(this.viewModel.contentRecyclerViewAdapter == null)
            {
                if(!this.viewModel.elementsToPickFrom.isEmpty() && (this.viewModel.elementsToPickFrom.get(0) instanceof IAttraction))
                {
                    HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
                    childTypesToExpand.add(Attraction.class);

                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.attractionCategoryHeaderProvider.getCategorizedAttractions(ConvertTool.convertElementsToType(this.viewModel.elementsToPickFrom, IAttraction.class)),
                            childTypesToExpand,
                            true);
                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategoryHeader.class, Typeface.BOLD);
                }
                else
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(this.viewModel.elementsToPickFrom, null, true);
                    this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(this.viewModel.elementsToPickFrom.get(0).getClass(), Typeface.BOLD);
                }
            }
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());
            RecyclerView recyclerView = findViewById(R.id.recyclerViewPickElements);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

            super.addToolbar();
            super.addToolbarHomeButton();
            super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

            super.addFloatingActionButton();
            this.decorateFloatingActionButton();

            super.addHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getText(R.string.help_text_pick_elements));

            this.addSelectOrDeselectAllBar();
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: accepted - return code <OK>");
                    returnResult(RESULT_OK);
                }
                else
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: no element selected");
                    Toaster.makeToast(PickElementsActivity.this, getString(R.string.error_text_no_entry_selected));
                }
            }
        });
        super.setFloatingActionButtonVisibility(true);
    }

    private void addSelectOrDeselectAllBar()
    {
        LinearLayout linearLayoutSelectAll = this.findViewById(android.R.id.content).findViewById(R.id.linearLayoutPickElements_SelectAll);
        linearLayoutSelectAll.setVisibility(View.VISIBLE);

        this.textViewSelectOrDeselectAll = linearLayoutSelectAll.findViewById(R.id.textViewPickElements_SelectAll);

        if(this.viewModel.contentRecyclerViewAdapter.isAllSelected())
        {
            this.textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        }
        else
        {
            this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        }

        this.radioButtonSelectOrDeselectAll = linearLayoutSelectAll.findViewById(R.id.radioButtonPickElements_SelectAll);
        this.radioButtonSelectOrDeselectAll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickSelectOrDeselectAllBar:: RadioButton clicked");

                if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_select_all)))
                {
                    viewModel.contentRecyclerViewAdapter.selectAllItems();
                    changeRadioButtonToDeselectAll();
                }
                else if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
                {
                    viewModel.contentRecyclerViewAdapter.deselectAllItems();
                    changeRadioButtonToSelectAll();
                }
            }
        });
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!view.isSelected() && viewModel.contentRecyclerViewAdapter.isAllSelected())
                {
                    changeRadioButtonToDeselectAll();
                }
                else
                {
                    if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
                    {
                        changeRadioButtonToSelectAll();
                    }
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                return false;
            }
        };
    }

    private void changeRadioButtonToSelectAll()
    {
        this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);

        Log.v(Constants.LOG_TAG, String.format("PickElementsActivity.changeRadioButtonToSelectAll:: changed RadioButtonText to [%s]", this.textViewSelectOrDeselectAll.getText()));
    }

    private void changeRadioButtonToDeselectAll()
    {
        this.textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);

        Log.v(Constants.LOG_TAG, String.format("PickElementsActivity.changeRadioButtonToDeselectAll:: changed RadioButtonText to [%s]", this.textViewSelectOrDeselectAll.getText()));
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            List<IElement> selectedElementsWithoutOrphanElements = new ArrayList<>();
            for(IElement element : this.viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection())
            {
                if(!OrphanElement.class.isInstance(element))
                {
                    selectedElementsWithoutOrphanElements.add(element);
                }
            }

            Log.d(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: returning [%d] elements", selectedElementsWithoutOrphanElements.size()));
            intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(selectedElementsWithoutOrphanElements));
        }

        setResult(resultCode, intent);
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_FINISH);
        finish();
    }
}
