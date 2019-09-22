package de.juliusawen.coastercreditcounter.tools.confirmSnackbar;

import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;

public interface IConfirmSnackbarClient
{
    void handleActionConfirmed(RequestCode requestCode);
}
