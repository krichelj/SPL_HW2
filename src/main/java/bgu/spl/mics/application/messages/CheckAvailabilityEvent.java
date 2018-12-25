package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

/**
 * A class representing a check availability event
 * @param <T> Some object
 */
public class CheckAvailabilityEvent<T> implements Event<T> {

    // fields

    private final String bookTitle;
    private final Customer currentCustomer;

    // constructor

    public CheckAvailabilityEvent (String bookTitle, Customer currentCustomer){

        this.bookTitle = bookTitle;
        this.currentCustomer = currentCustomer;
    }

    public String getBookTitle() {

        return bookTitle;
    }

    public Customer getCurrentCustomer() {

        return currentCustomer;
    }
}