package de.juliusawen.coastercreditcounter.presentation.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.toolbox.DrawableTool;

public class HelpOverlayFragment extends Fragment
{
    private String title;
    private CharSequence message;

    private TextView textViewTitle;
    private TextView textViewMessage;

    private HelpOverlayFragmentInteractionListener helpOverlayFragmentInteractionListener;

    public HelpOverlayFragment() {}

    public static HelpOverlayFragment newInstance(String helpTitle, CharSequence helpMessage)
    {
        Log.d(Constants.LOG_TAG, "HelpOverlayFragment.newInstance:: instantiating fragment...");

        HelpOverlayFragment helpOverlayFragment = new HelpOverlayFragment();
        Bundle args = new Bundle();
        args.putCharSequence(Constants.FRAGMENT_ARG_HELP_TITLE, helpTitle);
        args.putCharSequence(Constants.FRAGMENT_ARG_HELP_MESSAGE, helpMessage);
        helpOverlayFragment.setArguments(args);
        return helpOverlayFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "HelpOverlayFragment.onCreate:: creating fragment...");

        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            this.title = getArguments().getString(Constants.FRAGMENT_ARG_HELP_TITLE);
            if(this.title == null)
            {
                this.title = getString(R.string.title_help, "");
            }

            this.message = getArguments().getCharSequence(Constants.FRAGMENT_ARG_HELP_MESSAGE);
            if(this.message == null)
            {
                this.message = getString(R.string.help_text_not_available);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "HelpOverlayFragment.onCreateView:: creating view...");

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_help_overlay, container, false);
        this.textViewTitle = linearLayout.findViewById(R.id.textViewHelp_Title);
        this.textViewMessage = linearLayout.findViewById(R.id.textViewHelp_Message);
        return linearLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "HelpOverlayFragment.onViewCreated:: decorating view...");

        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null)
        {
            this.title = savedInstanceState.getString(Constants.KEY_HELP_TITLE);
            this.message = savedInstanceState.getCharSequence(Constants.KEY_HELP_MESSAGE);
        }

        this.textViewTitle.setText(this.title);
        this.textViewMessage.setText(this.message);

        ImageButton buttonBack = view.findViewById(R.id.imageButtonHelp_Close);
        Drawable drawable = DrawableTool.setTintToWhite(Objects.requireNonNull(getContext()).getDrawable(R.drawable.ic_baseline_close), getContext());
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
        outState.putString(Constants.KEY_HELP_TITLE, this.title);
        outState.putCharSequence(Constants.KEY_HELP_MESSAGE, this.message);

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
        this.title = null;
        this.message = null;
        this.textViewTitle = null;
        this.textViewMessage = null;
        this.helpOverlayFragmentInteractionListener = null;
    }

    public interface HelpOverlayFragmentInteractionListener
    {
        void onHelpOverlayFragmentInteraction(View view);
    }
}
