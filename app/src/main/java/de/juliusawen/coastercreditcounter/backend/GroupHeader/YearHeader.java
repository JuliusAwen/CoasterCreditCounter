package de.juliusawen.coastercreditcounter.backend.GroupHeader;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.backend.objects.temporaryElements.ITemporaryElement;
import de.juliusawen.coastercreditcounter.globals.Constants;

public class YearHeader extends GroupHeader implements ITemporaryElement
{
    private YearHeader(String name, UUID uuid)
    {
        super(name, uuid);
    }

    public static YearHeader create(String name)
    {
        YearHeader yearHeader = null;
        if(!name.trim().isEmpty())
        {
            name = name.trim();

            yearHeader = new YearHeader(name, UUID.randomUUID());
            Log.v(Constants.LOG_TAG,  String.format("YearHeader.create:: %s created", yearHeader));
        }
        else
        {
            Log.e(Constants.LOG_TAG,  String.format("YearHeader.create:: invalid name[%s] - yearHeader not created", name));
        }
        return yearHeader;
    }
}
