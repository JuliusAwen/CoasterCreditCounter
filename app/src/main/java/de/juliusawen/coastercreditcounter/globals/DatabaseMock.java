package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.juliusawen.coastercreditcounter.content.Attraction;
import de.juliusawen.coastercreditcounter.content.AttractionCategory;
import de.juliusawen.coastercreditcounter.content.Coaster;
import de.juliusawen.coastercreditcounter.content.Element;
import de.juliusawen.coastercreditcounter.content.Location;
import de.juliusawen.coastercreditcounter.content.Park;
import de.juliusawen.coastercreditcounter.content.Visit;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public final class DatabaseMock implements IDatabaseWrapper
{
    private AttractionCategory attractionCategoryRollerCoasters = AttractionCategory.create("RollerCoasters");
    private AttractionCategory attractionCategoryNonRollerCoasters = AttractionCategory.create("Non-Roller Coasters");

    @Override
    public void fetchContent(Content content)
    {
        Log.v(Constants.LOG_TAG, "DatabaseMock.fetchContent:: creating mock data");

        //initialize static lists
        String parkTypeAmusementPark = "Amusement Park";
        String parkTypeThemePark = "Theme Park";
        String parkTypeFair = "Fair";

        Park.addType(parkTypeAmusementPark);
        Park.addType(parkTypeThemePark);
        Park.addType(parkTypeFair);


        AttractionCategory attractionCategoryThrillRides = AttractionCategory.create("Thrill Rides");
        AttractionCategory attractionCategoryFamilyRides = AttractionCategory.create("Family Rides");
        AttractionCategory attractionCategoryWaterRides = AttractionCategory.create("Water Rides");

        Attraction.addCategory(this.attractionCategoryRollerCoasters);
        Attraction.addCategory(attractionCategoryThrillRides);
        Attraction.addCategory(attractionCategoryFamilyRides);
        Attraction.addCategory(attractionCategoryWaterRides);
        Attraction.addCategory(this.attractionCategoryNonRollerCoasters);

        // create Nodes
        Location earth = Location.create("Earth");

        Location europe = Location.create("Europe");
        Location usa = Location.create("USA");

        Location germany = Location.create("Germany");
        Location netherlands = Location.create("Netherlands");

        Location limburg = Location.create("Limburg");
        Location flevoland = Location.create("Flevoland");

        Location northRhineWestphalia = Location.create("North Rhine-Westphalia");
        Location lowerSaxony = Location.create("Lower Saxony");
        List<Location> germanStates = Arrays.asList(
                Location.create("Baden-Württemberg"),
                Location.create("Bavaria"),
                Location.create("Berlin"),
                Location.create("Brandenburg"),
                Location.create("Hamburg"),
                Location.create("Hesse"),
                Location.create("Mecklenburg-Vorpommern"),
                Location.create("Rhineland-Palatinate"),
                Location.create("Saarland"),
                Location.create("Saxony"),
                Location.create("Saxony-Anhalt"),
                Location.create("Schleswig-Holstein"),
                Location.create("Thuringia")
        );

        Location bruehl = Location.create("Brühl");
        Location soltau = Location.create("Soltau");
        Location bremen = Location.create("Bremen");

        Location biddinghuizen = Location.create("Biddinghuizen");

        Park phantasialand = Park.create("Phantasialand");
        Park heidePark = Park.create("Heide Park Resort");
        Park freimarkt = Park.create("Freimarkt");
        Park osterwiese = Park.create("Osterwiese");

        Park cedarPoint = Park.create("Cedar Point");
        Park sixFlagsMagicMountain = Park.create("Six Flags Magic Mountain");

        Park walibiHolland = Park.create("Walibi Holland");

        phantasialand.setType(parkTypeThemePark);
        heidePark.setType(parkTypeThemePark);
        freimarkt.setType(parkTypeFair);
        osterwiese.setType(parkTypeFair);

        cedarPoint.setType(parkTypeAmusementPark);
        sixFlagsMagicMountain.setType(parkTypeThemePark);

        walibiHolland.setType(parkTypeAmusementPark);


        Coaster taron = Coaster.create("Taron");
        Attraction hollywoodTour = Attraction.create("Hollywood Tour");

        taron.setCategory(this.attractionCategoryRollerCoasters);
        hollywoodTour.setCategory(attractionCategoryWaterRides);


        Coaster krake = Coaster.create("Krake");
        Attraction scream = Attraction.create("Scream");

        krake.setCategory(this.attractionCategoryRollerCoasters);
        scream.setCategory(attractionCategoryThrillRides);


        Coaster steelVengeance = Coaster.create("Steel Vengeance");
        Coaster valravn = Coaster.create("Valravn");
        Coaster maverick = Coaster.create("Maverick");
        Coaster gatekeeper = Coaster.create("Gatekeeper");
        Attraction dodgem = Attraction.create("Dodgem");

        steelVengeance.setCategory(this.attractionCategoryRollerCoasters);
        valravn.setCategory(this.attractionCategoryRollerCoasters);
        maverick.setCategory(this.attractionCategoryRollerCoasters);
        gatekeeper.setCategory(this.attractionCategoryRollerCoasters);
        dodgem.setCategory(this.attractionCategoryNonRollerCoasters);

        Coaster drako = Coaster.create("Drako");
        Coaster elCondor = Coaster.create("El Condor");
        Coaster robinHood = Coaster.create("Robin Hood");
        Coaster speedOfSound = Coaster.create("Speed of Sound");
        Coaster xpressPlatform13 = Coaster.create("Xpress: Platform 13");
        Coaster goliath = Coaster.create("Goliath");
        Coaster lostGravity = Coaster.create("Lost Gravity");

        drako.setCategory(this.attractionCategoryRollerCoasters);
        elCondor.setCategory(this.attractionCategoryRollerCoasters);
        robinHood.setCategory(this.attractionCategoryRollerCoasters);
        speedOfSound.setCategory(this.attractionCategoryRollerCoasters);
        xpressPlatform13.setCategory(this.attractionCategoryRollerCoasters);
        goliath.setCategory(this.attractionCategoryRollerCoasters);
        lostGravity.setCategory(this.attractionCategoryRollerCoasters);

        Attraction excalibur = Attraction.create("Excalibur");
        Attraction gForce = Attraction.create("G-Force");
        Attraction spaceShot = Attraction.create("Space Shot");
        Attraction spinningVibe = Attraction.create("Spinning Vibe");
        Attraction skydiver = Attraction.create("Skydiver");
        Attraction theTomahawk = Attraction.create("The Tomahawk");

        excalibur.setCategory(attractionCategoryThrillRides);
        gForce.setCategory(attractionCategoryThrillRides);
        spaceShot.setCategory(attractionCategoryThrillRides);
        spinningVibe.setCategory(attractionCategoryThrillRides);
        skydiver.setCategory(attractionCategoryThrillRides);
        theTomahawk.setCategory(attractionCategoryThrillRides);

        Attraction fibisBubbleSwirl = Attraction.create("Fibi's Bubble Swirl");
        Attraction haazGarage = Attraction.create("Haaz Garage");
        Attraction laGrandeRoue = Attraction.create("La Grande Roue");
        Attraction leTourDesJardins = Attraction.create("Le Tour Des Jardins");
        Attraction losSombreros = Attraction.create("Los Sombreros");
        Attraction merlinsMagicCastle = Attraction.create("Merlin's Magic Castle");
        Attraction merrieGoround = Attraction.create("Merrie Go'round");
        Attraction pavillonDeThe = Attraction.create("Pavillon de Thè");
        Attraction spaceKidz = Attraction.create("Space Kidz");
        Attraction superSwing = Attraction.create("Super Swing");
        Attraction squadsStuntFlight = Attraction.create("Squad's Stund Flight");
        Attraction tequillaTaxis = Attraction.create("Tequilla Taxi's");
        Attraction wabWorldTour = Attraction.create("WAB World Tour");
        Attraction walibiExpress = Attraction.create("Walibi Express");
        Attraction walibisFunRecorder = Attraction.create("Walibi's Fun Recorder");
        Attraction zensGraffityShuttle = Attraction.create("Zen's Graffity Shuttle");

        fibisBubbleSwirl.setCategory(attractionCategoryFamilyRides);
        haazGarage.setCategory(attractionCategoryFamilyRides);
        laGrandeRoue.setCategory(attractionCategoryFamilyRides);
        leTourDesJardins.setCategory(attractionCategoryFamilyRides);
        losSombreros.setCategory(attractionCategoryFamilyRides);
        merlinsMagicCastle.setCategory(attractionCategoryFamilyRides);
        merrieGoround.setCategory(attractionCategoryFamilyRides);
        pavillonDeThe.setCategory(attractionCategoryFamilyRides);
        spaceKidz.setCategory(attractionCategoryFamilyRides);
        superSwing.setCategory(attractionCategoryFamilyRides);
        squadsStuntFlight.setCategory(attractionCategoryFamilyRides);
        tequillaTaxis.setCategory(attractionCategoryFamilyRides);
        wabWorldTour.setCategory(attractionCategoryFamilyRides);
        walibiExpress.setCategory(attractionCategoryFamilyRides);
        walibisFunRecorder.setCategory(attractionCategoryFamilyRides);
        zensGraffityShuttle.setCategory(attractionCategoryFamilyRides);

        Coaster crazyRiver = Coaster.create("Crazy River");
        Attraction elRioGrande = Attraction.create("El Rio Grande");
        Attraction splashBattle = Attraction.create("SplashBattle");

        crazyRiver.setCategory(attractionCategoryWaterRides);
        elRioGrande.setCategory(attractionCategoryWaterRides);
        splashBattle.setCategory(attractionCategoryWaterRides);


        Visit visit1 = Visit.create(2018, 0, 1);
        Visit visit2 = Visit.create(2018, 1, 2);
        Visit visit3 = Visit.create(2018, 2, 3);
        Visit visit4 = Visit.create(2017, 3, 4);
        Visit visit5 = Visit.create(2017, 4, 5);
        Visit visit6 = Visit.create(2016, 5, 6);



        // build tree
        phantasialand.addChild(taron);
        phantasialand.addChild(hollywoodTour);

        heidePark.addChild(krake);
        heidePark.addChild(scream);

        walibiHolland.addChild(drako);
        walibiHolland.addChild(elCondor);
        walibiHolland.addChild(robinHood);
        walibiHolland.addChild(speedOfSound);
        walibiHolland.addChild(xpressPlatform13);
        walibiHolland.addChild(goliath);
        walibiHolland.addChild(lostGravity);

        walibiHolland.addChild(excalibur);
        walibiHolland.addChild(gForce);
        walibiHolland.addChild(spaceShot);
        walibiHolland.addChild(spinningVibe);
        walibiHolland.addChild(skydiver);
        walibiHolland.addChild(theTomahawk);

        walibiHolland.addChild(fibisBubbleSwirl);
        walibiHolland.addChild(haazGarage);
        walibiHolland.addChild(laGrandeRoue);
        walibiHolland.addChild(leTourDesJardins);
        walibiHolland.addChild(losSombreros);
        walibiHolland.addChild(merlinsMagicCastle);
        walibiHolland.addChild(merrieGoround);
        walibiHolland.addChild(pavillonDeThe);
        walibiHolland.addChild(spaceKidz);
        walibiHolland.addChild(superSwing);
        walibiHolland.addChild(squadsStuntFlight);
        walibiHolland.addChild(tequillaTaxis);
        walibiHolland.addChild(wabWorldTour);
        walibiHolland.addChild(walibiExpress);
        walibiHolland.addChild(walibisFunRecorder);
        walibiHolland.addChild(zensGraffityShuttle);

        walibiHolland.addChild(crazyRiver);
        walibiHolland.addChild(elRioGrande);
        walibiHolland.addChild(splashBattle);

        bruehl.addChild(phantasialand);
        soltau.addChild(heidePark);
        bremen.addChild(freimarkt);
        bremen.addChild(osterwiese);
        biddinghuizen.addChild(walibiHolland);

        northRhineWestphalia.addChild(bruehl);
        lowerSaxony.addChild(soltau);

        flevoland.addChild(biddinghuizen);

        germany.addChild(northRhineWestphalia);
        germany.addChild(lowerSaxony);
        germany.addChildren(new ArrayList<Element>(germanStates));
        germany.addChild(bremen);

        netherlands.addChild(limburg);
        netherlands.addChild(flevoland);

        europe.addChild(germany);
        europe.addChild(netherlands);

        cedarPoint.addChild(steelVengeance);
        cedarPoint.addChild(valravn);
        cedarPoint.addChild(maverick);
        cedarPoint.addChild(gatekeeper);

        cedarPoint.addChild(visit6);
        cedarPoint.addChild(visit5);
        cedarPoint.addChild(visit4);
        cedarPoint.addChild(visit3);
        cedarPoint.addChild(visit2);
        cedarPoint.addChild(visit1);

        usa.addChild(cedarPoint);
        usa.addChild(sixFlagsMagicMountain);

        earth.addChild(europe);
        earth.addChild(usa);

        content.addElement(earth);
        content.addElements(Attraction.getCategories());


        if(false)
        {
            Location testLocationParent = Location.create("TestParent 2L 2P");
            Location testLocationChild1 = Location.create("TestChild#1 1L 1P");
            Location testLocationChild2 = Location.create("TestChild#2 0L 0P");
            Location testLocationGrandChild = Location.create("TestGrandchild 0L 0P");
            Park testPark1 = Park.create("Test Park#1");
            Park testPark2 = Park.create("Test Park#2");
            Park testPark3 = Park.create("Test Park#3");

            testLocationChild1.addChild(testLocationGrandChild);
            testLocationChild1.addChild(testPark3);
            testLocationParent.addChild(testLocationChild1);
            testLocationParent.addChild(testLocationChild2);
            testLocationParent.addChild(testPark1);
            testLocationParent.addChild(testPark2);

            earth.addChild(testLocationParent);
        }
    }

    @Override
    public void fetchSettings(Settings settings)
    {
        List<AttractionCategory> attractionCategoriesExpandedByDefault = new ArrayList<>();
        attractionCategoriesExpandedByDefault.add(this.attractionCategoryRollerCoasters);
        attractionCategoriesExpandedByDefault.add(this.attractionCategoryNonRollerCoasters);
        settings.setCategoriesExpandedByDefault(attractionCategoriesExpandedByDefault);

        settings.setSortOrderVisits(SortOrder.DESCENDING);

        settings.setExpandLatestYearInListByDefault(true);
    }
}
