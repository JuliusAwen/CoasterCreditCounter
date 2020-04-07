package de.juliusawen.coastercreditcounter.tools.confirmSnackbar;

import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public class ConfirmSnackbar
{
    final IConfirmSnackbarClient client;
    final RequestCode requestCode;
    boolean actionConfirmed = false;

    public static void Show(Snackbar snackbar, RequestCode requestCode, IConfirmSnackbarClient client)
    {
        Log.i(Constants.LOG_TAG, String.format("ConfirmSnackbar.show:: showing snackbar with action [%s] in [%s]", requestCode, client.getClass().getSimpleName()));

        new ConfirmSnackbar(snackbar, requestCode, client);
        snackbar.show();
    }

    private ConfirmSnackbar(Snackbar snackbar, RequestCode requestCode, IConfirmSnackbarClient client)
    {
        this.requestCode = requestCode;
        this.client = client;
        this.setAction(snackbar);
        this.setCallback(snackbar);

    }

    private void setAction(Snackbar snackbar)
    {
        snackbar.setAction(R.string.action_confirm_text, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                actionConfirmed = true;
                Log.i(Constants.LOG_TAG, String.format("ConfirmSnackbar.onClick:: action [%s] in [%s] confirmed", requestCode, client.getClass().getSimpleName()));
            }
        });
    }

    private void setCallback(Snackbar snackbar)
    {
        snackbar.addCallback(new Snackbar.Callback()
        {
            @Override
            public void onDismissed(Snackbar snackbar, int event)
            {
                Log.i(Constants.LOG_TAG, String.format("ConfirmSnackbar.onDismissed:: action [%s] confirmed[%S] - calling client [%s]",
                        requestCode, actionConfirmed, client.getClass().getSimpleName()));

                if(actionConfirmed)
                {
                    actionConfirmed = false;
                    client.handleActionConfirmed(requestCode);
                }
                else
                {
                    client.handleActionConfirmed(RequestCode.INVALID);
                }
            }
        });
    }
}
