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
        this.elements.put(location.getUuid(), location);

        for (Location child : location.getChildren())
        {
            this.flattenContentTree(child);
        }

        if(location.getClass().equals(Park.class))
        {
            for (Attraction attraction : ((Park) location).getAttractions())
            {
                this.elements.put(attraction.getUuid(), attraction);
            }
        }
    }

    public ArrayList<String> getUuidStringsFromElements(List<? extends Element> elements)
    {
        ArrayList<String> strings = new ArrayList<>();

        for(Element element : elements)
        {
            strings.add(element.getUuid().toString());
        }

        return strings;
    }

    public List<Element> getElementsFromUuidStrings(List<String> strings)
    {
        List<Element> elements = new ArrayList<>();

        for(String string : strings)
        {
            elements.add(this.getElementByUuid(UUID.fromString(string)));
        }

        return elements;
    }

    public List<Location> getLocationsFromUuidStrings(List<String> strings)
    {
        List<Location> locations = new ArrayList<>();

        for(String string : strings)
        {
            locations.add((Location) this.getElementByUuid(UUID.fromString(string)));
        }

        return locations;
    }

    public List<Location> convertElementsToLocations(List<Element> elements)
    {
        List<Location> locations = new ArrayList<>();

        for(Element element : elements)
        {
            locations.add(((Location) element));
        }

        return locations;
    }

    public List<Attraction> convertElementsToAttractions(List<Element> elements)
    {
        List<Attraction> attractions = new ArrayList<>();

        for(Element element : elements)
        {
            attractions.add(((Attraction) element));
        }

        return attractions;
    }

    public void addLocation(Element parentElement, String childName)
    {
        if(!childName.isEmpty())
        {
            childName = childName.trim();
            Location parent = (Location) parentElement;
            Location child = new Location(childName, UUID.randomUUID());

            parent.addChild(child);
            this.elements.put(child.getUuid(), child);
        }
    }

    public void removeLocationAndChildren(Element element)
    {
        Location location = (Location) element;

        if(!location.getChildren().isEmpty())
        {
            for(Location child : location.getChildren())
            {
                this.removeLocationAndChildren(child);
            }
        }

        this.elements.remove(location.getUuid());

        Log.v(Constants.LOG_TAG, this.getClass().toString() + ":: deleted " + location.getName());
    }

    public void deleteElement(Element element)
    {
        this.elements.remove(element.getUuid());
    }
}
