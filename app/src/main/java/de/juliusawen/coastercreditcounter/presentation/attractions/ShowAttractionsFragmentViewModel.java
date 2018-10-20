package de.juliusawen.coastercreditcounter.presentation.attractions;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.ViewModel;
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategoryHeader;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.presentation.contentRecyclerViewAdapter.ContentRecyclerViewAdapter;

public class ShowAttractionsFragmentViewModel extends ViewModel
{
    public Park park;
    public ContentRecyclerViewAdapter contentRecyclerViewAdapter;

    private Map<String, AttractionCategoryHeader> attractionCategoryHeadersByAttractionCategoryNames = new HashMap<>();

    List<Element> getCategorizedAttractions(List<Element> attractions)
    {
        List<Element> categorizedAttractions = new ArrayList<>();

        if(attractionCategoryHeadersByAttractionCategoryNames.isEmpty())
        {
            Log.d(Constants.LOG_TAG, "ShowAttractionsFragmentViewModel.getCategorizedAttractions:: initally fetching AttractionCategoryHeaders...");

            categorizedAttractions = AttractionCategoryHeader.fetchAttractionCategoryHeadersFromElements(attractions);

            for(Element attractionCategoryHeader : categorizedAttractions)
            {
                this.attractionCategoryHeadersByAttractionCategoryNames.put(
                        ((Attraction)attractionCategoryHeader.getChildren().get(0)).getCategory().getName(),
                        (AttractionCategoryHeader) attractionCategoryHeader);
            }
        }
        else
        {
            boolean createdNewAttractionCategoryHeader = false;

            for(AttractionCategoryHeader attractionCategoryHeader : this.attractionCategoryHeadersByAttractionCategoryNames.values())
            {
                attractionCategoryHeader.getChildren().clear();
            }

            for(Attraction attraction : Element.convertElementsToType(attractions, Attraction.class))
            {
                String categoryName = attraction.getCategory().getName();
                AttractionCategoryHeader attractionCategoryHeader = this.attractionCategoryHeadersByAttractionCategoryNames.get(categoryName);

                if(attractionCategoryHeader == null)
                {
                    Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragmentViewModel.getCategorizedAttractions:: creating AttractionCategoryHeader for %s...", attraction));

                    attractionCategoryHeader = AttractionCategoryHeader.create(attraction.getCategory());
                    App.content.addOrphanElement(attractionCategoryHeader);

                    this.attractionCategoryHeadersByAttractionCategoryNames.put(attraction.getCategory().getName(), attractionCategoryHeader);
                    createdNewAttractionCategoryHeader = true;
                }

                if(!categorizedAttractions.contains(attractionCategoryHeader))
                {
                    Log.v(Constants.LOG_TAG, String.format("ShowAttractionsFragmentViewModel.getCategorizedAttractions:: adding %s...", attractionCategoryHeader));
                    categorizedAttractions.add(attractionCategoryHeader);
                }

                if(!attractionCategoryHeader.getChildren().contains(attraction))
                {
                    Log.v(Constants.LOG_TAG, String.format("ShowAttractionsFragmentViewModel.getCategorizedAttractions:: adding %s to %s...", attraction, attractionCategoryHeader));
                    attractionCategoryHeader.addChildToOrphanElement(attraction);
                }

            }

            if(createdNewAttractionCategoryHeader)
            {
                Log.v(Constants.LOG_TAG, "ShowAttractionsFragmentViewModel.getCategorizedAttractions:: new AttractionCategoryHeaders were created...");

                List<AttractionCategoryHeader> attractionCategoryHeadersToRemove = new ArrayList<>();
                for(AttractionCategoryHeader attractionCategoryHeader : this.attractionCategoryHeadersByAttractionCategoryNames.values())
                {
                    if(!categorizedAttractions.contains(attractionCategoryHeader))
                    {
                        Log.v(Constants.LOG_TAG, String.format("ShowAttractionsFragmentViewModel.getCategorizedAttractions:: marking unused %s to remove...", attractionCategoryHeader));
                        attractionCategoryHeadersToRemove.add(attractionCategoryHeader);
                    }
                }

                if(!attractionCategoryHeadersToRemove.isEmpty())
                {
                    for(AttractionCategoryHeader attractionCategoryHeader : attractionCategoryHeadersToRemove)
                    {
                        Log.d(Constants.LOG_TAG, String.format("ShowAttractionsFragmentViewModel.getCategorizedAttractions:: removing %s...", attractionCategoryHeader));

                        this.attractionCategoryHeadersByAttractionCategoryNames.remove(attractionCategoryHeader.getName());
                        App.content.removeOrphanElement(attractionCategoryHeader);
                    }
                }
            }
        }

        return AttractionCategoryHeader.sortAttractionCategoryHeadersBasedOnAttractionCategoriesOrder(categorizedAttractions);
    }
}
