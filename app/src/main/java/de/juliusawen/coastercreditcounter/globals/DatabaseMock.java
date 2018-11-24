package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.elements.attractions.Attraction;
import de.juliusawen.coastercreditcounter.data.elements.attractions.Coaster;
import de.juliusawen.coastercreditcounter.data.elements.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.data.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public final class DatabaseMock implements IDatabaseWrapper
{
    private AttractionCategory attractionCategoryThrillRides;
    private AttractionCategory attractionCategoryFamilyRides;
    private AttractionCategory attractionCategoryRollerCoasters;
    private AttractionCategory attractionCategoryNonRollerCoasters;
    private AttractionCategory attractionCategoryWaterRides;
    private AttractionCategory defaultAttractionCategory;

    private static final DatabaseMock instance = new DatabaseMock();

    public static DatabaseMock getInstance()
    {
        return instance;
    }

    private DatabaseMock() {}

    @Override
    public void fetchContent(Content content)
    {
        this.attractionCategoryThrillRides = AttractionCategory.create("Thrill Rides");
        this.attractionCategoryFamilyRides = AttractionCategory.create("Family Rides");
        this.attractionCategoryRollerCoasters = AttractionCategory.create("RollerCoasters");
        this.attractionCategoryNonRollerCoasters = AttractionCategory.create("Non-Roller Coasters");
        this.attractionCategoryWaterRides = AttractionCategory.create("Water Rides");

        List<AttractionCategory> attractionCategories = new ArrayList<>();
        attractionCategories.add(attractionCategoryRollerCoasters);
        attractionCategories.add(attractionCategoryThrillRides);
        attractionCategories.add(attractionCategoryFamilyRides);
        attractionCategories.add(attractionCategoryWaterRides);
        attractionCategories.add(attractionCategoryNonRollerCoasters);


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
        StockAttraction hollywoodTour = StockAttraction.create("Hollywood Tour");

        taron.setAttractionCategory(attractionCategoryRollerCoasters);
        hollywoodTour.setAttractionCategory(attractionCategoryWaterRides);


        Coaster krake = Coaster.create("Krake");
        Coaster flugDerDaemonen = Coaster.create("Flug der Dämonen");
        Coaster desertRace = Coaster.create("Desert Race");
        Coaster bigLoop = Coaster.create("Big Loop");
        Coaster limit = Coaster.create("Limit");
        Coaster grottenblitz = Coaster.create("Grottenblitz");
        Coaster indyBlitz = Coaster.create("Indy-Blitz");
        Coaster bobbahn = Coaster.create("Bobbahn");
        Coaster colossos = Coaster.create("Colossos");

        StockAttraction scream = StockAttraction.create("Scream");
        StockAttraction mountainRafting = StockAttraction.create("Mountain Rafting");
        StockAttraction wildwasserbahn = StockAttraction.create("Wildwasserbahn");
        StockAttraction ghostbusters5D = StockAttraction.create("Ghostbusters 5D");
        StockAttraction monorail = StockAttraction.create("Monorail");
        StockAttraction screamie = StockAttraction.create("Screamie");
        StockAttraction bounty = StockAttraction.create("Bounty");

        krake.setAttractionCategory(attractionCategoryRollerCoasters);
        flugDerDaemonen.setAttractionCategory(attractionCategoryRollerCoasters);
        desertRace.setAttractionCategory(attractionCategoryRollerCoasters);
        bigLoop.setAttractionCategory(attractionCategoryRollerCoasters);
        limit.setAttractionCategory(attractionCategoryRollerCoasters);
        grottenblitz.setAttractionCategory(attractionCategoryRollerCoasters);
        indyBlitz.setAttractionCategory(attractionCategoryRollerCoasters);
        bobbahn.setAttractionCategory(attractionCategoryRollerCoasters);
        colossos.setAttractionCategory(attractionCategoryRollerCoasters);

        scream.setAttractionCategory(attractionCategoryThrillRides);

        mountainRafting.setAttractionCategory(attractionCategoryWaterRides);
        wildwasserbahn.setAttractionCategory(attractionCategoryWaterRides);

        ghostbusters5D.setAttractionCategory(attractionCategoryFamilyRides);
        monorail.setAttractionCategory(attractionCategoryFamilyRides);
        screamie.setAttractionCategory(attractionCategoryFamilyRides);
        bounty.setAttractionCategory(attractionCategoryFamilyRides);


        Coaster steelVengeance = Coaster.create("Steel Vengeance");
        Coaster valravn = Coaster.create("Valravn");
        Coaster maverick = Coaster.create("Maverick");
        Coaster gatekeeper = Coaster.create("Gatekeeper");
        StockAttraction dodgem = StockAttraction.create("Dodgem");

        steelVengeance.setAttractionCategory(attractionCategoryRollerCoasters);
        valravn.setAttractionCategory(attractionCategoryRollerCoasters);
        maverick.setAttractionCategory(attractionCategoryRollerCoasters);
        gatekeeper.setAttractionCategory(attractionCategoryRollerCoasters);
        dodgem.setAttractionCategory(attractionCategoryFamilyRides);

        Coaster drako = Coaster.create("Drako");
        Coaster elCondor = Coaster.create("El Condor");
        Coaster robinHood = Coaster.create("Robin Hood");
        Coaster speedOfSound = Coaster.create("Speed of Sound");
        Coaster xpressPlatform13 = Coaster.create("Xpress: Platform 13");
        Coaster goliath = Coaster.create("Goliath");
        Coaster lostGravity = Coaster.create("Lost Gravity");

        drako.setAttractionCategory(attractionCategoryRollerCoasters);
        elCondor.setAttractionCategory(attractionCategoryRollerCoasters);
        robinHood.setAttractionCategory(attractionCategoryRollerCoasters);
        speedOfSound.setAttractionCategory(attractionCategoryRollerCoasters);
        xpressPlatform13.setAttractionCategory(attractionCategoryRollerCoasters);
        goliath.setAttractionCategory(attractionCategoryRollerCoasters);
        lostGravity.setAttractionCategory(attractionCategoryRollerCoasters);

        StockAttraction excalibur = StockAttraction.create("Excalibur");
        StockAttraction gForce = StockAttraction.create("G-Force");
        StockAttraction spaceShot = StockAttraction.create("Space Shot");
        StockAttraction spinningVibe = StockAttraction.create("Spinning Vibe");
        StockAttraction skydiver = StockAttraction.create("Skydiver");
        StockAttraction theTomahawk = StockAttraction.create("The Tomahawk");

        excalibur.setAttractionCategory(attractionCategoryThrillRides);
        gForce.setAttractionCategory(attractionCategoryThrillRides);
        spaceShot.setAttractionCategory(attractionCategoryThrillRides);
        spinningVibe.setAttractionCategory(attractionCategoryThrillRides);
        skydiver.setAttractionCategory(attractionCategoryThrillRides);
        theTomahawk.setAttractionCategory(attractionCategoryThrillRides);

        StockAttraction fibisBubbleSwirl = StockAttraction.create("Fibi's Bubble Swirl");
        StockAttraction haazGarage = StockAttraction.create("Haaz Garage");
        StockAttraction laGrandeRoue = StockAttraction.create("La Grande Roue");
        StockAttraction leTourDesJardins = StockAttraction.create("Le Tour Des Jardins");
        StockAttraction losSombreros = StockAttraction.create("Los Sombreros");
        StockAttraction merlinsMagicCastle = StockAttraction.create("Merlin's Magic Castle");
        StockAttraction merrieGoround = StockAttraction.create("Merrie Go'round");
        StockAttraction pavillonDeThe = StockAttraction.create("Pavillon de Thè");
        StockAttraction spaceKidz = StockAttraction.create("Space Kidz");
        StockAttraction superSwing = StockAttraction.create("Super Swing");
        StockAttraction squadsStuntFlight = StockAttraction.create("Squad's Stunt Flight");
        StockAttraction tequillaTaxis = StockAttraction.create("Tequilla Taxi's");
        StockAttraction wabWorldTour = StockAttraction.create("WAB World Tour");
        StockAttraction walibiExpress = StockAttraction.create("Walibi Express");
        StockAttraction walibisFunRecorder = StockAttraction.create("Walibi's Fun Recorder");
        StockAttraction zensGraffityShuttle = StockAttraction.create("Zen's Graffity Shuttle");

        fibisBubbleSwirl.setAttractionCategory(attractionCategoryFamilyRides);
        haazGarage.setAttractionCategory(attractionCategoryFamilyRides);
        laGrandeRoue.setAttractionCategory(attractionCategoryFamilyRides);
        leTourDesJardins.setAttractionCategory(attractionCategoryFamilyRides);
        losSombreros.setAttractionCategory(attractionCategoryFamilyRides);
        merlinsMagicCastle.setAttractionCategory(attractionCategoryFamilyRides);
        merrieGoround.setAttractionCategory(attractionCategoryFamilyRides);
        pavillonDeThe.setAttractionCategory(attractionCategoryFamilyRides);
        spaceKidz.setAttractionCategory(attractionCategoryFamilyRides);
        superSwing.setAttractionCategory(attractionCategoryFamilyRides);
        squadsStuntFlight.setAttractionCategory(attractionCategoryFamilyRides);
        tequillaTaxis.setAttractionCategory(attractionCategoryFamilyRides);
        wabWorldTour.setAttractionCategory(attractionCategoryFamilyRides);
        walibiExpress.setAttractionCategory(attractionCategoryFamilyRides);
        walibisFunRecorder.setAttractionCategory(attractionCategoryFamilyRides);
        zensGraffityShuttle.setAttractionCategory(attractionCategoryFamilyRides);

        StockAttraction crazyRiver = StockAttraction.create("Crazy River");
        StockAttraction elRioGrande = StockAttraction.create("El Rio Grande");
        StockAttraction splashBattle = StockAttraction.create("SplashBattle");

        crazyRiver.setAttractionCategory(attractionCategoryWaterRides);
        elRioGrande.setAttractionCategory(attractionCategoryWaterRides);
        splashBattle.setAttractionCategory(attractionCategoryWaterRides);

        // build tree
        phantasialand.addChildAndSetParent(taron);
        phantasialand.addChildAndSetParent(hollywoodTour);


        heidePark.addChildAndSetParent(krake);
        heidePark.addChildAndSetParent(flugDerDaemonen);
        heidePark.addChildAndSetParent(desertRace);
        heidePark.addChildAndSetParent(bigLoop);
        heidePark.addChildAndSetParent(limit);
        heidePark.addChildAndSetParent(grottenblitz);
        heidePark.addChildAndSetParent(indyBlitz);
        heidePark.addChildAndSetParent(bobbahn);
        heidePark.addChildAndSetParent(colossos);
        heidePark.addChildAndSetParent(monorail);
        heidePark.addChildAndSetParent(mountainRafting);
        heidePark.addChildAndSetParent(wildwasserbahn);
        heidePark.addChildAndSetParent(ghostbusters5D);
        heidePark.addChildAndSetParent(screamie);
        heidePark.addChildAndSetParent(bounty);
        heidePark.addChildAndSetParent(scream);

        walibiHolland.addChildAndSetParent(drako);
        walibiHolland.addChildAndSetParent(elCondor);
        walibiHolland.addChildAndSetParent(robinHood);
        walibiHolland.addChildAndSetParent(speedOfSound);
        walibiHolland.addChildAndSetParent(xpressPlatform13);
        walibiHolland.addChildAndSetParent(goliath);
        walibiHolland.addChildAndSetParent(lostGravity);

        walibiHolland.addChildAndSetParent(excalibur);
        walibiHolland.addChildAndSetParent(gForce);
        walibiHolland.addChildAndSetParent(spaceShot);
        walibiHolland.addChildAndSetParent(spinningVibe);
        walibiHolland.addChildAndSetParent(skydiver);
        walibiHolland.addChildAndSetParent(theTomahawk);

        walibiHolland.addChildAndSetParent(fibisBubbleSwirl);
        walibiHolland.addChildAndSetParent(haazGarage);
        walibiHolland.addChildAndSetParent(laGrandeRoue);
        walibiHolland.addChildAndSetParent(leTourDesJardins);
        walibiHolland.addChildAndSetParent(losSombreros);
        walibiHolland.addChildAndSetParent(merlinsMagicCastle);
        walibiHolland.addChildAndSetParent(merrieGoround);
        walibiHolland.addChildAndSetParent(pavillonDeThe);
        walibiHolland.addChildAndSetParent(spaceKidz);
        walibiHolland.addChildAndSetParent(superSwing);
        walibiHolland.addChildAndSetParent(squadsStuntFlight);
        walibiHolland.addChildAndSetParent(tequillaTaxis);
        walibiHolland.addChildAndSetParent(wabWorldTour);
        walibiHolland.addChildAndSetParent(walibiExpress);
        walibiHolland.addChildAndSetParent(walibisFunRecorder);
        walibiHolland.addChildAndSetParent(zensGraffityShuttle);

        walibiHolland.addChildAndSetParent(crazyRiver);
        walibiHolland.addChildAndSetParent(elRioGrande);
        walibiHolland.addChildAndSetParent(splashBattle);

        bruehl.addChildAndSetParent(phantasialand);
        soltau.addChildAndSetParent(heidePark);
        bremen.addChildAndSetParent(freimarkt);
        bremen.addChildAndSetParent(osterwiese);
        biddinghuizen.addChildAndSetParent(walibiHolland);

        northRhineWestphalia.addChildAndSetParent(bruehl);
        lowerSaxony.addChildAndSetParent(soltau);


        germany.addChildAndSetParent(northRhineWestphalia);
        germany.addChildAndSetParent(lowerSaxony);
        germany.addChildrenAndSetParents(new ArrayList<Element>(germanStates));
        germany.addChildAndSetParent(bremen);

        netherlands.addChildAndSetParent(biddinghuizen);

        europe.addChildAndSetParent(germany);
        europe.addChildAndSetParent(netherlands);

        cedarPoint.addChildAndSetParent(steelVengeance);
        cedarPoint.addChildAndSetParent(valravn);
        cedarPoint.addChildAndSetParent(maverick);
        cedarPoint.addChildAndSetParent(gatekeeper);

        usa.addChildAndSetParent(cedarPoint);
        usa.addChildAndSetParent(sixFlagsMagicMountain);

        earth.addChildAndSetParent(europe);
        earth.addChildAndSetParent(usa);

        Visit visit0 = Visit.create(2018, 2, 30);
        this.addAttractionsToVisit(visit0, Element.convertElementsToType(heidePark.getChildrenAsType(Coaster.class), Attraction.class));
        heidePark.addChildAndSetParent(visit0);

        Visit visit1 = Visit.create(2018, 0, 1);
        Visit visit2 = Visit.create(2018, 1, 2);
        Visit visit3 = Visit.create(2018, 2, 3);
        Visit visit4 = Visit.create(2017, 3, 4);
        Visit visit5 = Visit.create(2017, 4, 5);
        Visit visit6 = Visit.create(2016, 5, 6);
        cedarPoint.addChildAndSetParent(visit6);
        cedarPoint.addChildAndSetParent(visit5);
        cedarPoint.addChildAndSetParent(visit4);
        cedarPoint.addChildAndSetParent(visit3);
        cedarPoint.addChildAndSetParent(visit2);
        cedarPoint.addChildAndSetParent(visit1);

        Visit visit7 = Visit.create(2019, 0, 1);
        this.addAttractionsToVisit(visit7, walibiHolland.getChildrenAsType(Attraction.class));
        walibiHolland.addChildAndSetParent(visit7);

        Visit visitToday = Visit.create(Calendar.getInstance());
        freimarkt.addChildAndSetParent(visitToday);
        Visit.setOpenVisit(visitToday);



        //add tree to content (one element is enough - content is searching for root on its own and flattens tree from there)
        content.addElement(earth);

        content.setAttractionCategories(attractionCategories);

        this.addDefaults(content);
    }

    private void addAttractionsToVisit(Visit visit, List<Attraction> attractions)
    {
        for(Attraction attraction : attractions)
        {
            visit.addChildAndSetParent(VisitedAttraction.create((StockAttraction)attraction));
        }
    }

    private void addDefaults(Content content)
    {
        this.defaultAttractionCategory = AttractionCategory.create("uncategorized");
        content.addAttractionCategory(defaultAttractionCategory);
    }

    @Override
    public void fetchSettings(Settings settings)
    {
        Log.v(Constants.LOG_TAG, "DatabaseMock.fetchSettings:: creating mock data");

        Settings.jumpToTestActivityOnStart = false;
//        settings.setJumpToOpenVisitOnStart(false);

        settings.setDefaultSortOrderParkVisits(SortOrder.DESCENDING);

        settings.setExpandLatestYearInListByDefault(true);

        settings.setFirstDayOfTheWeek(Calendar.MONDAY);

        settings.setDefaultAttractionCategory(this.defaultAttractionCategory);
    }
}
