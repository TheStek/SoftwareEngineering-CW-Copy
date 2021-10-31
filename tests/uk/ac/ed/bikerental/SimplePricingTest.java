package uk.ac.ed.bikerental;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;


class SimplePricingTest {
    private SimplePricing policy;
    private BikeType type;

    @BeforeEach
    void setUp() {
        policy = new SimplePricing();
        type = new BikeType("Elon Musk's Cyber Truck", new BigDecimal(5));
    }

    /*
    We check that we can set the price of a given type and that this can be updated
     */
    @Test
    void setDailyRentalPriceTest() {
        policy.setDailyRentalPrice(type, new BigDecimal(10));
        Assertions.assertEquals(new BigDecimal(10), policy.getRates().get(type));

        policy.setDailyRentalPrice(type, new BigDecimal(15));
        Assertions.assertEquals(new BigDecimal(15), policy.getRates().get(type));
    }

    /*
    Here we generate a sample order with 2 bike types and calculate the total by hand
    We then check that the calculatePrice method gives the correct price
     */
    @Test
    void calculatePriceTest() {
        BikeType type2 = new BikeType("Road Racer", new BigDecimal(60));
        policy.setDailyRentalPrice(type, new BigDecimal(15));
        policy.setDailyRentalPrice(type2, new BigDecimal(20));
        ArrayList<Bike> bikesToPrice = new ArrayList<Bike>();
        for (int i=0; i<5; i++){
            bikesToPrice.add(new Bike(type));
        }
        for (int i=0; i<10; i++){
            bikesToPrice.add(new Bike(type2));
        }

        Assertions.assertEquals(new BigDecimal(1375), policy.calculatePrice(bikesToPrice,
                new DateRange(LocalDate.now(), LocalDate.now().plusDays(5))));
    }
}