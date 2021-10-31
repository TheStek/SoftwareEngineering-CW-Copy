package uk.ac.ed.bikerental;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


/**
 * Used to store a range of dates that bikes will be rented for
 */
public class DateRange {
    private LocalDate start, end;


    /**
     * Constructor taking the start and end dates of the range
     * @param start the start of the range as a LocalDate
     * @param end the end of the range as a LocalDate
     */
    public DateRange(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }
    
    public LocalDate getStart() {
        return this.start;
    }
    
    public LocalDate getEnd() {
        return this.end;
    }

    /**
     *
     * @return the length of the date range in years
     */
    public long toYears() {
        return ChronoUnit.YEARS.between(this.getStart(), this.getEnd());
    }

    /**
     *
     * @return the length of the date range in days
     */
    public long toDays() {
        return ChronoUnit.DAYS.between(this.getStart(), this.getEnd());
    }


    @Override
    public int hashCode() {
        // hashCode method allowing use in collections
        return Objects.hash(end, start);
    }

    @Override
    public boolean equals(Object obj) {
        // equals method for testing equality in tests
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DateRange other = (DateRange) obj;
        return Objects.equals(end, other.end) && Objects.equals(start, other.start);
    }
    
    // You can add your own methods here

    /**
     * Checks if a given DateRange and this DateRange object overlap with each other.
     * This problem is broken down into 2 cases: does the others start lie in this range,
     * or does this ranges start lie in the other.
     * The LocalDate.compareTo method is used to determine whether a date comes before or after another
     *
     * @param other the other DateRange to compare with
     * @return a boolean representing if the dates overlap
     */
    public Boolean overlaps(DateRange other) {
        LocalDate otherStart = other.getStart();
        LocalDate otherEnd = other.getEnd();
        return ((start.compareTo(otherStart)>=0 && start.compareTo(otherEnd)<=0) ||
                (otherStart.compareTo(start)>=0 && otherStart.compareTo(end)<=0));
    }


}





