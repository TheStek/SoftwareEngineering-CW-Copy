package uk.ac.ed.bikerental;

import java.math.BigDecimal;

/**
 *This class is used to store discounts in a usable way
 It stores the minimum and maximum amount of days to qualify for the discount, and the actual discount value
 The discount value is stored as a BigDecimal percentage
 ie a 20% discount would be stored as a BigDecimal with value 20
 */
public class DurationDiscount {

	public int min;
	public int max;
	public BigDecimal discount;

	/**
	 * This is a simple constructor with the attributes assigned to the input values
	 * 	In order to have an open ended discount ie 14+ days, the maximum input is -1
	 * 	Then DurationDiscount.max will be set to the maximum integer value
	 * @param min the minimum days to qualify for
	 * @param max the maximum days to qualify for
	 * @param discount the corresponding discount
	 */
	public DurationDiscount(int min,int max, BigDecimal discount) {
		this.min = min;
		if (max != -1) this.max = max;
		// Set to maximum integer
		else this.max = 2147483647;
		this.discount = discount;
	}
	
}
