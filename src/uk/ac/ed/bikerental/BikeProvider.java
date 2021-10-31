package uk.ac.ed.bikerental;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


/**
 * This is a representation of the Bike shop
 * It stores information about the shop and handles generating quotes and stock levels
 */
public class BikeProvider {

	private String name;
	private Location location;
	private PricingPolicy pricing;
	private ValuationPolicy valuation;
	private HashMap<BikeType, ArrayList<Bike>> bikeStocks;
	private Collection<BikeProvider> partners;
	private DeliveryService deliveryService;


	/**A constructor taking all the attribute of a bike provider and initialising the required collections and maps
	 *
	 * @param name the name of the rental shop
	 * @param location the location of the rental shop
	 * @param pricing the PricingPolicy object to calculate quote prices
	 * @param valuation the ValuationPolicy object to calculate quote deposits
	 * @param deliveryService the delivery service used by the BikeProvider
	 */
	public BikeProvider(String name, Location location, PricingPolicy pricing, ValuationPolicy valuation, DeliveryService deliveryService){
		this.name = name;
		this.location = location;
		this.pricing = pricing;
		this.valuation = valuation;
		this.deliveryService = deliveryService;
		this.bikeStocks = new HashMap<BikeType, ArrayList<Bike>>();
		this.partners = new ArrayList<BikeProvider>();

	}

	// General getters and setters

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	public Collection<BikeProvider> getPartners() {
		return partners;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public PricingPolicy getPricing() {
		return pricing;
	}

	public DeliveryService getDeliveryService() {
		return deliveryService;
	}

	public ValuationPolicy getValuation() {
		return valuation;
	}

	public void setPricing(PricingPolicy pricing) {
		this.pricing = pricing;
	}

	public void setValuation(ValuationPolicy valuation) {
		this.valuation = valuation;
	}

	public HashMap<BikeType, ArrayList<Bike>> getBikeStocks() {
		return bikeStocks;
	}

	/**
	 * Adds a partner to the partner collection if it is not already present
	 * @param partner the new partner to add
	 */
	public void addPartner(BikeProvider partner){
		if (! partners.contains(partner)) partners.add(partner);
	}

	/**
	 * Removes a partner from the partner collection if it is present
	 * @param partner the partner to remove
	 */
	public void removePartner(BikeProvider partner){
		if (partners.contains(partner)) partners.remove(partner);
	}

	public void addBike(Bike bike){
		if (bikeStocks.containsKey(bike.getType())) bikeStocks.get(bike.getType()).add(bike);
		else {
			bikeStocks.put(bike.getType(), new ArrayList<Bike>());
			addBike(bike);
		}
	}

	public void removeBike(Bike bike){
		if (bikeStocks.containsKey(bike.getType())){
			bikeStocks.get(bike.getType()).remove(bike);
		}
	}

	/**
	 * Generates a quote object for the input search parameters
	 * @param bikesWanted a map of the BikeTypes wanted to the quantity wanted
	 * @param dates the DateRange to quote for
	 * @return a related Quote object or null if no suitable quotes found
	 */
	public Quote generateQuote(Map<BikeType, Integer> bikesWanted, DateRange dates) {

		// This is a list of the Bike objects that are going to be in the Quote
		ArrayList<Bike> bikesToQuote = new ArrayList<Bike>();

		// First we loop through all the BikeTypes offered by the provider
		for (BikeType k : bikeStocks.keySet()){

			// Checks if this is a BikeType wanted in the search
			if (bikesWanted.containsKey(k)){

				// The quantity of bikes of this type currently wanted
				// This will decrease when we find more suitable Bikes to add to the quote
				int amountWanted = bikesWanted.get(k);

				// These are all the Bikes we look at, all the bikes of the current type we're looking at
				ArrayList<Bike> bikesOfWantedType = bikeStocks.get(k);

				int counter = 0;

				// We loop through all the bikesOfType and check if they're free for the given dates
				// If they are then we add the Bike to the bikesToQuote list and decrease amount wanted
				while (amountWanted > 0 && counter < bikesOfWantedType.size()){
					Bike b = bikesOfWantedType.get(counter);
					if (b.checkFree(dates) && (b.getStatus().equals("Available"))) {
						bikesToQuote.add(b);
						amountWanted --;
					}
					counter++;
				}

				// If we don't find all the Bikes wanted null is returned
				if (amountWanted != 0) return null;
			}
		}

		// This checks that we found all the Bikes that we wanted and returns null if not
		int totalWanted = 0;
		for (int n: bikesWanted.values()) totalWanted += n;
		if (bikesToQuote.size() != totalWanted) return null;


		// The PricingPolicy and ValuationPolicy are used to find the price and deposit
		BigDecimal price = pricing.calculatePrice(bikesToQuote, dates);
		BigDecimal deposit = new BigDecimal(0);
		for (Bike b : bikesToQuote){
			deposit = deposit.add(valuation.calculateValue(b, LocalDate.now()));
		}

		return new Quote(this, dates, bikesToQuote, price, deposit);
	}

	/**
	 * Records the return of a bike order in the system by removing the dates from each Bike.dates
	 * and updating order status
	 * @param bikeReturn the BookedQuote
	 */
	public void returnBikes(BookedQuote bikeReturn){
		DateRange datesRented = bikeReturn.getDates();
		for (Bike b: bikeReturn.getBikes()){
			b.removeFromDates(datesRented);
			b.setStatus("Available");
		}
		bikeReturn.setStatus("Returned");
	}

	/**
	 * Schedules a delivery of the order to the customer if they requested that it be delivered
	 * @param booking the BookedQuote that needs to be delivered
	 */
	public void scheduleDeliveryToCustomer(BookedQuote booking){
		for (Bike b: booking.getBikes()) {
			deliveryService.scheduleDelivery(b, location, booking.getCustomer().getAddress(), booking.getDates().getStart());
		}
	}

	/**
	 * Returns the order of a customer at a provider, given the customer and the bookingID
	 * @param customer the customer who made the booking
	 * @param bookingID the booking id of the booking to return
	 */
	public void returnOrder(Customer customer, String bookingID){
		BookedQuote bikeReturn = null;
		for (BookedQuote bq: customer.getBookings()){
			if (bq.getBookingID().equals(bookingID)) bikeReturn = bq;
		}

		if (bikeReturn == null) return;

		if (bikeReturn.isReturnedToPartner()){
			returnBikeToPartner(bikeReturn);
		}
		else returnBikes(bikeReturn);
	}

	/**
	 * This schedules the delivery of a BookedQuote to return to a partner and then calls the returnBikes
	 * method in the partner provider
	 * @param bikeReturn the BookedQuote to return (this contains information about who to return to)
	 */
	private void returnBikeToPartner(BookedQuote bikeReturn){
		BikeProvider partner = bikeReturn.getProvider();

		for (Bike b: bikeReturn.getBikes()) {
			deliveryService.scheduleDelivery(b, location, partner.getLocation(), LocalDate.now());
		}
		partner.returnBikes(bikeReturn);
	}
}
