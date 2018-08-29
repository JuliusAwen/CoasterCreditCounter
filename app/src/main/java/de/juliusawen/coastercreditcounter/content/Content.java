package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.content.database.DatabaseMock;
import de.juliusawen.coastercreditcounter.toolbox.Constants;

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
        Log.v(Constants.LOG_TAG, "Content:: Constructor called.");

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
        if(locationRoot.getParent() == null)
        {
            Log.v(Constants.LOG_TAG,  String.format("Content.setLocationRoot:: root[%s] set.", locationRoot.getName()));
            this.locationRoot = locationRoot;
        }
        else
        {
            throw new IllegalStateException("Location with parent can not be set as location root - parent has to be null.");
        }
    }

    public Element getElementByUuid(UUID uuid)
    {
        if(this.elements.containsKey(uuid))
        {
            return this.elements.get(uuid);
        }
        else
        {
            throw new IllegalStateException(String.format("No element found for uuid[%s].", uuid));
        }
    }

    private void flattenContentTree(Element element)
    {
        this.addElement(element);

        for (Element child : ((Location)element).getChildren())
        {
            this.flattenContentTree(child);
        }

        if(!((Location)element).getParks().isEmpty())
        {
            for(Park park : ((Location)element).getParks())
            {
                this.addElement(park);

                for (Attraction attraction : park.getAttractions())
                {
                    this.addElement(attraction);
                }
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

    public List<Element> getElementsFromUuidStrings(List<String> uuidStrings)
    {
        List<Element> elements = new ArrayList<>();

        for(String uuidString : uuidStrings)
        {
            elements.add(this.getElementByUuid(UUID.fromString(uuidString)));
        }

        return elements;
    }

    public Location getLocationFromUuidString(String uuidString)
    {
        return (Location) this.getElementByUuid(UUID.fromString(uuidString));
    }

    public List<Location> getLocationsFromUuidStrings(List<String> uuidStrings)
    {
        List<Location> locations = new ArrayList<>();

        for(String uuidString : uuidStrings)
        {
            locations.add((Location) this.getElementByUuid(UUID.fromString(uuidString)));
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

    public void addLocationAndChildren(Location location)
    {
        this.addElement(location);
        for(Location child : location.getChildren())
        {
            this.addLocationAndChildren(child);
        }
    }

    public void deleteLocationAndChildren(Location location)
    {
        for(Location child : location.getChildren())
        {
            this.deleteLocationAndChildren(child);
        }

        this.deleteElement(location);
    }

    public void addElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.addElement:: element[%s] added.", element.toString()));
        this.elements.put(element.getUuid(), element);
    }

    public void deleteElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.deleteElement:: element[%s] removed.", element.toString()));
        this.elements.remove(element.getUuid());
    }
}
