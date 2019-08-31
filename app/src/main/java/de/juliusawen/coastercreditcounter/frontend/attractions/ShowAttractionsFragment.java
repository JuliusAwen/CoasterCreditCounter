package de.juliusawen.coastercreditcounter.frontend.attractions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.Element;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;
import de.juliusawen.coastercreditcounter.toolbox.ResultTool;
import de.juliusawen.coastercreditcounter.toolbox.Toaster;

import static de.juliusawen.coastercreditcounter.globals.Constants.LOG_TAG;

public  class ShowAttractionsFragment extends Fragment implements AlertDialogFragment.AlertDialogListener
{
    private ShowAttractionsFragmentViewModel viewModel;
    private RecyclerView recyclerView;
    private ShowAttractionsFragmentInteraction showAttractionsFragmentInteraction;
    private boolean actionConfirmed;

    public ShowAttractionsFragment() {}

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

        this.viewModel = ViewModelProviders.of(this).get(ShowAttractionsFragmentViewModel.class);

        if(this.viewModel.park == null)
        {
            if(getArguments() != null)
            {
                this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
            }
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerViewAdapter();
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(AttractionCategoryHeader.class, Typeface.BOLD);
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewAdapterOnClickListener());

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_attractions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
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
            IElement selectedElement = ResultTool.fetchResultElement(data);

            if(requestCode == Constants.REQUEST_CODE_SORT_ATTRACTIONS)
            {
                List<IElement> resultElements = ResultTool.fetchResultElements(data);

                IElement parent = resultElements.get(0).getParent();
                if(parent != null)
                {
                    this.viewModel.park.reorderChildren(resultElements);

                    this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.park.getChildrenOfType(IOnSiteAttraction.class));

                    if(selectedElement != null)
                    {
                        Log.d(LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<SortAttractions>:: scrolling to selected element %s...", selectedElement));
                        this.viewModel.contentRecyclerViewAdapter.scrollToItem(((Attraction)selectedElement).getAttractionCategory());
                    }
                }
            }
            else if(requestCode == Constants.REQUEST_CODE_EDIT_CUSTOM_ATTRACTION)
            {
                Log.d(LOG_TAG, String.format("ShowAttractionsFragment.onActivityResult<EditCustomAttraction>:: edited %s", selectedElement));
                this.showAttractionsFragmentInteraction.updateElement(selectedElement);
                this.updateContentRecyclerView();
                this.viewModel.contentRecyclerViewAdapter.scrollToItem(selectedElement);
            }
            else if(requestCode == Constants.REQUEST_CODE_CREATE_CUSTOM_ATTRACTION)
            {
                this.updateContentRecyclerView();
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
    public void onDetach()
    {
        super.onDetach();
        this.viewModel = null;
        this.recyclerView = null;
        this.showAttractionsFragmentInteraction = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == Constants.SELECTION_EXPAND_ALL)
        {
            this.viewModel.contentRecyclerViewAdapter.expandAll();
        }
        else if(item.getItemId() == Constants.SELECTION_COLLAPSE_ALL)
        {
            this.viewModel.contentRecyclerViewAdapter.collapseAll();
        }

        return super.onOptionsItemSelected(item);
    }

    private ContentRecyclerViewAdapter createContentRecyclerViewAdapter()
    {
        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
        childTypesToExpand.add(Attraction.class);

        ContentRecyclerViewAdapter contentRecyclerViewAdapter = ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                this.viewModel.park.getChildrenOfType(IOnSiteAttraction.class),
                childTypesToExpand,
                Constants.TYPE_ATTRACTION_CATEGORY);
        contentRecyclerViewAdapter.displayManufacturers(true);
        contentRecyclerViewAdapter.displayStatus(true);
        contentRecyclerViewAdapter.displayTotalRideCount(true);

