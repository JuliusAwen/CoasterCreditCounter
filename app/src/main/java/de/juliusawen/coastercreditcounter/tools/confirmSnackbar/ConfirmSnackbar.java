package de.juliusawen.coastercreditcounter.tools.confirmSnackbar;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.logger.Log;

public class ConfirmSnackbar
{
    final IConfirmSnackbarClient client;
    final RequestCode requestCode;
    boolean actionConfirmed = false;

    public static void Show(Snackbar snackbar, RequestCode requestCode, IConfirmSnackbarClient client)
    {
        Log.i(String.format("showing snackbar with action [%s] in [%s]", requestCode, client.getClass().getSimpleName()));

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
                Log.i(String.format("action [%s] in [%s] confirmed", requestCode, client.getClass().getSimpleName()));
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
                Log.i(String.format("action [%s] confirmed[%S] - calling client [%s]",
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
