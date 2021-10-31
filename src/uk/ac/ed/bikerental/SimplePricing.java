package uk.ac.ed.bikerental;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of the PricingPolicy
 */
public class SimplePricing implements PricingPolicy {

    private Map<BikeType, BigDecimal> rates;

    public SimplePricing() {
        this.rates = new HashMap<BikeType, BigDecimal>();
    }


    @Override
    public void setDailyRentalPrice(BikeType bikeType, BigDecimal dailyPrice) {
        rates.put(bikeType, dailyPrice);
    }

    /**
     * Calculates the price of a quote by multiplying the daily price of each bike by the
     * number of days rented for and summing these
     * @param bikes a collection of the bikes to quote
     * @param duration the dates to quote for
     * @return the final price
     */
    @Override
    public BigDecimal calculatePrice(Collection<Bike> bikes, DateRange duration) {
        BigDecimal total = new BigDecimal(0);
        for (Bike b: bikes){
            if (rates.containsKey(b.getType())){
                total = total.add(rates.get(b.getType()));
            }
            else System.out.println("Bike type is not in the rates");
        }
        return total.multiply(new BigDecimal(duration.toDays()));
    }


    public Map<BikeType, BigDecimal> getRates() {
        return rates;
    }
}
