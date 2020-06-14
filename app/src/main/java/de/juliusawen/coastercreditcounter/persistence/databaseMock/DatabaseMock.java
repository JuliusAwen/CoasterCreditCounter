package de.juliusawen.coastercreditcounter.persistence.databaseMock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.juliusawen.coastercreditcounter.application.App;
import de.juliusawen.coastercreditcounter.application.Content;
import de.juliusawen.coastercreditcounter.dataModel.elements.IElement;
import de.juliusawen.coastercreditcounter.dataModel.elements.Location;
import de.juliusawen.coastercreditcounter.dataModel.elements.Park;
import de.juliusawen.coastercreditcounter.dataModel.elements.Visit;
import de.juliusawen.coastercreditcounter.dataModel.elements.annotations.Note;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.OnSiteAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.attractions.VisitedAttraction;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Category;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.CreditType;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Manufacturer;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Model;
import de.juliusawen.coastercreditcounter.dataModel.elements.properties.Status;
import de.juliusawen.coastercreditcounter.dataModel.statistics.StatisticsGlobalTotals;
import de.juliusawen.coastercreditcounter.persistence.IDatabaseWrapper;
import de.juliusawen.coastercreditcounter.tools.ConvertTool;
import de.juliusawen.coastercreditcounter.tools.Stopwatch;
import de.juliusawen.coastercreditcounter.tools.logger.Log;
import de.juliusawen.coastercreditcounter.tools.logger.LogLevel;

public final class DatabaseMock implements IDatabaseWrapper
{
    private final CreditTypes creditTypes;
    private final Categories categories;
    private final Manufacturers manufacturers;
    private final Models models;
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
        Log.frame(LogLevel.INFO, "initializing...", '#', true);

        this.creditTypes = new CreditTypes();
        this.categories = new Categories();
        this.manufacturers = new Manufacturers();
        this.models = new Models(this.creditTypes, this.categories, this.manufacturers);
        this.statuses = new Statuses();

