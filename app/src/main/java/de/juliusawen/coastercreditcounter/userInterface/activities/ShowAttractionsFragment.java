package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.ElementType;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterFacade;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public  class ShowAttractionsFragment extends Fragment implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowParkSharedViewModel viewModel;
    private ShowAttractionsFragmentInteraction fragmentInteraction;

    public static ShowAttractionsFragment newInstance()
    {
        return new ShowAttractionsFragment();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof ShowAttractionsFragment.ShowAttractionsFragmentInteraction)
        {
            this.fragmentInteraction = (ShowAttractionsFragment.ShowAttractionsFragmentInteraction) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement ShowAttractionsFragmentInteraction");
        }
    }


    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.frame(LogLevel.INFO, "creating...", '#', true);
        super.onCreate(savedInstanceState);

        this.viewModel = new ViewModelProvider(getActivity()).get(ShowParkSharedViewModel.class);

        if(this.viewModel.showAttractionsAdapterFacade == null)
        {
            this.viewModel.showAttractionsAdapterFacade = new ContentRecyclerViewAdapterFacade();
            this.viewModel.showAttractionsAdapterFacade.createPreconfiguredAdapter(RequestCode.SHOW_ATTRACTIONS, GroupType.CATEGORY);
            this.viewModel.showAttractionsAdapterFacade.getConfiguration()
                    .addOnElementTypeClickListener(ElementType.ON_SITE_ATTRACTION, this.fragmentInteraction.createOnElementTypeClickListener(ElementType.ON_SITE_ATTRACTION))
                    .addOnElementTypeClickListener(ElementType.GROUP_HEADER, this.fragmentInteraction.createOnElementTypeClickListener(ElementType.GROUP_HEADER))
                    .addOnElementTypeLongClickListener(ElementType.ON_SITE_ATTRACTION, this.fragmentInteraction.createOnElementTypeLongClickListener(ElementType.ON_SITE_ATTRACTION))
                    .addOnElementTypeLongClickListener(ElementType.GROUP_HEADER, this.fragmentInteraction.createOnElementTypeLongClickListener(ElementType.GROUP_HEADER));
        }

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_attractions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewShowAttractions);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter((ContentRecyclerViewAdapter) this.viewModel.showAttractionsAdapterFacade.getAdapter());

        this.updateContentRecyclerView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.viewModel.requestCode = RequestCode.SHOW_ATTRACTIONS;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(String.format("%s, %s", RequestCode.getValue(requestCode), StringTool.resultCodeToString(resultCode)));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement resultElement = ResultFetcher.fetchResultElement(data);

            switch(RequestCode.getValue(requestCode))
            {
                case SORT_ATTRACTIONS:
                {
                    List<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                    IElement parent = resultElements.get(0).getParent();
                    if(parent != null)
                    {
                        this.viewModel.park.reorderChildren(resultElements);

                        this.fragmentInteraction.markForUpdate(resultElement.getParent());

                        this.updateContentRecyclerView();
                        this.viewModel.showAttractionsAdapterFacade.getAdapter().expandGroupHeaderForItem(resultElement);
                        this.viewModel.showAttractionsAdapterFacade.getAdapter().scrollToItem(resultElement);
                    }
                    break;
                }

                case SHOW_ATTRACTION:
                {
                    this.viewModel.showAttractionsAdapterFacade.getAdapter().notifyItemChanged(resultElement);
                    break;
                }

                case CREATE_ATTRACTION:
                {
                    this.updateContentRecyclerView();
                    this.viewModel.showAttractionsAdapterFacade.getAdapter().expandGroupHeaderForItem(resultElement);
                    this.viewModel.showAttractionsAdapterFacade.getAdapter().scrollToItem(resultElement);
                    break;
                }
            }
        }
    }

    public void handleOnElementTypeClick(ElementType elementType, View view)
    {
        IElement element = (IElement) view.getTag();
        switch(elementType)
        {
            case
                GROUP_HEADER:
                this.handleOnGroupHeaderClick(element);
                break;

            case ON_SITE_ATTRACTION:
                this.handleOnOnSiteAttractionClick(element);
                break;

            default:
                Log.e(String.format("unhandled click on %s", elementType));
        }
    }

    private void handleOnGroupHeaderClick(IElement element)
    {
        this.viewModel.showAttractionsAdapterFacade.getAdapter().toggleExpansion(element);
        if(this.viewModel.showAttractionsAdapterFacade.getAdapter().isAllContentExpanded() || this.viewModel.showAttractionsAdapterFacade.getAdapter().isAllContentCollapsed())
        {
            getActivity().invalidateOptionsMenu();
        }
    }

    private void handleOnOnSiteAttractionClick(IElement element)
    {
        ActivityDistributor.startActivityShowForResult(getContext(), RequestCode.SHOW_ATTRACTION, element);
    }

    public boolean handleOnElementTypeLongClick(ElementType elementType, View view)
    {
        this.viewModel.longClickedElement = (IElement)view.getTag();

        switch(elementType)
        {
            case GROUP_HEADER:
                return this.handleOnGroupHeaderLongClick(view);

            case ON_SITE_ATTRACTION:
                return this.handleOnOnSiteAttractionLongClick(view);

            default:
                Log.e(String.format("unhandled click on %s", elementType));
                return false;
        }
    }

    private boolean handleOnGroupHeaderLongClick(View view)
    {
        PopupMenuAgent.getMenu()
                .add(PopupItem.SORT_ATTRACTIONS)
                .setVisible(PopupItem.SORT_ATTRACTIONS, viewModel.longClickedElement.getChildCountOfType(IAttraction.class) > 1)
                .show(getContext(), view);

        return true;
    }

    private boolean handleOnOnSiteAttractionLongClick(View view)
    {
        PopupMenuAgent.getMenu()
                .add(PopupItem.DELETE_ATTRACTION)
                .show(getContext(), view);

        return true;
    }

    public void handlePopupItemSortAttractionsClicked()
    {
        List<IElement> attractions = new ArrayList<>();

        if(this.viewModel.longClickedElement.hasChildrenOfType(Attraction.class))
        {
            attractions = this.viewModel.longClickedElement.getChildrenOfType(Attraction.class);
        }
        else if(this.viewModel.longClickedElement.hasChildrenOfType(VisitedAttraction.class))
        {
            attractions = this.viewModel.longClickedElement.getChildrenOfType(VisitedAttraction.class);
        }

        ActivityDistributor.startActivitySortForResult(getContext(), RequestCode.SORT_ATTRACTIONS, attractions);
    }

    public void handlePopupItemDeleteAttractionClicked()
    {
        AlertDialogFragment alertDialogFragmentDelete =
                AlertDialogFragment.newInstance(
                        R.drawable.warning,
                        getString(R.string.alert_dialog_title_delete_attraction),
                        getString(R.string.alert_dialog_message_confirm_delete_attraction, viewModel.longClickedElement.getName()),
                        getString(R.string.text_accept),
                        getString(R.string.text_cancel),
                        RequestCode.DELETE,
                        true);

        alertDialogFragmentDelete.setCancelable(false);
        alertDialogFragmentDelete.show(getChildFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            if(requestCode == RequestCode.DELETE)
            {
                this.fragmentInteraction.setFloatingActionButtonVisibility(false);
                ConfirmSnackbar.Show(
                        Snackbar.make(
                                getActivity().findViewById(android.R.id.content),
                                getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                                Snackbar.LENGTH_LONG),
                        requestCode,
                        this);
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(String.format("handling confirmed action [%s]", requestCode));

        this.fragmentInteraction.setFloatingActionButtonVisibility(true);

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(String.format("deleting %s...", this.viewModel.longClickedElement));

            for(Visit visit : this.viewModel.longClickedElement.getParent().getChildrenAsType(Visit.class))
            {
                for(VisitedAttraction visitedAttraction : visit.getChildrenAsType(VisitedAttraction.class))
                {
                    if(visitedAttraction.getOnSiteAttraction().equals(this.viewModel.longClickedElement))
                    {
                        visitedAttraction.deleteElementAndDescendants();
                    }
                }
            }

            this.fragmentInteraction.markForDeletion(this.viewModel.longClickedElement, true);
            updateContentRecyclerView();
        }
    }

    private void updateContentRecyclerView()
    {
        Log.d("resetting content...");
        this.viewModel.showAttractionsAdapterFacade.getAdapter().setContent(this.viewModel.park.getChildrenOfType(OnSiteAttraction.class));
        this.viewModel.showAttractionsAdapterFacade.getAdapter().groupContent(GroupType.CATEGORY);
    }

    public interface ShowAttractionsFragmentInteraction
    {
        View.OnClickListener createOnElementTypeClickListener(ElementType elementType);
        View.OnLongClickListener createOnElementTypeLongClickListener(ElementType elementType);
        void setFloatingActionButtonVisibility(boolean isVisible);
        void markForUpdate(IElement elementToUpdate);
        void markForDeletion(IElement elementToDelete, boolean deleteDescendants);
    }
}