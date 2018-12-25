package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 * A class representing an acquire vehicle event
 * @param <T> Some object
 */
public class AcquireVehicleEvent<T> implements Event<T> {

    // fields

    private final String address;

    // constructor

    public AcquireVehicleEvent(String address) {

        this.address = address;
    }

    // methods

    public String getAddress() {

        return address;
    }
}