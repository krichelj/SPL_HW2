package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * A class representing an event in which a release of a vehicle is to be made
 * @param <T> Some object
 */
public class ReleaseVehicleEvent<T> implements Event<T> {

    //fields

    private final DeliveryVehicle vehicle;

    // constructor

    public ReleaseVehicleEvent(DeliveryVehicle vehicle){

        this.vehicle = vehicle;
    }

    // methods

    public DeliveryVehicle getVehicle(){

        return vehicle;
    }
}
