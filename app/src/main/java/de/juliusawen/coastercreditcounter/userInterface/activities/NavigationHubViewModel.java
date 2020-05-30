package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.net.Uri;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.OptionsMenuButlerCompatibleBaseViewModel;

public class NavigationHubViewModel extends OptionsMenuButlerCompatibleBaseViewModel
{
    public RequestCode requestCode = RequestCode.NAVIGATE;
    public List<IElement> currentVisits;

    public Uri uri;
    public boolean isImporting = false;
    public boolean isImportSuccessful = false;

    public boolean isExporting = false;
    public boolean isExportSuccessful = false;

    long lastBackClickedInMS;

    @Override
    public RequestCode getRequestCode()
    {
        return this.requestCode;
    }

    @Override
    public List<IElement> getElements()
    {
        return this.currentVisits;
    }

    @Override
    public void setElements(List<IElement> elements)
    {
        if(!elements.isEmpty() && elements.get(0).isVisit())
        {
            this.currentVisits = elements;
        }
        else
        {
            throw new IllegalArgumentException("NavigationHubActivityViewModel.setElements:: elements are not Visits");
        }
    }
}
