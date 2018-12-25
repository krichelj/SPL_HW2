package bgu.spl.mics;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

@SuppressWarnings("unchecked") // suppress unchecked assignment warnings

public class MessageBusImpl implements MessageBus {

	// fields - 4 final distinct hashmaps that are efficient in arranging the required data and cannot be reassigned after the first time

	private final ConcurrentHashMap <MicroService, LinkedBlockingQueue<Message>> microServicesMessagesQueues; // a hash map of all microservices and a queue of their messages - both events and broadcasts
	private final ConcurrentHashMap <Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventSubscribeList; // a hash map of an event type and the microservices able to process it
	private final ConcurrentHashMap <Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastSubscribeList; //  a hash map of a broadcast type and the microservices that want to receive it
	private final ConcurrentHashMap <Event, Future> futureHashMap; // a hash map of a message and its future objects

	// thread-safe singleton implementation
	private static class MessageBusImplSingletonHolder {

		private static MessageBusImpl messageBusInstance = new MessageBusImpl();
	}

	// constructor

	private MessageBusImpl() {

		microServicesMessagesQueues = new ConcurrentHashMap<>();
		eventSubscribeList = new ConcurrentHashMap<>();
		broadcastSubscribeList = new ConcurrentHashMap<>();
		futureHashMap = new ConcurrentHashMap<>();
	}

	// methods

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBusImpl getInstance() {

		return MessageBusImplSingletonHolder.messageBusInstance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

		// register the microservice if it's not registered
		if (isNotRegistered(m))
			register(m);

		eventSubscribeList.putIfAbsent(type, new LinkedBlockingQueue<>()); // add the event if the events list doesn't contain it
		eventSubscribeList.get(type).add(m); // add the microservice m to the list of capable services

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

		// register the microservice if it's not registered
		if (isNotRegistered(m))
			register(m);

		broadcastSubscribeList.putIfAbsent(type, new LinkedBlockingQueue<>()); // add the broadcast if the broadcasts list doesn't contain it
		broadcastSubscribeList.get(type).add(m); // add the microservice m to the list of capable services

	}


	@Override
	public <T> void complete(Event<T> e, T result) {

		futureHashMap.get(e).resolve(result); // resolve the future object of the event e with the given result
	}

	@Override
	public void sendBroadcast(Broadcast b) {

		// add the broadcast message b to the queues of all the Micro-Services which subscribed to receive this specific message type
		for (MicroService currentMicroService : broadcastSubscribeList.get(b.getClass()))
				microServicesMessagesQueues.get(currentMicroService).add(b);
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		LinkedBlockingQueue<MicroService> roundRobinServicesQueue = eventSubscribeList.get(e.getClass()); // get the services that can handle the event e

		if (roundRobinServicesQueue == null || roundRobinServicesQueue.isEmpty())
			return null; // return null if there are no services that can handle the event e

		Future<T> futureOutput = new Future<>();
		futureHashMap.put(e, futureOutput);
		MicroService microServiceAtTheTop = roundRobinServicesQueue.poll(); // take and remove the microservice from the top of the queue of services that can handle the event e
		microServicesMessagesQueues.get(Objects.requireNonNull(microServiceAtTheTop)).add(e); // assign the event to the microservice from the top of the queue
		roundRobinServicesQueue.add(microServiceAtTheTop); // return the microservice from the top of the queue to the back of the queue to continue with the round-robin

		return futureOutput;
	}

	@Override
	public void register(MicroService m) {

		microServicesMessagesQueues.put(m, new LinkedBlockingQueue<>()); // register the microservice m in the message bus and assign a new queue of messages to it
	}

	@Override
	public void unregister(MicroService m) {

		// delete the microservice from any queue it's in
		eventSubscribeList.forEach((event, eventServicesList) -> eventServicesList.remove(m));
		broadcastSubscribeList.forEach((broadcast, broadcastServicesList) -> broadcastServicesList.remove(m));

		// resolve all of the microservice's currently pending events

		microServicesMessagesQueues.forEach((microService, currentMessage) -> {

			//noinspection SuspiciousMethodCalls - since currentMessage is checked to be of Event type
			if (currentMessage instanceof Event && futureHashMap.contains(currentMessage))
				futureHashMap.get(currentMessage).resolve(null);
		});

		microServicesMessagesQueues.remove(m); // delete the entry with value m to from the microservices hash map
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {

		// as the javadoc says - this method throws an IllegalStateException if the microservice m isn't registered
		if (isNotRegistered(m))
			throw new IllegalStateException("The current MicroService is not registered to the MessageBus");

		return microServicesMessagesQueues.get(m).take(); // return the current message
	}

	/**
	 * Returns a boolean to check if the {@link MicroService} m is registered to the {@link MessageBus}
	 * @param m The {@link MicroService} to check if registered
	 * @return If registered or not
	 */
	private boolean isNotRegistered (MicroService m){

		return !microServicesMessagesQueues.containsKey(m);
	}
}