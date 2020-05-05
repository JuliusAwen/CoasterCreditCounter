package de.juliusawen.coastercreditcounter.persistence.databaseMock;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Constants;
import de.juliusawen.coastercreditcounter.application.Content;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.dataModel.statistics.StatisticsGlobalTotals;
import de.juliusawen.coastercreditcounter.persistence.IDatabaseWrapper;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;

public final class DatabaseMock implements IDatabaseWrapper
{
    private final CreditTypes creditTypes;
    private final Categories categories;
    private final Manufacturers manufacturers;
    private final Statuses statuses;
    private final Locations locations;

    private static DatabaseMock instance;

    public static DatabaseMock getInstance()
    {
        if(DatabaseMock.instance == null)
        {
            DatabaseMock.instance = new DatabaseMock();
        }

        return instance;
    }

    private DatabaseMock()
    {
        Log.e(Constants.LOG_TAG, Constants.LOG_DIVIDER_ON_CREATE + "DatabaseMock.Constructor:: initializing DatabaseMock...");

        this.creditTypes = new CreditTypes();
        this.categories = new Categories();
        this.manufacturers = new Manufacturers();
        this.statuses = new Statuses();
        this.locations = new Locations();
    }

    @Override
    public boolean loadContent(Content content)
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.loadContent:: mocking Parks and Visits...");

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
        this.mockEnergylandia();

        Log.e(Constants.LOG_TAG, "DatabaseMock.loadContent:: creating node tree");
        content.addElement(locations.Bremen); //adding one location is enough - content is searching for root from there
        this.flattenContentTree(App.content.getRootLocation());

        Log.i(Constants.LOG_TAG, "DatabaseMock.loadContent:: adding Properties to content");
        content.addElements(ConvertTool.convertElementsToType(creditTypes.AllCreditTypes, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(categories.AllCategories, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(manufacturers.AllManufacturers, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(statuses.AllStatuses, IElement.class));

        Log.e(Constants.LOG_TAG, String.format(Constants.LOG_DIVIDER_ON_CREATE + "DatabaseMock.loadContent:: mock data successfully created - took [%d]ms", stopwatch.stop()));
        return true;
    }

    private void mockPhantasialand()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockPhantasialand");

        Park phantasialand = Park.create("Phantasialand");
        locations.Germany.addChildAndSetParent(phantasialand);

        OnSiteAttraction taron = OnSiteAttraction.create("Taron", 38);
        taron.setCreditType(creditTypes.RollerCoaster);
        taron.setCategory(categories.RollerCoasters);
        taron.setManufacturer(manufacturers.Intamin);
        phantasialand.addChildAndSetParent(taron);

        OnSiteAttraction blackMamba = OnSiteAttraction.create("Black Mamba", 18);
        blackMamba.setCreditType(creditTypes.RollerCoaster);
        blackMamba.setCategory(categories.RollerCoasters);
        blackMamba.setManufacturer(manufacturers.BolligerAndMabillard);
        phantasialand.addChildAndSetParent(blackMamba);

        OnSiteAttraction fly = OnSiteAttraction.create("F.L.Y.");
        fly.setCreditType(creditTypes.RollerCoaster);
        fly.setCategory(categories.RollerCoasters);
        fly.setManufacturer(manufacturers.Vekoma);
        fly.setStatus(statuses.UnderConstruction);
        phantasialand.addChildAndSetParent(fly);

        OnSiteAttraction coloradoAdventure = OnSiteAttraction.create("Colorado Adventure", 11);
        coloradoAdventure.setCreditType(creditTypes.RollerCoaster);
        coloradoAdventure.setCategory(categories.RollerCoasters);
        coloradoAdventure.setManufacturer(manufacturers.Vekoma);
        phantasialand.addChildAndSetParent(coloradoAdventure);

        OnSiteAttraction winjasFear = OnSiteAttraction.create("Winja's Fear", 8);
        winjasFear.setCreditType(creditTypes.RollerCoaster);
        winjasFear.setCategory(categories.RollerCoasters);
        winjasFear.setManufacturer(manufacturers.MaurerRides);
        phantasialand.addChildAndSetParent(winjasFear);

        OnSiteAttraction winjasForce = OnSiteAttraction.create("Winja's Force", 8);
        winjasForce.setCreditType(creditTypes.RollerCoaster);
        winjasForce.setCategory(categories.RollerCoasters);
        winjasForce.setManufacturer(manufacturers.MaurerRides);
        phantasialand.addChildAndSetParent(winjasForce);

        OnSiteAttraction raik = OnSiteAttraction.create("Raik", 5);
        raik.setCreditType(creditTypes.RollerCoaster);
        raik.setCategory(categories.RollerCoasters);
        raik.setManufacturer(manufacturers.Vekoma);
        phantasialand.addChildAndSetParent(raik);

        OnSiteAttraction templeOfTheNightHawk = OnSiteAttraction.create("Temple of the Night Hawk", 9);
        templeOfTheNightHawk.setCreditType(creditTypes.RollerCoaster);
        templeOfTheNightHawk.setCategory(categories.RollerCoasters);
        templeOfTheNightHawk.setManufacturer(manufacturers.Vekoma);
        phantasialand.addChildAndSetParent(templeOfTheNightHawk);

        OnSiteAttraction mysteryCastle = OnSiteAttraction.create("Mystery Castle");
        mysteryCastle.setCategory(categories.ThrillRides);
        mysteryCastle.setManufacturer(manufacturers.Intamin);
        phantasialand.addChildAndSetParent(mysteryCastle);

        OnSiteAttraction hollywoodTour = OnSiteAttraction.create("Hollywood Tour");
        hollywoodTour.setCategory(categories.DarkRides);
        hollywoodTour.setManufacturer(manufacturers.Intamin);
        phantasialand.addChildAndSetParent(hollywoodTour);

        OnSiteAttraction chiapas = OnSiteAttraction.create("Chiapas", 10);
        chiapas.setCategory(categories.WaterRides);
        chiapas.setManufacturer(manufacturers.Intamin);
        phantasialand.addChildAndSetParent(chiapas);

        OnSiteAttraction talocan = OnSiteAttraction.create("Talocan");
        talocan.setCategory(categories.ThrillRides);
        talocan.setManufacturer(manufacturers.Huss);
        phantasialand.addChildAndSetParent(talocan);

        OnSiteAttraction fengJuPalace = OnSiteAttraction.create("Feng Ju Palace");
        fengJuPalace.setCategory(categories.FamilyRides);
        fengJuPalace.setManufacturer(manufacturers.Vekoma);
        phantasialand.addChildAndSetParent(fengJuPalace);

        OnSiteAttraction geisterRiksha = OnSiteAttraction.create("Geister Rikscha");
        geisterRiksha.setCategory(categories.DarkRides);
        geisterRiksha.setManufacturer(manufacturers.Schwarzkopf);
        phantasialand.addChildAndSetParent(geisterRiksha);

        OnSiteAttraction mausAuChocolat = OnSiteAttraction.create("Maus-Au-Chocolat", 1);
        mausAuChocolat.setCategory(categories.DarkRides);
        mausAuChocolat.setManufacturer(manufacturers.EtfRideSystems);
        phantasialand.addChildAndSetParent(mausAuChocolat);

        OnSiteAttraction wellenflug = OnSiteAttraction.create("Wellenflug");
        wellenflug.setCategory(categories.FamilyRides);
        wellenflug.setManufacturer(manufacturers.Zierer);
        phantasialand.addChildAndSetParent(wellenflug);

        OnSiteAttraction tikal = OnSiteAttraction.create("Tikal", 1);
        tikal.setCategory(categories.FamilyRides);
        tikal.setManufacturer(manufacturers.Zierer);
        phantasialand.addChildAndSetParent(tikal);

        OnSiteAttraction verruecktesHotelTartueff = OnSiteAttraction.create("Verrücktes Hotel Tartüff");
        verruecktesHotelTartueff.setCategory(categories.FamilyRides);
        verruecktesHotelTartueff.setManufacturer(manufacturers.Hofmann);
        phantasialand.addChildAndSetParent(verruecktesHotelTartueff);

        OnSiteAttraction riverQuest = OnSiteAttraction.create("River Quest");
        riverQuest.setCategory(categories.WaterRides);
        riverQuest.setManufacturer(manufacturers.Hafema);
        phantasialand.addChildAndSetParent(riverQuest);

        OnSiteAttraction pferdekarusell = OnSiteAttraction.create("Pferdekarusell");
        pferdekarusell.setCategory(categories.FamilyRides);
        pferdekarusell.setManufacturer(manufacturers.PrestonAndBarbieri);
        phantasialand.addChildAndSetParent(pferdekarusell);

        OnSiteAttraction wuermlingExpress = OnSiteAttraction.create("Würmling Express");
        wuermlingExpress.setCategory(categories.FamilyRides);
        wuermlingExpress.setManufacturer(manufacturers.PrestonAndBarbieri);
        phantasialand.addChildAndSetParent(wuermlingExpress);


        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides14122018 = new LinkedHashMap<>();
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

        LinkedHashMap<OnSiteAttraction, Integer> rides28092019 = new LinkedHashMap<>();
        rides28092019.put(coloradoAdventure, 1);
        rides28092019.put(blackMamba, 1);
        rides28092019.put(winjasForce, 1);
        rides28092019.put(winjasFear, 1);
        rides28092019.put(taron, 2);
        rides28092019.put(mysteryCastle, 1);
        rides28092019.put(talocan, 1);
        rides28092019.put(fengJuPalace, 1);
        rides28092019.put(tikal, 1);
        rides28092019.put(wellenflug, 1);
        rides28092019.put(chiapas, 1);
        rides28092019.put(riverQuest, 1);
        rides28092019.put(geisterRiksha, 1);
        phantasialand.addChildAndSetParent(this.createVisit(28, 9, 2019, rides28092019));
    }

    private void mockHeidePark()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockHeidePark");

        Park heidePark = Park.create("Heide Park");
        locations.Germany.addChildAndSetParent(heidePark);

        OnSiteAttraction colossos = OnSiteAttraction.create("Colossos");
        colossos.setCreditType(creditTypes.RollerCoaster);
        colossos.setCategory(categories.RollerCoasters);
        colossos.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(colossos);

        OnSiteAttraction krake = OnSiteAttraction.create("Krake", 14);
        krake.setCreditType(creditTypes.RollerCoaster);
        krake.setCategory(categories.RollerCoasters);
        krake.setManufacturer(manufacturers.BolligerAndMabillard);
        heidePark.addChildAndSetParent(krake);

        OnSiteAttraction flugDerDaemonen = OnSiteAttraction.create("Flug der Dämonen", 11);
        flugDerDaemonen.setCreditType(creditTypes.RollerCoaster);
        flugDerDaemonen.setCategory(categories.RollerCoasters);
        flugDerDaemonen.setManufacturer(manufacturers.BolligerAndMabillard);
        heidePark.addChildAndSetParent(flugDerDaemonen);

        OnSiteAttraction desertRace = OnSiteAttraction.create("Desert Race", 10);
        desertRace.setCreditType(creditTypes.RollerCoaster);
        desertRace.setCategory(categories.RollerCoasters);
        desertRace.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(desertRace);

        OnSiteAttraction bigLoop = OnSiteAttraction.create("Big Loop", 2);
        bigLoop.setCreditType(creditTypes.RollerCoaster);
        bigLoop.setCategory(categories.RollerCoasters);
        bigLoop.setManufacturer(manufacturers.Vekoma);
        heidePark.addChildAndSetParent(bigLoop);

        OnSiteAttraction grottenblitz = OnSiteAttraction.create("Grottenblitz", 1);
        grottenblitz.setCreditType(creditTypes.RollerCoaster);
        grottenblitz.setCategory(categories.RollerCoasters);
        grottenblitz.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(grottenblitz);

        OnSiteAttraction limit = OnSiteAttraction.create("Limit", 1);
        limit.setCreditType(creditTypes.RollerCoaster);
        limit.setCategory(categories.RollerCoasters);
        limit.setManufacturer(manufacturers.Vekoma);
        heidePark.addChildAndSetParent(limit);

        OnSiteAttraction indyBlitz = OnSiteAttraction.create("Indy-Blitz", 1);
        indyBlitz.setCreditType(creditTypes.RollerCoaster);
        indyBlitz.setCategory(categories.RollerCoasters);
        indyBlitz.setManufacturer(manufacturers.Zierer);
        heidePark.addChildAndSetParent(indyBlitz);

        OnSiteAttraction bobbahn = OnSiteAttraction.create("Bobbahn", 1);
        bobbahn.setCreditType(creditTypes.RollerCoaster);
        bobbahn.setCategory(categories.RollerCoasters);
        bobbahn.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(bobbahn);

        OnSiteAttraction scream = OnSiteAttraction.create("Scream");
        scream.setCategory(categories.ThrillRides);
        scream.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(scream);

        OnSiteAttraction mountainRafting = OnSiteAttraction.create("Mountain Rafting");
        mountainRafting.setCategory(categories.WaterRides);
        mountainRafting.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(mountainRafting);

        OnSiteAttraction wildwasserbahn = OnSiteAttraction.create("Wildwasserbahn");
        wildwasserbahn.setCategory(categories.WaterRides);
        wildwasserbahn.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(wildwasserbahn);

        OnSiteAttraction ghostbusters5D = OnSiteAttraction.create("Ghostbusters 5D", 1);
        ghostbusters5D.setCategory(categories.DarkRides);
        ghostbusters5D.setManufacturer(manufacturers.Triotech);
        heidePark.addChildAndSetParent(ghostbusters5D);

        OnSiteAttraction monorail = OnSiteAttraction.create("Monorail");
        monorail.setCategory(categories.TransportRides);
        monorail.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(monorail);

        OnSiteAttraction screamie = OnSiteAttraction.create("Screamie");
        screamie.setCategory(categories.FamilyRides);
        screamie.setManufacturer(manufacturers.Zierer);
        heidePark.addChildAndSetParent(screamie);

        OnSiteAttraction bounty = OnSiteAttraction.create("Bounty");
        bounty.setCategory(categories.FamilyRides);
        bounty.setManufacturer(manufacturers.Intamin);
        heidePark.addChildAndSetParent(bounty);

        OnSiteAttraction drachengrotte = OnSiteAttraction.create("Drachengrotte");
        drachengrotte.setCategory(categories.WaterRides);
        drachengrotte.setManufacturer(manufacturers.Zierer);
        heidePark.addChildAndSetParent(drachengrotte);

        OnSiteAttraction laola = OnSiteAttraction.create("La Ola");
        laola.setCategory(categories.FamilyRides);
        laola.setManufacturer(manufacturers.Zierer);
        heidePark.addChildAndSetParent(laola);

        OnSiteAttraction panoramabahn = OnSiteAttraction.create("Panoramabahn");
        panoramabahn.setCategory(categories.TransportRides);
        panoramabahn.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(panoramabahn);

        OnSiteAttraction hickshimmelsstuermer = OnSiteAttraction.create("Hick's Himmelsstürmer");
        hickshimmelsstuermer.setCategory(categories.FamilyRides);
        hickshimmelsstuermer.setManufacturer(manufacturers.Zamperla);
        heidePark.addChildAndSetParent(hickshimmelsstuermer);

        OnSiteAttraction kaeptnsToern = OnSiteAttraction.create("Käpt'ns Törn");
        kaeptnsToern.setCategory(categories.WaterRides);
        kaeptnsToern.setManufacturer(manufacturers.Mack);
        heidePark.addChildAndSetParent(kaeptnsToern);

        OnSiteAttraction nostalgiekarusell = OnSiteAttraction.create("Nostalgiekarussell");
        nostalgiekarusell.setCategory(categories.FamilyRides);
        heidePark.addChildAndSetParent(nostalgiekarusell);



        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides12102018 = new LinkedHashMap<>();
        rides12102018.put(krake, 8);
        rides12102018.put(flugDerDaemonen, 8);
        rides12102018.put(desertRace, 8);
        rides12102018.put(limit, 1);
        rides12102018.put(bigLoop, 1);
        rides12102018.put(grottenblitz, 1);
        rides12102018.put(bobbahn, 1);
        rides12102018.put(scream, 1);
        heidePark.addChildAndSetParent(this.createVisit(12, 10, 2018, rides12102018));


        // 2019
        LinkedHashMap<OnSiteAttraction, Integer> rides07042019 = new LinkedHashMap<>();
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

        LinkedHashMap<OnSiteAttraction, Integer> rides18052019 = new LinkedHashMap<>();
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

        LinkedHashMap<OnSiteAttraction, Integer> rides07092019 = new LinkedHashMap<>();
        rides07092019.put(nostalgiekarusell, 1);
        rides07092019.put(flugDerDaemonen, 4);
        rides07092019.put(bigLoop, 1);
        rides07092019.put(desertRace, 3);
        rides07092019.put(colossos, 2);
        rides07092019.put(krake, 2);
        rides07092019.put(limit, 1);
        heidePark.addChildAndSetParent(this.createVisit(7, 9, 2019, rides07092019));
    }

