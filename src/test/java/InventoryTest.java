import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;

import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * This is a Unit Test for the {@link Inventory} public class.
 *
 * @author Joshua Shay Kricheli
 *
 */

public class InventoryTest {

    /**
     * OUT (Object Under Test)
     */
    private Inventory testInventoryInstance;

    /**
     * Set up for a test
     */
    @Before
    public void setUp () {

        testInventoryInstance = Inventory.getInstance(); // instantiate the Inventory singleton instance

        BookInventoryInfo book1_info = new BookInventoryInfo ("someBook", 5, 50);
        BookInventoryInfo book2_info = new BookInventoryInfo ("someDifferentBook", 1, 100);
        BookInventoryInfo book3_info = new BookInventoryInfo ("someOtherBook", 0, 200);

        BookInventoryInfo[ ] testInventoryInfo = new BookInventoryInfo[3];
        testInventoryInfo[0] = book1_info;
        testInventoryInfo[1] = book2_info;
        testInventoryInfo[2] = book3_info;

        testInventoryInstance.load(testInventoryInfo);
    }

    /**
     * Test method for {@link Inventory#getInstance()}:
     * Retrieves the single instance of this class
     */
    @Test
    public void getInstance() {

        Assert.assertNotNull("The inventory instance is null", testInventoryInstance);
        // check that the instance is not null

        Inventory anotherTestInventoryInstance = Inventory.getInstance();
        Assert.assertEquals ("The inventory instance is not a singleton", testInventoryInstance, anotherTestInventoryInstance);
        // check that the Inventory instance is a singleton

    }

    /**
     * Test method for {@link Inventory#load(BookInventoryInfo[])}:
     * Initializes the store inventory
     */

    @Test
    public void load() {


        Assert.assertEquals ("The first book's name was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[0].getBookTitle(), "someBook");
        Assert.assertEquals ("The second book's name was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[1].getBookTitle(), "someDifferentBook");
        Assert.assertEquals ("The third book's name was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[2].getBookTitle(), "someOtherBook");

        Assert.assertEquals ("The first book's amount in the inventory was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[0].getAmountInInventory(), 5);
        Assert.assertEquals ("The second book's amount in the inventory was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[1].getAmountInInventory(), 1);
        Assert.assertEquals ("The third book's amount in the inventory was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[2].getAmountInInventory(), 0);

        Assert.assertEquals ("The first book's price was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[0].getPrice(), 50);
        Assert.assertEquals ("The second book's price was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[1].getPrice(), 100);
        Assert.assertEquals ("The third book's price was not assigned correctly",
                testInventoryInstance.getBookInventoryInfo()[2].getPrice(), 200);

    }

    /**
     * Test method for {@link Inventory#take(String)}:
     * Attempts to take one book from the store
     */

    @Test
    public void take() {

        Assert.assertEquals ("The first book was not taken correctly",
                testInventoryInstance.take("someBook"), OrderResult.SUCCESSFULLY_TAKEN);
        Assert.assertEquals ("The first book was taken but its quantity remained the same",
                testInventoryInstance.getBookInventoryInfo()[0].getAmountInInventory(), 4);

        Assert.assertEquals ("The second book was not taken correctly",
                testInventoryInstance.take("someDifferentBook"), OrderResult.SUCCESSFULLY_TAKEN);
        Assert.assertEquals ("The second book was taken but its quantity remained the same",
                testInventoryInstance.getBookInventoryInfo()[1].getAmountInInventory(), 0);

    }

    /**
     * Test method for {@link Inventory#checkAvailabiltyAndGetPrice(String)}:
     * Checks if a certain book is available in the inventory
     */

    @Test
    public void checkAvailabiltyAndGetPrice() {

        Assert.assertEquals ("The first book's price is not correct",
                testInventoryInstance.checkAvailabiltyAndGetPrice("someBook"), 50);
        Assert.assertEquals ("The third book's price is not correct",
                testInventoryInstance.checkAvailabiltyAndGetPrice("someOtherBook"), -1);

    }

    @Test
    public void printInventoryToFile() {

        testInventoryInstance.printInventoryToFile("inventoryData.txt");

    }
}