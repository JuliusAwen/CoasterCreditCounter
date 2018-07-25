package de.juliusawen.coastercreditcounter.content.database;

import android.util.Log;

import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.Coaster;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Location;

public final class DatabaseMock implements IDatabaseWrapper
{
    @Override
    public void fetchContent(Content content)
    {
        Log.v(Constants.LOG_TAG, this.getClass().toString() + ":: fetchContent called.");

        // create Nodes
        Location earth = new Location("Earth", UUID.randomUUID());

        Location europe = new Location("Europe", UUID.randomUUID());
        Location usa = new Location("USA", UUID.randomUUID());

        Location germany = new Location("Germany", UUID.randomUUID());
        Location netherlands = new Location("Netherlands", UUID.randomUUID());

        Location northRhineWestphalia = new Location("North Rhine-Westphalia", UUID.randomUUID());
        Location lowerSaxony = new Location("Lower Saxony", UUID.randomUUID());

        Location bruehl = new Location("Brühl", UUID.randomUUID());
        Location soltau = new Location("Soltau", UUID.randomUUID());

        Location phantasialand = new Location("Phantasialand", UUID.randomUUID());
        Location heidePark = new Location("Heide Park", UUID.randomUUID());

        Coaster taron = new Coaster("Taron", UUID.randomUUID());
        Attraction hollywoodTour = new Attraction("Hollywood Tour", UUID.randomUUID());

        Coaster krake = new Coaster("Krake", UUID.randomUUID());
        Attraction scream = new Attraction("Scream", UUID.randomUUID());


        // build tree
        phantasialand.addAttraction(taron);
        phantasialand.addAttraction(hollywoodTour);

        heidePark.addAttraction(krake);
        heidePark.addAttraction(scream);

        bruehl.addChild(phantasialand);
        soltau.addChild(heidePark);

        northRhineWestphalia.addChild(bruehl);
        lowerSaxony.addChild(soltau);

        germany.addChild(northRhineWestphalia);
        germany.addChild(lowerSaxony);

        europe.addChild(germany);
        europe.addChild(netherlands);

        earth.addChild(europe);
        earth.addChild(usa);

        content.locationRoot = earth;
    }
}
