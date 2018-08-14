package de.juliusawen.coastercreditcounter.presentation.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Objects;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.Toolbox.DrawableTool;

public class HelpOverlayFragment extends Fragment
{
    public View fragmentView;

    private CharSequence helpText;
    private Boolean isVisibleOnCreation;

    private OnFragmentInteractionListener fragmentInteractionListener;


    public HelpOverlayFragment() {}

    public static HelpOverlayFragment newInstance(CharSequence helpText, boolean isVisible)
    {
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

        TextView textView = view.findViewById(R.id.textViewHelp);
        textView.setText(this.helpText);

        ImageButton buttonBack = view.findViewById(R.id.imageButton_help);
        Drawable drawable = DrawableTool.setTintToWhite(Objects.requireNonNull(getContext()), getContext().getDrawable(R.drawable.ic_baseline_close_24px));
        buttonBack.setImageDrawable(drawable);
        buttonBack.setId(Constants.BUTTON_CLOSE_HELP_OVERLAY);
        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                HelpOverlayFragment.this.onCloseButtonPressed(view);
            }
        });

        if(!this.isVisibleOnCreation)
        {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public void onCloseButtonPressed(View view)
    {
        if (this.fragmentInteractionListener != null)
        {
            this.fragmentInteractionListener.onFragmentInteraction(view);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            this.fragmentInteractionListener = (OnFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        this.fragmentInteractionListener = null;
        this.fragmentView = null;
        this.helpText = null;
        this.isVisibleOnCreation = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(View view);
    }
}
