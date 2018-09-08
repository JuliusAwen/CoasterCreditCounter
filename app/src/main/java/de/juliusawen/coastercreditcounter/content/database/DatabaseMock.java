package de.juliusawen.coastercreditcounter.content.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.Coaster;
import de.juliusawen.coastercreditcounter.content.Content;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.toolbox.Constants;

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

        Location limburg = Location.createLocation("Limburg");

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
        Park cedarPoint = Park.createPark("Cedar Point");
        Park sixFlagsMagicMountain = Park.createPark("Six Flags Magic Mountain");

        Coaster taron = Coaster.createCoaster("Taron");
        Attraction hollywoodTour = Attraction.createAttraction("Hollywood Tour");

        Coaster krake = Coaster.createCoaster("Krake");
        Attraction scream = Attraction.createAttraction("Scream");


        // build tree
        phantasialand.addChild(taron);
        phantasialand.addChild(hollywoodTour);

        heidePark.addChild(krake);
        heidePark.addChild(scream);

        bruehl.addChild(phantasialand);
        soltau.addChild(heidePark);

        northRhineWestphalia.addChild(bruehl);
        lowerSaxony.addChild(soltau);

        germany.addChild(northRhineWestphalia);
        germany.addChild(lowerSaxony);
        germany.addChildren(new ArrayList<Element>(states));

        netherlands.addChild(limburg);

        europe.addChild(germany);
        europe.addChild(netherlands);

        usa.addChild(cedarPoint);
        usa.addChild(sixFlagsMagicMountain);

        earth.addChild(europe);
        earth.addChild(usa);

        content.addElement(taron);



        Location testLocationParent = Location.createLocation("TestParent 2L 2P");
        Location testLocationChild1 = Location.createLocation("TestChild#1 1L 1P");
        Location testLocationChild2 = Location.createLocation("TestChild#2 0L 0P");
        Location testLocationGrandChild = Location.createLocation("TestGrandchild 0L 0P");
        Park testPark1 = Park.createPark("Test Park#1");
        Park testPark2 = Park.createPark("Test Park#2");
        Park testPark3 = Park.createPark("Test Park#3");

        testLocationChild1.addChild(testLocationGrandChild);
        testLocationChild1.addChild(testPark3);
        testLocationParent.addChild(testLocationChild1);
        testLocationParent.addChild(testLocationChild2);
        testLocationParent.addChild(testPark1);
        testLocationParent.addChild(testPark2);

        earth.addChild(testLocationParent);
    }
}
