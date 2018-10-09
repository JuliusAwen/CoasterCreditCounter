package de.juliusawen.coastercreditcounter.presentation.activities.elements;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.activities.BaseActivity;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.RecyclerOnClickListener;
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
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "PickElementsActivity.onCreate:: creating activity...");

        setContentView(R.layout.activity_pick_elements);
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(PickElementsActivityViewModel.class);
        
        if(this.viewModel.elementsToPickFrom == null)
        {
            this.viewModel.elementsToPickFrom = App.content.fetchElementsByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            if(this.viewModel.elementsToPickFrom.get(0).isInstance(AttractionCategory.class))
            {
                this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                                this.viewModel.elementsToPickFrom, Attraction.class, true);
            }
            else
            {
                this.viewModel.contentRecyclerViewAdapter =
                        ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(this.viewModel.elementsToPickFrom, null, true);
            }
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPickElements);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);

        super.addToolbar();
        super.addToolbarHomeButton();
        if(this.viewModel.toolbarTitle == null)
        {
            this.viewModel.toolbarTitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE);
        }
        if(this.viewModel.toolbarSubtitle == null)
        {
            this.viewModel.toolbarSubtitle = getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE);
        }
        super.setToolbarTitleAndSubtitle(this.viewModel.toolbarTitle, this.viewModel.toolbarSubtitle);

        super.addFloatingActionButton();
        this.decorateFloatingActionButton();

        super.addHelpOverlayFragment(getString(R.string.title_help, this.viewModel.toolbarTitle), getText(R.string.help_text_pick_elements));

        this.addSelectOrDeselectAllBar();
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableTool.setTintToWhite(this, getDrawable(R.drawable.ic_baseline_check)));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!viewModel.contentRecyclerViewAdapter.getSelectedElementsInOrderOfSelection().isEmpty())
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: accepted - return code <OK>");
                    returnResult(RESULT_OK);
                }
                else
                {
                    Log.d(Constants.LOG_TAG, "PickElementsActivity.onClickFloatingActionButton:: no parentElement selected");
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
                    viewModel.contentRecyclerViewAdapter.selectAllElements();
                    changeRadioButtonToDeselectAll();
                }
                else if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
                {
                    viewModel.contentRecyclerViewAdapter.deselectAllElements();
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
        Log.v(Constants.LOG_TAG, "PickElementsActivity.changeRadioButtonToSelectAll:: changing RadioButton");

        this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);
    }

    private void changeRadioButtonToDeselectAll()
    {
        Log.v(Constants.LOG_TAG, "PickElementsActivity.changeRadioButtonToDeselectAll:: changing RadioButton");

        this.textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);
    }

    private void returnResult(int resultCode)
    {
        Log.i(Constants.LOG_TAG, String.format("PickElementsActivity.returnResult:: resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(viewModel.contentRecyclerViewAdapter.getSelectedElementsInOrderOfSelection()));
        }

        setResult(resultCode, intent);
        finish();
    }
}
