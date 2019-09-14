package de.juliusawen.coastercreditcounter.userInterface.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class AlertDialogFragment extends DialogFragment
{
    public AlertDialogFragment() {}

    public AlertDialogListener alertDialogListener;

    public static AlertDialogFragment newInstance(
            int iconResource,
            String title,
            String message,
            String positiveButtonText,
            String negativeButtonText,
            RequestCode requestCode,
            boolean isChildFragment)
    {
        Log.v(Constants.LOG_TAG, String.format("AlertDialogFragment.newInstance:: " +
                "instantiating AlertDialogFragment with Title[%s], Message[%s], PositiveButtonText[%s], NegativeButtonText[%s]",
                title, message, positiveButtonText, negativeButtonText));

        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        args.putInt(Constants.FRAGMENT_ARG_ALERT_DIALOG_ICON_RESOURCE, iconResource);
        args.putString(Constants.FRAGMENT_ARG_ALERT_DIALOG_TITLE, title);
        args.putString(Constants.FRAGMENT_ARG_ALERT_DIALOG_MESSAGE, message);
        args.putString(Constants.FRAGMENT_ARG_ALERT_DIALOG_POSITIVE_BUTTON_TEXT, positiveButtonText);
        args.putString(Constants.FRAGMENT_ARG_ALERT_DIALOG_NEGATIVE_BUTTON_TEXT, negativeButtonText);
        args.putInt(Constants.FRAGMENT_ARG_ALERT_DIALOG_REQUEST_CODE, requestCode.ordinal());
        args.putBoolean(Constants.FRAGMENT_ARG_IS_CHILD_FRAGMENT, isChildFragment);
        alertDialogFragment.setArguments(args);

        return alertDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Log.v(Constants.LOG_TAG, "AlertDialogFragment.onCreateDialog:: creating alert dialog...");

        Bundle args = getArguments();
        assert args != null;

        int iconResource = args.getInt(Constants.FRAGMENT_ARG_ALERT_DIALOG_ICON_RESOURCE);
        String title = args.getString(Constants.FRAGMENT_ARG_ALERT_DIALOG_TITLE);
        String message = args.getString(Constants.FRAGMENT_ARG_ALERT_DIALOG_MESSAGE);
        final String positiveButtonText = args.getString(Constants.FRAGMENT_ARG_ALERT_DIALOG_POSITIVE_BUTTON_TEXT);
        final String negativeButtonText = args.getString(Constants.FRAGMENT_ARG_ALERT_DIALOG_NEGATIVE_BUTTON_TEXT);
        final int requestCode = args.getInt(Constants.FRAGMENT_ARG_ALERT_DIALOG_REQUEST_CODE);

        if(args.getBoolean(Constants.FRAGMENT_ARG_IS_CHILD_FRAGMENT))
        {
            this.alertDialogListener = (AlertDialogListener) getParentFragment();
        }
        else
        {
            this.alertDialogListener = (AlertDialogListener) getActivity();
        }


        return new AlertDialog.Builder(getActivity())
                .setIcon(iconResource)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.i(Constants.LOG_TAG, String.format("AlertDialogFragment.onClick:: PositiveButton [%s] clicked", positiveButtonText));
                        AlertDialogFragment.this.alertDialogListener.onAlertDialogClick(requestCode, dialog, which);
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.i(Constants.LOG_TAG, String.format("AlertDialogFragment.onClick:: NegativeButton [%s] clicked", negativeButtonText));
                        AlertDialogFragment.this.alertDialogListener.onAlertDialogClick(requestCode, dialog, which);
                    }
                })
                .create();
    }

    public interface AlertDialogListener
    {
        void onAlertDialogClick(int requestCode, DialogInterface dialog, int which);
    }
}
