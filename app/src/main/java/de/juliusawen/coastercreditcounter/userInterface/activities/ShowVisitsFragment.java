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
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.Element;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.groupHeader.SpecialGroupHeader;
import de.juliusawen.coastercreditcounter.enums.SortOrder;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.Toaster;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuTools.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapterProvider;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.GroupType;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.RecyclerOnClickListener;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ShowVisitsFragment extends Fragment implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowParkSharedViewModel viewModel;
    private ContentRecyclerViewAdapter contentRecyclerViewAdapter;
    private ShowVisitsFragmentInteraction fragmentInteraction;

    public static ShowVisitsFragment newInstance()
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowVisitsFragment.newInstance:: instantiating fragment...");
        return new ShowVisitsFragment();
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ShowVisitsFragment.onCreate:: creating fragment...");
        super.onCreate(savedInstanceState);

        this.viewModel = new ViewModelProvider(getActivity()).get(ShowParkSharedViewModel.class);

        this.contentRecyclerViewAdapter = this.createContentRecyclerAdapter();

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
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewShowVisits);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(this.contentRecyclerViewAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.viewModel.requestCode = RequestCode.SHOW_VISITS;
        this.viewModel.contentRecyclerViewAdapter = this.contentRecyclerViewAdapter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == RequestCode.CREATE_VISIT.ordinal())
            {
                this.updateContentRecyclerView();

                IElement resultElement = ResultFetcher.fetchResultElement(data);
                ActivityDistributor.startActivityShow(getActivity(), RequestCode.SHOW_VISIT, resultElement);
            }
        }
    }

    public void sortAscending()
    {
        Visit.setSortOrder(SortOrder.ASCENDING);
        this.updateContentRecyclerView();
    }

    public void sortDecending()
    {
        Visit.setSortOrder(SortOrder.DESCENDING);
        this.updateContentRecyclerView();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof ShowVisitsFragmentInteraction)
        {
            this.fragmentInteraction = (ShowVisitsFragmentInteraction) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement ShowVisitsFragmentInteraction");
        }
    }

    private ContentRecyclerViewAdapter createContentRecyclerAdapter()
    {
        return ContentRecyclerViewAdapterProvider.getExpandableContentRecyclerViewAdapter(this.viewModel.park.getChildrenOfType(Visit.class), Visit.class)
                .setTypefaceForContentType(SpecialGroupHeader.class, Typeface.BOLD)
                .groupItems(GroupType.YEAR)
                .setOnClickListener(this.getContentRecyclerViewOnClickListener());
    }

    private RecyclerOnClickListener.OnClickListener getContentRecyclerViewOnClickListener()
    {
        return new RecyclerOnClickListener.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Element element = (Element)view.getTag();
                if(element.isVisit())
                {
                    ActivityDistributor.startActivityShow(getActivity(), RequestCode.SHOW_VISIT, element);
                }
                else if(element.isGroupHeader())
                {
                    contentRecyclerViewAdapter.toggleExpansion(element);
                    if(contentRecyclerViewAdapter.isAllExpanded() || contentRecyclerViewAdapter.isAllCollapsed())
                    {
                        getActivity().invalidateOptionsMenu();
                    }
                }
            }

            @Override
            public boolean onLongClick(final View view)
            {
                IElement longClickedElement = (IElement)view.getTag();
                if(longClickedElement.isVisit())
                {
                    viewModel.longClickedElement = longClickedElement;

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
                        getString(R.string.alert_dialog_title_delete),
                        getString(R.string.alert_dialog_message_confirm_delete_with_children, viewModel.longClickedElement.getName()),
                        getString(R.string.text_accept),
                        getString(R.string.text_cancel),
                        RequestCode.DELETE,
                        true);

        alertDialogFragmentDelete.setCancelable(false);
        alertDialogFragmentDelete.show(getChildFragmentManager(), Constants.FRAGMENT_TAG_ALERT_DIALOG);
    }

    public void handlePopupItemEditElementClicked()
    {
        this.pickDate();
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
                    if(!Visit.fetchVisitsForYearAndDay(viewModel.calendar, viewModel.park.getChildrenAsType(Visit.class)).isEmpty())
                    {
                        viewModel.datePickerDialog.dismiss();
                        Toaster.makeLongToast(getContext(), getString(R.string.error_visit_already_exists));
                    }
                    else
                    {
                        ((Visit)viewModel.longClickedElement).setDateAndAdjustName(viewModel.calendar);
                        ShowVisitsFragment.this.fragmentInteraction.markForUpdate(viewModel.longClickedElement);
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

        this.viewModel.datePickerDialog.getDatePicker().setFirstDayOfWeek(App.preferences.getFirstDayOfTheWeek());
        this.viewModel.datePickerDialog.setCancelable(false);
        this.viewModel.datePickerDialog.setCanceledOnTouchOutside(false);
        this.viewModel.datePickerDialog.show();
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
        Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        this.fragmentInteraction.setFloatingActionButtonVisibility(true);

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(Constants.LOG_TAG, String.format("ShowVisitsFragment.handleActionConfirmed:: deleting %s...", this.viewModel.longClickedElement));

            ShowVisitsFragment.this.fragmentInteraction.markForDeletion(this.viewModel.longClickedElement, true);
            updateContentRecyclerView();
        }
    }

    private void updateContentRecyclerView()
    {
        Log.i(Constants.LOG_TAG, "ShowVisitsFragment.updateContentRecyclerView:: updating RecyclerView...");
        this.contentRecyclerViewAdapter.setItems(this.viewModel.park.getChildrenOfType(Visit.class));
    }

    public interface ShowVisitsFragmentInteraction
    {
        void setFloatingActionButtonVisibility(boolean isVisible);
        void markForDeletion(IElement elementToDelete, boolean deleteDescendants);
        void markForUpdate(IElement elementToDelete);
    }
}