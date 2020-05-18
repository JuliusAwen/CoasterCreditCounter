package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
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
import de.juliusawen.coastercreditcounter.userInterface.customViews.FrameLayoutWithMaxHeight;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

public class ShowParkOverviewFragment extends Fragment implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowParkSharedViewModel viewModel;
    private ShowParkOverviewFragmentInteraction fragmentInteraction;

    private FrameLayoutWithMaxHeight layoutNote;
    private TextView textViewNote;

    public static ShowParkOverviewFragment newInstance()
    {
        return new ShowParkOverviewFragment();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof ShowParkOverviewFragment.ShowParkOverviewFragmentInteraction)
        {
            this.fragmentInteraction = (ShowParkOverviewFragment.ShowParkOverviewFragmentInteraction) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement ShowParkOverviewFragmentInteraction");
        }
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.frame(LogLevel.INFO, "creating...", '#', true);
        
        super.onCreate(savedInstanceState);
        this.viewModel = new ViewModelProvider(getActivity()).get(ShowParkSharedViewModel.class);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_park_overview, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        this.createNoteLayout(view);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.viewModel.requestCode = RequestCode.SHOW_PARK_OVERVIEW;
        this.viewModel.oldContentRecyclerViewAdapter = null;
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
                case CREATE_NOTE:
                {
                    this.viewModel.park.addChildAndSetParent(resultElement);
                    this.fragmentInteraction.markForUpdate(this.viewModel.park);
                    break;
                }

                case EDIT_NOTE:
                {
                    this.fragmentInteraction.markForUpdate(this.viewModel.park);
                    break;
                }
            }

            this.handleNoteRelatedViews();
        }
    }

    private void createNoteLayout(View view)
    {
        this.layoutNote = view.findViewById(R.id.frameLayoutWithMaxHeightNote);
        this.layoutNote.setMaxHeight(ConvertTool.convertDpToPx(App.config.maxHeightForNoteInDP));

        this.textViewNote = view.findViewById(R.id.textViewNote);
        this.textViewNote.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onNoteClick();
            }
        });

        this.textViewNote.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                return onNoteLongClick();
            }
        });

        this.handleNoteRelatedViews();
    }

    private void onNoteClick()
    {
        ActivityDistributor.startActivityEditForResult(getContext(), RequestCode.EDIT_NOTE, this.viewModel.park.getNote());
    }

    private boolean onNoteLongClick()
    {
        PopupMenuAgent.getMenu()
                .add(PopupItem.DELETE_ELEMENT)
                .show(getContext(), this.layoutNote);

        return true;
    }

    public void handlePopupItemDeleteElementClicked()
    {
        AlertDialogFragment alertDialogFragmentDelete =
                AlertDialogFragment.newInstance(
                        R.drawable.warning,
                        getString(R.string.alert_dialog_title_delete),
                        getString(R.string.alert_dialog_message_confirm_delete, this.viewModel.park.getNote().getName()),
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
                                getString(R.string.action_confirm_delete_text, this.viewModel.park.getNote().getName()),
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

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(String.format("deleting %s...", this.viewModel.park.getNote()));

            ShowParkOverviewFragment.this.fragmentInteraction.markForUpdate(this.viewModel.park);
            ShowParkOverviewFragment.this.fragmentInteraction.markForDeletion(this.viewModel.park.getNote(), false);

            this.handleNoteRelatedViews();
        }
    }

    private void handleNoteRelatedViews()
    {
        if(this.viewModel.park.getNote() != null)
        {
            this.textViewNote.setText(this.viewModel.park.getNote().getText());
            this.fragmentInteraction.setFloatingActionButtonVisibility(false);
            this.layoutNote.setVisibility(View.VISIBLE);
        }
        else
        {
            this.textViewNote.setText("");
            this.fragmentInteraction.setFloatingActionButtonVisibility(true);
            this.layoutNote.setVisibility(View.GONE);
        }
    }

    public interface ShowParkOverviewFragmentInteraction
    {
        void setFloatingActionButtonVisibility(boolean isVisible);

        void markForUpdate(IElement elementToUpdate);
        void markForDeletion(IElement elementToDelete, boolean deleteDescendants);
    }
}