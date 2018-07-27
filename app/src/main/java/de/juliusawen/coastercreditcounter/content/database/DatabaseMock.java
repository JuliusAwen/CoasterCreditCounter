package de.juliusawen.coastercreditcounter.content.database;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.juliusawen.coastercreditcounter.Toolbox.Constants;
import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.Coaster;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;

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

        List<Location> states = Arrays.asList(
                new Location("Baden-Württemberg", UUID.randomUUID()),
                new Location("Bavaria", UUID.randomUUID()),
                new Location("Berlin", UUID.randomUUID()),
                new Location("Brandenburg", UUID.randomUUID()),
                new Location("Bremen", UUID.randomUUID()),
                new Location("Hamburg", UUID.randomUUID()),
                new Location("Hesse", UUID.randomUUID()),
                new Location("Mecklenburg-Vorpommern", UUID.randomUUID()),
                new Location("Rhineland-Palatinate", UUID.randomUUID()),
                new Location("Saarland", UUID.randomUUID()),
                new Location("Saxony", UUID.randomUUID()),
                new Location("Saxony-Anhalt", UUID.randomUUID()),
                new Location("Schleswig-Holstein", UUID.randomUUID()),
                new Location("Thuringia", UUID.randomUUID())
        );

        Location bruehl = new Location("Brühl", UUID.randomUUID());
        Location soltau = new Location("Soltau", UUID.randomUUID());

        Park phantasialand = new Park("Phantasialand", UUID.randomUUID());
        Park heidePark = new Park("Heide Park", UUID.randomUUID());

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
        germany.addChildren(states);

        europe.addChild(germany);
        europe.addChild(netherlands);

        earth.addChild(europe);
        earth.addChild(usa);


        // do things with tree
        this.putLocationsInAttractions(earth);

        content.setLocationRoot(earth);
    }

    private void putLocationsInAttractions(Location location)
    {
        if (!location.getChildren().isEmpty())
        {
            for (Location child : location.getChildren())
            {
                if(child.getClass().equals(Location.class))
                {
                    this.putLocationsInAttractions(child);
                }
                else if(child.getClass().equals(Park.class))
                {
                    for (Attraction attraction : ((Park) child).getAttractions())
                    {
                        attraction.setLocation(child);
                    }
                }
            }
        }
    }
}
