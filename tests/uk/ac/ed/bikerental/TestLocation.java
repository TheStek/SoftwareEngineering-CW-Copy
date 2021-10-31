package uk.ac.ed.bikerental;

import org.junit.jupiter.api.*;

class TestLocation {
    private Location testLoc1;
    private Location testLoc2;
    private Location testLoc3;


    @BeforeEach
    void setUp() throws Exception {
        testLoc1 = new Location("EH13 7EB", "Maximillian Ruffert Avenue");
        testLoc2 = new Location("EH23 6NL", "Toby Lane");
        testLoc3 = new Location("BT17 6NL", "Maximillian Ruffert Avenue");
    }

    @Test
    void isNearToTest(){
        Assertions.assertEquals(true, testLoc1.isNearTo(testLoc2));
        Assertions.assertEquals(true, testLoc2.isNearTo(testLoc1));

        Assertions.assertEquals(false, testLoc3.isNearTo(testLoc1));
        Assertions.assertEquals(false, testLoc1.isNearTo(testLoc3));

        Assertions.assertEquals(false, testLoc2.isNearTo(testLoc3));
        Assertions.assertEquals(false, testLoc3.isNearTo(testLoc2));

        Assertions.assertEquals(true, testLoc1.isNearTo(testLoc1));
        Assertions.assertEquals(true, testLoc2.isNearTo(testLoc2));
        Assertions.assertEquals(true, testLoc3.isNearTo(testLoc3));

    }
}
