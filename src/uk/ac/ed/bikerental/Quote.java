package uk.ac.ed.bikerental;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * This class stores information about the quotes that the customer can fetch and choose to book
 */
public class Quote {
	private BikeProvider provider;
	private DateRange dates;
	private Collection<Bike> bikes;
	private BigDecimal price;
	private BigDecimal deposit;
	
	
	/**
	 * @param provider the bike provider that generated the quote
	 * @param dates the dates the quote is for
	 * @param bikes a list of the bikes for the quote
	 * @param price the total price
	 * @param deposit the total deposit
	 */
	public Quote(BikeProvider provider, DateRange dates, Collection<Bike> bikes, BigDecimal price, BigDecimal deposit) {
		this.provider = provider;
		this.dates = dates;
		this.bikes = bikes;
		this.price = price;
		this.deposit = deposit;
	}

	public String getPricingInformation() {
		return null;
	}

	/**
	 * @return the provider
	 */
	public BikeProvider getProvider() {
		return provider;
	}

	/**
	 * @return the dates
	 */
	public DateRange getDates() {
		return dates;
	}

	/**
	 * @return the bikes
	 */
	public Collection<Bike> getBikes() {
		return bikes;
	}

	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @return the deposit
	 */
	public BigDecimal getDeposit() {
		return deposit;
	}


	/**
	 * @return some information about the quote in a nice format
	 */
	public String displayQuote(){
		return "Provider: " + provider.getName()  + "\n\t" + provider.getLocation().toString() + "\nPrice: " +
				price.toString() + "\nDeposit: " + deposit.toString() + "\nDates: " + dates.toString();
	}
}
