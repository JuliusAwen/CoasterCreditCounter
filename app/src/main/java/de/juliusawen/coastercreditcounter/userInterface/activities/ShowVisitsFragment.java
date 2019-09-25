package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.HashSet;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.SpecialGroupHeader;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
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
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ShowVisitsFragment extends Fragment implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowVisitsFragmentViewModel viewModel;
    private ShowVisitsFragmentInteraction showVisitsFragmentInteraction;
    private RecyclerView recyclerView;

    boolean optionsMenuIsInvalid = false;

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

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

        if(this.viewModel.contentRecyclerViewAdapter == null)
        {
            this.viewModel.contentRecyclerViewAdapter = this.createContentRecyclerAdapter();
            this.viewModel.contentRecyclerViewAdapter.setTypefaceForContentType(SpecialGroupHeader.class, Typeface.BOLD);
        }
        this.viewModel.contentRecyclerViewAdapter.setOnClickListener(this.getContentRecyclerViewOnClickListener());

        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_visits, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", requestCode, resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == RequestCode.CREATE_VISIT.ordinal())
            {
                this.updateContentRecyclerView();

                IElement visit = ResultFetcher.fetchResultElement(data);
                ActivityDistributor.startActivityShow(getActivity(), RequestCode.SHOW_VISIT, visit);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        this.viewModel.optionsMenuAgent
                .add(OptionsItem.SORT)
                    .addToGroup(OptionsItem.SORT_ASCENDING, OptionsItem.SORT)
                    .addToGroup(OptionsItem.SORT_DESCENDING, OptionsItem.SORT)
                .add(OptionsItem.EXPAND_ALL)
                .add(OptionsItem.COLLAPSE_ALL)
                .create(menu);

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        this.viewModel.optionsMenuAgent
                .setEnabled(OptionsItem.SORT, this.viewModel.park.getChildCountOfType(Visit.class) > 1)
                .setEnabled(OptionsItem.EXPAND_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllExpanded())
                .setEnabled(OptionsItem.COLLAPSE_ALL, !this.viewModel.contentRecyclerViewAdapter.isAllCollapsed())
                .prepare(menu);

        super.onPrepareOptionsMenu(menu);
    }

    public boolean sortAscending()
    {
        Visit.setSortOrder(SortOrder.ASCENDING);
        this.updateContentRecyclerView();
        return true;
    }

    public boolean sortDecending()
    {
        Visit.setSortOrder(SortOrder.DESCENDING);
        this.updateContentRecyclerView();
        return true;
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

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof ShowVisitsFragmentInteraction)
        {
            this.showVisitsFragmentInteraction = (ShowVisitsFragmentInteraction) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement ShowVisitsFragmentInteraction");
        }
    }

    private ContentRecyclerViewAdapter createContentRecyclerAdapter()
    {
        HashSet<Class<? extends IElement>> childTypesToExpand = new HashSet<>();
        childTypesToExpand.add(Visit.class);

        return ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(
                this.viewModel.park.fetchChildrenOfType(Visit.class),
                childTypesToExpand)
                .groupItems(GroupType.YEAR);
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element) view.getTag();
                if(element.isVisit())
                {
                    ActivityDistributor.startActivityShow(getActivity(), RequestCode.SHOW_VISIT, element);
                }
                else if(element.isGroupHeader())
                {
                    viewModel.contentRecyclerViewAdapter.toggleExpansion(element);
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                viewModel.longClickedElement = (IElement) view.getTag();

                if(viewModel.longClickedElement.isVisit())
                {

                    PopupMenuAgent.getMenu()
                            .add(PopupItem.EDIT_ELEMENT)
                            .add(PopupItem.DELETE_ELEMENT)
                            .show(getContext(), view);

                    return true;
                }
                else
                {
                    return false;
                }
            }
        };
    }

    public void handlePopupItemDeleteElementClicked()
    {
        AlertDialogFragment alertDialogFragmentDelete =
                AlertDialogFragment.newInstance(
                        R.drawable.ic_baseline_warning,
                        getString(R.string.alert_dialog_title_delete_element),
                        getString(R.string.alert_dialog_message_confirm_delete, viewModel.longClickedElement.getName()),
                        getString(R.string.text_accept),
                        getString(R.string.text_cancel),
                        RequestCode.DELETE,
                        true);

        alertDialogFragmentDelete.setCancelable(false);
        alertDialogFragmentDelete.show(getChildFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    public void handlePopupItemEditElementClicked()
    {
        pickDate();
    }

    private void pickDate()
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.pickDate:: picking date for visit in %s", this.viewModel.park));

        this.viewModel.calendar = (Calendar)((Visit)this.viewModel.longClickedElement).getCalendar().clone();
        int year = this.viewModel.calendar.get(Calendar.YEAR);
        int month = this.viewModel.calendar.get(Calendar.MONTH);
        int day = this.viewModel.calendar.get(Calendar.DAY_OF_MONTH);

        this.viewModel.datePickerDialog = new DatePickerDialog(ShowVisitsFragment.this.getContext(), new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                Log.v(Constants.LOG_TAG, String.format("ShowVisitsFragment.onDateSet:: picked date: year[%d], month[%d], day[%d]", year, month, day));
                viewModel.calendar.set(year, month, day);

                if(!(Visit.isSameDay(((Visit)viewModel.longClickedElement).getCalendar(), viewModel.calendar)))
                {
                    if(!Visit.fetchVisitsForYearAndDay(viewModel.calendar, viewModel.park.fetchChildrenAsType(Visit.class)).isEmpty())
                    {
                        viewModel.datePickerDialog.dismiss();
                        Toaster.makeLongToast(getContext(), getString(R.string.error_visit_already_exists));
                    }
                    else
                    {
                        ((Visit)viewModel.longClickedElement).setDateAndAdjustName(viewModel.calendar);
                        ShowVisitsFragment.this.showVisitsFragmentInteraction.markForUpdate(viewModel.longClickedElement);
                        updateContentRecyclerView();
                    }
                }
                else
                {
                    Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onDateSet:: same date picked - doing nothing");
                }

                viewModel.datePickerDialog.dismiss();
            }
        }, year, month, day);

        this.viewModel.datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.text_cancel), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int position)
            {
                viewModel.datePickerDialog.dismiss();
            }
        });

        this.viewModel.datePickerDialog.getDatePicker().setFirstDayOfWeek(App.settings.getFirstDayOfTheWeek());
        this.viewModel.datePickerDialog.setCancelable(false);
        this.viewModel.datePickerDialog.setCanceledOnTouchOutside(false);
        this.viewModel.datePickerDialog.show();
    }

    @Override
    public void handleAlertDialogClick(RequestCode requestCode, int which)
    {
        if(which == DialogInterface.BUTTON_POSITIVE && requestCode == RequestCode.DELETE)
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

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.handleActionConfirmed:: deleting %s...", this.viewModel.longClickedElement));

            ShowVisitsFragment.this.showVisitsFragmentInteraction.markForDeletion(viewModel.longClickedElement);
            this.viewModel.longClickedElement.deleteElementAndDescendants();
            updateContentRecyclerView();
        }
    }

    private void updateContentRecyclerView()
    {
        Log.i(Constants.LOG_TAG, "ShowVisitsFragment.updateContentRecyclerView:: updating RecyclerView...");
        this.viewModel.contentRecyclerViewAdapter.setItems(this.viewModel.park.fetchChildrenOfType(Visit.class));
    }

    public interface ShowVisitsFragmentInteraction
    {
        void markForDeletion(IElement elementToDelete);
        void markForUpdate(IElement elementToDelete);
    }
}