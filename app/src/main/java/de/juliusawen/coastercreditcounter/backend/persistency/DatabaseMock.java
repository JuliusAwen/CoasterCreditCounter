package de.juliusawen.coastercreditcounter.backend.persistency;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.attractions.AttractionBlueprint;
import de.juliusawen.coastercreditcounter.backend.attractions.CoasterBlueprint;
import de.juliusawen.coastercreditcounter.backend.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.backend.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.backend.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.elements.Location;
import de.juliusawen.coastercreditcounter.backend.elements.Park;
import de.juliusawen.coastercreditcounter.backend.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.orphanElements.Status;
import de.juliusawen.coastercreditcounter.backend.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public final class DatabaseMock implements IDatabaseWrapper
{
    private AttractionCategories attractionCategories;
    private Manufacturers manufacturers;
    private Statuses statuses;
    private Locations locations;
    private CoasterBlueprints coasterBlueprints;
    private AttractionBlueprints attractionBlueprints;

    private static final DatabaseMock instance = new DatabaseMock();

    public static DatabaseMock getInstance()
    {
        return instance;
    }

    private DatabaseMock()
    {
        this.attractionCategories = new AttractionCategories();
        this.manufacturers = new Manufacturers();
        this.statuses = new Statuses();
        this.locations = new Locations();
        this.coasterBlueprints = new CoasterBlueprints(this.manufacturers, this.attractionCategories);
        this.attractionBlueprints = new AttractionBlueprints(this.manufacturers, this.attractionCategories);
    }

    @Override
    public boolean loadContent(Content content)
    {
        Stopwatch stopwatch = new Stopwatch(true);

        this.mockPhantasialand();
        this.mockHeidePark();
//        this.mockWalibiHolland();
        this.mockOsterwiese();
//        this.mockFreimarkt();
        this.mockPortAventura();
        this.mockFerrariLand();

        content.addElement(locations.Germany); //adding one location is enough - content is searching for root from there
        this.flattenContentTree(App.content.getRootLocation());

        content.addElements(ConvertTool.convertElementsToType(manufacturers.All, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(attractionCategories.All, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(statuses.All, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(coasterBlueprints.All, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(attractionBlueprints.All, IElement.class));

        Log.i(Constants.LOG_TAG, String.format("DatabaseMock.loadContent:: creating mock data successful - took [%d]ms", stopwatch.stop()));

        return true;
    }

    private void mockPhantasialand()
    {
        Park phantasialand = Park.create("Phantasialand", null);
        locations.Bruehl.addChildAndSetParent(phantasialand);

        CustomCoaster taron = CustomCoaster.create("Taron", 38, null);
        taron.setAttractionCategory(attractionCategories.RollerCoasters);
        taron.setManufacturer(manufacturers.Intamin);
        phantasialand.addChildAndSetParent(taron);

        CustomCoaster blackMamba = CustomCoaster.create("Black Mamba", 18, null);
        blackMamba.setAttractionCategory(attractionCategories.RollerCoasters);
        blackMamba.setManufacturer(manufacturers.BolligerAndMabillard);
        phantasialand.addChildAndSetParent(blackMamba);

        CustomCoaster coloradoAdventure = CustomCoaster.create("Colorado Adventure", 11, null);
        coloradoAdventure.setAttractionCategory(attractionCategories.RollerCoasters);
        coloradoAdventure.setManufacturer(manufacturers.Vekoma);
        phantasialand.addChildAndSetParent(coloradoAdventure);

        CustomCoaster raik = CustomCoaster.create("Raik", 5, null);
        raik.setAttractionCategory(attractionCategories.RollerCoasters);
        raik.setManufacturer(manufacturers.Vekoma);
        phantasialand.addChildAndSetParent(raik);

        CustomCoaster templeOfTheNightHawk = CustomCoaster.create("Temple of the Night Hawk", 9, null);
        templeOfTheNightHawk.setAttractionCategory(attractionCategories.RollerCoasters);
        templeOfTheNightHawk.setManufacturer(manufacturers.Vekoma);
        phantasialand.addChildAndSetParent(templeOfTheNightHawk);

        CustomCoaster winjasFear = CustomCoaster.create("Winja's Fear", 8, null);
        winjasFear.setAttractionCategory(attractionCategories.RollerCoasters);
        winjasFear.setManufacturer(manufacturers.MaurerSoehne);
        phantasialand.addChildAndSetParent(winjasFear);

        CustomCoaster winjasForce = CustomCoaster.create("Winja's Force", 8, null);
        winjasForce.setAttractionCategory(attractionCategories.RollerCoasters);
        winjasForce.setManufacturer(manufacturers.MaurerSoehne);
        phantasialand.addChildAndSetParent(winjasForce);

        CustomAttraction mysteryCastle = CustomAttraction.create("Mystery Castle", 0, null);
        mysteryCastle.setAttractionCategory(attractionCategories.ThrillRides);
        mysteryCastle.setManufacturer(manufacturers.Intamin);
        phantasialand.addChildAndSetParent(mysteryCastle);

        CustomAttraction hollywoodTour = CustomAttraction.create("Hollywood Tour", 0, null);
        hollywoodTour.setAttractionCategory(attractionCategories.DarkRides);
        hollywoodTour.setManufacturer(manufacturers.Intamin);
        phantasialand.addChildAndSetParent(hollywoodTour);

        CustomAttraction chiapas = CustomAttraction.create("Chiapas", 10, null);
        chiapas.setAttractionCategory(attractionCategories.WaterRides);
        chiapas.setManufacturer(manufacturers.Intamin);
        phantasialand.addChildAndSetParent(chiapas);

        CustomAttraction talocan = CustomAttraction.create("Talocan", 0, null);
        talocan.setAttractionCategory(attractionCategories.ThrillRides);
        talocan.setManufacturer(manufacturers.Huss);
        phantasialand.addChildAndSetParent(talocan);

        CustomAttraction fengJuPalace = CustomAttraction.create("Feng Ju Palace", 0, null);
        fengJuPalace.setAttractionCategory(attractionCategories.FamilyRides);
        fengJuPalace.setManufacturer(manufacturers.Vekoma);
        phantasialand.addChildAndSetParent(fengJuPalace);

        CustomAttraction geisterRiksha = CustomAttraction.create("Geister Rikscha", 0, null);
        geisterRiksha.setAttractionCategory(attractionCategories.DarkRides);
        geisterRiksha.setManufacturer(manufacturers.Schwarzkopf);
        phantasialand.addChildAndSetParent(geisterRiksha);

        CustomAttraction mausAuChocolat = CustomAttraction.create("Maus-Au-Chocolat", 1, null);
        mausAuChocolat.setAttractionCategory(attractionCategories.DarkRides);
        mausAuChocolat.setManufacturer(manufacturers.EtfRideSystems);
        phantasialand.addChildAndSetParent(mausAuChocolat);

        CustomAttraction wellenflug = CustomAttraction.create("Wellenflug", 0, null);
        wellenflug.setAttractionCategory(attractionCategories.FamilyRides);
        wellenflug.setManufacturer(manufacturers.Zierer);
        phantasialand.addChildAndSetParent(wellenflug);

        CustomAttraction tikal = CustomAttraction.create("Tikal", 1, null);
        tikal.setAttractionCategory(attractionCategories.FamilyRides);
        tikal.setManufacturer(manufacturers.Zierer);
        phantasialand.addChildAndSetParent(tikal);

        CustomAttraction verruecktesHotelTartueff = CustomAttraction.create("Verrücktes Hotel Tartüff", 0, null);
        verruecktesHotelTartueff.setAttractionCategory(attractionCategories.FamilyRides);
        verruecktesHotelTartueff.setManufacturer(manufacturers.Hofmann);
        phantasialand.addChildAndSetParent(verruecktesHotelTartueff);

        CustomAttraction riverQuest = CustomAttraction.create("River Quest", 0, null);
        riverQuest.setAttractionCategory(attractionCategories.WaterRides);
        riverQuest.setManufacturer(manufacturers.Hafema);
        phantasialand.addChildAndSetParent(riverQuest);

        CustomAttraction pferdekarusell = CustomAttraction.create("Pferdekarusell", 0, null);
        pferdekarusell.setAttractionCategory(attractionCategories.FamilyRides);
        pferdekarusell.setManufacturer(manufacturers.PrestonAndBarbieri);
        phantasialand.addChildAndSetParent(pferdekarusell);

        CustomAttraction wuermlingExpress = CustomAttraction.create("Würmling Express", 0, null);
        wuermlingExpress.setAttractionCategory(attractionCategories.FamilyRides);
        wuermlingExpress.setManufacturer(manufacturers.PrestonAndBarbieri);
        phantasialand.addChildAndSetParent(wuermlingExpress);


        LinkedHashMap<IOnSiteAttraction, Integer> rides14122018 = new LinkedHashMap<>();
        rides14122018.put(taron, 5);
        rides14122018.put(blackMamba, 2);
        rides14122018.put(winjasFear, 1);
        rides14122018.put(winjasForce, 1);
        rides14122018.put(templeOfTheNightHawk, 1);
        rides14122018.put(coloradoAdventure, 2);
        rides14122018.put(raik, 1);
        rides14122018.put(talocan, 1);
        rides14122018.put(mysteryCastle, 1);
        rides14122018.put(wuermlingExpress, 1);
        rides14122018.put(tikal, 1);
        rides14122018.put(verruecktesHotelTartueff, 1);
        rides14122018.put(wellenflug, 1);
        rides14122018.put(mausAuChocolat, 1);
        rides14122018.put(geisterRiksha, 1);
        rides14122018.put(fengJuPalace, 1);
        rides14122018.put(hollywoodTour, 1);
        rides14122018.put(chiapas, 1);
        phantasialand.addChildAndSetParent(this.createVisit(14, 12, 2018, rides14122018));
    }

    private void mockHeidePark()
    {
        Park heidePark = Park.create("Heide Park", null);
        locations.Soltau.addChildAndSetParent(heidePark);

        CustomCoaster krake = CustomCoaster.create("Krake", 22, null);
        krake.setAttractionCategory(attractionCategories.RollerCoasters);
        krake.setManufacturer(manufacturers.BolligerAndMabillard);
        heidePark.addChildAndSetParent(krake);

        CustomCoaster flugDerDaemonen = CustomCoaster.create("Flug der Dämonen", 19, null);
        flugDerDaemonen.setAttractionCategory(attractionCategories.RollerCoasters);
        flugDerDaemonen.setManufacturer(manufacturers.BolligerAndMabillard);
        heidePark.addChildAndSetParent(flugDerDaemonen);

        CustomCoaster desertRace = CustomCoaster.create("Desert Race", 18, null);
        desertRace.setAttractionCategory(attractionCategories.RollerCoasters);
        desertRace.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(desertRace);

        CustomCoaster bigLoop = CustomCoaster.create("Big Loop", 3, null);
        bigLoop.setAttractionCategory(attractionCategories.RollerCoasters);
        bigLoop.setManufacturer(manufacturers.Vekoma);
        heidePark.addChildAndSetParent(bigLoop);

        StockAttraction limit = StockAttraction.create("Limit", coasterBlueprints.SuspendedLoopingCoaster, 2, null);
        heidePark.addChildAndSetParent(limit);

        CustomCoaster grottenblitz = CustomCoaster.create("Grottenblitz", 2, null);
        grottenblitz.setAttractionCategory(attractionCategories.RollerCoasters);
        grottenblitz.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(grottenblitz);

        CustomCoaster indyBlitz = CustomCoaster.create("Indy-Blitz", 1, null);
        indyBlitz.setAttractionCategory(attractionCategories.RollerCoasters);
        indyBlitz.setManufacturer(manufacturers.Zierer);
        heidePark.addChildAndSetParent(indyBlitz);

        CustomCoaster bobbahn = CustomCoaster.create("Bobbahn", 2, null);
        bobbahn.setAttractionCategory(attractionCategories.RollerCoasters);
        bobbahn.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(bobbahn);

        CustomCoaster colossos = CustomCoaster.create("Colossos", 0, null);
        colossos.setAttractionCategory(attractionCategories.RollerCoasters);
        colossos.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(colossos);

        CustomAttraction scream = CustomAttraction.create("Scream", 0, null);
        scream.setAttractionCategory(attractionCategories.ThrillRides);
        scream.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(scream);

        CustomAttraction mountainRafting = CustomAttraction.create("Mountain Rafting", 0, null);
        mountainRafting.setAttractionCategory(attractionCategories.WaterRides);
        mountainRafting.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(mountainRafting);

        CustomAttraction wildwasserbahn = CustomAttraction.create("Wildwasserbahn", 0, null);
        wildwasserbahn.setAttractionCategory(attractionCategories.WaterRides);
        wildwasserbahn.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(wildwasserbahn);

        CustomAttraction ghostbusters5D = CustomAttraction.create("Ghostbusters 5D", 1, null);
        ghostbusters5D.setAttractionCategory(attractionCategories.DarkRides);
        ghostbusters5D.setManufacturer(manufacturers.Triotech);
        heidePark.addChildAndSetParent(ghostbusters5D);

        CustomAttraction monorail = CustomAttraction.create("Monorail", 0, null);
        monorail.setAttractionCategory(attractionCategories.TransportRides);
        monorail.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(monorail);

        CustomAttraction screamie = CustomAttraction.create("Screamie", 0, null);
        screamie.setAttractionCategory(attractionCategories.FamilyRides);
        screamie.setManufacturer(manufacturers.Zierer);
        heidePark.addChildAndSetParent(screamie);

        CustomAttraction bounty = CustomAttraction.create("Bounty", 0, null);
        bounty.setAttractionCategory(attractionCategories.FamilyRides);
        bounty.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(bounty);

        CustomAttraction drachengrotte = CustomAttraction.create("Drachengrotte", 0, null);
        drachengrotte.setAttractionCategory(attractionCategories.WaterRides);
        drachengrotte.setManufacturer(manufacturers.Zierer);
        heidePark.addChildAndSetParent(drachengrotte);

        CustomAttraction laola = CustomAttraction.create("La Ola", 0, null);
        laola.setAttractionCategory(attractionCategories.FamilyRides);
        laola.setManufacturer(manufacturers.Zierer);
        heidePark.addChildAndSetParent(laola);

        CustomAttraction panoramabahn = CustomAttraction.create("Panoramabahn", 0, null);
        panoramabahn.setAttractionCategory(attractionCategories.TransportRides);
        panoramabahn.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(panoramabahn);

        CustomAttraction hickshimmelsstuermer = CustomAttraction.create("Hick's Himmelsstürmer", 0 , null);
        hickshimmelsstuermer.setAttractionCategory(attractionCategories.FamilyRides);
        hickshimmelsstuermer.setManufacturer(manufacturers.Zamperla);
        heidePark.addChildAndSetParent(hickshimmelsstuermer);

        CustomAttraction kaeptnsToern = CustomAttraction.create("Käpt'ns Törn", 0, null);
        kaeptnsToern.setAttractionCategory(attractionCategories.WaterRides);
        kaeptnsToern.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(kaeptnsToern);


        LinkedHashMap<IOnSiteAttraction, Integer> rides07042019 = new LinkedHashMap<>();
        rides07042019.put(desertRace, 3);
        rides07042019.put(grottenblitz, 1);
        rides07042019.put(krake, 3);
        rides07042019.put(flugDerDaemonen, 3);
        rides07042019.put(scream, 1);
        rides07042019.put(screamie, 1);
        rides07042019.put(bounty, 1);
        rides07042019.put(panoramabahn, 1);
        rides07042019.put(mountainRafting, 1);
        rides07042019.put(drachengrotte, 1);
        rides07042019.put(kaeptnsToern, 1);
        heidePark.addChildAndSetParent(this.createVisit(7, 4, 2019, rides07042019));

        LinkedHashMap<IOnSiteAttraction, Integer> rides18052019 = new LinkedHashMap<>();
        rides18052019.put(krake, 2);
        rides18052019.put(flugDerDaemonen, 2);
        rides18052019.put(grottenblitz, 1);
        rides18052019.put(colossos, 3);
        rides18052019.put(limit, 1);
        rides18052019.put(scream, 1);
        rides18052019.put(ghostbusters5D, 1);
        rides18052019.put(laola, 1);
        rides18052019.put(wildwasserbahn, 1);
        rides18052019.put(mountainRafting, 2);
        heidePark.addChildAndSetParent(this.createVisit(18, 5, 2019, rides18052019));
    }

    private void mockWalibiHolland()
    {
        Park walibiHolland = Park.create("Walibi Holland", null);
        locations.Biddinghuizen.addChildAndSetParent(walibiHolland);

        StockAttraction elCondor = StockAttraction.create("El Condor", coasterBlueprints.SuspendedLoopingCoaster, 1, null);
        StockAttraction speedOfSound = StockAttraction.create("Speed of Sound", coasterBlueprints.Boomerang, 2, null);

        StockAttraction excalibur = StockAttraction.create("Excalibur", attractionBlueprints.TopSpin, 1, null);
        StockAttraction gForce = StockAttraction.create("gForce", attractionBlueprints.Enterprise, 0, null);

        CustomCoaster drako = CustomCoaster.create("Drako", 2, null);
        drako.setAttractionCategory(attractionCategories.RollerCoasters);
        drako.setManufacturer(manufacturers.Zierer);

        CustomCoaster lostGravity = CustomCoaster.create("Lost Gravity", 7, null);
        lostGravity.setAttractionCategory(attractionCategories.RollerCoasters);
        lostGravity.setManufacturer(manufacturers.Mack);

        CustomCoaster robinHood = CustomCoaster.create("Robin Hood", 2, null);
        robinHood.setAttractionCategory(attractionCategories.RollerCoasters);
        robinHood.setManufacturer(manufacturers.Vekoma);
        robinHood.setStatus(statuses.ClosedForConversion);

        CustomCoaster xpressPlatform13 = CustomCoaster.create("Xpress: Platform 13", 2, null);
        xpressPlatform13.setAttractionCategory(attractionCategories.RollerCoasters);
        xpressPlatform13.setManufacturer(manufacturers.Vekoma);

        CustomCoaster goliath = CustomCoaster.create("Goliath", 7, null);
        goliath.setAttractionCategory(attractionCategories.RollerCoasters);
        goliath.setManufacturer(manufacturers.Intamin);

        CustomAttraction spaceShot = CustomAttraction.create("Space Shot", 0, null);
        CustomAttraction spinningVibe = CustomAttraction.create("Spinning Vibe", 0, null);
        CustomAttraction skydiver = CustomAttraction.create("Skydiver", 0, null);
        CustomAttraction theTomahawk = CustomAttraction.create("The Tomahawk", 0, null);

        excalibur.setAttractionCategory(attractionCategories.ThrillRides);
        excalibur.setManufacturer(manufacturers.Huss);

        gForce.setAttractionCategory(attractionCategories.ThrillRides);
        spaceShot.setAttractionCategory(attractionCategories.ThrillRides);
        spinningVibe.setAttractionCategory(attractionCategories.ThrillRides);
        skydiver.setAttractionCategory(attractionCategories.ThrillRides);
        theTomahawk.setAttractionCategory(attractionCategories.ThrillRides);

        CustomAttraction fibisBubbleSwirl = CustomAttraction.create("Fibi's Bubble Swirl", 0, null);
        CustomAttraction haazGarage = CustomAttraction.create("Haaz Garage", 0, null);
        CustomAttraction laGrandeRoue = CustomAttraction.create("La Grande Roue", 0, null);
        CustomAttraction leTourDesJardins = CustomAttraction.create("Le Tour Des Jardins", 0, null);
        CustomAttraction losSombreros = CustomAttraction.create("Los Sombreros", 0, null);
        CustomAttraction merlinsMagicCastle = CustomAttraction.create("Merlin's Magic Castle", 1, null);
        CustomAttraction merrieGoround = CustomAttraction.create("Merrie Go'round", 0, null);
        CustomAttraction pavillonDeThe = CustomAttraction.create("Pavillon de Thè", 0, null);
        CustomAttraction spaceKidz = CustomAttraction.create("Space Kidz", 0, null);
        CustomAttraction superSwing = CustomAttraction.create("Super Swing", 0, null);
        CustomAttraction squadsStuntFlight = CustomAttraction.create("Squad's Stunt Flight", 0, null);
        CustomAttraction tequillaTaxis = CustomAttraction.create("Tequilla Taxi's", 0, null);
        CustomAttraction wabWorldTour = CustomAttraction.create("WAB World Tour", 0, null);
        CustomAttraction walibiExpress = CustomAttraction.create("Walibi Express", 0, null);
        CustomAttraction walibisFunRecorder = CustomAttraction.create("Walibi's Fun Recorder", 0, null);
        CustomAttraction zensGraffityShuttle = CustomAttraction.create("Zen's Graffity Shuttle", 0, null);

        fibisBubbleSwirl.setAttractionCategory(attractionCategories.FamilyRides);
        haazGarage.setAttractionCategory(attractionCategories.FamilyRides);
        laGrandeRoue.setAttractionCategory(attractionCategories.FamilyRides);
        leTourDesJardins.setAttractionCategory(attractionCategories.FamilyRides);
        losSombreros.setAttractionCategory(attractionCategories.FamilyRides);

        merlinsMagicCastle.setAttractionCategory(attractionCategories.FamilyRides);
        merlinsMagicCastle.setManufacturer(manufacturers.Vekoma);

        merrieGoround.setAttractionCategory(attractionCategories.FamilyRides);
        pavillonDeThe.setAttractionCategory(attractionCategories.FamilyRides);
        spaceKidz.setAttractionCategory(attractionCategories.FamilyRides);
        superSwing.setAttractionCategory(attractionCategories.FamilyRides);
        squadsStuntFlight.setAttractionCategory(attractionCategories.FamilyRides);
        tequillaTaxis.setAttractionCategory(attractionCategories.FamilyRides);
        wabWorldTour.setAttractionCategory(attractionCategories.FamilyRides);
        walibiExpress.setAttractionCategory(attractionCategories.FamilyRides);
        walibisFunRecorder.setAttractionCategory(attractionCategories.FamilyRides);
        zensGraffityShuttle.setAttractionCategory(attractionCategories.FamilyRides);

        CustomAttraction crazyRiver = CustomAttraction.create("Crazy River", 2, null);
        CustomAttraction elRioGrande = CustomAttraction.create("El Rio Grande", 2, null);
        CustomAttraction splashBattle = CustomAttraction.create("SplashBattle", 0, null);

        crazyRiver.setAttractionCategory(attractionCategories.WaterRides);
        elRioGrande.setAttractionCategory(attractionCategories.WaterRides);
        splashBattle.setAttractionCategory(attractionCategories.WaterRides);


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
    }

    private void mockOsterwiese()
    {
        Park osterwiese = Park.create("Osterwiese", null);
        locations.Bremen.addChildAndSetParent(osterwiese);

        CustomCoaster crazyMouse = CustomCoaster.create("Crazy Mouse", 0, null);
        crazyMouse.setAttractionCategory(attractionCategories.RollerCoasters);
        osterwiese.addChildAndSetParent(crazyMouse);

        StockAttraction tomDerTiger = StockAttraction.create("Tom der Tiger", coasterBlueprints.BigAppleMB28, 0, null);
        osterwiese.addChildAndSetParent(tomDerTiger);

        LinkedHashMap<IOnSiteAttraction, Integer> rides13042019 = new LinkedHashMap<>();
        rides13042019.put(crazyMouse, 1);
        rides13042019.put(tomDerTiger, 1);
        osterwiese.addChildAndSetParent(this.createVisit(13, 4, 2019, rides13042019));
    }

    private void mockFreimarkt()
    {
        Park freimarkt = Park.create("Freimarkt", null);
        locations.Bremen.addChildAndSetParent(freimarkt);

        //Visit.setOpenVisit(visitToday);
    }

    private void mockPortAventura()
    {
        Park portAventura = Park.create("Port Aventura", null);
        locations.Salou.addChildAndSetParent(portAventura);

        CustomCoaster shambhala = CustomCoaster.create("Shambhala", 0, null);
        shambhala.setAttractionCategory(attractionCategories.RollerCoasters);
        shambhala.setManufacturer(manufacturers.BolligerAndMabillard);
        portAventura.addChildAndSetParent(shambhala);

        CustomCoaster dragonKhan = CustomCoaster.create("Dragon Khan", 0, null);
        dragonKhan.setAttractionCategory(attractionCategories.RollerCoasters);
        dragonKhan.setManufacturer(manufacturers.BolligerAndMabillard);
        portAventura.addChildAndSetParent(dragonKhan);

        CustomCoaster stampidaRoja = CustomCoaster.create("Stampida (Roja)", 0, null);
        stampidaRoja.setAttractionCategory(attractionCategories.RollerCoasters);
        stampidaRoja.setManufacturer(manufacturers.CCI);
        portAventura.addChildAndSetParent(stampidaRoja);

        CustomCoaster stampidaAzul = CustomCoaster.create("Stampida (Azul)", 0, null);
        stampidaAzul.setAttractionCategory(attractionCategories.RollerCoasters);
        stampidaAzul.setManufacturer(manufacturers.CCI);
        portAventura.addChildAndSetParent(stampidaAzul);

        CustomCoaster tomahawk = CustomCoaster.create("Tomahawk", 0, null);
        tomahawk.setAttractionCategory(attractionCategories.RollerCoasters);
        tomahawk.setManufacturer(manufacturers.CCI);
        portAventura.addChildAndSetParent(tomahawk);

        CustomCoaster elDiablo = CustomCoaster.create("El Diablo", 0, null);
        elDiablo.setAttractionCategory(attractionCategories.RollerCoasters);
        elDiablo.setManufacturer(manufacturers.ArrowDynamics);
        portAventura.addChildAndSetParent(elDiablo);

        CustomCoaster furiusBaco = CustomCoaster.create("Furius Baco", 0, null);
        furiusBaco.setAttractionCategory(attractionCategories.RollerCoasters);
        furiusBaco.setManufacturer(manufacturers.Intamin);
        portAventura.addChildAndSetParent(furiusBaco);

        CustomCoaster tamitami = CustomCoaster.create("Tami-Tami", 0, null);
        tamitami.setAttractionCategory(attractionCategories.RollerCoasters);
        tamitami.setManufacturer(manufacturers.Vekoma);
        portAventura.addChildAndSetParent(tamitami);

        CustomAttraction hurakanCondor = CustomAttraction.create("Hurakan Condor", 0, null);
        hurakanCondor.setAttractionCategory(attractionCategories.ThrillRides);
        hurakanCondor.setManufacturer(manufacturers.Intamin);
        portAventura.addChildAndSetParent(hurakanCondor);

        CustomAttraction serpienteEmplumada = CustomAttraction.create("Serpiente Emplumada", 0, null);
        serpienteEmplumada.setAttractionCategory(attractionCategories.FamilyRides);
        serpienteEmplumada.setManufacturer(manufacturers.Schwarzkopf);
        portAventura.addChildAndSetParent(serpienteEmplumada);

        CustomAttraction tutukiSplash = CustomAttraction.create("Tutuki Splash", 0, null);
        tutukiSplash.setAttractionCategory(attractionCategories.WaterRides);
        tutukiSplash.setManufacturer(manufacturers.Intamin);
        portAventura.addChildAndSetParent(tutukiSplash);

        CustomAttraction silverRiverFlumes = CustomAttraction.create("Silver River Flumes", 0, null);
        silverRiverFlumes.setAttractionCategory(attractionCategories.WaterRides);
        silverRiverFlumes.setManufacturer(manufacturers.Mack);
        portAventura.addChildAndSetParent(silverRiverFlumes);

        CustomAttraction grandCanyonRapids = CustomAttraction.create("Grand Canyon Rapids", 0, null);
        grandCanyonRapids.setAttractionCategory(attractionCategories.WaterRides);
        grandCanyonRapids.setManufacturer(manufacturers.Intamin);
        portAventura.addChildAndSetParent(grandCanyonRapids);

        LinkedHashMap<IOnSiteAttraction, Integer> rides02102018 = new LinkedHashMap<>();
        rides02102018.put(shambhala, 14);
        rides02102018.put(dragonKhan, 2);
        rides02102018.put(stampidaRoja, 1);
        rides02102018.put(stampidaAzul, 1);
        rides02102018.put(tomahawk, 1);
        rides02102018.put(elDiablo, 1);
        rides02102018.put(furiusBaco, 3);
        rides02102018.put(tamitami, 1);
        rides02102018.put(hurakanCondor, 1);
        portAventura.addChildAndSetParent(this.createVisit(2, 10, 2018, rides02102018));

        LinkedHashMap<IOnSiteAttraction, Integer> rides04102018 = new LinkedHashMap<>();
        rides04102018.put(shambhala, 3);
        rides04102018.put(dragonKhan, 1);
        rides04102018.put(tomahawk, 1);
        rides04102018.put(elDiablo, 1);
        rides04102018.put(furiusBaco, 1);
        rides04102018.put(tutukiSplash, 1);
        rides04102018.put(silverRiverFlumes, 4);
        rides04102018.put(grandCanyonRapids, 1);
        portAventura.addChildAndSetParent(this.createVisit(4, 10, 2018, rides04102018));

        LinkedHashMap<IOnSiteAttraction, Integer> rides02052019 = new LinkedHashMap<>();
        rides02052019.put(shambhala, 3);
        rides02052019.put(elDiablo, 1);
        rides02052019.put(dragonKhan, 1);
        rides02052019.put(stampidaAzul, 1);
        rides02052019.put(serpienteEmplumada, 1);
        rides02052019.put(silverRiverFlumes, 1);
        portAventura.addChildAndSetParent(this.createVisit(2, 5, 2019, rides02052019));

        LinkedHashMap<IOnSiteAttraction, Integer> rides04052019 = new LinkedHashMap<>();
        rides04052019.put(shambhala, 2);
        rides04052019.put(furiusBaco, 1);
        rides04052019.put(grandCanyonRapids, 1);
        portAventura.addChildAndSetParent(this.createVisit(4, 5, 2019, rides04052019));
    }

    private void mockFerrariLand()
    {
        Park ferrariLand = Park.create("Ferrari Land", null);
        locations.Salou.addChildAndSetParent(ferrariLand);

        CustomCoaster redForce = CustomCoaster.create("Red Force", 0, null);
        redForce.setAttractionCategory(attractionCategories.RollerCoasters);
        redForce.setManufacturer(manufacturers.Intamin);
        ferrariLand.addChildAndSetParent(redForce);

        CustomCoaster juniorRedForce = CustomCoaster.create("Junior Red Force", 0, null);
        juniorRedForce.setAttractionCategory(attractionCategories.RollerCoasters);
        juniorRedForce.setManufacturer(manufacturers.SbfVisa);
        ferrariLand.addChildAndSetParent(juniorRedForce);

        CustomAttraction thrillTowers1 = CustomAttraction.create("Thrill Tower I", 0, null);
        thrillTowers1.setAttractionCategory(attractionCategories.ThrillRides);
        thrillTowers1.setManufacturer(manufacturers.SAndS);
        ferrariLand.addChildAndSetParent(thrillTowers1);

        CustomAttraction thrillTowers2 = CustomAttraction.create("Thrill Towers II", 0, null);
        thrillTowers2.setAttractionCategory(attractionCategories.ThrillRides);
        thrillTowers2.setManufacturer(manufacturers.SAndS);
        ferrariLand.addChildAndSetParent(thrillTowers2);

        CustomAttraction racingLegends = CustomAttraction.create("Racing Legends", 0, null);
        racingLegends.setAttractionCategory(attractionCategories.DarkRides);
        racingLegends.setManufacturer(manufacturers.Simworx);
        ferrariLand.addChildAndSetParent(racingLegends);

        LinkedHashMap<IOnSiteAttraction, Integer> rides02102018 = new LinkedHashMap<>();
        rides02102018.put(redForce, 6);
        rides02102018.put(juniorRedForce, 1);
        rides02102018.put(thrillTowers1, 1);
        rides02102018.put(thrillTowers2, 1);
        rides02102018.put(racingLegends, 1);
        ferrariLand.addChildAndSetParent(this.createVisit(2, 10, 2018, rides02102018));

        LinkedHashMap<IOnSiteAttraction, Integer> rides02052018 = new LinkedHashMap<>();
        rides02052018.put(redForce, 1);
        ferrariLand.addChildAndSetParent(this.createVisit(2, 5, 2019, rides02052018));
    }

    private Visit createVisit(int day, int month, int year, LinkedHashMap<IOnSiteAttraction, Integer> rides)
    {
        Visit visit = Visit.create(year, month - 1, day, null);

        for(Map.Entry<IOnSiteAttraction, Integer> entry : rides.entrySet())
        {
            VisitedAttraction visitedAttraction = VisitedAttraction.create(entry.getKey());
            visit.addChildAndSetParent(visitedAttraction);
            this.addRidesToVisit(visitedAttraction, entry.getValue());
        }

        return visit;
    }


    private void addRidesToVisit(VisitedAttraction visitedAttraction, int count)
    {
        for(int i = 0; i < count; i++)
        {
            visitedAttraction.addRide();
        }
    }

    private void flattenContentTree(IElement element)
    {
        if(!App.content.containsElement(element))
        {
            Log.v(Constants.LOG_TAG, String.format("DatabaseMock.flattenContentTree:: adding %s to content", element));
            App.content.addElement(element);
        }
        else
        {
            Log.w(Constants.LOG_TAG,  String.format("DatabaseMock.flattenContentTree:: not adding %s to content as it is already known", element));
        }

        for (IElement child : element.getChildren())
        {
            this.flattenContentTree(child);
        }
    }

    @Override
    public boolean saveContent(Content content)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.saveContent:: content is not persited - DatabaseMock is not able to persist any data");
        return true;
    }

    @Override
    public boolean synchronize(Set<IElement> elementsToCreate, Set<IElement> elementsToUpdate, Set<IElement> elementsToDelete)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.synchronize:: persistence not synchronized - DatabaseMock is not able to persist any data");
        return true;
    }

    @Override
    public boolean create(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.create:: elements not created - DatabaseMock is not able to persist any data");
        return true;
    }

    @Override
    public boolean update(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.update:: elements not updated - DatabaseMock is not able to persist any data");
        return true;
    }

    @Override
    public boolean delete(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.delete:: elements not updated - DatabaseMock is not able to persist any data");
        return true;
    }
}

class AttractionCategories
{
    AttractionCategory RollerCoasters = AttractionCategory.create("RollerCoasters", null);
    AttractionCategory ThrillRides = AttractionCategory.create("Thrill Rides", null);
    AttractionCategory FamilyRides = AttractionCategory.create("Family Rides", null);
    AttractionCategory WaterRides = AttractionCategory.create("Water Rides", null);
    AttractionCategory DarkRides = AttractionCategory.create("Dark Rides", null);
    AttractionCategory TransportRides = AttractionCategory.create("Transport Rides", null);

    List<AttractionCategory> All = new LinkedList<>();

    AttractionCategories()
    {
        AttractionCategory.createAndSetDefault();

        All.add(AttractionCategory.getDefault());

        All.add(RollerCoasters);
        All.add(ThrillRides);
        All.add(FamilyRides);
        All.add(WaterRides);
        All.add(DarkRides);
        All.add(TransportRides);
    }
}

class Manufacturers
{
    Manufacturer BolligerAndMabillard = Manufacturer.create("Bolliger & Mabillard", null);
    Manufacturer Intamin = Manufacturer.create("Intamin", null);
    Manufacturer Vekoma = Manufacturer.create("Vekoma", null);
    Manufacturer Huss = Manufacturer.create("Huss", null);
    Manufacturer Pinfari = Manufacturer.create("Pinfari", null);
    Manufacturer MaurerSoehne = Manufacturer.create("Mauerer Söhne", null);
    Manufacturer EtfRideSystems = Manufacturer.create("ETF Ride Systems", null);
    Manufacturer Zierer = Manufacturer.create("Zierer", null);
    Manufacturer Hofmann = Manufacturer.create("Hofmann", null);
    Manufacturer Hafema = Manufacturer.create("Hafema", null);
    Manufacturer PrestonAndBarbieri = Manufacturer.create("Preston & Barbieri", null);
    Manufacturer Schwarzkopf = Manufacturer.create("Schwarzkopf", null);
    Manufacturer Mack = Manufacturer.create("Mack", null);
    Manufacturer SAndS = Manufacturer.create("S&S", null);
    Manufacturer SbfVisa = Manufacturer.create("SBF Visa", null);
    Manufacturer Triotech = Manufacturer.create("Triotech", null);
    Manufacturer Zamperla = Manufacturer.create("Zamperla", null);
    Manufacturer ArrowDynamics = Manufacturer.create("Arrow Dynamics", null);
    Manufacturer Simworx = Manufacturer.create("Simworx", null);
    Manufacturer CCI = Manufacturer.create("Custom Coasters International", null);

    List<Manufacturer> All = new LinkedList<>();

    Manufacturers()
    {
        Manufacturer.createAndSetDefault();

        All.add(Manufacturer.getDefault());

        All.add(BolligerAndMabillard);
        All.add(Intamin);
        All.add(Vekoma);
        All.add(Huss);
        All.add(Pinfari);
        All.add(MaurerSoehne);
        All.add(EtfRideSystems);
        All.add(Zierer);
        All.add(Hofmann);
        All.add(Hafema);
        All.add(PrestonAndBarbieri);
        All.add(Schwarzkopf);
        All.add(Mack);
        All.add(SAndS);
        All.add(SbfVisa);
        All.add(Triotech);
        All.add(Zamperla);
        All.add(ArrowDynamics);
        All.add(Simworx);
        All.add(CCI);
    }
}

class Statuses
{
    Status ClosedForRefurbishment = Status.create("closed for refurbishment", null);
    Status ClosedForConversion = Status.create("closed for conversion", null);
    Status Converted = Status.create("converted", null);
    Status Defunct = Status.create("defunct", null);

    List<Status> All = new LinkedList<>();

    Statuses()
    {
        Status.createAndSetDefault();

        All.add(Status.getDefault());

        All.add(ClosedForRefurbishment);
        All.add(ClosedForConversion);
        All.add(Defunct);
        All.add(Converted);
    }
}

class Locations
{
    Location Europe = Location.create("Europe", null);

    Location Germany = Location.create("Germany", null);
//    Location Netherlands = Location.create("Netherlands", null);
    Location Spain = Location.create("Spain", null);

    Location Bruehl = Location.create("Brühl", null);
    Location Soltau = Location.create("Soltau", null);
    Location Bremen = Location.create("Bremen", null);

    Location Biddinghuizen = Location.create("Biddinghuizen", null);

    Location Salou = Location.create("Salou", null);

    Locations()
    {
        Germany.addChildAndSetParent(Bruehl);
        Germany.addChildAndSetParent(Soltau);
        Germany.addChildAndSetParent(Bremen);

//        Netherlands.addChildAndSetParent(Biddinghuizen);

        Spain.addChildAndSetParent(Salou);

        Europe.addChildAndSetParent(Germany);
//        Europe.addChildAndSetParent(Netherlands);
        Europe.addChildAndSetParent(Spain);
    }
}

class CoasterBlueprints
{
    CoasterBlueprint SuspendedLoopingCoaster = CoasterBlueprint.create("Suspended Looping Coaster", null);
    CoasterBlueprint Boomerang = CoasterBlueprint.create("Boomerang", null);
    CoasterBlueprint BigAppleMB28 = CoasterBlueprint.create("Big Apple MB28", null);

    List<CoasterBlueprint> All = new LinkedList<>();

    CoasterBlueprints(Manufacturers manufacturers, AttractionCategories attractionCategories)
    {
        SuspendedLoopingCoaster.setManufacturer(manufacturers.Vekoma);
        SuspendedLoopingCoaster.setAttractionCategory(attractionCategories.RollerCoasters);

        Boomerang.setManufacturer(manufacturers.Vekoma);
        Boomerang.setAttractionCategory(attractionCategories.RollerCoasters);

        BigAppleMB28.setManufacturer(manufacturers.Pinfari);
        BigAppleMB28.setAttractionCategory(attractionCategories.RollerCoasters);


        All.add(SuspendedLoopingCoaster);
        All.add(Boomerang);
        All.add(BigAppleMB28);
    }
}

class AttractionBlueprints
{
    AttractionBlueprint TopSpin = AttractionBlueprint.create("Top Spin", null);
    AttractionBlueprint Enterprise = AttractionBlueprint.create("Enterprise", null);
    AttractionBlueprint BreakDancer = AttractionBlueprint.create("Break Dancer", null);

    List<AttractionBlueprint> All = new LinkedList<>();

    AttractionBlueprints(Manufacturers manufacturers, AttractionCategories attractionCategories)
    {
        TopSpin.setManufacturer(manufacturers.Huss);
        TopSpin.setAttractionCategory(attractionCategories.ThrillRides);

        Enterprise.setManufacturer(manufacturers.Huss);
        Enterprise.setAttractionCategory(attractionCategories.ThrillRides);

        BreakDancer.setManufacturer(manufacturers.Huss);
        BreakDancer.setAttractionCategory(attractionCategories.ThrillRides);

        All.add(TopSpin);
        All.add(Enterprise);
        All.add(BreakDancer);
    }
}
