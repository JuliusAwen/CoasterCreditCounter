package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.GroupHeader;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailDisplayMode;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.DetailType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

import static de.juliusawen.coastercreditcounter.application.Constants.LOG_TAG;

public  class ShowAttractionsFragment extends Fragment implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowAttractionsFragmentViewModel viewModel;
    private ShowAttractionsFragmentInteraction showAttractionsFragmentInteraction;
    private RecyclerView recyclerView;

    public static ShowAttractionsFragment newInstance(String uuidString)
    {
        Log.i(LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowAttractionsFragment.newInstance:: instantiating fragment...");

        ShowAttractionsFragment showAttractionsFragment =  new ShowAttractionsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, uuidString);
        showAttractionsFragment.setArguments(args);

        return showAttractionsFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.v(LOG_TAG, "ShowAttractionsFragment.onCreate:: creating fragment...");
        super.onCreate(savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(ShowAttractionsFragmentViewModel.class);

        if(this.viewModel.park == null)
        {
            if(getArguments() != null)
            {
                this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
            }
        }

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerViewAdapter();
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForContentType(GroupHeader.class, Typeface.BOLD);
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());

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
        this.recyclerView = view.findViewById(R.id.recyclerViewFragmentShowAttractions);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.recyclerView.setAdapter(this.viewModel.contentRecyclerViewAdapter);
    }

    @Override
    public void onDestroyView()
    {
        this.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement selectedElement = ResultFetcher.fetchResultElement(data);

            switch(RequestCode.values()[requestCode])
            {
                case SORT_ATTRACTIONS:
                    List<IElement> resultElements = ResultFetcher.fetchResultElements(data);
                    IElement parent = resultElements.get(0).getParent();
                    if(parent != null)
                    {
                        this.viewModel.park.reorderChildren(resultElements);

                        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.park.fetchChildrenOfType(IOnSiteAttraction.class))
                                .scrollToItem(((Attraction)selectedElement).getCategory());
                    }
                    break;

                case EDIT_CUSTOM_ATTRACTION:
                    if(!selectedElement.getName().equals(this.viewModel.formerAttractionName))
                    {
                        Log.d(LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<EditCustomAttraction>:: edited %s", selectedElement));

                        for(IElement visit : selectedElement.getParent().fetchChildrenOfType(Visit.class))
                        {
                            for(IElement visitedAttraction : visit.fetchChildrenOfType(VisitedAttraction.class))
                            {
                                if(visitedAttraction.getName().equals(this.viewModel.formerAttractionName))
                                {
                                    visitedAttraction.setName(selectedElement.getName());
                                    Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<EditCustomAttraction>:: renamed VisitedAttraction [%s]", visitedAttraction));
                                }
                            }
                        }

                        this.showAttractionsFragmentInteraction.markForUpdate(selectedElement.getParent());
                        this.updateContentRecyclerView().scrollToItem(selectedElement);
                    }
                    this.viewModel.formerAttractionName = null;
                    break;

                case CREATE_CUSTOM_ATTRACTION:
                    this.updateContentRecyclerView();
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof ShowAttractionsFragment.ShowAttractionsFragmentInteraction)
        {
            this.showAttractionsFragmentInteraction = (ShowAttractionsFragment.ShowAttractionsFragmentInteraction) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement ShowAttractionsFragmentInteraction");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        this.viewModel.optionsMenuAgent
                .add(OptionsItem.EXPAND_ALL)
                .add(OptionsItem.COLLAPSE_ALL)
                .create(menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        this.viewModel.optionsMenuAgent
                .setEnabled(OptionsItem.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .setEnabled(OptionsItem.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                .setVisible(OptionsItem.EXPAND_ALL, this.viewModel.park.hasChildrenOfType(IAttraction.class))
                .setVisible(OptionsItem.COLLAPSE_ALL, this.viewModel.park.hasChildrenOfType(IAttraction.class))
                .prepare(menu);
    }

    public boolean expandAll()
    {
        this.viewModel.contentRecyclerViewAdapter.expandAll();
        getActivity().invalidateOptionsMenu();
        return true;
    }

    public boolean collapseAll()
    {
        this.viewModel.contentRecyclerViewAdapter.collapseAll();
        getActivity().invalidateOptionsMenu();
        return true;
    }

    private ContentRecyclerViewAdapter createContentRecyclerViewAdapter()
    {
        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
        childTypesToExpand.add(Attraction.class);

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                this.viewModel.park.fetchChildrenOfType(IOnSiteAttraction.class),
                childTypesToExpand);

        contentRecyclerViewAdapter
                .setDisplayModeForDetail(DetailType.MANUFACTURER, DetailDisplayMode.ABOVE)
                .setDisplayModeForDetail(DetailType.STATUS, DetailDisplayMode.BELOW)
                .setTypefaceForDetailType(DetailType.STATUS, Typeface.ITALIC)
                .setDisplayModeForDetail(DetailType.TOTAL_RIDE_COUNT, DetailDisplayMode.BELOW)
                .groupItems(GroupType.CATEGORY);

        return contentRecyclerViewAdapter;
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element.isAttraction())
                {
                    Toaster.notYetImplemented(getContext());
                }
                else if(element.isGroupHeader())
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
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
                            .setVisible(PopupItem.SORT_ATTRACTIONS,
                                    viewModel.longClickedElement.getChildCountOfType(Attraction.class) > 1 || viewModel.longClickedElement.getChildCountOfType(VisitedAttraction.class) > 1)
                            .show(getContext(), view);
                }
                else
                {
                    PopupMenuAgent.getMenu()
                            .add(PopupItem.EDIT_CUSTOM_ATTRACTION)
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
            attractions = this.viewModel.longClickedElement.fetchChildrenOfType(Attraction.class);
        }
        else if(this.viewModel.longClickedElement.hasChildrenOfType(VisitedAttraction.class))
        {
            attractions = this.viewModel.longClickedElement.fetchChildrenOfType(VisitedAttraction.class);
        }

        ActivityDistributor.startActivitySortForResult(getContext(), RequestCode.SORT_ATTRACTIONS, attractions);
    }

    public void handlePopupItemEditCustomAttractionClicked()
    {
        this.viewModel.formerAttractionName = this.viewModel.longClickedElement.getName();

        ActivityDistributor.startActivityEditForResult(
                getContext(),
                RequestCode.EDIT_CUSTOM_ATTRACTION,
                this.viewModel.longClickedElement);
    }

    public void handlePopupItemDeleteAttractionClicked()
    {
        AlertDialogFragment alertDialogFragmentDelete =
                AlertDialogFragment.newInstance(
                        R.drawable.ic_baseline_warning,
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
        Log.i(Constants.LOG_TAG, String.format("ShowAttractionsFragment.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(LOG_TAG, String.format("ShowAttractionsFragment.handleActionConfirmed:: deleting %s...", this.viewModel.longClickedElement));

            for(Visit visit : this.viewModel.longClickedElement.getParent().fetchChildrenAsType(Visit.class))
            {
                for(VisitedAttraction visitedAttraction : visit.fetchChildrenAsType(VisitedAttraction.class))
                {
                    if(visitedAttraction.getOnSiteAttraction().equals(this.viewModel.longClickedElement))
                    {
                        this.showAttractionsFragmentInteraction.markForDeletion(visitedAttraction);
                        visitedAttraction.deleteElementAndDescendants();
                    }
                }
            }

            this.showAttractionsFragmentInteraction.markForDeletion(this.viewModel.longClickedElement);
            this.viewModel.longClickedElement.deleteElementAndDescendants();
            updateContentRecyclerView();
        }
    }

    private ContentRecyclerViewAdapter updateContentRecyclerView()
    {
        Log.i(LOG_TAG, "ShowAttractionsFragment.updateContentRecyclerView:: updating RecyclerView...");
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.park.fetchChildrenOfType(IOnSiteAttraction.class));

        return this.viewModel.contentRecyclerViewAdapter;
    }

    public interface ShowAttractionsFragmentInteraction
    {
        void markForUpdate(IElement elementToUpdate);
        void markForDeletion(IElement elemtToDelete);
    }
}