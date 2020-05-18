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
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
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
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD_ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD_ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD_ContentRecyclerViewOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.OLD_ContentRecyclerViewStyler;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public  class ShowAttractionsFragment extends Fragment implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowParkSharedViewModel viewModel;
    private OLD_ContentRecyclerViewAdapter oldContentRecyclerViewAdapter;
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
        this.oldContentRecyclerViewAdapter = this.createContentRecyclerViewAdapter();

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
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFragmentShowAttractions);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(this.oldContentRecyclerViewAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.viewModel.requestCode = RequestCode.SHOW_ATTRACTIONS;
        this.viewModel.oldContentRecyclerViewAdapter = this.oldContentRecyclerViewAdapter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(String.format("RequestCode[%s], ResultCode[%s]", RequestCode.getValue(requestCode), StringTool.resultCodeToString(resultCode)));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement resultElement = ResultFetcher.fetchResultElement(data);

            switch(RequestCode.values()[requestCode])
            {
                case SORT_ATTRACTIONS:
                {
                    List<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                    IElement parent = resultElements.get(0).getParent();
                    if(parent != null)
                    {
                        this.viewModel.park.reorderChildren(resultElements);

                        this.fragmentInteraction.markForUpdate(resultElement.getParent());

                        this.updateContentRecyclerView(true)
                                .expandGroupHeaderOfElement(resultElement)
                                .scrollToItem(resultElement);
                    }
                    break;
                }

                case SHOW_ATTRACTION:
                {
                    this.oldContentRecyclerViewAdapter.notifyItemChanged(resultElement);
                    break;
                }

                case CREATE_ATTRACTION:
                {
                    this.updateContentRecyclerView(true)
                            .expandGroupHeaderOfElement(resultElement)
                            .scrollToItem(resultElement);
                    break;
                }
            }
        }
    }

    private OLD_ContentRecyclerViewAdapter createContentRecyclerViewAdapter()
    {
        OLD_ContentRecyclerViewAdapter oldContentRecyclerViewAdapter = OLD_ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                this.viewModel.park.getChildrenOfType(OnSiteAttraction.class),
                OnSiteAttraction.class)
                .setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());

        OLD_ContentRecyclerViewStyler.groupElementsAndSetDetailModes(oldContentRecyclerViewAdapter, RequestCode.SHOW_ATTRACTIONS, GroupType.CATEGORY);

        return oldContentRecyclerViewAdapter;
    }

    private OLD_ContentRecyclerViewOnClickListener.CustomOnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new OLD_ContentRecyclerViewOnClickListener.CustomOnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element.isAttraction())
                {
                    ActivityDistributor.startActivityShowForResult(getContext(), RequestCode.SHOW_ATTRACTION, element);
                }
                else if(element.isGroupHeader())
                {
                    oldContentRecyclerViewAdapter.toggleExpansion(element);
                    if(oldContentRecyclerViewAdapter.isAllExpanded() || oldContentRecyclerViewAdapter.isAllCollapsed())
                    {
                        getActivity().invalidateOptionsMenu();
                    }
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                viewModel.longClickedElement = (IElement)view.getTag();

                if(viewModel.longClickedElement.isGroupHeader())
                {
                    PopupMenuAgent.getMenu()
                            .add(PopupItem.SORT_ATTRACTIONS)
                            .setVisible(PopupItem.SORT_ATTRACTIONS, viewModel.longClickedElement.getChildCountOfType(IAttraction.class) > 1)
                            .show(getContext(), view);
                }
                else
                {
                    PopupMenuAgent.getMenu()
                            .add(PopupItem.DELETE_ATTRACTION)
                            .show(getContext(), view);
                }

                return true;
            }
        };
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
            updateContentRecyclerView(true);
        }
    }

    private OLD_ContentRecyclerViewAdapter updateContentRecyclerView(boolean resetContent)
    {
        if(resetContent)
        {
            Log.d("resetting content...");
            this.oldContentRecyclerViewAdapter.setItems(this.viewModel.park.getChildrenOfType(OnSiteAttraction.class));
        }
        else
        {
            Log.d("notifying data set changed...");
            this.oldContentRecyclerViewAdapter.notifyDataSetChanged();
        }

        return this.oldContentRecyclerViewAdapter;
    }

    public interface ShowAttractionsFragmentInteraction
    {
        void setFloatingActionButtonVisibility(boolean isVisible);
        void markForUpdate(IElement elementToUpdate);
        void markForDeletion(IElement elementToDelete, boolean deleteDescendants);
    }
}