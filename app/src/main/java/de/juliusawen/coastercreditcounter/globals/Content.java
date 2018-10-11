package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.orphanElements.OrphanElement;
import de.juliusawen.coastercreditcounter.data.orphanElements.YearHeader;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public class Content
{
    private Map<UUID, Element> elements = new HashMap<>();
    private Map<UUID, Element> orphanElements = new HashMap<>();

    private Map<String, YearHeader> yearHeadersByName = new HashMap<>();

    private Element rootElement;



    private static final Content instance = new Content();

    static Content getInstance()
    {
        return instance;
    }

    private Content()
    {
        Log.i(Constants.LOG_TAG, Constants.LOG_DIVIDER + "Content.Constructor:: <Content> instantiated");
        Stopwatch stopwatchInitializeContent = new Stopwatch(true);

        Log.i(Constants.LOG_TAG, "Content.Constructor:: fetching content...");
        Stopwatch stopwatchFetchContent = new Stopwatch(true);
        DatabaseMock.getInstance().fetchContent(this);
        Log.i(Constants.LOG_TAG,  String.format("Content.Constructor:: fetching content took [%d]ms", stopwatchFetchContent.stop()));

        if(!this.elements.isEmpty())
        {
            Log.i(Constants.LOG_TAG, "Content.Constructor:: searching for root location...");
            Element rootElement = ((Element) this.elements.values().toArray()[0]).getRootElement();
            Log.i(Constants.LOG_TAG, String.format("Content.Constructor:: root %s found", rootElement));

            this.setRootLocation(rootElement);

            Log.i(Constants.LOG_TAG, "Content.Constructor:: flattening content tree...");
            Stopwatch stopwatchFlattenContentTree = new Stopwatch(true);
            this.flattenContentTree(this.rootElement);
            Log.i(Constants.LOG_TAG,  String.format("Content.Constructor:: flattening content tree took [%d]ms", stopwatchFlattenContentTree.stop()));
        }
        else
        {
            String errorMessage = "Content.Constructor:: no elements fetched - unable to find root location";
            Log.e(Constants.LOG_TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        Log.i(Constants.LOG_TAG, String.format("Content.Constructor:: initializing content took [%d]ms", stopwatchInitializeContent.stop()));
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

//    public void putElementByKeyForActivity(Class<? extends Activity> activity, String key, Element element)
//    {
//        if(!this.elementByKeyForActivity.containsKey(activity))
//        {
//            Map<String, Element> elementByKey = new HashMap<>();
//            elementByKey.put(key, element);
//            this.elementByKeyForActivity.put(activity, elementByKey);
//        }
//        else
//        {
//            this.elementByKeyForActivity.get(activity).put(key, element);
//        }
//    }
//
//    public List<Element> getElements(Class)
//
//    private void putElements(Class type, String key,List<Element> elements)
//    {
//        if(!this.elementsByKeyByActivity.containsKey(type))
//        {
//            Map<String, List<Element>> elementsByKeys = new LinkedHashMap<>();
//            elementsByKeys.put(key, elements);
//            this.elementsByKeyByActivity.put(type, elementsByKeys);
//        }
//    }

    public Element getYearHeader(String name)
    {
        if(this.yearHeadersByName.containsKey(name))
        {
            return this.yearHeadersByName.get(name);
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("Content.getYearHeader:: No YearHeader found with name[%s]", name));
            return null;
        }
    }

    public <T extends OrphanElement> List<T> getOrphanElementsOfType(Class<T> type)
    {
        List<T> orphanElementsOfType = new ArrayList<>();
        for(Element orphanElement : this.orphanElements.values())
        {
            if(orphanElement.isInstance(type))
            {
                orphanElementsOfType.add(type.cast(orphanElement));
            }
        }
        return orphanElementsOfType;
    }

    public void addOrphanElement(Element orphanElement)
    {
        if(orphanElement.isInstance(OrphanElement.class))
        {
            if(orphanElement.isInstance(YearHeader.class))
            {
                this.yearHeadersByName.put(orphanElement.getName(), (YearHeader)orphanElement);
                Log.v(Constants.LOG_TAG,  String.format("Content.addOrphanElement:: %s added to YearHeadersByName", orphanElement));
            }

            if(!this.orphanElements.containsKey(orphanElement.getUuid()))
            {
                this.orphanElements.put(orphanElement.getUuid(), orphanElement);
                Log.v(Constants.LOG_TAG, String.format("Content.addOrphanElement:: %s added to orphan elements", orphanElement));
            }
            else
            {
                Log.e(Constants.LOG_TAG, String.format("Content.addOrphanElement:: %s allready exists", orphanElement));
            }
        }
        else
        {
            Log.e(Constants.LOG_TAG, String.format("Content.addOrphanElement:: %s is not of type <OrphanElement>", orphanElement));
        }

    }

    public void removeOrphanElements(List<? extends OrphanElement> orphanElements)
    {
        Log.v(Constants.LOG_TAG,  String.format("Content.removeOrphanElements:: removing [%d] orphan elements", orphanElements.size()));

        for(OrphanElement orphanElement : orphanElements)
        {
            this.removeOrphanElement(orphanElement);
        }
    }

    private void removeOrphanElement(OrphanElement orphanElement)
    {
        if(orphanElement.isInstance(YearHeader.class))
        {
            if(this.yearHeadersByName.containsKey(orphanElement.getName()))
            {
                this.yearHeadersByName.remove(orphanElement.getName());
                Log.v(Constants.LOG_TAG,  String.format("Content.removeOrphanElement:: %s removed from YearHeadersByName", orphanElement));
            }
            else
            {
                Log.e(Constants.LOG_TAG,  String.format("Content.removeOrphanElement:: %s not found", orphanElement));
            }
        }

        if(this.orphanElements.containsValue(orphanElement))
        {
            this.orphanElements.remove(orphanElement.getUuid());
            Log.v(Constants.LOG_TAG,  String.format("Content.removeOrphanElement:: %s removed from orphan elements", orphanElement));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Content.removeOrphanElement:: %s not found", orphanElement));
        }
    }




    private void flattenContentTree(Element element)
    {
        this.addElement(element);
        for (Element child : element.getChildren())
        {
            this.flattenContentTree(child);
        }
    }

    public ArrayList<String> getUuidStringsFromElements(List<Element> elements)
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

        Log.v(Constants.LOG_TAG, String.format("Content.fetchElementsByUuidStrings:: fetching [%d] elements took [%d]ms ", uuidStrings.size(), stopwatch.stop()));
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
        else if(this.orphanElements.containsKey(uuid))
        {
            return this.orphanElements.get(uuid);
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
        if(!element.isInstance(OrphanElement.class))
        {
            Log.v(Constants.LOG_TAG,  String.format("Content.addElement:: %s added", element));
            this.elements.put(element.getUuid(), element);
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Content.addElement:: adding %s requested -- DEPRECATED: use AddOrphanElement", element));
            //Todo: remove when new way to pass elements around is implemented
            if(!this.orphanElements.containsValue(element))
            {
                this.orphanElements.put(element.getUuid(), element);
                Log.e(Constants.LOG_TAG,  String.format("Content.addElement:: %s added -- DEPRECATED: use AddOrphanElement", element));
            }
        }
    }

    public boolean deleteElement(Element element)
    {
        if(this.elements.containsKey(element.getUuid()))
        {
            Log.v(Constants.LOG_TAG,  String.format("Content.deleteElement:: %s removed", element));
            this.elements.remove(element.getUuid());
            return true;
        }
        return false;
    }
}