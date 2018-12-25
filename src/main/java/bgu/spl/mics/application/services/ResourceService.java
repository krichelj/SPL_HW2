package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

@SuppressWarnings("unchecked") // suppress unchecked assignment warnings

public class ResourceService extends MicroService {

	//fields

	private int currentTick; // a field for the current tick got from the time broadcast
	private final int duration;
	private final ResourcesHolder resourcesHolderInstance; // the resource holder instance
	private ConcurrentLinkedQueue<Future<DeliveryVehicle>> pendingForDeliveryVehiclesQueue; // a queue for the the requests for vehicles

	// constructor

	public ResourceService(int currentNumber, int duration) {

		super("ResourceService" + currentNumber);
		resourcesHolderInstance = ResourcesHolder.getInstance();
		pendingForDeliveryVehiclesQueue = new ConcurrentLinkedQueue<>();
		this.duration = duration;
		currentTick = 1;
	}

	// methods

	@Override
	protected void initialize() {

		// subscribe to get the TickBroadcast
		subscribeBroadcast(TickBroadcast.class, resourceTickBroadcast -> {

			currentTick = resourceTickBroadcast.getTick();
			if (currentTick == duration) {

				for (Future<DeliveryVehicle> currentFuture : pendingForDeliveryVehiclesQueue)
					currentFuture.resolve(null); // resolve all pending requests for vehicles

				terminate();
			}
		});

		// subscribe to handle events of type AcquireVehicleEvent
		subscribeEvent(AcquireVehicleEvent.class, acquireVehicleEvent -> {

			Future <DeliveryVehicle> futureAcquiredVehicle = resourcesHolderInstance.acquireVehicle();

			if (!futureAcquiredVehicle.isDone())
				pendingForDeliveryVehiclesQueue.add(futureAcquiredVehicle);

			complete(acquireVehicleEvent, futureAcquiredVehicle);
		});

		// subscribe to handle events of type ReleaseVehicleEvent
		subscribeEvent(ReleaseVehicleEvent.class, releaseVehicleEvent -> {

			DeliveryVehicle vehicleToBeReleased = releaseVehicleEvent.getVehicle();

			if (!pendingForDeliveryVehiclesQueue.isEmpty())
				pendingForDeliveryVehiclesQueue.poll().resolve(vehicleToBeReleased);

			resourcesHolderInstance.releaseVehicle(vehicleToBeReleased);
			complete(releaseVehicleEvent,vehicleToBeReleased);
		});
	}

}