    private void mockWalibiHolland()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockWalibiHolland");

        Park walibiHolland = Park.create("Walibi Holland");
        locations.Netherlands.addChildAndSetParent(walibiHolland);

        OnSiteAttraction goliath = OnSiteAttraction.create("Goliath", 7);
        goliath.setCreditType(creditTypes.RollerCoaster);
        goliath.setCategory(categories.RollerCoasters);
        goliath.setManufacturer(manufacturers.Intamin);
        walibiHolland.addChildAndSetParent(goliath);

        OnSiteAttraction untamed = OnSiteAttraction.create("Untamed");
        untamed.setCreditType(creditTypes.RollerCoaster);
        untamed.setCategory(categories.RollerCoasters);
        untamed.setManufacturer(manufacturers.RMC);
        walibiHolland.addChildAndSetParent(untamed);

        OnSiteAttraction lostGravity = OnSiteAttraction.create("Lost Gravity", 7);
        lostGravity.setCreditType(creditTypes.RollerCoaster);
        lostGravity.setCategory(categories.RollerCoasters);
        lostGravity.setManufacturer(manufacturers.Mack);
        walibiHolland.addChildAndSetParent(lostGravity);

        OnSiteAttraction xpressPlatform13 = OnSiteAttraction.create("Xpress: Platform 13", 2);
        xpressPlatform13.setCreditType(creditTypes.RollerCoaster);
        xpressPlatform13.setCategory(categories.RollerCoasters);
        xpressPlatform13.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(xpressPlatform13);

        OnSiteAttraction speedOfSound = OnSiteAttraction.create("Speed of Sound", 2);
        speedOfSound.setCreditType(creditTypes.RollerCoaster);
        speedOfSound.setCategory(categories.RollerCoasters);
        speedOfSound.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(speedOfSound);

        OnSiteAttraction elCondor = OnSiteAttraction.create("El Condor", 1);
        elCondor.setCreditType(creditTypes.RollerCoaster);
        elCondor.setCategory(categories.RollerCoasters);
        elCondor.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(elCondor);

        OnSiteAttraction drako = OnSiteAttraction.create("Drako", 2);
        drako.setCreditType(creditTypes.RollerCoaster);
        drako.setCategory(categories.RollerCoasters);
        drako.setManufacturer(manufacturers.Zierer);
        walibiHolland.addChildAndSetParent(drako);

        OnSiteAttraction robinHood = OnSiteAttraction.create("Robin Hood", 2);
        robinHood.setCreditType(creditTypes.RollerCoaster);
        robinHood.setCategory(categories.RollerCoasters);
        robinHood.setManufacturer(manufacturers.Vekoma);
        robinHood.setStatus(statuses.Converted);
        walibiHolland.addChildAndSetParent(robinHood);

        OnSiteAttraction excalibur = OnSiteAttraction.create("Excalibur", 1);
        excalibur.setCategory(categories.ThrillRides);
        excalibur.setManufacturer(manufacturers.Huss);
        walibiHolland.addChildAndSetParent(excalibur);

        OnSiteAttraction gForce = OnSiteAttraction.create("G-Force");
        gForce.setCategory(categories.ThrillRides);
        gForce.setManufacturer(manufacturers.Huss);
        walibiHolland.addChildAndSetParent(gForce);

        OnSiteAttraction spaceShot = OnSiteAttraction.create("Space Shot");
        spaceShot.setCategory(categories.ThrillRides);
        spaceShot.setManufacturer(manufacturers.SAndS);
        walibiHolland.addChildAndSetParent(spaceShot);

        OnSiteAttraction spinningVibe = OnSiteAttraction.create("Spinning Vibe");
        spinningVibe.setCategory(categories.ThrillRides);
        spinningVibe.setManufacturer(manufacturers.Huss);
        walibiHolland.addChildAndSetParent(spinningVibe);

        OnSiteAttraction skydiver = OnSiteAttraction.create("Skydiver");
        skydiver.setCategory(categories.ThrillRides);
        walibiHolland.addChildAndSetParent(skydiver);

        OnSiteAttraction tomahawk = OnSiteAttraction.create("Tomahawk");
        tomahawk.setCategory(categories.ThrillRides);
        tomahawk.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(tomahawk);

        OnSiteAttraction fibisBubbleSwirl = OnSiteAttraction.create("Fibi's Bubble Swirl");
        fibisBubbleSwirl.setCategory(categories.FamilyRides);
        fibisBubbleSwirl.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(fibisBubbleSwirl);

        OnSiteAttraction haazGarage = OnSiteAttraction.create("Haaz Garage");
        haazGarage.setCategory(categories.FamilyRides);
        haazGarage.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(haazGarage);

        OnSiteAttraction laGrandeRoue = OnSiteAttraction.create("La Grande Roue");
        laGrandeRoue.setCategory(categories.FamilyRides);
        laGrandeRoue.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(laGrandeRoue);

        OnSiteAttraction leTourDesJardins = OnSiteAttraction.create("Le Tour Des Jardins");
        leTourDesJardins.setCategory(categories.FamilyRides);
        walibiHolland.addChildAndSetParent(leTourDesJardins);

        OnSiteAttraction losSombreros = OnSiteAttraction.create("Los Sombreros");
        losSombreros.setCategory(categories.FamilyRides);
        walibiHolland.addChildAndSetParent(losSombreros);

        OnSiteAttraction merlinsMagicCastle = OnSiteAttraction.create("Merlin's Magic Castle", 1);
        merlinsMagicCastle.setCategory(categories.FamilyRides);
        merlinsMagicCastle.setManufacturer(manufacturers.Vekoma);
        merlinsMagicCastle.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(merlinsMagicCastle);

        OnSiteAttraction merrieGoround = OnSiteAttraction.create("Merrie Go'round");
        merrieGoround.setCategory(categories.FamilyRides);
        merrieGoround.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(merrieGoround);

        OnSiteAttraction pavillonDeThe = OnSiteAttraction.create("Pavillon de Thè");
        pavillonDeThe.setCategory(categories.FamilyRides);
        walibiHolland.addChildAndSetParent(pavillonDeThe);

        OnSiteAttraction spaceKidz = OnSiteAttraction.create("Space Kidz");
        spaceKidz.setCategory(categories.FamilyRides);
        spaceKidz.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(spaceKidz);

        OnSiteAttraction superSwing = OnSiteAttraction.create("Super Swing");
        superSwing.setCategory(categories.FamilyRides);
        superSwing.setManufacturer(manufacturers.Zierer);
        walibiHolland.addChildAndSetParent(superSwing);

        OnSiteAttraction squadsStuntFlight = OnSiteAttraction.create("Squad's Stunt Flight");
        squadsStuntFlight.setCategory(categories.FamilyRides);
        squadsStuntFlight.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(squadsStuntFlight);

        OnSiteAttraction tequillaTaxis = OnSiteAttraction.create("Tequilla Taxi's");
        tequillaTaxis.setCategory(categories.FamilyRides);
        tequillaTaxis.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(tequillaTaxis);

        OnSiteAttraction wabWorldTour = OnSiteAttraction.create("WAB World Tour");
        wabWorldTour.setCategory(categories.FamilyRides);
        wabWorldTour.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(wabWorldTour);

