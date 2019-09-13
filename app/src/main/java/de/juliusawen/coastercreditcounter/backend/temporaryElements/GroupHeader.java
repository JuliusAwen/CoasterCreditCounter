package de.juliusawen.coastercreditcounter.backend.temporaryElements;

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
import de.juliusawen.coastercreditcounter.backend.attractions.Attraction;
import de.juliusawen.coastercreditcounter.backend.elements.Element;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.frontend.activityDistributor.ActivityDistributor;
import de.juliusawen.coastercreditcounter.frontend.activityDistributor.RequestCode;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class GroupHeader extends OrphanElement implements IGroupHeader, IElement, ITemporaryElement
{
    private final IElement groupElement;

    private GroupHeader(String name, UUID uuid, IElement groupElement)
    {
        super(name, uuid);
        this.groupElement = groupElement;
    }

    public IElement getGroupElement()
    {
        return groupElement;
    }

    public static GroupHeader create(IElement groupItem)
    {
        GroupHeader groupHeader;
        groupHeader = new GroupHeader(groupItem.getName(), UUID.randomUUID(), groupItem);

        Log.v(Constants.LOG_TAG,  String.format("GroupHeader.create:: %s created", groupHeader.getFullName()));

        return groupHeader;
    }
    public static void handleOnGroupHeaderLongClick(final Context context, View view)
    {
        final Element longClickedElement = (Element) view.getTag();

        PopupMenu popupMenu = new PopupMenu(context, view);

        if(longClickedElement.getChildCountOfType(Attraction.class) > 1 || longClickedElement.getChildCountOfType(VisitedAttraction.class) > 1)
        {
            popupMenu.getMenu().add(0, Constants.SELECTION_SORT_ATTRACTIONS, Menu.NONE, R.string.selection_sort_attractions);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    Log.i(Constants.LOG_TAG, String.format("GroupHeader.handleOnGroupHeaderLongClick.onMenuItemClick:: [%S] selected", item.getItemId()));

                    int id = item.getItemId();

                    if(id == Constants.SELECTION_SORT_ATTRACTIONS)
                    {
                        List<IElement> attractions = new ArrayList<>();

                        if(longClickedElement.hasChildrenOfType(Attraction.class))
                        {
                            attractions = longClickedElement.getChildrenOfType(Attraction.class);
                        }
                        else if(longClickedElement.hasChildrenOfType(VisitedAttraction.class))
                        {
                            attractions = longClickedElement.getChildrenOfType(VisitedAttraction.class);
                        }

                        ActivityDistributor.startActivitySortForResult(Objects.requireNonNull(context), RequestCode.SORT_ATTRACTIONS, attractions);
                    }

                    return true;
                }
            });
            popupMenu.show();
        }
    }
}
