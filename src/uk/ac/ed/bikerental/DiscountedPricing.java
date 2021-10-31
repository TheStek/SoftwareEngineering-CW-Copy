package uk.ac.ed.bikerental;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * This is an implementation of the Discounted pricing submodule
 */
public class DiscountedPricing implements PricingPolicy{
	
	
	/** Stores the daily rental price of each bike type */
	private HashMap<BikeType, BigDecimal> dailyPrices;
	
	/** Stores the minimum days to qualify for a discount and the discount as a percentage */
	private ArrayList<DurationDiscount> durationDiscounts;


	public DiscountedPricing(){
		this.dailyPrices = new HashMap<BikeType, BigDecimal>();
		this.durationDiscounts = new ArrayList<DurationDiscount>();
	}

	public HashMap<BikeType, BigDecimal> getDailyPrices() {
		return dailyPrices;
	}

	public ArrayList<DurationDiscount> getDurationDiscounts() {
		return durationDiscounts;
	}

	/**
	 *  We check if the input BikeType is in the hashmap. If it is the price is updated
	 * 	If it isn't in the map the price is not updated and an error message is printed
	 * @param bikeType the BikeType to set the price of
	 * @param price the corresponding price
	 */
	public void setDailyRentalPrice(BikeType bikeType, BigDecimal price){
		if (this.dailyPrices.containsKey(bikeType)){
			this.dailyPrices.put(bikeType, price);
		}
		else {
			System.out.print("BikeType not in pricebook. Add type separately");
		}
	}


	/**
	 * Adds a new BikeType to dailyPrices map if not already a member
	 * @param bikeType the BikeType to add the price for
	 * @param price the corresponding price
	 */
	public void addBikeTypeToPrices(BikeType bikeType, BigDecimal price) {
		if (!this.dailyPrices.containsKey(bikeType)){
			this.dailyPrices.put(bikeType, price);
		}
		else {
			System.out.print("BikeType already in pricebook");
		}
	}
		
	/**
	Inserts the inputted DurationDiscount in the correct position in the ArrayList
	This should be in order of increasing discount value
	 */
	public void addDiscount(DurationDiscount discount) {
		int pos = 0;
		if (durationDiscounts.size() != 0){
			for (int i=0; i<durationDiscounts.size(); i++) {
				DurationDiscount posD = durationDiscounts.get(i);
				if (posD.discount.doubleValue()<discount.discount.doubleValue()) {
					pos = i;
				}
			}
		}
		durationDiscounts.add(pos, discount);
	}

	/**
	 * This calculates the price of a quote given the Bikes and DateRange
	 * 	To do this it multiplies the daily rate for each bike by the duration
	 * 	It then finds the best applicable discount and applies this
	 *
	 * @param bikes the bikes to calculate the price for
	 * @param date the dates to calculate the price for
	 * @return the total price to quote
	 */
	@Override
	public BigDecimal calculatePrice(Collection<Bike> bikes, DateRange date) {
		BigDecimal total = new BigDecimal(0);
		for (Bike b: bikes) {
			BikeType type = b.getType();
			if (dailyPrices.containsKey(type)) {
				total = total.add(dailyPrices.get(type).multiply(new BigDecimal(date.toDays())));
			}
			else {
				System.out.print("Invalid BikeType in order");
			}
		}
		
		BigDecimal discount = findDiscount(date.toDays());
		BigDecimal discountFactor = new BigDecimal(1).subtract(discount.divide(new BigDecimal(100)));
		
		return total.multiply(discountFactor);
	}

	/**
	 *	To find the best available discount the discount ArrayList is looped through
	 * 	If the duration, l, falls within the range of a discount we set the best to this discount
	 * 	This works since the list is in order of increasing discount
	 * @param l the length of days the bikes are being rented for
	 * @return the best applicable discount as a percentage
	 */
	private BigDecimal findDiscount(long l) {
		DurationDiscount best = new DurationDiscount(0,0,new BigDecimal(0));
		for (DurationDiscount d : durationDiscounts) {
			if (d.min<=l & d.max>=l) best = d;
		}
		return best.discount;
		
	}
		
}


