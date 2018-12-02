package de.juliusawen.coastercreditcounter.globals;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.juliusawen.coastercreditcounter.data.attractions.CoasterBlueprint;
import de.juliusawen.coastercreditcounter.data.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.data.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.data.attractions.IBlueprint;
import de.juliusawen.coastercreditcounter.data.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.data.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.data.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.data.elements.Element;
import de.juliusawen.coastercreditcounter.data.elements.IElement;
import de.juliusawen.coastercreditcounter.data.elements.Location;
import de.juliusawen.coastercreditcounter.data.elements.Park;
import de.juliusawen.coastercreditcounter.data.elements.Visit;
import de.juliusawen.coastercreditcounter.data.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.globals.enums.SortOrder;

public final class DatabaseMock implements IDatabaseWrapper
{
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
        AttractionCategory attractionCategoryThrillRides = AttractionCategory.create("Thrill Rides");
        AttractionCategory attractionCategoryFamilyRides = AttractionCategory.create("Family Rides");
        AttractionCategory attractionCategoryRollerCoasters = AttractionCategory.create("RollerCoasters");
        AttractionCategory attractionCategoryNonRollerCoasters = AttractionCategory.create("Non-Roller Coasters");
        AttractionCategory attractionCategoryWaterRides = AttractionCategory.create("Water Rides");
        AttractionCategory attractionCategoryDefault = AttractionCategory.create("uncategorized");

        List<AttractionCategory> attractionCategories = new ArrayList<>();
        attractionCategories.add(attractionCategoryRollerCoasters);
        attractionCategories.add(attractionCategoryThrillRides);
        attractionCategories.add(attractionCategoryFamilyRides);
        attractionCategories.add(attractionCategoryWaterRides);
        attractionCategories.add(attractionCategoryNonRollerCoasters);
        attractionCategories.add(attractionCategoryDefault);


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


        CustomCoaster taron = CustomCoaster.create("Taron");
        CustomAttraction hollywoodTour = CustomAttraction.create("Hollywood Tour");

        taron.setAttractionCategory(attractionCategoryRollerCoasters);
        hollywoodTour.setAttractionCategory(attractionCategoryWaterRides);



        //Create Blueprints
        List<IBlueprint> blueprints = new ArrayList<>();


        CoasterBlueprint suspendedLoopingCoaster = CoasterBlueprint.create("Suspended Looping Coaster", null);
        suspendedLoopingCoaster.setAttractionCategory(attractionCategoryRollerCoasters);

        blueprints.add(suspendedLoopingCoaster);

        //Create Attractions
        CustomCoaster krake = CustomCoaster.create("Krake");
        CustomCoaster flugDerDaemonen = CustomCoaster.create("Flug der Dämonen");
        CustomCoaster desertRace = CustomCoaster.create("Desert Race");
        CustomCoaster bigLoop = CustomCoaster.create("Big Loop");

        StockAttraction limit = StockAttraction.create("Limit", suspendedLoopingCoaster, null);


        CustomCoaster grottenblitz = CustomCoaster.create("Grottenblitz");
        CustomCoaster indyBlitz = CustomCoaster.create("Indy-Blitz");
        CustomCoaster bobbahn = CustomCoaster.create("Bobbahn");
        CustomCoaster colossos = CustomCoaster.create("Colossos");

        CustomAttraction scream = CustomAttraction.create("Scream");
        CustomAttraction mountainRafting = CustomAttraction.create("Mountain Rafting");
        CustomAttraction wildwasserbahn = CustomAttraction.create("Wildwasserbahn");
        CustomAttraction ghostbusters5D = CustomAttraction.create("Ghostbusters 5D");
        CustomAttraction monorail = CustomAttraction.create("Monorail");
        CustomAttraction screamie = CustomAttraction.create("Screamie");
        CustomAttraction bounty = CustomAttraction.create("Bounty");

        krake.setAttractionCategory(attractionCategoryRollerCoasters);
        flugDerDaemonen.setAttractionCategory(attractionCategoryRollerCoasters);
        desertRace.setAttractionCategory(attractionCategoryRollerCoasters);
        bigLoop.setAttractionCategory(attractionCategoryRollerCoasters);
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


        CustomCoaster steelVengeance = CustomCoaster.create("Steel Vengeance");
        CustomCoaster valravn = CustomCoaster.create("Valravn");
        CustomCoaster maverick = CustomCoaster.create("Maverick");
        CustomCoaster gatekeeper = CustomCoaster.create("Gatekeeper");
        CustomAttraction dodgem = CustomAttraction.create("Dodgem");

        steelVengeance.setAttractionCategory(attractionCategoryRollerCoasters);
        valravn.setAttractionCategory(attractionCategoryRollerCoasters);
        maverick.setAttractionCategory(attractionCategoryRollerCoasters);
        gatekeeper.setAttractionCategory(attractionCategoryRollerCoasters);
        dodgem.setAttractionCategory(attractionCategoryFamilyRides);






        CustomCoaster drako = CustomCoaster.create("Drako");
        CustomCoaster robinHood = CustomCoaster.create("Robin Hood");
        CustomCoaster speedOfSound = CustomCoaster.create("Speed of Sound");
        CustomCoaster xpressPlatform13 = CustomCoaster.create("Xpress: Platform 13");
        CustomCoaster goliath = CustomCoaster.create("Goliath");
        CustomCoaster lostGravity = CustomCoaster.create("Lost Gravity");

