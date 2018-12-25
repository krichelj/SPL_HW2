package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */

@SuppressWarnings("WeakerAccess") // suppress warnings about methods that can be weaker

public class BookInventoryInfo {

	// fields

	private final String bookTitle;
	private final int price;
	private int amount;

	//constructor

	public BookInventoryInfo(String bookTitle, int amount, int price) {

		this.bookTitle = bookTitle;
		this.amount = amount;
		this.price = price;
	}

	// methods

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {

		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {

		return amount;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {

		return price;
	}

	/**
	 * Removes one book from the current book
	 */
	public void removeOneBook (){

		amount--;
	}
}
