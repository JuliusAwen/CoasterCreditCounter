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
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.VisitedAttraction;
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

    public static void handleOnAttractionCategoryHeaderLongClick(final Context context, View view)
    {
        final Element longClickedElement = (Element) view.getTag();

        PopupMenu popupMenu = new PopupMenu(context, view);

        if(longClickedElement.getChildCountOfType(Attraction.class) > 1 || longClickedElement.getChildCountOfType(VisitedAttraction.class) > 1)
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
                            else if(longClickedElement.hasChildrenOfType(VisitedAttraction.class))
                            {
                                attractions = longClickedElement.getChildrenOfType(VisitedAttraction.class);
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
