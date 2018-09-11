package de.juliusawen.coastercreditcounter.content;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.globals.Constants;

public class Park extends Element
{
    public static List<String> types = new ArrayList<>();
    private String type;

    private Park(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static Park createPark(@NonNull String name)
    {
        Park park = null;

        if(!name.trim().isEmpty())
        {
            name = name.trim();

            park = new Park(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("Park.createPark:: %s created", park.getFullName()));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Park.createPark:: invalid name[%s] - park not created", name));
        }

        return park;
    }

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        if(Park.types.contains(type))
        {
            this.type = type;
            Log.v(Constants.LOG_TAG,  String.format("Park.setType:: set type [%s] to %s", type, this));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("Park.setType:: invalid type [%s]", type));
        }
    }
}
