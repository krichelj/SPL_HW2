package bgu.spl.mics.application.passiveObjects;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */

public class Inventory implements Serializable {

	// fields

	private BookInventoryInfo[] bookInventoryInfosArray; // for test purposes
	private final ConcurrentHashMap<String,BookInventoryInfo> bookInventoryInfos; // a concurrent hash map for the book inventory for the store
	private final ConcurrentHashMap<String, Semaphore> bookInventoryAmounts; // a concurrent hash map of semaphores for the books amount in the store

	// thread-safe singleton implementation

	private static class InventorySingletonHolder {

		private static Inventory inventoryInstance = new Inventory();
	}

	// constructor

	private Inventory() {

		bookInventoryInfos = new ConcurrentHashMap<>();
		bookInventoryAmounts = new ConcurrentHashMap<>();
	}

	// methods

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {

		return InventorySingletonHolder.inventoryInstance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */

	public void load (BookInventoryInfo[ ] inventory) {

		bookInventoryInfosArray = inventory; // copy the input array

		// build the book hash maps
		for (BookInventoryInfo currentBookInfo : inventory) {

			bookInventoryInfos.put(currentBookInfo.getBookTitle(), currentBookInfo); // insert the book
			bookInventoryAmounts.put(currentBookInfo.getBookTitle(), new Semaphore(currentBookInfo.getAmountInInventory())); // create a semaphore
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */

	public OrderResult take (String book) {


		OrderResult result = OrderResult.NOT_IN_STOCK;

		if (bookInventoryAmounts.get(book).tryAcquire()) {
			bookInventoryInfos.get(book).removeOneBook();
			result = OrderResult.SUCCESSFULLY_TAKEN;
		}

		return result;
	}
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */

	public int checkAvailabiltyAndGetPrice(String book) {

		int output = -1;

		if (bookInventoryInfos.containsKey(book) && bookInventoryAmounts.get(book).tryAcquire()) {
			output = bookInventoryInfos.get(book).getPrice();
			bookInventoryAmounts.get(book).release();
		}

		return output;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer>
	 *     which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */

	public void printInventoryToFile(String filename){

		/*HashMap class is Serializable and therefore can be represented as a known byes series
		 This method first instantiates a hash map and inserts all the inventory's data*/
		HashMap<String,Integer> inventoryOutputHashMap = new HashMap<>();

		bookInventoryInfos.forEach((key,value) -> inventoryOutputHashMap.put(key, value.getAmountInInventory()));
		Printer.print(inventoryOutputHashMap, filename); //write to the output file named filename
	}

	// ---------- TESTS AID FUNCTION - NOT USED IN IMPLEMENTATION -----------

	public BookInventoryInfo[] getBookInventoryInfo(){

		return bookInventoryInfosArray;

	}
}