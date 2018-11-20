package de.juliusawen.coastercreditcounter.data.orphanElements;

import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.R;
import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
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

        if(longClickedElement.getChildCountOfType(Attraction.class) > 1)
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
                            ActivityTool.startActivitySortForResult(
                                    Objects.requireNonNull(context),
                                    Constants.REQUEST_SORT_ATTRACTIONS,
                                    longClickedElement.getChildrenOfType(Attraction.class));

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
