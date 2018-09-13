package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.OrphanElement;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Content
{
    private Element rootElement;
    private Map<UUID, Element> elements = new HashMap<>();
    private Map<UUID, Element> temporaryElements = new HashMap<>();

    private static final Content instance = new Content();

    static Content getInstance()
    {
        return instance;
    }

    private Content()
    {
        Stopwatch stopwatch = new Stopwatch(true);

        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "Content.Constructor:: creating instance...");

        Log.d(Constants.LOG_TAG, "Content.Constructor:: fetching content...");
        DatabaseMock.getInstance().fetchContent(this);

        if(!this.elements.isEmpty())
        {
            Log.d(Constants.LOG_TAG, "Content.Constructor:: searching for root location...");
            Element rootElement = ((Element) this.elements.values().toArray()[0]).getRootElement();
            Log.d(Constants.LOG_TAG, String.format("Content.Constructor:: root %s found", rootElement));

            this.setRootLocation(rootElement);

            Log.d(Constants.LOG_TAG, "Content.Constructor:: flattening content tree...");
            Stopwatch stopwatchFlattenContentTree = new Stopwatch(true);
            this.flattenContentTree(this.rootElement);
            Log.d(Constants.LOG_TAG,  String.format("Content.flattenContentTree:: flattening content tree took [%d]ms", stopwatchFlattenContentTree.stop()));
        }
        else
        {
            String errorMessage = "Content.Constructor:: no elements fetched - unable to find root location";
            Log.e(Constants.LOG_TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        Log.i(Constants.LOG_TAG, String.format("Content.Constructor:: initializing content took [%d]ms", stopwatch.stop()));
    }

    public Element getRootLocation()
    {
        return this.rootElement;
    }

    private void setRootLocation(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.setRootLocation:: %s set as root", element));
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

    public List<Element> fetchElementsByUuidStrings(List<String> uuidStrings)
    {
        Stopwatch stopwatch = new Stopwatch(true);

        List<Element> elements = new ArrayList<>();
        for(String uuidString : uuidStrings)
        {
            elements.add(this.getElementByUuid(UUID.fromString(uuidString)));
        }

        Log.v(Constants.LOG_TAG, String.format("Content.fetchElementsByUuidStrings:: fetching #[%d] elements took [%d]ms ", uuidStrings.size(), stopwatch.stop()));
        return elements;
    }

    public Element fetchElementByUuidString(String uuidString)
    {
        return this.getElementByUuid(UUID.fromString(uuidString));
    }

    public Element getElementByUuid(UUID uuid)
    {
        if(this.elements.containsKey(uuid))
        {
            return this.elements.get(uuid);
        }
        else if(this.temporaryElements.containsKey(uuid))
        {
            return this.temporaryElements.get(uuid);
        }
        else
        {
            Log.w(Constants.LOG_TAG, String.format("Content.getElementByUuid:: No element found for uuid[%s]", uuid));
            return null;
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

    public boolean deleteElementAndChildren(Element element)
    {
        for(Element child : element.getChildren())
        {
            if(!this.deleteElementAndChildren(child))
            {
                return false;
            }
        }
        return this.deleteElement(element);
    }

    public void addElements(List<? extends Element> elements)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.addElements:: adding #[%d] elements", elements.size()));
        for(Element element : elements)
        {
            this.addElement(element);
        }
    }

    public void addElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.addElement:: %s added", element));
        if(!element.isInstance(OrphanElement.class))
        {
            this.elements.put(element.getUuid(), element);
        }
        else
        {
            this.temporaryElements.put(element.getUuid(), element);
        }
    }

    public boolean deleteElements(List<Element> elements)
    {
        for(Element element : elements)
        {
            if(!this.deleteElement(element))
            {
                return false;
            }
        }
        return true;
    }

    public boolean deleteElement(Element element)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.deleteElement:: %s removed", element));
        if(this.temporaryElements.containsKey(element.getUuid()))
        {
            this.temporaryElements.remove(element.getUuid());
            return true;
        }
        else if(this.elements.containsKey(element.getUuid()))
        {
            this.elements.remove(element.getUuid());
            return true;
        }
        return false;
    }
}