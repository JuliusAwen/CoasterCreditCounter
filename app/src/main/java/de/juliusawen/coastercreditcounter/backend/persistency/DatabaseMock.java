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
        this.mockOsterwiese();
        this.mockFreimarkt();
        this.mockPortAventura();
        this.mockFerrariLand();
        this.mockWalibiHolland();
        this.mockSlagharen();
        this.mockHolidayPark();
        this.mockMovieParkGermany();
        this.mockToverland();
        this.mockEfteling();
        this.mockHansaPark();
        this.mockMagicParkVerden();
        this.mockEuropaPark();

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
        locations.Germany.addChildAndSetParent(phantasialand);

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

        StockAttraction talocan = StockAttraction.create("Talocan", attractionBlueprints.SuspendedTopSpin, 0, null);
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
        locations.Germany.addChildAndSetParent(heidePark);

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
        locations.Netherlands.addChildAndSetParent(walibiHolland);

        StockAttraction elCondor = StockAttraction.create("El Condor", coasterBlueprints.SuspendedLoopingCoaster, 1, null);
        walibiHolland.addChildAndSetParent(elCondor);

        StockAttraction speedOfSound = StockAttraction.create("Speed of Sound", coasterBlueprints.Boomerang, 2, null);
        walibiHolland.addChildAndSetParent(speedOfSound);

        StockAttraction excalibur = StockAttraction.create("Excalibur", attractionBlueprints.TopSpin, 1, null);
        walibiHolland.addChildAndSetParent(excalibur);

        CustomAttraction gForce = CustomAttraction.create("G-Force", 0, null);
        gForce.setAttractionCategory(attractionCategories.ThrillRides);
        gForce.setManufacturer(manufacturers.Huss);
        walibiHolland.addChildAndSetParent(gForce);

        CustomCoaster drako = CustomCoaster.create("Drako", 2, null);
        drako.setAttractionCategory(attractionCategories.RollerCoasters);
        drako.setManufacturer(manufacturers.Zierer);
        walibiHolland.addChildAndSetParent(drako);

        CustomCoaster lostGravity = CustomCoaster.create("Lost Gravity", 7, null);
        lostGravity.setAttractionCategory(attractionCategories.RollerCoasters);
        lostGravity.setManufacturer(manufacturers.Mack);
        walibiHolland.addChildAndSetParent(lostGravity);

        CustomCoaster robinHood = CustomCoaster.create("Robin Hood", 2, null);
        robinHood.setAttractionCategory(attractionCategories.RollerCoasters);
        robinHood.setManufacturer(manufacturers.Vekoma);
        robinHood.setStatus(statuses.Converted);
        walibiHolland.addChildAndSetParent(robinHood);

        CustomCoaster xpressPlatform13 = CustomCoaster.create("Xpress: Platform 13", 2, null);
        xpressPlatform13.setAttractionCategory(attractionCategories.RollerCoasters);
        xpressPlatform13.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(xpressPlatform13);

        CustomCoaster goliath = CustomCoaster.create("Goliath", 7, null);
        goliath.setAttractionCategory(attractionCategories.RollerCoasters);
        goliath.setManufacturer(manufacturers.Intamin);
        walibiHolland.addChildAndSetParent(goliath);

        CustomCoaster untamed = CustomCoaster.create("Untamed", 0, null);
        untamed.setAttractionCategory(attractionCategories.RollerCoasters);
        untamed.setManufacturer(manufacturers.RMC);
        walibiHolland.addChildAndSetParent(untamed);

        CustomAttraction spaceShot = CustomAttraction.create("Space Shot", 0, null);
        spaceShot.setAttractionCategory(attractionCategories.ThrillRides);
        spaceShot.setManufacturer(manufacturers.SAndS);
        walibiHolland.addChildAndSetParent(spaceShot);

        CustomAttraction spinningVibe = CustomAttraction.create("Spinning Vibe", 0, null);
        spinningVibe.setAttractionCategory(attractionCategories.ThrillRides);
        spinningVibe.setManufacturer(manufacturers.Huss);
        walibiHolland.addChildAndSetParent(spinningVibe);

        CustomAttraction skydiver = CustomAttraction.create("Skydiver", 0, null);
        skydiver.setAttractionCategory(attractionCategories.ThrillRides);
        walibiHolland.addChildAndSetParent(skydiver);

        CustomAttraction tomahawk = CustomAttraction.create("Tomahawk", 0, null);
        tomahawk.setAttractionCategory(attractionCategories.ThrillRides);
        tomahawk.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(tomahawk);

        CustomAttraction fibisBubbleSwirl = CustomAttraction.create("Fibi's Bubble Swirl", 0, null);
        fibisBubbleSwirl.setAttractionCategory(attractionCategories.FamilyRides);
        fibisBubbleSwirl.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(fibisBubbleSwirl);

        CustomAttraction haazGarage = CustomAttraction.create("Haaz Garage", 0, null);
        haazGarage.setAttractionCategory(attractionCategories.FamilyRides);
        haazGarage.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(haazGarage);

        CustomAttraction laGrandeRoue = CustomAttraction.create("La Grande Roue", 0, null);
        laGrandeRoue.setAttractionCategory(attractionCategories.FamilyRides);
        laGrandeRoue.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(laGrandeRoue);

        CustomAttraction leTourDesJardins = CustomAttraction.create("Le Tour Des Jardins", 0, null);
        leTourDesJardins.setAttractionCategory(attractionCategories.FamilyRides);
        walibiHolland.addChildAndSetParent(leTourDesJardins);

        CustomAttraction losSombreros = CustomAttraction.create("Los Sombreros", 0, null);
        losSombreros.setAttractionCategory(attractionCategories.FamilyRides);
        walibiHolland.addChildAndSetParent(losSombreros);

        CustomAttraction merlinsMagicCastle = CustomAttraction.create("Merlin's Magic Castle", 1, null);
        merlinsMagicCastle.setAttractionCategory(attractionCategories.FamilyRides);
        merlinsMagicCastle.setManufacturer(manufacturers.Vekoma);
        merlinsMagicCastle.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(merlinsMagicCastle);

        CustomAttraction merrieGoround = CustomAttraction.create("Merrie Go'round", 0, null);
        merrieGoround.setAttractionCategory(attractionCategories.FamilyRides);
        merrieGoround.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(merrieGoround);

        CustomAttraction pavillonDeThe = CustomAttraction.create("Pavillon de Thè", 0, null);
        pavillonDeThe.setAttractionCategory(attractionCategories.FamilyRides);
        walibiHolland.addChildAndSetParent(pavillonDeThe);

        CustomAttraction spaceKidz = CustomAttraction.create("Space Kidz", 0, null);
        spaceKidz.setAttractionCategory(attractionCategories.FamilyRides);
        spaceKidz.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(spaceKidz);

        CustomAttraction superSwing = CustomAttraction.create("Super Swing", 0, null);
        superSwing.setAttractionCategory(attractionCategories.FamilyRides);
        superSwing.setManufacturer(manufacturers.Zierer);
        walibiHolland.addChildAndSetParent(superSwing);

        CustomAttraction squadsStuntFlight = CustomAttraction.create("Squad's Stunt Flight", 0, null);
        squadsStuntFlight.setAttractionCategory(attractionCategories.FamilyRides);
        squadsStuntFlight.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(squadsStuntFlight);

        CustomAttraction tequillaTaxis = CustomAttraction.create("Tequilla Taxi's", 0, null);
        tequillaTaxis.setAttractionCategory(attractionCategories.FamilyRides);
        tequillaTaxis.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(tequillaTaxis);

        CustomAttraction wabWorldTour = CustomAttraction.create("WAB World Tour", 0, null);
        wabWorldTour.setAttractionCategory(attractionCategories.FamilyRides);
        wabWorldTour.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(wabWorldTour);

        CustomAttraction walibiExpress = CustomAttraction.create("Walibi Express", 0, null);
        walibiExpress.setAttractionCategory(attractionCategories.TransportRides);
        walibiHolland.addChildAndSetParent(walibiExpress);

        CustomAttraction walibisFunRecorder = CustomAttraction.create("Walibi's Fun Recorder", 0, null);
        walibisFunRecorder.setAttractionCategory(attractionCategories.FamilyRides);
        walibisFunRecorder.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(walibisFunRecorder);

        CustomAttraction zensGraffityShuttle = CustomAttraction.create("Zen's Graffity Shuttle", 0, null);
        zensGraffityShuttle.setAttractionCategory(attractionCategories.FamilyRides);
        zensGraffityShuttle.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(zensGraffityShuttle);

        CustomAttraction crazyRiver = CustomAttraction.create("Crazy River", 2, null);
        crazyRiver.setAttractionCategory(attractionCategories.WaterRides);
        crazyRiver.setManufacturer(manufacturers.Mack);
        walibiHolland.addChildAndSetParent(crazyRiver);

        CustomAttraction elRioGrande = CustomAttraction.create("El Rio Grande", 2, null);
        elRioGrande.setAttractionCategory(attractionCategories.WaterRides);
        elRioGrande.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(elRioGrande);

        CustomAttraction splashBattle = CustomAttraction.create("SplashBattle", 0, null);
        splashBattle.setAttractionCategory(attractionCategories.WaterRides);
        splashBattle.setManufacturer(manufacturers.PrestonAndBarbieri);
        walibiHolland.addChildAndSetParent(splashBattle);



        LinkedHashMap<IOnSiteAttraction, Integer> rides01072019 = new LinkedHashMap<>();
        rides01072019.put(xpressPlatform13, 2);
        rides01072019.put(goliath, 5);
        rides01072019.put(lostGravity, 1);
        rides01072019.put(drako, 1);
        rides01072019.put(untamed, 3);
        rides01072019.put(spaceShot, 1);
        rides01072019.put(superSwing, 1);
        rides01072019.put(pavillonDeThe, 1);
        rides01072019.put(laGrandeRoue, 1);
        rides01072019.put(merlinsMagicCastle, 1);
        rides01072019.put(elRioGrande, 1);
        walibiHolland.addChildAndSetParent(this.createVisit(1, 7, 2019, rides01072019));
    }

    private void mockOsterwiese()
    {
        Park osterwiese = Park.create("Bremer Osterwiese", null);
        locations.Germany.addChildAndSetParent(osterwiese);

        //2018
        CustomCoaster bergUndTal = CustomCoaster.create("Berg & Tal", 1, null);
        bergUndTal.setAttractionCategory(attractionCategories.RollerCoasters);
        osterwiese.addChildAndSetParent(bergUndTal);

        //2019
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
        Park freimarkt = Park.create("Bremer Freimarkt", null);
        locations.Germany.addChildAndSetParent(freimarkt);

        //2018
        CustomCoaster alpinaBahn = CustomCoaster.create("Alpina Bahn", 2, null);
        alpinaBahn.setAttractionCategory(attractionCategories.RollerCoasters);
        alpinaBahn.setManufacturer(manufacturers.Schwarzkopf);
        freimarkt.addChildAndSetParent(alpinaBahn);

        CustomCoaster wildeMaus = CustomCoaster.create("Wilde Maus", 1, null);
        wildeMaus.setAttractionCategory(attractionCategories.RollerCoasters);
        freimarkt.addChildAndSetParent(wildeMaus);

        CustomCoaster euroCoaster = CustomCoaster.create("Euro Coaster", 1, null);
        euroCoaster.setAttractionCategory(attractionCategories.RollerCoasters);
        freimarkt.addChildAndSetParent(euroCoaster);
    }

    private void mockPortAventura()
    {
        Park portAventura = Park.create("Port Aventura", null);
        locations.Spain.addChildAndSetParent(portAventura);

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
        locations.Spain.addChildAndSetParent(ferrariLand);

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

    private void mockSlagharen()
    {
        Park slagharen = Park.create("Attractiepark Slagharen", null);
        locations.Netherlands.addChildAndSetParent(slagharen);

        CustomCoaster goldrush = CustomCoaster.create("Gold Rush", 0, null);
        goldrush.setAttractionCategory(attractionCategories.RollerCoasters);
        goldrush.setManufacturer(manufacturers.Gerstlauer);
        slagharen.addChildAndSetParent(goldrush);

        CustomCoaster minetrain = CustomCoaster.create("Mine Train", 0, null);
        minetrain.setAttractionCategory(attractionCategories.RollerCoasters);
        minetrain.setManufacturer(manufacturers.Vekoma);
        slagharen.addChildAndSetParent(minetrain);

        CustomAttraction ripsawFalls = CustomAttraction.create("Ripsaw Falls", 0, null);
        ripsawFalls.setAttractionCategory(attractionCategories.WaterRides);
        slagharen.addChildAndSetParent(ripsawFalls);

        CustomAttraction enterprise = CustomAttraction.create("Enterprise", 0, null);
        enterprise.setAttractionCategory(attractionCategories.ThrillRides);
        enterprise.setManufacturer(manufacturers.Schwarzkopf);
        slagharen.addChildAndSetParent(enterprise);

        CustomAttraction apollo = CustomAttraction.create("Apollo", 0, null);
        apollo.setAttractionCategory(attractionCategories.FamilyRides);
        apollo.setManufacturer(manufacturers.Schwarzkopf);
        slagharen.addChildAndSetParent(apollo);

        CustomAttraction freeFall = CustomAttraction.create("Free Fall", 0, null);
        freeFall.setAttractionCategory(attractionCategories.ThrillRides);
        slagharen.addChildAndSetParent(freeFall);

        CustomAttraction pirate = CustomAttraction.create("Pirate", 0, null);
        pirate.setAttractionCategory(attractionCategories.FamilyRides);
        pirate.setManufacturer(manufacturers.Huss);
        slagharen.addChildAndSetParent(pirate);

        CustomAttraction galoppers = CustomAttraction.create("Galoppers", 0, null);
        galoppers.setAttractionCategory(attractionCategories.FamilyRides);
        galoppers.setManufacturer(manufacturers.Zierer);
        slagharen.addChildAndSetParent(galoppers);

        StockAttraction eagle = StockAttraction.create("Eagle", attractionBlueprints.Condor, 0, null);
        slagharen.addChildAndSetParent(eagle);

        CustomAttraction tomahawk = CustomAttraction.create("Tomahawk", 0, null);
        tomahawk.setAttractionCategory(attractionCategories.FamilyRides);
        tomahawk.setManufacturer(manufacturers.Huss);
        slagharen.addChildAndSetParent(tomahawk);

        CustomAttraction wildWestAdventure = CustomAttraction.create("Wild West Adventure", 0, null);
        wildWestAdventure.setAttractionCategory(attractionCategories.DarkRides);
        wildWestAdventure.setManufacturer(manufacturers.Mack);
        slagharen.addChildAndSetParent(wildWestAdventure);

        CustomAttraction bigWheel = CustomAttraction.create("Big Wheel", 0, null);
        bigWheel.setAttractionCategory(attractionCategories.FamilyRides);
        bigWheel.setManufacturer(manufacturers.Schwarzkopf);
        slagharen.addChildAndSetParent(bigWheel);

        CustomAttraction kabelbaan = CustomAttraction.create("Kabelbaan", 0, null);
        kabelbaan.setAttractionCategory(attractionCategories.TransportRides);
        slagharen.addChildAndSetParent(kabelbaan);

        CustomAttraction monorail = CustomAttraction.create("Monorail", 0, null);
        monorail.setAttractionCategory(attractionCategories.TransportRides);
        monorail.setManufacturer(manufacturers.Schwarzkopf);
        slagharen.addChildAndSetParent(monorail);


        LinkedHashMap<IOnSiteAttraction, Integer> rides30062019 = new LinkedHashMap<>();
        rides30062019.put(goldrush, 3);
        rides30062019.put(minetrain, 1);
        rides30062019.put(freeFall, 2);
        rides30062019.put(enterprise, 1);
        rides30062019.put(pirate, 1);
        rides30062019.put(galoppers, 1);
        rides30062019.put(eagle, 1);
        rides30062019.put(apollo, 1);
        rides30062019.put(tomahawk, 1);
        rides30062019.put(ripsawFalls, 1);
        rides30062019.put(wildWestAdventure, 1);
        rides30062019.put(kabelbaan, 1);
        rides30062019.put(monorail, 1);
        rides30062019.put(bigWheel, 1);
        slagharen.addChildAndSetParent(this.createVisit(20, 6, 2019, rides30062019));
    }

    private void mockHolidayPark()
    {
        Park holidayPark = Park.create("Holiday Park", null);
        locations.Germany.addChildAndSetParent(holidayPark);

        CustomCoaster expeditionGeForce = CustomCoaster.create("Expedition GeForce", 0, null);
        expeditionGeForce.setAttractionCategory(attractionCategories.RollerCoasters);
        expeditionGeForce.setManufacturer(manufacturers.Intamin);
        holidayPark.addChildAndSetParent(expeditionGeForce);

        StockAttraction skyScream = StockAttraction.create("Sky Scream", coasterBlueprints.SkyRocketII, 0, null);
        holidayPark.addChildAndSetParent(skyScream);

        CustomAttraction burgFalkenstein = CustomAttraction.create("Burg Falkenstein", 0, null);
        burgFalkenstein.setAttractionCategory(attractionCategories.DarkRides);
        burgFalkenstein.setManufacturer(manufacturers.Mack);
        holidayPark.addChildAndSetParent(burgFalkenstein);

        CustomAttraction anubis = CustomAttraction.create("Anubis Free Fall Tower", 0, null);
        anubis.setAttractionCategory(attractionCategories.ThrillRides);
        anubis.setManufacturer(manufacturers.Intamin);
        holidayPark.addChildAndSetParent(anubis);

        LinkedHashMap<IOnSiteAttraction, Integer> rides13062018 = new LinkedHashMap<>();
        rides13062018.put(expeditionGeForce, 2);
        rides13062018.put(skyScream, 2);
        rides13062018.put(burgFalkenstein, 1);
        rides13062018.put(anubis, 2);
        holidayPark.addChildAndSetParent(this.createVisit(13, 6, 2018, rides13062018));
    }

    private void mockMovieParkGermany()
    {
        Park movieParkGermany = Park.create("Movie Park Germany", null);
        locations.Germany.addChildAndSetParent(movieParkGermany);

        CustomAttraction bermudaTriangle = CustomAttraction.create("Bermuda Triangle: Alien Encounter", 0, null);
        bermudaTriangle.setAttractionCategory(attractionCategories.DarkRides);
        bermudaTriangle.setManufacturer(manufacturers.Intamin);
        bermudaTriangle.setStatus(statuses.Converted);
        movieParkGermany.addChildAndSetParent(bermudaTriangle);

        CustomCoaster vanHelsingsFactory = CustomCoaster.create("Van Helsing's Factory", 0, null);
        vanHelsingsFactory.setAttractionCategory(attractionCategories.RollerCoasters);
        vanHelsingsFactory.setManufacturer(manufacturers.Gerstlauer);
        movieParkGermany.addChildAndSetParent(vanHelsingsFactory);

        CustomCoaster backyardigans = CustomCoaster.create("The Backyardigans: Mission to Mars", 0, null);
        backyardigans.setAttractionCategory(attractionCategories.RollerCoasters);
        backyardigans.setManufacturer(manufacturers.Vekoma);
        movieParkGermany.addChildAndSetParent(backyardigans);

        CustomAttraction dorasBigRiverAdventuer = CustomAttraction.create("Dora's Big River Adventure", 0, null);
        dorasBigRiverAdventuer.setAttractionCategory(attractionCategories.WaterRides);
        dorasBigRiverAdventuer.setManufacturer(manufacturers.Zamperla);
        movieParkGermany.addChildAndSetParent(dorasBigRiverAdventuer);

        CustomCoaster ghostChasers = CustomCoaster.create("Ghost Chasers", 0, null);
        ghostChasers.setAttractionCategory(attractionCategories.RollerCoasters);
        ghostChasers.setManufacturer(manufacturers.Mack);
        movieParkGermany.addChildAndSetParent(ghostChasers);

        CustomCoaster jimmyNeutronsAtomicFlyer = CustomCoaster.create("Jimmy Neutron's Atomic Flyer", 0, null);
        jimmyNeutronsAtomicFlyer.setAttractionCategory(attractionCategories.RollerCoasters);
        jimmyNeutronsAtomicFlyer.setManufacturer(manufacturers.Vekoma);
        movieParkGermany.addChildAndSetParent(jimmyNeutronsAtomicFlyer);

        CustomCoaster bandit = CustomCoaster.create("Bandit", 0, null);
        bandit.setAttractionCategory(attractionCategories.RollerCoasters);
        bandit.setManufacturer(manufacturers.PremierRides);
        movieParkGermany.addChildAndSetParent(bandit);

        StockAttraction mpXpress = StockAttraction.create("MP Xpress", coasterBlueprints.SuspendedLoopingCoaster, 0, null);
        movieParkGermany.addChildAndSetParent(mpXpress);

        CustomAttraction theHighFall = CustomAttraction.create("The High Fall", 0, null);
        theHighFall.setAttractionCategory(attractionCategories.ThrillRides);
        theHighFall.setManufacturer(manufacturers.Intamin);
        movieParkGermany.addChildAndSetParent(theHighFall);

        StockAttraction crazySurfer = StockAttraction.create("Crazy Surfer", attractionBlueprints.DiskO, 0, null);
        movieParkGermany.addChildAndSetParent(crazySurfer);

        CustomAttraction santaMonicaWheel = CustomAttraction.create("Santa Monica Wheel", 0, null);
        santaMonicaWheel.setAttractionCategory(attractionCategories.FamilyRides);
        santaMonicaWheel.setManufacturer(manufacturers.SbfVisa);
        movieParkGermany.addChildAndSetParent(santaMonicaWheel);

        CustomAttraction excalibur = CustomAttraction.create("Excalibur - Secrets of the Dark Forest", 0, null);
        excalibur.setAttractionCategory(attractionCategories.WaterRides);
        excalibur.setManufacturer(manufacturers.Intamin);
        movieParkGermany.addChildAndSetParent(excalibur);

        CustomCoaster starTrekOperationEnterprise = CustomCoaster.create("Star Trek: Operation Enterprise", 0, null);
        starTrekOperationEnterprise.setAttractionCategory(attractionCategories.RollerCoasters);
        starTrekOperationEnterprise.setManufacturer(manufacturers.Mack);
        movieParkGermany.addChildAndSetParent(starTrekOperationEnterprise);

        CustomAttraction timeRiders = CustomAttraction.create("Time Riders", 0, null);
        timeRiders.setAttractionCategory(attractionCategories.FamilyRides);
        movieParkGermany.addChildAndSetParent(timeRiders);

        StockAttraction NycTransformer = StockAttraction.create("NYC Transformer", attractionBlueprints.TopSpin, 0, null);
        movieParkGermany.addChildAndSetParent(NycTransformer);

        LinkedHashMap<IOnSiteAttraction, Integer> rides11062018 = new LinkedHashMap<>();
        rides11062018.put(bermudaTriangle, 1);
        rides11062018.put(vanHelsingsFactory, 2);
        rides11062018.put(backyardigans, 1);
        rides11062018.put(dorasBigRiverAdventuer, 1);
        rides11062018.put(ghostChasers, 2);
        rides11062018.put(jimmyNeutronsAtomicFlyer, 1);
        rides11062018.put(bandit, 1);
        rides11062018.put(mpXpress, 1);
        rides11062018.put(theHighFall, 3);
        rides11062018.put(crazySurfer, 1);
        rides11062018.put(santaMonicaWheel, 1);
        rides11062018.put(excalibur, 1);
        rides11062018.put(starTrekOperationEnterprise, 4);
        rides11062018.put(timeRiders, 1);
        rides11062018.put(NycTransformer, 1);
        movieParkGermany.addChildAndSetParent(this.createVisit(11, 6, 2018, rides11062018));
    }

    private void mockToverland()
    {
        Park toverland = Park.create("Toverland", null);
        this.locations.Netherlands.addChildAndSetParent(toverland);

        CustomCoaster troy = CustomCoaster.create("Troy", 10, null);
        troy.setAttractionCategory(attractionCategories.RollerCoasters);
        troy.setManufacturer(manufacturers.GCI);
        toverland.addChildAndSetParent(troy);

        CustomCoaster dwervelwind = CustomCoaster.create("Dwervelwind", 20, null);
        dwervelwind.setAttractionCategory(attractionCategories.RollerCoasters);
        dwervelwind.setManufacturer(manufacturers.Mack);
        toverland.addChildAndSetParent(dwervelwind);

        CustomCoaster boosterBike = CustomCoaster.create("Booster Bike", 3, null);
        boosterBike.setAttractionCategory(attractionCategories.RollerCoasters);
        boosterBike.setManufacturer(manufacturers.Vekoma);
        toverland.addChildAndSetParent(boosterBike);

        CustomCoaster toosExpress = CustomCoaster.create("Toos-Express", 2, null);
        toosExpress.setAttractionCategory(attractionCategories.RollerCoasters);
        toosExpress.setManufacturer(manufacturers.Vekoma);
        toverland.addChildAndSetParent(toosExpress);

        CustomCoaster fenix = CustomCoaster.create("Fēnix", 1, null);
        fenix.setAttractionCategory(attractionCategories.RollerCoasters);
        fenix.setManufacturer(manufacturers.BolligerAndMabillard);
        toverland.addChildAndSetParent(fenix);

        CustomAttraction expeditionZork = CustomAttraction.create("Expedition Zork", 3, null);
        expeditionZork.setAttractionCategory(attractionCategories.WaterRides);
        expeditionZork.setManufacturer(manufacturers.Mack);
        toverland.addChildAndSetParent(expeditionZork);

        CustomAttraction maximusBlitzbahn = CustomAttraction.create("Maximus' Blitz Bahn", 1, null);
        maximusBlitzbahn.setAttractionCategory(attractionCategories.FamilyRides);
        toverland.addChildAndSetParent(maximusBlitzbahn);

        CustomAttraction scorpios = CustomAttraction.create("Scorpios", 2, null);
        scorpios.setAttractionCategory(attractionCategories.FamilyRides);
        toverland.addChildAndSetParent(scorpios);

        CustomAttraction djenguRiver = CustomAttraction.create("Djengu River", 2, null);
        djenguRiver.setAttractionCategory(attractionCategories.WaterRides);
        djenguRiver.setManufacturer(manufacturers.Hafema);
        toverland.addChildAndSetParent(djenguRiver);

        CustomAttraction merlinsQuest = CustomAttraction.create("Merlin's Quest", 1, null);
        merlinsQuest.setAttractionCategory(attractionCategories.DarkRides);
        merlinsQuest.setManufacturer(manufacturers.Mack);
        toverland.addChildAndSetParent(merlinsQuest);

        CustomAttraction villaFiasco = CustomAttraction.create("Villa Fiasco", 1, null);
        villaFiasco.setAttractionCategory(attractionCategories.FamilyRides);
        toverland.addChildAndSetParent(villaFiasco);
    }

    private void mockEfteling()
    {
        Park efteling = Park.create("Efteling", null);
        locations.Netherlands.addChildAndSetParent(efteling);

        CustomCoaster jorisEnDeDraakWater = CustomCoaster.create("Joris en de Draak (Water)", 2, null);
        jorisEnDeDraakWater.setAttractionCategory(attractionCategories.RollerCoasters);
        jorisEnDeDraakWater.setManufacturer(manufacturers.GCI);
        efteling.addChildAndSetParent(jorisEnDeDraakWater);

        CustomCoaster jorisEnDeDraakVuur = CustomCoaster.create("Joris en de Draak (Vuur)", 1, null);
        jorisEnDeDraakVuur.setAttractionCategory(attractionCategories.RollerCoasters);
        jorisEnDeDraakVuur.setManufacturer(manufacturers.GCI);
        efteling.addChildAndSetParent(jorisEnDeDraakVuur);

        CustomCoaster baron1898 = CustomCoaster.create("Baron 1898", 5, null);
        baron1898.setAttractionCategory(attractionCategories.RollerCoasters);
        baron1898.setManufacturer(manufacturers.BolligerAndMabillard);
        efteling.addChildAndSetParent(baron1898);

        CustomCoaster python = CustomCoaster.create("Python", 1, null);
        python.setAttractionCategory(attractionCategories.RollerCoasters);
        python.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(python);

        CustomCoaster deVliegendeHollander = CustomCoaster.create("De Vliegende Hollander", 2, null);
        deVliegendeHollander.setAttractionCategory(attractionCategories.RollerCoasters);
        efteling.addChildAndSetParent(deVliegendeHollander);

        CustomCoaster vogelRok = CustomCoaster.create("Vogel Rok", 1, null);
        vogelRok.setAttractionCategory(attractionCategories.RollerCoasters);
        vogelRok.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(vogelRok);

        CustomCoaster bobbaan = CustomCoaster.create("Bobbaan", 2, null);
        bobbaan.setAttractionCategory(attractionCategories.RollerCoasters);
        bobbaan.setManufacturer(manufacturers.Intamin);
        bobbaan.setStatus(statuses.Defunct);
        efteling.addChildAndSetParent(bobbaan);

        CustomAttraction fataMorgana = CustomAttraction.create("Fata Morgana", 2, null);
        fataMorgana.setAttractionCategory(attractionCategories.DarkRides);
        fataMorgana.setManufacturer(manufacturers.Intamin);
        efteling.addChildAndSetParent(fataMorgana);

        CustomAttraction carnevalFestival = CustomAttraction.create("CarnevalFestival", 2, null);
        carnevalFestival.setAttractionCategory(attractionCategories.DarkRides);
        carnevalFestival.setManufacturer(manufacturers.Mack);
        efteling.addChildAndSetParent(carnevalFestival);

        CustomAttraction droomvlucht = CustomAttraction.create("Droomvlucht", 2, null);
        droomvlucht.setAttractionCategory(attractionCategories.DarkRides);
        droomvlucht.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(droomvlucht);

        CustomAttraction symbolica = CustomAttraction.create("Symbolica", 4, null);
        symbolica.setAttractionCategory(attractionCategories.DarkRides);
        symbolica.setManufacturer(manufacturers.EtfRideSystems);
        efteling.addChildAndSetParent(symbolica);

        CustomAttraction pirana = CustomAttraction.create("Piraña", 2, null);
        pirana.setAttractionCategory(attractionCategories.WaterRides);
        pirana.setManufacturer(manufacturers.Intamin);
        efteling.addChildAndSetParent(pirana);

        CustomAttraction stoomtrein = CustomAttraction.create("Efteling Stoomtrein", 1, null);
        stoomtrein.setAttractionCategory(attractionCategories.TransportRides);
        efteling.addChildAndSetParent(stoomtrein);

        CustomAttraction halveMaen = CustomAttraction.create("Halve Maen", 2, null);
        halveMaen.setAttractionCategory(attractionCategories.FamilyRides);
        halveMaen.setManufacturer(manufacturers.Intamin);
        efteling.addChildAndSetParent(halveMaen);

        CustomAttraction polkaMarina = CustomAttraction.create("Polka Marina", 1, null);
        polkaMarina.setAttractionCategory(attractionCategories.FamilyRides);
        polkaMarina.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(polkaMarina);

        CustomAttraction spookslot = CustomAttraction.create("Spookslot", 1, null);
        spookslot.setAttractionCategory(attractionCategories.FamilyRides);
        efteling.addChildAndSetParent(spookslot);

        CustomAttraction villaVolta = CustomAttraction.create("Villa Volta", 2, null);
        villaVolta.setAttractionCategory(attractionCategories.FamilyRides);
        villaVolta.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(villaVolta);
    }

    private void mockHansaPark()
    {
        Park hansaPark = Park.create("Hansa Park", null);
        locations.Germany.addChildAndSetParent(hansaPark);

        CustomCoaster fluchVonNovgorod = CustomCoaster.create("Fluch von Novgorod", 4, null);
        fluchVonNovgorod.setAttractionCategory(attractionCategories.RollerCoasters);
        fluchVonNovgorod.setManufacturer(manufacturers.Gerstlauer);
        hansaPark.addChildAndSetParent(fluchVonNovgorod);

        CustomCoaster schwurDesKaernan = CustomCoaster.create("Der Schwur des Kärnan", 6, null);
        schwurDesKaernan.setAttractionCategory(attractionCategories.RollerCoasters);
        schwurDesKaernan.setManufacturer(manufacturers.Gerstlauer);
        hansaPark.addChildAndSetParent(schwurDesKaernan);

        CustomCoaster nessie = CustomCoaster.create("Nessie", 3, null);
        nessie.setAttractionCategory(attractionCategories.RollerCoasters);
        nessie.setManufacturer(manufacturers.Schwarzkopf);
        hansaPark.addChildAndSetParent(nessie);

        CustomCoaster crazyMine = CustomCoaster.create("Crazy-Mine", 3, null);
        crazyMine.setAttractionCategory(attractionCategories.RollerCoasters);
        crazyMine.setManufacturer(manufacturers.MaurerSoehne);
        hansaPark.addChildAndSetParent(crazyMine);

        CustomCoaster rasenderRoland = CustomCoaster.create("Rasender Roland", 2, null);
        rasenderRoland.setAttractionCategory(attractionCategories.RollerCoasters);
        rasenderRoland.setManufacturer(manufacturers.Vekoma);
        hansaPark.addChildAndSetParent(rasenderRoland);

        CustomCoaster schlangeVonMidgard = CustomCoaster.create("Schlange von Midgard", 2, null);
        schlangeVonMidgard.setAttractionCategory(attractionCategories.RollerCoasters);
        schlangeVonMidgard.setManufacturer(manufacturers.Gerstlauer);
        hansaPark.addChildAndSetParent(schlangeVonMidgard);

        CustomCoaster derKleineZar = CustomCoaster.create("Der kleine Zar", 1, null);
        derKleineZar.setAttractionCategory(attractionCategories.RollerCoasters);
        derKleineZar.setManufacturer(manufacturers.PrestonAndBarbieri);
        hansaPark.addChildAndSetParent(derKleineZar);

        CustomAttraction wasserwolfAmIlmensee = CustomAttraction.create("Der Wasserwolf am Ilmensee", 2, null);
        wasserwolfAmIlmensee.setAttractionCategory(attractionCategories.WaterRides);
        wasserwolfAmIlmensee.setManufacturer(manufacturers.Mack);
        hansaPark.addChildAndSetParent(wasserwolfAmIlmensee);

        CustomAttraction superSplash = CustomAttraction.create("Super Splash", 2, null);
        superSplash.setAttractionCategory(attractionCategories.WaterRides);
        superSplash.setManufacturer(manufacturers.Intamin);
        hansaPark.addChildAndSetParent(superSplash);

        CustomAttraction stoertebekersKaperfahrt = CustomAttraction.create("Störtebeker's Kaperfahrt", 1, null);
        stoertebekersKaperfahrt.setAttractionCategory(attractionCategories.WaterRides);
        hansaPark.addChildAndSetParent(stoertebekersKaperfahrt);

        CustomAttraction sturmfahrtDerDrachenboote = CustomAttraction.create("Sturmfahrt der Drachenboote", 1, null);
        sturmfahrtDerDrachenboote.setAttractionCategory(attractionCategories.WaterRides);
        hansaPark.addChildAndSetParent(sturmfahrtDerDrachenboote);

        CustomAttraction fliegenderHollaender = CustomAttraction.create("Fliegender Holländer", 1, null);
        fliegenderHollaender.setAttractionCategory(attractionCategories.FamilyRides);
        fliegenderHollaender.setManufacturer(manufacturers.Huss);
        hansaPark.addChildAndSetParent(fliegenderHollaender);

        CustomAttraction holsteinturm = CustomAttraction.create("Holsteinturm", 1, null);
        holsteinturm.setAttractionCategory(attractionCategories.FamilyRides);
        holsteinturm.setManufacturer(manufacturers.Huss);
        hansaPark.addChildAndSetParent(holsteinturm);

        StockAttraction kaernapulten = StockAttraction.create("Kärnapulten", attractionBlueprints.SkyFly, 1, null);
        hansaPark.addChildAndSetParent(kaernapulten);

        CustomAttraction kettenflieger = CustomAttraction.create("Kettenflieger", 1, null);
        kettenflieger.setAttractionCategory(attractionCategories.FamilyRides);
        hansaPark.addChildAndSetParent(kettenflieger);

        StockAttraction fliegenderHai = StockAttraction.create("Fliegender Hai", attractionBlueprints.Ranger, 2, null);
        fliegenderHai.setStatus(statuses.Defunct);
        hansaPark.addChildAndSetParent(fliegenderHai);
    }

    private void mockMagicParkVerden()
    {
        Park magicParkVerden = Park.create("Magic Park Verden", null);
        locations.Germany.addChildAndSetParent(magicParkVerden);

        CustomCoaster achterbahn = CustomCoaster.create("Magic Park Achterbahn", 0, null);
        achterbahn.setAttractionCategory(attractionCategories.RollerCoasters);
        magicParkVerden.addChildAndSetParent(achterbahn);

        CustomAttraction wildwasserbahn = CustomAttraction.create("Wildwasserbahn", 0, null);
        wildwasserbahn.setAttractionCategory(attractionCategories.WaterRides);
        magicParkVerden.addChildAndSetParent(wildwasserbahn);



        LinkedHashMap<IOnSiteAttraction, Integer> rides09062018 = new LinkedHashMap<>();
        rides09062018.put(achterbahn, 4);
        rides09062018.put(wildwasserbahn, 1);
        magicParkVerden.addChildAndSetParent(this.createVisit(9, 6, 2018, rides09062018));
    }

    private void mockEuropaPark()
    {
        Park europaPark = Park.create("Europa Park", null);
        locations.Germany.addChildAndSetParent(europaPark);

        CustomCoaster silverStar = CustomCoaster.create("Silver Star", 5, null);
        silverStar.setAttractionCategory(attractionCategories.RollerCoasters);
        silverStar.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(silverStar);

        CustomCoaster blueFire = CustomCoaster.create("Blue Fire Megacoaster", 4, null);
        blueFire.setAttractionCategory(attractionCategories.RollerCoasters);
        blueFire.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(blueFire);

        CustomCoaster wodan = CustomCoaster.create("Wodan - Timburcoaster", 4, null);
        wodan.setAttractionCategory(attractionCategories.RollerCoasters);
        wodan.setManufacturer(manufacturers.GCI);
        europaPark.addChildAndSetParent(wodan);

        CustomCoaster eurosatCanCanCoaster = CustomCoaster.create("Eurosat - Can Can Coaster", 0, null);
        eurosatCanCanCoaster.setAttractionCategory(attractionCategories.RollerCoasters);
        eurosatCanCanCoaster.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(eurosatCanCanCoaster);

        CustomCoaster euroMir = CustomCoaster.create("Euro-Mir", 1, null);
        euroMir.setAttractionCategory(attractionCategories.RollerCoasters);
        euroMir.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(euroMir);

        CustomCoaster alpenexpressEnzian = CustomCoaster.create("Alpenexpress Enzian", 1, null);
        alpenexpressEnzian.setAttractionCategory(attractionCategories.RollerCoasters);
        alpenexpressEnzian.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(alpenexpressEnzian);

        CustomCoaster schweizerBobbahn = CustomCoaster.create("Schweizer Bobbahn", 2, null);
        schweizerBobbahn.setAttractionCategory(attractionCategories.RollerCoasters);
        schweizerBobbahn.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(schweizerBobbahn);

        CustomCoaster matterhornBlitz = CustomCoaster.create("Matterhorn-Blitz", 2, null);
        matterhornBlitz.setAttractionCategory(attractionCategories.RollerCoasters);
        matterhornBlitz.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(matterhornBlitz);

        CustomCoaster pegasus = CustomCoaster.create("Pegasus", 1, null);
        pegasus.setAttractionCategory(attractionCategories.RollerCoasters);
        pegasus.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(pegasus);

        CustomCoaster baaaExpress = CustomCoaster.create("Ba-a-a Express", 1, null);
        baaaExpress.setAttractionCategory(attractionCategories.RollerCoasters);
        baaaExpress.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(baaaExpress);

        CustomCoaster arthur = CustomCoaster.create("Arthur", 1, null);
        arthur.setAttractionCategory(attractionCategories.RollerCoasters);
        arthur.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(arthur);

        CustomCoaster poseidon = CustomCoaster.create("Poseidon", 2, null);
        poseidon.setAttractionCategory(attractionCategories.RollerCoasters);
        poseidon.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(poseidon);

        CustomCoaster atlantica = CustomCoaster.create("Atlantica SuperSplash", 2, null);
        atlantica.setAttractionCategory(attractionCategories.RollerCoasters);
        atlantica.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(atlantica);

        CustomAttraction abenteuerAtlantis = CustomAttraction.create("Abenteuer Atlantis", 1, null);
        abenteuerAtlantis.setAttractionCategory(attractionCategories.DarkRides);
        abenteuerAtlantis.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(abenteuerAtlantis);

        CustomAttraction epExpress = CustomAttraction.create("EP-Express", 1, null);
        epExpress.setAttractionCategory(attractionCategories.TransportRides);
        europaPark.addChildAndSetParent(epExpress);

        CustomAttraction euroTower = CustomAttraction.create("Euro-Tower", 1, null);
        euroTower.setAttractionCategory(attractionCategories.FamilyRides);
        euroTower.setManufacturer(manufacturers.Intamin);
        europaPark.addChildAndSetParent(euroTower);

        CustomAttraction fjordRafting = CustomAttraction.create("Fjord Rafting", 2, null);
        fjordRafting.setAttractionCategory(attractionCategories.WaterRides);
        fjordRafting.setManufacturer(manufacturers.Intamin);
        europaPark.addChildAndSetParent(fjordRafting);

        CustomAttraction fluchDerKassandra = CustomAttraction.create("Fluch der Kassandra", 1, null);
        fluchDerKassandra.setAttractionCategory(attractionCategories.FamilyRides);
        fluchDerKassandra.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(fluchDerKassandra);

        CustomAttraction geisterschloss = CustomAttraction.create("Geisterschloss", 1, null);
        geisterschloss.setAttractionCategory(attractionCategories.DarkRides);
        geisterschloss.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(geisterschloss);

        CustomAttraction piccoloMondo = CustomAttraction.create("Piccolo Mondo", 1, null);
        piccoloMondo.setAttractionCategory(attractionCategories.DarkRides);
        piccoloMondo.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(piccoloMondo);

        CustomAttraction schlittenfahrtSchneefloeckchen = CustomAttraction.create("Schlittenfahrt Schneeflöckchen", 1, null);
        schlittenfahrtSchneefloeckchen.setAttractionCategory(attractionCategories.DarkRides);
        schlittenfahrtSchneefloeckchen.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(schlittenfahrtSchneefloeckchen);

        CustomAttraction tirolerWildwasserbahn = CustomAttraction.create("Tiroler Wildwasserbahn", 2, null);
        tirolerWildwasserbahn.setAttractionCategory(attractionCategories.WaterRides);
        tirolerWildwasserbahn.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(tirolerWildwasserbahn);

        CustomAttraction vindjammer = CustomAttraction.create("Vindjammer", 2, null);
        vindjammer.setAttractionCategory(attractionCategories.FamilyRides);
        vindjammer.setManufacturer(manufacturers.Huss);
        europaPark.addChildAndSetParent(vindjammer);

        CustomAttraction voletarium = CustomAttraction.create("Voletarium", 2, null);
        voletarium.setAttractionCategory(attractionCategories.FamilyRides);
        europaPark.addChildAndSetParent(voletarium);

        CustomAttraction wienerWellenflieger = CustomAttraction.create("Wiener Wellenflieger", 1, null);
        wienerWellenflieger.setAttractionCategory(attractionCategories.FamilyRides);
        wienerWellenflieger.setManufacturer(manufacturers.Zierer);
        europaPark.addChildAndSetParent(wienerWellenflieger);

        CustomAttraction kolumbusjolle = CustomAttraction.create("Kolumbusjolle", 1, null);
        kolumbusjolle.setAttractionCategory(attractionCategories.FamilyRides);
        kolumbusjolle.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(kolumbusjolle);
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

    @Override
    public int fetchTotalCoasterCreditsCount()
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.fetchTotalCoasterCreditsCount:: empty implementation to satisfy interface");
        return 0;
    }

    @Override
    public int fetchTotalCoasterRidesCount()
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.fetchTotalCoasterRidesCount:: empty implementation to satisfy interface");
        return 0;
    }

    @Override
    public int fetchTotalVisitedParksCount()
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.fetchTotalVisitedParksCount:: empty implementation to satisfy interface");
        return 0;
    }

    @Override
    public List<Visit> fetchCurrentVisits()
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.fetchCurrentVisits:: empty implementation to satisfy interface");
        return null;
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
    Manufacturer RMC = Manufacturer.create("Rocky Mountain Construction", null);
    Manufacturer Gerstlauer = Manufacturer.create("Gerstlauer Amusement Rides", null);
    Manufacturer PremierRides = Manufacturer.create("Premier Rides", null);
    Manufacturer GCI = Manufacturer.create("Great Coasters International", null);

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
        All.add(RMC);
        All.add(Gerstlauer);
        All.add(PremierRides);
        All.add(GCI);
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
    Location Netherlands = Location.create("Netherlands", null);
    Location Spain = Location.create("Spain", null);

    Locations()
    {
        Europe.addChildAndSetParent(Germany);
        Europe.addChildAndSetParent(Netherlands);
        Europe.addChildAndSetParent(Spain);
    }
}

