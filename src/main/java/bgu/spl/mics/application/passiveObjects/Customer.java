package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.messages.BookOrderEvent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */

@SuppressWarnings({"WeakerAccess", "unused"}) // suppress warnings about methods that can be weaker and unused methods

public class Customer implements Serializable {

	// fields - all final as they are not to be changed for a customer

	private final int id, distance;
	private final String name, address;
	private final CreditCard creditCard; // a field for the customer's credit card details
	private final BookOrderEvent[] orderSchedule; // a field for the customer's order schedule
	private final LinkedList<OrderReceipt> Receipts; // a field for the customer's receipts

	// constructor

	/**
	 * A copy constructor for a customer
	 * @param customer input customer to copy from
	 */
	public Customer (Customer customer){

		this.id = customer.getId();
		this.name = customer.getName();
		this.address = customer.getAddress();
		this.distance = customer.getDistance();
		this.creditCard = customer.getCreditCard();
		this.orderSchedule = customer.getOrderSchedule();
		Receipts = new LinkedList<>();
		for (BookOrderEvent currentBookOrderEvent : orderSchedule)
			currentBookOrderEvent.setCurrentCustomer(this);
	}

	// methods

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {

		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {

		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {

		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {

		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {

		return Receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {

		return creditCard.amount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {

		return creditCard.number;
	}

	/**
	 * subtracts a given amount from the customer's credit card balance
	 * @param amount the amount to be subtracted
	 */
	// synchronized as it is a thread-safe operation
	public synchronized void subtractAmountFromCredit(int amount) {

		creditCard.amount -= amount;
	}


	/**
	 * @return the order schedule of the customer
	 */
	public BookOrderEvent[] getOrderSchedule() {

		return orderSchedule;
	}


	/**
	 * @return the credit card instance of this customer
	 */
	public CreditCard getCreditCard() {

		return creditCard;
	}

	/**
	 * private nested class for the customer's credit card details
	 */
	private class CreditCard implements Serializable {

		private final int number; // final field for the customer's credit card number as it does not change
		private int amount;

		// constructor

		private CreditCard(int number, int amount) {
			this.number = number;
			this.amount = amount;
		}
	}
}