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
    List<IElement> getChildren();
    List<IElement> getChildrenOfType(Class<? extends IElement> type);
    int getChildCountOfType(Class<? extends IElement> type);
    boolean hasChildrenOfType(Class<? extends IElement> type);
    boolean hasChildren();

    Element getParent();
    void setParent(Element parent);
}
