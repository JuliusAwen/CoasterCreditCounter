package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;

public class ShowPropertyActivity extends BaseActivity
{
    private ShowPropertyViewModel viewModel;

    @Override
    protected void setContentView()
    {
        setContentView(R.layout.activity_show_property);
    }

    @Override
    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ShowPropertyViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.getValue(getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0));
            Log.d(String.format("%s", this.viewModel.requestCode));
        }

        if(this.viewModel.property == null)
        {
            this.viewModel.property = (IProperty) App.content.getContentByUuid(UUID.fromString(getIntent().getStringExtra(Constants.EXTRA_ELEMENT_UUID)));

            this.reorderPropertiesChildren(SortType.BY_PARK, SortOrder.ASCENDING);
        }

        if(this.viewModel.adapterFacade == null)
        {
            this.viewModel.adapterFacade = new ContentRecyclerViewAdapterFacade();

            this.viewModel.adapterFacade.getConfiguration()
                    .addOnElementTypeClickListener(ElementType.ON_SITE_ATTRACTION, super.createOnElementTypeClickListener(ElementType.ON_SITE_ATTRACTION));

            this.viewModel.adapterFacade.createPreconfiguredAdapter(this.viewModel.requestCode);
            this.viewModel.adapterFacade.setSingleElementAsContent(this.viewModel.property);
            this.viewModel.adapterFacade.getAdapter().expandItem(this.viewModel.property, false);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShowProperty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter((ContentRecyclerViewAdapter) this.viewModel.adapterFacade.getAdapter());

        super.createHelpOverlayFragment(getString(R.string.title_help, getString(R.string.help_title_show_property)), getString(R.string.help_text_show_property));
        super.createToolbar();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), null);
        super.addToolbarHomeButton();

        super.setOptionsMenuButlerViewModel(this.viewModel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(String.format("%s, %s", RequestCode.getValue(requestCode), StringTool.resultCodeToString(resultCode)));

        if(resultCode != RESULT_OK)
        {
            return;
        }

        if(RequestCode.getValue(requestCode) == RequestCode.SHOW_ATTRACTION)
        {
            IElement resultElement = ResultFetcher.fetchResultElement(data);
            if(resultElement != null)
            {
                this.viewModel.adapterFacade.getAdapter().notifyItemChanged(resultElement);
                this.viewModel.adapterFacade.getAdapter().scrollToItem(resultElement);
            }
        }
    }

    @Override
    protected boolean handleOptionsItemSelected(MenuItem item)
    {
        switch(super.getOptionsItem(item))
        {
            case SORT_BY_PARK_ASCENDING:
                this.reorderPropertiesChildren(SortType.BY_PARK, SortOrder.ASCENDING);
                break;

            case SORT_BY_PARK_DESCENDING:
                this.reorderPropertiesChildren(SortType.BY_PARK, SortOrder.DESCENDING);
                break;

            case SORT_BY_CREDIT_TYPE_ASCENDING:
                this.reorderPropertiesChildren(SortType.BY_CREDIT_TYPE, SortOrder.ASCENDING);
                break;

            case SORT_BY_CREDIT_TYPE_DESCENDING:
                this.reorderPropertiesChildren(SortType.BY_CREDIT_TYPE, SortOrder.DESCENDING);
                break;

            case SORT_BY_CATEGORY_ASCENDING:
                this.reorderPropertiesChildren(SortType.BY_CATEGORY, SortOrder.ASCENDING);
                break;

            case SORT_BY_CATEGORY_DESCENDING:
                this.reorderPropertiesChildren(SortType.BY_CATEGORY, SortOrder.DESCENDING);
                break;

            case SORT_BY_MANUFACTURER_ASCENDING:
                this.reorderPropertiesChildren(SortType.BY_MANUFACTURER, SortOrder.ASCENDING);
                break;

            case SORT_BY_MANUFACTURER_DESCENDING:
                this.reorderPropertiesChildren(SortType.BY_MANUFACTURER, SortOrder.DESCENDING);
                break;

            case SORT_BY_MODEL_ASCENDING:
                this.reorderPropertiesChildren(SortType.BY_MODEL, SortOrder.ASCENDING);
                break;

            case SORT_BY_MODEL_DESCENDING:
                this.reorderPropertiesChildren(SortType.BY_MODEL, SortOrder.DESCENDING);
                break;

            case SORT_BY_STATUS_ASCENDING:
                this.reorderPropertiesChildren(SortType.BY_STATUS, SortOrder.ASCENDING);
                break;

            case SORT_BY_STATUS_DESCENDING:
                this.reorderPropertiesChildren(SortType.BY_STATUS, SortOrder.DESCENDING);
                break;

            default:
                return super.handleOptionsItemSelected(item);
        }

        this.viewModel.adapterFacade.setSingleElementAsContent(this.viewModel.property);
        return true;
    }

    private void reorderPropertiesChildren(SortType sortType, SortOrder sortOrder)
    {
        this.viewModel.property.reorderChildren(SortTool.sortElements(this.viewModel.property.getChildrenOfType(OnSiteAttraction.class), sortType, sortOrder));
    }

    @Override
    protected void handleOnElementTypeClick(ElementType elementType, View view)
    {
        if(elementType != ElementType.ON_SITE_ATTRACTION)
        {
            super.handleOnElementTypeClick(elementType, view);
        }

        ActivityDistributor.startActivityShowForResult(this, RequestCode.SHOW_ATTRACTION, (IElement) view.getTag());
    }
}