        OnSiteAttraction walibiExpress = OnSiteAttraction.create("Walibi Express");
        walibiExpress.setCategory(categories.TransportRides);
        walibiHolland.addChildAndSetParent(walibiExpress);

        OnSiteAttraction walibisFunRecorder = OnSiteAttraction.create("Walibi's Fun Recorder");
        walibisFunRecorder.setCategory(categories.FamilyRides);
        walibisFunRecorder.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(walibisFunRecorder);

        OnSiteAttraction zensGraffityShuttle = OnSiteAttraction.create("Zen's Graffity Shuttle");
        zensGraffityShuttle.setCategory(categories.FamilyRides);
        zensGraffityShuttle.setManufacturer(manufacturers.SbfVisa);
        walibiHolland.addChildAndSetParent(zensGraffityShuttle);

        OnSiteAttraction crazyRiver = OnSiteAttraction.create("Crazy River", 2);
        crazyRiver.setCategory(categories.WaterRides);
        crazyRiver.setManufacturer(manufacturers.Mack);
        walibiHolland.addChildAndSetParent(crazyRiver);

        OnSiteAttraction elRioGrande = OnSiteAttraction.create("El Rio Grande", 2);
        elRioGrande.setCategory(categories.WaterRides);
        elRioGrande.setManufacturer(manufacturers.Vekoma);
        walibiHolland.addChildAndSetParent(elRioGrande);

        OnSiteAttraction splashBattle = OnSiteAttraction.create("SplashBattle");
        splashBattle.setCategory(categories.WaterRides);
        splashBattle.setManufacturer(manufacturers.PrestonAndBarbieri);
        walibiHolland.addChildAndSetParent(splashBattle);


        // 2019
        LinkedHashMap<OnSiteAttraction, Integer> rides01072019 = new LinkedHashMap<>();
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
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockOsterwiese");

        //2018
        Park osterwiese2018 = Park.create("Osterwiese 2018");
        locations.Bremen.addChildAndSetParent(osterwiese2018);

        OnSiteAttraction bergUndTal = OnSiteAttraction.create("Berg & Tal");
        bergUndTal.setCreditType(creditTypes.RollerCoaster);
        bergUndTal.setCategory(categories.RollerCoasters);
        osterwiese2018.addChildAndSetParent(bergUndTal);

        LinkedHashMap<OnSiteAttraction, Integer> rides24032018 = new LinkedHashMap<>();
        rides24032018.put(bergUndTal, 1);
        osterwiese2018.addChildAndSetParent(this.createVisit(24, 3, 2018, rides24032018));

        //2019
        Park osterwiese2019 = Park.create("Osterwiese 2019");
        locations.Bremen.addChildAndSetParent(osterwiese2019);

        OnSiteAttraction crazyMouse = OnSiteAttraction.create("Crazy Mouse");
        crazyMouse.setCreditType(creditTypes.RollerCoaster);
        crazyMouse.setCategory(categories.RollerCoasters);
        osterwiese2019.addChildAndSetParent(crazyMouse);

        OnSiteAttraction tomDerTiger = OnSiteAttraction.create("Tom der Tiger");
        tomDerTiger.setCreditType(creditTypes.RollerCoaster);
        tomDerTiger.setCategory(categories.RollerCoasters);
        tomDerTiger.setManufacturer(manufacturers.Pinfari);
        osterwiese2019.addChildAndSetParent(tomDerTiger);

