package de.juliusawen.coastercreditcounter.backend.persistency;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import de.juliusawen.coastercreditcounter.backend.application.App;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.CoasterBlueprint;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.CustomAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.CustomCoaster;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IBlueprint;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.IOnSiteAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.attractions.StockAttraction;
import de.juliusawen.coastercreditcounter.backend.objects.elements.IElement;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Location;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Park;
import de.juliusawen.coastercreditcounter.backend.objects.elements.Visit;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.AttractionCategory;
import de.juliusawen.coastercreditcounter.backend.objects.orphanElements.Manufacturer;
import de.juliusawen.coastercreditcounter.backend.objects.temporaryElements.VisitedAttraction;
import de.juliusawen.coastercreditcounter.globals.Constants;
import de.juliusawen.coastercreditcounter.globals.Content;
import de.juliusawen.coastercreditcounter.toolbox.ConvertTool;
import de.juliusawen.coastercreditcounter.toolbox.Stopwatch;

public final class DatabaseMock implements IDatabaseWrapper
{
    private static final DatabaseMock instance = new DatabaseMock();

    public static DatabaseMock getInstance()
    {
        return instance;
    }

    private DatabaseMock() {}

    @Override
    public boolean loadContent(Content content)
    {
        Stopwatch stopwatch = new Stopwatch(true);

        AttractionCategory attractionCategoryRollerCoasters = AttractionCategory.create("RollerCoasters", null);
        AttractionCategory attractionCategoryThrillRides = AttractionCategory.create("Thrill Rides", null);
        AttractionCategory attractionCategoryFamilyRides = AttractionCategory.create("Family Rides", null);
        AttractionCategory attractionCategoryWaterRides = AttractionCategory.create("Water Rides", null);
        AttractionCategory attractionCategoryNonRollerCoasters = AttractionCategory.create("Non-Roller Coasters", null);

        List<AttractionCategory> attractionCategories = new ArrayList<>();
        attractionCategories.add(attractionCategoryRollerCoasters);
        attractionCategories.add(attractionCategoryThrillRides);
        attractionCategories.add(attractionCategoryFamilyRides);
        attractionCategories.add(attractionCategoryWaterRides);
        attractionCategories.add(attractionCategoryNonRollerCoasters);

        Manufacturer bolligerAndMabillard = Manufacturer.create("Bolliger & Mabillard Ingénieurs Conseils SA", null);
        Manufacturer intamin = Manufacturer.create("Intamin Amusement Rides", null);
        Manufacturer vekoma = Manufacturer.create("Vekoma Rides Manufacturing B.V.", null);
        Manufacturer huss = Manufacturer.create("Huss Park Attractions GmbH", null);

        List<Manufacturer> manufacturers = new ArrayList<>();
        manufacturers.add(bolligerAndMabillard);
        manufacturers.add(intamin);
        manufacturers.add(vekoma);
        manufacturers.add(huss);


        // create Nodes
        Location earth = Location.create("Earth", null);

        Location europe = Location.create("Europe", null);
        Location usa = Location.create("USA", null);

        Location germany = Location.create("Germany", null);
        Location netherlands = Location.create("Netherlands", null);

        Location northRhineWestphalia = Location.create("North Rhine-Westphalia", null);
        Location lowerSaxony = Location.create("Lower Saxony", null);
        Location badenWuerttemberg = Location.create("Baden-Württemberg", null);
        Location bavaria = Location.create("Bavaria", null);
        Location berlin = Location.create("Berlin", null);
        Location brandenburg = Location.create("Brandenburg", null);
        Location hamburg = Location.create("Hamburg", null);
        Location hesse = Location.create("Hesse", null);
        Location mecklenburgVorpommern = Location.create("Mecklenburg-Vorpommern", null);
        Location rhinelandPalatinate = Location.create("Rhineland-Palatinate", null);
        Location saarland = Location.create("Saarland", null);
        Location saxony = Location.create("Saxony", null);
        Location saxonyAnhalt = Location.create("Saxony-Anhalt", null);
        Location schleswigHolstein = Location.create("Schleswig-Holstein", null);
        Location thuringia = Location.create("Thuringia", null);
        Location bremen = Location.create("Bremen", null);

        Location bruehl = Location.create("Brühl", null);
        Location soltau = Location.create("Soltau", null);


        Location biddinghuizen = Location.create("Biddinghuizen", null);

        Park phantasialand = Park.create("Phantasialand", null);
        Park heidePark = Park.create("Heide Park Resort", null);
        Park freimarkt = Park.create("Freimarkt", null);
        Park osterwiese = Park.create("Osterwiese", null);

        Park cedarPoint = Park.create("Cedar Point", null);
        Park sixFlagsMagicMountain = Park.create("Six Flags Magic Mountain", null);

        Park walibiHolland = Park.create("Walibi Holland", null);




        //Create Blueprints
        List<IBlueprint> blueprints = new ArrayList<>();


        CoasterBlueprint suspendedLoopingCoaster = CoasterBlueprint.create("Suspended Looping Coaster", 4, null);
        suspendedLoopingCoaster.setManufacturer(vekoma);
        suspendedLoopingCoaster.setAttractionCategory(attractionCategoryRollerCoasters);
        blueprints.add(suspendedLoopingCoaster);

        CoasterBlueprint boomerang = CoasterBlueprint.create("Boomerang", 2, null);
        boomerang.setManufacturer(vekoma);
        boomerang.setAttractionCategory(attractionCategoryRollerCoasters);
        blueprints.add(boomerang);







        //Create Attractions

        CustomCoaster taron = CustomCoaster.create("Taron", 38, null);
        CustomCoaster blackMamba = CustomCoaster.create("Black Mamba", 18, null);
        CustomCoaster coloradoAdventure = CustomCoaster.create("Colorado Adventure", 11, null);
        CustomCoaster raik = CustomCoaster.create("Raik", 5, null);
        CustomCoaster templeOfTheNightHawk = CustomCoaster.create("Temple of the Night Hawk", 9, null);
        CustomCoaster winjasFear = CustomCoaster.create("Winja's Fear", 8, null);
        CustomCoaster winjasForce = CustomCoaster.create("Winja's Force", 8, null);

        CustomAttraction mysteryCastle = CustomAttraction.create("Mystery Castle", 0, null);
        CustomAttraction hollywoodTour = CustomAttraction.create("Hollywood Tour", 0, null);
        CustomAttraction chiapas = CustomAttraction.create("Chiapas", 10, null);
        CustomAttraction talocan = CustomAttraction.create("Talocan", 0, null);
        CustomAttraction fengJuPalace = CustomAttraction.create("Feng Ju Palace", 0, null);
        CustomAttraction geisterRiksha = CustomAttraction.create("Geister Rikscha", 0, null);
        CustomAttraction mausAuChocolat = CustomAttraction.create("Maus-Au-Chocolat", 1, null);
        CustomAttraction wellenflug = CustomAttraction.create("Wellenflug", 0, null);
        CustomAttraction tikal = CustomAttraction.create("Tikal", 1, null);
        CustomAttraction verruecktesHotelTartueff = CustomAttraction.create("Verrücktes Hotel Tartüff", 0, null);
        CustomAttraction riverQuest = CustomAttraction.create("River Quest", 0, null);
        CustomAttraction pferdekarusell = CustomAttraction.create("Pferdekarusell", 0, null);
        CustomAttraction wuermlingExpress = CustomAttraction.create("Würmling Express", 0, null);


        taron.setAttractionCategory(attractionCategoryRollerCoasters);
        taron.setManufacturer(intamin);

        blackMamba.setAttractionCategory(attractionCategoryRollerCoasters);
        blackMamba.setManufacturer(bolligerAndMabillard);

        coloradoAdventure.setAttractionCategory(attractionCategoryRollerCoasters);
        coloradoAdventure.setManufacturer(vekoma);

        raik.setAttractionCategory(attractionCategoryRollerCoasters);
        raik.setManufacturer(vekoma);

        templeOfTheNightHawk.setAttractionCategory(attractionCategoryRollerCoasters);
        templeOfTheNightHawk.setManufacturer(vekoma);

        winjasFear.setAttractionCategory(attractionCategoryRollerCoasters);
        winjasForce.setAttractionCategory(attractionCategoryRollerCoasters);

        hollywoodTour.setAttractionCategory(attractionCategoryWaterRides);
        chiapas.setAttractionCategory(attractionCategoryWaterRides);
        riverQuest.setAttractionCategory(attractionCategoryWaterRides);

        mysteryCastle.setAttractionCategory(attractionCategoryThrillRides);
        mysteryCastle.setManufacturer(intamin);

        talocan.setAttractionCategory(attractionCategoryThrillRides);
        talocan.setManufacturer(huss);

        fengJuPalace.setAttractionCategory(attractionCategoryFamilyRides);
        fengJuPalace.setManufacturer(vekoma);

        geisterRiksha.setAttractionCategory(attractionCategoryFamilyRides);
        mausAuChocolat.setAttractionCategory(attractionCategoryFamilyRides);
        wellenflug.setAttractionCategory(attractionCategoryFamilyRides);
        tikal.setAttractionCategory(attractionCategoryFamilyRides);
        verruecktesHotelTartueff.setAttractionCategory(attractionCategoryFamilyRides);
        pferdekarusell.setAttractionCategory(attractionCategoryFamilyRides);
        wuermlingExpress.setAttractionCategory(attractionCategoryFamilyRides);




        CustomCoaster krake = CustomCoaster.create("Krake", 22, null);
        CustomCoaster flugDerDaemonen = CustomCoaster.create("Flug der Dämonen", 19, null);
        CustomCoaster desertRace = CustomCoaster.create("Desert Race", 18, null);
        CustomCoaster bigLoop = CustomCoaster.create("Big Loop", 3, null);

        StockAttraction limit = StockAttraction.create("Limit", suspendedLoopingCoaster, 2, null);


        CustomCoaster grottenblitz = CustomCoaster.create("Grottenblitz", 2, null);
        CustomCoaster indyBlitz = CustomCoaster.create("Indy-Blitz", 1, null);
        CustomCoaster bobbahn = CustomCoaster.create("Bobbahn", 2, null);
        CustomCoaster colossos = CustomCoaster.create("Colossos", 0, null);

        CustomAttraction scream = CustomAttraction.create("Scream", 0, null);
        CustomAttraction mountainRafting = CustomAttraction.create("Mountain Rafting", 0, null);
        CustomAttraction wildwasserbahn = CustomAttraction.create("Wildwasserbahn", 0, null);
        CustomAttraction ghostbusters5D = CustomAttraction.create("Ghostbusters 5D", 1, null);
        CustomAttraction monorail = CustomAttraction.create("Monorail", 0, null);
        CustomAttraction screamie = CustomAttraction.create("Screamie", 0, null);
        CustomAttraction bounty = CustomAttraction.create("Bounty", 0, null);

        krake.setAttractionCategory(attractionCategoryRollerCoasters);
        krake.setManufacturer(bolligerAndMabillard);

        flugDerDaemonen.setAttractionCategory(attractionCategoryRollerCoasters);
        flugDerDaemonen.setManufacturer(bolligerAndMabillard);

        desertRace.setAttractionCategory(attractionCategoryRollerCoasters);
        desertRace.setManufacturer(intamin);

        bigLoop.setAttractionCategory(attractionCategoryRollerCoasters);
        grottenblitz.setAttractionCategory(attractionCategoryRollerCoasters);
        indyBlitz.setAttractionCategory(attractionCategoryRollerCoasters);
        bobbahn.setAttractionCategory(attractionCategoryRollerCoasters);

        colossos.setAttractionCategory(attractionCategoryRollerCoasters);
        colossos.setManufacturer(intamin);

        scream.setAttractionCategory(attractionCategoryThrillRides);

        mountainRafting.setAttractionCategory(attractionCategoryWaterRides);
        wildwasserbahn.setAttractionCategory(attractionCategoryWaterRides);

        ghostbusters5D.setAttractionCategory(attractionCategoryFamilyRides);
        monorail.setAttractionCategory(attractionCategoryFamilyRides);
        screamie.setAttractionCategory(attractionCategoryFamilyRides);
        bounty.setAttractionCategory(attractionCategoryFamilyRides);


        CustomCoaster steelVengeance = CustomCoaster.create("Steel Vengeance", 0, null);
        CustomCoaster valravn = CustomCoaster.create("Valravn", 0, null);
        CustomCoaster maverick = CustomCoaster.create("Maverick", 0,null);
        CustomCoaster gatekeeper = CustomCoaster.create("Gatekeeper", 0, null);
        CustomAttraction dodgem = CustomAttraction.create("Dodgem", 0, null);

        steelVengeance.setAttractionCategory(attractionCategoryRollerCoasters);

        valravn.setAttractionCategory(attractionCategoryRollerCoasters);
        valravn.setManufacturer(bolligerAndMabillard);

        maverick.setAttractionCategory(attractionCategoryRollerCoasters);
        maverick.setManufacturer(intamin);

        gatekeeper.setAttractionCategory(attractionCategoryRollerCoasters);
        gatekeeper.setManufacturer(bolligerAndMabillard);

        dodgem.setAttractionCategory(attractionCategoryFamilyRides);






        StockAttraction elCondor = StockAttraction.create("El Condor", suspendedLoopingCoaster, 1, null);
        StockAttraction speedOfSound = StockAttraction.create("Speed of Sound", boomerang, 2, null);

        CustomCoaster drako = CustomCoaster.create("Drako", 2, null);
        CustomCoaster robinHood = CustomCoaster.create("Robin Hood", 2, null);
        CustomCoaster xpressPlatform13 = CustomCoaster.create("Xpress: Platform 13", 2, null);
        CustomCoaster goliath = CustomCoaster.create("Goliath", 7, null);
        CustomCoaster lostGravity = CustomCoaster.create("Lost Gravity", 7, null);

        drako.setAttractionCategory(attractionCategoryRollerCoasters);
        robinHood.setAttractionCategory(attractionCategoryRollerCoasters);
        xpressPlatform13.setAttractionCategory(attractionCategoryRollerCoasters);

        goliath.setAttractionCategory(attractionCategoryRollerCoasters);
        goliath.setManufacturer(intamin);

        lostGravity.setAttractionCategory(attractionCategoryRollerCoasters);

        CustomAttraction excalibur = CustomAttraction.create("Excalibur", 1, null);
        CustomAttraction gForce = CustomAttraction.create("G-Force", 0, null);
        CustomAttraction spaceShot = CustomAttraction.create("Space Shot", 0, null);
        CustomAttraction spinningVibe = CustomAttraction.create("Spinning Vibe", 0, null);
        CustomAttraction skydiver = CustomAttraction.create("Skydiver", 0, null);
        CustomAttraction theTomahawk = CustomAttraction.create("The Tomahawk", 0, null);

        excalibur.setAttractionCategory(attractionCategoryThrillRides);
        excalibur.setManufacturer(huss);

        gForce.setAttractionCategory(attractionCategoryThrillRides);
        spaceShot.setAttractionCategory(attractionCategoryThrillRides);
        spinningVibe.setAttractionCategory(attractionCategoryThrillRides);
        skydiver.setAttractionCategory(attractionCategoryThrillRides);
        theTomahawk.setAttractionCategory(attractionCategoryThrillRides);

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

        fibisBubbleSwirl.setAttractionCategory(attractionCategoryFamilyRides);
        haazGarage.setAttractionCategory(attractionCategoryFamilyRides);
        laGrandeRoue.setAttractionCategory(attractionCategoryFamilyRides);
        leTourDesJardins.setAttractionCategory(attractionCategoryFamilyRides);
        losSombreros.setAttractionCategory(attractionCategoryFamilyRides);

        merlinsMagicCastle.setAttractionCategory(attractionCategoryFamilyRides);
        merlinsMagicCastle.setManufacturer(vekoma);

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

        CustomAttraction crazyRiver = CustomAttraction.create("Crazy River", 2, null);
        CustomAttraction elRioGrande = CustomAttraction.create("El Rio Grande", 2, null);
        CustomAttraction splashBattle = CustomAttraction.create("SplashBattle", 0, null);

        crazyRiver.setAttractionCategory(attractionCategoryWaterRides);
        elRioGrande.setAttractionCategory(attractionCategoryWaterRides);
        splashBattle.setAttractionCategory(attractionCategoryWaterRides);

        // build tree
        phantasialand.addChildAndSetParent(taron);
        phantasialand.addChildAndSetParent(blackMamba);
        phantasialand.addChildAndSetParent(coloradoAdventure);
        phantasialand.addChildAndSetParent(raik);
        phantasialand.addChildAndSetParent(templeOfTheNightHawk);
        phantasialand.addChildAndSetParent(winjasFear);
        phantasialand.addChildAndSetParent(winjasForce);

        phantasialand.addChildAndSetParent(mysteryCastle);
        phantasialand.addChildAndSetParent(hollywoodTour);
        phantasialand.addChildAndSetParent(chiapas);
        phantasialand.addChildAndSetParent(talocan);
        phantasialand.addChildAndSetParent(fengJuPalace);
        phantasialand.addChildAndSetParent(geisterRiksha);
        phantasialand.addChildAndSetParent(mausAuChocolat);
        phantasialand.addChildAndSetParent(wellenflug);
        phantasialand.addChildAndSetParent(tikal);
        phantasialand.addChildAndSetParent(verruecktesHotelTartueff);
        phantasialand.addChildAndSetParent(riverQuest);
        phantasialand.addChildAndSetParent(pferdekarusell);
        phantasialand.addChildAndSetParent(wuermlingExpress);

        

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
        germany.addChildAndSetParent(badenWuerttemberg);
        germany.addChildAndSetParent(bavaria);
        germany.addChildAndSetParent(berlin);
        germany.addChildAndSetParent(brandenburg);
        germany.addChildAndSetParent(hamburg);
        germany.addChildAndSetParent(hesse);
        germany.addChildAndSetParent(mecklenburgVorpommern);
        germany.addChildAndSetParent(rhinelandPalatinate);
        germany.addChildAndSetParent(saarland);
        germany.addChildAndSetParent(saxony);
        germany.addChildAndSetParent(saxonyAnhalt);
        germany.addChildAndSetParent(schleswigHolstein);
        germany.addChildAndSetParent(thuringia);
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

        Visit visit0 = Visit.create(2018, 2, 30, null);
        this.addAttractionsToVisit(visit0, heidePark.getChildrenAsType(IOnSiteAttraction.class));
        heidePark.addChildAndSetParent(visit0);

        Visit visit1 = Visit.create(2018, 0, 1, null);
        Visit visit2 = Visit.create(2018, 1, 2, null);
        Visit visit3 = Visit.create(2018, 2, 3, null);
        Visit visit4 = Visit.create(2017, 3, 4, null);
        Visit visit5 = Visit.create(2017, 4, 5, null);
        Visit visit6 = Visit.create(2016, 5, 6, null);
        cedarPoint.addChildAndSetParent(visit6);
        cedarPoint.addChildAndSetParent(visit5);
        cedarPoint.addChildAndSetParent(visit4);
        cedarPoint.addChildAndSetParent(visit3);
        cedarPoint.addChildAndSetParent(visit2);
        cedarPoint.addChildAndSetParent(visit1);

        Visit visit7 = Visit.create(2019, 0, 1, null);
        this.addAttractionsToVisit(visit7, walibiHolland.getChildrenAsType(IOnSiteAttraction.class));
        walibiHolland.addChildAndSetParent(visit7);


        Visit visitPhantasialand181214 = Visit.create(2018, 11, 14, null);

        VisitedAttraction visitedTaron = VisitedAttraction.create(taron);
        visitedTaron.increaseRideCount(5);
        visitPhantasialand181214.addChildAndSetParent(visitedTaron);

        VisitedAttraction visitedBlackMamba = VisitedAttraction.create(blackMamba);
        visitedBlackMamba.increaseRideCount(2);
        visitPhantasialand181214.addChildAndSetParent(visitedBlackMamba);

        VisitedAttraction visitedWinjasFear = VisitedAttraction.create(winjasFear);
        visitedWinjasFear.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedWinjasFear);

