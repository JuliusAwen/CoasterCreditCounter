package de.juliusawen.coastercreditcounter.userInterface.toolFragments;

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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;
import de.juliusawen.coastercreditcounter.tools.DrawableProvider;
import de.juliusawen.coastercreditcounter.userInterface.activities.BaseActivityViewModel;

public class HelpOverlayFragment extends Fragment
{
    private BaseActivityViewModel viewModel;

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

        this.viewModel = ViewModelProviders.of(this).get(BaseActivityViewModel.class);

        if (getArguments() != null)
        {
            this.viewModel.helpOverlayFragmentTitle = getArguments().getString(Constants.FRAGMENT_ARG_HELP_TITLE);
            if(this.viewModel.helpOverlayFragmentTitle == null)
            {
                this.viewModel.helpOverlayFragmentTitle = getString(R.string.title_help, getString(R.string.help_title_not_yet_available));
            }

            this.viewModel.helpOverlayFragmentMessage = getArguments().getCharSequence(Constants.FRAGMENT_ARG_HELP_MESSAGE);
            if(this.viewModel.helpOverlayFragmentMessage == null)
            {
                this.viewModel.helpOverlayFragmentMessage = getString(R.string.help_text_not_yet_available);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "HelpOverlayFragment.onCreateView:: creating view...");

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_help_overlay, container, false);
        this.textViewTitle = linearLayout.findViewById(R.id.textViewHelp_Title);
        this.textViewMessage = linearLayout.findViewById(R.id.textViewHelp_Message);
        return linearLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "HelpOverlayFragment.onViewCreated:: decorating view...");

        super.onViewCreated(view, savedInstanceState);

        this.textViewTitle.setText(this.viewModel.helpOverlayFragmentTitle);
        this.textViewMessage.setText(this.viewModel.helpOverlayFragmentMessage);

        ImageButton buttonBack = view.findViewById(R.id.imageButtonHelp_Close);
        Drawable drawable = DrawableProvider.getColoredDrawable(R.drawable.ic_baseline_close, R.color.white);
        buttonBack.setImageDrawable(drawable);
        buttonBack.setId(ButtonFunction.CLOSE.ordinal());
        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                HelpOverlayFragment.this.onCloseButtonClick(view);
            }
        });
    }

    private void onCloseButtonClick(View view)
    {
        if(this.helpOverlayFragmentInteractionListener != null)
        {
            this.helpOverlayFragmentInteractionListener.onHelpOverlayFragmentInteraction(view);
        }
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
        this.textViewTitle = null;
        this.textViewMessage = null;
        this.helpOverlayFragmentInteractionListener = null;
    }

    public void setTitleAndMessage(String title, String message)
    {
        if(this.textViewTitle != null && this.textViewMessage != null)
        {
            this.textViewTitle.setText(title);
            this.textViewMessage.setText(message);
        }
        else
        {
            Log.e(Constants.LOG_TAG, "HelpOverlayFragment.setTitleAndMessage:: TextViewTitle and TextViewMessage not available.");
        }

        this.viewModel.helpOverlayFragmentTitle = title;
        this.viewModel.helpOverlayFragmentMessage = message;
    }

    public interface HelpOverlayFragmentInteractionListener
    {
        void onHelpOverlayFragmentInteraction(View view);
    }
}