class CoasterBlueprints
{
    CoasterBlueprint SuspendedLoopingCoaster = CoasterBlueprint.create("Suspended Looping Coaster", null);
    CoasterBlueprint Boomerang = CoasterBlueprint.create("Boomerang", null);
    CoasterBlueprint BigAppleMB28 = CoasterBlueprint.create("Big Apple", null);
    CoasterBlueprint SkyRocketII = CoasterBlueprint.create("Sky Rocker II", null);

    List<CoasterBlueprint> All = new LinkedList<>();

    CoasterBlueprints(Manufacturers manufacturers, AttractionCategories attractionCategories)
    {
        SuspendedLoopingCoaster.setManufacturer(manufacturers.Vekoma);
        SuspendedLoopingCoaster.setAttractionCategory(attractionCategories.RollerCoasters);

        Boomerang.setManufacturer(manufacturers.Vekoma);
        Boomerang.setAttractionCategory(attractionCategories.RollerCoasters);

        BigAppleMB28.setManufacturer(manufacturers.Pinfari);
        BigAppleMB28.setAttractionCategory(attractionCategories.RollerCoasters);

        SkyRocketII.setManufacturer(manufacturers.PremierRides);
        SkyRocketII.setAttractionCategory(attractionCategories.RollerCoasters);


        All.add(SuspendedLoopingCoaster);
        All.add(Boomerang);
        All.add(BigAppleMB28);
        All.add(SkyRocketII);
    }
}

