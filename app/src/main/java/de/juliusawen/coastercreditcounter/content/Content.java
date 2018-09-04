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
        Log.d(Constants.LOG_TAG, Constants.LOG_DIVIDER);
        Log.i(Constants.LOG_TAG, "Content.Constructor:: creating instance...");

        this.elements = new HashMap<>();

        Log.d(Constants.LOG_TAG, "Content.Constructor:: fetching content...");
        new DatabaseMock().fetchContent(this);
        Log.d(Constants.LOG_TAG, "Content.Constructor:: content fetched");

        if(!this.elements.isEmpty())
        {
            Log.d(Constants.LOG_TAG, "Content.Constructor:: searching for root element...");
            Element rootElement = ((Element) this.elements.values().toArray()[0]).getRootElement();
            Log.d(Constants.LOG_TAG, String.format("Content.Constructor:: root element %s found", rootElement));

            this.setRootElement(rootElement);

            Log.d(Constants.LOG_TAG, "Content.Constructor:: flattening content tree...");
            this.flattenContentTree(this.rootElement);
            Log.d(Constants.LOG_TAG, "Content.Constructor:: content tree flattened");
        }
        else
        {
            String errorMessage = "Content.Constructor:: no elements fetched - unable to find root element";
            Log.e(Constants.LOG_TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    public Element getRootElement()
    {
        return this.rootElement;
    }

    private void setRootElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.setRootElement:: %s set as root element", element));
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

    public static ArrayList<String> getUuidStringsFromElements(List<Element> elements)
    {
        ArrayList<String> uuidStrings = new ArrayList<>();

        for(Element element : elements)
        {
            uuidStrings.add(element.getUuid().toString());
        }

        return uuidStrings;
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
            String errorMessage = String.format("Content.getElementByUuid:: No element found for uuid[%s]", uuid);
            Log.e(Constants.LOG_TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
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
        Log.v(Constants.LOG_TAG,  String.format("Content.addElement:: %s added", element));
        this.elements.put(element.getUuid(), element);
    }

    public void deleteElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.deleteElement:: %s removed", element));
        this.elements.remove(element.getUuid());
    }

    public static List<Element> sortElementListByCompareList(ArrayList<Element> listToOrder, ArrayList<Element> listToCompare)
    {
        Log.d(Constants.LOG_TAG, "Content.sortElementListByCompareList:: sort list...");

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
