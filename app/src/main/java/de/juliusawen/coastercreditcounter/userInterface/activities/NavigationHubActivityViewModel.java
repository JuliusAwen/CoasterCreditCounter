package de.juliusawen.coastercreditcounter.userInterface.activities;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import java.util.List;

import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.tools.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.tools.menuTools.IOptionsMenuButlerCompatibleViewModel;
import de.juliusawen.coastercreditcounter.userInterface.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class NavigationHubActivityViewModel extends ViewModel implements IOptionsMenuButlerCompatibleViewModel
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
    public ContentRecyclerViewAdapter getContentRecyclerViewAdapter()
    {
        return null;
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

    @Override
    public IElement getElement()
    {
        return null;
    }
}
