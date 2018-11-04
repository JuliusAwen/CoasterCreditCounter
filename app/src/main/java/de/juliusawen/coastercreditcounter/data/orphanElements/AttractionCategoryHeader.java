package de.juliusawen.coastercreditcounter.data.orphanElements;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.CountableAttraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.globals.App;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.enums.Selection;
import de.juliusawen.coastercreditcounter.toolbox.ActivityTool;

public class AttractionCategoryHeader extends OrphanElement
{
    private AttractionCategory attractionCategory;

    private AttractionCategoryHeader(String name, UUID uuid, AttractionCategory attractionCategory)
    {
        super(name, uuid);
        this.attractionCategory = attractionCategory;
    }

    public AttractionCategory getAttractionCategory()
    {
        return attractionCategory;
    }

    public static AttractionCategoryHeader create(AttractionCategory attractionCategory)
    {
        AttractionCategoryHeader attractionCategoryHeader;
        attractionCategoryHeader = new AttractionCategoryHeader(attractionCategory.getName(), UUID.randomUUID(), attractionCategory);

        Log.v(Constants.LOG_TAG,  String.format("AttractionCategoryHeader.create:: %s created", attractionCategoryHeader.getFullName()));

        return attractionCategoryHeader;
    }

    public static List<Element> fetchCategorizedAttractions(List<? extends Element> elements)
    {
        if(elements.isEmpty())
        {
            Log.v(Constants.LOG_TAG, "AttractionCategoryHeader.fetchCategorizedAttractions:: no attractions passed");
            return new ArrayList<>(elements);
        }

        List<Element> categorizedAttractions = new ArrayList<>();

        if(elements.get(0).isInstance(Attraction.class))
        {
            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeader.fetchCategorizedAttractions:: fetching headers for [%d] attractions...", elements.size()));
            List<Attraction> attractions = Element.convertElementsToType(elements, Attraction.class);

            for(Attraction attraction : attractions)
            {
                Element existingCategoryHeader = null;
                for(Element attractionCategoryHeader : categorizedAttractions)
                {
                    if(((AttractionCategoryHeader)attractionCategoryHeader).getAttractionCategory().equals(attraction.getCategory()))
                    {
                        existingCategoryHeader = attractionCategoryHeader;
                    }
                }

                if(existingCategoryHeader != null)
                {
                    existingCategoryHeader.addChildToOrphanElement(attraction);
                }
                else
                {
                    Element attractionCategoryHeader = AttractionCategoryHeader.create(attraction.getCategory());
                    App.content.addOrphanElement(attractionCategoryHeader);
                    attractionCategoryHeader.addChildToOrphanElement(attraction);
                    categorizedAttractions.add(attractionCategoryHeader);
                }
            }
        }
        else if(elements.get(0).isInstance(CountableAttraction.class))
        {
            Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeader.fetchCategorizedAttractions:: adding headers for [%d] countable attractions...", elements.size()));
            List<CountableAttraction> countableAttractions = Element.convertElementsToType(elements, CountableAttraction.class);

            for(CountableAttraction countableAttraction : countableAttractions)
            {
                Element existingCategoryHeader = null;
                for(Element attractionCategoryHeader : categorizedAttractions)
                {
                    if(((AttractionCategoryHeader)attractionCategoryHeader).getAttractionCategory().equals(countableAttraction.getAttraction().getCategory()))
                    {
                        existingCategoryHeader = attractionCategoryHeader;
                    }
                }

                if(existingCategoryHeader != null)
                {
                    existingCategoryHeader.addChildToOrphanElement(countableAttraction);
                }
                else
                {
                    Element attractionCategoryHeader = AttractionCategoryHeader.create(countableAttraction.getAttraction().getCategory());
                    App.content.addOrphanElement(attractionCategoryHeader);
                    attractionCategoryHeader.addChildToOrphanElement(countableAttraction);
                    categorizedAttractions.add(attractionCategoryHeader);
                }
            }
        }

        categorizedAttractions = AttractionCategoryHeader.sortAttractionCategoryHeadersBasedOnAttractionCategoriesOrder(categorizedAttractions);

        Log.v(Constants.LOG_TAG, String.format("AttractionCategoryHeader.fetchCategorizedAttractions:: [%d] headers added", categorizedAttractions.size()));
        return categorizedAttractions;
    }

