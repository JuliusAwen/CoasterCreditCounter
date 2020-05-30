package de.juliusawen.coastercreditcounter.userInterface.activities;

import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.IOptionsMenuButlerCompatibleViewModel;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButlerCompatibleBaseViewModel;

public class DeveloperOptionsViewModel extends OptionsMenuButlerCompatibleBaseViewModel implements IOptionsMenuButlerCompatibleViewModel
{
    public RequestCode requestCode = RequestCode.DEVELOPER_OPTIONS;

    public DeveloperOptionsActivity.Mode mode;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }
}
