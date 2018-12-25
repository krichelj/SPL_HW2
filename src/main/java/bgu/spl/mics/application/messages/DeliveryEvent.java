package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 * A class representing an event in which a delivery of a book is to be made
 * @param <T> Some object
 */
public class DeliveryEvent<T> implements Event<T> {

    // fields

    private final String address;
    private final int distance;

    // constructor

    public DeliveryEvent(String address, int distance){

        this.address = address;
        this.distance = distance;
    }

    // methods
    public String getAddress (){

        return address;
    }

    public int getDistance () {

        return distance;
    }
}
