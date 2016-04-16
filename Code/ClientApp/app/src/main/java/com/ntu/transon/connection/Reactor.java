package com.ntu.transon.connection;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.util.Log;

public class Reactor implements Runnable{
	private final static String TAG = "Reactor";
	private Selector selector;
//	private Queue<Runnable> pendingTasks = new ConcurrentLinkedQueue<Runnable>();
	private HashMap<Handle,EventHandler> pendingHandler;

	private Boolean terminating = false;
	

	public Reactor() throws IOException{
		Log.d(TAG,"I-(Reactor)create");
		selector = Selector.open();
		pendingHandler=new HashMap<Handle,EventHandler>();

	}
	
	public void setTerminate(boolean terminate){
		terminating = terminate;
	}
	
	public boolean getTerminate(){
		return terminating;
	}

	public SelectionKey registerHandler(EventHandler eh,int eventType) throws ClosedChannelException {
		SelectionKey key=null;	
		if(eh.getHandle() instanceof TCPSocketHandle){
			Log.d(TAG,"I-(registerHandler)register TCP to reactor");
			TCPSocketHandle handle=(TCPSocketHandle)eh.getHandle();
			Log.d(TAG,"I-(registerHandler)get handle");
            pendingHandler.put(handle, eh);
			key=handle.getSocketChannel().register(selector, eventType,eh);
			Log.d(TAG,"I-(registerHandler)register TCP to reactor finish?");
			selector.wakeup();
			Log.d(TAG,"I-(registerHandler)register TCP to reactor finish");
		
		}
		return key;
	}
	
 

	public void removeHandler(EventHandler eh){
		pendingHandler.remove(eh.getHandle());
	}

	public void handleEvent() throws IOException{
		while(!terminating){
			
			Log.d(TAG,"I-Reactor is working");
//	        if(pendingHandler.isEmpty()){
//                continue;
//            }else {


                if (selector.select() == 0) {
                    continue;
                }

                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<?> it = readyKeys.iterator();
                while (it.hasNext()) {

                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    if (!key.isValid()) {
                        continue;
                    }


                    if (key.isConnectable()) {
                        EventHandler eventHandler = (EventHandler) key.attachment();

                        if (((SocketChannel) key.channel()).finishConnect()) {
                            Log.d(TAG, "I-(handleEvent)TCP Connection is detect");
                            removeHandler(eventHandler);
                            eventHandler.handleEvent();
                        }
                    } else if (key.isReadable()) {
                        EventHandler eventHandler = (EventHandler) key.attachment();
                        eventHandler.handleEvent();
                    } else if (key.isWritable()) {
                        EventHandler eventHandler = (EventHandler) key.attachment();
                        eventHandler.handleEvent();
                    }

                }
                readyKeys.clear();
//            }
		}
		Log.d(TAG,"I-(handleEvent)Reactor thread is End");
	}

	public Selector getSelector(){
		return selector;
	}

	public EventHandler getPendingHandler(Handle handle) {
		return pendingHandler.get(handle);
	}

	@Override
	public void run() {
		 try {
			handleEvent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finalize() throws IOException {
	      selector.close();
	   }
	  
	  
//	private void handleEvent(long timeout) throws IOException {
//		int i=0;
//		// perform any pending registration or cancellation requests
//		while (!pendingTasks.isEmpty()){
//			Log.d( "Reactor","pendingTask"+(i++));
//			
//			pendingTasks.poll().run();
//		}
//		
//		Log.d("Reactor","I am in Event loop.");
//		
//		selector.select(timeout);
//				
//		for (Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); iter.hasNext();) { 
//			SelectionKey key = iter.next(); 
//			iter.remove();
//			try {
//				if (key.isConnectable()) {
//					EventHandler eventHandler=(EventHandler) key.attachment();
//					if(((SocketChannel)key.channel()).finishConnect()){
//						eventHandler.handleEvent();
//					}
//				}
//				
//				
//				if (key.isReadable()) { 
//					EventHandler eventHandler=(EventHandler) key.attachment();
//					eventHandler.handleEvent();
//				}
//				
//				if (key.isWritable()) {
//					EventHandler eventHandler=(EventHandler) key.attachment();
//					eventHandler.handleEvent();
//				}
//				
//			} catch (IOException e) {
//				// on any IO exception we simply close the Handle
//				// which will make it fire its onDisconnect() event.
//				requestCloseHandle((EventHandler) key.attachment(), e);
//			}
//		}
//	}
	
	
//	protected void requestCloseHandle (EventHandler h, IOException reason){
//		addTask(new CloseRequest(h, reason));
//	}
	
//	
//	private class CloseRequest implements Runnable {
//		private Handle handle;
//		private IOException reason;
//		
//		public CloseRequest(EventHandler eh, IOException reason){
//			this.handle = eh.getHandle();
//			this.reason = reason;
//		}
//		
//		@Override
//		public void run(){
//			try {
//				if(handle instanceof TCPSocketHandle){
//					((TCPSocketHandle) handle).getSocketChannel().close();
//				}
//				else if(handle instanceof UDPSocketHandle){
//					((UDPSocketHandle) handle).getSocketChannel().close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//		@Override
//		public void run(){
//			try {
//				while(!terminating){
//					handleEvent(0);
//				}
//			} catch (Exception e) {
//				System.err.println("WTF??? BUG: fatal error in select loop");
//				e.printStackTrace();
//				System.exit(1);
//			}
//			
//			try {
//				selector.close();
//			} catch (Exception e) {
//				System.err.println("WTF??? BUG: fatal error when closing selector");
//				e.printStackTrace();
//				System.exit(1);
//			}
//		}
	//	

	//public void registerHandler(EventHandler h, int ops){
//		Log.d("Reactor","registing");
//		addTask(new RegistrationRequest(h, ops));
	//}

	//
	//private void addTask(Runnable r){
//		pendingTasks.offer(r);
//		selector.wakeup();
	//}



	//private class TerminateRequest implements Runnable{
	//	
//		@Override
//		public void run(){
//			terminating = true;
//		}
	//}


	//private class RegistrationRequest implements Runnable {
//		private Handle handle;
//		private int operations;
//		private EventHandler eh;
	//	
//		public RegistrationRequest(EventHandler eventHandler, int ops){
//			handle = eh.getHandle();
//			operations = ops;
//			eh=eventHandler;
//		}
		
//		@Override
//		public void run(){
//			try {
//				if(handle instanceof TCPSocketHandle){
//				
//					((TCPSocketHandle)handle).getSocketChannel().register(selector, operations,eh);
//				}else if(handle instanceof UDPSocketHandle){
//				
//					((UDPSocketHandle)handle).getSocketChannel().register(selector, operations,eh);
//				}
//				
//				
//			} catch (ClosedChannelException e) {
//				e.printStackTrace();
//				// nothing we can do here, just ignore it 
//			}
//		}
	//}


}
