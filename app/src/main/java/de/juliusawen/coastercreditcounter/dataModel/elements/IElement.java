package de.juliusawen.coastercreditcounter.dataModel.elements;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public interface IElement
{
    String getName();
    String getFullName();
    boolean setName(String name);

    UUID getUuid();

    IElement getParent();
    void setParent(IElement parent);

    void addChild(IElement child);
    void addChildren(List<IElement> children);
    void addChildAndSetParent(IElement child);
    void addChildAndSetParentAtIndex(int index, IElement child);
    void addChildrenAndSetParent(List<UUID> childUuids);
    void addChildrenAndSetParentsAtIndex(int index, List<IElement> children);

    List<IElement> getChildren();
    List<IElement> getChildrenOfType(Class<? extends IElement> type);
    <T extends IElement> List<T> getChildrenAsType(Class<T> type);

    int getChildCount();
    int getChildCountOfType(Class<? extends IElement> type);

    boolean hasChildrenOfType(Class<? extends IElement> type);
    boolean hasChildren();

    int getIndexOfChild(IElement child);

    void deleteElementAndDescendants();
    void deleteElement();
    void deleteChild(IElement child);

    boolean isDescendantOf(IElement ancestor);

    void removeElement();

    void reorderChildren(List<? extends IElement> children);

    void relocateElement(IElement newParent);

    String toString();
    JSONObject toJson() throws JSONException;
}