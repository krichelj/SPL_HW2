package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateStoreBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

	// fields

	private final int speed, duration; // make the speed and duration final as they cannot be changed when the store is open
	private final Timer storeTimer; // a final timer instance that handles the time
	private final AtomicInteger currentTick; // an atomic integer for the tick value

	// constructor

	public TimeService(int speed, int duration) {

		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		storeTimer = new Timer();
		currentTick = new AtomicInteger(1);
	}

	//methods

	@Override
	protected void initialize() {

		// subscribe to get the TerminateStoreBroadcast
		subscribeBroadcast(TerminateStoreBroadcast.class, terminateStoreBroadcast -> terminate());

		// start the timer with an appropriate task and at the fixed given speed
		storeTimer.scheduleAtFixedRate(new TimerTask() { // define a new timer that handles the tick that represent time

			@Override
			public void run() { // define the method that runs in each tick

				if (currentTick.get() <= duration)
					sendBroadcast(new TickBroadcast(currentTick.getAndIncrement())); // send the time broadcast and increment the tick
				else {

					this.cancel(); // cancels the TimerTask
					storeTimer.cancel(); // cancels the Timer
					terminate();
					sendBroadcast(new TerminateStoreBroadcast()); // sends a broadcast to terminate the store
				}
			}
		},0, speed);
	}

	/**
	 * Returns the speed of the time service
	 * @return speed value
	 */
	public int getSpeed() {

		return speed;
	}

	/**
	 * Returns the duration of the run
	 * @return duration value
	 */
	public int getDuration() {

		return duration;
	}
}