        LinkedHashMap<OnSiteAttraction, Integer> rides13042019 = new LinkedHashMap<>();
        rides13042019.put(crazyMouse, 1);
        rides13042019.put(tomDerTiger, 1);
        osterwiese2019.addChildAndSetParent(this.createVisit(13, 4, 2019, rides13042019));
    }

    private void mockFreimarkt()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockFreimarkt");

        //2018
        Park freimarkt2018 = Park.create("Freimarkt 2018");
        locations.Bremen.addChildAndSetParent(freimarkt2018);

        OnSiteAttraction alpinaBahn = OnSiteAttraction.create("Alpina Bahn", 2);
        alpinaBahn.setCreditType(creditTypes.RollerCoaster);
        alpinaBahn.setCategory(categories.RollerCoasters);
        alpinaBahn.setManufacturer(manufacturers.Schwarzkopf);
        freimarkt2018.addChildAndSetParent(alpinaBahn);

        OnSiteAttraction wildeMaus = OnSiteAttraction.create("Wilde Maus", 1);
        wildeMaus.setCreditType(creditTypes.RollerCoaster);
        wildeMaus.setCategory(categories.RollerCoasters);
        freimarkt2018.addChildAndSetParent(wildeMaus);

        OnSiteAttraction euroCoaster = OnSiteAttraction.create("Euro Coaster", 1);
        euroCoaster.setCreditType(creditTypes.RollerCoaster);
        euroCoaster.setCategory(categories.RollerCoasters);
        freimarkt2018.addChildAndSetParent(euroCoaster);

        LinkedHashMap<OnSiteAttraction, Integer> rides20102018 = new LinkedHashMap<>();
        rides20102018.put(alpinaBahn, 1);
        rides20102018.put(wildeMaus, 1);
        rides20102018.put(euroCoaster, 1);
        freimarkt2018.addChildAndSetParent(this.createVisit(20, 10, 2018, rides20102018));


        //2019
        Park freimarkt2019 = Park.create("Freimarkt 2019");
        locations.Bremen.addChildAndSetParent(freimarkt2019);

        OnSiteAttraction rockAndRollerCoaster = OnSiteAttraction.create("Rock & Roller Coaster");
        rockAndRollerCoaster.setCreditType(creditTypes.RollerCoaster);
        rockAndRollerCoaster.setCategory(categories.RollerCoasters);
        freimarkt2019.addChildAndSetParent(rockAndRollerCoaster);

        OnSiteAttraction wildeMausXXL = OnSiteAttraction.create("Wilde Maus XXL");
        wildeMausXXL.setCreditType(creditTypes.RollerCoaster);
        wildeMausXXL.setCategory(categories.RollerCoasters);
        freimarkt2019.addChildAndSetParent(wildeMausXXL);

        OnSiteAttraction kuddelDerHai = OnSiteAttraction.create("Kuddel der Hai");
        kuddelDerHai.setCreditType(creditTypes.RollerCoaster);
        kuddelDerHai.setCategory(categories.RollerCoasters);
        kuddelDerHai.setManufacturer(manufacturers.Pinfari);
        freimarkt2019.addChildAndSetParent(kuddelDerHai);

        LinkedHashMap<OnSiteAttraction, Integer> rides02112019 = new LinkedHashMap<>();
        rides02112019.put(rockAndRollerCoaster, 1);
        rides02112019.put(wildeMausXXL, 1);
        rides02112019.put(kuddelDerHai, 1);
        freimarkt2019.addChildAndSetParent(this.createVisit(2, 11, 2019, rides02112019));
    }

    private void mockPortAventura()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockPortAventura");

        Park portAventura = Park.create("Port Aventura");
        locations.Spain.addChildAndSetParent(portAventura);

        OnSiteAttraction shambhala = OnSiteAttraction.create("Shambhala");
        shambhala.setCreditType(creditTypes.RollerCoaster);
        shambhala.setCategory(categories.RollerCoasters);
        shambhala.setManufacturer(manufacturers.BolligerAndMabillard);
        portAventura.addChildAndSetParent(shambhala);

        OnSiteAttraction dragonKhan = OnSiteAttraction.create("Dragon Khan");
        dragonKhan.setCreditType(creditTypes.RollerCoaster);
        dragonKhan.setCategory(categories.RollerCoasters);
        dragonKhan.setManufacturer(manufacturers.BolligerAndMabillard);
        portAventura.addChildAndSetParent(dragonKhan);

        OnSiteAttraction furiusBaco = OnSiteAttraction.create("Furius Baco");
        furiusBaco.setCreditType(creditTypes.RollerCoaster);
        furiusBaco.setCategory(categories.RollerCoasters);
        furiusBaco.setManufacturer(manufacturers.Intamin);
        portAventura.addChildAndSetParent(furiusBaco);

        OnSiteAttraction stampidaRoja = OnSiteAttraction.create("Stampida (Roja)");
        stampidaRoja.setCreditType(creditTypes.RollerCoaster);
        stampidaRoja.setCategory(categories.RollerCoasters);
        stampidaRoja.setManufacturer(manufacturers.CCI);
        portAventura.addChildAndSetParent(stampidaRoja);

        OnSiteAttraction stampidaAzul = OnSiteAttraction.create("Stampida (Azul)");
        stampidaAzul.setCreditType(creditTypes.RollerCoaster);
        stampidaAzul.setCategory(categories.RollerCoasters);
        stampidaAzul.setManufacturer(manufacturers.CCI);
        portAventura.addChildAndSetParent(stampidaAzul);

        OnSiteAttraction tomahawk = OnSiteAttraction.create("Tomahawk");
        tomahawk.setCreditType(creditTypes.RollerCoaster);
        tomahawk.setCategory(categories.RollerCoasters);
        tomahawk.setManufacturer(manufacturers.CCI);
        portAventura.addChildAndSetParent(tomahawk);

        OnSiteAttraction elDiablo = OnSiteAttraction.create("El Diablo");
        elDiablo.setCreditType(creditTypes.RollerCoaster);
        elDiablo.setCategory(categories.RollerCoasters);
        elDiablo.setManufacturer(manufacturers.ArrowDynamics);
        portAventura.addChildAndSetParent(elDiablo);

        OnSiteAttraction tamitami = OnSiteAttraction.create("Tami-Tami");
        tamitami.setCreditType(creditTypes.RollerCoaster);
        tamitami.setCategory(categories.RollerCoasters);
        tamitami.setManufacturer(manufacturers.Vekoma);
        portAventura.addChildAndSetParent(tamitami);

        OnSiteAttraction hurakanCondor = OnSiteAttraction.create("Hurakan Condor");
        hurakanCondor.setCategory(categories.ThrillRides);
        hurakanCondor.setManufacturer(manufacturers.Intamin);
        portAventura.addChildAndSetParent(hurakanCondor);

        OnSiteAttraction serpienteEmplumada = OnSiteAttraction.create("Serpiente Emplumada");
        serpienteEmplumada.setCategory(categories.FamilyRides);
        serpienteEmplumada.setManufacturer(manufacturers.Schwarzkopf);
        portAventura.addChildAndSetParent(serpienteEmplumada);

        OnSiteAttraction tutukiSplash = OnSiteAttraction.create("Tutuki Splash");
        tutukiSplash.setCategory(categories.WaterRides);
        tutukiSplash.setManufacturer(manufacturers.Intamin);
        portAventura.addChildAndSetParent(tutukiSplash);

        OnSiteAttraction silverRiverFlumes = OnSiteAttraction.create("Silver River Flumes");
        silverRiverFlumes.setCategory(categories.WaterRides);
        silverRiverFlumes.setManufacturer(manufacturers.Mack);
        portAventura.addChildAndSetParent(silverRiverFlumes);

        OnSiteAttraction grandCanyonRapids = OnSiteAttraction.create("Grand Canyon Rapids");
        grandCanyonRapids.setCategory(categories.WaterRides);
        grandCanyonRapids.setManufacturer(manufacturers.Intamin);
        portAventura.addChildAndSetParent(grandCanyonRapids);


        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides02102018 = new LinkedHashMap<>();
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

        LinkedHashMap<OnSiteAttraction, Integer> rides04102018 = new LinkedHashMap<>();
        rides04102018.put(shambhala, 3);
        rides04102018.put(dragonKhan, 1);
        rides04102018.put(tomahawk, 1);
        rides04102018.put(elDiablo, 1);
        rides04102018.put(furiusBaco, 1);
        rides04102018.put(tutukiSplash, 1);
        rides04102018.put(silverRiverFlumes, 4);
        rides04102018.put(grandCanyonRapids, 1);
        portAventura.addChildAndSetParent(this.createVisit(4, 10, 2018, rides04102018));


        // 2019
        LinkedHashMap<OnSiteAttraction, Integer> rides02052019 = new LinkedHashMap<>();
        rides02052019.put(shambhala, 3);
        rides02052019.put(elDiablo, 1);
        rides02052019.put(dragonKhan, 1);
        rides02052019.put(stampidaAzul, 1);
        rides02052019.put(serpienteEmplumada, 1);
        rides02052019.put(silverRiverFlumes, 1);
        portAventura.addChildAndSetParent(this.createVisit(2, 5, 2019, rides02052019));

        LinkedHashMap<OnSiteAttraction, Integer> rides04052019 = new LinkedHashMap<>();
        rides04052019.put(shambhala, 2);
        rides04052019.put(furiusBaco, 1);
        rides04052019.put(grandCanyonRapids, 1);
        portAventura.addChildAndSetParent(this.createVisit(4, 5, 2019, rides04052019));
    }

    private void mockFerrariLand()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockFerrariLand");

        Park ferrariLand = Park.create("Ferrari Land");
        locations.Spain.addChildAndSetParent(ferrariLand);

        OnSiteAttraction redForce = OnSiteAttraction.create("Red Force");
        redForce.setCreditType(creditTypes.RollerCoaster);
        redForce.setCategory(categories.RollerCoasters);
        redForce.setManufacturer(manufacturers.Intamin);
        ferrariLand.addChildAndSetParent(redForce);

        OnSiteAttraction juniorRedForce = OnSiteAttraction.create("Junior Red Force");
        juniorRedForce.setCreditType(creditTypes.RollerCoaster);
        juniorRedForce.setCategory(categories.RollerCoasters);
        juniorRedForce.setManufacturer(manufacturers.SbfVisa);
        ferrariLand.addChildAndSetParent(juniorRedForce);

        OnSiteAttraction thrillTowers1 = OnSiteAttraction.create("Thrill Tower I");
        thrillTowers1.setCategory(categories.ThrillRides);
        thrillTowers1.setManufacturer(manufacturers.SAndS);
        ferrariLand.addChildAndSetParent(thrillTowers1);

        OnSiteAttraction thrillTowers2 = OnSiteAttraction.create("Thrill Towers II");
        thrillTowers2.setCategory(categories.ThrillRides);
        thrillTowers2.setManufacturer(manufacturers.SAndS);
        ferrariLand.addChildAndSetParent(thrillTowers2);

        OnSiteAttraction racingLegends = OnSiteAttraction.create("Racing Legends");
        racingLegends.setCategory(categories.DarkRides);
        racingLegends.setManufacturer(manufacturers.Simworx);
        ferrariLand.addChildAndSetParent(racingLegends);


        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides02102018 = new LinkedHashMap<>();
        rides02102018.put(redForce, 6);
        rides02102018.put(juniorRedForce, 1);
        rides02102018.put(thrillTowers1, 1);
        rides02102018.put(thrillTowers2, 1);
        rides02102018.put(racingLegends, 1);
        ferrariLand.addChildAndSetParent(this.createVisit(2, 10, 2018, rides02102018));


        // 2019
        LinkedHashMap<OnSiteAttraction, Integer> rides02052018 = new LinkedHashMap<>();
        rides02052018.put(redForce, 1);
        ferrariLand.addChildAndSetParent(this.createVisit(2, 5, 2019, rides02052018));
    }

    private void mockSlagharen()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockSlagharen");

        Park slagharen = Park.create("Attractiepark Slagharen");
        locations.Netherlands.addChildAndSetParent(slagharen);

        OnSiteAttraction goldrush = OnSiteAttraction.create("Gold Rush");
        goldrush.setCreditType(creditTypes.RollerCoaster);
        goldrush.setCategory(categories.RollerCoasters);
        goldrush.setManufacturer(manufacturers.Gerstlauer);
        slagharen.addChildAndSetParent(goldrush);

        OnSiteAttraction minetrain = OnSiteAttraction.create("Mine Train");
        minetrain.setCreditType(creditTypes.RollerCoaster);
        minetrain.setCategory(categories.RollerCoasters);
        minetrain.setManufacturer(manufacturers.Vekoma);
        slagharen.addChildAndSetParent(minetrain);

        OnSiteAttraction ripsawFalls = OnSiteAttraction.create("Ripsaw Falls");
        ripsawFalls.setCategory(categories.WaterRides);
        slagharen.addChildAndSetParent(ripsawFalls);

        OnSiteAttraction enterprise = OnSiteAttraction.create("Enterprise");
        enterprise.setCategory(categories.ThrillRides);
        enterprise.setManufacturer(manufacturers.Schwarzkopf);
        slagharen.addChildAndSetParent(enterprise);

        OnSiteAttraction apollo = OnSiteAttraction.create("Apollo");
        apollo.setCategory(categories.FamilyRides);
        apollo.setManufacturer(manufacturers.Schwarzkopf);
        slagharen.addChildAndSetParent(apollo);

        OnSiteAttraction freeFall = OnSiteAttraction.create("Free Fall");
        freeFall.setCategory(categories.ThrillRides);
        slagharen.addChildAndSetParent(freeFall);

        OnSiteAttraction pirate = OnSiteAttraction.create("Pirate");
        pirate.setCategory(categories.FamilyRides);
        pirate.setManufacturer(manufacturers.Huss);
        slagharen.addChildAndSetParent(pirate);

        OnSiteAttraction galoppers = OnSiteAttraction.create("Galoppers");
        galoppers.setCategory(categories.FamilyRides);
        galoppers.setManufacturer(manufacturers.Zierer);
        slagharen.addChildAndSetParent(galoppers);

        OnSiteAttraction eagle = OnSiteAttraction.create("Eagle");
        eagle.setCategory(categories.FamilyRides);
        eagle.setManufacturer(manufacturers.Huss);
        slagharen.addChildAndSetParent(eagle);

        OnSiteAttraction tomahawk = OnSiteAttraction.create("Tomahawk");
        tomahawk.setCategory(categories.FamilyRides);
        tomahawk.setManufacturer(manufacturers.Huss);
        slagharen.addChildAndSetParent(tomahawk);

        OnSiteAttraction wildWestAdventure = OnSiteAttraction.create("Wild West Adventure");
        wildWestAdventure.setCategory(categories.DarkRides);
        wildWestAdventure.setManufacturer(manufacturers.Mack);
        slagharen.addChildAndSetParent(wildWestAdventure);

        OnSiteAttraction bigWheel = OnSiteAttraction.create("Big Wheel");
        bigWheel.setCategory(categories.FamilyRides);
        bigWheel.setManufacturer(manufacturers.Schwarzkopf);
        slagharen.addChildAndSetParent(bigWheel);

        OnSiteAttraction kabelbaan = OnSiteAttraction.create("Kabelbaan");
        kabelbaan.setCategory(categories.TransportRides);
        slagharen.addChildAndSetParent(kabelbaan);

        OnSiteAttraction monorail = OnSiteAttraction.create("Monorail");
        monorail.setCategory(categories.TransportRides);
        monorail.setManufacturer(manufacturers.Schwarzkopf);
        slagharen.addChildAndSetParent(monorail);


        // 2019
        LinkedHashMap<OnSiteAttraction, Integer> rides30062019 = new LinkedHashMap<>();
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
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockHolidayPark");

        Park holidayPark = Park.create("Holiday Park");
        locations.Germany.addChildAndSetParent(holidayPark);

        OnSiteAttraction skyScream = OnSiteAttraction.create("Sky Scream");
        skyScream.setCreditType(creditTypes.RollerCoaster);
        skyScream.setCategory(categories.RollerCoasters);
        skyScream.setManufacturer(manufacturers.PremierRides);
        holidayPark.addChildAndSetParent(skyScream);

        OnSiteAttraction expeditionGeForce = OnSiteAttraction.create("Expedition GeForce");
        expeditionGeForce.setCreditType(creditTypes.RollerCoaster);
        expeditionGeForce.setCategory(categories.RollerCoasters);
        expeditionGeForce.setManufacturer(manufacturers.Intamin);
        holidayPark.addChildAndSetParent(expeditionGeForce);

        OnSiteAttraction burgFalkenstein = OnSiteAttraction.create("Burg Falkenstein");
        burgFalkenstein.setCategory(categories.DarkRides);
        burgFalkenstein.setManufacturer(manufacturers.Mack);
        holidayPark.addChildAndSetParent(burgFalkenstein);

        OnSiteAttraction anubis = OnSiteAttraction.create("Anubis Free Fall Tower");
        anubis.setCategory(categories.ThrillRides);
        anubis.setManufacturer(manufacturers.Intamin);
        holidayPark.addChildAndSetParent(anubis);


        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides13062018 = new LinkedHashMap<>();
        rides13062018.put(expeditionGeForce, 2);
        rides13062018.put(skyScream, 2);
        rides13062018.put(burgFalkenstein, 1);
        rides13062018.put(anubis, 2);
        holidayPark.addChildAndSetParent(this.createVisit(13, 6, 2018, rides13062018));
    }

    private void mockMovieParkGermany()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockMovieParkGermany");

        Park movieParkGermany = Park.create("Movie Park Germany");
        locations.Germany.addChildAndSetParent(movieParkGermany);

        OnSiteAttraction starTrekOperationEnterprise = OnSiteAttraction.create("Star Trek: Operation Enterprise");
        starTrekOperationEnterprise.setCreditType(creditTypes.RollerCoaster);
        starTrekOperationEnterprise.setCategory(categories.RollerCoasters);
        starTrekOperationEnterprise.setManufacturer(manufacturers.Mack);
        movieParkGermany.addChildAndSetParent(starTrekOperationEnterprise);

        OnSiteAttraction vanHelsingsFactory = OnSiteAttraction.create("Van Helsing's Factory");
        vanHelsingsFactory.setCreditType(creditTypes.RollerCoaster);
        vanHelsingsFactory.setCategory(categories.RollerCoasters);
        vanHelsingsFactory.setManufacturer(manufacturers.Gerstlauer);
        movieParkGermany.addChildAndSetParent(vanHelsingsFactory);

        OnSiteAttraction ghostChasers = OnSiteAttraction.create("Ghost Chasers");
        ghostChasers.setCreditType(creditTypes.RollerCoaster);
        ghostChasers.setCategory(categories.RollerCoasters);
        ghostChasers.setManufacturer(manufacturers.Mack);
        movieParkGermany.addChildAndSetParent(ghostChasers);

        OnSiteAttraction jimmyNeutronsAtomicFlyer = OnSiteAttraction.create("Jimmy Neutron's Atomic Flyer");
        jimmyNeutronsAtomicFlyer.setCreditType(creditTypes.RollerCoaster);
        jimmyNeutronsAtomicFlyer.setCategory(categories.RollerCoasters);
        jimmyNeutronsAtomicFlyer.setManufacturer(manufacturers.Vekoma);
        movieParkGermany.addChildAndSetParent(jimmyNeutronsAtomicFlyer);

        OnSiteAttraction mpXpress = OnSiteAttraction.create("MP Xpress");
        mpXpress.setCreditType(creditTypes.RollerCoaster);
        mpXpress.setCategory(categories.RollerCoasters);
        mpXpress.setManufacturer(manufacturers.Vekoma);
        movieParkGermany.addChildAndSetParent(mpXpress);

        OnSiteAttraction backyardigans = OnSiteAttraction.create("The Backyardigans: Mission to Mars");
        backyardigans.setCreditType(creditTypes.RollerCoaster);
        backyardigans.setCategory(categories.RollerCoasters);
        backyardigans.setManufacturer(manufacturers.Vekoma);
        movieParkGermany.addChildAndSetParent(backyardigans);

        OnSiteAttraction bandit = OnSiteAttraction.create("The Bandit");
        bandit.setCreditType(creditTypes.RollerCoaster);
        bandit.setCategory(categories.RollerCoasters);
        bandit.setManufacturer(manufacturers.PremierRides);
        movieParkGermany.addChildAndSetParent(bandit);

        OnSiteAttraction dorasBigRiverAdventuer = OnSiteAttraction.create("Dora's Big River Adventure");
        dorasBigRiverAdventuer.setCategory(categories.WaterRides);
        dorasBigRiverAdventuer.setManufacturer(manufacturers.Zamperla);
        movieParkGermany.addChildAndSetParent(dorasBigRiverAdventuer);

        OnSiteAttraction area51TopSecret = OnSiteAttraction.create("Area 51 - Top Secret");
        area51TopSecret.setCategory(categories.DarkRides);
        area51TopSecret.setManufacturer(manufacturers.Intamin);
        area51TopSecret.setStatus(statuses.Converted);
        movieParkGermany.addChildAndSetParent(area51TopSecret);

        OnSiteAttraction highFall = OnSiteAttraction.create("The High Fall");
        highFall.setCategory(categories.ThrillRides);
        highFall.setManufacturer(manufacturers.Intamin);
        movieParkGermany.addChildAndSetParent(highFall);

        OnSiteAttraction crazySurfer = OnSiteAttraction.create("Crazy Surfer");
        crazySurfer.setCategory(categories.FamilyRides);
        crazySurfer.setManufacturer(manufacturers.Zamperla);
        movieParkGermany.addChildAndSetParent(crazySurfer);

        OnSiteAttraction santaMonicaWheel = OnSiteAttraction.create("Santa Monica Wheel");
        santaMonicaWheel.setCategory(categories.FamilyRides);
        santaMonicaWheel.setManufacturer(manufacturers.SbfVisa);
        movieParkGermany.addChildAndSetParent(santaMonicaWheel);

        OnSiteAttraction excalibur = OnSiteAttraction.create("Excalibur - Secrets of the Dark Forest");
        excalibur.setCategory(categories.WaterRides);
        excalibur.setManufacturer(manufacturers.Intamin);
        movieParkGermany.addChildAndSetParent(excalibur);

        OnSiteAttraction timeRiders = OnSiteAttraction.create("Time Riders");
        timeRiders.setCategory(categories.FamilyRides);
        movieParkGermany.addChildAndSetParent(timeRiders);

        OnSiteAttraction NycTransformer = OnSiteAttraction.create("NYC Transformer");
        NycTransformer.setCategory(categories.ThrillRides);
        NycTransformer.setManufacturer(manufacturers.Huss);
        movieParkGermany.addChildAndSetParent(NycTransformer);

        OnSiteAttraction pierPatrolJetSki = OnSiteAttraction.create("Pier Patrol Jet Ski");
        pierPatrolJetSki.setCategory(categories.FamilyRides);
        pierPatrolJetSki.setManufacturer(manufacturers.Zierer);
        movieParkGermany.addChildAndSetParent(pierPatrolJetSki);

        OnSiteAttraction fairyWorldSpin = OnSiteAttraction.create("Fairy World Spin");
        fairyWorldSpin.setCategory(categories.FamilyRides);
        fairyWorldSpin.setManufacturer(manufacturers.Mack);
        movieParkGermany.addChildAndSetParent(fairyWorldSpin);

        OnSiteAttraction splatOSphere = OnSiteAttraction.create("Splat-O-Sphere");
        splatOSphere.setCategory(categories.FamilyRides);
        splatOSphere.setManufacturer(manufacturers.ChanceRides);
        movieParkGermany.addChildAndSetParent(splatOSphere);

        OnSiteAttraction lostTemple = OnSiteAttraction.create("The Lost Temple");
        lostTemple.setCategory(categories.DarkRides);
        lostTemple.setManufacturer(manufacturers.Simworx);
        movieParkGermany.addChildAndSetParent(lostTemple);


        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides11062018 = new LinkedHashMap<>();
        rides11062018.put(area51TopSecret, 1);
        rides11062018.put(vanHelsingsFactory, 2);
        rides11062018.put(backyardigans, 1);
        rides11062018.put(dorasBigRiverAdventuer, 1);
        rides11062018.put(ghostChasers, 2);
        rides11062018.put(jimmyNeutronsAtomicFlyer, 1);
        rides11062018.put(bandit, 1);
        rides11062018.put(mpXpress, 1);
        rides11062018.put(highFall, 3);
        rides11062018.put(crazySurfer, 1);
        rides11062018.put(santaMonicaWheel, 1);
        rides11062018.put(excalibur, 1);
        rides11062018.put(starTrekOperationEnterprise, 4);
        rides11062018.put(timeRiders, 1);
        rides11062018.put(NycTransformer, 1);
        movieParkGermany.addChildAndSetParent(this.createVisit(11, 6, 2018, rides11062018));

        // 2019
        LinkedHashMap<OnSiteAttraction, Integer> rides29091019 = new LinkedHashMap<>();
        rides29091019.put(vanHelsingsFactory, 2);
        rides29091019.put(starTrekOperationEnterprise, 1);
        rides29091019.put(mpXpress, 1);
        rides29091019.put(bandit, 1);
        rides29091019.put(ghostChasers, 1);
        rides29091019.put(jimmyNeutronsAtomicFlyer, 1);
        rides29091019.put(highFall, 2);
        rides29091019.put(pierPatrolJetSki, 2);
        rides29091019.put(crazySurfer, 1);
        rides29091019.put(fairyWorldSpin, 1);
        rides29091019.put(splatOSphere, 1);
        rides29091019.put(lostTemple, 1);
        rides29091019.put(excalibur, 1);
        rides29091019.put(area51TopSecret, 1);
        movieParkGermany.addChildAndSetParent(this.createVisit(29, 9, 2019, rides29091019));
    }

    private void mockToverland()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockToverland");

        Park toverland = Park.create("Toverland");
        this.locations.Netherlands.addChildAndSetParent(toverland);

        OnSiteAttraction troy = OnSiteAttraction.create("Troy");
        troy.setCreditType(creditTypes.RollerCoaster);
        troy.setCategory(categories.RollerCoasters);
        troy.setManufacturer(manufacturers.GCI);
        toverland.addChildAndSetParent(troy);

        OnSiteAttraction fenix = OnSiteAttraction.create("Fēnix");
        fenix.setCreditType(creditTypes.RollerCoaster);
        fenix.setCategory(categories.RollerCoasters);
        fenix.setManufacturer(manufacturers.BolligerAndMabillard);
        toverland.addChildAndSetParent(fenix);

        OnSiteAttraction dwervelwind = OnSiteAttraction.create("Dwervelwind");
        dwervelwind.setCreditType(creditTypes.RollerCoaster);
        dwervelwind.setCategory(categories.RollerCoasters);
        dwervelwind.setManufacturer(manufacturers.Mack);
        toverland.addChildAndSetParent(dwervelwind);

        OnSiteAttraction boosterBike = OnSiteAttraction.create("Booster Bike");
        boosterBike.setCreditType(creditTypes.RollerCoaster);
        boosterBike.setCategory(categories.RollerCoasters);
        boosterBike.setManufacturer(manufacturers.Vekoma);
        toverland.addChildAndSetParent(boosterBike);

        OnSiteAttraction toosExpress = OnSiteAttraction.create("Toos-Express");
        toosExpress.setCreditType(creditTypes.RollerCoaster);
        toosExpress.setCategory(categories.RollerCoasters);
        toosExpress.setManufacturer(manufacturers.Vekoma);
        toverland.addChildAndSetParent(toosExpress);

        OnSiteAttraction expeditionZork = OnSiteAttraction.create("Expedition Zork");
        expeditionZork.setCategory(categories.WaterRides);
        expeditionZork.setManufacturer(manufacturers.Mack);
        toverland.addChildAndSetParent(expeditionZork);

        OnSiteAttraction maximusBlitzbahn = OnSiteAttraction.create("Maximus' Blitz Bahn");
        maximusBlitzbahn.setCategory(categories.FamilyRides);
        toverland.addChildAndSetParent(maximusBlitzbahn);

        OnSiteAttraction scorpios = OnSiteAttraction.create("Scorpios");
        scorpios.setCategory(categories.FamilyRides);
        toverland.addChildAndSetParent(scorpios);

        OnSiteAttraction djenguRiver = OnSiteAttraction.create("Djengu River");
        djenguRiver.setCategory(categories.WaterRides);
        djenguRiver.setManufacturer(manufacturers.Hafema);
        toverland.addChildAndSetParent(djenguRiver);

        OnSiteAttraction merlinsQuest = OnSiteAttraction.create("Merlin's Quest");
        merlinsQuest.setCategory(categories.DarkRides);
        merlinsQuest.setManufacturer(manufacturers.Mack);
        toverland.addChildAndSetParent(merlinsQuest);

        OnSiteAttraction villaFiasco = OnSiteAttraction.create("Villa Fiasco");
        villaFiasco.setCategory(categories.FamilyRides);
        toverland.addChildAndSetParent(villaFiasco);

        OnSiteAttraction toverhuis = OnSiteAttraction.create("Toverhuis");
        toverhuis.setCategory(categories.FamilyRides);
        toverland.addChildAndSetParent(toverhuis);


        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides24042018 = new LinkedHashMap<>();
        rides24042018.put(troy, 4);
        rides24042018.put(dwervelwind, 16);
        rides24042018.put(expeditionZork, 2);
        rides24042018.put(boosterBike, 2);
        rides24042018.put(toosExpress, 1);
        rides24042018.put(maximusBlitzbahn, 1);
        rides24042018.put(scorpios, 1);
        rides24042018.put(djenguRiver, 1);
        rides24042018.put(villaFiasco, 1);
        toverland.addChildAndSetParent(this.createVisit(24, 4, 2018, rides24042018));

        LinkedHashMap<OnSiteAttraction, Integer> rides07072018 = new LinkedHashMap<>();
        rides07072018.put(troy, 6);
        rides07072018.put(fenix, 1);
        rides07072018.put(dwervelwind, 4);
        rides07072018.put(boosterBike, 1);
        rides07072018.put(toosExpress, 1);
        rides07072018.put(expeditionZork, 1);
        rides07072018.put(scorpios, 1);
        rides07072018.put(djenguRiver, 1);
        rides07072018.put(merlinsQuest, 1);
        toverland.addChildAndSetParent(this.createVisit(7, 7, 2018, rides07072018));
    }

    private void mockEfteling()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockEfteling");

        Park efteling = Park.create("Efteling");
        locations.Netherlands.addChildAndSetParent(efteling);


        OnSiteAttraction baron1898 = OnSiteAttraction.create("Baron 1898", 5);
        baron1898.setCreditType(creditTypes.RollerCoaster);
        baron1898.setCategory(categories.RollerCoasters);
        baron1898.setManufacturer(manufacturers.BolligerAndMabillard);
        efteling.addChildAndSetParent(baron1898);

        OnSiteAttraction deVliegendeHollander = OnSiteAttraction.create("De Vliegende Hollander", 2);
        deVliegendeHollander.setCreditType(creditTypes.RollerCoaster);
        deVliegendeHollander.setCategory(categories.RollerCoasters);
        efteling.addChildAndSetParent(deVliegendeHollander);

        OnSiteAttraction jorisEnDeDraakWater = OnSiteAttraction.create("Joris en de Draak (Water)", 2);
        jorisEnDeDraakWater.setCreditType(creditTypes.RollerCoaster);
        jorisEnDeDraakWater.setCategory(categories.RollerCoasters);
        jorisEnDeDraakWater.setManufacturer(manufacturers.GCI);
        efteling.addChildAndSetParent(jorisEnDeDraakWater);

        OnSiteAttraction jorisEnDeDraakVuur = OnSiteAttraction.create("Joris en de Draak (Vuur)", 1);
        jorisEnDeDraakVuur.setCreditType(creditTypes.RollerCoaster);
        jorisEnDeDraakVuur.setCategory(categories.RollerCoasters);
        jorisEnDeDraakVuur.setManufacturer(manufacturers.GCI);
        efteling.addChildAndSetParent(jorisEnDeDraakVuur);

        OnSiteAttraction vogelRok = OnSiteAttraction.create("Vogel Rok", 1);
        vogelRok.setCreditType(creditTypes.RollerCoaster);
        vogelRok.setCategory(categories.RollerCoasters);
        vogelRok.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(vogelRok);

        OnSiteAttraction python = OnSiteAttraction.create("Python", 1);
        python.setCreditType(creditTypes.RollerCoaster);
        python.setCategory(categories.RollerCoasters);
        python.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(python);

        OnSiteAttraction bobbaan = OnSiteAttraction.create("Bobbaan", 2);
        bobbaan.setCreditType(creditTypes.RollerCoaster);
        bobbaan.setCategory(categories.RollerCoasters);
        bobbaan.setManufacturer(manufacturers.Intamin);
        bobbaan.setStatus(statuses.Defunct);
        efteling.addChildAndSetParent(bobbaan);

        OnSiteAttraction fataMorgana = OnSiteAttraction.create("Fata Morgana", 2);
        fataMorgana.setCategory(categories.DarkRides);
        fataMorgana.setManufacturer(manufacturers.Intamin);
        efteling.addChildAndSetParent(fataMorgana);

        OnSiteAttraction carnevalFestival = OnSiteAttraction.create("CarnevalFestival", 2);
        carnevalFestival.setCategory(categories.DarkRides);
        carnevalFestival.setManufacturer(manufacturers.Mack);
        efteling.addChildAndSetParent(carnevalFestival);

        OnSiteAttraction droomvlucht = OnSiteAttraction.create("Droomvlucht", 2);
        droomvlucht.setCategory(categories.DarkRides);
        droomvlucht.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(droomvlucht);

        OnSiteAttraction symbolica = OnSiteAttraction.create("Symbolica", 4);
        symbolica.setCategory(categories.DarkRides);
        symbolica.setManufacturer(manufacturers.EtfRideSystems);
        efteling.addChildAndSetParent(symbolica);

        OnSiteAttraction pirana = OnSiteAttraction.create("Piraña", 2);
        pirana.setCategory(categories.WaterRides);
        pirana.setManufacturer(manufacturers.Intamin);
        efteling.addChildAndSetParent(pirana);

        OnSiteAttraction stoomtrein = OnSiteAttraction.create("Efteling Stoomtrein", 1);
        stoomtrein.setCategory(categories.TransportRides);
        efteling.addChildAndSetParent(stoomtrein);

        OnSiteAttraction halveMaen = OnSiteAttraction.create("Halve Maen", 2);
        halveMaen.setCategory(categories.FamilyRides);
        halveMaen.setManufacturer(manufacturers.Intamin);
        efteling.addChildAndSetParent(halveMaen);

        OnSiteAttraction polkaMarina = OnSiteAttraction.create("Polka Marina", 1);
        polkaMarina.setCategory(categories.FamilyRides);
        polkaMarina.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(polkaMarina);

        OnSiteAttraction spookslot = OnSiteAttraction.create("Spookslot", 1);
        spookslot.setCategory(categories.FamilyRides);
        efteling.addChildAndSetParent(spookslot);

        OnSiteAttraction villaVolta = OnSiteAttraction.create("Villa Volta", 2);
        villaVolta.setCategory(categories.FamilyRides);
        villaVolta.setManufacturer(manufacturers.Vekoma);
        efteling.addChildAndSetParent(villaVolta);
    }

    private void mockHansaPark()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockHansaPark");

        Park hansaPark = Park.create("Hansa Park");
        locations.Germany.addChildAndSetParent(hansaPark);

        OnSiteAttraction fluchVonNovgorod = OnSiteAttraction.create("Fluch von Novgorod", 4);
        fluchVonNovgorod.setCreditType(creditTypes.RollerCoaster);
        fluchVonNovgorod.setCategory(categories.RollerCoasters);
        fluchVonNovgorod.setManufacturer(manufacturers.Gerstlauer);
        hansaPark.addChildAndSetParent(fluchVonNovgorod);

        OnSiteAttraction schwurDesKaernan = OnSiteAttraction.create("Der Schwur des Kärnan", 6);
        schwurDesKaernan.setCreditType(creditTypes.RollerCoaster);
        schwurDesKaernan.setCategory(categories.RollerCoasters);
        schwurDesKaernan.setManufacturer(manufacturers.Gerstlauer);
        hansaPark.addChildAndSetParent(schwurDesKaernan);

        OnSiteAttraction nessie = OnSiteAttraction.create("Nessie", 3);
        nessie.setCreditType(creditTypes.RollerCoaster);
        nessie.setCategory(categories.RollerCoasters);
        nessie.setManufacturer(manufacturers.Schwarzkopf);
        hansaPark.addChildAndSetParent(nessie);

        OnSiteAttraction crazyMine = OnSiteAttraction.create("Crazy-Mine", 3);
        crazyMine.setCreditType(creditTypes.RollerCoaster);
        crazyMine.setCategory(categories.RollerCoasters);
        crazyMine.setManufacturer(manufacturers.MaurerRides);
        hansaPark.addChildAndSetParent(crazyMine);

        OnSiteAttraction rasenderRoland = OnSiteAttraction.create("Rasender Roland", 2);
        rasenderRoland.setCreditType(creditTypes.RollerCoaster);
        rasenderRoland.setCategory(categories.RollerCoasters);
        rasenderRoland.setManufacturer(manufacturers.Vekoma);
        hansaPark.addChildAndSetParent(rasenderRoland);

        OnSiteAttraction schlangeVonMidgard = OnSiteAttraction.create("Schlange von Midgard", 2);
        schlangeVonMidgard.setCreditType(creditTypes.RollerCoaster);
        schlangeVonMidgard.setCategory(categories.RollerCoasters);
        schlangeVonMidgard.setManufacturer(manufacturers.Gerstlauer);
        hansaPark.addChildAndSetParent(schlangeVonMidgard);

        OnSiteAttraction derKleineZar = OnSiteAttraction.create("Der kleine Zar", 1);
        derKleineZar.setCreditType(creditTypes.RollerCoaster);
        derKleineZar.setCategory(categories.RollerCoasters);
        derKleineZar.setManufacturer(manufacturers.PrestonAndBarbieri);
        hansaPark.addChildAndSetParent(derKleineZar);

        OnSiteAttraction wasserwolfAmIlmensee = OnSiteAttraction.create("Der Wasserwolf am Ilmensee", 2);
        wasserwolfAmIlmensee.setCategory(categories.WaterRides);
        wasserwolfAmIlmensee.setManufacturer(manufacturers.Mack);
        hansaPark.addChildAndSetParent(wasserwolfAmIlmensee);

        OnSiteAttraction superSplash = OnSiteAttraction.create("Super Splash", 2);
        superSplash.setCategory(categories.WaterRides);
        superSplash.setManufacturer(manufacturers.Intamin);
        hansaPark.addChildAndSetParent(superSplash);

        OnSiteAttraction stoertebekersKaperfahrt = OnSiteAttraction.create("Störtebeker's Kaperfahrt", 1);
        stoertebekersKaperfahrt.setCategory(categories.WaterRides);
        hansaPark.addChildAndSetParent(stoertebekersKaperfahrt);

        OnSiteAttraction sturmfahrtDerDrachenboote = OnSiteAttraction.create("Sturmfahrt der Drachenboote", 1);
        sturmfahrtDerDrachenboote.setCategory(categories.WaterRides);
        hansaPark.addChildAndSetParent(sturmfahrtDerDrachenboote);

        OnSiteAttraction fliegenderHollaender = OnSiteAttraction.create("Fliegender Holländer", 1);
        fliegenderHollaender.setCategory(categories.FamilyRides);
        fliegenderHollaender.setManufacturer(manufacturers.Huss);
        hansaPark.addChildAndSetParent(fliegenderHollaender);

        OnSiteAttraction holsteinturm = OnSiteAttraction.create("Holsteinturm", 1);
        holsteinturm.setCategory(categories.FamilyRides);
        holsteinturm.setManufacturer(manufacturers.Huss);
        hansaPark.addChildAndSetParent(holsteinturm);

        OnSiteAttraction kaernapulten = OnSiteAttraction.create("Kärnapulten", 1);
        kaernapulten.setCategory(categories.FamilyRides);
        kaernapulten.setManufacturer(manufacturers.Gerstlauer);
        hansaPark.addChildAndSetParent(kaernapulten);

        OnSiteAttraction hanseflieger = OnSiteAttraction.create("Hanse Flieger", 1);
        hanseflieger.setCategory(categories.FamilyRides);
        hansaPark.addChildAndSetParent(hanseflieger);

        OnSiteAttraction fliegenderHai = OnSiteAttraction.create("Fliegender Hai", 2);
        fliegenderHai.setCategory(categories.ThrillRides);
        fliegenderHai.setManufacturer(manufacturers.Huss);
        fliegenderHai.setStatus(statuses.Defunct);
        hansaPark.addChildAndSetParent(fliegenderHai);

        OnSiteAttraction hansaParkExpress = OnSiteAttraction.create("Hansa Park Express");
        hansaParkExpress.setCategory(categories.TransportRides);
        hansaPark.addChildAndSetParent(hansaParkExpress);

        OnSiteAttraction highlander = OnSiteAttraction.create("Highlander");
        highlander.setCategory(categories.ThrillRides);
        hansaPark.addChildAndSetParent(highlander);

        OnSiteAttraction barracudaSlide = OnSiteAttraction.create("Barracuda Slide");
        barracudaSlide.setCategory(categories.FamilyRides);
        hansaPark.addChildAndSetParent(barracudaSlide);

        OnSiteAttraction blumenmeerbootsfahrt = OnSiteAttraction.create("Blumenmeerbootsfahrt");
        blumenmeerbootsfahrt.setCategory(categories.WaterRides);
        hansaPark.addChildAndSetParent(blumenmeerbootsfahrt);

        LinkedHashMap<OnSiteAttraction, Integer> rides08092019 = new LinkedHashMap<>();
        rides08092019.put(hansaParkExpress, 1);
        rides08092019.put(hanseflieger, 1);
        rides08092019.put(highlander, 2);
        rides08092019.put(nessie, 1);
        rides08092019.put(rasenderRoland, 1);
        rides08092019.put(barracudaSlide, 1);
        rides08092019.put(schwurDesKaernan, 1);
        rides08092019.put(stoertebekersKaperfahrt, 1);
        rides08092019.put(fluchVonNovgorod, 1);
        rides08092019.put(blumenmeerbootsfahrt, 1);
        rides08092019.put(fliegenderHollaender, 1);
        rides08092019.put(wasserwolfAmIlmensee, 1);
        rides08092019.put(superSplash, 1);
        rides08092019.put(kaernapulten, 1);
        hansaPark.addChildAndSetParent(this.createVisit(8, 9, 2019, rides08092019));
    }

    private void mockMagicParkVerden()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockMagicParkVerden");

        Park magicParkVerden = Park.create("Magic Park Verden");
        locations.Germany.addChildAndSetParent(magicParkVerden);

        OnSiteAttraction achterbahn = OnSiteAttraction.create("Magic Park Achterbahn");
        achterbahn.setCreditType(creditTypes.RollerCoaster);
        achterbahn.setCategory(categories.RollerCoasters);
        magicParkVerden.addChildAndSetParent(achterbahn);

        OnSiteAttraction wildwasserbahn = OnSiteAttraction.create("Wildwasserbahn");
        wildwasserbahn.setCategory(categories.WaterRides);
        magicParkVerden.addChildAndSetParent(wildwasserbahn);


        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides09062018 = new LinkedHashMap<>();
        rides09062018.put(achterbahn, 4);
        rides09062018.put(wildwasserbahn, 1);
        magicParkVerden.addChildAndSetParent(this.createVisit(9, 6, 2018, rides09062018));
    }

    private void mockEuropaPark()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockEuropaPark");

        Park europaPark = Park.create("Europa Park");
        locations.Germany.addChildAndSetParent(europaPark);

        OnSiteAttraction silverStar = OnSiteAttraction.create("Silver Star", 5);
        silverStar.setCreditType(creditTypes.RollerCoaster);
        silverStar.setCategory(categories.RollerCoasters);
        silverStar.setManufacturer(manufacturers.BolligerAndMabillard);
        europaPark.addChildAndSetParent(silverStar);

        OnSiteAttraction wodan = OnSiteAttraction.create("Wodan - Timburcoaster", 4);
        wodan.setCreditType(creditTypes.RollerCoaster);
        wodan.setCategory(categories.RollerCoasters);
        wodan.setManufacturer(manufacturers.GCI);
        europaPark.addChildAndSetParent(wodan);

        OnSiteAttraction blueFireMegacoaster = OnSiteAttraction.create("Blue Fire Megacoaster", 4);
        blueFireMegacoaster.setCreditType(creditTypes.RollerCoaster);
        blueFireMegacoaster.setCategory(categories.RollerCoasters);
        blueFireMegacoaster.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(blueFireMegacoaster);

        OnSiteAttraction eurosatCanCanCoaster = OnSiteAttraction.create("Eurosat - Can Can Coaster");
        eurosatCanCanCoaster.setCreditType(creditTypes.RollerCoaster);
        eurosatCanCanCoaster.setCategory(categories.RollerCoasters);
        eurosatCanCanCoaster.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(eurosatCanCanCoaster);

        OnSiteAttraction arthur = OnSiteAttraction.create("Arthur", 1);
        arthur.setCreditType(creditTypes.RollerCoaster);
        arthur.setCategory(categories.RollerCoasters);
        arthur.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(arthur);

        OnSiteAttraction matterhornBlitz = OnSiteAttraction.create("Matterhorn-Blitz", 2);
        matterhornBlitz.setCreditType(creditTypes.RollerCoaster);
        matterhornBlitz.setCategory(categories.RollerCoasters);
        matterhornBlitz.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(matterhornBlitz);

        OnSiteAttraction poseidon = OnSiteAttraction.create("Poseidon", 2);
        poseidon.setCreditType(creditTypes.RollerCoaster);
        poseidon.setCategory(categories.RollerCoasters);
        poseidon.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(poseidon);

        OnSiteAttraction euroMir = OnSiteAttraction.create("Euro-Mir", 1);
        euroMir.setCreditType(creditTypes.RollerCoaster);
        euroMir.setCategory(categories.RollerCoasters);
        euroMir.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(euroMir);

        OnSiteAttraction atlantica = OnSiteAttraction.create("Atlantica SuperSplash", 2);
        atlantica.setCreditType(creditTypes.RollerCoaster);
        atlantica.setCategory(categories.RollerCoasters);
        atlantica.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(atlantica);

        OnSiteAttraction pegasus = OnSiteAttraction.create("Pegasus", 1);
        pegasus.setCreditType(creditTypes.RollerCoaster);
        pegasus.setCategory(categories.RollerCoasters);
        pegasus.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(pegasus);

        OnSiteAttraction alpenexpressEnzian = OnSiteAttraction.create("Alpenexpress Enzian", 1);
        alpenexpressEnzian.setCreditType(creditTypes.RollerCoaster);
        alpenexpressEnzian.setCategory(categories.RollerCoasters);
        alpenexpressEnzian.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(alpenexpressEnzian);

        OnSiteAttraction schweizerBobbahn = OnSiteAttraction.create("Schweizer Bobbahn", 2);
        schweizerBobbahn.setCreditType(creditTypes.RollerCoaster);
        schweizerBobbahn.setCategory(categories.RollerCoasters);
        schweizerBobbahn.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(schweizerBobbahn);

        OnSiteAttraction baaaExpress = OnSiteAttraction.create("Ba-a-a Express", 1);
        baaaExpress.setCreditType(creditTypes.RollerCoaster);
        baaaExpress.setCategory(categories.RollerCoasters);
        baaaExpress.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(baaaExpress);

        OnSiteAttraction abenteuerAtlantis = OnSiteAttraction.create("Abenteuer Atlantis", 1);
        abenteuerAtlantis.setCategory(categories.DarkRides);
        abenteuerAtlantis.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(abenteuerAtlantis);

        OnSiteAttraction epExpress = OnSiteAttraction.create("EP-Express", 1);
        epExpress.setCategory(categories.TransportRides);
        europaPark.addChildAndSetParent(epExpress);

        OnSiteAttraction euroTower = OnSiteAttraction.create("Euro-Tower", 1);
        euroTower.setCategory(categories.FamilyRides);
        euroTower.setManufacturer(manufacturers.Intamin);
        europaPark.addChildAndSetParent(euroTower);

        OnSiteAttraction fjordRafting = OnSiteAttraction.create("Fjord Rafting", 2);
        fjordRafting.setCategory(categories.WaterRides);
        fjordRafting.setManufacturer(manufacturers.Intamin);
        europaPark.addChildAndSetParent(fjordRafting);

        OnSiteAttraction fluchDerKassandra = OnSiteAttraction.create("Fluch der Kassandra", 1);
        fluchDerKassandra.setCategory(categories.FamilyRides);
        fluchDerKassandra.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(fluchDerKassandra);

        OnSiteAttraction geisterschloss = OnSiteAttraction.create("Geisterschloss", 1);
        geisterschloss.setCategory(categories.DarkRides);
        geisterschloss.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(geisterschloss);

        OnSiteAttraction piccoloMondo = OnSiteAttraction.create("Piccolo Mondo", 1);
        piccoloMondo.setCategory(categories.DarkRides);
        piccoloMondo.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(piccoloMondo);

        OnSiteAttraction schlittenfahrtSchneefloeckchen = OnSiteAttraction.create("Schlittenfahrt Schneeflöckchen", 1);
        schlittenfahrtSchneefloeckchen.setCategory(categories.DarkRides);
        schlittenfahrtSchneefloeckchen.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(schlittenfahrtSchneefloeckchen);

        OnSiteAttraction tirolerWildwasserbahn = OnSiteAttraction.create("Tiroler Wildwasserbahn", 2);
        tirolerWildwasserbahn.setCategory(categories.WaterRides);
        tirolerWildwasserbahn.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(tirolerWildwasserbahn);

        OnSiteAttraction vindjammer = OnSiteAttraction.create("Vindjammer", 2);
        vindjammer.setCategory(categories.FamilyRides);
        vindjammer.setManufacturer(manufacturers.Huss);
        europaPark.addChildAndSetParent(vindjammer);

        OnSiteAttraction voletarium = OnSiteAttraction.create("Voletarium", 2);
        voletarium.setCategory(categories.FamilyRides);
        europaPark.addChildAndSetParent(voletarium);

        OnSiteAttraction wienerWellenflieger = OnSiteAttraction.create("Wiener Wellenflieger", 1);
        wienerWellenflieger.setCategory(categories.FamilyRides);
        wienerWellenflieger.setManufacturer(manufacturers.Zierer);
        europaPark.addChildAndSetParent(wienerWellenflieger);

        OnSiteAttraction kolumbusjolle = OnSiteAttraction.create("Kolumbusjolle", 1);
        kolumbusjolle.setCategory(categories.FamilyRides);
        kolumbusjolle.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(kolumbusjolle);

        OnSiteAttraction feriaSwing = OnSiteAttraction.create("Feria Swing");
        feriaSwing.setCategory(categories.FamilyRides);
        feriaSwing.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(feriaSwing);

        OnSiteAttraction poppyTowers = OnSiteAttraction.create("Poppy Towers");
        poppyTowers.setCategory(categories.FamilyRides);
        poppyTowers.setManufacturer(manufacturers.Zierer);
        europaPark.addChildAndSetParent(poppyTowers);

        OnSiteAttraction madameFreudenreichsCuriosites = OnSiteAttraction.create("Madame Freudenreichs Curiosités");
        madameFreudenreichsCuriosites.setCategory(categories.DarkRides);
        madameFreudenreichsCuriosites.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(madameFreudenreichsCuriosites);

        OnSiteAttraction panoramabahn = OnSiteAttraction.create("Panoramabahn");
        panoramabahn.setCategory(categories.TransportRides);
        panoramabahn.setManufacturer(manufacturers.ChanceRides);
        europaPark.addChildAndSetParent(panoramabahn);


        // 2019

        LinkedHashMap<OnSiteAttraction, Integer> rides04102019 = new LinkedHashMap<>();
        rides04102019.put(arthur, 1);
        rides04102019.put(alpenexpressEnzian, 1);
        rides04102019.put(wodan, 3);
        rides04102019.put(silverStar, 3);
        rides04102019.put(eurosatCanCanCoaster, 2);
        rides04102019.put(matterhornBlitz, 1);
        rides04102019.put(schweizerBobbahn, 1);
        rides04102019.put(euroMir, 1);
        rides04102019.put(blueFireMegacoaster, 2);
        rides04102019.put(feriaSwing, 1);
        rides04102019.put(fluchDerKassandra, 1);
        rides04102019.put(voletarium, 1);
        rides04102019.put(geisterschloss, 1);
        europaPark.addChildAndSetParent(this.createVisit(4, 10, 2019, rides04102019));

        LinkedHashMap<OnSiteAttraction, Integer> rides05102019 = new LinkedHashMap<>();
        rides05102019.put(blueFireMegacoaster, 1);
        rides05102019.put(wodan, 1);
        rides05102019.put(atlantica, 1);
        rides05102019.put(silverStar, 2);
        rides05102019.put(poseidon, 1);
        rides05102019.put(matterhornBlitz, 1);
        rides05102019.put(euroMir, 1);
        rides05102019.put(eurosatCanCanCoaster, 1);
        rides05102019.put(wienerWellenflieger, 1);
        rides05102019.put(poppyTowers, 1);
        rides05102019.put(voletarium, 1);
        rides05102019.put(tirolerWildwasserbahn, 1);
        rides05102019.put(fjordRafting, 1);
        rides05102019.put(madameFreudenreichsCuriosites, 1);
        rides05102019.put(geisterschloss, 1);
        rides05102019.put(panoramabahn, 1);
        europaPark.addChildAndSetParent(this.createVisit(5, 10, 2019, rides05102019));
    }

    private void mockEnergylandia()
    {
        Log.e(Constants.LOG_TAG, "DatabaseMock.mockEnergylandia");

        Park energylandia = Park.create("Energylandia");
        locations.Poland.addChildAndSetParent(energylandia);

        OnSiteAttraction zadra = OnSiteAttraction.create("Zadra");
        zadra.setCreditType(creditTypes.RollerCoaster);
        zadra.setCategory(categories.RollerCoasters);
        zadra.setManufacturer(manufacturers.RMC);
        energylandia.addChildAndSetParent(zadra);

        OnSiteAttraction hyperion = OnSiteAttraction.create("Hyperion");
        hyperion.setCreditType(creditTypes.RollerCoaster);
        hyperion.setCategory(categories.RollerCoasters);
        hyperion.setManufacturer(manufacturers.Intamin);
        energylandia.addChildAndSetParent(hyperion);

        OnSiteAttraction formula = OnSiteAttraction.create("Formula");
        formula.setCreditType(creditTypes.RollerCoaster);
        formula.setCategory(categories.RollerCoasters);
        formula.setManufacturer(manufacturers.Vekoma);
        energylandia.addChildAndSetParent(formula);

        OnSiteAttraction mayan = OnSiteAttraction.create("Mayan");
        mayan.setCreditType(creditTypes.RollerCoaster);
        mayan.setCategory(categories.RollerCoasters);
        mayan.setManufacturer(manufacturers.Vekoma);
        energylandia.addChildAndSetParent(mayan);

        OnSiteAttraction dragon = OnSiteAttraction.create("Dragon");
        dragon.setCreditType(creditTypes.RollerCoaster);
        dragon.setCategory(categories.RollerCoasters);
        dragon.setManufacturer(manufacturers.Vekoma);
        energylandia.addChildAndSetParent(dragon);

        OnSiteAttraction frida = OnSiteAttraction.create("Frida");
        frida.setCreditType(creditTypes.RollerCoaster);
        frida.setCategory(categories.RollerCoasters);
        frida.setManufacturer(manufacturers.Vekoma);
        energylandia.addChildAndSetParent(frida);

        OnSiteAttraction mars = OnSiteAttraction.create("Mars");
        mars.setCreditType(creditTypes.RollerCoaster);
        mars.setCategory(categories.RollerCoasters);
        mars.setManufacturer(manufacturers.SbfVisa);
        energylandia.addChildAndSetParent(mars);

        OnSiteAttraction boomerang = OnSiteAttraction.create("Boomerang");
        boomerang.setCreditType(creditTypes.RollerCoaster);
        boomerang.setCategory(categories.RollerCoasters);
        boomerang.setManufacturer(manufacturers.Vekoma);
        energylandia.addChildAndSetParent(boomerang);

        OnSiteAttraction speed = OnSiteAttraction.create("Speed");
        speed.setCreditType(creditTypes.RollerCoaster);
        speed.setCategory(categories.RollerCoasters);
        speed.setManufacturer(manufacturers.Intamin);
        energylandia.addChildAndSetParent(speed);

        OnSiteAttraction energus = OnSiteAttraction.create("Energuś");
        energus.setCreditType(creditTypes.RollerCoaster);
        energus.setCategory(categories.RollerCoasters);
        energus.setManufacturer(manufacturers.Vekoma);
        energylandia.addChildAndSetParent(energus);

        OnSiteAttraction happyLoops = OnSiteAttraction.create("Happy Loops");
        happyLoops.setCreditType(creditTypes.RollerCoaster);
        happyLoops.setCategory(categories.RollerCoasters);
        happyLoops.setManufacturer(manufacturers.SbfVisa);
        energylandia.addChildAndSetParent(happyLoops);

        OnSiteAttraction draken = OnSiteAttraction.create("Draken");
        draken.setCreditType(creditTypes.RollerCoaster);
        draken.setCategory(categories.RollerCoasters);
        draken.setManufacturer(manufacturers.PrestonAndBarbieri);
        energylandia.addChildAndSetParent(draken);

        OnSiteAttraction viking = OnSiteAttraction.create("Viking");
        viking.setCreditType(creditTypes.RollerCoaster);
        viking.setCategory(categories.RollerCoasters);
        viking.setManufacturer(manufacturers.SbfVisa);
        energylandia.addChildAndSetParent(viking);

        OnSiteAttraction fruttiLoop = OnSiteAttraction.create("Frutti Loop");
        fruttiLoop.setCreditType(creditTypes.RollerCoaster);
        fruttiLoop.setCategory(categories.RollerCoasters);
        fruttiLoop.setManufacturer(manufacturers.Pinfari);
        energylandia.addChildAndSetParent(fruttiLoop);

        OnSiteAttraction circusCoaster = OnSiteAttraction.create("Circus Coaster");
        circusCoaster.setCreditType(creditTypes.RollerCoaster);
        circusCoaster.setCategory(categories.RollerCoasters);
        circusCoaster.setManufacturer(manufacturers.SbfVisa);
        energylandia.addChildAndSetParent(circusCoaster);

        OnSiteAttraction monsterAttack = OnSiteAttraction.create("Monster Attack");
        monsterAttack.setCategory(categories.DarkRides);
        monsterAttack.setManufacturer(manufacturers.SbfVisa);
        energylandia.addChildAndSetParent(monsterAttack);

        OnSiteAttraction aztecSwing = OnSiteAttraction.create("Aztec Swing");
        aztecSwing.setCategory(categories.ThrillRides);
        aztecSwing.setManufacturer(manufacturers.SbfVisa);
        energylandia.addChildAndSetParent(aztecSwing);

        OnSiteAttraction spaceGun = OnSiteAttraction.create("Space Gun");
        spaceGun.setCategory(categories.ThrillRides);
        spaceGun.setManufacturer(manufacturers.SbfVisa);
        energylandia.addChildAndSetParent(spaceGun);


        // 2019
        LinkedHashMap<OnSiteAttraction, Integer> rides02102019 = new LinkedHashMap<>();
        rides02102019.put(zadra, 16);
        rides02102019.put(hyperion, 5);
        rides02102019.put(formula, 2);
        rides02102019.put(mayan, 2);
        rides02102019.put(draken, 1);
        rides02102019.put(frida, 1);
        rides02102019.put(dragon, 1);
        rides02102019.put(speed, 1);
        rides02102019.put(happyLoops, 1);
        rides02102019.put(energus, 1);
        rides02102019.put(fruttiLoop, 1);
        rides02102019.put(boomerang, 1);
        rides02102019.put(viking, 1);
        rides02102019.put(circusCoaster, 1);
        rides02102019.put(mars, 1);
        rides02102019.put(aztecSwing, 1);
        rides02102019.put(spaceGun, 1);
        rides02102019.put(monsterAttack, 1);
        energylandia.addChildAndSetParent(this.createVisit(2, 10, 2019, rides02102019));
    }

    private Visit createVisit(int day, int month, int year, LinkedHashMap<OnSiteAttraction, Integer> rides)
    {
        Log.i(Constants.LOG_TAG, String.format("DatabaseMock.createVisit:: creating Visit at [%s]...", day + "." + month + "." + year));

        Visit visit = Visit.create(year, month - 1, day);

        for(Map.Entry<OnSiteAttraction, Integer> entry : rides.entrySet())
        {
            VisitedAttraction visitedAttraction = VisitedAttraction.create(entry.getKey());
            visitedAttraction.increaseTrackedRideCount(entry.getValue());
            visit.addChildAndSetParent(visitedAttraction);
        }

        return visit;
    }


    private void flattenContentTree(IElement element)
    {
        if(App.content.containsElement(element))
        {
            Log.w(Constants.LOG_TAG,  String.format("DatabaseMock.flattenContentTree:: not adding %s to content as it is already known", element));
        }
        else
        {
            Log.v(Constants.LOG_TAG, String.format("DatabaseMock.flattenContentTree:: adding %s to content", element));
            App.content.addElement(element);
        }

        for (IElement child : element.getChildren())
        {
            this.flattenContentTree(child);
        }
    }

    @Override
    public boolean saveContent(Content content)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.saveContent:: DatabaseMock is not able to persist any data - content not persited");
        return true;
    }

    @Override
    public boolean synchronize(Set<IElement> elementsToCreate, Set<IElement> elementsToUpdate, Set<IElement> elementsToDelete)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.synchronize:: DatabaseMock is not able to persist any data - persistence not synchronized");
        return true;
    }

    @Override
    public boolean create(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.create:: DatabaseMock is not able to persist any data - elements not created");
        return true;
    }

    @Override
    public boolean update(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.update:: DatabaseMock is not able to persist any data - elements not updated");
        return true;
    }

    @Override
    public boolean delete(Set<IElement> elements)
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.delete:: DatabaseMock is not able to persist any data - elements not deleted");
        return true;
    }

    @Override
    public StatisticsGlobalTotals fetchStatisticsGlobalTotals()
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.fetchStatisticsGlobalTotals:: empty mock implementation to satisfy interface");

        StatisticsGlobalTotals statisticsGlobalTotals = new StatisticsGlobalTotals();
        statisticsGlobalTotals.visits = -1;
        statisticsGlobalTotals.credits = -1;
        statisticsGlobalTotals.rides = -1;

        return statisticsGlobalTotals;
    }

    @Override
    public List<Visit> fetchCurrentVisits()
    {
        Log.e(Constants.LOG_TAG,  "DatabaseMock.fetchCurrentVisits:: empty mock implementation to satisfy interface");
        return new ArrayList<>();
    }

    static private class CreditTypes
    {
        final CreditType RollerCoaster = CreditType.create("CoasterCredit");

        final List<CreditType> AllCreditTypes = new LinkedList<>();

        CreditTypes()
        {
            AllCreditTypes.add(CreditType.getDefault());

            AllCreditTypes.add(RollerCoaster);
        }
    }

    static private class Categories
    {
        final Category RollerCoasters = Category.create("RollerCoasters");
        final Category ThrillRides = Category.create("Thrill Rides");
        final Category FamilyRides = Category.create("Family Rides");
        final Category WaterRides = Category.create("Water Rides");
        final Category DarkRides = Category.create("Dark Rides");
        final Category TransportRides = Category.create("Transport Rides");

        final List<Category> AllCategories = new LinkedList<>();

        Categories()
        {
            AllCategories.add(Category.getDefault());

            AllCategories.add(RollerCoasters);
            AllCategories.add(ThrillRides);
            AllCategories.add(FamilyRides);
            AllCategories.add(WaterRides);
            AllCategories.add(DarkRides);
            AllCategories.add(TransportRides);
        }
    }

    static private class Manufacturers
    {
        final Manufacturer BolligerAndMabillard = Manufacturer.create("Bolliger & Mabillard");
        final Manufacturer Intamin = Manufacturer.create("Intamin");
        final Manufacturer Vekoma = Manufacturer.create("Vekoma");
        final Manufacturer Huss = Manufacturer.create("HUSS");
        final Manufacturer Pinfari = Manufacturer.create("Pinfari");
        final Manufacturer MaurerRides = Manufacturer.create("Mauerer Rides");
        final Manufacturer EtfRideSystems = Manufacturer.create("ETF Ride Systems");
        final Manufacturer Zierer = Manufacturer.create("Zierer");
        final Manufacturer Hofmann = Manufacturer.create("Hofmann");
        final Manufacturer Hafema = Manufacturer.create("Hafema");
        final Manufacturer PrestonAndBarbieri = Manufacturer.create("Preston & Barbieri");
        final Manufacturer Schwarzkopf = Manufacturer.create("Schwarzkopf");
        final Manufacturer Mack = Manufacturer.create("Mack Rides");
        final Manufacturer SAndS = Manufacturer.create("S&S");
        final Manufacturer SbfVisa = Manufacturer.create("SBF Visa Group");
        final Manufacturer Triotech = Manufacturer.create("Triotech");
        final Manufacturer Zamperla = Manufacturer.create("Zamperla");
        final Manufacturer ArrowDynamics = Manufacturer.create("Arrow Dynamics");
        final Manufacturer Simworx = Manufacturer.create("Simworx");
        final Manufacturer CCI = Manufacturer.create("Custom Coasters International");
        final Manufacturer RMC = Manufacturer.create("Rocky Mountain Construction");
        final Manufacturer Gerstlauer = Manufacturer.create("Gerstlauer Amusement Rides");
        final Manufacturer PremierRides = Manufacturer.create("Premier Rides");
        final Manufacturer GCI = Manufacturer.create("Great Coasters International");
        final Manufacturer ChanceRides = Manufacturer.create("Chance Rides");

        final List<Manufacturer> AllManufacturers = new LinkedList<>();

        Manufacturers()
        {
            AllManufacturers.add(Manufacturer.getDefault());

            AllManufacturers.add(BolligerAndMabillard);
            AllManufacturers.add(Intamin);
            AllManufacturers.add(Vekoma);
            AllManufacturers.add(Huss);
            AllManufacturers.add(Pinfari);
            AllManufacturers.add(MaurerRides);
            AllManufacturers.add(EtfRideSystems);
            AllManufacturers.add(Zierer);
            AllManufacturers.add(Hofmann);
            AllManufacturers.add(Hafema);
            AllManufacturers.add(PrestonAndBarbieri);
            AllManufacturers.add(Schwarzkopf);
            AllManufacturers.add(Mack);
            AllManufacturers.add(SAndS);
            AllManufacturers.add(SbfVisa);
            AllManufacturers.add(Triotech);
            AllManufacturers.add(Zamperla);
            AllManufacturers.add(ArrowDynamics);
            AllManufacturers.add(Simworx);
            AllManufacturers.add(CCI);
            AllManufacturers.add(RMC);
            AllManufacturers.add(Gerstlauer);
            AllManufacturers.add(PremierRides);
            AllManufacturers.add(GCI);
            AllManufacturers.add(ChanceRides);
        }
    }

    static private class Statuses
    {
        final Status ClosedForRefurbishment = Status.create("closed for refurbishment");
        final Status ClosedForConversion = Status.create("closed for conversion");
        final Status Converted = Status.create("converted");
        final Status Defunct = Status.create("defunct");
        final Status UnderConstruction = Status.create("under construction");

        final List<Status> AllStatuses = new LinkedList<>();

        Statuses()
        {
            AllStatuses.add(Status.getDefault());

            AllStatuses.add(ClosedForRefurbishment);
            AllStatuses.add(ClosedForConversion);
            AllStatuses.add(Defunct);
            AllStatuses.add(Converted);
            AllStatuses.add(UnderConstruction);
        }
    }

    static private class Locations
    {
        final Location Europe = Location.create("Europe");

        final Location Germany = Location.create("Germany");
        final Location Bremen = Location.create("Bremen");

        final Location Netherlands = Location.create("Netherlands");
        final Location Spain = Location.create("Spain");
        final Location Poland = Location.create("Poland");

        Locations()
        {
            Germany.addChildAndSetParent(Bremen);

            Europe.addChildAndSetParent(Germany);
            Europe.addChildAndSetParent(Netherlands);
            Europe.addChildAndSetParent(Spain);
            Europe.addChildAndSetParent(Poland);
        }
    }
}