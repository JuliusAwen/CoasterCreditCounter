package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class TemporaryElement extends Element
{
    TemporaryElement(String name)
    {
        super(name, null);
    }

    protected static TemporaryElement createTemporaryElement(String name)
    {
        TemporaryElement temporaryElement = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            temporaryElement = new TemporaryElement(name);
            Log.v(Constants.LOG_TAG,  String.format("TemporaryElement.createTemporaryElement:: %s created.", temporaryElement));
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("TemporaryElement.createTemporaryElement:: invalid name[%s] - temporaryElement not created.", name));
        }

        return temporaryElement;
    }
}
