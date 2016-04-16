package ntu.csie.transon.server;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;

import ntu.csie.transon.server.connect.Acceptor;
import ntu.csie.transon.server.connect.ClientServiceHandlerFactory;
import ntu.csie.transon.server.connect.Reactor;
import ntu.csie.transon.server.meetingroom.MeetingList;

public class Main {
	public static void init(){
		MeetingList.getInstance();
	}
	public static void main(String[] args){
		init();
		Reactor reactor = Reactor.getInstance();
		Acceptor acceptor = new Acceptor(new ClientServiceHandlerFactory());
		acceptor.open(new InetSocketAddress("0.0.0.0",1126));
		reactor.registerHandler(acceptor, SelectionKey.OP_ACCEPT);
		System.out.println("Start to serve");
		reactor.run();
	}
}
