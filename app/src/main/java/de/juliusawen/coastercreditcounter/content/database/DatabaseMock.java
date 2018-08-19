package de.juliusawen.coastercreditcounter.content.database;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

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
        Location earth = Location.createLocation("Earth");

        Location europe = Location.createLocation("Europe");
        Location usa = Location.createLocation("USA");

        Location germany = Location.createLocation("Germany");
        Location netherlands = Location.createLocation("Netherlands");

        Location northRhineWestphalia = Location.createLocation("North Rhine-Westphalia");
        Location lowerSaxony = Location.createLocation("Lower Saxony");
        List<Location> states = Arrays.asList(
                Location.createLocation("Baden-Württemberg"),
                Location.createLocation("Bavaria"),
                Location.createLocation("Berlin"),
                Location.createLocation("Brandenburg"),
                Location.createLocation("Bremen"),
                Location.createLocation("Hamburg"),
                Location.createLocation("Hesse"),
                Location.createLocation("Mecklenburg-Vorpommern"),
                Location.createLocation("Rhineland-Palatinate"),
                Location.createLocation("Saarland"),
                Location.createLocation("Saxony"),
                Location.createLocation("Saxony-Anhalt"),
                Location.createLocation("Schleswig-Holstein"),
                Location.createLocation("Thuringia")
        );

        Location bruehl = Location.createLocation("Brühl");
        Location soltau = Location.createLocation("Soltau");

        Park phantasialand = Park.createPark("Phantasialand");
        Park heidePark = Park.createPark("Heide Park Resort");

        Coaster taron = Coaster.createCoaster("Taron");
        Attraction hollywoodTour = Attraction.createAttraction("Hollywood Tour");

        Coaster krake = Coaster.createCoaster("Krake");
        Attraction scream = Attraction.createAttraction("Scream");


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
