package de.juliusawen.coastercreditcounter.frontend.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.ButtonFunction;

public class ConfirmDialogFragment extends Fragment
{
    private ConfirmDialogFragmentInteractionListener confirmDialogFragmentInteractionListener;
    private LinearLayout linearLayoutFragmentConfirmDialog;

    public ConfirmDialogFragment() {}

    public static ConfirmDialogFragment newInstance()
    {
        Log.i(Constants.LOG_TAG, "ConfirmDialogFragment.newInstance:: instantiating fragment...");
        return new ConfirmDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ConfirmDialogFragment.onCreateView:: creating view...");
        return inflater.inflate(R.layout.fragment_confirm_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "ConfirmDialogFragment.onViewCreated:: decorating view...");
        super.onViewCreated(view, savedInstanceState);

        this.linearLayoutFragmentConfirmDialog = view.findViewById(R.id.linearLayoutFragmentConfirmDialog);
        Button buttonOk = view.findViewById(R.id.buttonConfirmDialogFragment_ok);
        buttonOk.setId(ButtonFunction.OK.ordinal());
        buttonOk.setText(R.string.text_ok);
        if(!buttonOk.hasOnClickListeners())
        {
            buttonOk.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onButtonPressed(view);
                }
            });
        }

        Button buttonCancel = view.findViewById(R.id.buttonConfirmDialogFragment_cancel);
        buttonCancel.setId(ButtonFunction.CANCEL.ordinal());
        buttonCancel.setText(R.string.text_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener()
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
        this.linearLayoutFragmentConfirmDialog = null;
    }

    public void hideDialog()
    {
        this.linearLayoutFragmentConfirmDialog.setVisibility(View.INVISIBLE);
    }

    public void showDialog()
    {
        this.linearLayoutFragmentConfirmDialog.setVisibility(View.VISIBLE);
    }

    public interface ConfirmDialogFragmentInteractionListener
    {
        void onConfirmDialogFragmentInteraction(View view);
    }
}
