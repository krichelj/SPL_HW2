package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

@SuppressWarnings("unchecked") // suppress unchecked assignment warnings

public class APIService extends MicroService {

	//fields

	private int currentTick; // a field for the current tick got from the time broadcast
	private final int duration, speed;
	private final Customer currentCustomer;
	private final List <BookOrderEvent> orderSchedule; // a list for the customer's orders

	// constructor

	public APIService(int currentNumber, int duration, int speed, Customer currentCustomer) {

		super("WebAPIService" + currentNumber);
		this.currentCustomer = currentCustomer;
		orderSchedule = new LinkedList<>();
		Collections.addAll(orderSchedule, currentCustomer.getOrderSchedule());
		this.duration = duration;
		this.speed = speed;
		currentTick = 1;
	}

	// methods

	@Override
	protected void initialize() {

		// subscribe to get the time broadcast
		subscribeBroadcast(TickBroadcast.class, apiTickBroadcast -> {

			currentTick = apiTickBroadcast.getTick();
			if (currentTick == duration)
				terminate();
			else
				for (BookOrderEvent currentBookOrderEvent : orderSchedule)
					if (currentBookOrderEvent.getTick() == apiTickBroadcast.getTick())
						currentCustomer.getCustomerReceiptList().add((OrderReceipt) sendEvent(currentBookOrderEvent)
								.get((duration-currentTick)*speed, TimeUnit.MILLISECONDS)); // save this receipt into customer receipts list
		});
	}
}