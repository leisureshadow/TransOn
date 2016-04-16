package com.ntu.transon.connection;

import java.io.IOException;
import android.util.Log;


public abstract class EventHandler {
	public String handlerName;
	protected Handle handle=null;
	
	
	public abstract void handleEvent() throws IOException;
	
	public Handle getHandle(){
		return handle;
	}
	
	public void setHandle(Handle handle){
		Log.d("EventHandler", "I-setHandle");
		this.handle=handle;
	}
}
