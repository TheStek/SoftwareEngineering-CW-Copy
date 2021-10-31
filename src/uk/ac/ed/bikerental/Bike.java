package uk.ac.ed.bikerental;

import java.util.ArrayList;
import java.util.Collection;


/**
 * The Bike class represents the bikes in code
 */
public class Bike implements Deliverable {


    private BikeType type;
    private String status;
    private Collection<DateRange> datesRented;

    public Collection<DateRange> getDatesRented() {
        return datesRented;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Constructor defining the bikes type.
     * An empty list of DateRange objects is created which store all the dates that the Bike is being rented for
     * @param type the bike's BikeType
     */
    public Bike(BikeType type){
        this.type = type;
        this.datesRented = new ArrayList<DateRange>();
        status = "Available";
    }

    public BikeType getType() {
        return this.type;
    }

    /**
     * Checks if a bike is free to be booked for a given DateRange
     * @param dates the DateRange to check
     * @return true if the bike is free on the input dates, otherwise false
     */
    public boolean checkFree(DateRange dates){
        for (DateRange d: datesRented){
            if (d.overlaps(dates)) return false;
        }
        return true;
    }

    /**
     * Add a DateRange into the dates the bike is booked for
     * @param date the DateRange to add
     */
    public void addToDates(DateRange date){
        datesRented.add(date);
    }

    /**
     * Remove a DateRange from the dates the bike is booked for
     * @param date the DateRange to remove
     */
    public void removeFromDates(DateRange date){
        datesRented.remove(date);
    }

    @Override
    public void onPickup() {
        setStatus("Out for Delivery");
    }

    @Override
    public void onDropoff() {
        setStatus("Delivered");
    }
}