class AttractionBlueprints
{
    AttractionBlueprint TopSpin = AttractionBlueprint.create("Top Spin", null);
    AttractionBlueprint SuspendedTopSpin = AttractionBlueprint.create("Suspended Top Spin", null);
    AttractionBlueprint BreakDancer = AttractionBlueprint.create("Break Dancer", null);
    AttractionBlueprint DiskO = AttractionBlueprint.create("Disk'O", null);
    AttractionBlueprint Condor = AttractionBlueprint.create("Condor", null);
    AttractionBlueprint SkyFly = AttractionBlueprint.create("Sky Fly", null);
    AttractionBlueprint Ranger = AttractionBlueprint.create("Ranger", null);

    List<AttractionBlueprint> All = new LinkedList<>();

    AttractionBlueprints(Manufacturers manufacturers, AttractionCategories attractionCategories)
    {
        TopSpin.setAttractionCategory(attractionCategories.ThrillRides);
        TopSpin.setManufacturer(manufacturers.Huss);

        SuspendedTopSpin.setAttractionCategory(attractionCategories.ThrillRides);
        SuspendedTopSpin.setManufacturer(manufacturers.Huss);

        BreakDancer.setAttractionCategory(attractionCategories.ThrillRides);
        BreakDancer.setManufacturer(manufacturers.Huss);

        DiskO.setAttractionCategory(attractionCategories.FamilyRides);
        DiskO.setManufacturer(manufacturers.Zamperla);

        Condor.setAttractionCategory(attractionCategories.FamilyRides);
        Condor.setManufacturer(manufacturers.Huss);

        SkyFly.setAttractionCategory(attractionCategories.ThrillRides);
        SkyFly.setManufacturer(manufacturers.Gerstlauer);

        Ranger.setAttractionCategory(attractionCategories.ThrillRides);
        Ranger.setManufacturer(manufacturers.Huss);

        All.add(TopSpin);
        All.add(SuspendedTopSpin);
        All.add(BreakDancer);
        All.add(DiskO);
        All.add(Condor);
        All.add(SkyFly);
        All.add(Ranger);
    }
}
