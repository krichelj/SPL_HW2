package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.concurrent.TimeUnit;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

@SuppressWarnings("unchecked") // suppress unchecked assignment warnings

public class LogisticsService extends MicroService {

	//fields

	private int currentTick; // a field for the current tick got from the time broadcast
	private final int duration, speed;

	// constructor

	public LogisticsService(int currentNumber, int duration, int speed) {

		super("LogisticsService" + currentNumber);
		this.duration = duration;
		this.speed = speed;
		currentTick = 1;
	}

	// methods

	@Override
	protected void initialize() {

		// subscribe to get the TickBroadcast
		subscribeBroadcast(TickBroadcast.class, logisticsTickBroadcast -> {

			currentTick = logisticsTickBroadcast.getTick();
			if (currentTick == duration)
				terminate();
		});

		// subscribe to handle events of type DeliveryEvent
		subscribeEvent(DeliveryEvent.class, deliveryEvent -> {

			AcquireVehicleEvent acquiredVehicleEvent = new AcquireVehicleEvent<>(deliveryEvent.getAddress());

			DeliveryVehicle acquiredVehicle = ((Future<Future<DeliveryVehicle>>)
					sendEvent(acquiredVehicleEvent)).get((duration-currentTick)*speed, TimeUnit.MILLISECONDS)
					.get((duration-currentTick)*speed, TimeUnit.MILLISECONDS); // wait for an acquired vehicle

			acquiredVehicle.deliver(deliveryEvent.getAddress(), deliveryEvent.getDistance()); // deliver the book
			sendEvent(new ReleaseVehicleEvent<>(acquiredVehicle)).get((duration-currentTick)*speed, TimeUnit.MILLISECONDS); // release the vehicle
			complete(deliveryEvent, acquiredVehicle);
		});
	}

}
