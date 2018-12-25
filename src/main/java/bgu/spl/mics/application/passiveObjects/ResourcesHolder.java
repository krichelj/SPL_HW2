package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	// fields

	private final ConcurrentLinkedQueue<DeliveryVehicle> deliveryVehiclesInStoreQueue; // a concurrent queue for the vehicles parking in the store

	// thread-safe singleton implementation

	private static class ResourcesHolderSingletonHolder {

		private final static ResourcesHolder resourcesHolderInstance = new ResourcesHolder();
	}

	// constructor

	private ResourcesHolder() {

		deliveryVehiclesInStoreQueue = new ConcurrentLinkedQueue<>();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {

		return ResourcesHolderSingletonHolder.resourcesHolderInstance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {

		DeliveryVehicle acquiredVehicle = deliveryVehiclesInStoreQueue.poll(); // take the first delivery vehicle
		Future<DeliveryVehicle> currentlyPendingForVehicle = new Future<>();

		if (acquiredVehicle!=null) // resolve the future object if there's a delivery vehicle available
			currentlyPendingForVehicle.resolve(acquiredVehicle);

		return currentlyPendingForVehicle;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {

		deliveryVehiclesInStoreQueue.add(vehicle); // add the vehicle back to the store
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {

		Collections.addAll(deliveryVehiclesInStoreQueue, vehicles);
	}

}
