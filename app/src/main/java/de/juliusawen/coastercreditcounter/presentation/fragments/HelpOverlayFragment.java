package de.juliusawen.coastercreditcounter.presentation.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;

public class HelpOverlayFragment extends Fragment
{
    private String helpTitle;
    private CharSequence helpMessage;

    private TextView textViewHelpTitle;
    private TextView textViewHelpMessage;

    private HelpOverlayFragmentInteractionListener helpOverlayFragmentInteractionListener;

    public HelpOverlayFragment() {}

    public static HelpOverlayFragment newInstance(String helpTitle, CharSequence helpMessage)
    {
        Log.i(Constants.LOG_TAG, "HelpOverlayFragment.newInstance:: creating instance...");

        HelpOverlayFragment fragment = new HelpOverlayFragment();
        Bundle args = new Bundle();
        args.putCharSequence(Constants.FRAGMENT_ARG_HELP_TITLE, helpTitle);
        args.putCharSequence(Constants.FRAGMENT_ARG_HELP_MESSAGE, helpMessage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (getArguments() != null)
        {
            this.helpTitle = getArguments().getString(Constants.FRAGMENT_ARG_HELP_TITLE);
            if(this.helpTitle == null)
            {
                this.helpTitle = getString(R.string.title_help);
            }

            this.helpMessage = getArguments().getCharSequence(Constants.FRAGMENT_ARG_HELP_MESSAGE);
        }

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_help_overlay, container, false);
        this.textViewHelpTitle = linearLayout.findViewById(R.id.textViewHelp_Title);
        this.textViewHelpMessage = linearLayout.findViewById(R.id.textViewHelp_Message);
        return linearLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null)
        {
            this.helpTitle = savedInstanceState.getString(Constants.KEY_HELP_TITLE);
            this.helpMessage = savedInstanceState.getCharSequence(Constants.KEY_HELP_MESSAGE);
        }

        this.setHelpTitle(this.helpTitle);
        this.setHelpMessage(this.helpMessage);

        ImageButton buttonBack = view.findViewById(R.id.imageButtonHelp_Close);
        Drawable drawable = DrawableTool.setTintToWhite(Objects.requireNonNull(getContext()), getContext().getDrawable(R.drawable.ic_baseline_close));
        buttonBack.setImageDrawable(drawable);
        buttonBack.setId(ButtonFunction.CLOSE.ordinal());
        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                HelpOverlayFragment.this.onCloseButtonPressed(view);
            }
        });
    }

    private void onCloseButtonPressed(View view)
    {
        if (this.helpOverlayFragmentInteractionListener != null)
        {
            this.helpOverlayFragmentInteractionListener.onHelpOverlayFragmentInteraction(view);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putString(Constants.KEY_HELP_TITLE, this.helpTitle);
        outState.putCharSequence(Constants.KEY_HELP_MESSAGE, this.helpMessage);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof HelpOverlayFragmentInteractionListener)
        {
            this.helpOverlayFragmentInteractionListener = (HelpOverlayFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement HelpOverlayFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.helpTitle = null;
        this.helpMessage = null;
        this.textViewHelpTitle = null;
        this.textViewHelpMessage = null;
        this.helpOverlayFragmentInteractionListener = null;
    }

    public interface HelpOverlayFragmentInteractionListener
    {
        void onHelpOverlayFragmentInteraction(View view);
    }

    public void setHelpTitle(String helpTitle)
    {
        this.helpTitle = helpTitle;
        this.textViewHelpTitle.setText(helpTitle);

        Log.d(Constants.LOG_TAG, String.format("HelpOverlayFragment.setHelpTitle:: helpTitle set to [%s]", helpTitle));
    }

    public void setHelpMessage(CharSequence helpMessage)
    {
        this.helpMessage = helpMessage;
        this.textViewHelpMessage.setText(helpMessage);

        Log.d(Constants.LOG_TAG, "HelpOverlayFragment.setHelpMessage:: message set");
    }
}
