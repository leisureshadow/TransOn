package ntu.csie.transon.server.connect;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import ntu.csie.transon.server.databean.User;

public class ClientServiceHandler extends ServiceHandler {

	private User user;

	public ClientServiceHandler(SocketChannel _handle) {
		super(_handle);
	}
	
	@Override
	public void open() {
		Reactor reactor = Reactor.getInstance();
		reactor.registerHandler(this, SelectionKey.OP_READ);
	}

	public User getUser(){
		return user;
	}
	
	public void setUser(User _user){
		user = _user;
	}

	@Override
	public void endService() {
		Reactor.getInstance().cancelRegister(this);
	}
}
