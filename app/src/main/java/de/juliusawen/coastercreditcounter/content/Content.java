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
    private Element rootElement;
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

        if(!this.elements.isEmpty())
        {
            Element element = ((Element) this.elements.values().toArray()[0]).getRootElement();

            this.setRootElement(element);
            this.flattenContentTree(this.rootElement);
        }
        else
        {
            throw new IllegalStateException("Content.Constructor:: not able to find root element.");
        }
    }

    public Element getRootElement()
    {
        return this.rootElement;
    }

    private void setRootElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.setRootElement:: %s[%s] set as root element.", element.getClass().getSimpleName(), element.toString()));
        this.rootElement = element;
    }

    private void flattenContentTree(Element element)
    {
        this.addElement(element);

        for (Element child : element.getChildren())
        {
            this.flattenContentTree(child);
        }
    }

    public static ArrayList<String> createUuidStringsFromElements(List<? extends Element> elements)
    {
        ArrayList<String> strings = new ArrayList<>();

        for(Element element : elements)
        {
            strings.add(element.getUuid().toString());
        }

        return strings;
    }

    public List<Element> fetchElementsFromUuidStrings(List<String> uuidStrings)
    {
        List<Element> elements = new ArrayList<>();

        for(String uuidString : uuidStrings)
        {
            elements.add(this.getElementByUuid(UUID.fromString(uuidString)));
        }

        return elements;
    }

    public Location fetchElementFromUuidString(String uuidString)
    {
        return (Location) this.getElementByUuid(UUID.fromString(uuidString));
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

    public void addElementAndChildren(Element element)
    {
        for(Element child : element.getChildren())
        {
            this.addElementAndChildren(child);
        }

        this.addElement(element);
    }

    public void deleteElementAndChildren(Element element)
    {
        for(Element child : element.getChildren())
        {
            this.deleteElementAndChildren(child);
        }

        this.deleteElement(element);
    }

    public void addElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.addElement:: %s[%s] added.", element.getClass().getSimpleName(), element.toString()));
        this.elements.put(element.getUuid(), element);
    }

    public void deleteElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.deleteElement:: %s[%s] removed.", element.getClass().getSimpleName(), element.toString()));
        this.elements.remove(element.getUuid());
    }

    public static List<Element> orderElementListByCompareList(ArrayList<Element> listToOrder, ArrayList<Element> listToCompare)
    {
        ArrayList<Element> orderedList = new ArrayList<>();

        for(Element element : listToCompare)
        {
            if(listToOrder.contains(element))
            {
                orderedList.add(listToOrder.get(listToOrder.indexOf(element)));
            }
        }

        return orderedList;
    }
}
