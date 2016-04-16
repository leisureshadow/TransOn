package ntu.csie.transon.server.connect;

import java.nio.channels.SelectableChannel;

public abstract class EventHandler {
	protected SelectableChannel handle;
	
	public abstract void handle();
	public SelectableChannel getChannel(){
		return handle;
	}
}
