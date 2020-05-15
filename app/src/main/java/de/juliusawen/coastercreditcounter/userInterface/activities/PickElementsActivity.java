package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewStyler;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;

public class PickElementsActivity extends BaseActivity
{
    private PickElementsViewModel viewModel;

    private LinearLayout linearLayoutSelectAll;
    private TextView textViewSelectOrDeselectAll;
    private RadioButton radioButtonSelectOrDeselectAll;

    protected void setContentView()
    {
        setContentView(R.layout.activity_pick_elements);
    }

    protected void create()
    {
        this.linearLayoutSelectAll = this.findViewById(android.R.id.content).findViewById(R.id.linearLayoutPickElements_SelectAll);
        this.linearLayoutSelectAll.setVisibility(View.GONE);

        this.textViewSelectOrDeselectAll = this.linearLayoutSelectAll.findViewById(R.id.textViewPickElements_SelectAll);
        this.radioButtonSelectOrDeselectAll = this.linearLayoutSelectAll.findViewById(R.id.radioButtonPickElements_SelectAll);

        this.viewModel = new ViewModelProvider(this).get(PickElementsViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.values()[getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0)];
        }

        this.viewModel.isSinglePick = getIntent().getBooleanExtra(Constants.EXTRA_SINGLE_PICK, false);

        if(this.viewModel.elementsToPickFrom == null)
        {
            this.viewModel.elementsToPickFrom = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        }

        boolean groupByCategory = false;
        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            switch(this.viewModel.requestCode)
            {
                case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
                case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
                case ASSIGN_MODEL_TO_ATTRACTIONS:
                case ASSIGN_STATUS_TO_ATTRACTIONS:
                {
                    this.viewModel.elementsToPickFrom = SortTool.sortElements(this.viewModel.elementsToPickFrom, SortType.BY_NAME, SortOrder.ASCENDING);
                }

                case PICK_ATTRACTIONS:
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            OnSiteAttraction.class,
                            true)
                            .setUseDedicatedExpansionOnClickListener(true);
                    groupByCategory = true;
                    break;
                }

                case PICK_VISIT:
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            new HashSet<Class<? extends IElement>>(),
                            false)
                            .setTypefaceForContentType(Visit.class, Typeface.BOLD)
                            .setSpecialStringResourceForType(Visit.class, R.string.text_visit_display_full_name);
                    break;
                }

                default:
                {
                    this.viewModel.contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
                            this.viewModel.elementsToPickFrom,
                            new HashSet<Class<? extends IElement>>(),
                            true)
                            .setTypefaceForContentType(this.viewModel.elementsToPickFrom.get(0).getClass(), Typeface.BOLD);
                    break;
                }
            }
        }

        if(this.viewModel.contentRecyclerViewAdapter != null)
        {
            this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());
            RecyclerView recyclerView = findViewById(R.id.recyclerViewPickElements);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);
        }


        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getString(R.string.help_text_pick_elements));
        super.createToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.setOptionsMenuButlerViewModel(this.viewModel);


        if(groupByCategory)
        {
            ContentRecyclerViewStyler.groupElementsAndSetDetailModes(this.viewModel.contentRecyclerViewAdapter, this.viewModel.requestCode, GroupType.CATEGORY);
        }

        if(!this.viewModel.isSinglePick)
        {
            this.addSelectOrDeselectAllBar();

            super.createFloatingActionButton();
            this.decorateFloatingActionButtonCheck();
        }

        Log.d(String.format("requestCode[%s], isSinglePick[%s]", this.viewModel.requestCode, this.viewModel.isSinglePick));
    }

    private void decorateFloatingActionButtonCheck()
    {
        Log.d("decorating FloatingActionButton <CHECK>...");

        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("<CHECK> clicked");

                if(!viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection().isEmpty())
                {
                    Log.d("<CHECK> accepted - returning <RESULT_OK>");
                    returnResult(RESULT_OK);
                }
                else
                {
                    Log.d("<CHECK> no element selected - returning <RESULT_CANCELED>");
                    returnResult(RESULT_CANCELED);
                }
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }


    private void addSelectOrDeselectAllBar()
    {
        this.linearLayoutSelectAll.setVisibility(View.VISIBLE);

        if(this.viewModel.contentRecyclerViewAdapter.isAllSelected())
        {
            this.textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        }
        else
        {
            this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        }

        this.radioButtonSelectOrDeselectAll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d("RadioButton clicked");

                if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_select_all)))
                {
                    viewModel.contentRecyclerViewAdapter.setAllItemsSelected();
                    changeRadioButtonToDeselectAll();
                }
                else if(textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
                {
                    viewModel.contentRecyclerViewAdapter.setAllItemsDeselected();
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
                IElement element = (IElement)view.getTag();

                if(PickElementsActivity.this.viewModel.isSinglePick)
                {
                    if(element.isGroupHeader())
                    {
                        viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                        if(viewModel.contentRecyclerViewAdapter.isAllExpanded() || viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                        {
                            invalidateOptionsMenu();
                        }
                    }
                    else
                    {
                        Log.d("single pick - return code <OK>");
                        returnResult(RESULT_OK, element);
                    }
                }
                else
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

                    if(viewModel.contentRecyclerViewAdapter.isAllExpanded() || viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                    {
                        invalidateOptionsMenu();
                    }
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                return true;
            }
        };
    }

    private void changeRadioButtonToSelectAll()
    {
        this.textViewSelectOrDeselectAll.setText(R.string.text_select_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);

        Log.v(String.format("changed RadioButtonText to [%s]", this.textViewSelectOrDeselectAll.getText()));
    }

    private void changeRadioButtonToDeselectAll()
    {
        this.textViewSelectOrDeselectAll.setText(R.string.text_deselect_all);
        this.radioButtonSelectOrDeselectAll.setChecked(false);

        Log.v(String.format("changed RadioButtonText to [%s]", this.textViewSelectOrDeselectAll.getText()));
    }

    private void returnResult(int resultCode)
    {
        this.returnResult(resultCode, null);
    }

    private void returnResult(int resultCode, IElement element)
    {
        Log.i(String.format(Locale.getDefault(), "resultCode[%d]", resultCode));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            if(element == null)
            {
                if(this.viewModel.requestCode == RequestCode.PICK_VISIT)
                {
                    Log.d(String.format("returning %s", this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem()));
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.contentRecyclerViewAdapter.getLastSelectedItem().getUuid().toString());
                }
                else
                {
                    LinkedList<IElement> selectedElementsWithoutOrphanElements = new LinkedList<>();

                    for(IElement selectedElement : this.viewModel.contentRecyclerViewAdapter.getSelectedItemsInOrderOfSelection())
                    {
                        if(!selectedElement.isOrphan())
                        {
                            selectedElementsWithoutOrphanElements.add(selectedElement);
                        }
                    }

                    Log.d(String.format(Locale.getDefault(), "returning [%d] elements", selectedElementsWithoutOrphanElements.size()));
                    intent.putExtra(Constants.EXTRA_ELEMENTS_UUIDS, App.content.getUuidStringsFromElements(selectedElementsWithoutOrphanElements));
                }
            }
            else
            {
                Log.d(String.format("returning picked %s", element));
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, element.getUuid().toString());
            }
        }

        setResult(resultCode, intent);
        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', true);
        finish();
    }
}