        StockAttraction elCondor = StockAttraction.create("El Condor", suspendedLoopingCoaster, null);

        drako.setAttractionCategory(attractionCategoryRollerCoasters);
        robinHood.setAttractionCategory(attractionCategoryRollerCoasters);
        speedOfSound.setAttractionCategory(attractionCategoryRollerCoasters);
        xpressPlatform13.setAttractionCategory(attractionCategoryRollerCoasters);
        goliath.setAttractionCategory(attractionCategoryRollerCoasters);
        lostGravity.setAttractionCategory(attractionCategoryRollerCoasters);

        CustomAttraction excalibur = CustomAttraction.create("Excalibur");
        CustomAttraction gForce = CustomAttraction.create("G-Force");
        CustomAttraction spaceShot = CustomAttraction.create("Space Shot");
        CustomAttraction spinningVibe = CustomAttraction.create("Spinning Vibe");
        CustomAttraction skydiver = CustomAttraction.create("Skydiver");
        CustomAttraction theTomahawk = CustomAttraction.create("The Tomahawk");

        excalibur.setAttractionCategory(attractionCategoryThrillRides);
        gForce.setAttractionCategory(attractionCategoryThrillRides);
        spaceShot.setAttractionCategory(attractionCategoryThrillRides);
        spinningVibe.setAttractionCategory(attractionCategoryThrillRides);
        skydiver.setAttractionCategory(attractionCategoryThrillRides);
        theTomahawk.setAttractionCategory(attractionCategoryThrillRides);

        CustomAttraction fibisBubbleSwirl = CustomAttraction.create("Fibi's Bubble Swirl");
        CustomAttraction haazGarage = CustomAttraction.create("Haaz Garage");
        CustomAttraction laGrandeRoue = CustomAttraction.create("La Grande Roue");
        CustomAttraction leTourDesJardins = CustomAttraction.create("Le Tour Des Jardins");
        CustomAttraction losSombreros = CustomAttraction.create("Los Sombreros");
        CustomAttraction merlinsMagicCastle = CustomAttraction.create("Merlin's Magic Castle");
        CustomAttraction merrieGoround = CustomAttraction.create("Merrie Go'round");
        CustomAttraction pavillonDeThe = CustomAttraction.create("Pavillon de Thè");
        CustomAttraction spaceKidz = CustomAttraction.create("Space Kidz");
        CustomAttraction superSwing = CustomAttraction.create("Super Swing");
        CustomAttraction squadsStuntFlight = CustomAttraction.create("Squad's Stunt Flight");
        CustomAttraction tequillaTaxis = CustomAttraction.create("Tequilla Taxi's");
        CustomAttraction wabWorldTour = CustomAttraction.create("WAB World Tour");
        CustomAttraction walibiExpress = CustomAttraction.create("Walibi Express");
        CustomAttraction walibisFunRecorder = CustomAttraction.create("Walibi's Fun Recorder");
        CustomAttraction zensGraffityShuttle = CustomAttraction.create("Zen's Graffity Shuttle");

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

        CustomAttraction crazyRiver = CustomAttraction.create("Crazy River");
        CustomAttraction elRioGrande = CustomAttraction.create("El Rio Grande");
        CustomAttraction splashBattle = CustomAttraction.create("SplashBattle");

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
        germany.addChildrenAndSetParents(new ArrayList<IElement>(germanStates));
        germany.addChildAndSetParent(bremen);

        netherlands.addChildAndSetParent(biddinghuizen);

        europe.addChildAndSetParent(germany);
        europe.addChildAndSetParent(netherlands);

        cedarPoint.addChildAndSetParent(steelVengeance);
        cedarPoint.addChildAndSetParent(valravn);
        cedarPoint.addChildAndSetParent(maverick);
        cedarPoint.addChildAndSetParent(gatekeeper);
        cedarPoint.addChildAndSetParent(dodgem);

        usa.addChildAndSetParent(cedarPoint);
        usa.addChildAndSetParent(sixFlagsMagicMountain);

        earth.addChildAndSetParent(europe);
        earth.addChildAndSetParent(usa);

        Visit visit0 = Visit.create(2018, 2, 30);
        this.addAttractionsToVisit(visit0, heidePark.getChildrenAsType(IOnSiteAttraction.class));
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
        this.addAttractionsToVisit(visit7, walibiHolland.getChildrenAsType(IOnSiteAttraction.class));
        walibiHolland.addChildAndSetParent(visit7);

        Visit visitToday = Visit.create(Calendar.getInstance());
        freimarkt.addChildAndSetParent(visitToday);
        Visit.setOpenVisit(visitToday);


        this.defaultAttractionCategory = attractionCategoryDefault;
        AppSettings.defaultAttractionCategoryUuid = this.defaultAttractionCategory.getUuid();

        content.addElement(germany); //adding one location is enough - content is searching for root on its own and flattens tree from there)

        content.setAttractionCategories(attractionCategories);
        content.addElements(Element.convertElementsToType(blueprints, IElement.class));

        this.addDefaults(content);
    }

    private void addAttractionsToVisit(Visit visit, List<IOnSiteAttraction> attractions)
    {
        for(IOnSiteAttraction attraction : attractions)
        {
            visit.addChildAndSetParent(VisitedAttraction.create(attraction));
        }
    }

    private void addDefaults(Content content)
    {

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

        settings.setDefaultIncrement(1);

        settings.setExportFileName("CCCExport.json");
    }
}
