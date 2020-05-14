package de.juliusawen.coastercreditcounter.userInterface.toolFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.tools.StringTool;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class AlertDialogFragment extends DialogFragment
{
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
        Log.i(String.format("instantiating AlertDialogFragment with Title[%s], Message[%s], PositiveButtonText[%s], NegativeButtonText[%s]", 
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

    public static AlertDialogFragment newInstance(
            int iconResource,
            String title,
            String message,
            LinkedHashMap<String, Integer> substringsByTypefaces,
            String positiveButtonText,
            String negativeButtonText,
            RequestCode requestCode,
            boolean isChildFragment)
    {
        Log.i(String.format("instantiating AlertDialogFragment with Title[%s], Message[%s], PositiveButtonText[%s], NegativeButtonText[%s]",
                title, message, positiveButtonText, negativeButtonText));

        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();

        ArrayList<String> substrings = new ArrayList<>(substringsByTypefaces.keySet());
        ArrayList<Integer> typefaces = new ArrayList<>(substringsByTypefaces.values());

        Bundle args = new Bundle();
        args.putInt(Constants.FRAGMENT_ARG_ALERT_DIALOG_ICON_RESOURCE, iconResource);
        args.putString(Constants.FRAGMENT_ARG_ALERT_DIALOG_TITLE, title);
        args.putString(Constants.FRAGMENT_ARG_ALERT_DIALOG_MESSAGE, message);
        args.putStringArrayList(Constants.FRAGMENT_ARG_ALERT_DIALOG_SUBSTRINGS, substrings);
        args.putIntegerArrayList(Constants.FRAGMENT_ARG_ALERT_DIALOG_TYPEFACES, typefaces);
        args.putString(Constants.FRAGMENT_ARG_ALERT_DIALOG_POSITIVE_BUTTON_TEXT, positiveButtonText);
        args.putString(Constants.FRAGMENT_ARG_ALERT_DIALOG_NEGATIVE_BUTTON_TEXT, negativeButtonText);
        args.putInt(Constants.FRAGMENT_ARG_ALERT_DIALOG_REQUEST_CODE, requestCode.ordinal());
        args.putBoolean(Constants.FRAGMENT_ARG_IS_CHILD_FRAGMENT, isChildFragment);
        alertDialogFragment.setArguments(args);

        return alertDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Log.v("AlertDialogFragment.onCreateDialog:: creating alert dialog...");

        Bundle args = getArguments();
        assert args != null;

        int iconResource = args.getInt(Constants.FRAGMENT_ARG_ALERT_DIALOG_ICON_RESOURCE);
        String title = args.getString(Constants.FRAGMENT_ARG_ALERT_DIALOG_TITLE);
        String message = args.getString(Constants.FRAGMENT_ARG_ALERT_DIALOG_MESSAGE);
        ArrayList<String> substrings = args.getStringArrayList(Constants.FRAGMENT_ARG_ALERT_DIALOG_SUBSTRINGS);
        ArrayList<Integer> typefaces = args.getIntegerArrayList(Constants.FRAGMENT_ARG_ALERT_DIALOG_TYPEFACES);
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

        CharSequence formattedMessage = "";
        Map<String, Integer> substringsByTypefaces = new HashMap<>();
        if(substrings != null && !substrings.isEmpty() && typefaces != null && !typefaces.isEmpty())
        {
            for(int i = 0; i < substrings.size(); i++)
            {
                substringsByTypefaces.put(substrings.get(i), typefaces.get(i));
            }
            formattedMessage = StringTool.buildSpannableStringWithTypefaces(message, substringsByTypefaces);
        }

        return new AlertDialog.Builder(getActivity())
                .setIcon(iconResource)
                .setTitle(title)
                .setMessage(formattedMessage.length() == 0 ? message : formattedMessage)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.i(String.format("PositiveButton [%s] clicked", positiveButtonText));
                        dialog.dismiss();
                        AlertDialogFragment.this.alertDialogListener.handleAlertDialogClick(RequestCode.values()[requestCode], which);
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.i(String.format("NegativeButton [%s] clicked", negativeButtonText));
                        dialog.dismiss();
                        AlertDialogFragment.this.alertDialogListener.handleAlertDialogClick(RequestCode.values()[requestCode], which);
                    }
                })
                .create();
    }

    public interface AlertDialogListener
    {
        void handleAlertDialogClick(RequestCode requestCode, int which);
    }
}
