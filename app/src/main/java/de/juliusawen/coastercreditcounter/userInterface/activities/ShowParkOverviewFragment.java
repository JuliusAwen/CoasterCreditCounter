package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.ConfirmSnackbar;
import de.juliusawen.coastercreditcounter.tools.confirmSnackbar.IConfirmSnackbarClient;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupItem;
import de.juliusawen.coastercreditcounter.tools.menuAgents.PopupMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.customViews.FrameLayoutWithMaxHeight;
import de.juliusawen.coastercreditcounter.userInterface.toolFragments.AlertDialogFragment;

import static de.juliusawen.coastercreditcounter.application.Constants.LOG_TAG;

public class ShowParkOverviewFragment extends Fragment implements AlertDialogFragment.AlertDialogListener, IConfirmSnackbarClient
{
    private ShowParkOverviewFragmentViewModel viewModel;
    private ShowParkOverviewFragmentInteraction showParkOverviewFragmentInteraction;

    private FrameLayoutWithMaxHeight frameLayoutNote;
    private TextView textViewNote;

    public static ShowParkOverviewFragment newInstance(String parkUuid)
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "ShowParkOverviewFragment.newInstance:: instantiating fragment...");

        ShowParkOverviewFragment showParkOverviewFragment = new ShowParkOverviewFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_ARG_PARK_UUID, parkUuid);
        showParkOverviewFragment.setArguments(args);

        return showParkOverviewFragment;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof ShowParkOverviewFragment.ShowParkOverviewFragmentInteraction)
        {
            this.showParkOverviewFragmentInteraction = (ShowParkOverviewFragment.ShowParkOverviewFragmentInteraction) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement ShowParkOverviewFragmentInteraction");
        }
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(ShowParkOverviewFragmentViewModel.class);

        if(this.viewModel.park == null)
        {
            if(getArguments() != null)
            {
                this.viewModel.park = (Park)App.content.getContentByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
                this.viewModel.note = this.viewModel.park.getNote();
            }
        }

        if(this.viewModel.optionsMenuAgent == null)
        {
            this.viewModel.optionsMenuAgent = new OptionsMenuAgent();
        }

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
        this.decorateNote(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(LOG_TAG, String.format("ShowParkOvierviewFragment.onActivityResult:: requestCode[%s], resultCode[%s]", RequestCode.getValue(requestCode), resultCode));

        if(resultCode == Activity.RESULT_OK)
        {
            IElement resultElement = ResultFetcher.fetchResultElement(data);

            switch(RequestCode.values()[requestCode])
            {
                case CREATE_NOTE:
                {
                    this.viewModel.note = (Note)resultElement;
                    this.viewModel.park.addChildAndSetParent(this.viewModel.note);
                    this.showParkOverviewFragmentInteraction.markForUpdate(this.viewModel.park);
                    this.textViewNote.setText(this.viewModel.note.getText());
                    this.frameLayoutNote.setVisibility(View.VISIBLE);
                    this.showParkOverviewFragmentInteraction.setFloatingActionButtonVisibility(false);
                    break;
                }

                case EDIT_NOTE:
                {
                    this.viewModel.note = (Note)resultElement;
                    this.showParkOverviewFragmentInteraction.markForUpdate(this.viewModel.park);
                    this.textViewNote.setText(this.viewModel.note.getText());
                    this.frameLayoutNote.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    private void decorateNote(View view)
    {
        this.frameLayoutNote = view.findViewById(R.id.frameLayoutWithMaxHeightNote);
        this.frameLayoutNote.setMaxHeight(ConvertTool.convertDpToPx(App.config.maxHeightForNoteInDP));

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

        if(this.viewModel.note != null)
        {
            this.textViewNote.setText(this.viewModel.note.getText());
            this.frameLayoutNote.setVisibility(View.VISIBLE);
            this.showParkOverviewFragmentInteraction.setFloatingActionButtonVisibility(false);
        }
        else
        {
            this.showParkOverviewFragmentInteraction.setFloatingActionButtonVisibility(true);
            this.frameLayoutNote.setVisibility(View.GONE);
        }
    }

    private void onNoteClick()
    {
        ActivityDistributor.startActivityEditForResult(getContext(), RequestCode.EDIT_NOTE, this.viewModel.park.getNote());
    }

    private boolean onNoteLongClick()
    {
        PopupMenuAgent.getMenu()
                .add(PopupItem.DELETE_ELEMENT)
                .show(getContext(), this.frameLayoutNote);

        return true;
    }

    public void handlePopupItemDeleteElementClicked()
    {
        AlertDialogFragment alertDialogFragmentDelete =
                AlertDialogFragment.newInstance(
                        R.drawable.ic_baseline_warning,
                        getString(R.string.alert_dialog_title_delete),
                        getString(R.string.alert_dialog_message_confirm_delete, this.viewModel.note.getName()),
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
                this.showParkOverviewFragmentInteraction.setFloatingActionButtonVisibility(false);

                ConfirmSnackbar.Show(
                        Snackbar.make(
                                getActivity().findViewById(android.R.id.content),
                                getString(R.string.action_confirm_delete_text, this.viewModel.note.getName()),
                                Snackbar.LENGTH_LONG),
                        requestCode,
                        this);
            }
        }
    }

    @Override
    public void handleActionConfirmed(RequestCode requestCode)
    {
        Log.i(Constants.LOG_TAG, String.format("ShowParkOverviewFragment.handleActionConfirmed:: handling confirmed action [%s]", requestCode));

        if(requestCode == RequestCode.DELETE)
        {
            Log.i(Constants.LOG_TAG, String.format("ShowParkOverviewFragment.handleActionConfirmed:: deleting %s...", this.viewModel.note));

            this.textViewNote.setText("");
            this.frameLayoutNote.setVisibility(View.GONE);
            this.showParkOverviewFragmentInteraction.setFloatingActionButtonVisibility(true);

            this.viewModel.note.delete();
            ShowParkOverviewFragment.this.showParkOverviewFragmentInteraction.markForDeletion(this.viewModel.note);
            ShowParkOverviewFragment.this.showParkOverviewFragmentInteraction.markForUpdate(this.viewModel.park);
            this.viewModel.note = null;
        }
    }

    public interface ShowParkOverviewFragmentInteraction
    {
        void setFloatingActionButtonVisibility(boolean isVisible);

        void markForUpdate(IElement elementToUpdate);
        void markForDeletion(IElement elementToUpdate);
    }
}