        VisitedAttraction visitedWinjasForce = VisitedAttraction.create(winjasForce);
        visitedWinjasForce.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedWinjasForce);

        VisitedAttraction visitedTempleOfTheNightHawk = VisitedAttraction.create(templeOfTheNightHawk);
        visitedTempleOfTheNightHawk.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedTempleOfTheNightHawk);

        VisitedAttraction visitedColoradoAdventure = VisitedAttraction.create(coloradoAdventure);
        visitedColoradoAdventure.increaseRideCount(2);
        visitPhantasialand181214.addChildAndSetParent(visitedColoradoAdventure);

        VisitedAttraction visitedRaik = VisitedAttraction.create(raik);
        visitedRaik.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedRaik);

        VisitedAttraction visitedTalocan = VisitedAttraction.create(talocan);
        visitedTalocan.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedTalocan);

        VisitedAttraction visitedMysteryCastle = VisitedAttraction.create(mysteryCastle);
        visitedMysteryCastle.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedMysteryCastle);

        VisitedAttraction visitedWuermlingExpress = VisitedAttraction.create(wuermlingExpress);
        visitedWuermlingExpress.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedWuermlingExpress);

        VisitedAttraction visitedTikal = VisitedAttraction.create(tikal);
        visitedTikal.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedTikal);

        VisitedAttraction visitedVerruecktesHotelTartueff = VisitedAttraction.create(verruecktesHotelTartueff);
        visitedVerruecktesHotelTartueff.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedVerruecktesHotelTartueff);

        VisitedAttraction visitedWellenflug = VisitedAttraction.create(wellenflug);
        visitedWellenflug.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedWellenflug);

        VisitedAttraction visitedMausAuChocolat = VisitedAttraction.create(mausAuChocolat);
        visitedMausAuChocolat.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedMausAuChocolat);

        VisitedAttraction visitedGeisterRiksha = VisitedAttraction.create(geisterRiksha);
        visitedGeisterRiksha.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedGeisterRiksha);

        VisitedAttraction visitedFengJuPalace = VisitedAttraction.create(fengJuPalace);
        visitedFengJuPalace.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedFengJuPalace);

        VisitedAttraction visitedHollywoodTour = VisitedAttraction.create(hollywoodTour);
        visitedHollywoodTour.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedHollywoodTour);

        VisitedAttraction visitedChiapas = VisitedAttraction.create(chiapas);
        visitedChiapas.increaseRideCount(1);
        visitPhantasialand181214.addChildAndSetParent(visitedChiapas);

        phantasialand.addChildAndSetParent(visitPhantasialand181214);


        Visit visitToday = Visit.create(Calendar.getInstance(), null);
        freimarkt.addChildAndSetParent(visitToday);
        Visit.setOpenVisit(visitToday);

        content.addElement(germany); //adding one location is enough - content is searching for root from there
        this.flattenContentTree(App.content.getRootLocation());

        Manufacturer.createAndSetDefault();
        manufacturers.add(Manufacturer.getDefault());
        content.addElements(ConvertTool.convertElementsToType(manufacturers, IElement.class));

        AttractionCategory.createAndSetDefault();
        attractionCategories.add(AttractionCategory.getDefault());
        content.addElements(ConvertTool.convertElementsToType(attractionCategories, IElement.class));

        content.addElements(ConvertTool.convertElementsToType(blueprints, IElement.class));

        Log.i(Constants.LOG_TAG, String.format("DatabaseMock.loadContent:: creating mock data successful - took [%d]ms", stopwatch.stop()));

        return true;
    }

    private void addAttractionsToVisit(Visit visit, List<IOnSiteAttraction> attractions)
    {
        for(IOnSiteAttraction attraction : attractions)
        {
            visit.addChildAndSetParent(VisitedAttraction.create(attraction));
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