        return contentRecyclerViewAdapter;
    }

    public boolean isAllExpanded()
    {
        return this.viewModel.contentRecyclerViewAdapter.isAllExpanded();
    }

    public boolean isAllCollapsed()
    {
        return this.viewModel.contentRecyclerViewAdapter.isAllCollapsed();
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewAdapterOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();

                if(element instanceof Attraction)
                {
                    Toaster.makeToast(getContext(), String.format("ShowAttraction not yet implemented %s", (Element) view.getTag()));
                }
                else if(element instanceof AttractionCategoryHeader)
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
            }

            @Override
            public boolean onLongClick(View view)
            {
                if(view.getTag() instanceof AttractionCategoryHeader)
                {
                    AttractionCategoryHeader.handleOnGroupHeaderLongClick(getActivity(), view);
                }
                else
                {
                    viewModel.longClickedElement = (IElement)view.getTag();

                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenu().add(0, Constants.SELECTION_EDIT_CUSTOM_ATTRACTION, Menu.NONE, R.string.selection_edit);
                    popupMenu.getMenu().add(0, Constants.SELECTION_DELETE_ATTRACTION, Menu.NONE, R.string.selection_delete);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            Log.i(LOG_TAG, String.format("ShowAttractionsFragment.onMenuItemClick:: [%S] selected", item.getItemId()));

                            int id = item.getItemId();

                            if(id == Constants.SELECTION_EDIT_CUSTOM_ATTRACTION)
                            {
                                ActivityTool.startActivityEditForResult(
                                        getContext(),
                                        Constants.REQUEST_CODE_EDIT_CUSTOM_ATTRACTION,
                                        viewModel.longClickedElement);
                            }
                            else if(id == Constants.SELECTION_DELETE_ATTRACTION)
                            {
                                AlertDialogFragment alertDialogFragmentDelete =
                                        AlertDialogFragment.newInstance(
                                                R.drawable.ic_baseline_warning,
                                                getString(R.string.alert_dialog_title_delete_attraction),
                                                getString(R.string.alert_dialog_message_delete_attraction, viewModel.longClickedElement.getName()),
                                                getString(R.string.text_accept),
                                                getString(R.string.text_cancel),
                                                Constants.REQUEST_CODE_DELETE,
                                                true);

                                alertDialogFragmentDelete.setCancelable(false);
                                alertDialogFragmentDelete.show(Objects.requireNonNull(getChildFragmentManager()), Constants.FRAGMENT_TAG_ALERT_DIALOG);
                            }

                            return true;
                        }
                    });
                    popupMenu.show();
                }

                return true;
            }
        };
    }

    @Override
    public void onAlertDialogClick(int requestCode, DialogInterface dialog, int which)
    {
        dialog.dismiss();

        Snackbar snackbar;

        if(which == DialogInterface.BUTTON_POSITIVE)
        {
            if(requestCode == Constants.REQUEST_CODE_DELETE)
            {
                snackbar = Snackbar.make(
                        Objects.requireNonNull(getActivity()).findViewById(android.R.id.content),
                        getString(R.string.action_confirm_delete_text, viewModel.longClickedElement.getName()),
                        Snackbar.LENGTH_LONG);

                snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        actionConfirmed = true;
                        Log.i(LOG_TAG, "ShowAttractionsFragment.onSnackbarClick<DELETE>:: action <DELETE> confirmed");
                    }
                });

                snackbar.addCallback(new Snackbar.Callback()
                {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event)
                    {
                        if(actionConfirmed)
                        {
                            actionConfirmed = false;

                            Log.i(LOG_TAG, String.format("ShowAttractionsFragment.onDismissed<DELETE>:: deleting %s...", viewModel.longClickedElement));

                            for(Visit visit : viewModel.longClickedElement.getParent().getChildrenAsType(Visit.class))
                            {
                                for(VisitedAttraction visitedAttraction : visit.getChildrenAsType(VisitedAttraction.class))
                                {
                                    if(visitedAttraction.getOnSiteAttraction().equals(viewModel.longClickedElement))
                                    {
                                        ShowAttractionsFragment.this.showAttractionsFragmentInteraction.deleteElement(visitedAttraction);
                                        visitedAttraction.deleteElementAndDescendants();
                                    }
                                }
                            }

                            ShowAttractionsFragment.this.showAttractionsFragmentInteraction.deleteElement(viewModel.longClickedElement);
                            viewModel.longClickedElement.deleteElementAndDescendants();
                            updateContentRecyclerView();
                        }
                        else
                        {
                            Log.d(LOG_TAG, "ShowAttractionsFragment.onDismissed<DELETE>:: action <DELETE> not confirmed - doing nothing");
                        }
                    }
                });

                snackbar.show();
            }
        }
    }

    private void updateContentRecyclerView()
    {
        Log.i(LOG_TAG, "ShowAttractionsFragment.updateContentRecyclerView:: updating RecyclerView...");
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.park.getChildrenOfType(IOnSiteAttraction.class));
    }

    public interface ShowAttractionsFragmentInteraction
    {
        void updateElement(IElement elementToUpdate);
        void deleteElement(IElement elemtToDelete);
    }
}