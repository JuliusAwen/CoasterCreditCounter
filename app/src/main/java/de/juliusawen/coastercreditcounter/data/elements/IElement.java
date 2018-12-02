package de.juliusawen.coastercreditcounter.data.elements;

import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;

public interface IElement
{
    @NonNull
    String toString();

    String getName();
    boolean setName(String name);

    UUID getUuid();
    long getItemId();

    void addChild(IElement child);
    void addChildrenAndSetParents(List<IElement> children);
    void addChildrenAndSetParents(int index, List<IElement> children);
    void addChildAndSetParent(IElement child);
    boolean addChildAndSetParent(int index, IElement child);
    List<IElement> getChildren();
    List<IElement> getChildrenOfType(Class<? extends IElement> type);
    <T extends IElement> List<T> getChildrenAsType(Class<T> type);
    int getChildCountOfType(Class<? extends IElement> type);

    boolean hasChildrenOfType(Class<? extends IElement> type);
    boolean hasChildren();

    void deleteChildren(List<IElement> children);
    void deleteChild(IElement child);
    boolean deleteElementAndChildren();

    boolean removeElement();

    boolean undoIsPossible();
    boolean undoDeleteElementAndChildren();
    boolean undoRemoveElement();


    int getIndexOfChild(IElement child);

    IElement getParent();
    void setParent(IElement parent);
}
