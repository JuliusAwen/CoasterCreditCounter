package de.juliusawen.coastercreditcounter.userInterface.activities;

import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.BaseViewModel;
import de.juliusawen.coastercreditcounter.tools.menuTools.IBaseViewModel;

public class DeveloperOptionsViewModel extends BaseViewModel implements IBaseViewModel
{
    public RequestCode requestCode = RequestCode.DEVELOPER_OPTIONS;

    public DeveloperOptionsActivity.Mode mode;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }
}
