package uk.ac.ed.bikerental;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;


class SimpleValuationTest {
    @Test
    void calculateValue() {
        BikeType type = new BikeType("Example Type", new BigDecimal(100));
        Bike b = new Bike(type);
        SimpleValuation policy = new SimpleValuation();
        Assertions.assertEquals(new BigDecimal(100), policy.calculateValue(b, LocalDate.now()));
    }
}