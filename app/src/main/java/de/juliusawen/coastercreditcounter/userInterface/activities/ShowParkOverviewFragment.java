package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.ResultFetcher;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuAgents.OptionsMenuAgent;
import de.juliusawen.coastercreditcounter.userInterface.customViews.FrameLayoutWithMaxHeight;

import static de.juliusawen.coastercreditcounter.application.Constants.LOG_TAG;

public class ShowParkOverviewFragment extends Fragment
{
    private ShowParkOverviewFragmentViewModel viewModel;
    private ShowParkOverviewFragmentInteraction showParkOverviewFragmentInteraction;

    private FrameLayout frameLayoutNote;
    private FrameLayoutWithMaxHeight frameLayoutNoteWithMaxHeight;
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
                this.viewModel.park = (Park) App.content.getContentByUuid(UUID.fromString(getArguments().getString(Constants.FRAGMENT_ARG_PARK_UUID)));
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
        this.frameLayoutNote = view.findViewById(R.id.frameLayoutShowParkOvierview_Note);
        View layoutNote = getLayoutInflater().inflate(R.layout.layout_note, frameLayoutNote);
        ((FrameLayoutWithMaxHeight)layoutNote.findViewById(R.id.frameLayoutWithMaxHeightNote)).setMaxHeight(ConvertTool.convertDpToPx(App.config.maxHeightForNoteInDP));
        this.textViewNote = layoutNote.findViewById(R.id.textViewNote);
        if(this.viewModel.park.hasChildrenOfType(Note.class))
        {
            this.decorateTextViewNote(this.viewModel.park.getChildrenAsType(Note.class).get(0).getText());
        }
        else
        {
            this.frameLayoutNote.setVisibility(View.GONE);
        }
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
                    this.viewModel.park.addChildAndSetParent(resultElement);
                    this.showParkOverviewFragmentInteraction.markForUpdate(this.viewModel.park);

                    this.decorateTextViewNote(((Note)resultElement).getText());
                    break;
                }

                case EDIT_NOTE:
                {
                    this.showParkOverviewFragmentInteraction.markForUpdate(this.viewModel.park);
                    this.decorateTextViewNote(((Note)resultElement).getText());
                    break;
                }
            }
        }
    }

    private void decorateTextViewNote(String text)
    {
        this.textViewNote.setText(text);
        this.frameLayoutNote.setVisibility(View.VISIBLE);
    }

    public interface ShowParkOverviewFragmentInteraction
    {
        void markForUpdate(IElement elementToUpdate);
    }
}