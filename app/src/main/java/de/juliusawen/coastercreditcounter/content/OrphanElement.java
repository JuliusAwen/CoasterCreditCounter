package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public abstract class OrphanElement extends Element
{
    OrphanElement(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static void removeAllChildren(List<? extends OrphanElement> orphanElements)
    {
        for(OrphanElement orphanElement : orphanElements)
        {
            orphanElement.getChildren().clear();
        }
        Log.v(Constants.LOG_TAG,  String.format("OrphanElement.removeAllChildren:: #[%d] children removed", orphanElements.size()));
    }
}
