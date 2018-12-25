package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import java.io.Serializable;

/**
 * A class representing an book order event
 * @param <T> Some object
 */
public class BookOrderEvent<T> implements Event<T>, Serializable {

    // fields

    private final String bookTitle; // final string that represents the book title
    private final int tick; // a final int that represents the tick in which the order is to be made
    private Customer currentCustomer;

    // constructor

    public BookOrderEvent(String bookTitle, int tick, Customer currentCustomer) {

        this.bookTitle = bookTitle;
        this.tick = tick;
        this.currentCustomer = currentCustomer;
    }

    // methods

    public String getBookTitle() {

        return bookTitle;
    }

    public int getTick() {

        return tick;
    }

    public Customer getCurrentCustomer() {

        return currentCustomer;
    }

    public void setCurrentCustomer(Customer customer) {

        this.currentCustomer = customer;
    }
}