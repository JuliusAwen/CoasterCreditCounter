package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.database.DatabaseMock;

public class Content
{
    private Location locationRoot;
    private Map<UUID, Element> elements;

    private static final Content instance = new Content();

    public static Content getInstance()
    {

        return instance;
    }

    private Content()
    {
        Log.v(Constants.LOG_TAG, this.getClass().toString() + ":: Constructor called.");

        this.elements = new HashMap<>();

        new DatabaseMock().fetchContent(this);
        this.flattenContentTree(this.getLocationRoot());
    }

    public Location getLocationRoot()
    {
        return this.locationRoot;
    }

    public void setLocationRoot(Location locationRoot)
    {
        this.locationRoot = locationRoot;
    }

    public Element getElementByUuid(UUID uuid)
    {
        return this.elements.get(uuid);
    }

    private void flattenContentTree(Location location)
    {
        if (!location.getChildren().isEmpty())
        {
            this.elements.put(location.getUuid(), location);

            for (Location child : location.getChildren())
            {
                if(child.getClass().equals(Location.class))
                {
                    this.flattenContentTree(child);
                }
                else if(child.getClass().equals(Park.class))
                {
                    for (Attraction attraction : ((Park) child).getAttractions())
                    {
                        this.elements.put(attraction.getUuid(), attraction);
                    }
                }
            }
        }
    }

    public ArrayList<String> convertToUuidStringArrayList(List<? extends Element> elements)
    {
        ArrayList<String> strings = new ArrayList<>();

        for(Element element : elements)
        {
            strings.add(element.getUuid().toString());
        }

        return strings;
    }

    public List<Element> getElementsFromUuidStringArrayList(List<String> strings)
    {
        List<Element> elements = new ArrayList<>();

        for(String string : strings)
        {
            elements.add(this.getElementByUuid(UUID.fromString(string)));
        }

        return elements;
    }

    //TODO: implement List<Location> & List<Attraction>
    public List<Location> getLocationsFromUuidStringArrayList(List<String> strings)
    {
        List<Location> locations = new ArrayList<>();

        for(String string : strings)
        {
            locations.add((Location) this.getElementByUuid(UUID.fromString(string)));
        }

        return locations;
    }

    public List<Element> convertToElementArrayList(List<? extends Element> elementsToConvert)
    {
        List<Element> elements = new ArrayList<>();

        for(Element element : elementsToConvert)
        {
            elements.add(element);
        }

        return elements;
    }
}
