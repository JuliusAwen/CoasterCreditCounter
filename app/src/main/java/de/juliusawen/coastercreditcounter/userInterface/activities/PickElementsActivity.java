package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;

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
            Log.d(String.format("%s", this.viewModel.requestCode));
        }

        this.viewModel.isSinglePick = getIntent().getBooleanExtra(Constants.EXTRA_SINGLE_PICK, false);

        if(this.viewModel.elementsToPickFrom == null)
        {
            this.viewModel.elementsToPickFrom = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
        }

        if(this.viewModel.adapterFacade == null)
        {
            this.viewModel.adapterFacade = new ContentRecyclerViewAdapterFacade();
            this.viewModel.adapterFacade.createPreconfiguredAdapter(this.viewModel.requestCode);

            switch(this.viewModel.requestCode)
            {
                case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
                case ASSIGN_CATEGORY_TO_ATTRACTIONS:
                case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
                case ASSIGN_STATUS_TO_ATTRACTIONS:
                {
                    this.viewModel.elementsToPickFrom = SortTool.sortElements(this.viewModel.elementsToPickFrom, SortType.BY_NAME, SortOrder.ASCENDING);
                }
                case PICK_ATTRACTIONS:
                {
                    this.viewModel.adapterFacade.getAdapter().setContent(this.viewModel.elementsToPickFrom);
                    this.viewModel.adapterFacade.applyPresetDecoration(this.viewModel.requestCode, GroupType.CATEGORY);
                    this.viewModel.adapterFacade.getAdapter().groupContent(GroupType.CATEGORY);
                    break;
                }

                case PICK_VISIT:
                {
                    this.viewModel.adapterFacade.getAdapter().setContent(this.viewModel.elementsToPickFrom);
                    break;
                }

                default:
                {

//                    this.viewModel.oldContentRecyclerViewAdapter = OLD_ContentRecyclerViewAdapterProvider.getSelectableContentRecyclerViewAdapter(
//                            this.viewModel.elementsToPickFrom,
//                            new HashSet<Class<? extends IElement>>(),
//                            true)
//                            .setTypefaceForContentType(this.viewModel.elementsToPickFrom.get(0).getClass(), Typeface.BOLD);

                    throw new IllegalStateException(String.format("CONGRATS - you found your default case: %s", this.viewModel.requestCode));
                }
            }

            this.viewModel.adapterFacade.getConfiguration().addOnElementTypeClickListener(ElementType.IELEMENT, super.createOnElementTypeClickListener(ElementType.IELEMENT));
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPickElements);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter((ContentRecyclerViewAdapter) this.viewModel.adapterFacade.getAdapter());


        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE)), getString(R.string.help_text_pick_elements));
        super.createToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.setOptionsMenuButlerViewModel(this.viewModel);


        if(!this.viewModel.isSinglePick)
        {
            this.addSelectOrDeselectAllBar();

            super.createFloatingActionButton();
            this.decorateFloatingActionButtonCheck();
        }

        Log.d(String.format("%s, isSinglePick[%s]", this.viewModel.requestCode, this.viewModel.isSinglePick));
    }

    private void decorateFloatingActionButtonCheck()
    {
        Log.d("decorating FloatingActionButton<CHECK>...");

        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.check, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.i("FloatingActionButton<CHECK> clicked");
                handleOnFloatingActionButtonClicked();
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    private void handleOnFloatingActionButtonClicked()
    {
        if(!viewModel.adapterFacade.getAdapter().getSelectedItemsInOrderOfSelection().isEmpty())
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


    private void addSelectOrDeselectAllBar()
    {
        this.linearLayoutSelectAll.setVisibility(View.VISIBLE);

        if(this.viewModel.adapterFacade.getAdapter().isAllContentSelected())
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
                Log.d("RadioButton<SELECT_ALL> clicked");
                handleOnRadioButtonClicked();
            }
        });
    }

    private void handleOnRadioButtonClicked()
    {
        if(this.textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_select_all)))
        {
            this.viewModel.adapterFacade.getAdapter().selectAllContent();
            changeRadioButtonToDeselectAll();
        }
        else if(this.textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
        {
            this.viewModel.adapterFacade.getAdapter().deselectAllContent();
            changeRadioButtonToSelectAll();
        }
    }

    @Override
    protected void handleOnElementTypeClick(ElementType elementType, View view)
    {
        if(elementType != ElementType.IELEMENT)
        {
            super.handleOnElementTypeClick(elementType, view);
            return;
        }

        IElement element = (IElement)view.getTag();

        if(this.viewModel.isSinglePick)
        {
            if(element.isGroupHeader())
            {
                this.viewModel.adapterFacade.getAdapter().toggleExpansion(element);
                if(this.viewModel.adapterFacade.getAdapter().isAllContentExpanded() || this.viewModel.adapterFacade.getAdapter().isAllContentCollapsed())
                {
                    invalidateOptionsMenu();
                }
            }
            else
            {
                returnResult(RESULT_OK, element);
            }
        }
        else
        {
            if(!view.isSelected() && this.viewModel.adapterFacade.getAdapter().isAllContentSelected())
            {
                changeRadioButtonToDeselectAll();
            }
            else
            {
                if(this.textViewSelectOrDeselectAll.getText().equals(getString(R.string.text_deselect_all)))
                {
                    changeRadioButtonToSelectAll();
                }
            }

            if(this.viewModel.adapterFacade.getAdapter().isAllContentExpanded() || this.viewModel.adapterFacade.getAdapter().isAllContentCollapsed())
            {
                invalidateOptionsMenu();
            }
        }
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
        Log.i(String.format("ResultCode[%s]", StringTool.resultCodeToString(resultCode)));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            if(element == null)
            {
                if(this.viewModel.requestCode == RequestCode.PICK_VISIT)
                {
                    Log.d(String.format("returning %s", this.viewModel.adapterFacade.getAdapter().getLastSelectedItem()));
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.adapterFacade.getAdapter().getLastSelectedItem().getUuid().toString());
                }
                else
                {
                    LinkedList<IElement> selectedElementsWithoutOrphanElements = new LinkedList<>();

                    for(IElement selectedElement : this.viewModel.adapterFacade.getAdapter().getSelectedItemsInOrderOfSelection())
                    {
                        if(!selectedElement.isOrphan())
                        {
                            selectedElementsWithoutOrphanElements.add(selectedElement);
                        }
                    }

                    Log.d(String.format(Locale.getDefault(), "returning [%d] Elements", selectedElementsWithoutOrphanElements.size()));
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
        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', false);
        finish();
    }
}