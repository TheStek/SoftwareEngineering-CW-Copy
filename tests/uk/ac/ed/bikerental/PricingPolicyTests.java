package uk.ac.ed.bikerental;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class PricingPolicyTests {
    // You can add attributes here
    private ArrayList<DurationDiscount> sampleDiscounts;
    private BikeType sampleType;
    private Collection<Bike> bikes;
    private HashMap<BikeType, BigDecimal> samplePrices;
    private DateRange sampleDates;
    private DiscountedPricing pricing;


    @BeforeEach
    void setUp() throws Exception {
        this.sampleDiscounts = new ArrayList<DurationDiscount>();
        sampleDiscounts.add(new DurationDiscount(1, 2, new BigDecimal(0)));
        sampleDiscounts.add(new DurationDiscount(3, 6, new BigDecimal(5)));
        sampleDiscounts.add(new DurationDiscount(7, 13, new BigDecimal(10)));
        sampleDiscounts.add(new DurationDiscount(14, -1, new BigDecimal(15)));

        this.sampleType = new BikeType("Test", new BigDecimal(5));
        this.bikes = new ArrayList<Bike>();

        this.samplePrices = new HashMap<>();
        samplePrices.put(sampleType, new BigDecimal(10));

        for (int i=0; i<5; i++){
            bikes.add(new Bike(sampleType));
        }

        this.sampleDates = new DateRange(LocalDate.now(), LocalDate.now().plusDays(5));

        this.pricing = new DiscountedPricing();

        // Add the discounts in reverse order
        pricing.addDiscount(new DurationDiscount(14, -1, new BigDecimal(15)));
        pricing.addDiscount(new DurationDiscount(7, 13, new BigDecimal(10)));
        pricing.addDiscount(new DurationDiscount(3, 6, new BigDecimal(5)));
        pricing.addDiscount(new DurationDiscount(1, 2, new BigDecimal(0)));

        pricing.addBikeTypeToPrices(sampleType, new BigDecimal(10));
    }


    /*
    Here we check that the discounts added in the setup are in the correct order
    (increasing discount rate)
     */
    @Test
    void addDiscountTest(){
        ArrayList<DurationDiscount> generatedDiscounts = pricing.getDurationDiscounts();

        for (int i=0; i<sampleDiscounts.size(); i++){
            DurationDiscount expected = sampleDiscounts.get(i);
            DurationDiscount actual = generatedDiscounts.get(i);
            Assertions.assertEquals(expected.min, actual.min);
            Assertions.assertEquals(expected.max, actual.max);
            Assertions.assertEquals(expected.discount, actual.discount);
        }
    }


    @Test
    void dailyPricesAddedTest(){
        Assertions.assertEquals(samplePrices, pricing.getDailyPrices());
    }

    /*
    We calculated what the price should be by hand and check that the method gives the correct result
     */
    @Test
    void calculatePriceTest(){

        BigDecimal expectedPrice = new BigDecimal(237.5);
        BigDecimal actualPrice = pricing.calculatePrice(bikes, sampleDates);

        Assertions.assertEquals(expectedPrice.stripTrailingZeros(), actualPrice.stripTrailingZeros());
    }

}