    public static List<Element> sortAttractionCategoryHeadersBasedOnAttractionCategoriesOrder(List<Element> attractionCategoryHeaders)
    {
        if(attractionCategoryHeaders.size() > 1)
        {
            List<Element> sortedAttractionCategoryHeaders = new ArrayList<>();
            List<AttractionCategory> attractionCategories = App.content.getAttractionCategories();

            Log.v(Constants.LOG_TAG,  String.format("AttractionCategoryHeader.sortAttractionCategoryHeadersBasedOnAttractionCategoriesOrder::" +
                            " sorting [%d]AttractionCategoryHeaders based on [%d]AttractionCategories", attractionCategoryHeaders.size(), attractionCategories.size()));

            List<AttractionCategoryHeader> castedAttractionCategoryHeaders = Element.convertElementsToType(attractionCategoryHeaders, AttractionCategoryHeader.class);

            for(AttractionCategory attractionCategory : attractionCategories)
            {
                for(AttractionCategoryHeader attractionCategoryHeader : castedAttractionCategoryHeaders)
                {
                    if(attractionCategoryHeader.getAttractionCategory().equals(attractionCategory) && !sortedAttractionCategoryHeaders.contains(attractionCategoryHeader))
                    {
                        sortedAttractionCategoryHeaders.add(attractionCategoryHeader);
                        break;
                    }
                }
            }

            return sortedAttractionCategoryHeaders;
        }
        else
        {
            Log.v(Constants.LOG_TAG,"Element.sortElementsBasedOnComparisonList:: not sorted - list contains less than two elements");
            return attractionCategoryHeaders;
        }
    }

    public static AttractionCategoryHeader getAttractionCategoryHeaderForAttractionCategoryFromElements(List<? extends Element> elements, AttractionCategory attractionCategory)
    {
        for(Element element : elements)
        {
            if(element.isInstance(AttractionCategoryHeader.class))
            {
                if(((AttractionCategoryHeader)element).getAttractionCategory().equals(attractionCategory))
                {
                    return (AttractionCategoryHeader) element;
                }
            }
        }

        return null;
    }

    public static void handleOnAttractionCategoryHeaderLongClick(final Context context, View view)
    {
        final Element longClickedElement = (Element) view.getTag();

        PopupMenu popupMenu = new PopupMenu(context, view);

        if(longClickedElement.getChildCountOfType(Attraction.class) > 1 || longClickedElement.getChildCountOfType(CountableAttraction.class) > 1)
        {
            popupMenu.getMenu().add(0, Selection.SORT_ATTRACTIONS.ordinal(), Menu.NONE, R.string.selection_sort_attractions);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    Selection selection = Selection.values()[item.getItemId()];
                    Log.i(Constants.LOG_TAG, String.format("AttractionCategoryHeader.handleOnAttractionCategoryHeaderLongClick.onMenuItemClick:: [%S] selected", selection));

                    switch (selection)
                    {
                        case SORT_ATTRACTIONS:
                        {
                            List<Element> attractions = new ArrayList<>();

                            if(longClickedElement.hasChildrenOfType(Attraction.class))
                            {
                                attractions = longClickedElement.getChildrenOfType(Attraction.class);
                            }
                            else if(longClickedElement.hasChildrenOfType(CountableAttraction.class))
                            {
                                attractions = longClickedElement.getChildrenOfType(CountableAttraction.class);
                            }

                            ActivityTool.startActivitySortForResult(
                                    Objects.requireNonNull(context),
                                    Constants.REQUEST_SORT_ATTRACTIONS,
                                    attractions);

                            return true;
                        }
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        }
    }
}
