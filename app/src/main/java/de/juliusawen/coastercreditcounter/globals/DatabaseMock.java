package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.Coaster;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public final class DatabaseMock implements IDatabaseWrapper
{
    private AttractionCategory attractionCategoryThrillRides;
    private AttractionCategory attractionCategoryFamilyRides;
    private AttractionCategory attractionCategoryRollerCoasters;
    private AttractionCategory attractionCategoryNonRollerCoasters;
    private AttractionCategory attractionCategoryWaterRides;

    private static final DatabaseMock instance = new DatabaseMock();

    public static DatabaseMock getInstance()
    {
        return instance;
    }

    private DatabaseMock() {}

    @Override
    public void fetchContent(Content content) {

        //initialize static lists
        this.attractionCategoryThrillRides = AttractionCategory.create("Thrill Rides");
        this.attractionCategoryFamilyRides = AttractionCategory.create("Family Rides");
        this.attractionCategoryRollerCoasters = AttractionCategory.create("RollerCoasters");
        this.attractionCategoryNonRollerCoasters = AttractionCategory.create("Non-Roller Coasters");
        this.attractionCategoryWaterRides = AttractionCategory.create("Water Rides");

        Attraction.addCategory(attractionCategoryRollerCoasters);
        Attraction.addCategory(attractionCategoryThrillRides);
        Attraction.addCategory(attractionCategoryFamilyRides);
        Attraction.addCategory(attractionCategoryWaterRides);
        Attraction.addCategory(attractionCategoryNonRollerCoasters);


        // create Nodes
        Location earth = Location.create("Earth");

        Location europe = Location.create("Europe");
        Location usa = Location.create("USA");

        Location germany = Location.create("Germany");
        Location netherlands = Location.create("Netherlands");

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


        Coaster taron = Coaster.create("Taron");
        Attraction hollywoodTour = Attraction.create("Hollywood Tour");

        taron.setCategory(attractionCategoryRollerCoasters);
        hollywoodTour.setCategory(attractionCategoryWaterRides);


        Coaster krake = Coaster.create("Krake");
        Attraction scream = Attraction.create("Scream");

        krake.setCategory(attractionCategoryRollerCoasters);
        scream.setCategory(attractionCategoryThrillRides);


        Coaster steelVengeance = Coaster.create("Steel Vengeance");
        Coaster valravn = Coaster.create("Valravn");
        Coaster maverick = Coaster.create("Maverick");
        Coaster gatekeeper = Coaster.create("Gatekeeper");
        Attraction dodgem = Attraction.create("Dodgem");

        steelVengeance.setCategory(attractionCategoryRollerCoasters);
        valravn.setCategory(attractionCategoryRollerCoasters);
        maverick.setCategory(attractionCategoryRollerCoasters);
        gatekeeper.setCategory(attractionCategoryRollerCoasters);
        dodgem.setCategory(attractionCategoryNonRollerCoasters);

        Coaster drako = Coaster.create("Drako");
        Coaster elCondor = Coaster.create("El Condor");
        Coaster robinHood = Coaster.create("Robin Hood");
        Coaster speedOfSound = Coaster.create("Speed of Sound");
        Coaster xpressPlatform13 = Coaster.create("Xpress: Platform 13");
        Coaster goliath = Coaster.create("Goliath");
        Coaster lostGravity = Coaster.create("Lost Gravity");

        drako.setCategory(attractionCategoryRollerCoasters);
        elCondor.setCategory(attractionCategoryRollerCoasters);
        robinHood.setCategory(attractionCategoryRollerCoasters);
        speedOfSound.setCategory(attractionCategoryRollerCoasters);
        xpressPlatform13.setCategory(attractionCategoryRollerCoasters);
        goliath.setCategory(attractionCategoryRollerCoasters);
        lostGravity.setCategory(attractionCategoryRollerCoasters);

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
        Attraction squadsStuntFlight = Attraction.create("Squad's Stunt Flight");
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


        germany.addChild(northRhineWestphalia);
        germany.addChild(lowerSaxony);
        germany.addChildren(new ArrayList<Element>(germanStates));
        germany.addChild(bremen);

        netherlands.addChild(biddinghuizen);

        europe.addChild(germany);
        europe.addChild(netherlands);

        cedarPoint.addChild(steelVengeance);
        cedarPoint.addChild(valravn);
        cedarPoint.addChild(maverick);
        cedarPoint.addChild(gatekeeper);

        usa.addChild(cedarPoint);
        usa.addChild(sixFlagsMagicMountain);

        earth.addChild(europe);
        earth.addChild(usa);

        Visit visit1 = Visit.create(2018, 0, 1);
        Visit visit2 = Visit.create(2018, 1, 2);
        Visit visit3 = Visit.create(2018, 2, 3);
        Visit visit4 = Visit.create(2017, 3, 4);
        Visit visit5 = Visit.create(2017, 4, 5);
        Visit visit6 = Visit.create(2016, 5, 6);

        Visit visitToday = Visit.create(Calendar.getInstance());
        heidePark.addChild(visitToday);
        Visit.setOpenVisit(visitToday);

        cedarPoint.addChild(visit6);
        cedarPoint.addChild(visit5);
        cedarPoint.addChild(visit4);
        cedarPoint.addChild(visit3);
        cedarPoint.addChild(visit2);
        cedarPoint.addChild(visit1);

        //add tree to content (one element is enough - content is searching for root on its own)
        content.addElement(earth);
        content.addElements(AttractionCategory.getAttractionCategories());
    }

    @Override
    public void fetchSettings(Settings settings)
    {
        Log.v(Constants.LOG_TAG, "DatabaseMock.fetchSettings:: creating mock data");

        Settings.jumpToTestActivityOnStart = false;
        settings.setJumpToOpenVisitOnStart(false);

//        List<AttractionCategory> attractionCategoriesExpandedByDefault = new ArrayList<>();
//        attractionCategoriesExpandedByDefault.add(this.attractionCategoryRollerCoasters);
//        attractionCategoriesExpandedByDefault.add(this.attractionCategoryNonRollerCoasters);
//        settings.setAttractionCategoriesToExpandByDefault(attractionCategoriesExpandedByDefault);

        settings.setDefaultSortOrderParkVisits(SortOrder.DESCENDING);

        settings.setExpandLatestYearInListByDefault(false);

        settings.setFirstDayOfTheWeek(Calendar.MONDAY);
    }
}
