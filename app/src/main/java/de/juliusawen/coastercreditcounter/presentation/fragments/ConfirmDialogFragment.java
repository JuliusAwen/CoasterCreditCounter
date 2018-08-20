package de.juliusawen.coastercreditcounter.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.Toolbox.Constants;

public class ConfirmDialogFragment extends Fragment
{
    private View fragmentView;
    private ConfirmDialogFragmentInteractionListener confirmDialogFragmentInteractionListener;

    public ConfirmDialogFragment() {}

    public static ConfirmDialogFragment newInstance()
    {
        return new ConfirmDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.fragmentView = inflater.inflate(R.layout.fragment_confirm_dialog, container, false);
        return this.fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Button buttonCancel = view.findViewById(R.id.buttonConfirmDialogFragment_cancel);
        buttonCancel.setId(Constants.BUTTON_CANCEL);
        buttonCancel.setText(R.string.button_text_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onButtonPressed(view);
            }
        });

        Button buttonOk = view.findViewById(R.id.buttonConfirmDialogFragment_ok);
        buttonOk.setId(Constants.BUTTON_OK);
        buttonOk.setText(R.string.button_text_ok);
        buttonOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onButtonPressed(view);
            }
        });
    }

    public void onButtonPressed(View view)
    {
        if (confirmDialogFragmentInteractionListener != null)
        {
            confirmDialogFragmentInteractionListener.onConfirmDialogFragmentInteraction(view);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if (context instanceof ConfirmDialogFragmentInteractionListener)
        {
            confirmDialogFragmentInteractionListener = (ConfirmDialogFragmentInteractionListener) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement ConfirmDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.confirmDialogFragmentInteractionListener = null;
    }

    public interface ConfirmDialogFragmentInteractionListener
    {
        void onConfirmDialogFragmentInteraction(View view);
    }

    public void setVisibility(Boolean isVisible)
    {
        this.fragmentView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }
}
