package ntu.csie.transon.server.connect;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;


public class Reactor {
	private Selector selector;
	private static Reactor reactor;
	private boolean join;
	
	private Reactor(){
		selector = null;
		join = false;
		try{
			selector = Selector.open();
		}catch(IOException exception){
			System.err.println("Can't open selector");
			exception.printStackTrace();
		}
	}
	public static Reactor getInstance(){
		if(reactor == null){
			reactor = new Reactor();
		}
		return reactor;
	}
	
	public void cancelRegister(EventHandler handler){
		Set<SelectionKey> keys = selector.keys();
		Iterator<SelectionKey> iter = keys.iterator();
		while(iter.hasNext()){
			SelectionKey key = iter.next();
			Object attachment = key.attachment();
			if(attachment == handler){
				key.cancel();
			}
		}
		return;
	}
	public void registerHandler(EventHandler eventHandler,int selectionKey){
		SelectableChannel channel = eventHandler.getChannel();
		try {
			channel.register(selector, selectionKey,eventHandler);
		} catch (ClosedChannelException e) {
			System.err.println("Register error because of the channel has closed.");
			e.printStackTrace();
		}
	}
	public void join(){
		join = true;
		wakeup();
	}
	public void run(){
		while(!join){
			iteration();
		}
	}
	private void wakeup(){
		selector.wakeup();
	}
	private void iteration(){
		try {
			selector.select();
		} catch (IOException e) {
			System.err.println("Select failed");
			e.printStackTrace();
		}
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> iter = selectedKeys.iterator();
		while(iter.hasNext()){
			SelectionKey key = iter.next();
			Object attachment = key.attachment();
			((EventHandler)attachment).handle();
			iter.remove();
		}
	}
}
