package uk.ac.ed.bikerental;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SystemTests {
    private Customer customer;
    private BikeType bmx;
    private BikeType yellowBike;
    private MockDeliveryService deliveryService;
    private ArrayList<BikeProvider> providers;


    /*
    We only set up the BikeTypes, the delivery service and the customer here so that we have more control over the
    stocks of the providers for each test
     */
    @BeforeEach
    void setUp() throws Exception {
        DeliveryServiceFactory.setupMockDeliveryService();
        this.deliveryService = (MockDeliveryService) DeliveryServiceFactory.getDeliveryService();

        this.providers = new ArrayList<BikeProvider>();

        this.bmx = new BikeType("BMX", new BigDecimal(235));
        this.yellowBike = new BikeType("Yellow Bike", new BigDecimal(75));

        this.customer = new Customer("Juan", "Del Potro",
                new Location("KY12 0RJ", "Carneggie's Left Limb Crescent"));

    }



    // Generate Quotes Tests


    /*
    We generate a search that we know should give quotes back and check that the returned collection is not empty
     */
    @Test
    void quotesReturnedTest(){
        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));


        for (int i=0; i<20; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 15);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        Assertions.assertEquals(false, quotes.isEmpty());

    }

    /*
    We create 2 BikeProviders, 1 with SimplePricing and one with DiscountedPricing
    Then we generate a quote and check that the expected price is obtained
     */
    @Test
    void pricingForQuotesTest(){

        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.add(new BikeProvider("Puffin Pedals",
                new Location("KY12 2QY", "69 Puffin Rd."), new DiscountedPricing(),
                new SimpleValuation(), deliveryService));


        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(20));


        DiscountedPricing ppDP = (DiscountedPricing) providers.get(1).getPricing();

        ppDP.addBikeTypeToPrices(bmx, new BigDecimal(20));

        ppDP.addDiscount(new DurationDiscount(14, -1, new BigDecimal(15)));
        ppDP.addDiscount(new DurationDiscount(7, 13, new BigDecimal(10)));
        ppDP.addDiscount(new DurationDiscount(3, 6, new BigDecimal(5)));
        ppDP.addDiscount(new DurationDiscount(1, 2, new BigDecimal(0)));


        for (int i=0; i<10; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        for (int i=0; i<10; i++){
            providers.get(1).addBike(new Bike(bmx));
        }


        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 10);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);


        Assertions.assertEquals(0, quotes.get(0).getPrice().compareTo(new BigDecimal(1400)));
        Assertions.assertEquals(0, quotes.get(1).getPrice().compareTo(new BigDecimal(1260)));




    }


    /*
    We generate the providers and give them certain stocks of bikes
    We then initiate a search we know should only get a quote from one of the providers and check this is the case
     */
    @Test
    void quotesFoundCorrectly(){

        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.add(new BikeProvider("Puffin Pedals",
                new Location("KY12 2QY", "69 Puffin Rd."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.add(new BikeProvider("Wally's Wheelies",
                new Location("KY12 7EP", "5 Fergie Rd."), new SimplePricing(),
                new SimpleValuation(), deliveryService));



        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));
        providers.get(0).getPricing().setDailyRentalPrice(yellowBike, new BigDecimal(15));

        providers.get(1).getPricing().setDailyRentalPrice(bmx, new BigDecimal(50));
        providers.get(1).getPricing().setDailyRentalPrice(yellowBike, new BigDecimal(5));

        providers.get(2).getPricing().setDailyRentalPrice(bmx, new BigDecimal(75));
        providers.get(2).getPricing().setDailyRentalPrice(yellowBike, new BigDecimal(45));


        for (int i=0; i<2; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        for (int i=0; i<2; i++){
            providers.get(0).addBike(new Bike(yellowBike));
        }

        for (int i=0; i<10; i++){
            providers.get(1).addBike(new Bike(bmx));
        }

        for (int i=0; i<5; i++){
            providers.get(1).addBike(new Bike(yellowBike));
        }

        for (int i=0; i<30; i++){
            providers.get(2).addBike(new Bike(bmx));
        }

        for (int i=0; i<50; i++){
            providers.get(2).addBike(new Bike(yellowBike));
        }


        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 10);
        bikesToSearch.put(yellowBike, 15);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);



        Assertions.assertEquals(1, quotes.size());

        Assertions.assertEquals("Wally's Wheelies", quotes.get(0).getProvider().getName());
    }

    /*
    We generate the provider, give it bikes, and book all of the vikes for the next week
    We then check that we can make another booking for all the bikes after the first booking is over
     */
    @Test
    void quotesForLaterDateTest() {

        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));


        for (int i=0; i<15; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 15);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        customer.makeBooking(quotes.get(0), null, true);

        ArrayList<Quote> quotes2 = customer.searchQuotes(new Location("KY12 OLF", ""),
                bikesToSearch, new DateRange(LocalDate.now().plusDays(8), LocalDate.now().plusDays(14)), providers);

        Assertions.assertEquals(1, quotes.size());
    }



    /*
    We generate the provider and give them bikes, then make a booking for half of these bikes
    Then we search for a quote for the same dates looking for more than half of the number of bikes
    We check that no quotes are generated as there are not enough bikes in the shop for those dates to
    fulfill the order
     */
    @Test
    void bookedBikesNotQuoted(){

        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));

        for (int i=0; i<10; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 5);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        customer.makeBooking(quotes.get(0), null, true);

        bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 7);

        ArrayList<Quote> quotes2 = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        Assertions.assertEquals(0, quotes2.size());
    }

    /*
    We generate a provider and give it bikes
    Then we make a search that the provider could quote for but for a location that is not "near" to them
    We check that they return no quotes for this
     */
    @Test
    void quotesTakenFromCorrectLocationRangeTest(){

        providers.add(new BikeProvider("Tour d'Ecosse",
                new Location("HU14 7UP ", "130 Fife Av."), new SimplePricing(),
                new SimpleValuation(), deliveryService));


        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));



        for (int i=0; i<20; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 10);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        Assertions.assertEquals(0, quotes.size());

    }

    /*
    We generate a provider and give it only one type of bike
    We then check that if we search for a different bike type (one it doesn't have) it doesn't return any quotes
    Then we add some stock of that type and make the same search and check that a quote is returned
     */
    @Test
    void invalidBikeSearchReturnsNoQuotesTest(){
        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));
        providers.get(0).getPricing().setDailyRentalPrice(yellowBike, new BigDecimal(10));


        for (int i=0; i<10; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(yellowBike, 5);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        customer.displayQuotes(quotes);

        Assertions.assertEquals(0, quotes.size());

        for (int i=0; i<10; i++){
            providers.get(0).addBike(new Bike(yellowBike));
        }


        quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        Assertions.assertEquals(1, quotes.size());

    }


    // Book Quotes Tests


    /*
    This essentially generates 1000 empty bookings and checks that all bookingIds are unique
     */
    @Test
    void bookingIdsAreUnique(){
        ArrayList<String> ids = new ArrayList<>();
        for (int i =0; i<1000; i++){
            String id = new BookedQuote().getBookingID();
            Assertions.assertEquals(false, ids.contains(id));
            ids.add(id);
        }
    }


    /*
    We make a provider with bikes, generate a quote and then book it
    We check that a new booking is made and is in customer.bookings
    We then repeat this process but specify that the order should be delivered to the customer
    Then we search for another quote and check that none are generated as all bikes should be booked at this point
     */
    @Test
    void quoteBookedTest(){

        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));



        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));


        for (int i=0; i<20; i++){
            providers.get(0).addBike(new Bike(bmx));
        }


        DateRange datesToSearch =  new DateRange(LocalDate.now(), LocalDate.now().plusDays(7));

        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 15);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, datesToSearch, providers);

        int currentBookings = customer.getBookings().size();

        customer.makeBooking(quotes.get(0), null, true);

        Assertions.assertEquals(currentBookings+1, customer.getBookings().size());

        bikesToSearch.put(bmx, 5);

        quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, datesToSearch, providers);

        Assertions.assertEquals(1, quotes.size());

        customer.makeBooking(quotes.get(0), null, false);

        Assertions.assertEquals(currentBookings+2, customer.getBookings().size());

        quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, datesToSearch, providers);

        Assertions.assertEquals(0, quotes.size());


    }

    /*
    We generate a provider, give it bikes then find and book a quote specifying that it should be delivered to the
    customer
    We then check that all the bikes are in the pickup list of the delivery service for the start date of the order
    Then we tell the delivery service to carry out the pickups and check the status of all the bikes have been
    updated accordingly
    Then we tell teh delivery service to carry out the dropoffs and check the status of all the bikes have been updated
    accordingly
     */
    @Test
    void deliveryToCustomerScheduledTest(){
        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));


        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));

        for (int i=0; i<20; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 5);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        customer.makeBooking(quotes.get(0), null, false);

        BookedQuote booking = customer.getBookings().get(customer.getBookings().size()-1);
        Collection<Deliverable> pickups = deliveryService.getPickupsOn(LocalDate.now());

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(true, pickups.contains(b));
        }

        deliveryService.carryOutPickups(LocalDate.now());

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(true, b.getStatus().equals("Out for Delivery"));
        }

        deliveryService.carryOutDropoffs();

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(true, b.getStatus().equals("Delivered"));
        }
    }


    // Return Bikes Tests

    /*
    We generate the provider, give it bikes and book all of these bikes
    First we check that none of these bikes are free for the specified dates and we get no quotes when
    performing the same search
    Then we return the booking to the original provider
    We check that all the bikes are now free for the specified dates, the booking status is "Returned" and that
    performing the same search now gives us a quote
     */
    @Test
    void registerBikeReturnToProviderTest() {

        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));


        for (int i=0; i<20; i++){
            providers.get(0).addBike(new Bike(bmx));
        }


        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 20);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        customer.makeBooking(quotes.get(0), null, true);


        BookedQuote booking = customer.getBookings().get(customer.getBookings().size() - 1);

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(false, b.checkFree(booking.getDates()));
        }

        quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        Assertions.assertEquals(0, quotes.size());

        BikeProvider bprov = booking.getProvider();
        bprov.returnOrder(customer, booking.getBookingID());

        Assertions.assertEquals("Returned", booking.getStatus());

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(true, b.checkFree(booking.getDates()));
        }

        quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        Assertions.assertEquals(1, quotes.size());
    }





    /*
    We generate 2 BikeProviders, give 1 bikes and set them as partners
    We then search and make a booking specifying that the order will be returned to the partner provider
    Then we return the order to the partner location and check the delivery schedule in the same way as we did
    for delivering to the customer
     */
    @Test
    void bikeReturnToPartnerTest(){
        providers.add(new BikeProvider("EnCyclePedia",
                new Location("KY12 3BB", "24 Penguin St."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.add(new BikeProvider("Puffin Pedals",
                new Location("KY12 2QY", "69 Puffin Rd."), new SimplePricing(),
                new SimpleValuation(), deliveryService));

        providers.get(0).addPartner(providers.get(1));


        providers.get(0).getPricing().setDailyRentalPrice(bmx, new BigDecimal(30));

        for (int i=0; i<20; i++){
            providers.get(0).addBike(new Bike(bmx));
        }

        HashMap<BikeType, Integer> bikesToSearch = new HashMap<BikeType, Integer>();
        bikesToSearch.put(bmx, 5);

        ArrayList<Quote> quotes = customer.searchQuotes(new Location("KY12 5WE", ""),
                bikesToSearch, new DateRange(LocalDate.now(), LocalDate.now().plusDays(7)), providers);

        customer.makeBooking(quotes.get(0), providers.get(1), true);


        BookedQuote booking = customer.getBookings().get(customer.getBookings().size()-1);

        System.out.println(booking.isReturnedToPartner());
        System.out.println(booking.getPartnerToReturnTo());

        providers.get(1).returnOrder(customer, booking.getBookingID());

        Collection<Deliverable> pickups = deliveryService.getPickupsOn(LocalDate.now());

        System.out.println(pickups);

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(true, pickups.contains(b));
        }

        deliveryService.carryOutPickups(LocalDate.now());

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(true, b.getStatus().equals("Out for Delivery"));
        }

        deliveryService.carryOutDropoffs();

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(true, b.getStatus().equals("Delivered"));
        }

        for (Bike b: booking.getBikes()){
            Assertions.assertEquals(true, b.checkFree(booking.getDates()));
        }
    }




}
