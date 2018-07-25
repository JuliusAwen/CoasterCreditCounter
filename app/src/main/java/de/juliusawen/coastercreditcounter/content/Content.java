package de.juliusawen.coastercreditcounter.content;

import android.util.Log;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.database.DatabaseMock;

public class Content
{
    public Location locationRoot;

    private static final Content instance = new Content();

    public static Content getInstance()
    {
        return instance;
    }

    private Content()
    {
        Log.v(Constants.LOG_TAG, this.getClass().toString() + ":: Constructor called.");

        new DatabaseMock().fetchContent(this);
    }
}
