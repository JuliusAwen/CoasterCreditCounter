package de.juliusawen.coastercreditcounter.dataModel.elements.attractions;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.IHasNote;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;

/**
 * Abstract base class for all OnSiteAttractions containing all base methods.<br>
 * Can be CustomAttraction<br>
 */
public abstract class OnSiteAttraction extends Attraction implements IOnSiteAttraction, IHasNote
{
    private Note note;

    protected OnSiteAttraction(String name, int untrackedRideCount, UUID uuid)
    {
        super(name, untrackedRideCount, uuid);
    }

    @Override
    public Note getNote()
    {
        if(this.note == null && this.hasChildrenOfType(Note.class))
        {
            this.note = this.getChildrenAsType(Note.class).get(0);
        }
        return this.note;
    }

    @Override
    public void deleteChild(IElement child)
    {
        if(child.equals(this.note))
        {
            Log.v(Constants.LOG_TAG, String.format("OnSiteAttraction.deleteChild:: setting private field %s on %s to NULL", this.note, this));
            this.note = null;
        }

        super.deleteChild(child);
    }
}
