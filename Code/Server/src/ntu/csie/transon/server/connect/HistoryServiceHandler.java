package ntu.csie.transon.server.connect;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.history.HistoryRoom;

public class HistoryServiceHandler extends ServiceHandler {

	private HistoryRoom historyRoom;
	private User user;
	
	public HistoryServiceHandler(SocketChannel _handle,HistoryRoom _historyRoom) {
		super(_handle);
		historyRoom = _historyRoom;
	}

	@Override
	public void open() {
		Reactor reactor = Reactor.getInstance();
		reactor.registerHandler(this, SelectionKey.OP_READ);
	}

	public HistoryRoom getHistoryRoom() {
		return historyRoom;
	}

	public void setUser(User _user){
		user = _user;
	}
	
	public User getUser(){
		return user;
	}

	@Override
	public void endService() {
		Reactor.getInstance().cancelRegister(this);
		historyRoom.leave(user);
	}
}
