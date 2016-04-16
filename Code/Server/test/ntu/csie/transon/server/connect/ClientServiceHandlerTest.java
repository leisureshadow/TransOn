package ntu.csie.transon.server.connect;

import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.databean.User;

import org.junit.Test;

public class ClientServiceHandlerTest {

	@Test
	public void testUserGetSet(){
		ClientServiceHandler clientSH = new ClientServiceHandler(null);
		User user = new User("1","2","3");
		clientSH.setUser(user);
		assertSame("The user get from client service handler should equals to the one the previous seting in",user,clientSH.getUser());
	}
	
	private int testHandlePort = 59829;
	@Test
	public void testOpen() throws InterruptedException {
		Acceptor acceptor = new Acceptor(new ClientServiceHandlerFactory());
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
