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
import android.widget.TextView;

import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.toolbox.Constants;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;
import de.juliusawen.coastercreditcounter.toolbox.enums.ButtonFunction;

public class HelpOverlayFragment extends Fragment
{
    public View fragmentView;
    private Boolean isVisibleOnCreation;

    private CharSequence helpText;
    private HelpOverlayFragmentInteractionListener helpOverlayFragmentInteractionListener;


    public HelpOverlayFragment() {}

    public static HelpOverlayFragment newInstance(CharSequence helpText, boolean isVisible)
    {
        Log.i(Constants.LOG_TAG, "HelpOverlayFragment.newInstance:: creating instance...");

        HelpOverlayFragment fragment = new HelpOverlayFragment();
        Bundle args = new Bundle();
        args.putCharSequence(Constants.FRAGMENT_ARG_1, helpText);
        args.putBoolean(Constants.FRAGMENT_ARG_2, isVisible);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            this.helpText = getArguments().getCharSequence(Constants.FRAGMENT_ARG_1);
            this.isVisibleOnCreation = getArguments().getBoolean(Constants.FRAGMENT_ARG_2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.fragmentView = inflater.inflate(R.layout.fragment_help_overlay, container, false);
        return this.fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        TextView textViewHelpTitle = view.findViewById(R.id.textViewHelp_Title);
        textViewHelpTitle.setText(R.string.title_help);

        if(savedInstanceState != null)
        {
            this.helpText = savedInstanceState.getCharSequence(Constants.KEY_HELP_TEXT);
        }

        this.setHelpText(this.helpText);

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

        this.setVisibility(this.isVisibleOnCreation);
    }

    public void onCloseButtonPressed(View view)
    {
        if (this.helpOverlayFragmentInteractionListener != null)
        {
            this.helpOverlayFragmentInteractionListener.onHelpOverlayFragmentInteraction(view);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(Constants.KEY_HELP_TEXT, this.helpText);
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

        this.helpOverlayFragmentInteractionListener = null;
        this.fragmentView = null;
        this.helpText = null;
        this.isVisibleOnCreation = null;
    }

    public interface HelpOverlayFragmentInteractionListener
    {
        void onHelpOverlayFragmentInteraction(View view);
    }

    public void setHelpText(CharSequence helpText)
    {
        this.helpText = helpText;
        TextView textViewHelpMessage = this.fragmentView.findViewById(R.id.textViewHelp_Message);
        textViewHelpMessage.setText(this.helpText);

        Log.d(Constants.LOG_TAG, "HelpOverlayFragment.setHelpText:: HelpText set");
    }

    public void setVisibility(Boolean isVisible)
    {
        this.fragmentView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        Log.d(Constants.LOG_TAG, String.format("HelpOverlayFragment.setVisibility:: isVisible[%S]", this.isVisible()));
    }
}
