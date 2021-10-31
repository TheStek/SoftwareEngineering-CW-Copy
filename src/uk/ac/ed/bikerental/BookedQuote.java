package uk.ac.ed.bikerental;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A Quote that has been booked
 */
public class BookedQuote{
	private BikeProvider provider;
	private DateRange dates;
	private Collection<Bike> bikes;
	private BigDecimal price;
	private BigDecimal deposit;
	private boolean returnedToPartner;
	private BikeProvider partnerToReturnTo;
	private Customer customer;
	private String status;
	private String bookingID;

	private final ArrayList<String> possibleStatuses = new ArrayList<String>(Arrays.asList("Returned", "Pending Payment", "Pending Collection", "Pending Delivery"));

	private static long idCounter = 0;

	public BookedQuote() {
		generateBookingID();
	}

	public BikeProvider getProvider() {
		return provider;
	}

	public DateRange getDates() {
		return dates;
	}

	public Collection<Bike> getBikes() {
		return bikes;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}


	public boolean isReturnedToPartner() {
		return returnedToPartner;
	}

	public BikeProvider getPartnerToReturnTo() {
		return partnerToReturnTo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		assert possibleStatuses.contains(status);
		this.status = status;
	}

	public String getBookingID() {
		return bookingID;
	}

	public void generateBookingID(){
		this.bookingID = String.valueOf(idCounter++);
	}


	/**
	 * The constructor takes in the selected Quote and copies the data across from it
	 * resolveCollectionOrDelivery is also called to fill out some of the other attributes
	 * @param parentQuote the selected Quote to book
	 * @param customer the customer doing the booking
	 */
	public BookedQuote(Quote parentQuote, Customer customer) {
		this.provider = parentQuote.getProvider();
		this.bikes = parentQuote.getBikes();
		this.deposit = parentQuote.getDeposit();
		this.price = parentQuote.getPrice();
		this.dates = parentQuote.getDates();
		this.customer = customer;
		for (Bike b: bikes){
			b.addToDates(dates);
		}
		generateBookingID();
	}


	/**
	 * Allows the customer to select a partner to return their order to and sets attributes accordingly
	 * @param partner the BikeProvider to return the order to (if it is a partner)
	 */
	public void setPartnerToReturnTo(BikeProvider partner){

		if (provider.getPartners().contains(partner)){
			partnerToReturnTo = partner;
			returnedToPartner = true;
		}
	}



}
