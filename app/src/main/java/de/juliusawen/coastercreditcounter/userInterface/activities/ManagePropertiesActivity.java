package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.IProperty;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.enums.SortType;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.SortTool;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ManagePropertiesActivity extends BaseActivity implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ManagePropertiesViewModel viewModel;

    protected void setContentView()
    {
        setContentView(R.layout.activity_manage_properties);
    }

    protected void create()
    {
        this.viewModel = new ViewModelProvider(this).get(ManagePropertiesViewModel.class);

        if(this.viewModel.requestCode == null)
        {
            this.viewModel.requestCode = RequestCode.getValue(getIntent().getIntExtra(Constants.EXTRA_REQUEST_CODE, 0));
            Log.d(String.format("%s", this.viewModel.requestCode));
        }

        this.viewModel.isSelectionMode = this.viewModel.requestCode == RequestCode.PICK_CREDIT_TYPE
                || this.viewModel.requestCode == RequestCode.PICK_CATEGORY
                || this.viewModel.requestCode == RequestCode.PICK_MANUFACTURER
                || this.viewModel.requestCode == RequestCode.PICK_MODEL
                || this.viewModel.requestCode == RequestCode.PICK_STATUS;

        if(this.viewModel.typeToManage == null)
        {
            this.viewModel.typeToManage = ElementType.getValue(getIntent().getIntExtra(Constants.EXTRA_TYPE_TO_MANAGE, -1));
        }

        if(this.viewModel.adapterFacade == null)
        {
            this.viewModel.adapterFacade = new ContentRecyclerViewAdapterFacade();

            if(this.viewModel.isSelectionMode)
            {
                this.viewModel.elements = App.content.getContentByUuidStrings(getIntent().getStringArrayListExtra(Constants.EXTRA_ELEMENTS_UUIDS));
                this.viewModel.elements = SortTool.sortElements(this.viewModel.elements, SortType.BY_NAME, SortOrder.ASCENDING);

                this.viewModel.adapterFacade.createPreconfiguredAdapter(this.viewModel.requestCode);
            }
            else //ManageMode
            {
                this.viewModel.elements = App.content.getContentOfType(this.fetchPropertyType());
                for(IElement element : this.viewModel.elements)
                {
                    Log.d(String.format("Sorting %s's children", element));
                    SortTool.sortElements(element.getChildren(), SortType.BY_NAME, SortOrder.ASCENDING);
                }

                this.viewModel.adapterFacade.getConfiguration()
                        .addOnElementTypeLongClickListener(ElementType.IPROPERTY, super.createOnElementTypeLongClickListener(ElementType.IPROPERTY))
                        .setOnScrollHandleFloatingActionButtonVisibiltyListener(super.createOnScrollHandleFloatingActionButtonVisibilityListener());

                this.viewModel.adapterFacade.createPreconfiguredAdapter(this.viewModel.requestCode, this.viewModel.typeToManage);
            }

            this.viewModel.adapterFacade.getConfiguration().addOnElementTypeClickListener(ElementType.IPROPERTY, super.createOnElementTypeClickListener(ElementType.IPROPERTY));
            this.viewModel.adapterFacade.getAdapter().setContent(this.viewModel.elements);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewManageProperties);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter((ContentRecyclerViewAdapter) this.viewModel.adapterFacade.getAdapter());

        if(this.viewModel.typeToManage == ElementType.MODEL)
        {
            this.viewModel.adapterFacade.applyPresetDecoration(this.viewModel.requestCode, GroupType.MANUFACTURER);
            this.viewModel.adapterFacade.getAdapter().groupContent(GroupType.MANUFACTURER);

            this.viewModel.adapterFacade.getConfiguration()
                    .addOnElementTypeClickListener(ElementType.IGROUP_HEADER, super.createOnElementTypeClickListener(ElementType.IGROUP_HEADER));
        }

        super.createHelpOverlayFragment(getString(R.string.title_help, getIntent().getStringExtra(Constants.EXTRA_HELP_TITLE)), getIntent().getStringExtra(Constants.EXTRA_HELP_TEXT));
        super.createToolbar();
        super.addToolbarHomeButton();
        super.setToolbarTitleAndSubtitle(getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_TITLE), getIntent().getStringExtra(Constants.EXTRA_TOOLBAR_SUBTITLE));

        super.createFloatingActionButton();
        this.decorateFloatingActionButton();

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

        IElement resultElement = ResultFetcher.fetchResultElement(data);
        switch(RequestCode.getValue(requestCode))
        {
            case CREATE_CREDIT_TYPE:
            case CREATE_CATEGORY:
            case CREATE_MANUFACTURER:
            case CREATE_MODEL:
            case CREATE_STATUS:
            {
                this.viewModel.propertyToReturn = resultElement;
                this.viewModel.adapterFacade.getAdapter().insertItem(resultElement);
                break;
            }

            case EDIT_CREDIT_TYPE:
            case EDIT_CATEGORY:
            case EDIT_MANUFACTURER:
            case EDIT_MODEL:
            case EDIT_STATUS:
            {
                this.viewModel.adapterFacade.getAdapter().notifyItemChanged(resultElement);
                break;
            }

            case SORT_CREDIT_TYPES:
            case SORT_CATEGORIES:
            case SORT_MANUFACTURERS:
            case SORT_MODELS:
            case SORT_STATUSES:
            {
                ArrayList<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                this.handleSortProperties(resultElements);
                this.viewModel.adapterFacade.getAdapter().scrollToItem(resultElement);
                invalidateOptionsMenu();
                break;
            }

            case ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS:
            case ASSIGN_CATEGORY_TO_ATTRACTIONS:
            case ASSIGN_MANUFACTURER_TO_ATTRACTIONS:
            case ASSIGN_STATUS_TO_ATTRACTIONS:
            {
                ArrayList<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                for(IElement element : resultElements)
                {
                    switch(this.viewModel.typeToManage)
                    {
                        case CREDIT_TYPE:
                            ((Attraction) element).setCreditType((CreditType) this.viewModel.longClickedElement);
                            break;

                        case CATEGORY:
                            ((Attraction) element).setCategory((Category) this.viewModel.longClickedElement);
                            break;

                        case MANUFACTURER:
                            ((Attraction) element).setManufacturer((Manufacturer) this.viewModel.longClickedElement);
                            break;

                        case MODEL:
                            ((Attraction) element).setModel((Model) this.viewModel.longClickedElement);
                            break;

                        case STATUS:
                            ((Attraction) element).setStatus((Status) this.viewModel.longClickedElement);
                            break;
                    }

                    super.markForUpdate(element);
                }

                Toaster.makeShortToast(this, getString(R.string.information_assigned_to_attractions, this.viewModel.longClickedElement.getName(), resultElements.size()));
                Log.d(String.format(Locale.getDefault(), "<ASSIGN_TO_ATTRACTIONS>:: assigned %s to [%d] attractions", this.viewModel.longClickedElement, resultElements.size()));
            }
        }
    }

    private void handleSortProperties(ArrayList<IElement> resultElements)
    {
        App.content.reorderElements(resultElements);
        this.viewModel.elements = App.content.getContentOfType(this.fetchPropertyType());

        this.viewModel.adapterFacade.getAdapter().setContent(this.viewModel.elements);

        super.markForUpdate(resultElements);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(super.getOptionsItem(item) == OptionsItem.SORT)
        {
            switch(this.viewModel.requestCode)
            {
                case MANAGE_CREDIT_TYPES:
                    ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_CREDIT_TYPES, App.content.getContentOfType(CreditType.class));
                    return true;

                case MANAGE_CATEGORIES:
                    ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_CATEGORIES, App.content.getContentOfType(Category.class));
                    return true;

                case MANAGE_MANUFACTURERS:
                    ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_MANUFACTURERS, App.content.getContentOfType(Manufacturer.class));
                    return true;

                case MANAGE_MODELS:
                    ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_MODELS, App.content.getContentOfType(Model.class));
                    return true;

                case MANAGE_STATUSES:
                    ActivityDistributor.startActivitySortForResult(this, RequestCode.SORT_STATUSES, App.content.getContentOfType(Status.class));
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.i(String.format("<%s> pressed", StringTool.keyCodeToString(keyCode)));

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            this.returnResult(RESULT_OK);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void handleOnElementTypeClick(ElementType elementType, View view)
    {
        IElement element = (IElement) view.getTag();
        switch(elementType)
        {
            case IPROPERTY:
                this.handleOnPropertyClick(element);
                break;

            case IGROUP_HEADER:
                this.handleOnGroupHeaderClick(element);
                break;

            default:
                super.handleOnElementTypeClick(elementType, view);
                break;
        }
    }

    private void handleOnGroupHeaderClick(IElement element)
    {
        this.viewModel.adapterFacade.getAdapter().toggleExpansion(element);
    }

    private void handleOnPropertyClick(IElement element)
    {
        if(this.viewModel.isSelectionMode)
        {
            this.viewModel.propertyToReturn = element;
            returnResult(RESULT_OK);
        }
        else
        {
            RequestCode requestCode;

            switch(this.viewModel.typeToManage)
            {
                case CREDIT_TYPE:
                    requestCode = RequestCode.SHOW_CREDIT_TYPE;
                    break;

                case CATEGORY:
                    requestCode = RequestCode.SHOW_CATEGORY;
                    break;

                case MANUFACTURER:
                    requestCode = RequestCode.SHOW_MANUFACTURER;
                    break;

                case MODEL:
                    requestCode = RequestCode.SHOW_MODEL;
                    break;

                case STATUS:
                    requestCode = RequestCode.SHOW_STATUS;
                    break;

                default:
                    requestCode = RequestCode.INVALID;
            }

            if(requestCode != RequestCode.INVALID)
            {
                ActivityDistributor.startActivityShow(this, requestCode, element);
            }
        }
    }

    @Override
    protected boolean handleOnElementTypeLongClick(ElementType elementType, View view)
    {
        if(elementType != ElementType.IPROPERTY)
        {
            return super.handleOnElementTypeLongClick(elementType, view);
        }

        this.viewModel.longClickedElement = (IElement) view.getTag();

        PopupMenuAgent popupMenuAgent = PopupMenuAgent.getMenu();

        if(!this.viewModel.isSelectionMode && this.viewModel.typeToManage != ElementType.MODEL)
        {
            popupMenuAgent
                    .add(PopupItem.ASSIGN_TO_ATTRACTIONS)
                    .setEnabled(PopupItem.ASSIGN_TO_ATTRACTIONS, !App.content.getContentAsType(IAttraction.class).isEmpty());
        }

        boolean longClickedPropertyIsDefault = this.fetchPropertyType().cast(this.viewModel.longClickedElement).isDefault();
        boolean isSetAsDefaultVisible = !this.viewModel.typeToManage.equals(ElementType.CREDIT_TYPE) && !this.viewModel.typeToManage.equals(ElementType.MODEL);

        popupMenuAgent
                .add(PopupItem.EDIT_ELEMENT)
                .add(PopupItem.DELETE_ELEMENT)
                .add(PopupItem.SET_AS_DEFAULT)
                .setEnabled(PopupItem.DELETE_ELEMENT, !longClickedPropertyIsDefault)
                .setEnabled(PopupItem.SET_AS_DEFAULT, !longClickedPropertyIsDefault)
                .setVisible(PopupItem.SET_AS_DEFAULT, isSetAsDefaultVisible)
                .show(ManagePropertiesActivity.this, view);

        return true;
    }

    @Override
    public void handlePopupItemClicked(PopupItem item)
    {
        switch(item)
        {
            case ASSIGN_TO_ATTRACTIONS:
            {
                this.handleAssignToAttractionsClicked();
                break;
            }

            case EDIT_ELEMENT:
            {
                this.handleEditElementClicked();
                break;
            }

            case DELETE_ELEMENT:
            {
                this.handleDeleteElementClicked();
                break;
            }

            case SET_AS_DEFAULT:
            {
                this.handleSetAsDefaultClicked();
                break;
            }
        }
    }

    private void handleAssignToAttractionsClicked()
    {
        List<IElement> elementsToAssignTo = new ArrayList<>(App.content.getContentOfType(OnSiteAttraction.class));
        List<IAttraction> possibleAttractionsToAssignTo = new LinkedList<>(ConvertTool.convertElementsToType(elementsToAssignTo, IAttraction.class));

        RequestCode requestCode = null;
        switch(this.viewModel.typeToManage)
        {
            case CREDIT_TYPE:
            {
                for(IAttraction attraction : possibleAttractionsToAssignTo)
                {
                    if(attraction.getCreditType().equals(this.viewModel.longClickedElement))
                    {
                        Log.v(String.format("removing %s from pick list - %s is already assigned", attraction, this.viewModel.longClickedElement));
                        elementsToAssignTo.remove(attraction);
                    }
                }

                requestCode = RequestCode.ASSIGN_CREDIT_TYPE_TO_ATTRACTIONS;
                break;
            }

            case CATEGORY:
            {
                for(IAttraction attraction : possibleAttractionsToAssignTo)
                {
                    if(attraction.getCategory().equals(this.viewModel.longClickedElement))
                    {
                        Log.v(String.format("removing %s from pick list - %s is already assigned", attraction, this.viewModel.longClickedElement));
                        elementsToAssignTo.remove(attraction);
                    }
                }

                requestCode = RequestCode.ASSIGN_CATEGORY_TO_ATTRACTIONS;
                break;
            }

            case MANUFACTURER:
            {
                for(IAttraction attraction : possibleAttractionsToAssignTo)
                {
                    if(attraction.getManufacturer().equals(this.viewModel.longClickedElement))
                    {
                        Log.v(String.format("removing %s from pick list - %s is already assigned", attraction, this.viewModel.longClickedElement));
                        elementsToAssignTo.remove(attraction);
                    }
                }

                requestCode = RequestCode.ASSIGN_MANUFACTURER_TO_ATTRACTIONS;
                break;
            }

            case STATUS:
            {
                for(IAttraction attraction : possibleAttractionsToAssignTo)
                {
                    if(attraction.getStatus().equals(this.viewModel.longClickedElement))
                    {
                        Log.v(String.format("removing %s from pick list - %s is already assigned", attraction, this.viewModel.longClickedElement));
                        elementsToAssignTo.remove(attraction);
                    }
                }

                requestCode = RequestCode.ASSIGN_STATUS_TO_ATTRACTIONS;
                break;
            }
        }

        if(requestCode == null)
        {
            Log.e(String.format("not able to determine RequestCode for %s", this.viewModel.typeToManage));
            return;
        }

        ActivityDistributor.startActivityPickForResult(this, requestCode, elementsToAssignTo);
    }

    private void handleEditElementClicked()
    {
        RequestCode requestCode = null;

        switch(this.viewModel.typeToManage)
        {
            case CREDIT_TYPE:
                requestCode = RequestCode.EDIT_CREDIT_TYPE;
                break;

            case CATEGORY:
                requestCode = RequestCode.EDIT_CATEGORY;
                break;

            case MANUFACTURER:
                requestCode = RequestCode.EDIT_MANUFACTURER;
                break;

            case MODEL:
            {
                // for Model's default only the name may be changed
                if(((Model) this.viewModel.longClickedElement).isDefault())
                {
                    Intent intent = new Intent(this, EditSimpleElementActivity.class);
                    intent.putExtra(Constants.EXTRA_HINT, getString(R.string.hint_edit_name, this.viewModel.longClickedElement.getName()));
                    intent.putExtra(Constants.EXTRA_TOOLBAR_TITLE, getString(R.string.title_edit_model));
                    intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.longClickedElement.getUuid().toString());
                    intent.putExtra(Constants.EXTRA_REQUEST_CODE, RequestCode.EDIT_MODEL.ordinal());
                    ActivityDistributor.startActivityViaIntent(this, intent);
                }
                else
                {
                    requestCode = RequestCode.EDIT_MODEL;
                }
                break;
            }

            case STATUS:
                requestCode = RequestCode.EDIT_STATUS;
                break;
        }

        if(requestCode == null)
        {
            Log.e(String.format("not able to determine RequestCode for %s", this.viewModel.typeToManage));
            return;
        }

        ActivityDistributor.startActivityEditForResult(this, requestCode, this.viewModel.longClickedElement);
    }

    private void handleDeleteElementClicked()
    {
        String alertDialogMessage;
        if(this.viewModel.longClickedElement.hasChildren())
        {
            String defaultName;

            switch(this.viewModel.typeToManage)
            {
                case CREDIT_TYPE:
                    defaultName = CreditType.getDefault().getName();
                    break;

                case CATEGORY:
                    defaultName = Category.getDefault().getName();
                    break;

                case MANUFACTURER:
                    defaultName = Manufacturer.getDefault().getName();
                    break;

                case MODEL:
                    defaultName = Model.getDefault().getName();
                    break;

                case STATUS:
                    defaultName = Status.getDefault().getName();
                    break;

                default:
                    defaultName = getString(R.string.error_missing_text);
                    break;
            }

            alertDialogMessage = getString(R.string.alert_dialog_message_confirm_delete_property_has_children,
                    this.viewModel.longClickedElement.getChildCount(),
                    this.viewModel.longClickedElement.getName(),
                    defaultName);
        }
        else
        {
            alertDialogMessage = getString(R.string.alert_dialog_message_confirm_delete_property_has_no_children, this.viewModel.longClickedElement.getName());
        }

        AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                R.drawable.warning,
                getString(R.string.alert_dialog_title_delete),
                alertDialogMessage,
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                RequestCode.DELETE,
                false);

        alertDialogFragmentDelete.setCancelable(false);
        alertDialogFragmentDelete.show(this.getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    private void handleSetAsDefaultClicked()
    {
        String alterDialogMessage = getString(R.string.alert_dialog_message_confirm_set_as_default, this.viewModel.longClickedElement.getName());

        AlertDialogFragment alertDialogFragmentDelete = AlertDialogFragment.newInstance(
                R.drawable.warning,
                getString(R.string.alert_dialog_title_set_as_default),
                alterDialogMessage,
                getString(R.string.text_accept),
                getString(R.string.text_cancel),
                RequestCode.SET_AS_DEFAULT,
                false);

        alertDialogFragmentDelete.setCancelable(false);
        alertDialogFragmentDelete.show(this.getSupportFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            switch(requestCode)
            {
                case DELETE:
                {
                    super.setFloatingActionButtonVisibility(false);

                    ConfirmSnackbar.Show(
                            Snackbar.make(
                                    findViewById(android.R.id.content),
                                    getString(R.string.action_confirm_delete_text, this.viewModel.longClickedElement.getName()),
                                    Snackbar.LENGTH_LONG),
                            requestCode,
                            this);
                    break;
                }

                case SET_AS_DEFAULT:
                {
                    switch(this.viewModel.typeToManage)
                    {
                        case CATEGORY:
                            super.markForUpdate(Category.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            Category.setDefault((Category) this.viewModel.longClickedElement);
                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                            break;

                        case MANUFACTURER:
                            super.markForUpdate(Manufacturer.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            Manufacturer.setDefault((Manufacturer) this.viewModel.longClickedElement);
                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                            break;

                        case STATUS:
                            super.markForUpdate(Status.getDefault());
                            super.markForUpdate(this.viewModel.longClickedElement);
                            Status.setDefault((Status) this.viewModel.longClickedElement);
                            Toaster.makeLongToast(this, getString(R.string.information_set_as_default, this.viewModel.longClickedElement.getName()));
                            break;

                        default:
                            throw new IllegalArgumentException(String.format("setting default for %s is not allowed", this.viewModel.typeToManage));
                    }

                    this.viewModel.adapterFacade.getAdapter().notifyItemChanged(this.viewModel.longClickedElement);

                    Log.d(String.format("[%s]:: setting %s as default [%s]", requestCode, this.viewModel.longClickedElement, this.viewModel.typeToManage));
                    break;
                }
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(String.format("handling confirmed action [%s]", requestCode));

        super.setFloatingActionButtonVisibility(true);

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(String.format("deleting %s...", this.viewModel.longClickedElement));

            if(this.viewModel.longClickedElement.hasChildren())
            {
                List<IAttraction> children = new ArrayList<>(this.viewModel.longClickedElement.getChildrenAsType(IAttraction.class));

                for(IAttraction child : children)
                {
                    switch(this.viewModel.typeToManage)
                    {
                        case CREDIT_TYPE:
                            child.setCreditType(CreditType.getDefault());
                            break;

                        case CATEGORY:
                            child.setCategory(Category.getDefault());
                            break;

                        case MANUFACTURER:
                            child.setManufacturer(Manufacturer.getDefault());
                            break;

                        case MODEL:
                            child.setModel(Model.getDefault());

                        case STATUS:
                            child.setStatus(Status.getDefault());
                            break;
                    }

                    super.markForUpdate(child);
                }
            }

            this.viewModel.adapterFacade.getAdapter().removeItem(this.viewModel.longClickedElement);
            super.markForDeletion(this.viewModel.longClickedElement);
        }
    }

    private void decorateFloatingActionButton()
    {
        super.setFloatingActionButtonIcon(DrawableProvider.getColoredDrawable(R.drawable.add, R.color.white));
        super.setFloatingActionButtonOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleOnFloatingActionButtonClick();
            }
        });

        super.setFloatingActionButtonVisibility(true);
    }

    private void handleOnFloatingActionButtonClick()
    {
        Log.i("FloatingActionButton clicked");

        RequestCode requestCode = null;
        switch(this.viewModel.typeToManage)
        {
            case CREDIT_TYPE:
                requestCode = RequestCode.CREATE_CREDIT_TYPE;
                break;

            case CATEGORY:
                requestCode = RequestCode.CREATE_CATEGORY;
                break;

            case MANUFACTURER:
                requestCode = RequestCode.CREATE_MANUFACTURER;
                break;

            case MODEL:
                requestCode = RequestCode.CREATE_MODEL;
                break;

            case STATUS:
                requestCode = RequestCode.CREATE_STATUS;
                break;
        }

        if(requestCode == null)
        {
            Log.e(String.format("not able to determine RequestCode for %s", this.viewModel.typeToManage));
            return;
        }

        ActivityDistributor.startActivityCreateForResult(ManagePropertiesActivity.this, requestCode);
    }

    private Class<? extends IProperty> fetchPropertyType()
    {
        switch(this.viewModel.typeToManage)
        {
            case CREDIT_TYPE:
                return CreditType.class;

            case CATEGORY:
                return Category.class;

            case MANUFACTURER:
                return Manufacturer.class;

            case MODEL:
                return Model.class;

            case STATUS:
                return Status.class;

            default:
                return null;
        }
    }

    private void returnResult(int resultCode)
    {
        Log.i(String.format("%s", StringTool.resultCodeToString(resultCode)));

        Intent intent = new Intent();

        if(resultCode == RESULT_OK)
        {
            if(this.viewModel.propertyToReturn != null)
            {
                Log.i(String.format("returning %s", this.viewModel.propertyToReturn));
                intent.putExtra(Constants.EXTRA_ELEMENT_UUID, this.viewModel.propertyToReturn.getUuid().toString());
            }
        }

        setResult(resultCode, intent);
        Log.frame(LogLevel.INFO, String.format("finishing [%s]", this.getClass().getSimpleName()), '+', false);
        finish();
    }
}