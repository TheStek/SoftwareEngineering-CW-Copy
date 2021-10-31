package uk.ac.ed.bikerental;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDateRange {
    private DateRange dateRange1, dateRange2, dateRange3;

    @BeforeEach
    void setUp() throws Exception {
        // Setup resources before each test
        this.dateRange1 = new DateRange(LocalDate.of(2019, 1, 7),
                LocalDate.of(2019, 1, 10));
        this.dateRange2 = new DateRange(LocalDate.of(2019, 1, 5),
                LocalDate.of(2019, 1, 23));
        this.dateRange3 = new DateRange(LocalDate.of(2015, 1, 7),
                LocalDate.of(2018, 1, 10));
    }

    // Sample JUnit tests checking toYears works
    @Test
    void testToYears1() {
        Assertions.assertEquals(0, this.dateRange1.toYears());
    }

    @Test
    void testToYears3() {
        Assertions.assertEquals(3, this.dateRange3.toYears());
    }

    /*
    We generate 3 DateRanges that overlap with each other and check that they all return true
    We also check that they all overlap with themselves
     */
    @Test
    void testOverlapsTrue() {
        LocalDate start1 = LocalDate.now();
        LocalDate end1 = start1.plusDays(10);
        LocalDate start2 = start1.plusDays(3);
        LocalDate end2 = start1.plusDays(13);
        LocalDate start3 = start1.plusDays(4);
        LocalDate end3 = start1.plusDays(7);

        DateRange dr1 = new DateRange(start1, end1);
        DateRange dr2 = new DateRange(start2, end2);
        DateRange dr3 = new DateRange(start3, end3);

        Assertions.assertEquals(true, dr2.overlaps(dr1));
        Assertions.assertEquals(true, dr1.overlaps(dr2));

        Assertions.assertEquals(true, dr3.overlaps(dr1));
        Assertions.assertEquals(true, dr1.overlaps(dr3));

        Assertions.assertEquals(true, dr3.overlaps(dr2));
        Assertions.assertEquals(true, dr2.overlaps(dr3));

        Assertions.assertEquals(true, dr1.overlaps(dr1));
        Assertions.assertEquals(true, dr2.overlaps(dr2));
        Assertions.assertEquals(true, dr3.overlaps(dr3));
    }

    /*
    We generate 3 DateRanges in which some do not overlap and check that this is detected
     */
    @Test
    void testOverlapsFalse() {
        LocalDate start1 = LocalDate.now();
        LocalDate end1 = start1.plusDays(10);
        LocalDate start2 = start1.plusDays(13);
        LocalDate end2 = start1.plusDays(14);
        LocalDate start3 = start1.plusDays(14);
        LocalDate end3 = start1.plusDays(25);

        DateRange dr1 = new DateRange(start1, end1);
        DateRange dr2 = new DateRange(start2, end2);
        DateRange dr3 = new DateRange(start3, end3);

        Assertions.assertEquals(false, dr2.overlaps(dr1));
        Assertions.assertEquals(false, dr1.overlaps(dr2));

        Assertions.assertEquals(false, dr3.overlaps(dr1));
        Assertions.assertEquals(false, dr1.overlaps(dr3));

    }
}
