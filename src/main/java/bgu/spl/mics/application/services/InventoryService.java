package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

@SuppressWarnings("unchecked") // suppress unchecked assignment warnings

public class InventoryService extends MicroService {

	//fields

	private int currentTick; // a field for the current tick got from the time broadcast
	private final int duration;
	private final Inventory inventoryInstance;

	// constructor

	public InventoryService(int currentNumber, int duration) {

		super("InventoryService" + currentNumber);
		inventoryInstance = Inventory.getInstance();
		this.duration = duration;
		currentTick = 1;
	}

	// methods

	@Override
	protected void initialize() {

		// subscribe to get the TickBroadcast
		subscribeBroadcast(TickBroadcast.class, inventoryTickBroadcast -> {

			currentTick = inventoryTickBroadcast.getTick();
			if (currentTick == duration)
				terminate();
		});

		// subscribe to handle events of type CheckAvailabilityEvent
		subscribeEvent(CheckAvailabilityEvent.class, checkAvailabilityEvent -> {

			String currentBookTitle = checkAvailabilityEvent.getBookTitle();
			int currentBookPrice = inventoryInstance.checkAvailabiltyAndGetPrice(currentBookTitle);

			// complete according to the price gotten

			if (currentBookPrice == -1 || currentBookPrice > checkAvailabilityEvent.getCurrentCustomer().getAvailableCreditAmount())
				complete(checkAvailabilityEvent, -1);
			else if (inventoryInstance.take(currentBookTitle) == OrderResult.SUCCESSFULLY_TAKEN)
				complete(checkAvailabilityEvent, currentBookPrice);
			else
				complete(checkAvailabilityEvent, -1);
		});
	}
}