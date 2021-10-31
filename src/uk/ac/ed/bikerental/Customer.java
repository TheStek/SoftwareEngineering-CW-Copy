package uk.ac.ed.bikerental;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

/**
 * This is a representation of the customer and is used to take their inputs and handle processes initiated by them
 */
public class Customer {
	private String firstname;
	private String surname;
	private Location address;
	private ArrayList<BookedQuote> bookings;


	public Customer(String firstname, String surname, Location address){
		this.firstname = firstname;
		this.surname = surname;
		this.address = address;
		this.bookings = new ArrayList<BookedQuote>();
	}


	// General getters and setters

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Location getAddress() {
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	public ArrayList<BookedQuote> getBookings() {
		return bookings;
	}


	/**
	 * 	 Creates a new Search object with required parameters
	 * 	 This is then used to return a collection of quotes from different providers
	 * 	 If none are returned alternate dates are found and the method is called recursively with these dates
	 * @param location the location to look for quotes near
	 * @param bikes a map of bike types to quantities to get quotes for
	 * @param dates the dates to get quotes for
	 * @param providers the providers to look for quotes from (should generally be all providers)
	 * @return a collection of relevant Quotes
	 */
	public ArrayList<Quote> searchQuotes(Location location, Map<BikeType, Integer> bikes, DateRange dates, ArrayList<BikeProvider> providers){
		Search search = new Search(location, dates, bikes, providers);
		ArrayList<Quote> quotes = (ArrayList<Quote>) search.getQuotes();
		return quotes;

	}

	/**
	 * Prints the inputted quotes nicely
	 * @param quotes the quotes to display
	 */
	public void displayQuotes(ArrayList<Quote> quotes) {
		for (int i=0;i<quotes.size(); i++) {
			System.out.println(i + quotes.get(i).displayQuote());
		}
	}


	/**
	 * Takes in a Quote object and makes a new BookedQuote object with the same data
	 * 	Then other attributes like delivery or collection and partner return are checked and set
	 * 	Throughout the process the status is set accordingly
	 * @param selectedQuote the quote to book
	 * @param partner
	 * @param isCollected
	 * @return the BookedQuote generated
	 */
	public void makeBooking(Quote selectedQuote, BikeProvider partner, Boolean isCollected) {
		BookedQuote bq = new BookedQuote(selectedQuote, this);
		bq.setStatus("Pending Payment");

		if (new PaymentMethod().authorisePayment()) {
			if (! isCollected){
				bq.setStatus("Pending Delivery");
				bq.getProvider().scheduleDeliveryToCustomer(bq);

			}
			else bq.setStatus("Pending Collection");
		}
		else return;
		if (partner != null){
			bq.setPartnerToReturnTo(partner);
		}

		bookings.add(bq);

	}






}
