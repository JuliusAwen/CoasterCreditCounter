package de.juliusawen.coastercreditcounter.dataModel.elements;

import java.util.List;
import java.util.UUID;

/**
 * Base interface for all Elements
 */
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
    void addChildAtIndexAndSetParent(int index, IElement child);
    void addChildrenAndSetParent(List<UUID> childUuids);
    void addChildrenAtIndexAndSetParent(int index, List<IElement> children);

    List<IElement> getChildren();
    List<IElement> fetchChildrenOfType(Class<? extends IElement> type);
    <T extends IElement> List<T> fetchChildrenAsType(Class<T> type);

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

    boolean isLocation();
    boolean isRootLocation();
    boolean isPark();
    boolean isVisit();
    boolean isAttraction();
    boolean isCustomAttraction();
    boolean isBlueprint();
    boolean isStockAttraction();
    boolean isVisitedAttraction();
    boolean isProperty();
    boolean hasCreditType();
    boolean hasCategory();
    boolean hasManufacturer();
    boolean hasStatus();
    boolean isIPersistable();
    boolean isGroupHeader();
    boolean isOrphan();
}