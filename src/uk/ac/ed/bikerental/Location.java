package uk.ac.ed.bikerental;

public class Location {
    private String postcode;
    private String address;


    /**
     * Constructor takes the initial postcode and address strings
     * @param postcode the postcode
     * @param address the address
     */
    public Location(String postcode, String address) {
        assert postcode.length() >= 6;
        this.postcode = postcode;
        this.address = address;
    }

    /**
     * Takes in another Location object and checks that they lie in the same postal area
     * (as defined in the CW3 instructions)
     * @param other the other Location object to compare with
     * @return a boolean indicating if the other Location is "near" to this Location
     */
    public boolean isNearTo(Location other) {
        return getPostalArea(postcode).equals(getPostalArea(other.getPostcode()));
    }

    /**
     * Getter for the postcode
     * @return postcode
     */
    public String getPostcode() {
        return postcode;
    }


    /**
     * Getter for the address
     * @return address
     */
    public String getAddress() {
        return address;
    }
    
    // You can add your own methods here

    /**
     * Takes in a postcode and returns the first 2 characters
     * @param postcode the postcode in question as a String
     * @return the first 2 characters of the postcode
     */
    private static String getPostalArea(String postcode){
        return postcode.substring(0, 2);
    }

}
