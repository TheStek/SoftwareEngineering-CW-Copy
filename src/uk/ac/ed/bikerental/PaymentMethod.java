package uk.ac.ed.bikerental;
/**
 This is a placeholder class, providing an interface for an external system to handle the payment process
 For the purposes of testing authorisePayment will always return true
 */
public class PaymentMethod {

    /**
	 * This would call the external system and attempt to authorise the payment
	 * @return a boolean indicating if the payment was authorised
	 */
	public boolean authorisePayment() {
		return true;
	}
}
