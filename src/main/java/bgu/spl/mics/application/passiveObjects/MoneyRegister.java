package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */

@SuppressWarnings("unused") // suppress warnings about unused methods

public class MoneyRegister implements Serializable {

	// fields

	private final LinkedList<OrderReceipt> receiptList; // the receipts of the store

	// singleton implementation

	private static class MoneyRegisterSingletonHolder {

		private static MoneyRegister moneyRegisterInstance = new MoneyRegister();
	}

	// constructor

	private MoneyRegister() {

		receiptList = new LinkedList<>();
	}

	// methods

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {

		return MoneyRegisterSingletonHolder.moneyRegisterInstance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	// synchronized as it is a thread-safe operation
	public synchronized void file (OrderReceipt r) {

		receiptList.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {

		int totalEarnings = 0;

		for (OrderReceipt currentReceipt : receiptList)
			totalEarnings += currentReceipt.getPrice();

		return totalEarnings;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	// synchronized as it is a thread-safe operation
	public synchronized void chargeCreditCard(Customer c, int amount) {

		c.subtractAmountFromCredit(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {

		Printer.print(receiptList, filename);
	}

	public List<OrderReceipt> getOrderReceipts() {

		return receiptList;
	}
}