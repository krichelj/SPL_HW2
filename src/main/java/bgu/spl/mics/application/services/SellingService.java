package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.TimeUnit;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

@SuppressWarnings("unchecked") // suppress unchecked assignment and method invocation warnings

public class SellingService extends MicroService {

	//fields

	private final MoneyRegister moneyRegisterInstance; // a final MoneyRegister instance
	private final int duration, speed;
	private int currentTick, processTick; // fields for the current tick got from the time broadcast and the tick in which a BookOrderEvent process started
	private static int orderId; // a static field that keeps the order ID - iterated at each order completion

	// constructor

	public SellingService(int currentNumber, int duration, int speed) {

		super("SellingService" + currentNumber);
		moneyRegisterInstance = MoneyRegister.getInstance();
		orderId = 1;
		currentTick = 1;
		processTick = 1;
		this.duration = duration;
		this.speed = speed;
	}

	// methods

	@Override
	protected void initialize() {

		// subscribe to get the TickBroadcast
		subscribeBroadcast(TickBroadcast.class, sellingTickBroadcast -> {

			// define the callback call function for the TickBroadcast as a lambda
			currentTick = sellingTickBroadcast.getTick();
			if (currentTick == duration)
				terminate();
		});

		// subscribe to handle events of type BookOrderEvent
		subscribeEvent(BookOrderEvent.class, bookOrderEvent -> {

			processTick = currentTick; // declare the starting tick as the current one
			String currentBookTitle = bookOrderEvent.getBookTitle();
			Customer currentCustomer = bookOrderEvent.getCurrentCustomer();
			int currentBookPrice = (int) sendEvent(new CheckAvailabilityEvent<>(currentBookTitle,currentCustomer))
					.get((duration-currentTick)*speed, TimeUnit.MILLISECONDS);

			// checks if the book is in the inventory and the customer has enough money
			if (currentBookPrice != -1 && currentCustomer.getAvailableCreditAmount() >= currentBookPrice) {

				OrderReceipt currentPurchaseReceipt = new OrderReceipt(SellingService.orderId, this.getName(), currentCustomer.getId(),
						currentBookTitle, currentBookPrice, currentTick, bookOrderEvent.getTick(), processTick); // create an order receipt
				moneyRegisterInstance.chargeCreditCard(currentCustomer, currentBookPrice); // charge the customer the price of the book
				moneyRegisterInstance.file(currentPurchaseReceipt); // file the order receipt to the money register
				SellingService.orderId++; // increment the orderID
				sendEvent(new DeliveryEvent<>(currentCustomer.getAddress(), currentCustomer.getDistance()));
				complete(bookOrderEvent, currentPurchaseReceipt);
			}
			else
				complete(bookOrderEvent, null);
		});
	}

}