package bgu.spl.mics;
import java.util.Map;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance= null; // for making sure it singleton
	private Map<MicroService, BlockingQueue<Message>> microServiceHashTable; // MicroServies with Blocking Queue.
	private Map<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastHash; // Messages of Broadcast with Queue.
	private Map<Class<? extends Event<?>>, ConcurrentLinkedQueue<MicroService>> eventHash;// Messages of Events with Queue.
	private Map<Event<?>, Future> futureHash;
	boolean hanSoloFinished;

	private MessageBusImpl(){
		microServiceHashTable=new ConcurrentHashMap<>();
		broadcastHash=new ConcurrentHashMap<>();
		eventHash=new ConcurrentHashMap<>();
		futureHash=new ConcurrentHashMap<>();
		hanSoloFinished=false;
	}

	private static class MessageBusImplHolder {
		private static MessageBusImpl initialization = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() // Singleton
	{	if (instance==null)
		instance=MessageBusImpl.MessageBusImplHolder.initialization;
		return instance;
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService serviceMe) {
		if (!eventHash.containsKey(type)) {
			synchronized (eventHash){
				if (!eventHash.containsKey(type)) {
					ConcurrentLinkedQueue<MicroService> newEvent = new ConcurrentLinkedQueue<>();
					newEvent.add(serviceMe);
					eventHash.put(type, newEvent);
			}
				else
					eventHash.get(type).add(serviceMe);
		}
		}
		else
			eventHash.get(type).add(serviceMe);
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService serviceMe) {
			if (!broadcastHash.containsKey(type)) {
				ConcurrentLinkedQueue<MicroService> newBroadCast = new ConcurrentLinkedQueue<>();
				newBroadCast.add(serviceMe);
				broadcastHash.put(type, newBroadCast);
			}
			else
				broadcastHash.get(type).add(serviceMe);
    }

	@Override
	public <T> void complete(Event<T> e, T result) {
		if (futureHash.get(e) != null)
			futureHash.get(e).resolve(result);
	}


	@Override
	public void sendBroadcast(Broadcast b) {
		if (broadcastHash.get(b.getClass()) != null) {
			for (MicroService x : broadcastHash.get(b.getClass()))
				microServiceHashTable.get(x).add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) { //Actually send it and have fun with the Robin-round-manner.
		if (eventHash.get(e.getClass()) != null) {
			synchronized (eventHash.get(e.getClass())){
				MicroService sendEvent = eventHash.get(e.getClass()).poll(); //the element at the front
				if (sendEvent != null) {
					Future eventFuture = new Future();
					futureHash.put(e, eventFuture);
					microServiceHashTable.get(sendEvent).add(e);
					eventHash.get(e.getClass()).add(sendEvent); // round manner
					return eventFuture;
				}
			}
			return null;
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> microServiceSubScriber=new LinkedBlockingQueue<>();
		microServiceHashTable.put(m,microServiceSubScriber);
	}

	@Override
	public void unregister(MicroService m) {
		if (microServiceHashTable.get(m) != null)
			microServiceHashTable.remove(m);
		if(futureHash.get(m)!=null)
			futureHash.get(m).resolve(null);
		for (Map.Entry<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> entry : broadcastHash.entrySet())
			entry.getValue().remove(m);
		for (Map.Entry<Class<? extends Event<?>>, ConcurrentLinkedQueue<MicroService>> entry : eventHash.entrySet())
			entry.getValue().remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (microServiceHashTable.containsKey(m))
			return microServiceHashTable.get(m).take();
		throw new InterruptedException("MicroService need to get register before he can do something...");
	}
}