        this.locations = new Locations();
    }

    @Override
    public boolean loadContent(Content content)
    {
        Log.i("mocking Parks and Visits...");
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

        Log.i("creating node tree");
        content.addElement(locations.Bremen); //adding one location is enough - content is searching for root from there
        this.flattenContentTree(App.content.getRootLocation());

        Log.i("adding Properties to content");
        content.addElements(ConvertTool.convertElementsToType(creditTypes.AllCreditTypes, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(categories.AllCategories, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(manufacturers.AllManufacturers, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(models.AllModels, IElement.class));
        content.addElements(ConvertTool.convertElementsToType(statuses.AllStatuses, IElement.class));

        Log.d(String.format(Locale.getDefault(), "mock data successfully created - took [%d]ms", stopwatch.stop()));
        return true;
    }

    private void mockPhantasialand()
    {
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park phantasialand = Park.create("Phantasialand");
        locations.Germany.addChildAndSetParent(phantasialand);

        OnSiteAttraction taron = OnSiteAttraction.create("Taron", 38);
        taron.setModel(models.Intamin_BlitzCoaster);
        phantasialand.addChildAndSetParent(taron);

        OnSiteAttraction blackMamba = OnSiteAttraction.create("Black Mamba", 18);
        blackMamba.setModel(models.BollingerAndMabillard_InvertedCoaster);
        phantasialand.addChildAndSetParent(blackMamba);

        OnSiteAttraction fly = OnSiteAttraction.create("F.L.Y.");
        fly.setModel(models.Vekoma_FlyingCoaster);
        fly.setStatus(statuses.UnderConstruction);
        phantasialand.addChildAndSetParent(fly);

        OnSiteAttraction coloradoAdventure = OnSiteAttraction.create("Colorado Adventure", 11);
        coloradoAdventure.setModel(models.Vekoma_MineTrain);
        phantasialand.addChildAndSetParent(coloradoAdventure);

        OnSiteAttraction winjasFear = OnSiteAttraction.create("Winja's Fear", 8);
        winjasFear.setModel(models.Maurer_SpinningCoaster);
        phantasialand.addChildAndSetParent(winjasFear);

        OnSiteAttraction winjasForce = OnSiteAttraction.create("Winja's Force", 8);
        winjasForce.setModel(models.Maurer_SpinningCoaster);
        phantasialand.addChildAndSetParent(winjasForce);

        OnSiteAttraction raik = OnSiteAttraction.create("Raik", 5);
        raik.setModel(models.Vekoma_FamilyBoomerang);
        phantasialand.addChildAndSetParent(raik);

        OnSiteAttraction templeOfTheNightHawk = OnSiteAttraction.create("Temple of the Night Hawk", 9);
        templeOfTheNightHawk.setModel(models.Vekoma_MK900);
        phantasialand.addChildAndSetParent(templeOfTheNightHawk);

        OnSiteAttraction mysteryCastle = OnSiteAttraction.create("Mystery Castle");
        mysteryCastle.setManufacturer(manufacturers.Intamin);
        mysteryCastle.setModel(models.EnclosedDropTower);
        phantasialand.addChildAndSetParent(mysteryCastle);

        OnSiteAttraction hollywoodTour = OnSiteAttraction.create("Hollywood Tour");
        hollywoodTour.setCategory(categories.DarkRides);
        hollywoodTour.setManufacturer(manufacturers.Intamin);
        hollywoodTour.setModel(models.BoatRide);
        phantasialand.addChildAndSetParent(hollywoodTour);

        OnSiteAttraction chiapas = OnSiteAttraction.create("Chiapas", 10);
        chiapas.setManufacturer(manufacturers.Intamin);
        chiapas.setModel(models.LogFlume);
        phantasialand.addChildAndSetParent(chiapas);

        OnSiteAttraction talocan = OnSiteAttraction.create("Talocan");
        talocan.setModel(models.Huss_SuspendedTopSpin);
        phantasialand.addChildAndSetParent(talocan);

        OnSiteAttraction fengJuPalace = OnSiteAttraction.create("Feng Ju Palace");
        fengJuPalace.setModel(models.Vekoma_MadHouse);
        phantasialand.addChildAndSetParent(fengJuPalace);

        OnSiteAttraction geisterRiksha = OnSiteAttraction.create("Geister Rikscha");
        geisterRiksha.setCategory(categories.DarkRides);
        geisterRiksha.setManufacturer(manufacturers.Schwarzkopf);
        phantasialand.addChildAndSetParent(geisterRiksha);

        OnSiteAttraction mausAuChocolat = OnSiteAttraction.create("Maus-Au-Chocolat", 1);
        mausAuChocolat.setManufacturer(manufacturers.EtfRideSystems);
        mausAuChocolat.setModel(models.InteractiveDarkRide);
        phantasialand.addChildAndSetParent(mausAuChocolat);

        OnSiteAttraction wellenflug = OnSiteAttraction.create("Wellenflug");
        wellenflug.setManufacturer(manufacturers.Zierer);
        wellenflug.setModel(models.ChairSwing);
        phantasialand.addChildAndSetParent(wellenflug);

        OnSiteAttraction tikal = OnSiteAttraction.create("Tikal", 1);
        tikal.setModel(models.Zierer_DoubleFamilyTower);
        phantasialand.addChildAndSetParent(tikal);

        OnSiteAttraction verruecktesHotelTartueff = OnSiteAttraction.create("Verrücktes Hotel Tartüff");
        verruecktesHotelTartueff.setManufacturer(manufacturers.Hofmann);
        verruecktesHotelTartueff.setModel(models.FunHouse);
        phantasialand.addChildAndSetParent(verruecktesHotelTartueff);

        OnSiteAttraction riverQuest = OnSiteAttraction.create("River Quest");
        riverQuest.setManufacturer(manufacturers.Hafema);
        riverQuest.setModel(models.RiverRapids);
        phantasialand.addChildAndSetParent(riverQuest);

        OnSiteAttraction pferdekarusell = OnSiteAttraction.create("Pferdekarusell");
        pferdekarusell.setManufacturer(manufacturers.PrestonAndBarbieri);
        pferdekarusell.setModel(models.Carousel);
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
        Log.frame(LogLevel.INFO, "creating park...", '+', true);

        Park heidePark = Park.create("Heide Park");
        locations.Germany.addChildAndSetParent(heidePark);

        OnSiteAttraction colossos = OnSiteAttraction.create("Colossos");
        colossos.setModel(models.Intamin_Prefab_Woodie);
        heidePark.addChildAndSetParent(colossos);

        OnSiteAttraction krake = OnSiteAttraction.create("Krake", 14);
        krake.setModel(models.BollingerAndMabillard_DiveCoaster);
        heidePark.addChildAndSetParent(krake);

        OnSiteAttraction flugDerDaemonen = OnSiteAttraction.create("Flug der Dämonen", 11);
        flugDerDaemonen.setModel(models.BollingerAndMabillard_WingCoaster);
        heidePark.addChildAndSetParent(flugDerDaemonen);

        OnSiteAttraction desertRace = OnSiteAttraction.create("Desert Race", 10);
        desertRace.setModel(models.Intamin_AcceleratorCoaster);
        heidePark.addChildAndSetParent(desertRace);

        OnSiteAttraction bigLoop = OnSiteAttraction.create("Big Loop", 2);
        bigLoop.setModel(models.Vekoma_MK1200);
        heidePark.addChildAndSetParent(bigLoop);

        OnSiteAttraction grottenblitz = OnSiteAttraction.create("Grottenblitz", 1);
        grottenblitz.setModel(models.Mack_PoweredCoaster);
        heidePark.addChildAndSetParent(grottenblitz);

        OnSiteAttraction limit = OnSiteAttraction.create("Limit", 1);
        limit.setModel(models.Vekoma_SuspendedLoopingCoaster);
        heidePark.addChildAndSetParent(limit);

        OnSiteAttraction indyBlitz = OnSiteAttraction.create("Indy-Blitz", 1);
        indyBlitz.setModel(models.Zierer_ForceOne);
        heidePark.addChildAndSetParent(indyBlitz);

        OnSiteAttraction bobbahn = OnSiteAttraction.create("Bobbahn", 1);
        bobbahn.setModel(models.Mack_Bobsled);
        heidePark.addChildAndSetParent(bobbahn);

        OnSiteAttraction scream = OnSiteAttraction.create("Scream");
        scream.setManufacturer(manufacturers.Intamin);
        scream.setModel(models.GyroDropTower);
        heidePark.addChildAndSetParent(scream);

        OnSiteAttraction mountainRafting = OnSiteAttraction.create("Mountain Rafting");
        mountainRafting.setManufacturer(manufacturers.Intamin);
        mountainRafting.setModel(models.RiverRapids);
        heidePark.addChildAndSetParent(mountainRafting);

        OnSiteAttraction wildwasserbahn = OnSiteAttraction.create("Wildwasserbahn");
        wildwasserbahn.setManufacturer(manufacturers.Mack);
        wildwasserbahn.setModel(models.LogFlume);
        heidePark.addChildAndSetParent(wildwasserbahn);

        OnSiteAttraction ghostbusters5D = OnSiteAttraction.create("Ghostbusters 5D", 1);
        ghostbusters5D.setManufacturer(manufacturers.Triotech);
        ghostbusters5D.setModel(models.InteractiveDarkRide);
        heidePark.addChildAndSetParent(ghostbusters5D);

        OnSiteAttraction monorail = OnSiteAttraction.create("Monorail");
        monorail.setManufacturer(manufacturers.Mack);
        monorail.setModel(models.Monorail);
        heidePark.addChildAndSetParent(monorail);

        OnSiteAttraction screamie = OnSiteAttraction.create("Screamie");
        screamie.setManufacturer(manufacturers.Zierer);
        screamie.setModel(models.MiniDropTower);
        heidePark.addChildAndSetParent(screamie);

        OnSiteAttraction bounty = OnSiteAttraction.create("Bounty");
        bounty.setManufacturer(manufacturers.Intamin);
        bounty.setModel(models.PirateShip);
        heidePark.addChildAndSetParent(bounty);

        OnSiteAttraction drachengrotte = OnSiteAttraction.create("Drachengrotte");
        drachengrotte.setCategory(categories.WaterRides);
        drachengrotte.setManufacturer(manufacturers.Zierer);
        drachengrotte.setModel(models.BoatRide);
        heidePark.addChildAndSetParent(drachengrotte);

        OnSiteAttraction laola = OnSiteAttraction.create("La Ola");
        laola.setManufacturer(manufacturers.Zierer);
        laola.setModel(models.ChairSwing);
        heidePark.addChildAndSetParent(laola);

        OnSiteAttraction panoramabahn = OnSiteAttraction.create("Panoramabahn");
        panoramabahn.setManufacturer(manufacturers.Mack);
        panoramabahn.setModel(models.Monorail);
        heidePark.addChildAndSetParent(panoramabahn);

        OnSiteAttraction hickshimmelsstuermer = OnSiteAttraction.create("Hick's Himmelsstürmer");
        hickshimmelsstuermer.setCategory(categories.FamilyRides);
        hickshimmelsstuermer.setManufacturer(manufacturers.Zamperla);
        hickshimmelsstuermer.setModel(models.FlatRide);
        heidePark.addChildAndSetParent(hickshimmelsstuermer);

        OnSiteAttraction kaeptnsToern = OnSiteAttraction.create("Käpt'ns Törn");
        kaeptnsToern.setCategory(categories.WaterRides);
        kaeptnsToern.setManufacturer(manufacturers.Mack);
        kaeptnsToern.setModel(models.BoatRide);
        heidePark.addChildAndSetParent(kaeptnsToern);

        OnSiteAttraction nostalgiekarusell = OnSiteAttraction.create("Nostalgiekarussell");
        nostalgiekarusell.setModel(models.Carousel);
        heidePark.addChildAndSetParent(nostalgiekarusell);

        this.setDefaults(heidePark);

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


        // 2020
        LinkedHashMap<OnSiteAttraction, Integer> rides0506202 = new LinkedHashMap<>();
        rides0506202.put(colossos, 2);
        rides0506202.put(desertRace, 2);
        rides0506202.put(krake, 3);
        rides0506202.put(flugDerDaemonen, 1);
        rides0506202.put(grottenblitz, 1);
        rides0506202.put(scream, 2);
        rides0506202.put(mountainRafting, 1);
        rides0506202.put(monorail, 1);
        heidePark.addChildAndSetParent(this.createVisit(5, 6, 2020, rides0506202));
    }

    private void mockWalibiHolland()
    {
        Log.frame(LogLevel.INFO, "creating Park...", '#', true);

        Park walibiHolland = Park.create("Walibi Holland");
        locations.Netherlands.addChildAndSetParent(walibiHolland);

        OnSiteAttraction goliath = OnSiteAttraction.create("Goliath", 7);
        goliath.setModel(models.Intamin_MegaCoaster);
        walibiHolland.addChildAndSetParent(goliath);

        OnSiteAttraction untamed = OnSiteAttraction.create("Untamed");
        untamed.setModel(models.RMC_IBoxTrack);
        untamed.addChildAndSetParent(Note.create("Attended opening day on June 1st 2019"));
        walibiHolland.addChildAndSetParent(untamed);

        OnSiteAttraction lostGravity = OnSiteAttraction.create("Lost Gravity", 7);
        lostGravity.setModel(models.Mack_BigDipper);
        walibiHolland.addChildAndSetParent(lostGravity);

        OnSiteAttraction xpressPlatform13 = OnSiteAttraction.create("Xpress: Platform 13", 2);
        xpressPlatform13.setModel(models.Vekoma_LsmCoaster);
        walibiHolland.addChildAndSetParent(xpressPlatform13);

        OnSiteAttraction speedOfSound = OnSiteAttraction.create("Speed of Sound", 2);
        speedOfSound.setModel(models.Vekoma_Boomerang);
        walibiHolland.addChildAndSetParent(speedOfSound);

        OnSiteAttraction elCondor = OnSiteAttraction.create("El Condor", 1);
        elCondor.setModel(models.Vekoma_SuspendedLoopingCoaster);
        walibiHolland.addChildAndSetParent(elCondor);

        OnSiteAttraction drako = OnSiteAttraction.create("Drako", 2);
        drako.setModel(models.Zierer_Tivoli_Medium);
        walibiHolland.addChildAndSetParent(drako);

        OnSiteAttraction robinHood = OnSiteAttraction.create("Robin Hood", 2);
        robinHood.setManufacturer(manufacturers.Vekoma);
        robinHood.setModel(models.WoodenCoaster);
        robinHood.setStatus(statuses.Converted);
        walibiHolland.addChildAndSetParent(robinHood);

        OnSiteAttraction excalibur = OnSiteAttraction.create("Excalibur", 1);
        excalibur.setModel(models.Huss_TopSpin);
        walibiHolland.addChildAndSetParent(excalibur);

        OnSiteAttraction gForce = OnSiteAttraction.create("G-Force");
        gForce.setManufacturer(manufacturers.Huss);
        gForce.setModel(models.Enterprise);
        walibiHolland.addChildAndSetParent(gForce);

        OnSiteAttraction spaceShot = OnSiteAttraction.create("Space Shot");
        spaceShot.setManufacturer(manufacturers.SAndS);
        spaceShot.setModel(models.ShotTower);
        walibiHolland.addChildAndSetParent(spaceShot);

        OnSiteAttraction spinningVibe = OnSiteAttraction.create("Spinning Vibe");
        spinningVibe.setModel(models.Huss_Magic);
        walibiHolland.addChildAndSetParent(spinningVibe);

        OnSiteAttraction skydiver = OnSiteAttraction.create("Skydiver");
        skydiver.setCategory(categories.ThrillRides);
        walibiHolland.addChildAndSetParent(skydiver);

        OnSiteAttraction tomahawk = OnSiteAttraction.create("Tomahawk");
        tomahawk.setManufacturer(manufacturers.SbfVisa);
        tomahawk.setModel(models.Frisbee);
        walibiHolland.addChildAndSetParent(tomahawk);

        OnSiteAttraction laGrandeRoue = OnSiteAttraction.create("La Grande Roue");
        laGrandeRoue.setManufacturer(manufacturers.Vekoma);
        laGrandeRoue.setModel(models.FerrisWheel);
        walibiHolland.addChildAndSetParent(laGrandeRoue);

        OnSiteAttraction leTourDesJardins = OnSiteAttraction.create("Le Tour Des Jardins");
        leTourDesJardins.setManufacturer(manufacturers.ChanceRides);
        leTourDesJardins.setModel(models.AntiqueCars);
        walibiHolland.addChildAndSetParent(leTourDesJardins);

        OnSiteAttraction losSombreros = OnSiteAttraction.create("Los Sombreros");
        losSombreros.setCategory(categories.FamilyRides);
        losSombreros.setModel(models.FlatRide);
        walibiHolland.addChildAndSetParent(losSombreros);

        OnSiteAttraction merlinsMagicCastle = OnSiteAttraction.create("Merlin's Magic Castle", 1);
        merlinsMagicCastle.setModel(models.Vekoma_MadHouse);
        walibiHolland.addChildAndSetParent(merlinsMagicCastle);

        OnSiteAttraction merrieGoround = OnSiteAttraction.create("Merrie Go'round");
        merrieGoround.setManufacturer(manufacturers.SbfVisa);
        merrieGoround.setModel(models.Carousel);
        walibiHolland.addChildAndSetParent(merrieGoround);

        OnSiteAttraction pavillonDeThe = OnSiteAttraction.create("Pavillon de Thè");
        pavillonDeThe.setManufacturer(manufacturers.ChanceRides);
        pavillonDeThe.setModel(models.Teacups);
        walibiHolland.addChildAndSetParent(pavillonDeThe);

        OnSiteAttraction superSwing = OnSiteAttraction.create("Super Swing");
        superSwing.setManufacturer(manufacturers.Zierer);
        superSwing.setModel(models.ChairSwing);
        walibiHolland.addChildAndSetParent(superSwing);

        OnSiteAttraction walibiExpress = OnSiteAttraction.create("Walibi Express");
        walibiExpress.setCategory(categories.TransportRides);
        walibiHolland.addChildAndSetParent(walibiExpress);

        OnSiteAttraction crazyRiver = OnSiteAttraction.create("Crazy River", 2);
        crazyRiver.setManufacturer(manufacturers.Mack);
        crazyRiver.setModel(models.LogFlume);
        walibiHolland.addChildAndSetParent(crazyRiver);

        OnSiteAttraction elRioGrande = OnSiteAttraction.create("El Rio Grande", 2);
        elRioGrande.setManufacturer(manufacturers.Vekoma);
        elRioGrande.setModel(models.RiverRapids);
        walibiHolland.addChildAndSetParent(elRioGrande);


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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

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
        crazyMouse.setModel(models.WildMouse);

        osterwiese2019.addChildAndSetParent(crazyMouse);

        OnSiteAttraction tomDerTiger = OnSiteAttraction.create("Tom der Tiger");
        tomDerTiger.setModel(models.Pinfari_BigApple);
        osterwiese2019.addChildAndSetParent(tomDerTiger);

        LinkedHashMap<OnSiteAttraction, Integer> rides13042019 = new LinkedHashMap<>();
        rides13042019.put(crazyMouse, 1);
        rides13042019.put(tomDerTiger, 1);
        osterwiese2019.addChildAndSetParent(this.createVisit(13, 4, 2019, rides13042019));
    }

    private void mockFreimarkt()
    {
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        //2018
        Park freimarkt2018 = Park.create("Freimarkt 2018");
        locations.Bremen.addChildAndSetParent(freimarkt2018);

        OnSiteAttraction alpinaBahn = OnSiteAttraction.create("Alpina Bahn", 2);
        alpinaBahn.setManufacturer(manufacturers.Schwarzkopf);
        alpinaBahn.setModel(models.SteelCoaster);
        freimarkt2018.addChildAndSetParent(alpinaBahn);

        OnSiteAttraction wildeMaus = OnSiteAttraction.create("Wilde Maus", 1);
        wildeMaus.setModel(models.WildMouse);
        freimarkt2018.addChildAndSetParent(wildeMaus);

        OnSiteAttraction euroCoaster = OnSiteAttraction.create("Euro Coaster", 1);
        euroCoaster.setModel(models.InvertedCoaster);
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
        rockAndRollerCoaster.setModel(models.Schwarzkopf_Wildcat);
        freimarkt2019.addChildAndSetParent(rockAndRollerCoaster);

        OnSiteAttraction wildeMausXXL = OnSiteAttraction.create("Wilde Maus XXL");
        wildeMausXXL.setModel(models.WildMouse);
        freimarkt2019.addChildAndSetParent(wildeMausXXL);

        OnSiteAttraction kuddelDerHai = OnSiteAttraction.create("Kuddel der Hai");
        kuddelDerHai.setModel(models.Pinfari_BigApple);
        freimarkt2019.addChildAndSetParent(kuddelDerHai);

        LinkedHashMap<OnSiteAttraction, Integer> rides02112019 = new LinkedHashMap<>();
        rides02112019.put(rockAndRollerCoaster, 1);
        rides02112019.put(wildeMausXXL, 1);
        rides02112019.put(kuddelDerHai, 1);
        freimarkt2019.addChildAndSetParent(this.createVisit(2, 11, 2019, rides02112019));
    }

    private void mockPortAventura()
    {
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park portAventura = Park.create("Port Aventura");
        locations.Spain.addChildAndSetParent(portAventura);

        OnSiteAttraction shambhala = OnSiteAttraction.create("Shambhala");
        shambhala.setModel(models.BollingerAndMabillard_HyperCoaster);
        portAventura.addChildAndSetParent(shambhala);

        OnSiteAttraction dragonKhan = OnSiteAttraction.create("Dragon Khan");
        dragonKhan.setModel(models.BollingerAndMabillard_SittingCoaster);
        portAventura.addChildAndSetParent(dragonKhan);

        OnSiteAttraction furiusBaco = OnSiteAttraction.create("Furius Baco");
        furiusBaco.setModel(models.Intamin_WingRiderCoaster);
        portAventura.addChildAndSetParent(furiusBaco);

        OnSiteAttraction stampidaRoja = OnSiteAttraction.create("Stampida (Roja)");
        stampidaRoja.setManufacturer(manufacturers.CCI);
        stampidaRoja.setModel(models.WoodenCoaster);
        portAventura.addChildAndSetParent(stampidaRoja);

        OnSiteAttraction stampidaAzul = OnSiteAttraction.create("Stampida (Azul)");
        stampidaAzul.setManufacturer(manufacturers.CCI);
        stampidaAzul.setModel(models.WoodenCoaster);
        portAventura.addChildAndSetParent(stampidaAzul);

        OnSiteAttraction tomahawk = OnSiteAttraction.create("Tomahawk");
        tomahawk.setManufacturer(manufacturers.CCI);
        tomahawk.setModel(models.JuniorWoodenCoaster);
        portAventura.addChildAndSetParent(tomahawk);

        OnSiteAttraction elDiablo = OnSiteAttraction.create("El Diablo");
        elDiablo.setModel(models.Arrow_MineTrain);
        portAventura.addChildAndSetParent(elDiablo);

        OnSiteAttraction tamitami = OnSiteAttraction.create("Tami-Tami");
        tamitami.setModel(models.Vekoma_JuniorCoaster);
        portAventura.addChildAndSetParent(tamitami);

        OnSiteAttraction hurakanCondor = OnSiteAttraction.create("Hurakan Condor");
        hurakanCondor.setManufacturer(manufacturers.Intamin);
        hurakanCondor.setModel(models.DropTower);
        portAventura.addChildAndSetParent(hurakanCondor);

        OnSiteAttraction serpienteEmplumada = OnSiteAttraction.create("Serpiente Emplumada");
        serpienteEmplumada.setCategory(categories.FamilyRides);
        serpienteEmplumada.setManufacturer(manufacturers.Schwarzkopf);
        serpienteEmplumada.setModel(models.FlatRide);
        portAventura.addChildAndSetParent(serpienteEmplumada);

        OnSiteAttraction tutukiSplash = OnSiteAttraction.create("Tutuki Splash");
        tutukiSplash.setManufacturer(manufacturers.Intamin);
        tutukiSplash.setModel(models.ShootTheChute);
        portAventura.addChildAndSetParent(tutukiSplash);

        OnSiteAttraction silverRiverFlumes = OnSiteAttraction.create("Silver River Flumes");
        silverRiverFlumes.setManufacturer(manufacturers.Mack);
        silverRiverFlumes.setModel(models.LogFlume);
        portAventura.addChildAndSetParent(silverRiverFlumes);

        OnSiteAttraction grandCanyonRapids = OnSiteAttraction.create("Grand Canyon Rapids");
        grandCanyonRapids.setManufacturer(manufacturers.Intamin);
        grandCanyonRapids.setModel(models.RiverRapids);
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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park ferrariLand = Park.create("Ferrari Land");
        locations.Spain.addChildAndSetParent(ferrariLand);

        OnSiteAttraction redForce = OnSiteAttraction.create("Red Force");
        redForce.setModel(models.Intamin_AcceleratorCoaster);
        ferrariLand.addChildAndSetParent(redForce);

        OnSiteAttraction juniorRedForce = OnSiteAttraction.create("Junior Red Force");
        juniorRedForce.setModel(models.SbfVisa_RaceCoaster);
        ferrariLand.addChildAndSetParent(juniorRedForce);

        OnSiteAttraction thrillTowers1 = OnSiteAttraction.create("Thrill Towers: Torre de Rebote");
        thrillTowers1.setManufacturer(manufacturers.SAndS);
        thrillTowers1.setModel(models.ShotTower);
        ferrariLand.addChildAndSetParent(thrillTowers1);

        OnSiteAttraction thrillTowers2 = OnSiteAttraction.create("Thrill Towers: Torre de Caída Libre");
        thrillTowers2.setManufacturer(manufacturers.SAndS);
        thrillTowers2.setModel(models.DropTower);
        ferrariLand.addChildAndSetParent(thrillTowers2);

        OnSiteAttraction racingLegends = OnSiteAttraction.create("Racing Legends");
        racingLegends.setCategory(categories.FamilyRides);
        racingLegends.setManufacturer(manufacturers.Simworx);
        racingLegends.setModel(models.Simulator);
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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park slagharen = Park.create("Attractiepark Slagharen");
        locations.Netherlands.addChildAndSetParent(slagharen);

        OnSiteAttraction goldrush = OnSiteAttraction.create("Gold Rush");
        goldrush.setModel(models.Gerstlauer_InfinityCoaster);
        slagharen.addChildAndSetParent(goldrush);

        OnSiteAttraction minetrain = OnSiteAttraction.create("Mine Train");
        minetrain.setModel(models.Vekoma_MineTrain);
        slagharen.addChildAndSetParent(minetrain);

        OnSiteAttraction ripsawFalls = OnSiteAttraction.create("Ripsaw Falls");
        ripsawFalls.setModel(models.LogFlume);
        slagharen.addChildAndSetParent(ripsawFalls);

        OnSiteAttraction enterprise = OnSiteAttraction.create("Enterprise");
        enterprise.setManufacturer(manufacturers.Schwarzkopf);
        enterprise.setModel(models.Enterprise);
        slagharen.addChildAndSetParent(enterprise);

        OnSiteAttraction apollo = OnSiteAttraction.create("Apollo");
        apollo.setManufacturer(manufacturers.Schwarzkopf);
        apollo.setModel(models.ChairSwing);
        slagharen.addChildAndSetParent(apollo);

        OnSiteAttraction freeFall = OnSiteAttraction.create("Free Fall");
        freeFall.setModel(models.DropTower);
        slagharen.addChildAndSetParent(freeFall);

        OnSiteAttraction pirate = OnSiteAttraction.create("Pirate");
        pirate.setManufacturer(manufacturers.Huss);
        pirate.setModel(models.PirateShip);
        slagharen.addChildAndSetParent(pirate);

        OnSiteAttraction galoppers = OnSiteAttraction.create("Galoppers");
        galoppers.setCategory(categories.FamilyRides);
        galoppers.setManufacturer(manufacturers.Zierer);
        slagharen.addChildAndSetParent(galoppers);

        OnSiteAttraction eagle = OnSiteAttraction.create("Eagle");
        eagle.setCategory(categories.FamilyRides);
        eagle.setManufacturer(manufacturers.Huss);
        eagle.setModel(models.FlatRide);
        slagharen.addChildAndSetParent(eagle);

        OnSiteAttraction tomahawk = OnSiteAttraction.create("Tomahawk");
        tomahawk.setCategory(categories.FamilyRides);
        tomahawk.setManufacturer(manufacturers.Huss);
        tomahawk.setModel(models.FlatRide);
        slagharen.addChildAndSetParent(tomahawk);

        OnSiteAttraction wildWestAdventure = OnSiteAttraction.create("Wild West Adventure");
        wildWestAdventure.setCategory(categories.DarkRides);
        wildWestAdventure.setManufacturer(manufacturers.Mack);
        wildWestAdventure.setModel(models.BoatRide);
        slagharen.addChildAndSetParent(wildWestAdventure);

        OnSiteAttraction bigWheel = OnSiteAttraction.create("Big Wheel");
        bigWheel.setManufacturer(manufacturers.Schwarzkopf);
        bigWheel.setModel(models.FerrisWheel);
        slagharen.addChildAndSetParent(bigWheel);

        OnSiteAttraction kabelbaan = OnSiteAttraction.create("Kabelbaan");
        kabelbaan.setModel(models.ChairLift);
        slagharen.addChildAndSetParent(kabelbaan);

        OnSiteAttraction monorail = OnSiteAttraction.create("Monorail");
        monorail.setManufacturer(manufacturers.Schwarzkopf);
        monorail.setModel(models.Monorail);
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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park holidayPark = Park.create("Holiday Park");
        locations.Germany.addChildAndSetParent(holidayPark);

        OnSiteAttraction expeditionGeForce = OnSiteAttraction.create("Expedition GeForce");
        expeditionGeForce.setModel(models.Intamin_MegaCoaster);
        holidayPark.addChildAndSetParent(expeditionGeForce);

        OnSiteAttraction skyScream = OnSiteAttraction.create("Sky Scream");
        skyScream.setModel(models.Premier_SkyRocketII);
        holidayPark.addChildAndSetParent(skyScream);

        OnSiteAttraction tabalugasAchterbahn = OnSiteAttraction.create("Tabalugas Achterbahn");
        tabalugasAchterbahn.setModel(models.Zierer_ForceTwo);
        holidayPark.addChildAndSetParent(tabalugasAchterbahn);

        OnSiteAttraction burgFalkenstein = OnSiteAttraction.create("Burg Falkenstein");
        burgFalkenstein.setCategory(categories.DarkRides);
        burgFalkenstein.setManufacturer(manufacturers.Mack);
        holidayPark.addChildAndSetParent(burgFalkenstein);

        OnSiteAttraction anubis = OnSiteAttraction.create("Anubis Free Fall Tower");
        anubis.setManufacturer(manufacturers.Intamin);
        anubis.setModel(models.DropTower);
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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park movieParkGermany = Park.create("Movie Park Germany");
        locations.Germany.addChildAndSetParent(movieParkGermany);

        OnSiteAttraction starTrekOperationEnterprise = OnSiteAttraction.create("Star Trek: Operation Enterprise");
        starTrekOperationEnterprise.setModel(models.Mack_LaunchedCoaster);
        movieParkGermany.addChildAndSetParent(starTrekOperationEnterprise);

        OnSiteAttraction vanHelsingsFactory = OnSiteAttraction.create("Van Helsing's Factory");
        vanHelsingsFactory.setModel(models.Gerstlauer_BobsledCoaster);
        movieParkGermany.addChildAndSetParent(vanHelsingsFactory);

        OnSiteAttraction ghostChasers = OnSiteAttraction.create("Ghost Chasers");
        ghostChasers.setModel(models.Mack_WildeMaus);
        movieParkGermany.addChildAndSetParent(ghostChasers);

        OnSiteAttraction jimmyNeutronsAtomicFlyer = OnSiteAttraction.create("Jimmy Neutron's Atomic Flyer");
        jimmyNeutronsAtomicFlyer.setModel(models.Vekoma_SuspendedFamilyCoaster);
        movieParkGermany.addChildAndSetParent(jimmyNeutronsAtomicFlyer);

        OnSiteAttraction mpXpress = OnSiteAttraction.create("MP Xpress");
        mpXpress.setModel(models.Vekoma_SuspendedLoopingCoaster);
        movieParkGermany.addChildAndSetParent(mpXpress);

        OnSiteAttraction backyardigans = OnSiteAttraction.create("The Backyardigans: Mission to Mars");
        backyardigans.setModel(models.Vekoma_JuniorCoaster);
        movieParkGermany.addChildAndSetParent(backyardigans);

        OnSiteAttraction bandit = OnSiteAttraction.create("The Bandit");
        bandit.setManufacturer(manufacturers.RollerCoasterCorporationOfAmerica);
        bandit.setModel(models.WoodenCoaster);
        movieParkGermany.addChildAndSetParent(bandit);

        OnSiteAttraction dorasBigRiverAdventuer = OnSiteAttraction.create("Dora's Big River Adventure");
        dorasBigRiverAdventuer.setManufacturer(manufacturers.Zamperla);
        dorasBigRiverAdventuer.setModel(models.LogFlume);
        movieParkGermany.addChildAndSetParent(dorasBigRiverAdventuer);

        OnSiteAttraction area51TopSecret = OnSiteAttraction.create("Area 51 - Top Secret");
        area51TopSecret.setCategory(categories.DarkRides);
        area51TopSecret.setManufacturer(manufacturers.Intamin);
        area51TopSecret.setModel(models.BoatRide);
        area51TopSecret.setStatus(statuses.Converted);
        movieParkGermany.addChildAndSetParent(area51TopSecret);

        OnSiteAttraction highFall = OnSiteAttraction.create("The High Fall");
        highFall.setManufacturer(manufacturers.Intamin);
        highFall.setModel(models.DropTower);
        movieParkGermany.addChildAndSetParent(highFall);

        OnSiteAttraction crazySurfer = OnSiteAttraction.create("Crazy Surfer");
        crazySurfer.setModel(models.Zamperla_DiskO);
        movieParkGermany.addChildAndSetParent(crazySurfer);

        OnSiteAttraction santaMonicaWheel = OnSiteAttraction.create("Santa Monica Wheel");
        santaMonicaWheel.setManufacturer(manufacturers.SbfVisa);
        santaMonicaWheel.setModel(models.FerrisWheel);
        movieParkGermany.addChildAndSetParent(santaMonicaWheel);

        OnSiteAttraction excalibur = OnSiteAttraction.create("Excalibur - Secrets of the Dark Forest");
        excalibur.setManufacturer(manufacturers.Intamin);
        excalibur.setModel(models.RiverRapids);
        movieParkGermany.addChildAndSetParent(excalibur);

        OnSiteAttraction timeRiders = OnSiteAttraction.create("Time Riders");
        timeRiders.setCategory(categories.FamilyRides);
        timeRiders.setModel(models.Simulator);
        movieParkGermany.addChildAndSetParent(timeRiders);

        OnSiteAttraction NycTransformer = OnSiteAttraction.create("NYC Transformer");
        NycTransformer.setModel(models.Huss_TopSpin);
        movieParkGermany.addChildAndSetParent(NycTransformer);

        OnSiteAttraction pierPatrolJetSki = OnSiteAttraction.create("Pier Patrol Jet Ski");
        pierPatrolJetSki.setModel(models.Zierer_WildWaterRondell);
        movieParkGermany.addChildAndSetParent(pierPatrolJetSki);

        OnSiteAttraction fairyWorldSpin = OnSiteAttraction.create("Fairy World Spin");
        fairyWorldSpin.setManufacturer(manufacturers.Mack);
        fairyWorldSpin.setModel(models.Teacups);
        movieParkGermany.addChildAndSetParent(fairyWorldSpin);

        OnSiteAttraction splatOSphere = OnSiteAttraction.create("Splat-O-Sphere");
        splatOSphere.setCategory(categories.FamilyRides);
        splatOSphere.setManufacturer(manufacturers.ChanceRides);
        splatOSphere.setModel(models.FlatRide);
        movieParkGermany.addChildAndSetParent(splatOSphere);

        OnSiteAttraction lostTemple = OnSiteAttraction.create("The Lost Temple");
        lostTemple.setCategory(categories.DarkRides);
        lostTemple.setManufacturer(manufacturers.Simworx);
        lostTemple.setModel(models.Simulator);
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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park toverland = Park.create("Toverland");
        this.locations.Netherlands.addChildAndSetParent(toverland);

        OnSiteAttraction troy = OnSiteAttraction.create("Troy");
        troy.setManufacturer(manufacturers.GCI);
        troy.setModel(models.WoodenCoaster);
        toverland.addChildAndSetParent(troy);

        OnSiteAttraction fenix = OnSiteAttraction.create("Fēnix");
        fenix.setModel(models.BollingerAndMabillard_WingCoaster);
        fenix.addChildAndSetParent(Note.create("Attended opening day on July 7th 2018"));
        toverland.addChildAndSetParent(fenix);

        OnSiteAttraction dwervelwind = OnSiteAttraction.create("Dwervelwind");
        dwervelwind.setModel(models.Mack_SpinningCoaster);
        toverland.addChildAndSetParent(dwervelwind);

        OnSiteAttraction boosterBike = OnSiteAttraction.create("Booster Bike");
        boosterBike.setModel(models.Vekoma_MotorbikeCoaster);
        toverland.addChildAndSetParent(boosterBike);

        OnSiteAttraction toosExpress = OnSiteAttraction.create("Toos-Express");
        toosExpress.setModel(models.Vekoma_JuniorCoaster);
        toverland.addChildAndSetParent(toosExpress);

        OnSiteAttraction expeditionZork = OnSiteAttraction.create("Expedition Zork");
        expeditionZork.setManufacturer(manufacturers.Mack);
        expeditionZork.setModel(models.LogFlume);
        toverland.addChildAndSetParent(expeditionZork);

        OnSiteAttraction maximusBlitzbahn = OnSiteAttraction.create("Maximus' Blitz Bahn");
        maximusBlitzbahn.setCategory(categories.FamilyRides);
        toverland.addChildAndSetParent(maximusBlitzbahn);

        OnSiteAttraction scorpios = OnSiteAttraction.create("Scorpios");
        scorpios.setModel(models.PirateShip);
        toverland.addChildAndSetParent(scorpios);

        OnSiteAttraction djenguRiver = OnSiteAttraction.create("Djengu River");
        djenguRiver.setManufacturer(manufacturers.Hafema);
        djenguRiver.setModel(models.RiverRapids);
        toverland.addChildAndSetParent(djenguRiver);

        OnSiteAttraction merlinsQuest = OnSiteAttraction.create("Merlin's Quest");
        merlinsQuest.setCategory(categories.DarkRides);
        merlinsQuest.setManufacturer(manufacturers.Mack);
        merlinsQuest.setModel(models.BoatRide);
        toverland.addChildAndSetParent(merlinsQuest);

        OnSiteAttraction villaFiasco = OnSiteAttraction.create("Villa Fiasco");
        villaFiasco.setModel(models.FunHouse);
        toverland.addChildAndSetParent(villaFiasco);

        OnSiteAttraction toverhuis = OnSiteAttraction.create("Toverhuis");
        toverhuis.setModel(models.WalkThroughAttraction);
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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park efteling = Park.create("Efteling");
        locations.Netherlands.addChildAndSetParent(efteling);


        OnSiteAttraction baron1898 = OnSiteAttraction.create("Baron 1898", 5);
        baron1898.setModel(models.BollingerAndMabillard_DiveCoaster);
        efteling.addChildAndSetParent(baron1898);

        OnSiteAttraction deVliegendeHollander = OnSiteAttraction.create("De Vliegende Hollander", 2);
        deVliegendeHollander.setCreditType(creditTypes.RollerCoaster);
        deVliegendeHollander.setCategory(categories.RollerCoasters);
        deVliegendeHollander.setManufacturer(manufacturers.KumbaK);
        efteling.addChildAndSetParent(deVliegendeHollander);

        OnSiteAttraction jorisEnDeDraakWater = OnSiteAttraction.create("Joris en de Draak (Water)", 2);
        jorisEnDeDraakWater.setManufacturer(manufacturers.GCI);
        jorisEnDeDraakWater.setModel(models.WoodenCoaster);
        efteling.addChildAndSetParent(jorisEnDeDraakWater);

        OnSiteAttraction jorisEnDeDraakVuur = OnSiteAttraction.create("Joris en de Draak (Vuur)", 1);
        jorisEnDeDraakVuur.setManufacturer(manufacturers.GCI);
        jorisEnDeDraakVuur.setModel(models.WoodenCoaster);
        efteling.addChildAndSetParent(jorisEnDeDraakVuur);

        OnSiteAttraction vogelRok = OnSiteAttraction.create("Vogel Rok", 1);
        vogelRok.setModel(models.Vekoma_MK900);
        efteling.addChildAndSetParent(vogelRok);

        OnSiteAttraction python = OnSiteAttraction.create("Python", 1);
        python.setModel(models.Vekoma_MK1200);
        efteling.addChildAndSetParent(python);

        OnSiteAttraction bobbaan = OnSiteAttraction.create("Bobbaan", 2);
        bobbaan.setModel(models.Intamin_SwissBob);
        bobbaan.setStatus(statuses.Defunct);
        efteling.addChildAndSetParent(bobbaan);

        OnSiteAttraction fataMorgana = OnSiteAttraction.create("Fata Morgana", 2);
        fataMorgana.setCategory(categories.DarkRides);
        fataMorgana.setManufacturer(manufacturers.Intamin);
        fataMorgana.setModel(models.BoatRide);
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
        symbolica.setManufacturer(manufacturers.EtfRideSystems);
        symbolica.setModel(models.TracklessDarkRide);
        efteling.addChildAndSetParent(symbolica);

        OnSiteAttraction pirana = OnSiteAttraction.create("Piraña", 2);
        pirana.setManufacturer(manufacturers.Intamin);
        pirana.setModel(models.RiverRapids);
        efteling.addChildAndSetParent(pirana);

        OnSiteAttraction stoomtrein = OnSiteAttraction.create("Efteling Stoomtrein", 1);
        stoomtrein.setModel(models.TrainRide);
        efteling.addChildAndSetParent(stoomtrein);

        OnSiteAttraction halveMaen = OnSiteAttraction.create("Halve Maen", 2);
        halveMaen.setManufacturer(manufacturers.Intamin);
        halveMaen.setModel(models.PirateShip);
        efteling.addChildAndSetParent(halveMaen);

        OnSiteAttraction polkaMarina = OnSiteAttraction.create("Polka Marina", 1);
        polkaMarina.setCategory(categories.FamilyRides);
        polkaMarina.setManufacturer(manufacturers.Vekoma);
        polkaMarina.setModel(models.FlatRide);
        efteling.addChildAndSetParent(polkaMarina);

        OnSiteAttraction spookslot = OnSiteAttraction.create("Spookslot", 1);
        spookslot.setModel(models.WalkThroughAttraction);
        efteling.addChildAndSetParent(spookslot);

        OnSiteAttraction villaVolta = OnSiteAttraction.create("Villa Volta", 2);
        villaVolta.setModel(models.Vekoma_MadHouse);
        efteling.addChildAndSetParent(villaVolta);
    }

    private void mockHansaPark()
    {
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park hansaPark = Park.create("Hansa Park");
        locations.Germany.addChildAndSetParent(hansaPark);

        OnSiteAttraction fluchVonNovgorod = OnSiteAttraction.create("Fluch von Novgorod", 4);
        fluchVonNovgorod.setModel(models.Gerstlauer_EuroFighter);
        hansaPark.addChildAndSetParent(fluchVonNovgorod);

        OnSiteAttraction schwurDesKaernan = OnSiteAttraction.create("Der Schwur des Kärnan", 6);
        schwurDesKaernan.setModel(models.Gerstlauer_InfinityCoaster);
        hansaPark.addChildAndSetParent(schwurDesKaernan);

        OnSiteAttraction nessie = OnSiteAttraction.create("Nessie", 3);
        nessie.setManufacturer(manufacturers.Schwarzkopf);
        nessie.setModel(models.SteelCoaster);
        hansaPark.addChildAndSetParent(nessie);

        OnSiteAttraction crazyMine = OnSiteAttraction.create("Crazy-Mine", 3);
        crazyMine.setModel(models.Maurer_WildeMausClassic);
        hansaPark.addChildAndSetParent(crazyMine);

        OnSiteAttraction rasenderRoland = OnSiteAttraction.create("Rasender Roland", 2);
        rasenderRoland.setModel(models.Vekoma_JuniorCoaster);
        hansaPark.addChildAndSetParent(rasenderRoland);

        OnSiteAttraction schlangeVonMidgard = OnSiteAttraction.create("Schlange von Midgard", 2);
        schlangeVonMidgard.setModel(models.Gerstlauer_FamilyCoaster);
        hansaPark.addChildAndSetParent(schlangeVonMidgard);

        OnSiteAttraction derKleineZar = OnSiteAttraction.create("Der kleine Zar", 1);
        derKleineZar.setModel(models.PrestonAndBarbieri_MiniCoaster);
        hansaPark.addChildAndSetParent(derKleineZar);

        OnSiteAttraction wasserwolfAmIlmensee = OnSiteAttraction.create("Der Wasserwolf am Ilmensee", 2);
        wasserwolfAmIlmensee.setManufacturer(manufacturers.Mack);
        wasserwolfAmIlmensee.setModel(models.LogFlume);
        hansaPark.addChildAndSetParent(wasserwolfAmIlmensee);

        OnSiteAttraction superSplash = OnSiteAttraction.create("Super Splash", 2);
        superSplash.setManufacturer(manufacturers.Intamin);
        superSplash.setModel(models.ShootTheChute);
        hansaPark.addChildAndSetParent(superSplash);

        OnSiteAttraction stoertebekersKaperfahrt = OnSiteAttraction.create("Störtebeker's Kaperfahrt", 1);
        stoertebekersKaperfahrt.setCategory(categories.WaterRides);
        hansaPark.addChildAndSetParent(stoertebekersKaperfahrt);

        OnSiteAttraction sturmfahrtDerDrachenboote = OnSiteAttraction.create("Sturmfahrt der Drachenboote", 1);
        sturmfahrtDerDrachenboote.setModel(models.LogFlume);
        hansaPark.addChildAndSetParent(sturmfahrtDerDrachenboote);

        OnSiteAttraction fliegenderHollaender = OnSiteAttraction.create("Fliegender Holländer", 1);
        fliegenderHollaender.setManufacturer(manufacturers.Huss);
        fliegenderHollaender.setModel(models.PirateShip);
        hansaPark.addChildAndSetParent(fliegenderHollaender);

        OnSiteAttraction holsteinturm = OnSiteAttraction.create("Holsteinturm", 1);
        holsteinturm.setManufacturer(manufacturers.Huss);
        holsteinturm.setModel(models.ObservationTower);
        hansaPark.addChildAndSetParent(holsteinturm);

        OnSiteAttraction kaernapulten = OnSiteAttraction.create("Kärnapulten", 1);
        kaernapulten.setModel(models.Gerstlauer_SkyFly);
        hansaPark.addChildAndSetParent(kaernapulten);

        OnSiteAttraction hanseflieger = OnSiteAttraction.create("Hanse Flieger", 1);
        hanseflieger.setModel(models.ChairSwing);
        hansaPark.addChildAndSetParent(hanseflieger);

        OnSiteAttraction fliegenderHai = OnSiteAttraction.create("Fliegender Hai", 2);
        fliegenderHai.setCategory(categories.ThrillRides);
        fliegenderHai.setManufacturer(manufacturers.Huss);
        fliegenderHai.setModel(models.FlatRide);
        fliegenderHai.setStatus(statuses.Defunct);
        hansaPark.addChildAndSetParent(fliegenderHai);

        OnSiteAttraction hansaParkExpress = OnSiteAttraction.create("Hansa Park Express");
        hansaParkExpress.setModel(models.TrainRide);
        hansaPark.addChildAndSetParent(hansaParkExpress);

        OnSiteAttraction highlander = OnSiteAttraction.create("Highlander");
        highlander.setModel(models.GyroDropTower);
        hansaPark.addChildAndSetParent(highlander);

        OnSiteAttraction barracudaSlide = OnSiteAttraction.create("Barracuda Slide");
        barracudaSlide.setCategory(categories.FamilyRides);
        hansaPark.addChildAndSetParent(barracudaSlide);

        OnSiteAttraction blumenmeerbootsfahrt = OnSiteAttraction.create("Blumenmeerbootsfahrt");
        blumenmeerbootsfahrt.setCategory(categories.WaterRides);
        blumenmeerbootsfahrt.setModel(models.BoatRide);
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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park magicParkVerden = Park.create("Magic Park Verden");
        locations.Germany.addChildAndSetParent(magicParkVerden);

        OnSiteAttraction achterbahn = OnSiteAttraction.create("Die Eiserne Schlange");
        achterbahn.setModel(models.Zierer_Tivoli_Large);
        magicParkVerden.addChildAndSetParent(achterbahn);

        OnSiteAttraction wildwasserbahn = OnSiteAttraction.create("Wildwasserbahn");
        wildwasserbahn.setModel(models.LogFlume);
        magicParkVerden.addChildAndSetParent(wildwasserbahn);


        // 2018
        LinkedHashMap<OnSiteAttraction, Integer> rides09062018 = new LinkedHashMap<>();
        rides09062018.put(achterbahn, 4);
        rides09062018.put(wildwasserbahn, 1);
        magicParkVerden.addChildAndSetParent(this.createVisit(9, 6, 2018, rides09062018));
    }

    private void mockEuropaPark()
    {
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park europaPark = Park.create("Europa Park");
        locations.Germany.addChildAndSetParent(europaPark);

        OnSiteAttraction silverStar = OnSiteAttraction.create("Silver Star", 5);
        silverStar.setModel(models.BollingerAndMabillard_HyperCoaster);
        europaPark.addChildAndSetParent(silverStar);

        OnSiteAttraction wodan = OnSiteAttraction.create("Wodan - Timburcoaster", 4);
        wodan.setManufacturer(manufacturers.GCI);
        wodan.setModel(models.WoodenCoaster);
        europaPark.addChildAndSetParent(wodan);

        OnSiteAttraction blueFireMegacoaster = OnSiteAttraction.create("Blue Fire Megacoaster", 4);
        blueFireMegacoaster.setModel(models.Mack_LaunchedCoaster);
        europaPark.addChildAndSetParent(blueFireMegacoaster);

        OnSiteAttraction eurosatCanCanCoaster = OnSiteAttraction.create("Eurosat - Can Can Coaster");
        eurosatCanCanCoaster.setCreditType(creditTypes.RollerCoaster);
        eurosatCanCanCoaster.setCategory(categories.RollerCoasters);
        eurosatCanCanCoaster.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(eurosatCanCanCoaster);

        OnSiteAttraction arthur = OnSiteAttraction.create("Arthur", 1);
        arthur.setModel(models.Mack_InvertedPoweredCoaster);
        europaPark.addChildAndSetParent(arthur);

        OnSiteAttraction matterhornBlitz = OnSiteAttraction.create("Matterhorn-Blitz", 2);
        matterhornBlitz.setModel(models.Mack_WildeMaus);
        europaPark.addChildAndSetParent(matterhornBlitz);

        OnSiteAttraction poseidon = OnSiteAttraction.create("Poseidon", 2);
        poseidon.setModel(models.Mack_WaterCoaster);
        europaPark.addChildAndSetParent(poseidon);

        OnSiteAttraction euroMir = OnSiteAttraction.create("Euro-Mir", 1);
        euroMir.setModel(models.Mack_SpinningCoaster);
        europaPark.addChildAndSetParent(euroMir);

        OnSiteAttraction atlantica = OnSiteAttraction.create("Atlantica SuperSplash", 2);
        atlantica.setModel(models.Mack_SuperSplash);
        europaPark.addChildAndSetParent(atlantica);

        OnSiteAttraction pegasus = OnSiteAttraction.create("Pegasus", 1);
        pegasus.setModel(models.Mack_YoungstarCoaster);
        europaPark.addChildAndSetParent(pegasus);

        OnSiteAttraction alpenexpressEnzian = OnSiteAttraction.create("Alpenexpress Enzian", 1);
        alpenexpressEnzian.setModel(models.Mack_PoweredCoaster);
        europaPark.addChildAndSetParent(alpenexpressEnzian);

        OnSiteAttraction schweizerBobbahn = OnSiteAttraction.create("Schweizer Bobbahn", 2);
        schweizerBobbahn.setModel(models.Mack_Bobsled);
        europaPark.addChildAndSetParent(schweizerBobbahn);

        OnSiteAttraction baaaExpress = OnSiteAttraction.create("Ba-a-a Express", 1);
        baaaExpress.setModel(models.Art_ChildrensRollerCoaster);
        europaPark.addChildAndSetParent(baaaExpress);

        OnSiteAttraction abenteuerAtlantis = OnSiteAttraction.create("Abenteuer Atlantis", 1);
        abenteuerAtlantis.setManufacturer(manufacturers.Mack);
        abenteuerAtlantis.setModel(models.InteractiveDarkRide);
        europaPark.addChildAndSetParent(abenteuerAtlantis);

        OnSiteAttraction epExpress = OnSiteAttraction.create("EP-Express", 1);
        epExpress.setModel(models.Monorail);
        europaPark.addChildAndSetParent(epExpress);

        OnSiteAttraction euroTower = OnSiteAttraction.create("Euro-Tower", 1);
        euroTower.setManufacturer(manufacturers.Intamin);
        euroTower.setModel(models.ObservationTower);
        europaPark.addChildAndSetParent(euroTower);

        OnSiteAttraction fjordRafting = OnSiteAttraction.create("Fjord Rafting", 2);
        fjordRafting.setManufacturer(manufacturers.Intamin);
        fjordRafting.setModel(models.RiverRapids);
        europaPark.addChildAndSetParent(fjordRafting);

        OnSiteAttraction fluchDerKassandra = OnSiteAttraction.create("Fluch der Kassandra", 1);
        fluchDerKassandra.setModel(models.Mack_MadHouse);
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
        tirolerWildwasserbahn.setManufacturer(manufacturers.Mack);
        tirolerWildwasserbahn.setModel(models.LogFlume);
        europaPark.addChildAndSetParent(tirolerWildwasserbahn);

        OnSiteAttraction vindjammer = OnSiteAttraction.create("Vindjammer", 2);
        vindjammer.setManufacturer(manufacturers.Huss);
        vindjammer.setModel(models.PirateShip);
        europaPark.addChildAndSetParent(vindjammer);

        OnSiteAttraction voletarium = OnSiteAttraction.create("Voletarium", 2);
        voletarium.setModel(models.FlyingTheatre);
        europaPark.addChildAndSetParent(voletarium);

        OnSiteAttraction wienerWellenflieger = OnSiteAttraction.create("Wiener Wellenflieger", 1);
        wienerWellenflieger.setManufacturer(manufacturers.Zierer);
        wienerWellenflieger.setModel(models.ChairSwing);
        europaPark.addChildAndSetParent(wienerWellenflieger);

        OnSiteAttraction kolumbusjolle = OnSiteAttraction.create("Kolumbusjolle", 1);
        kolumbusjolle.setCategory(categories.FamilyRides);
        kolumbusjolle.setManufacturer(manufacturers.Mack);
        kolumbusjolle.setModel(models.FlatRide);
        europaPark.addChildAndSetParent(kolumbusjolle);

        OnSiteAttraction feriaSwing = OnSiteAttraction.create("Feria Swing");
        feriaSwing.setCategory(categories.FamilyRides);
        feriaSwing.setManufacturer(manufacturers.Mack);
        feriaSwing.setModel(models.FlatRide);
        europaPark.addChildAndSetParent(feriaSwing);

        OnSiteAttraction poppyTowers = OnSiteAttraction.create("Poppy Towers");
        poppyTowers.setModel(models.Zierer_DoubleFamilyTower);
        europaPark.addChildAndSetParent(poppyTowers);

        OnSiteAttraction madameFreudenreichsCuriosites = OnSiteAttraction.create("Madame Freudenreichs Curiosités");
        madameFreudenreichsCuriosites.setCategory(categories.DarkRides);
        madameFreudenreichsCuriosites.setManufacturer(manufacturers.Mack);
        europaPark.addChildAndSetParent(madameFreudenreichsCuriosites);

        OnSiteAttraction panoramabahn = OnSiteAttraction.create("Panoramabahn");
        panoramabahn.setManufacturer(manufacturers.ChanceRides);
        panoramabahn.setModel(models.TrainRide);
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
        Log.frame(LogLevel.INFO, "creating Park...", '+', true);

        Park energylandia = Park.create("Energylandia");
        locations.Poland.addChildAndSetParent(energylandia);

        OnSiteAttraction zadra = OnSiteAttraction.create("Zadra");
        zadra.setModel(models.RMC_IBoxTrack);
        energylandia.addChildAndSetParent(zadra);

        OnSiteAttraction hyperion = OnSiteAttraction.create("Hyperion");
        hyperion.setModel(models.Intamin_MegaCoaster);
        energylandia.addChildAndSetParent(hyperion);

        OnSiteAttraction formula = OnSiteAttraction.create("Formula");
        formula.setModel(models.Vekoma_SpaceWarp);
        energylandia.addChildAndSetParent(formula);

        OnSiteAttraction mayan = OnSiteAttraction.create("Mayan");
        mayan.setModel(models.Vekoma_SuspendedLoopingCoaster);
        energylandia.addChildAndSetParent(mayan);

        OnSiteAttraction dragon = OnSiteAttraction.create("Dragon");
        dragon.setModel(models.Vekoma_SuspendedFamilyCoaster);
        energylandia.addChildAndSetParent(dragon);

        OnSiteAttraction frida = OnSiteAttraction.create("Frida");
        frida.setModel(models.Vekoma_JuniorCoaster);
        energylandia.addChildAndSetParent(frida);

        OnSiteAttraction mars = OnSiteAttraction.create("Mars");
        mars.setCreditType(creditTypes.RollerCoaster);
        mars.setCategory(categories.RollerCoasters);
        mars.setManufacturer(manufacturers.SbfVisa);
        energylandia.addChildAndSetParent(mars);

        OnSiteAttraction boomerang = OnSiteAttraction.create("Boomerang");
        boomerang.setModel(models.Vekoma_FamilyBoomerang);
        energylandia.addChildAndSetParent(boomerang);

        OnSiteAttraction speed = OnSiteAttraction.create("Speed");
        speed.setModel(models.Intamin_WaterCoaster);
        energylandia.addChildAndSetParent(speed);

        OnSiteAttraction energus = OnSiteAttraction.create("Energuś");
        energus.setModel(models.Vekoma_JuniorCoaster);
        energylandia.addChildAndSetParent(energus);

        OnSiteAttraction happyLoops = OnSiteAttraction.create("Happy Loops");
        happyLoops.setModel(models.SbfVisa_CompactSpinningCoaster);
        energylandia.addChildAndSetParent(happyLoops);

        OnSiteAttraction draken = OnSiteAttraction.create("Draken");
        draken.setModel(models.PrestonAndBarbieri_FamilyCoaster);
        energylandia.addChildAndSetParent(draken);

        OnSiteAttraction viking = OnSiteAttraction.create("Viking");
        viking.setModel(models.SbfVisa_SpinningCoaster);
        energylandia.addChildAndSetParent(viking);

        OnSiteAttraction fruttiLoop = OnSiteAttraction.create("Frutti Loop");
        fruttiLoop.setModel(models.SbfVisa_FamilyCoasterBigApple);
        energylandia.addChildAndSetParent(fruttiLoop);

        OnSiteAttraction circusCoaster = OnSiteAttraction.create("Circus Coaster");
        circusCoaster.setModel(models.SbfVisa_DragoonCoaster);
        energylandia.addChildAndSetParent(circusCoaster);

        OnSiteAttraction monsterAttack = OnSiteAttraction.create("Monster Attack");
        monsterAttack.setManufacturer(manufacturers.SbfVisa);
        monsterAttack.setModel(models.InteractiveDarkRide);
        energylandia.addChildAndSetParent(monsterAttack);

        OnSiteAttraction aztecSwing = OnSiteAttraction.create("Aztec Swing");
        aztecSwing.setManufacturer(manufacturers.SbfVisa);
        aztecSwing.setModel(models.Frisbee);
        energylandia.addChildAndSetParent(aztecSwing);

        OnSiteAttraction spaceGun = OnSiteAttraction.create("Space Gun");
        spaceGun.setCategory(categories.ThrillRides);
        spaceGun.setManufacturer(manufacturers.SbfVisa);
        spaceGun.setModel(models.FlatRide);
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

    private void setDefaults(Park park)
    {
        for(OnSiteAttraction attraction : park.getChildrenAsType(OnSiteAttraction.class))
        {
            if(!attraction.getModel().isCreditTypeSet() && attraction.getCreditType().isDefault())
            {
                attraction.setCreditType(CreditType.getDefault());
            }

            if(!attraction.getModel().isCategorySet() && attraction.getCategory().isDefault())
            {
                attraction.setCategory(Category.getDefault());
            }

            if(!attraction.getModel().isManufacturerSet() && attraction.getManufacturer().isDefault())
            {
                attraction.setManufacturer(Manufacturer.getDefault());
            }

            if(attraction.getModel().isDefault())
            {
                attraction.setModel(Model.getDefault());
            }

            if(attraction.getStatus().isDefault())
            {
                attraction.setStatus(Status.getDefault());
            }
        }
    }

    private Visit createVisit(int day, int month, int year, LinkedHashMap<OnSiteAttraction, Integer> rides)
    {
        Log.d(String.format("creating Visit at [%s]...", day + "." + month + "." + year));

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
            Log.w(String.format("DatabaseMock.flattenContentTree:: not adding %s to content as it is already known", element));
        }
        else
        {
            if(App.config.logDetailsOnStartup)
            {
                Log.v(String.format("DatabaseMock.flattenContentTree:: adding %s to content", element));
            }

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
        Log.w("DatabaseMock is not able to persist any data - content not persited");
        return false;
    }

    @Override
    public boolean create(Set<IElement> elements)
    {
        Log.w("DatabaseMock is not able to persist any data - elements not created");
        return false;
    }

    @Override
    public boolean update(Set<IElement> elements)
    {
        Log.w("DatabaseMock is not able to persist any data - elements not updated");
        return false;
    }

    @Override
    public boolean delete(Set<IElement> elements)
    {
        Log.w("DatabaseMock is not able to persist any data - elements not deleted");
        return false;
    }

    @Override
    public boolean synchronize(Set<IElement> elementsToCreate, Set<IElement> elementsToUpdate, Set<IElement> elementsToDelete)
    {
        Log.w("DatabaseMock is not able to persist any data - persistence not synchronized");
        return true;
    }

    @Override
    public StatisticsGlobalTotals fetchStatisticsGlobalTotals()
    {
        Log.w("mock implementation to satisfy interface - invalid statistics returned");

        StatisticsGlobalTotals statisticsGlobalTotals = new StatisticsGlobalTotals();
        statisticsGlobalTotals.totalVisits = -1;
        statisticsGlobalTotals.totalCredits = -1;
        statisticsGlobalTotals.totalRides = -1;

        return statisticsGlobalTotals;
    }

    @Override
    public List<IElement> fetchCurrentVisits()
    {
        Log.w("mock implementation to satisfy interface - empty list returned");
        return new ArrayList<>();
    }

    static private class CreditTypes
    {
        final CreditType RollerCoaster = CreditType.create("CoasterCredit");

        final List<CreditType> AllCreditTypes = new LinkedList<>();

        private CreditTypes()
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

        private Categories()
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
        final Manufacturer Huss = Manufacturer.create("Huss");
        final Manufacturer Pinfari = Manufacturer.create("Pinfari");
        final Manufacturer MaurerRides = Manufacturer.create("Mauerer");
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
        final Manufacturer Gerstlauer = Manufacturer.create("Gerstlauer");
        final Manufacturer PremierRides = Manufacturer.create("Premier Rides");
        final Manufacturer GCI = Manufacturer.create("Great Coasters International");
        final Manufacturer ChanceRides = Manufacturer.create("Chance Rides");
        final Manufacturer ArtEngineering = Manufacturer.create("Art Engineering");
        final Manufacturer KumbaK = Manufacturer.create("KumbaK");
        final Manufacturer RollerCoasterCorporationOfAmerica = Manufacturer.create("Roller Coaster Corporation of America");

        final List<Manufacturer> AllManufacturers = new LinkedList<>();

        private Manufacturers()
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
            AllManufacturers.add(ArtEngineering);
            AllManufacturers.add(KumbaK);
            AllManufacturers.add(RollerCoasterCorporationOfAmerica);
        }
    }

    static private class Models
    {
        CreditTypes creditTypes;
        Categories categories;

        // MANUFACTURERS
        final Model BollingerAndMabillard_HyperCoaster = Model.create("Hyper Coaster");
        final Model BollingerAndMabillard_InvertedCoaster = Model.create("Inverted Coaster");
        final Model BollingerAndMabillard_DiveCoaster = Model.create("Dive Coaster");
        final Model BollingerAndMabillard_WingCoaster = Model.create("Wing Coaster");
        final Model BollingerAndMabillard_SittingCoaster = Model.create("Sitting Coaster");

        final Model Intamin_MegaCoaster = Model.create("Mega Coaster");
        final Model Intamin_BlitzCoaster = Model.create("Blitz Coaster");
        final Model Intamin_AcceleratorCoaster = Model.create("Accelerator Coaster");
        final Model Intamin_Prefab_Woodie = Model.create("Prefabricated Wooden Coaster");
        final Model Intamin_WaterCoaster = Model.create("Water Coaster");
        final Model Intamin_SwissBob = Model.create("Swiss Bob");
        final Model Intamin_WingRiderCoaster = Model.create("Wing Rider Coaster");

        final Model Gerstlauer_InfinityCoaster = Model.create("Infinity Coaster");
        final Model Gerstlauer_EuroFighter = Model.create("Euro-Fighter");
        final Model Gerstlauer_FamilyCoaster = Model.create("Family Coaster");
        final Model Gerstlauer_BobsledCoaster = Model.create("Bobsled Coaster");
        final Model Gerstlauer_SkyFly = Model.create("Sky Fly");

        final Model Mack_LaunchedCoaster = Model.create("Launched Coaster");
        final Model Mack_PoweredCoaster = Model.create("Powered Coaster");
        final Model Mack_InvertedPoweredCoaster = Model.create("Inverted Powered Coaster");
        final Model Mack_BigDipper = Model.create("Big Dipper");
        final Model Mack_SuperSplash = Model.create("SuperSplash");
        final Model Mack_WaterCoaster = Model.create("Water Coaster");
        final Model Mack_SpinningCoaster = Model.create("Spinning Coaster");
        final Model Mack_Bobsled = Model.create("Bobsled");
        final Model Mack_WildeMaus = Model.create("Wilde Maus");
        final Model Mack_YoungstarCoaster = Model.create("Youngstar Coaster");
        final Model Mack_MadHouse = Model.create("Mad House");

        final Model Vekoma_SuspendedLoopingCoaster = Model.create("Suspended Looping Coaster");
        final Model Vekoma_Boomerang = Model.create("Boomerang");
        final Model Vekoma_FamilyBoomerang = Model.create("Family Boomerang");
        final Model Vekoma_FlyingCoaster = Model.create("Flying Coaster");
        final Model Vekoma_MotorbikeCoaster = Model.create("Motorbike Coaster");
        final Model Vekoma_MineTrain = Model.create("Mine Train");
        final Model Vekoma_SuspendedFamilyCoaster = Model.create("Suspended Family Coaster");
        final Model Vekoma_JuniorCoaster = Model.create("Junior Coaster");
        final Model Vekoma_SpaceWarp = Model.create("Space Warp");
        final Model Vekoma_MK900 = Model.create("MK-900");
        final Model Vekoma_MK1200 = Model.create("MK-1200");
        final Model Vekoma_LsmCoaster = Model.create("LSM Coaster");
        final Model Vekoma_MadHouse = Model.create("Mad House");

        final Model RMC_IBoxTrack = Model.create("IBox Track");

        final Model Schwarzkopf_Wildcat = Model.create("Wildcat");

        final Model Maurer_WildeMausClassic = Model.create("Wilde Maus Classic");
        final Model Maurer_SpinningCoaster = Model.create("Spinning Coaster");

        final Model SbfVisa_DragoonCoaster = Model.create("Dragoon Coaster");
        final Model SbfVisa_CompactSpinningCoaster = Model.create("Compact Spinning Coaster");
        final Model SbfVisa_SpinningCoaster = Model.create("Spinning Coaster");
        final Model SbfVisa_FamilyCoasterBigApple = Model.create("Family Coaster (Big Apple)");
        final Model SbfVisa_RaceCoaster = Model.create("Race Coaster");

        final Model Pinfari_BigApple = Model.create("Big Apple");

        final Model PrestonAndBarbieri_FamilyCoaster = Model.create("Family Coaster");
        final Model PrestonAndBarbieri_MiniCoaster = Model.create("Mini Coaster");

        final Model Art_ChildrensRollerCoaster = Model.create("Children's Roller Coaster");

        final Model Premier_SkyRocketII = Model.create("Sky Rocket II");

        final Model Zierer_ForceOne = Model.create("Force One");
        final Model Zierer_ForceTwo = Model.create("Force Two");
        final Model Zierer_Tivoli_Medium = Model.create("Tivoli (medium)");
        final Model Zierer_Tivoli_Large = Model.create("Tivoli (large)");
        final Model Zierer_DoubleFamilyTower = Model.create("Double Family Tower");
        final Model Zierer_WildWaterRondell = Model.create("Wild Water Rondell");

        final Model Arrow_MineTrain = Model.create("Mine Train");

        final Model Zamperla_DiskO = Model.create("Disk'O");


        final Model Huss_TopSpin = Model.create("Top Spin");
        final Model Huss_SuspendedTopSpin = Model.create("Suspended Top Spin");
        final Model Huss_Magic = Model.create("Magic");
        final Model Huss_BreakDance = Model.create("Break Dance");


        // ROLLER COASTERS
        final Model WoodenCoaster = Model.create("Wooden Coaster");
        final Model JuniorWoodenCoaster = Model.create("Junior Wooden Coaster");
        final Model SteelCoaster = Model.create("Steel Coaster");
        final Model WildMouse = Model.create("Wild Mouse");
        final Model InvertedCoaster = Model.create("Inverted Coaster");


        // MIXED
        final Model FlatRide = Model.create("Flat Ride");
        final Model BoatRide = Model.create("Boat Ride");
        final Model Simulator = Model.create("Simulator");

        // WATER RIDES
        final Model LogFlume = Model.create("Log Flume");
        final Model RiverRapids = Model.create("River Rapids");
        final Model ShootTheChute = Model.create("Shoot the Chute");

        // FAMILY RIDES
        final Model FunHouse = Model.create("Fun House");
        final Model ChairSwing = Model.create("Chair Swing");
        final Model Carousel = Model.create("Carousel");
        final Model PirateShip = Model.create("Pirate Ship");
        final Model MiniDropTower = Model.create("Mini Drop Tower");
        final Model FerrisWheel = Model.create("Ferris Wheel");
        final Model Teacups = Model.create("Teacups");
        final Model AntiqueCars = Model.create("Antique Cars");
        final Model ObservationTower = Model.create("Observation Tower");
        final Model FlyingTheatre = Model.create("Flying Theatre");
        final Model WalkThroughAttraction = Model.create("Walk Through Attraction");

        // THRILL RIDES
        final Model DropTower = Model.create("Drop Tower");
        final Model GyroDropTower = Model.create("Gyro Drop Tower");
        final Model EnclosedDropTower = Model.create("Enclosed Drop Tower");
        final Model ShotTower = Model.create("Shot Tower");
        final Model Enterprise = Model.create("Enterprise");
        final Model Frisbee = Model.create("Frisbee");

        // DARK RIDES
        final Model InteractiveDarkRide = Model.create("Interactive Dark Ride");
        final Model TracklessDarkRide = Model.create("Trackless Dark Ride");

        // TRANSPORT RIDES
        final Model Monorail = Model.create("Monorail");
        final Model TrainRide = Model.create("Train Ride");
        final Model ChairLift = Model.create("Chair Lift");


        final List<Model> AllModels = new LinkedList<>();

        private Models(CreditTypes creditTypes, Categories categories, Manufacturers manufacturers)
        {
            this.creditTypes = creditTypes;
            this.categories = categories;

            AllModels.add(Model.getDefault());


            // ROLLER COASTERS
            WoodenCoaster.setCreditType(creditTypes.RollerCoaster);
            WoodenCoaster.setCategory(categories.RollerCoasters);
            AllModels.add(WoodenCoaster);

            JuniorWoodenCoaster.setCreditType(creditTypes.RollerCoaster);
            JuniorWoodenCoaster.setCategory(categories.RollerCoasters);
            AllModels.add(JuniorWoodenCoaster);

            SteelCoaster.setCreditType(creditTypes.RollerCoaster);
            SteelCoaster.setCategory(categories.RollerCoasters);
            AllModels.add(SteelCoaster);

            WildMouse.setCreditType(creditTypes.RollerCoaster);
            WildMouse.setCategory(categories.RollerCoasters);
            AllModels.add(WildMouse);

            InvertedCoaster.setCreditType(creditTypes.RollerCoaster);
            InvertedCoaster.setCategory(categories.RollerCoasters);
            AllModels.add(InvertedCoaster);


            // MIXED
            AllModels.add(FlatRide);
            AllModels.add(BoatRide);
            AllModels.add(Simulator);

            // WATER RIDES
            LogFlume.setCategory(categories.WaterRides);
            AllModels.add(LogFlume);
            RiverRapids.setCategory(categories.WaterRides);
            AllModels.add(RiverRapids);
            ShootTheChute.setCategory(categories.WaterRides);
            AllModels.add(ShootTheChute);


            // FAMILY RIDES
            FunHouse.setCategory(categories.FamilyRides);
            AllModels.add(FunHouse);
            ChairSwing.setCategory(categories.FamilyRides);
            AllModels.add(ChairSwing);
            Carousel.setCategory(categories.FamilyRides);
            AllModels.add(Carousel);
            PirateShip.setCategory(categories.FamilyRides);
            AllModels.add(PirateShip);
            MiniDropTower.setCategory(categories.FamilyRides);
            AllModels.add(MiniDropTower);
            FerrisWheel.setCategory(categories.FamilyRides);
            AllModels.add(FerrisWheel);
            Teacups.setCategory(categories.FamilyRides);
            AllModels.add(Teacups);
            AntiqueCars.setCategory(categories.FamilyRides);
            AllModels.add(AntiqueCars);
            ObservationTower.setCategory(categories.FamilyRides);
            AllModels.add(ObservationTower);
            FlyingTheatre.setCategory(categories.FamilyRides);
            AllModels.add(FlyingTheatre);
            WalkThroughAttraction.setCategory(categories.FamilyRides);
            AllModels.add(WalkThroughAttraction);


            // THRILL RIDES
            DropTower.setCategory(categories.ThrillRides);
            AllModels.add(DropTower);
            GyroDropTower.setCategory(categories.ThrillRides);
            AllModels.add(GyroDropTower);
            EnclosedDropTower.setCategory(categories.ThrillRides);
            AllModels.add(EnclosedDropTower);
            ShotTower.setCategory(categories.ThrillRides);
            AllModels.add(ShotTower);
            Enterprise.setCategory(categories.ThrillRides);
            AllModels.add(Enterprise);
            Frisbee.setCategory(categories.ThrillRides);
            AllModels.add(Frisbee);

            // DARK RIDES
            InteractiveDarkRide.setCategory(categories.DarkRides);
            AllModels.add(InteractiveDarkRide);
            TracklessDarkRide.setCategory(categories.DarkRides);
            AllModels.add(TracklessDarkRide);

            // TRANSPORT RIDES
            Monorail.setCategory(categories.TransportRides);
            AllModels.add(Monorail);
            TrainRide.setCategory(categories.TransportRides);
            AllModels.add(TrainRide);
            ChairLift.setCategory(categories.TransportRides);
            AllModels.add(ChairLift);


            // MANUFACTURERS
            this.decorateRollerCoasterAndAddToList(manufacturers.BolligerAndMabillard, BollingerAndMabillard_HyperCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.BolligerAndMabillard, BollingerAndMabillard_InvertedCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.BolligerAndMabillard, BollingerAndMabillard_DiveCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.BolligerAndMabillard, BollingerAndMabillard_WingCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.BolligerAndMabillard, BollingerAndMabillard_SittingCoaster);


            this.decorateRollerCoasterAndAddToList(manufacturers.Intamin, Intamin_MegaCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Intamin, Intamin_BlitzCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Intamin, Intamin_AcceleratorCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Intamin, Intamin_Prefab_Woodie);
            this.decorateRollerCoasterAndAddToList(manufacturers.Intamin, Intamin_WaterCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Intamin, Intamin_SwissBob);
            this.decorateRollerCoasterAndAddToList(manufacturers.Intamin, Intamin_WingRiderCoaster);


            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_LaunchedCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_PoweredCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_InvertedPoweredCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_BigDipper);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_SuperSplash);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_WaterCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_SpinningCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_Bobsled);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_WildeMaus);
            this.decorateRollerCoasterAndAddToList(manufacturers.Mack, Mack_YoungstarCoaster);

            Mack_MadHouse.setCategory(categories.FamilyRides);
            Mack_MadHouse.setManufacturer(manufacturers.Mack);
            AllModels.add(Mack_MadHouse);


            this.decorateRollerCoasterAndAddToList(manufacturers.Gerstlauer, Gerstlauer_InfinityCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Gerstlauer, Gerstlauer_EuroFighter);
            this.decorateRollerCoasterAndAddToList(manufacturers.Gerstlauer, Gerstlauer_FamilyCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Gerstlauer, Gerstlauer_BobsledCoaster);

            Gerstlauer_SkyFly.setCategory(categories.FamilyRides);
            Gerstlauer_SkyFly.setManufacturer(manufacturers.Gerstlauer);
            AllModels.add(Gerstlauer_SkyFly);


            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_SuspendedLoopingCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_Boomerang);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_FamilyBoomerang);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_FlyingCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_MotorbikeCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_MineTrain);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_SuspendedFamilyCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_JuniorCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_SpaceWarp);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_MK900);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_MK1200);
            this.decorateRollerCoasterAndAddToList(manufacturers.Vekoma, Vekoma_LsmCoaster);

            Vekoma_MadHouse.setCategory(categories.FamilyRides);
            Vekoma_MadHouse.setManufacturer(manufacturers.Vekoma);
            AllModels.add(Vekoma_MadHouse);


            this.decorateRollerCoasterAndAddToList(manufacturers.RMC, RMC_IBoxTrack);


            this.decorateRollerCoasterAndAddToList(manufacturers.Schwarzkopf, Schwarzkopf_Wildcat);


            this.decorateRollerCoasterAndAddToList(manufacturers.MaurerRides, Maurer_WildeMausClassic);
            this.decorateRollerCoasterAndAddToList(manufacturers.MaurerRides, Maurer_SpinningCoaster);


            this.decorateRollerCoasterAndAddToList(manufacturers.SbfVisa, SbfVisa_DragoonCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.SbfVisa, SbfVisa_CompactSpinningCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.SbfVisa, SbfVisa_SpinningCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.SbfVisa, SbfVisa_FamilyCoasterBigApple);
            this.decorateRollerCoasterAndAddToList(manufacturers.SbfVisa, SbfVisa_RaceCoaster);


            this.decorateRollerCoasterAndAddToList(manufacturers.Pinfari, Pinfari_BigApple);


            this.decorateRollerCoasterAndAddToList(manufacturers.PrestonAndBarbieri, PrestonAndBarbieri_FamilyCoaster);
            this.decorateRollerCoasterAndAddToList(manufacturers.PrestonAndBarbieri, PrestonAndBarbieri_MiniCoaster);


            this.decorateRollerCoasterAndAddToList(manufacturers.ArtEngineering, Art_ChildrensRollerCoaster);


            this.decorateRollerCoasterAndAddToList(manufacturers.PremierRides, Premier_SkyRocketII);


            this.decorateRollerCoasterAndAddToList(manufacturers.ArrowDynamics, Arrow_MineTrain);


            this.decorateRollerCoasterAndAddToList(manufacturers.Zierer, Zierer_ForceOne);
            this.decorateRollerCoasterAndAddToList(manufacturers.Zierer, Zierer_ForceTwo);
            this.decorateRollerCoasterAndAddToList(manufacturers.Zierer, Zierer_Tivoli_Medium);
            this.decorateRollerCoasterAndAddToList(manufacturers.Zierer, Zierer_Tivoli_Large);

            Zierer_DoubleFamilyTower.setCategory(categories.FamilyRides);
            Zierer_DoubleFamilyTower.setManufacturer(manufacturers.Zierer);
            AllModels.add(Zierer_DoubleFamilyTower);

            Zierer_WildWaterRondell.setCategory(categories.FamilyRides);
            Zierer_WildWaterRondell.setManufacturer(manufacturers.Zierer);
            AllModels.add(Zierer_WildWaterRondell);


            Zamperla_DiskO.setCategory(categories.FamilyRides);
            Zamperla_DiskO.setManufacturer(manufacturers.Zamperla);
            AllModels.add(Zamperla_DiskO);


            Huss_TopSpin.setCategory(categories.ThrillRides);
            Huss_TopSpin.setManufacturer(manufacturers.Huss);
            AllModels.add(Huss_TopSpin);

            Huss_SuspendedTopSpin.setCategory(categories.ThrillRides);
            Huss_SuspendedTopSpin.setManufacturer(manufacturers.Huss);
            AllModels.add(Huss_SuspendedTopSpin);

            Huss_Magic.setCategory(categories.ThrillRides);
            Huss_Magic.setManufacturer(manufacturers.Huss);
            AllModels.add(Huss_Magic);

            Huss_BreakDance.setCategory(categories.ThrillRides);
            Huss_BreakDance.setManufacturer(manufacturers.Huss);
            AllModels.add(Huss_BreakDance);
        }

        private void decorateRollerCoasterAndAddToList(Manufacturer manufacturer, Model model)
        {
            model.setCreditType(this.creditTypes.RollerCoaster);
            model.setCategory(categories.RollerCoasters);
            model.setManufacturer(manufacturer);

            AllModels.add(model);
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

        private Statuses()
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

        private Locations()
        {
            Germany.addChildAndSetParent(Bremen);

            Europe.addChildAndSetParent(Germany);
            Europe.addChildAndSetParent(Netherlands);
            Europe.addChildAndSetParent(Spain);
            Europe.addChildAndSetParent(Poland);
        }
    }
}