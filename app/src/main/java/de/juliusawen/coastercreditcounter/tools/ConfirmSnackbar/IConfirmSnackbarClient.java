package de.juliusawen.coastercreditcounter.tools.ConfirmSnackbar;

import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public interface IConfirmSnackbarClient
{
    void handleActionConfirmed(RequestCode requestCode);
}
