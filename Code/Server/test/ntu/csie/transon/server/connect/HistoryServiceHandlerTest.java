package ntu.csie.transon.server.connect;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.history.HistoryRoom;

import org.junit.Test;

public class HistoryServiceHandlerTest {

	@Test
	public void testGetHistoryRoom() {
		HistoryRoom historyRoom = new HistoryRoom("123");
		HistoryServiceHandler historySH = new HistoryServiceHandler(null,historyRoom);
		
		assertSame("Result of getHistoryRoom should equal to the one setting in previously.",historyRoom,historySH.getHistoryRoom());
	}
	
	@Test
	public void testUserGetSet(){
		HistoryServiceHandler historySH = new HistoryServiceHandler(null,null);
		User user = new User("1","2","3");
		historySH.setUser(user);
		assertSame("The user get from client service handler should equals to the one the previous seting in",user,historySH.getUser());
	}
	
	private int testHandlePort = 59823;
	@Test
	public void testOpen() throws InterruptedException {
		Acceptor acceptor = new Acceptor(new HistoryServiceHandlerFactory(null));
		acceptor.open(new InetSocketAddress("0.0.0.0",testHandlePort));
		virtualClient();
		Thread.sleep(1000);
		acceptor.handle();
		acceptor.close();
	}
	
	//Try to mimic connection
	private void virtualClient(){
		Thread tryToConnect = new Thread(){
			@Override
			public void run(){
				SocketChannel channel = null;
				try {
					channel = SocketChannel.open(new InetSocketAddress(InetAddress.getLocalHost(),testHandlePort));
					Thread.sleep(1000);
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		tryToConnect.start();
	}

}
