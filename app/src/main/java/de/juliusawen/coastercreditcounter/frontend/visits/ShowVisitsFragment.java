package de.juliusawen.coastercreditcounter.frontend.visits;

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

import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.backend.GroupHeader.YearHeader;
import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Element;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.frontend.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.frontend.fragments.AlertDialogFragment;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class ShowVisitsFragment extends Fragment implements AlertDialogFragment.AlertDialogListener
{
    private ShowVisitsFragmentViewModel viewModel;
    private RecyclerView recyclerView;
    private boolean actionConfirmed;
    private ShowVisitsFragmentAlertDialogInteraction showVisitsFragmentAlertDialogInteraction;

    public ShowVisitsFragment() {}

    public static ShowVisitsFragment newInstance(String parkUuid)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowVisitsFragment.newInstance:: instantiating fragment...");

        ShowVisitsFragment showVisitsFragment = new ShowVisitsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuid);
        showVisitsFragment.setArguments(args);

        return showVisitsFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onCreate:: creating fragment...");
        super.onCreate(savedInstanceState);

        this.viewModel = ViewModelProviders.of(this).get(ShowVisitsFragmentViewModel.class);

        if(this.viewModel.park == null)
        {
            if (getArguments() != null)
            {
                this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
            }
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerAdapter();
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForType(YearHeader.class, Typeface.BOLD);
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        this.recyclerView = view.findViewById(R.id.recyclerViewShowVisits);
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.onOptionItemSelected:: [%s] selected", item.getItemId()));

        int id = item.getItemId();
        if(id == Constants.SELECTION_ASCENDING)
        {
            Visit.setSortOrder(SortOrder.ASCENDING);
            this.updateContentRecyclerView();
            return true;
        }
        else if(id == Constants.SELECTION_DESCENDING)
        {
            Visit.setSortOrder(SortOrder.DESCENDING);
            this.updateContentRecyclerView();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == Constants.REQUEST_CODE_CREATE_VISIT)
            {
                this.updateContentRecyclerView();

                IElement visit = App.content.getContentByUuid(UUID.fromString(data.getStringExtra(Constants.EXTRA_ELEMENT_UUID)));
                ActivityTool.startActivityShow(getActivity(), Constants.REQUEST_CODE_SHOW_VISIT, visit);
            }
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof ShowVisitsFragment.ShowVisitsFragmentAlertDialogInteraction)
        {
            this.showVisitsFragmentAlertDialogInteraction = (ShowVisitsFragment.ShowVisitsFragmentAlertDialogInteraction) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement ShowVisitsFragmentAlertDialogInteraction");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.viewModel = null;
        this.recyclerView = null;
        this.showVisitsFragmentAlertDialogInteraction = null;
    }

    private ContentRecyclerViewAdapter createContentRecyclerAdapter()
    {
        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
        childTypesToExpand.add(Visit.class);

        return ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                this.viewModel.park.getChildrenOfType(Visit.class),
                childTypesToExpand,
                Constants.TYPE_YEAR);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();
                if(element instanceof Visit)
                {
                    ActivityTool.startActivityShow(getActivity(), Constants.REQUEST_CODE_SHOW_VISIT, element);
                }
                else if(element instanceof YearHeader)
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (Element) view.getTag();

                PopupMenu popupMenu = getRecyclerViewItemPopupMenu(view);
                popupMenu.setOnMenuItemClickListener(getOnMenuItemClickListener());
                popupMenu.show();
                return false;
            }
        };
    }

    private PopupMenu getRecyclerViewItemPopupMenu(View view)
    {
        PopupMenu popupMenu = new PopupMenu(ShowVisitsFragment.this.getContext(), view);
        this.populatePopupMenu(popupMenu.getMenu());
        return popupMenu;
    }

    private void populatePopupMenu(Menu menu)
    {
        menu.add(Menu.NONE, Constants.SELECTION_EDIT_ELEMENT, Menu.NONE, R.string.selection_edit);
        menu.add(Menu.NONE, Constants.SELECTION_DELETE_ELEMENT, Menu.NONE, R.string.selection_delete);
    }

    private PopupMenu.OnMenuItemClickListener getOnMenuItemClickListener()
    {
        return new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.onPopupMenuItemLongClick:: [%S] selected", item.getItemId()));

                int id = item.getItemId();
                if(id == Constants.SELECTION_DELETE_ELEMENT)
                {
                    AlertDialogFragment alertDialogFragmentDelete =
                            AlertDialogFragment.newInstance(
                                    R.drawable.ic_baseline_warning,
                                    getString(R.string.alert_dialog_title_delete_element),
                                    getString(R.string.alert_dialog_message_delete_element, viewModel.longClickedElement.getName()),
                                    getString(R.string.text_accept),
                                    getString(R.string.text_cancel),
                                    Constants.REQUEST_CODE_DELETE,
                                    true);

                    alertDialogFragmentDelete.setCancelable(false);
                    alertDialogFragmentDelete.show(Objects.requireNonNull(getChildFragmentManager()), Constants.FRAGMENT_TAG_ALERT_DIALOG);

                    return true;
                }
                else if(id == Constants.SELECTION_EDIT_ELEMENT)
                {


                    return true;
                }
                else
                {
                    return false;
                }
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
                        Log.i(Constants.LOG_TAG, "ShowLocationsActivity.onSnackbarClick<DELETE>:: action <DELETE> confirmed");
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

                            Log.i(Constants.LOG_TAG, String.format("ShowLocationsActivity.onDismissed<DELETE>:: deleting %s...", viewModel.longClickedElement));

                            if(ShowVisitsFragment.this.showVisitsFragmentAlertDialogInteraction != null)
                            {
                               ShowVisitsFragment.this.showVisitsFragmentAlertDialogInteraction.onShowVisitsFragmentAlertDialogInteraction(
                                       viewModel.longClickedElement, true,
                                       viewModel.longClickedElement.getParent()
                               );
                            }

                            viewModel.longClickedElement.deleteElement();
                            if(viewModel.longClickedElement instanceof Park)
                            {
                                for(IOnSiteAttraction onSiteAttraction : viewModel.longClickedElement.getChildrenAsType(IOnSiteAttraction.class))
                                {
                                    onSiteAttraction.deleteElement();
                                }
                            }

                            updateContentRecyclerView();
                        }
                        else
                        {
                            Log.d(Constants.LOG_TAG, "ShowLocationsActivity.onDismissed<DELETE>:: action <DELETE> not confirmed - doing nothing");
                        }
                    }
                });

                snackbar.show();
            }
        }
    }

    private void updateContentRecyclerView()
    {
        Log.i(Constants.LOG_TAG, "ShowVisitsFragment.updateContentRecyclerView:: updating RecyclerView...");
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.park.getChildrenOfType(Visit.class));
    }

    public interface ShowVisitsFragmentAlertDialogInteraction
    {
        void onShowVisitsFragmentAlertDialogInteraction(IElement elementToDelete, boolean deleteDescendants, IElement elementToUpdate);
    }
}