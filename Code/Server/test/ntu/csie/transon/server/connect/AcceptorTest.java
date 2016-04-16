package ntu.csie.transon.server.connect;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.junit.Test;

import ntu.csie.transon.server.connect.Acceptor;

public class AcceptorTest {
	
	@Test
	public void testOpen() {
		//Also test getPort
		Acceptor acceptor = new Acceptor(null);
		acceptor.open();
		assertFalse("The acceptor's port should listen on a random numbet rather than 0",acceptor.getPort() == 0);
		acceptor.close();
		acceptor.open(new InetSocketAddress("0.0.0.0",16540));
		System.out.println(acceptor.getPort());
		assertEquals("The acceptor's port should listen on 16540",16540,acceptor.getPort());
		acceptor.close();
		acceptor.open(new InetSocketAddress("0.0.0.0",17747));
		assertEquals("The acceptor's port should listen on 17747",17747,acceptor.getPort());
		acceptor.close();
	}
	
	private int testHandlePort = 28893; 
	@Test
	public void testHandle() throws IOException, InterruptedException {
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
